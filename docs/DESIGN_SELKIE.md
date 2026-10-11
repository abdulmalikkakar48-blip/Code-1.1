# Design Android de Selkie 0.8.0

Selkie est le nom choisi par l’éditeur. L’identité du paquet `com.maree.app` et les clés de stockage restent stables pour conserver les données lors des mises à jour. La disponibilité du nom comme application et comme marque doit être vérifiée avant la commercialisation.

## Système commun

- Fond blanc cassé `#F6F7F3`, cartes blanches, texte foncé `#1D352C`, texte secondaire `#5E7065`.
- Actions vert profond `#245A43`, carte de points `#183D2D`, menthe `#D6ECD9`, surfaces légères `#EEF4EE`. Le rouge est réservé aux limites atteintes et aux suppressions.
- Marges de 20 dp, cartes arrondies à 20 dp, boutons à 14 dp et au moins 50 dp pour l’action principale. Typographie commune, titres 28 sp avec interligne 34 sp, corps 14–16 sp.
- `UiComponents.kt` définit les titres, les cartes bordées, les en-têtes de section, les badges et les boutons. Pas de gradients décoratifs ni de couleur distincte par onglet.
- Navigation fixe : Accueil, Quotidien, Défis, Coach, Plus. Le logo est un phoque noir stylisé sur fond menthe. La selkie et son passage entre mer et terre évoquent l’équilibre entre vie réelle et écrans.

## Écrans

Accueil sépare le capital de discipline et les applications suivies. La liste utilise `LazyColumn` avec clés stables pour chaque application ; les grandes listes Ultra ne composent pas tous les éléments d’emblée. « Modifier la limite » est visible ; réinitialisation et retrait sont accessibles dans le menu de l’application et conservent leurs confirmations. Une réinitialisation nécessite 10 points et un ajout ou changement reste interdit à 0 point.

Quotidien commence par une idée de pause. Son bilan additionne les compteurs suivis ; l’explication précise qu’il ne s’agit pas du temps d’écran total et que les compteurs peuvent être remis à zéro. Les mesures ne sont pas affichées lorsque le suivi est en pause. Une liste extensible donne le détail des applications.

Défis conserve les dates distinctes et les récompenses. Chaque étape personnelle est sur sa propre ligne, numérotée 1 à 3. Les journées sont ordonnées en lignes ; les longs défis se déplient sans nombres dispersés en colonnes. Les défis proposés ont un calendrier repliable, avec le premier défi non terminé ouvert au départ. Les explications secondaires peuvent se développer ; le délai de remise à zéro de la série reste visible.

Coach est un onglet direct. Trois situations ouvrent des conseils fixes, avec trois gestes ordonnés. Les conseils ne sont pas présentés comme une réponse générée par une IA. Aucun message ou secret n’est envoyé ; le dialogue IA est indiqué comme non disponible.

Plus rassemble le suivi, les alertes facultatives, la confidentialité et les offres. Les trois offres montrent leurs avantages sur des lignes comparables. Les prix Premium/Ultra sont prévus et aucun paiement n’est actif. Le choix d’offre gratuit est réservé aux builds DEBUG.

Le premier parcours présente les trois accès Android avec leur fonction et leur état. Les deux premiers conditionnent le suivi, les notifications restent facultatives et l’exploration sans surveillance est conservée. La confidentialité affiche un résumé, le responsable et le contact, puis permet d’ouvrir tout le texte hors ligne. L’effacement demande toujours une confirmation.

## Outils demandés

L’éditeur a fourni https://chatgpt.com/plugins/lovable, https://chatgpt.com/plugins/figma et https://chatgpt.com/plugins/magicpath. Aucun outil ou skill de ces trois services n’est accessible dans cette session. Ouvrir leurs fiches ne donne pas une connexion à la tâche. La refonte est donc implémentée directement en Kotlin/Compose ; elle ne contient pas de maquette présentée comme issue de ces services.

Un accès Figma connecté permettrait de consulter une maquette, ses composants et les spécifications transmises. Une maquette web issue d’un autre outil demanderait une adaptation aux permissions, au suivi local et aux contraintes Android de cette application native.

## Vérification

Compiler, exécuter les tests des règles existantes et Android Lint. Examiner aussi la navigation, les vues avec ou sans suivi, les étapes/journées, l’accès aux réglages, les états Premium/Ultra, le retour depuis la confidentialité et les grandes polices sur un appareil Android. Les captures et limites effectivement observées sont consignées dans `downloads/VALIDATION.md` ; une compilation seule ne valide pas le rendu.
