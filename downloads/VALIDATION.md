# Validation de l’APK de la refonte

- Sources de l’application : 58a8f0d46e4f49d281adb3f5d9bbcc1cfba95fff.
- Compilation, tests unitaires et Android Lint réussis : [workflow 38140276591](https://github.com/abdulmalikkakar48-blip/Code-1.1/actions/runs/38140276591).
- Version : Selkie 0.8.0, versionCode 9, package com.maree.app, Android 10+.
- Taille finale : 23401584 octets.
- SHA-256 APK : 71572ec8c3eb85f3db0fe5a1559716ef4a58df1509868afb10bd44b6777df209.
- SHA-256 certificat fixe : c3def84958aae0971760964b987090c787c1796926d716a91131d69396c25826.
- Clé : RSA 3072 bits, alias selkie-stable, sauvegardée de façon privée.
- Vérification officielle : apksigner Android Build Tools 34.0.0, résultat « Verifies », un seul signataire, schéma v2 valide.

L’APK final conserve exactement les 147 fichiers et leur contenu de la compilation validée ; seul le bloc de signature et son alignement ont changé. Le workflow de publication vérifie les empreintes du fichier d’entrée et du fichier final, le contenu ZIP, l’absence de modification des sources de l’application, puis le certificat fixe avant de remplacer le téléchargement.

Le premier APK Actions de la refonte utilisait une clé debug temporaire non sauvegardée. Le fichier actuel utilise la nouvelle clé durable acceptée par l’éditeur. Il diffère également de l’ancien certificat initial (9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50).

La clé privée et son mot de passe ne sont pas dans le dépôt ni dans l’archive des sources. Les données du bloc de signature publiées dans tools/selkie-signed-public.json contiennent uniquement le certificat et les signatures publics. Elles permettent de remettre en place cet APK exact ; elles ne permettent pas de signer une autre compilation.

Le rendu de cette refonte n’a pas encore été contrôlé sur téléphone ou émulateur. Les captures existantes montrent l’ancienne version. L’APK reste un build DEBUG ; ces vérifications ne remplacent pas les essais du suivi, des notifications, du verrou et du rendu sur Samsung.

## Contrôle de la version retenue, le 11 octobre 2026

La refonte publiée au commit `51331aa` a été adoptée comme [base de travail](../docs/VERSION_REFERENCE.md). Son code d’application et ses fichiers Gradle sont identiques à ceux du commit de refonte `58a8f0d`.

- Contrôle local indépendant des sources : `:app:assembleDebug :app:testDebugUnitTest :app:lintDebug`, compilation réussie.
- 31 tests unitaires, zéro échec, erreur ou test ignoré.
- Android Lint : zéro erreur, 13 avertissements.
- APK distribué : signature fixe vérifiée par `apksigner`, taille et SHA-256 conformes aux valeurs ci-dessus, archive ZIP sans erreur CRC.
- Présence vérifiée dans le binaire des textes du nouvel accueil : « Explorer les défis », « points pour avancer », « Applications suivies » et « Défis terminés · série ».

L’APK distribué reste exactement celui signé avec la clé fixe ; la compilation locale de vérification ne le remplace pas par un APK à signature différente. Aucun nouveau contrôle visuel sur téléphone ou émulateur n’est revendiqué.
