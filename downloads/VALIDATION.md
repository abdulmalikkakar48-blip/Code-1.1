# Validation de Selkie 0.8.0

## Contrôles automatisés

- Commande : `./gradlew --no-daemon --max-workers=2 :app:assembleDebug :app:testDebugUnitTest :app:lintDebug`.
- Compilation finale réussie en 52 s ; journal local `selkie-080-final-validation.log`.
- 31 tests : 26 Store et 5 SetupPreferences, aucun échec, erreur ou test ignoré.
- Android Lint : aucune erreur, 13 avertissements existants.
- APK : `com.maree.app`, versionCode 9, versionName 0.8.0, nom affiché Selkie, Android 10+, target API 34.
- Archive ZIP contrôlée sans erreur CRC ; icône Coach présente.
- Taille : **23 632 504 octets**.
- SHA-256 APK : `3870cf3e2ec2e2dc34d0f226492cabc0ca830a5af4440171eafab1f2eda56a20`.
- Signature vérifiée, certificat identique aux APK cloud 0.1.0 à 0.7.0 : SHA-256 `9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50`.
- Identifiant Android, clés de préférences et canaux de notification conservés. Les changements Store sont limités au commentaire du nom ; les règles n’ont pas été modifiées.
- Contrastes calculés : texte principal/fond 12,21:1, texte secondaire/fond 4,90:1, bouton blanc/vert 8,02:1 et menthe/vert profond 9,67:1.

## Fonctionnement et outils

Lovable, Figma et MagicPath ont été demandés, mais aucun de leurs outils n’est accessible dans cette session. Les liens vers leurs fiches ne constituent pas une connexion. La refonte est réalisée dans le code Android Kotlin/Compose ; aucune capture n’est présentée comme une maquette issue de ces services.

Le Coach propose des conseils fixes hors ligne, pas des réponses produites par une IA. Le dialogue IA, les vrais achats et la synchronisation restent à développer. Aucun accès Internet ou secret de serveur n’est ajouté à l’APK.

Avant Google Play : vérifier la disponibilité de Selkie, mettre à niveau le SDK, préparer la signature de production, la politique publique, les déclarations et le parcours de tests. Aucun compte développeur, paiement ou publication Google Play n’est créé par cette tâche.

## Contrôle Android de la refonte

Un AVD Android 14 existant, 480 × 800 px à 240 dpi (320 dp de large), a démarré en rendu logiciel sans KVM. Android a présenté des ANR de System UI pendant le démarrage, avant l’ouverture de l’application. Après attente, il est devenu utilisable.

Avant le dernier changement de marque, le binaire de la même refonte a permis d’observer le parcours des autorisations, le bouton de suivi désactivé avec 0/2 accès, la confidentialité avant l’entrée, son retour au parcours, l’exploration sans suivi et les cinq onglets. Les jours 1 à 5 étaient chacun sur une ligne et dans l’ordre. Le Coach affichait « Conseils hors ligne ». Les données locales de l’AVD ont été conservées après réinstallation. Aucun MonitorService ne tournait en découverte sans suivi et aucune entrée de crash du processus de l’application n’a été observée dans le buffer consulté.

Le dernier binaire Selkie reprend ces écrans, le logo phoque, le bandeau de marque, le résumé Défis compact et le libellé court « À valider ». Les captures du dernier binaire sont séparées des essais antérieurs. Le suivi complet, les surcouches et les longues durées n’ont pas été validés sur un Samsung A16 réel ; les tests unitaires couvrent les règles.

Le dernier APK Selkie a été réinstallé sans effacement. Le nouveau logo, l’accueil, le résumé des défis et les cinq journées sur des lignes distinctes ont été observés sur le binaire final. Un contrôle avec texte à 150 % montre le défi et la navigation lisibles, avec « Quotidien » sur deux lignes. Les [quatre captures réelles](screenshots/README.md) correspondent au SHA-256 indiqué ci-dessus et sont conservées sans retouche. Le texte à 200 %, les surcouches en suivi actif et l’A16 réel ne sont pas validés par ces captures.
