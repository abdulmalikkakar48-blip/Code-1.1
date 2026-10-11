#!/usr/bin/env python3
"""Publish the tested Selkie redesign as a directly downloadable APK."""
from pathlib import Path
import hashlib
import os
import subprocess
import tempfile
import zipfile

SOURCE_COMMIT = "58a8f0d46e4f49d281adb3f5d9bbcc1cfba95fff"
ARTIFACT_ID = 11703331071
APK_SHA256 = "683c4366bc824c0ed0029a7a393eddbcae0613d0decd9ca35ca674517864df3e"
CERT_SHA256 = "fba73c6576ae1408f342a199d03d2f6766b69721a1c889c42d5ed0a61f0514d3"
PREVIOUS_COMMIT = "96fd6caf27c192a173ea20820f85f3b257c6db66"
repo = os.environ["GITHUB_REPOSITORY"]
subprocess.run(["git", "diff", "--exit-code", SOURCE_COMMIT, "HEAD", "--",
    "app", "build.gradle.kts", "settings.gradle.kts", "gradle.properties",
    "gradle", "gradlew", "gradlew.bat"], check=True)
with tempfile.TemporaryDirectory() as tmp:
    archive = Path(tmp) / "artifact.zip"
    with archive.open("wb") as output:
        subprocess.run(["gh", "api",
            f"repos/{repo}/actions/artifacts/{ARTIFACT_ID}/zip"], stdout=output, check=True)
    with zipfile.ZipFile(archive) as artifact:
        apk = artifact.read("app-debug.apk")
    if hashlib.sha256(apk).hexdigest() != APK_SHA256:
        raise SystemExit("APK checksum does not match the tested artifact")
    with zipfile.ZipFile(archive) as artifact:
        if artifact.testzip() is not None:
            raise SystemExit("Damaged artifact")
downloads = Path("downloads")
previous = downloads / "Selkie-Android-avant-refonte.apk"
if not previous.exists():
    previous.write_bytes(subprocess.check_output(["git", "show",
        f"{PREVIOUS_COMMIT}:downloads/Selkie-Android.apk"]))
(downloads / "Selkie-Android.apk").write_bytes(apk)
files = subprocess.check_output(["git", "ls-files", "-z"]).decode().split("\0")
with zipfile.ZipFile(downloads / "Selkie-Android-source.zip", "w",
        compression=zipfile.ZIP_DEFLATED) as source:
    for name in files:
        if name and not name.startswith("downloads/") and Path(name).is_file():
            source.write(name, "Selkie-Android/" + name)
(downloads / "INSTALLATION.md").write_text(f"""# Installer Selkie — refonte moderne et contrastée

[Télécharger Selkie-Android.apk](https://github.com/{repo}/raw/maree-apk-download/downloads/Selkie-Android.apk).

Le fichier contient la refonte vert profond et menthe lumineuse : titres affirmés, navigation sombre, carte de points et statistiques. Android 10 ou plus récent. Version de test 0.8.0, code 9.

Ouvre le fichier sur ton téléphone Android et lance l'installation. Android peut demander d'autoriser l'installation depuis le navigateur ou le gestionnaire de fichiers.

## Installation existante

Ce nouvel APK est signé par la compilation GitHub Actions. Son certificat diffère de celui de l'ancien APK cloud. Android ne permet pas de le mettre à jour par-dessus cette installation avec une signature différente.

Ne désinstalle pas ton application si tu souhaites garder ses données : la désinstallation effacerait les données locales. Pour une mise à jour conservant les données, le projet doit être recompilé avec la clé de signature de l'ancien APK. Cette clé privée n'est pas disponible dans ce dépôt.

[L'ancien APK avant refonte](Selkie-Android-avant-refonte.apk) est conservé, ainsi que son historique GitHub.

## Vérifications

Compilation, tests unitaires et Android Lint réussis sur la révision {SOURCE_COMMIT}. [Résultat et artefact](https://github.com/{repo}/actions/runs/38140276591).

Le rendu de cette refonte sur appareil reste à vérifier. Les captures du dossier screenshots montrent la version antérieure.

SHA-256 du nouvel APK : `{APK_SHA256}`.
Taille : {len(apk)} octets.
""", encoding="utf-8")
(downloads / "VALIDATION.md").write_text(f"""# Validation de la refonte Selkie du 11 octobre 2026

- Code Android construit : `{SOURCE_COMMIT}`.
- Compilation, tests unitaires et lint : réussis dans [GitHub Actions](https://github.com/{repo}/actions/runs/38140276591).
- APK publié : `downloads/Selkie-Android.apk`.
- SHA-256 : `{APK_SHA256}`.
- Taille : {len(apk)} octets.
- Android : `com.maree.app`, version 0.8.0, versionCode 9, Android 10+.
- Certificat SHA-256 extrait du bloc de signature APK v2 : `{CERT_SHA256}`.
- Ancien certificat cloud : `9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50`. Les signatures sont différentes ; une mise à jour directe de cette ancienne installation n'est pas possible.
- L'ancien APK est conservé dans `downloads/Selkie-Android-avant-refonte.apk`.
- L'archive de source est régénérée à partir du code actuel, sans les binaires du dossier downloads.
- La publication vérifie le SHA-256 et la correspondance du code Android avec la révision testée avant de remplacer le fichier.

## Limites de validation

Les captures existantes datent de la version antérieure. Aucun rendu de cette refonte, suivi prolongé ou contrôle de grandes polices sur téléphone réel n'a été observé dans cette tâche. Les contrôles automatisés ne remplacent pas cette vérification visuelle.

Les règles métier et la structure de stockage n'ont pas été modifiées par la refonte.
""", encoding="utf-8")
print(f"Published APK: {len(apk)} bytes; SHA-256 {APK_SHA256}")
