#!/usr/bin/env python3
"""Signer les mises à jour avec la clé Selkie sauvegardée, puis vérifier son certificat."""
import argparse
import hashlib
import json
import os
from pathlib import Path
import re
import shutil
import subprocess


def apksigner_command(explicit):
    if explicit:
        path = Path(explicit)
        if not path.is_file():
            raise ValueError("Outil apksigner introuvable")
        return ["java", "-jar", str(path)] if path.suffix == ".jar" else [str(path)]
    installed = shutil.which("apksigner")
    if installed:
        return [installed]
    sdk = os.environ.get("ANDROID_HOME") or os.environ.get("ANDROID_SDK_ROOT")
    if sdk:
        path = Path(sdk) / "build-tools" / "34.0.0" / "apksigner"
        if path.is_file():
            return [str(path)]
    raise ValueError("Installer Android Build Tools 34.0.0 ou fournir --apksigner")


def verify(command, apk, expected):
    result = subprocess.run(command + ["verify", "--verbose", "--print-certs", str(apk)], text=True, capture_output=True, check=True)
    fingerprints = re.findall(r"^Signer #\d+ certificate SHA-256 digest: ([0-9a-f]+)$", result.stdout, re.MULTILINE)
    if fingerprints != [expected]:
        raise ValueError("Le certificat ne correspond pas à la signature fixe de Selkie")
    print(result.stdout, end="")
    print("SHA-256 APK : " + hashlib.sha256(apk.read_bytes()).hexdigest())


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("apk", type=Path)
    parser.add_argument("--verify-only", action="store_true")
    parser.add_argument("--output", type=Path)
    parser.add_argument("--key", type=Path)
    parser.add_argument("--password-file", type=Path)
    parser.add_argument("--apksigner")
    args = parser.parse_args()
    metadata = json.loads(Path(__file__).with_name("selkie-signed-public.json").read_text())
    command = apksigner_command(args.apksigner)
    if args.verify_only:
        verify(command, args.apk, metadata["certificate_sha256"])
        return
    if not args.output or not args.key or not args.password_file:
        parser.error("Fournir --output, --key et --password-file depuis la sauvegarde privée")
    if not args.key.is_file() or not args.password_file.is_file():
        parser.error("Clé sauvegardée ou fichier du mot de passe introuvable ; aucune clé ne sera créée")
    if args.output.resolve() == args.apk.resolve() or args.output.exists():
        parser.error("Choisir un nouveau fichier de sortie")
    args.output.parent.mkdir(parents=True, exist_ok=True)
    subprocess.run(command + [
        "sign", "--ks", str(args.key.resolve()), "--ks-type", "PKCS12",
        "--ks-key-alias", metadata["alias"], "--ks-pass", "file:" + str(args.password_file.resolve()),
        "--v1-signing-enabled", "false", "--v2-signing-enabled", "true",
        "--v3-signing-enabled", "false", "--v4-signing-enabled", "false",
        "--out", str(args.output), str(args.apk),
    ], check=True)
    try:
        verify(command, args.output, metadata["certificate_sha256"])
    except Exception:
        args.output.unlink(missing_ok=True)
        raise


if __name__ == "__main__":
    main()
