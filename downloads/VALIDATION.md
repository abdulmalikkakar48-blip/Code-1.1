# Validation de l’APK avec la signature d’origine

L’APK downloads/Selkie-Android.apk est exactement le fichier d’origine du commit 96fd6caf27c192a173ea20820f85f3b257c6db66.

- Version : Selkie 0.8.0, versionCode 9, Android 10+.
- Taille : 23 632 504 octets.
- SHA-256 APK : 3870cf3e2ec2e2dc34d0f226492cabc0ca830a5af4440171eafab1f2eda56a20.
- SHA-256 certificat initial : 9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50.
- SHA du blob Git d’origine : 9b0fdad8e45e60516aa603e7197d53e8e03450ce.

La refonte du code a compilé et passé les tests et lint dans le workflow 38140276591, mais l’APK GitHub Actions utilisait une autre clé de test. Il a été retiré du lien principal. L’APK rétabli ne contient donc pas encore cette nouvelle refonte.

Le dépôt et son archive de source ne contiennent pas la clé initiale. Pour reconstruire le nouveau design en conservant le même certificat, il faut réutiliser le fichier de signature de l’environnement Codex qui a produit l’APK d’origine. Le certificat présent dans l’APK ne contient pas cette clé privée.
