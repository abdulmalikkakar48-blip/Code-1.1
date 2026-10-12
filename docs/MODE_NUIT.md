# Apparence — Selkie 0.10.3

Dans **le menu ☰ en haut → Apparence**, choisir **Clair**, **Nuit** ou **Comme le téléphone**. Le clair reste le choix initial pour conserver le design existant. Nuit utilise un fond presque noir, des surfaces bleu marine et des accents bleus clairs. Comme le téléphone suit le réglage d’apparence d’Android.

Les textes, cartes, menus, dialogues, boutons, navigation et barres système utilisent des couleurs cohérentes et adaptées à l’apparence. Le logo garde son fond existant pour rester lisible ; l’écran rouge de blocage conserve son rôle. Le changement de thème conserve les points, les limites, les validations et les abonnements de démonstration. Il n’active aucun suivi, compte ou paiement.

Le choix est enregistré localement dans `selkie_appearance`, sans réseau et avec confirmation de l’écriture avant la mise à jour. Il est exclu des sauvegardes Android avec les autres préférences. L’effacement global retire aussi ce choix et revient à Clair.

La palette distingue les accents, leur texte, les fonds de cartes et les fonds de navigation. Les couleurs sont dynamiques : les valeurs utilisées dans les écrans ne doivent pas être capturées une seule fois. Les tests des contrastes vérifient des couples réellement utilisés en clair et en nuit ; ils ne remplacent pas la vérification visuelle sur Android et Samsung.

## Surface d’eau pointillée

Les quatre onglets et les pages des réglages utilisent une nappe de plusieurs centaines de points répartis sur toute la surface du fond. Des vagues font onduler cette nappe comme de l’eau, avec une boucle de 12 secondes et une visibilité renforcée en clair comme en nuit. Le maillage s’adapte à la surface, avec une limite de 1 600 points, y compris sur les grands écrans. L’espacement de départ est de 18 dp ; il augmente si nécessaire pour respecter cette limite. Le rayon des points est de 1,35 dp. Les vagues combinent 18 et 5 dp de déplacement vertical, ainsi que 6 dp horizontalement. Les crêtes utilisent l’accent du thème : opacité maximale de 0,32 en Nuit et 0,14 en Clair, avec trois niveaux de luminosité. La teinte claire préserve le contraste des petits textes sans carte. Les cartes restent opaques. Le fond ne reçoit aucun geste et ne crée aucun élément interactif pour le lecteur d’écran.

L’animation s’arrête hors du cycle RESUMED de l’activité et devient statique si Android désactive les animations. Le réglage système est observé et revérifié à la reprise. Le dessin lit l’état de mouvement dans Canvas, sans recomposer les écrans à chaque image ; les rafraîchissements sont espacés pour cette décoration lente. Aucun choix supplémentaire ni donnée de mouvement n’est enregistré. Les contrôles sur appareil réel restent nécessaires.

Le fond, le mouvement, l’arrêt et la reprise ont été observés dans les deux thèmes sur émulateur pour la version 0.10.3 ; voir [la validation 0.10.3](../downloads/VALIDATION-0.10.3.md).

## Fond géométrique de 0.10.2 — archive

Les quatre onglets et les pages des réglages ont quatre formes décoratives composées de **144 points** : deux cercles (48 et 32 points), un triangle arrondi (36) et un losange (28). Une onde serpente sur les contours tandis que leurs centres dérivent doucement, jusqu’à 6 dp. Le mouvement suit une boucle de 24 secondes. Les couleurs utilisent la palette courante : opacité principale de 0,10 en clair et 0,18 en nuit ; secondaire de 0,08 en clair et 0,14 en nuit. Les cartes restent opaques ; le fond ne reçoit aucun geste et ne crée aucun élément interactif pour le lecteur d’écran.

La version 0.10.1 utilisait des contours géométriques avec une dérive lente et une petite rotation des polygones. Ses artefacts restent archivés. Le mouvement, son arrêt et sa reprise ont été vérifiés sur émulateur dans la version 0.10.2 ; voir [la validation](../downloads/VALIDATION-0.10.2.md).
