# Conserver la signature Android de Selkie

## Exception de test 0.8.1

L’éditeur a demandé de distribuer immédiatement le correctif de l’icône, en acceptant une réinstallation. La sauvegarde de la clé fixe ci-dessous n’est pas accessible dans cet environnement. `downloads/Selkie-Android-0.8.1.apk` utilise donc la clé de développement déjà disponible ici ; son certificat est documenté dans [sa validation](../downloads/VALIDATION-0.8.1.md). Aucune nouvelle clé n’a été générée. Ce fichier ne remplace pas directement une installation 0.8.0 signée avec la clé fixe : sa désinstallation efface les données locales.

Cette exception concerne l’essai direct, pas une publication Google Play. Les artefacts 0.8.0 et leurs empreintes restent conservés. La procédure ci-dessous et `tools/sign_apk.py` continuent de contrôler la clé fixe 0.8.0 ; ne pas les utiliser pour déclarer la signature de développement équivalente. Avant la première publication, choisir et sauvegarder la clé de téléversement adaptée à Google Play App Signing.

## Signature fixe de la refonte 0.8.0

L’APK de la refonte est signé avec une nouvelle clé fixe. Elle doit être réutilisée pour toutes les prochaines mises à jour de cette installation.

- Alias : selkie-stable.
- Format : PKCS12, RSA 3072 bits.
- SHA-256 du certificat : c3def84958aae0971760964b987090c787c1796926d716a91131d69396c25826.
- Sauvegarde privée : Selkie-signature-privee.zip, contenant selkie-signature.p12, password.txt, certificat-public.pem et lire-moi.txt.

Le dépôt conserve le certificat et les signatures publics, pas la clé privée ni son mot de passe. Garder la sauvegarde privée hors du dépôt et ne pas la joindre aux APK distribués.

## Signer une nouvelle compilation

Construire l’application, exécuter les tests et Android Lint avec Java 17 et le SDK Android 34 :

```sh
./gradlew --no-daemon :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
```

Extraire la sauvegarde privée dans un dossier privé extérieur au dépôt, puis utiliser le script fourni en adaptant les chemins :

```sh
python tools/sign_apk.py app/build/outputs/apk/debug/app-debug.apk \
  --output downloads/Selkie-Android-nouvelle.apk \
  --key /chemin/prive/selkie-signature.p12 \
  --password-file /chemin/prive/password.txt
```

Le script utilise apksigner (Android Build Tools 34.0.0), lit le mot de passe depuis le fichier et vérifie ensuite que le certificat correspond à la signature fixe. Il ne crée jamais une autre clé et refuse de remplacer un fichier de sortie existant. L’option --apksigner permet de fournir un chemin vers apksigner ou son fichier JAR.

Vérification seule :

```sh
python tools/sign_apk.py downloads/Selkie-Android.apk --verify-only
```

Pour une prochaine mise à jour installable, augmenter également le versionCode de l’application, vérifier le fonctionnement puis distribuer le nouvel APK signé. Les artefacts de compilation Selkie-build-to-sign emploient la clé temporaire du runner : les signer avec la clé fixe avant distribution.

## Publication de cet APK exact

Le workflow « Publier l’APK Selkie à signature fixe » remet en place l’APK déjà signé à partir de la compilation validée et de son bloc de signature public. Il ne reçoit aucune clé privée. Il contrôle les empreintes, les sources et le certificat avant de publier downloads/Selkie-Android.apk et l’archive des sources.

Ce workflow est lié à la compilation de cette refonte et ne signe pas automatiquement les futures compilations. Pour celles-ci, réutiliser la sauvegarde et le script ci-dessus, puis publier le nouvel APK avec ses empreintes actualisées.

La clé initiale et la clé temporaire du premier APK Actions ne sont pas récupérables dans le dépôt. La première installation utilisant cette nouvelle clé ne peut pas remplacer directement une installation signée avec ces anciennes clés ; la désinstallation efface les données locales. Les versions signées avec la nouvelle clé seront compatibles entre elles.
