# Selkie 0.10.0 — bibliothèque du Coach et mode Nuit

APK de test construit le 12 octobre 2026.

- Fichier : `Selkie-Android-0.10.0.apk`, 23 980 740 octets (environ 24 Mo).
- SHA-256 : `79608623cfb9379b81793b19be484d75a09910e2acd182ddd7c9f2007d9bced6`.
- Package : `com.maree.app`, versionCode 15, versionName 0.10.0, Android 10+, cible 34.
- Signature Android v2 vérifiée avec l’outil officiel : certificat `9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50`, identique aux APK 0.8.1 à 0.9.3. Clé privée exclue du dépôt et des sources.

## Changements

Le Coach ajoute 1 152 questions et réponses préparées dans chaque langue, avec les mêmes identifiants, huit rubriques, une recherche et des pages de vingt entrées. Les 48 situations concrètes et 24 types de conseils sont combinés à partir de textes préparés ; il ne s’agit pas d’un modèle génératif ni de 1 152 essais rédigés indépendamment. Les alias et traductions ne gonflent pas ce nombre. Les 67 sujets, 125 réponses antérieures et 38 questions d’aide Selkie sont conservés. Les réponses reconnues de détresse ont priorité sur les conseils ordinaires.

Une carte contient la question en haut, une autre contient sa réponse immédiatement dessous. Modifier la question masque l’ancienne réponse. Choisir une question de la bibliothèque remplit la question, affiche le conseil et revient à ces cartes. L’interface du futur dialogue distant adopte aussi les deux cartes, sans annoncer ce service comme actif.

Dans Plus → Apparence, Clair, Nuit et Comme le téléphone sont disponibles. Nuit utilise des fonds presque noirs, des surfaces bleu marine et des accents bleus clairs. Le choix local est conservé après relance et exclu des sauvegardes Android. L’effacement global revient à Clair. Le logo reste lisible sur son fond existant et l’écran rouge de blocage conserve son rôle.

## Compilation et tests

Commande exécutée dans le dépôt avec Java 17, Gradle 8.7 et SDK 34 :

```sh
source /workspace/.cloud-setup/activate.sh
./gradlew --no-daemon --max-workers=2 :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
```

Compilation réussie ; les rapports de cette exécution contiennent **97 tests unitaires**, zéro échec, erreur ou test ignoré. Parmi eux : 33 tests du moteur Coach, 10 de la nouvelle bibliothèque et 10 de l’apparence. Ils couvrent notamment l’unicité des questions et réponses, les deux langues, la recherche, les réponses sélectionnées, la priorité de la détresse, le contexte local réel, les écritures refusées, l’effacement et le contraste des rôles de texte effectivement utilisés.

Android Lint : zéro erreur ou erreur fatale, deux avertissements et une information. Les avertissements existants concernent `fullBackupContent` sur Android antérieur à 12 et une ressource d’icône `v26` devenue redondante avec minSdk 29. L’information concerne un état entier Compose. Aucune vérification n’a été désactivée pour obtenir le résultat.

Une revue indépendante du contenu couvre les 48 situations et les 24 intentions à travers 50 réponses FR/EN. Les obstacles sont décrits comme des difficultés, les actions et alternatives restent concrètes, les usages essentiels sont conservés et les exercices du Coach ne promettent pas de points de défi. La composition de textes peut donner un style répétitif ; elle ne comprend pas toutes les questions nouvelles.

## Essais observés sur émulateur

La mise à jour a été installée avec `adb install -r` sur Android 14 (API 34), écran 480 × 800. Version 0.10.0 / code 15 confirmés ; données et certificat existants conservés. L’écran Coach anglais a été ouvert ; choisir Sleep a conservé la question dans sa carte et affiché le conseil de sommeil dans une carte séparée dessous. Des XML récents et des captures confirment ce parcours. [Capture de la réponse](Selkie-Android-0.10.0-coach-answer.png).

Le rendu noir et bleu de l’accueil a été observé après relance, avec une préférence Nuit placée dans le stockage local de cette installation de test. Ce contrôle vérifie le chargement de la préférence et le rendu réel ; il ne constitue pas un essai réussi du sélecteur d’apparence. [Capture de l’accueil Nuit](Selkie-Android-0.10.0-night-home.png).

L’émulateur fonctionne sans `/dev/kvm`. Plusieurs interactions ont déclenché des ANR ; la trace inspectée montre le thread principal occupé dans la mise à jour des nœuds Compose. Une précompilation ART du package et une relance ont été essayées, sans permettre un parcours complet. La cause ne permet pas de conclure à la fluidité sur téléphone. La recherche et la pagination ont des tests unitaires, mais leur parcours tactile, le sélecteur de nuit et la bascule de langue ne sont pas déclarés validés ici. Les essais sur un appareil physique restent nécessaires.

## Limites de cette version

Aucun essai sur Samsung physique n’a été effectué. La présentation et l’usage sur le Galaxy A16 doivent encore être confirmés par l’éditeur. Le Coach local reste gratuit et hors ligne. Les constantes Supabase de la compilation sont vides ; aucun compte serveur, paiement Google Play ou dialogue IA distant n’est actif. Les prix restent sans réduction : Premium 10 €/mois ou 120 €/an, Ultra 20 €/mois ou 240 €/an.

Le téléchargement utilise la [page GitHub du fichier](https://github.com/abdulmalikkakar48-blip/Code-1.1/blob/maree-apk-download/downloads/Selkie-Android-0.10.0.apk) avec un compte ayant accès au dépôt. Les anciens liens raw ont affiché 404 sur le téléphone ; l’accès du cloud ne prouve pas une distribution publique sans connexion. Voir [l’aide au téléchargement](../docs/TELECHARGER_APK.md).

## Sources conservées

- Commit exact du code : `90c595407dfbf1601f0f69eb6225980cb711f4a3`.
- Archive : `Selkie-Android-0.10.0-source.zip`, 644032 octets, 94 fichiers.
- SHA-256 : `75ab07f47c088667bdf0d1ce34df260d0cb6867d9feba5dbd10cfd46b52c2322`.
- CRC ZIP et égalité de chaque fichier avec le commit vérifiés ; script Gradle exécutable conservé. Aucun APK, cache de compilation ou fichier de signature privée dans les sources.
