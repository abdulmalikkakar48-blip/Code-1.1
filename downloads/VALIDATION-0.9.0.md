# Selkie 0.9.0 — Coach à conseils préparés

APK de test construit le 11 octobre 2026.

- Fichier : `Selkie-Android-0.9.0.apk`, 23625744 octets (environ 24 Mo).
- SHA-256 : `a754706f84b9f525600647433ad885fb497a9de516319a9421eca055ec50f589`.
- Package : `com.maree.app`, versionCode 11, versionName 0.9.0, Android 10+, cible 34.
- Signature officielle Android vérifiée : v2 valide, un signataire, certificat `9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50`. Même clé que le fichier 0.8.1 distribué. Une installation utilisant le certificat fixe de 0.8.0 reste incompatible pour une mise à jour directe.

## Contrôles exécutés

`./gradlew --no-daemon --max-workers=2 :app:assembleDebug :app:testDebugUnitTest :app:lintDebug` : compilation réussie, **53 tests Android** dont 15 tests du moteur, zéro échec, erreur ou test ignoré. Android Lint : zéro erreur, treize avertissements et une information.

Les cas du moteur comprennent des questions avec le catalogue réel, les accents et petites fautes, les bornes des mots, le meilleur score et les égalités d’intention, les variantes non répétées, les suggestions, les points en quarts, les durées locales réelles et la priorité des phrases de détresse.

Une revue indépendante du branchement UI, des suggestions et de l’effacement n’a pas identifié de défaut bloquant. Cette revue est statique ; les contrôles de compilation ne constituent pas un essai sur le Samsung A16. Clavier, défilement et réglages de conservation locale restent à essayer sur téléphone.

## Fonctionnement livré

40 sujets et 120 conseils écrits à l’avance sont accessibles par défaut, sans compte, API ni connexion. Le champ de discussion reconnaît des sujets connus et fournit quatre boutons en cas de phrase inconnue. Les compteurs repris dans une réponse sont ceux de la période de suivi, pas un temps quotidien inventé. Les durées ne sont pas reprises si le suivi est inactif.

La conversation reste en mémoire ; seules les phrases inconnues non identifiées comme sensibles peuvent être conservées, après activation volontaire, jusqu’à 50 sur le téléphone. Aucun export automatique ou envoi à l’éditeur. L’effacement utilise une génération pour rejeter les écritures retardées.

Le dialogue IA génératif distant reste non configuré et n’est pas présenté comme actif. Aucun paiement ni droit d’abonnement vérifié par Google Play n’est intégré. Le phoque et son cadrage, les couleurs et les règles du suivi restent conservés. Voir [le fonctionnement](../docs/COACH_HORS_LIGNE.md).

## Sources conservées

- Commit exact du code : `08e7bbacb5bdbb64bdc0ffe63971a68983bf0a64`.
- Archive : `Selkie-Android-0.9.0-source.zip`, 555472 octets, 78 fichiers.
- SHA-256 : `1e28317e665bbbe7490d5a24ff4729b53a41c74a98592d85ddecbfb362d74b12`.
- CRC ZIP et égalité de chaque fichier avec le commit vérifiés ; script Gradle exécutable conservé. Aucun APK, cache de compilation ou fichier de signature privée dans les sources.
