# Selkie 0.8.1 — essai du cadrage du lanceur

Version préparée le 11 octobre 2026, après accord de l’éditeur pour une réinstallation. Le nouveau fichier est distinct de l’archive 0.8.0.

## Fichier installé

- APK : `Selkie-Android-0.8.1.apk`.
- Taille : 23 475 888 octets, environ 24 Mo.
- SHA-256 : `8d534fdfcde170fed1857e5406410a8afd8e21c502a48b236f8ace34548c090d`.
- Package : `com.maree.app`, versionName `0.8.1`, versionCode `10`.
- Android minimal : 10 / API 29 ; targetSdk et compileSdk : 34.
- Signature officielle vérifiée avec Android Build Tools 34.0.0 : v2 valide, un signataire.
- SHA-256 du certificat : `9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50`.

La clé de développement existante a été réutilisée ; aucune nouvelle clé n’a été créée ni publiée. Son certificat diffère de celui de la refonte 0.8.0 (`c3def849…`). Une installation 0.8.0 utilisant cet autre certificat ne peut pas être mise à jour directement : il faut la désinstaller pour essayer 0.8.1, ce qui efface les points, défis et réglages locaux. Cet APK de test ne constitue pas une publication Google Play.

## Vérifications exécutées

```sh
source /workspace/.cloud-setup/activate.sh
./gradlew --no-daemon --max-workers=2 :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
node --test supabase/functions/coach/protocol.test.mjs
```

Compilation réussie ; 38 tests Android, zéro échec, erreur ou test ignoré ; 5 tests du protocole serveur réussis. Android Lint : zéro erreur et 13 avertissements. Les résultats JUnit et Lint appartiennent à cette compilation.

L’inspection `aapt dump xmltree` du drawable dans l’APK confirme les quatre marges `22.2222%` de type fraction (`TYPE_FRACTION`), `gravity="fill"` et `filter=true`. Le PNG du phoque, le fond du lanceur et les logos dans les écrans sont conservés. Les marges réduisent proportionnellement la silhouette pour la faire tenir dans la zone sûre adaptative.

## État du Coach et de la publication

Le client de conversation préparé est inclus, avec des paramètres Supabase vides. Il ne contacte pas un service par défaut et affiche que l’activation reste nécessaire. Aucun compte, serveur déployé ou crédit OpenAI API n’est configuré ; aucun dialogue réel n’a été testé. Les conseils locaux restent disponibles. Aucun paiement ni droit Premium/Ultra vérifié par Google Play n’est actif.

Le lanceur Samsung, les limites, notifications, défis et autorisations doivent être essayés sur le téléphone. Ce contrôle de compilation ne prouve pas encore le rendu sur le Samsung A16. La publication exige encore les services retenus, la facturation, les exigences Google Play actuelles, les documents et les tests de distribution.
