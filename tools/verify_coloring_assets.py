"""Check the assets in the built APK, including Android packaging transformations.

Run after assembleDebug: python3 tools/verify_coloring_assets.py [path/to.apk]
Mask files deliberately use .scmz: aapt removes/decompresses a terminal .gz suffix.
"""
from pathlib import Path
import gzip
import hashlib
import json
import struct
import sys
import zipfile

root = Path(__file__).resolve().parents[1]
apk = Path(sys.argv[1]) if len(sys.argv) > 1 else root / "app/build/outputs/apk/debug/app-debug.apk"
assets = root / "app/src/main/assets"
pages = json.loads((assets / "imported_coloring/manifest.json").read_text())
assert len(pages) == 45 and len({p["id"] for p in pages}) == 45
with zipfile.ZipFile(apk) as archive:
    for page in pages:
        for field, digest in [("imageAsset", "sha256"), ("maskAsset", "maskSha256")]:
            name = page[field]
            source = (assets / name).read_bytes()
            actual = archive.read("assets/" + name)
            assert actual == source, f"APK changed {name}"
            assert hashlib.sha256(actual).hexdigest() == page[digest], f"Checksum changed for {name}"
        mask = gzip.decompress(archive.read("assets/" + page["maskAsset"]))
        expected = (b"SCM1", page["width"], page["height"], len(page["anchors"]))
        assert struct.unpack(">4sIII", mask[:16]) == expected, page["id"]
        assert len(mask) == 16 + page["width"] * page["height"] * 2, page["id"]
print("Verified 45 original images and 45 region masks inside the APK.")
