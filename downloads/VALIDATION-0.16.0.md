# Selkie 0.16.0 — décor en relief et nouvelle hiérarchie

Source : `820bccf3f4c392da412023f05f3b6d7b34e731b4`. VersionCode 27, minSdk 29, cible API 34. APK : 27214116 octets, SHA-256 `c93a9b62c9c3bf53ff5646ab06f4479fd523f1211a7ba457dd423d1f2fa7f445`. Certificat debug conservé : `9a7487f626169300b57b897c488827428b9517ac8bac8a805f6508fddb932a50`. Le ZIP contient les fichiers suivis du commit source hors downloads ; chacune de ses entrées a été comparée au blob Git correspondant et son CRC vérifié.

## Vérifications finales

Compilation debug, suite JVM et Android Lint : **218 tests sans échec, erreur ou test ignoré**, Lint zéro erreur, 5 avertissements et 4 informations. Les vérifications de palette couvrent les rôles de texte et boutons à 4,5:1, les contours de contrôle à 3:1, les nouveaux fonds pêche/lilas/menthe/sable et leurs accents sémantiques en Clair/Nuit. Le contraste du cumul maximal des deux lavis de fond utilise les constantes réelles de dessin (alphas 24/255 et 14/255). L’illustration plus contrastée est séparée des libellés, jamais placée derrière les textes.

## Changements visuels

Références fournies par l’utilisateur : hiérarchie de tableaux de bord, cartes pastel, profondeur et volumes. Aucun dessin, capture, logo, marque, photo ou texte commercial des références Dribbble n’est redistribué. Le décor est original et procédural : anneau ombré, sphère pêche, billes et capsule menthe, reflets et ombre elliptique. Il produit une impression de volume avec les primitives natives Android ; il n’intègre pas de moteur de rendu 3D externe. Voir [APPARENCE_CLAIRE.md](../docs/APPARENCE_CLAIRE.md) et [DROITS_ET_ACTIFS.md](../docs/DROITS_ET_ACTIFS.md).

Clair : ivoire, lavande, pêche et sauge ; cartes opaques arrondies, bordures et ombres légères, boutons pilules. L’ancien pavage d’octogones est retiré. Deux grands lavis de couleur habillent les marges, les volumes plus colorés restent dans des zones réservées de l’accueil et de l’introduction. Nuit : fond très sombre, surfaces prune/marine et accents bleu-lilas ; le renderer d’eau et ses ondulations restent présents avec la nouvelle couleur d’accent.

Une seule horloge SelkieMotion, partagée par le décor, les volumes et l’introduction : boucle 12 secondes, cadence visée 20 images/s, activité RESUMED et animations système autorisées. Phase lue pendant le dessin, sans recomposition des formulaires à chaque image. Les peintures, dégradés et tracés décoratifs sont réutilisés ; aucune image distante, minuterie supplémentaire ou dépendance ajoutée.

## Accueil

Présentation plus courte avec volume illustré ; bandeau des vrais points et du suivi ; applications remontées avec une seule action Ajouter, puis raccourcis Calendrier/Coach et détail des points. Aucun chiffre de progression n’est inventé. Les accès, limites, règles à zéro point et plafonds de l’offre contrôlent toujours les actions. En écran étroit/grande police, bandeau et en-tête de liste s’empilent ; le texte et les contrôles peuvent agrandir les cartes. Les étapes d’autorisation conservent leurs explications et actions sobres. Le sceau et l’icône du lanceur sont conservés.

## Observations sur Android 14

APK final versionCode 27 installé en mise à jour sur l’émulateur Android 14 (480 × 800 pixels, 320 dp de large), certificat identique à 0.15.0. Captures natives examinées : [accueil Clair](Selkie-0.16.0-accueil-clair.png), [Quotidien/Jour Clair](Selkie-0.16.0-quotidien-clair.png) et [accueil Nuit](Selkie-0.16.0-accueil-nuit.png). Les objets en relief, les couleurs, la lisibilité et la séparation illustration/texte ont aussi fait l’objet d’une relecture indépendante. Les applications commencent près du bas de cet écran étroit ; la liste défile pour atteindre la suite. Sur le vrai Samsung A16, le viewport et les grandes polices restent à vérifier.

Avec animations système activées, deux images de l’accueil montrent 4 457 pixels différents dans la zone dédiée à la sculpture (125 × 140 pixels, hors horloge système et textes). Avec animator_duration_scale=0, les deux captures suivantes ont un contenu d’application strictement identique entre y=38 et680 (horloge et barre système exclues). Cela vérifie le déplacement de la scène et son arrêt dans ce parcours ; le cycle de vie complet, toutes les interactions et les ondulations de Nuit ne sont pas annoncés comme testés graphiquement dans tous les états.

Au premier essai, Android a affiché « System UI isn’t responding » : ces images bloquées ne servent pas de preuve de rendu ou de mouvement. Après Attendre et relance de l’essai, les captures finales affichent bien Selkie sans ce dialogue. Une capture Nuit prise trop tôt ne montrait que le splash ; elle a été écartée au profit d’une capture native valide après le lancement complet. Aucune stabilité universelle de l’émulation n’est affirmée.

À la fin des captures, le fichier original de thème a été restauré et comparé octet pour octet à sa sauvegarde. L’échelle d’animation a retrouvé sa valeur initiale 0 et le délai d’extinction sa valeur initiale 2147483647. Les scripts n’ont pas réécrit les données de suivi/progression. Le thème original était Nuit, confirmé avant la dernière capture sans modification des préférences. Les 150 points visibles sont les données réelles de cet appareil de test, pas une progression inventée par le nouveau décor.

## Conservation et limites

Le coach local conserve 1 152 / 23 040 / 50 688 questions combinées par langue ; les seize lectures et leurs trente-deux fichiers FR/EN restent inchangés. Calendrier, défis, suivi, points, données, export et effacement sont conservés. Prix 5/10 €/mois et 60/120 €/an sans réduction ; offres debug sans paiement réel. Aucun compte, IA, nouveau droit Android, permission Internet ou composant tiers ajouté. Les sources et messagerie sont ouvertes volontairement dans d’autres applications.

Les tests JVM et captures ne valident pas tout le parcours graphique, TalkBack, toutes les grandes polices, le placement du widget ou les fournisseurs d’export. Essais sur Samsung A16 à réaliser physiquement. L’APK debug cible API 34 ; préparation et publication Play restent séparées. Les liens GitHub demandent l’accès au dépôt, sans garantie de téléchargement anonyme.
