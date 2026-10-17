# Selkie 0.22.0 — vingt illustrations gratuites

Validation du 17 octobre 2026. Sources exactes : `ee767fe66d6d55853b0540f2009ac189b6951eaa`. VersionCode 35, minSdk 29 (Android 10+), cible API 34. APK : 26370532 octets, SHA-256 `f123f769e9794ef27f73db29f721904590baccfc2a4876ec2a0c7064fd30f61e`. Même certificat de développement : `9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50`. ZIP source : 1255688 octets, SHA-256 `d1f8abfcb8ad091d18a5a3c0026c68f6544ea6e5dd8ce9553a9a5a0835ebbb41`. Les 206 entrées du ZIP ont été comparées aux blobs du commit hors downloads ; CRC validé.

## Catalogue

20 illustrations gratuites, regroupées en 8 Fleurs, 7 Paysages et 5 Motifs. Les huit modèles de 0.21.0 gardent exactement leurs IDs, les 625 régions dans leur ordre, chemins, couleurs suggérées et ancres. Les douze grilles de 12 × 12 cases restent séparées, avec leurs couleurs. Douze nouvelles illustrations ajoutent 1188 zones :

| Modèle | Zones |
| --- | ---: |
| sunflower_bouquet | 79 |
| water_lilies | 66 |
| iris_garden | 48 |
| magnolia_branch | 51 |
| forest_path | 72 |
| desert_dunes | 69 |
| terraced_garden | 89 |
| night_lake | 56 |
| garden_mandala | 169 |
| woven_leaves | 126 |
| seashell_spiral | 169 |
| patterned_mountains | 194 |

Total : 1813 zones illustrées. Les contours sont des créations originales intégrées, sans copie des images commerciales. Les douze fichiers *-exemple.png sont des rendus autonomes des courbes et couleurs suggérées, pas des captures Android. Les douze versions colorées ont été inspectées, ainsi que leurs contours/repères ; vérifications de visibilité et ancres des 1188 nouvelles régions, complétées par une relecture indépendante.

La galerie utilise LazyRow avec clé de dessin stable : miniatures visibles et préchargement, sans composer immédiatement les vingt aperçus. Changer de catégorie remet son défilement au début. Sélection et gardes de sauvegarde sont conservées. Seize couleurs, gomme, numéros facultatifs, remplissage par zone, zoom/pan et Annuler/Rétablir restent identiques ; aucun changement au moteur de dessin ni au stockage.

## Tests et conservation

Compilation debug et 281 tests JVM réussis, zéro échec, erreur ou ignoré. Lint : zéro erreur, cinq avertissements et cinq informations. Nouveau test : lecture des huit dessins publiés depuis leurs tailles fixes, sans réécriture ; ajout, relecture et suppression de la vingtième page sans altérer les couleurs précédentes ni les points. Tests existants étendus : 20 IDs uniques, catégories 8/7/5, sélection restaurée, contours/palette, stockage et export de toutes les pages en dessous du plafond de 30 000 caractères.

Export schéma 6, effacement global et garde contre les anciennes sessions inchangés. Les 32 assets de 0.21.0 sont identiques octet pour octet. Sept permissions inchangées, sans Internet ; aucune dépendance ou collecte ajoutée. Signature vérifiée par apksigner. Les 12 nouveaux IDs sont présents dans le DEX, sans diagnostic SelkieArtProbe. Les points restent plafonnés à 100, dé/calendrier/lectures/coach préparé conservés. Premium 5 €/mois ou 60 €/an ; Ultra 10 €/mois ou 120 €/an, sans réduction. Aucun achat réel ni IA distante. Aide et politiques FR/EN alignées.

## Vérification Android

APK final installé en mise à jour sur emulator-5554, Android 14 logiciel, 480 × 800. Son base.apk a le même SHA-256 que le livrable. En français et Nuit : galerie des illustrations affichée ; filtre Motifs puis défilement vers les deux derniers modèles, Spirales marines et Montagnes à motifs. La vingtième illustration a été sélectionnée depuis la galerie différée. Les deux captures natives distribuées proviennent de cet APK final.

À 100 %, geste pressé/relâché de 400 ms sur central_peak_terrace_2_1, index 71, ancre 505/246 : seule cette zone devient rose (indice 2). La couleur est confirmée dans les préférences, puis encore présente après arrêt du processus ; points 100. Après essai, seule la clé du dessin créé pour le test a été retirée des préférences courantes, sans restaurer une ancienne progression. Toutes les autres valeurs correspondent au départ ; thème Nuit identique octet pour octet, échelles d’animation 0 et délai écran 2147483647 préservés. Application relancée. Préférences et traces privées hors dépôt.

## Limites

Essai natif limité au parcours décrit et à une zone de la vingtième page ; les autres nouveaux modèles sont contrôlés par rendus, géométrie et stockage, pas entièrement colorés au toucher sur appareil. Zoom/pan, numéros, undo/redo à l’écran, anglais, TalkBack, grandes polices et Samsung A16 physique n’ont pas été réexercés pour cette version. Émulateur logiciel lent : attendre le rendu effectif. APK debug cible API 34 ; pas de publication Play ni facturation réelle. Éditeur public encore à décider, politiques à héberger ; aucune certification juridique. Liens GitHub soumis à l’accès au dépôt. La sauvegarde du brouillon cloud ne démontre pas une publication ou une restauration dans une nouvelle tâche.
