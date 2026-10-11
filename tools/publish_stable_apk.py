#!/usr/bin/env python3
"""Publier l'APK déjà signé à partir de données publiques, sans clé privée."""
import argparse
import base64
import hashlib
import io
import json
from pathlib import Path
import struct
import subprocess
import tempfile
import zipfile


def digest(data):
    return hashlib.sha256(data).hexdigest()


def zip_offsets(data):
    eocd = data.rfind(b"PK\x05\x06", max(0, len(data) - 65557))
    if eocd < 0 or eocd + 22 > len(data):
        raise ValueError("Fin du ZIP introuvable")
    if eocd + 22 + struct.unpack_from("<H", data, eocd + 20)[0] != len(data):
        raise ValueError("Fin du ZIP invalide")
    central = struct.unpack_from("<I", data, eocd + 16)[0]
    if data[central - 16:central] != b"APK Sig Block 42":
        raise ValueError("Bloc de signature APK v2 introuvable")
    start = central - struct.unpack_from("<Q", data, central - 24)[0] - 8
    if start < 0 or struct.unpack_from("<Q", data, start)[0] != central - start - 8:
        raise ValueError("Bloc de signature invalide")
    return start, central, eocd


def reconstruct(original, metadata):
    if digest(original) != metadata["base_apk_sha256"]:
        raise ValueError("L'APK de compilation ne correspond pas au fichier validé")
    start, central, eocd = zip_offsets(original)
    block = base64.b64decode(metadata["signing_block_base64"], validate=True)
    result = bytearray(original[:start] + block + original[central:])
    delta = len(block) - (central - start)
    struct.pack_into("<I", result, eocd + delta + 16, central + delta)
    result = bytes(result)
    if digest(result) != metadata["final_apk_sha256"] or len(result) != metadata["size_bytes"]:
        raise ValueError("Le fichier reconstruit ne correspond pas à l'APK signé")
    zip_offsets(result)
    with zipfile.ZipFile(io.BytesIO(original)) as before, zipfile.ZipFile(io.BytesIO(result)) as after:
        if after.testzip() is not None or before.namelist() != after.namelist():
            raise ValueError("Archive APK altérée")
        if any(before.read(name) != after.read(name) for name in before.namelist()):
            raise ValueError("Le contenu de l'application a changé pendant la signature")
    return result


def write_source_archive(output):
    paths = subprocess.check_output(["git", "ls-files", "-z"]).decode().split("\0")
    with zipfile.ZipFile(output, "w", zipfile.ZIP_DEFLATED) as archive:
        for name in paths:
            if name and not name.startswith("downloads/"):
                archive.write(name, "Selkie/" + name)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--artifact", type=Path, help="ZIP Actions local, pour vérification")
    parser.add_argument("--output", type=Path, default=Path("downloads/Selkie-Android.apk"))
    parser.add_argument("--no-source-archive", action="store_true")
    args = parser.parse_args()
    metadata = json.loads(Path(__file__).with_name("selkie-signed-public.json").read_text())
    if not args.no_source_archive:
        subprocess.run([
            "git", "diff", "--exit-code", metadata["source_commit"], "HEAD", "--",
            "app", "build.gradle.kts", "settings.gradle.kts", "gradle.properties",
            "gradle", "gradlew", "gradlew.bat",
        ], check=True)
    if args.output.is_file() and digest(args.output.read_bytes()) == metadata["final_apk_sha256"]:
        print("L'APK signé est déjà présent et son empreinte est correcte.")
    else:
        with tempfile.TemporaryDirectory() as directory:
            artifact = args.artifact
            if artifact is None:
                artifact = Path(directory) / "artifact.zip"
                repo = subprocess.check_output(["gh", "repo", "view", "--json", "nameWithOwner", "--jq", ".nameWithOwner"]).decode().strip()
                with artifact.open("wb") as stream:
                    subprocess.run(["gh", "api", f"repos/{repo}/actions/artifacts/{metadata['artifact_id']}/zip"], stdout=stream, check=True)
            with zipfile.ZipFile(artifact) as archive:
                apks = [name for name in archive.namelist() if name.endswith(".apk")]
                if len(apks) != 1:
                    raise ValueError("L'artefact doit contenir exactement un APK")
                signed = reconstruct(archive.read(apks[0]), metadata)
            args.output.parent.mkdir(parents=True, exist_ok=True)
            args.output.write_bytes(signed)
    if digest(args.output.read_bytes()) != metadata["final_apk_sha256"]:
        raise ValueError("Empreinte finale incorrecte")
    args.output.with_suffix(".sha256").write_text(metadata["final_apk_sha256"] + "  " + args.output.name + "\n")
    if not args.no_source_archive:
        write_source_archive(args.output.with_name("Selkie-Android-source.zip"))
    print(f"APK contrôlé : {args.output}, SHA-256 {metadata['final_apk_sha256']}")


if __name__ == "__main__":
    main()
