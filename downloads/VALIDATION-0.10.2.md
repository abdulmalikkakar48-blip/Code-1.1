# Selkie 0.10.2 — contours en points et mouvement ondulant

APK de test construit le 12 octobre 2026.

- Fichier : `Selkie-Android-0.10.2.apk`, 24065850 octets (environ 24 Mo).
- SHA-256 : `993f1a54499c3cc29dc71d05b2dda29cbb468461a23c99ef363b98c885675022`.
- Package : `com.maree.app`, versionCode 17, versionName 0.10.2, Android 10+, cible 34.
- Signature v2 vérifiée avec l’outil officiel Android ; certificat `9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50`, identique à 0.10.1. La clé privée est exclue du dépôt et des sources.

## Changement

Les quatre contours continus sont remplacés par 144 petits points : deux cercles de 48 et 32 points, un triangle arrondi de 36 points et un losange de 28 points. Deux ondulations parcourent chaque contour pour un mouvement serpentant ; les centres dérivent jusqu’à 6 dp. Les déplacements bouclent toutes les 24 secondes. Les amplitudes sont de 5,5 / 4 / 3,5 / 3 dp et les couleurs suivent le thème, avec une opacité discrète. Les cartes restent opaques.

Les tableaux de coordonnées et normales sont préparés une fois. Le dessin n’ajoute ni état par point, ni gestes, ni éléments accessibles interactifs. La phase est lue uniquement dans Canvas ; les écrans ne sont pas recomposés à chaque image. Le dessin respecte la désactivation Android des animations et le cycle RESUMED, avec l’observation de la valeur effective du framework sur Android 13+. Aucun mouvement n’est enregistré ni envoyé.

Le menu ☰ et ses six entrées, les quatre onglets Accueil / Quotidien / Défis / Coach, les 1 152 questions et réponses préparées par langue, les cartes question au-dessus et réponse dessous, les langues, points, défis, données et prix sont conservés. Premium 10 €/mois ou 120 €/an ; Ultra 20 €/mois ou 240 €/an, sans réduction. Les offres sont des démonstrations ; aucun paiement, compte serveur ou dialogue IA distant n’est activé. Les deux constantes publiques Supabase sont vides.

## Contrôles réalisés

`./gradlew --no-daemon --max-workers=2 :app:assembleDebug :app:testDebugUnitTest :app:lintDebug` : compilation réussie avec Java 17, Gradle 8.7 et SDK 34. Les **97 tests unitaires** ont été exécutés, sans échec, erreur ou test ignoré. Android Lint : zéro erreur ou erreur fatale, trois avertissements et une information, identiques à 0.10.1 ; aucun contrôle désactivé.

Une revue indépendante du helper et de son intégration n’a trouvé aucun problème concret de normales, périodicité, compatibilité Android ou capture tactile.

`adb install -r` a installé la mise à jour sur l’émulateur Android 14 (480 × 800). L’accueil final conserve les 120 points, la langue française et le mode Nuit observés auparavant. Des XML fraîchement capturés et des images confirment le bouton du menu, les quatre onglets, le panneau français, l’entrée Confidentialité et données après défilement, puis l’ouverture de l’écran de confidentialité depuis cette entrée.

Sur l’accueil stabilisé, les comparaisons de captures séparées de deux secondes et excluant la barre d’état donnent : **1971 pixels modifiés** avec animations activées ; **0** quand elles sont désactivées ; **2036** après réactivation. Le mouvement, l’arrêt et la reprise sont observés. Le réglage système initial de l’émulateur a été restauré après l’essai.

[Accueil Nuit avec les contours en points](Selkie-Android-0.10.2-geometry-night.png) · [Menu français](Selkie-Android-0.10.2-menu-fr.png).

Les essais complets de tous les écrans, les tests sur Samsung physique et la validation de publication Google Play restent à faire. La pause en arrière-plan est implémentée par le cycle de vie, sans mesure supplémentaire lors de ce contrôle. Les observations de bascule de langue de 0.10.1 sont conservées dans [sa validation](VALIDATION-0.10.1.md) ; elles ne sont pas présentées comme une nouvelle bascule dans cette version.

## Sources conservées

- Commit exact du code : `eecdba6c425ba161375db07c41f07a92234ed529`.
- Archive : `Selkie-Android-0.10.2-source.zip`, 654493 octets, 99 fichiers.
- SHA-256 : `796f3acb79384b930fe8a14bc05687b90544a91ac171ae906d6ea7453f748330`.
- CRC ZIP et égalité de chaque fichier avec le commit vérifiés ; permissions exécutables du script Gradle conservées. Aucun APK, cache, fichier .env ou signature privée dans l’archive.

L’APK est disponible sur [sa page GitHub](https://github.com/abdulmalikkakar48-blip/Code-1.1/blob/maree-apk-download/downloads/Selkie-Android-0.10.2.apk) avec un compte ayant accès au dépôt. Choisir **Download raw file**. L’accès cloud ne démontre pas une distribution publique sans connexion.
