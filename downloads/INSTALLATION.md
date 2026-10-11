# Installer la refonte Selkie

[Télécharger Selkie-Android.apk](https://github.com/abdulmalikkakar48-blip/Code-1.1/raw/maree-apk-download/downloads/Selkie-Android.apk).

L’APK contient la refonte moderne du commit 58a8f0d46e4f49d281adb3f5d9bbcc1cfba95fff : vert profond, menthe lumineuse, navigation foncée et nouvel accueil. Il est signé avec une nouvelle clé fixe, sauvegardée de façon privée pour les prochaines mises à jour. La signature a été acceptée par l’éditeur et vérifiée avec l’outil officiel Android.

1. Télécharger l’APK sur un téléphone Android 10 ou plus récent.
2. Ouvrir le fichier et autoriser l’installation depuis la source choisie si Android le demande.
3. Ouvrir Selkie et suivre les explications des accès Android. Le mode découverte reste disponible sans suivi.

La signature diffère de celle de l’ancienne application et de celle du premier APK Actions de la refonte. Android refuse une mise à jour entre deux certificats différents. Si une ancienne installation est encore présente, il faut la désinstaller avant cette première installation ; cela efface ses points, limites et défis locaux. Une installation qui utilise déjà cette nouvelle clé fixe pourra recevoir de futures mises à jour signées avec la même clé.

La version reste 0.8.0 (versionCode 9), en mode test. Le suivi et le rendu de cette refonte restent à essayer sur un téléphone réel.

L’archive Selkie-Android-source.zip contient les sources de la refonte et les instructions de signature, sans clé privée. Les captures dans screenshots/ montrent la version antérieure. L’ancien APK reste disponible dans Selkie-Android-avant-refonte.apk.

Les artefacts Selkie-build-to-sign sont des compilations intermédiaires à signer, pas des mises à jour déjà compatibles. Voir [la procédure de signature](../docs/SIGNATURE_ANDROID.md) et [la validation](VALIDATION.md).
