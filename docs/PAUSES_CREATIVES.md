# Dé Selkie, calendrier et coloriage — 0.19.0

Ces deux fonctions sont gratuites dans toutes les offres. Les trois modes de pause du dé et le coloriage ne changent pas les points. Un quatrième mode facultatif, Points, retire des points comme expliqué avant le lancement ; aucun achat, rappel automatique ou verrouillage supplémentaire n’est ajouté.

Les points d’honneur sont plafonnés à 100. Un ancien solde supérieur à 100 est ramené à 100 ; les soldes inférieurs ne sont pas augmentés. Les récompenses habituelles continuent de fonctionner dans cette limite.

## Dé Selkie

Dans Quotidien → Dé, choisis un mode :

- Activité : six idées hors défilement, comme lire cinq pages, marcher dix minutes ou ranger un petit coin. La confirmation crée une tâche aujourd’hui.
- Application : sélectionne de une à six applications déjà suivies. Le tirage choisit chacune avec la même probabilité et propose vingt minutes sans elle aujourd’hui. Le numéro du dé est décoratif dans ce mode.
- Semaine : choisis une application. Les faces 1–2 proposent un jour, 3–4 deux jours, 5–6 trois jours. Choisis exactement ce nombre de dates parmi les sept prochains jours, aujourd’hui compris, puis confirme.
- Points : le lancement volontaire retire autant de points d’honneur que la face obtenue, de 1 à 6. Le retrait est limité au solde disponible : avec 0,25 point restant, il retire 0,25 point, jamais plus. Aucun nouveau lancer de ce mode n’est proposé à zéro point. Le coût est annoncé avant de lancer ; ce mode n’ajoute aucune tâche au calendrier.

Dans Activité, Application et Semaine, une confirmation enregistre les tâches en une seule opération ; un calendrier plein refuse tout le groupe. Tu coches les tâches toi-même dans Calendrier. Un résultat non confirmé de ces trois modes n’est pas conservé. Les limites et verrous habituels restent actifs ; ces tirages ne les changent pas et ne vérifient pas automatiquement les pauses.

Le mode Points ne rapporte aucun gain, prix, argent ou crédit payant. Les points ne peuvent pas être achetés. Chaque lancement a un identifiant unique : réessayer le même lancement après une erreur ou une interruption ne retire pas les points deux fois. L’éventuelle récompense de 24 heures arrivée à échéance est appliquée avant le retrait. Seul le dernier reçu du mode Points reste sur le téléphone : identifiant du lancement, face obtenue, points retirés et solde après le retrait. Il permet de retrouver le résultat après une interruption. Le lancer suivant le remplace ; l’effacement global ou la désinstallation le supprime. Il n’y a pas de journal de tous les lancers.

Le résultat et l’action de confirmation sont regroupés près du dé. Les activités proposées sont présentées dans une liste distincte, pour distinguer les possibilités du résultat réellement tiré. Le calendrier affiche le mois sur sept colonnes dans la largeur de l’écran ; la journée sélectionnée et ses tâches restent séparées du relevé d’usage observé. La présentation ne change pas les règles de validation ni les données conservées.

## Coloriage

Dans Quotidien → Coloriage, une galerie de miniatures permet de choisir parmi douze pages originales :

- Quatre dessins à carreaux : Arbre, Fleur, Livre ouvert et Paysage.
- Les huit motifs existants : Mosaïque, Damier, Vagues, Rosace, Losanges, Montagne, Soleil et Quadrillage libre.

Chaque page contient 12 × 12 cases indépendantes. Choisis une couleur ou la gomme dans la palette au-dessus de la grille, puis touche ou glisse sur la page. Les contours guident le coloriage ; chaque case peut être remplie séparément. Les dessins sont des illustrations à carreaux, pas des photos ou du dessin libre. Les nouvelles pages s’ajoutent aux précédentes sans remplacer les couleurs déjà enregistrées.

Pour éviter de viser une petite case, choisis sa ligne et sa colonne avec les commandes de 48 dp, puis Appliquer. La palette propose dix couleurs. Annuler et Rétablir conservent les cinquante derniers gestes pendant l’ouverture de la page. Chaque geste terminé s’enregistre sur le téléphone ; aucun enregistrement à l’ouverture. Une erreur propose de réessayer. Un changement de motif est bloqué pendant l’enregistrement ou après un échec non résolu.

Effacer cette page demande confirmation, sans toucher aux autres pages. Les cellules non blanches sont conservées jusqu’à cet effacement, l’effacement global ou la désinstallation. Aucun fichier image, photo, compte ou synchronisation n’est créé. Les choix de couleurs sont privés dans `maree/coloringPages_v1`, bornés à douze identifiants fixes et 144 indices par page.

L’export volontaire JSON utilise le schéma 5 : `storedData.coloringPages` contient uniquement les identifiants de pages et indices de couleurs validés. Les objectifs des trois modes de pause sont des `calendarTasks` ordinaires. `storedData.points.maximumValue` indique 100 ; `storedData.honorDice` contient le dernier reçu valide du mode Points, ou `null` s’il n’y en a pas. Les anciens objets de stockage ne peuvent pas réenregistrer des données après l’effacement global. Les copies de documents déjà exportées se suppriment séparément.

## Périmètre

Le dessin libre à main levée et l’export d’un coloriage en image ne sont pas inclus. Aucun plugin supplémentaire n’est nécessaire : Compose et Canvas Android dessinent les motifs. Aucun SDK, service réseau ou droit Android n’a été ajouté. Les permissions du suivi ne sont pas requises pour le mode Activité et le coloriage.
