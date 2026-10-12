# Apparence — Selkie 0.10.2

Dans **le menu ☰ en haut → Apparence**, choisir **Clair**, **Nuit** ou **Comme le téléphone**. Le clair reste le choix initial pour conserver le design existant. Nuit utilise un fond presque noir, des surfaces bleu marine et des accents bleus clairs. Comme le téléphone suit le réglage d’apparence d’Android.

Les textes, cartes, menus, dialogues, boutons, navigation et barres système utilisent des couleurs cohérentes et adaptées à l’apparence. Le logo garde son fond existant pour rester lisible ; l’écran rouge de blocage conserve son rôle. Le changement de thème conserve les points, les limites, les validations et les abonnements de démonstration. Il n’active aucun suivi, compte ou paiement.

Le choix est enregistré localement dans `selkie_appearance`, sans réseau et avec confirmation de l’écriture avant la mise à jour. Il est exclu des sauvegardes Android avec les autres préférences. L’effacement global retire aussi ce choix et revient à Clair.

La palette distingue les accents, leur texte, les fonds de cartes et les fonds de navigation. Les couleurs sont dynamiques : les valeurs utilisées dans les écrans ne doivent pas être capturées une seule fois. Les tests des contrastes vérifient des couples réellement utilisés en clair et en nuit ; ils ne remplacent pas la vérification visuelle sur Android et Samsung.

## Fond géométrique

Les quatre onglets et les pages des réglages ont quatre formes décoratives composées de **144 points** : deux cercles (48 et 32 points), un triangle arrondi (36) et un losange (28). Une onde serpente sur les contours tandis que leurs centres dérivent doucement, jusqu’à 6 dp. Le mouvement suit une boucle de 24 secondes. Les couleurs utilisent la palette courante : opacité principale de 0,10 en clair et 0,18 en nuit ; secondaire de 0,08 en clair et 0,14 en nuit. Les cartes restent opaques ; le fond ne reçoit aucun geste et ne crée aucun élément interactif pour le lecteur d’écran.

L’animation s’arrête hors du cycle RESUMED de l’activité et devient statique si Android désactive les animations. Le réglage système est observé et revérifié à la reprise. Le dessin lit l’état de mouvement dans Canvas, sans recomposer les écrans à chaque image ; les rafraîchissements sont espacés pour cette décoration lente. Aucun choix supplémentaire ni donnée de mouvement n’est enregistré. Les contrôles sur appareil réel restent nécessaires.

La version 0.10.1 utilisait des contours géométriques avec une dérive lente et une petite rotation des polygones. Ses artefacts restent archivés. Le mouvement, son arrêt et sa reprise ont été vérifiés sur émulateur dans la version 0.10.2 ; voir [la validation](../downloads/VALIDATION-0.10.2.md).
