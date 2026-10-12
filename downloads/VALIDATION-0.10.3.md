# Selkie 0.10.3 — fond d’eau en points

APK de test construit le 12 octobre 2026.

- Fichier : `Selkie-Android-0.10.3.apk`, 25408750 octets (environ 25 Mo).
- SHA-256 : `e08371b091e2aa7739f4e69c1cda9fb99a8bc48ad11f8d0008a0c5976f6cf57d`.
- Package : `com.maree.app`, versionCode 18, versionName 0.10.3, Android 10+, cible 34.
- Signature v2 vérifiée avec l’outil officiel Android ; certificat `9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50`, identique à 0.10.2. Aucune signature privée publiée.

## Changement

Le fond utilise une nappe de points sur toute sa surface, avec une marge de deux cellules hors écran. Des vagues liées aux coordonnées x/y font onduler les rangées comme de l’eau. Elles combinent 18 et 5 dp verticalement et 6 dp horizontalement ; la boucle dure 12 secondes. La grille commence à 18 dp d’espacement, puis s’adapte pour rester sous 1 600 points, y compris sur les grands écrans. Rayon des points : 1,35 dp.

Trois niveaux de luminosité dessinent les creux et les crêtes. L’opacité maximale est de 0,32 en Nuit et de 0,14 en Clair. Une revue a identifié un contraste insuffisant pour les petits textes sans carte avec l’opacité claire initiale de 0,20 ; cette valeur a été réduite et les points légèrement agrandis pour conserver la visibilité du maillage. Le rayon a changé de 1,2 à 1,35 dp. Les cartes restent opaques.

Les buffers FloatArray et les trois peintures sont réutilisés. Trois appels natifs drawPoints au maximum remplacent le dessin individuel des anciens contours. La phase est lue uniquement dans Canvas ; pas de recomposition des écrans par image, de geste capturé ou de donnée de mouvement conservée. Le cycle RESUMED et la suppression Android des animations restent respectés. La couleur suit le thème choisi.

Les menus, quatre onglets, langues, 1 152 questions préparées par langue, cartes question/réponse, points, limites, défis et offres sont conservés. Premium 10 €/mois ou 120 €/an, Ultra 20 €/mois ou 240 €/an, sans réduction. Aucun paiement, compte serveur ou dialogue IA distant n’est activé.

## Vérification

`./gradlew --no-daemon --max-workers=2 :app:assembleDebug :app:testDebugUnitTest :app:lintDebug` a été exécuté puis réexécuté après l’ajustement de contraste. La compilation finale réussit avec Java 17, Gradle 8.7 et SDK 34. **97 tests unitaires**, zéro échec, erreur ou test ignoré. Lint : zéro erreur/fatale, trois avertissements et une information, identiques à 0.10.2 ; aucun contrôle désactivé. Les constantes publiques Supabase restent vides.

Une revue indépendante confirme le plafond, les buffers, les comptes pairs de coordonnées drawPoints, les marges, la périodicité, les ressources réutilisées et la gestion du cycle de vie. Le problème concret de contraste clair a été corrigé avant l’APK final.

La mise à jour finale s’est installée avec adb install -r sur l’émulateur Android 14, 480 × 800, en conservant les 120 points et la langue française observés. Le menu et sa page Apparence permettent de passer de Clair à Nuit. Les captures finales montrent le fond d’eau et les cartes lisibles dans ces deux modes. Plusieurs captures XML demandées trop tôt pendant le lancement ou le changement de thème n’avaient pas de nœud racine ; elles ne servent pas de preuve de fonctionnement. Les captures stables sont retenues.

Les comparaisons de captures de l’accueil stabilisé, espacées de deux secondes et sans la barre d’état, donnent :

| Thème | Animations activées | Animations désactivées | Après réactivation |
| --- | ---: | ---: | ---: |
| Clair | 9284 pixels modifiés | 0 | 9421 |
| Nuit | 9293 pixels modifiés | 0 | 9495 |

Le mouvement, son arrêt et sa reprise sont donc observés dans les deux thèmes. Le réglage Android initial de l’émulateur est restauré après chaque essai.

[Fond clair](Selkie-Android-0.10.3-water-light.png) · [Fond nuit](Selkie-Android-0.10.3-water-night.png).

Les essais sur Samsung physique, le parcours complet de tous les écrans et la validation de publication Google Play restent à faire. La pause en arrière-plan est implémentée par le cycle de vie ; elle n’a pas été mesurée lors de ces essais. Une comparaison de captures confirme les déplacements et non une consommation énergétique ou une fluidité sur tous les téléphones.

## Sources

- Commit exact : `007296cd1f92fa32921a0c4397aefbb4f33006d2`.
- Archive : `Selkie-Android-0.10.3-source.zip`, 655746 octets, 99 fichiers.
- SHA-256 : `19a2a107e03e4bcd6b73ab8f8de1fb544b71cd25af975b10a7fdfe0c35fc188c`.
- CRC ZIP et égalité de chaque fichier avec le commit vérifiés ; script Gradle exécutable conservé. APK, caches, .env et clés privées sont exclus.

[Téléchargement sur la page GitHub de l’APK](https://github.com/abdulmalikkakar48-blip/Code-1.1/blob/maree-apk-download/downloads/Selkie-Android-0.10.3.apk), avec un compte ayant accès au dépôt, puis Download raw file. L’accès cloud ne prouve pas une distribution publique sans connexion.
