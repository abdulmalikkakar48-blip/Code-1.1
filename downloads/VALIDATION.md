# Validation de la refonte Selkie du 11 octobre 2026

- Code Android construit : `58a8f0d46e4f49d281adb3f5d9bbcc1cfba95fff`.
- Compilation, tests unitaires et lint : réussis dans [GitHub Actions](https://github.com/abdulmalikkakar48-blip/Code-1.1/actions/runs/38140276591).
- APK publié : `downloads/Selkie-Android.apk`.
- SHA-256 : `683c4366bc824c0ed0029a7a393eddbcae0613d0decd9ca35ca674517864df3e`.
- Taille : 23401484 octets.
- Android : `com.maree.app`, version 0.8.0, versionCode 9, Android 10+.
- Certificat SHA-256 extrait du bloc de signature APK v2 : `fba73c6576ae1408f342a199d03d2f6766b69721a1c889c42d5ed0a61f0514d3`.
- Ancien certificat cloud : `9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50`. Les signatures sont différentes ; une mise à jour directe de cette ancienne installation n'est pas possible.
- L'ancien APK est conservé dans `downloads/Selkie-Android-avant-refonte.apk`.
- L'archive de source est régénérée à partir du code actuel, sans les binaires du dossier downloads.
- La publication vérifie le SHA-256 et la correspondance du code Android avec la révision testée avant de remplacer le fichier.

## Limites de validation

Les captures existantes datent de la version antérieure. Aucun rendu de cette refonte, suivi prolongé ou contrôle de grandes polices sur téléphone réel n'a été observé dans cette tâche. Les contrôles automatisés ne remplacent pas cette vérification visuelle.

Les règles métier et la structure de stockage n'ont pas été modifiées par la refonte.
