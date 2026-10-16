# Dé Selkie et coloriage — 0.17.0

Ces deux fonctions sont gratuites dans toutes les offres. Elles proposent une pause choisie ; aucun point, achat, rappel automatique ou verrouillage supplémentaire.

## Dé Selkie

Dans Quotidien → Dé, choisis un mode :

- Activité : six idées hors défilement, comme lire cinq pages, marcher dix minutes ou ranger un petit coin. La confirmation crée une tâche aujourd’hui.
- Application : sélectionne de une à six applications déjà suivies. Le tirage choisit chacune avec la même probabilité et propose vingt minutes sans elle aujourd’hui. Le numéro du dé est décoratif dans ce mode.
- Semaine : choisis une application. Les faces 1–2 proposent un jour, 3–4 deux jours, 5–6 trois jours. Choisis exactement ce nombre de dates parmi les sept prochains jours, aujourd’hui compris, puis confirme.

Une confirmation enregistre les tâches en une seule opération ; un calendrier plein refuse tout le groupe. Tu coches les tâches toi-même dans Calendrier. Un résultat non confirmé n’est pas conservé. Les limites et verrous habituels restent actifs ; le tirage ne les change pas et ne vérifie pas automatiquement les pauses. Aucun mécanisme de pari ni gain aléatoire.

## Coloriage

Dans Quotidien → Coloriage, huit pages originales sont disponibles : Mosaïque, Damier, Vagues, Rosace, Losanges, Montagne, Soleil et Quadrillage libre. Chaque grille contient 12 × 12 cases indépendantes. Choisis une couleur ou la gomme, puis touche ou glisse sur la page ; les gros contours dessinent le motif, chaque case peut être remplie séparément.

Pour éviter de viser une petite case, choisis sa ligne et sa colonne avec les commandes de 48 dp, puis Appliquer. La palette propose dix couleurs. Annuler et Rétablir conservent les cinquante derniers gestes pendant l’ouverture de la page. Chaque geste terminé s’enregistre sur le téléphone ; aucun enregistrement à l’ouverture. Une erreur propose de réessayer. Un changement de motif est bloqué pendant l’enregistrement ou après un échec non résolu.

Effacer cette page demande confirmation, sans toucher aux autres pages. Les cellules non blanches sont conservées jusqu’à cet effacement, l’effacement global ou la désinstallation. Aucune image, photo, compte ou synchronisation n’est créée. Les choix de couleurs sont privés dans `maree/coloringPages_v1`, bornés à huit identifiants fixes et 144 indices par page.

L’export volontaire JSON passe au schéma 4 : `storedData.coloringPages` contient uniquement les identifiants de pages et indices de couleurs validés. Les objectifs du dé sont des `calendarTasks` ordinaires. Les anciens objets de stockage ne peuvent pas réenregistrer des données après l’effacement global. Les copies de documents déjà exportées se suppriment séparément.

## Périmètre

Le dessin libre à main levée et l’export d’un coloriage en image ne sont pas inclus. Aucun plugin supplémentaire n’est nécessaire : Compose et Canvas Android dessinent les motifs. Aucun SDK, service réseau ou droit Android n’a été ajouté. Les permissions du suivi ne sont pas requises pour le mode Activité et le coloriage.
