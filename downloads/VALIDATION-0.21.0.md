# Selkie 0.21.0 — paysages et arbre aux motifs

Validation du 17 octobre 2026. Sources exactes : `f4df220561fdc408f310ac3b0228a5760290af9c`. VersionCode 34, Android 10+ (minSdk 29), cible API 34. APK : 25865872 octets, SHA-256 `615018d591e53d820068bff8b948f3aa707b08f53e754c8da06ba0dcd4889e09`. Certificat de développement conservé : `9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50`. ZIP source : 1106326 octets, SHA-256 `8c1bff8a0c8d7cc2037f3c2b377bc876dc85a14ad79ff7c93f9b8295c9f69cf4` ; ses 203 entrées ont été comparées aux blobs du commit hors downloads, CRC validé.

## Ajouts

Quotidien → Coloriage → Illustrations conserve les quatre dessins floraux et ajoute Vallée de la cascade (67 zones), Lac des sommets (77), Jardin côtier (67) et Arbre aux motifs (193). Huit illustrations, 625 zones en tout. Catégories Toutes, Fleurs, Paysages et Motifs ; une sélection restaurée doit appartenir au filtre choisi. Un changement de filtre remet le défilement des miniatures au début. Les changements de dessin et de catégorie sont bloqués pendant le chargement, une sauvegarde, un effacement ou un échec à résoudre.

Les quatre dessins historiques gardent exactement leurs identifiants, leurs 221 régions dans le même ordre et leurs courbes. Les douze pages À carreaux restent séparées. Seize couleurs, gomme, remplissage au toucher, zoom à 300 %, déplacement au zoom, numéros facultatifs et cinquante annuler/rétablir sont conservés. Tous les modèles restent gratuits. Les aperçus utilisent les couleurs suggérées sans écrire dans la feuille personnelle. Les dessins sont originaux, sans copie d’images commerciales. Les quatre fichiers *-exemple.png sont des rendus autonomes des courbes du catalogue, pas des captures Android. Versions blanches, colorées et numérotées contrôlées ; les 404 nouvelles régions restent visibles après les superpositions et leurs ancres appartiennent à leur portion visible.

## Données et fonctions conservées

Aucun changement de format : illustrationPages_v1, limites et filtrage du stockage, export JSON schéma6, effacement global et protection contre les écritures d’une ancienne session conservés. Les descriptions et politiques FR/EN reflètent les huit illustrations. Aucune dépendance, permission ni collecte supplémentaire : les sept permissions sont identiques à0.20.0, sans Internet. Les32 assets de l’APK antérieur sont identiques octet pour octet.

Plafond100points, quatre modes du dé, calendrier, lectures, coach préparé FR/EN et offres conservés. Prix prévus : Premium5€/mois ou60€/an ; Ultra10€/mois ou120€/an, sans réduction. Aucun achat réel ni IA distante.

## Vérifications

Compilation debug réussie ; **280 tests JVM réussis**, zéro échec, erreur ou test ignoré. Android Lint : zéro erreur, cinq avertissements, cinq informations. Les nouveaux tests couvrent le filtrage/restauration et la lecture des quatre dessins0.20.0 sans réécriture, puis l’ajout et la suppression d’un paysage sans altérer ces dessins ni les anciennes grilles. Les tests existants couvrent aussi le catalogue complet, ses identifiants et tailles, la sauvegarde/export de toutes les pages, les échecs de commit et l’effacement. Signature vérifiée par apksigner ; les quatre nouveaux IDs sont présents dans le DEX, aucun diagnostic SelkieArtProbe.

## Essai Android sur l’APK final

APK installé en mise à jour sur emulator-5554, Android14 logiciel,480×800. Le SHA-256 du base.apk installé est identique au livrable. En français/Nuit, les filtres Paysages et Motifs montrent leurs modèles et sélectionnent leur première feuille blanche. Les captures publiques proviennent de cette construction finale.

Cascade : toucher pressé/relâché de400ms → pool, index26, couleur2. Après passage à150%, un second toucher → waterfall_light_stream, index22, couleur2. Les deux seules zones sont confirmées dans les préférences, puis encore présentes après arrêt du processus. Points inchangés à100.

Arbre : après réouverture, la catégorie Motifs affiche Arbre aux motifs. Un toucher à100% remplit uniquement sun_heart, index2, couleur2. Après arrêt du processus, les deux dessins et leurs trois zones sont persistés. Les5 captures Android distribuées montrent les galeries, les zones peintes et le zoom ; aucune capture du splash n’est présentée comme preuve.

Après essais, seule la clé des deux illustrations créées pour ce test a été retirée du fichier de préférences courant, sans restaurer une ancienne progression. Toutes les autres préférences sont identiques à leur état initial ; points100, anciennes grilles, tâches, applications, défis, offre et reçu du dé préservés. Thème Nuit inchangé octet pour octet ; trois échelles d’animation0 et délai écran2147483647 conservés. Application relancée. Préférences et traces privées hors dépôt.

## Limites

L’émulateur logiciel est lent : attendre le rendu effectif après les gestes. Essais tactiles natifs limités à la cascade et à l’arbre, trois zones et zoom150% ; les deux autres paysages, le remplissage complet, tous les numéros, le déplacement, les undo/redo à l’écran, l’anglais, TalkBack, grandes polices et SamsungA16 physique n’ont pas tous été validés nativement. Les détails les plus petits demandent le zoom. APK debug cibleAPI34 ; aucune publication Play ni facturation réelle. L’identité publique de l’éditeur reste à décider et les pages légales à héberger ; aucune certification juridique. Les liens GitHub exigent l’accès au dépôt. Le dépôt et un brouillon d’environnement sauvegardés ne prouvent pas la publication cloud ou une restauration dans une nouvelle tâche.
