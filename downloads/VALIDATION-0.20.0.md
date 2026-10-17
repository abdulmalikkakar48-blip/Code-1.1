# Selkie 0.20.0 — illustrations botaniques à colorier

Validation du 17 octobre 2026. Source exacte : `0705738157b5811c80a29aca86415b995a4bd02f`. VersionCode 33, Android 10+ (minSdk 29), cible API 34. APK : 25527516 octets, SHA-256 `561bcdb3c265c8fb22924b67d394f7a0acfbb400def2e88000ecc605584d91e2`. Certificat de développement conservé : `9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50`. ZIP source : 999918 octets, SHA-256 `684c2d9c44c15dd7a87661970f8ff5f8daa7936dc4f8686fd337b45b17814433`. Ses 200 entrées ont été comparées aux blobs du commit, hors téléchargements ; CRC validé.

## Fonction ajoutée

Quotidien → Coloriage distingue Illustrations et À carreaux. Les douze pages précédentes et leurs couleurs restent séparées. Les quatre nouvelles illustrations originales sont Jardin de roses (48 zones), Bouquet sauvage (71), Tulipes de printemps (47) et Feuillages (55), soit 221 zones courbes fermées. Les contours et nervures sont dessinés pour Selkie ; aucune image commerciale des références n’est incorporée. Les quatre fichiers *-exemple.png montrent des rendus des mêmes courbes, avec leurs couleurs suggérées ; ils ne sont pas des captures de l’application et ne précolorent pas la feuille personnelle.

Seize couleurs et une gomme, repères numérotés facultatifs, zoom de 100 à 300 %, déplacement au zoom, remplissage d’une zone au toucher et commandes de sélection de zone. Les numéros suggèrent des couleurs ; ce n’est pas une obligation de couleur. Annuler/Rétablir garde cinquante actions pendant la session de la page. Les couleurs sont enregistrées après chaque zone, avec message et nouvelle tentative distincte sur échec de sauvegarde ou d’effacement.

Les écritures sont lancées immédiatement puis leur seul bloc de commit reste non annulable ; quitter un écran ne supprime pas une écriture déjà demandée. La garde de génération empêche une ancienne session de restaurer des dessins après effacement global. Les anciennes grilles bénéficient de la même protection de sauvegarde. La clé distincte illustrationPages_v1 borne les données et filtre identifiants, nombres de zones et indices 0..16. Export JSON schéma 6 : storedData.illustrations contient seulement id et colors, séparément de storedData.coloringPages. Guide et politiques FR/EN mis à jour.

Tout reste gratuit dans les trois offres, sans points ni téléchargement d’image, nouvelle dépendance ou permission. Les sept permissions sont identiques à 0.19.0, sans Internet. Tous les assets existants de l’APK sont conservés octet pour octet. Le plafond de 100 points, le dé Points, le calendrier, les lectures, le coach préparé et les prix 5/10 €/mois et 60/120 €/an sans réduction sont conservés. Aucun paiement réel ni IA distante.

## Tests

Compilation debug et **276 tests JVM réussis**, zéro échec, erreur ou test ignoré. Lint : zéro erreur, cinq avertissements et cinq informations. Les dix-sept nouveaux tests couvrent le stockage des zones et la conservation des grilles, la validation stricte des données et exports, les deux comportements possibles d’un commit en échec, la reprise, l’effacement et les anciennes sessions, les cinquante undo/redo, les IDs et contours du catalogue. Aucun test désactivé. Signature vérifiée avec apksigner ; aucune trace des sondes temporaires de diagnostic dans les fichiers DEX du livrable.

## Vérification Android

APK final installé en mise à jour sur emulator-5554, Android 14 logiciel, 480 × 800. Le base.apk installé a le même SHA-256 que le livrable. En français et Nuit, la galerie montre les dessins colorés, une feuille personnelle blanche et une palette numérotée. La galerie distribuée a été capturée sur la première construction ; son affichage est inchangé par le correctif final. Les deux captures de coloriage et de zoom sont prises sur l’APK final.

Le premier contrôle du toucher a trouvé un blocage réel : la géométrie détectait correctement le pétale, mais la fonction mémorisée gardait la valeur dérivée initiale editable=false du chargement. Le correctif relit les états au moment du geste et fournit une fonction de remplissage mise à jour. Après correction, un toucher pressé/relâché de 400 ms remplit exactement rose_front_cup, zone 37, en rose (indice 2). À 150 % de zoom, un second toucher remplit exactement rose_outer_right, zone 27. Les pixels colorés restent dans leurs contours ; les deux zones sont confirmées dans le stockage. Le libellé Centrer tient sur une ligne sur cet écran.

Une zone de tige avait auparavant été coloriée par le bouton Appliquer et a été conservée lors des mises à jour. Les trois couleurs sont encore présentes dans le fichier privé après arrêt du processus, ce qui confirme leur persistance. Le solde reste 100 points. Après les essais, seule cette illustration de test a été retirée des préférences courantes, sans restaurer une ancienne progression ; toutes les autres préférences, les applications, défis, tâches, grilles, offre et reçu du dé sont conservés. Thème Nuit inchangé octet pour octet, trois échelles d’animation à zéro et délai écran 2147483647 préservés. Application relancée. Les préférences et traces de diagnostic restent privées hors dépôt.

## Limites

L’émulateur est lent ; les captures attendent le rendu effectif. Les essais natifs décrits portent sur la rose, deux zones touchées et le zoom à 150 %. Le déplacement au zoom, les numéros, les quatre pages complètes, les undo/redo à l’écran, l’anglais, la grande police, TalkBack et le Samsung A16 physique n’ont pas tous été validés nativement. Les contrôles de stockage et de catalogue sont distincts de ces essais visuels. APK debug API 34 : aucune publication Play ou facturation réelle. Les pages légales statiques restent à héberger ; aucune certification juridique. Les liens GitHub nécessitent l’accès au dépôt.
