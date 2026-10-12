# Selkie 0.10.1 — menu organisé et fond géométrique

APK de test construit le 12 octobre 2026.

- Fichier : `Selkie-Android-0.10.1.apk`, 25379742 octets (environ 25 Mo).
- SHA-256 : `79d459e077628b382a98ca3ff68e129d2b41a159562247732ded471b1450e9de`.
- Package : `com.maree.app`, versionCode 16, versionName 0.10.1, Android 10+, cible 34.
- Signature Android v2 vérifiée avec l’outil officiel ; certificat `9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50`, identique à 0.10.0. Clé privée exclue du dépôt et des sources.

## Modification

Un bouton ☰ accessible en haut à droite ouvre un menu latéral classé en trois groupes : Réglages (Langue, Apparence, Accès Android), Offres (Abonnements), Informations (Règles de Selkie, Confidentialité et données). Chaque entrée ouvre une page dédiée. La barre du bas garde Accueil, Quotidien, Défis et Coach. Le badge de l’offre et l’accès Premium depuis Défis ouvrent Abonnements.

Les règles réutilisent treize réponses existantes d’aide. Les offres réutilisent leur composant et leurs conditions ; les informations de compte et changement de téléphone restent accessibles. La politique complète, son résumé, son contact et l’effacement local gardent l’écran de confidentialité existant. L’accès depuis l’accueil initial reste disponible. Le retour Android ferme le menu ou revient à l’onglet précédent pour les réglages. L’activation du suivi repasse par les explications et les accès Android.

Les chemins de navigation dans l’aide et l’accueil initial sont corrigés en français et en anglais. Le coach préparé, les 1 152 questions par langue, les deux cartes question/réponse, le mode Nuit, les points, défis et données sont conservés. Premium 10 €/mois ou 120 €/an ; Ultra 20 €/mois ou 240 €/an, sans réduction. Aucun paiement ou compte serveur n’est activé ; constantes Supabase de compilation vides.

## Contrôles

`./gradlew --no-daemon --max-workers=2 :app:assembleDebug :app:testDebugUnitTest :app:lintDebug` exécuté avec Java 17, Gradle 8.7 et SDK 34 : compilation réussie, **97 tests unitaires**, zéro échec, erreur ou test ignoré. Après correction de trois chemins/libellés, cette commande a été réexécutée sur le code final.

Android Lint : zéro erreur ou erreur fatale, trois avertissements et une information. Deux avertissements existants concernent `fullBackupContent` et l’icône `v26` ; le troisième signale l’ancienne ressource `ic_nav_more` inutilisée depuis le retrait de Plus. L’information concerne un état entier Compose. Aucun contrôle désactivé.

Une revue indépendante confirme les routes, le retour Android, les callbacks de suivi, les quatre onglets et la conservation des règles, tarifs, confidentialité et informations de comptes.

La mise à jour s’est installée avec `adb install -r` sur l’émulateur Android 14, 480 × 800, en conservant les points et préférences observés. Des XML frais et des captures confirment le bouton Open menu, les quatre onglets, l’ouverture du panneau, ses groupes et entrées visibles, la page Language accessible depuis ce menu, puis la bascule en français et les libellés français. Le mode Nuit était conservé dans cette installation. Une première capture XML demandée pendant le lancement n’avait pas de nœud racine ; une capture fraîche après démarrage a confirmé le contenu.

[Menu anglais](Selkie-Android-0.10.1-menu-en.png) · [Menu français](Selkie-Android-0.10.1-menu-fr.png).

Le parcours complet de tous les écrans et les essais sur un Samsung physique restent à faire. Les observations de cette version ne remplacent pas ces essais ni une validation de publication Google Play.

## Fond géométrique et mouvement vérifié

Quatre contours discrets (deux cercles, un triangle, un losange) se déplacent sur une boucle de 24 secondes derrière les quatre onglets et les réglages. Les cartes restent opaques. Le dessin ne reçoit pas de gestes et ne recompose pas les écrans à chaque image. Il respecte le réglage Android des animations, observe aussi la valeur effective sur Android 13+ et s’arrête hors du cycle RESUMED. Aucun mouvement n’est enregistré ni transmis.

Une première comparaison a trouvé le fond statique lors d’une réactivation : l’observation de la valeur effective Android a été corrigée. Une autre paire prise trop tôt pendant le lancement contenait une transition de l’écran initial et n’a pas été retenue comme validation. Sur l’accueil ensuite stabilisé, la comparaison de pixels hors barre d’état donne : 2887 pixels changés avec les animations activées ; 0 avec les animations désactivées ; 2975 après réactivation. Les trois conditions attendues sont vérifiées et le réglage initial de l’émulateur est restauré. Le code final passe les 97 tests et Lint sans erreur.

[Accueil Nuit avec formes géométriques](Selkie-Android-0.10.1-geometry-night.png). Les essais du menu et de la langue ci-dessus précèdent l’ajout du dessin décoratif ; leur routage est conservé. L’accueil final et le mouvement ont été observés après cet ajout. La suspension lors du passage en arrière-plan est implémentée par le cycle de vie, sans mesure directe supplémentaire sur téléphone.

## Sources conservées

- Commit exact du code : `4afe4d3df9ba3fa6c0cb130355e6daeca457dd2c`.
- Archive : `Selkie-Android-0.10.1-source.zip`, 652026 octets, 98 fichiers.
- SHA-256 : `6dba7bf72cc31913324d79a8889ec19adfa873a62c8a009a88660add3ad8f72d`.
- CRC ZIP et égalité de chaque fichier avec le commit vérifiés ; script Gradle exécutable conservé. Aucun APK, cache ou fichier de signature privée dans l’archive.

Le fichier est disponible sur la [page GitHub de l’APK](https://github.com/abdulmalikkakar48-blip/Code-1.1/blob/maree-apk-download/downloads/Selkie-Android-0.10.1.apk) avec un compte ayant accès au dépôt. Choisir Download raw file. Un accès réussi depuis le cloud ne prouve pas une distribution publique sans connexion.
