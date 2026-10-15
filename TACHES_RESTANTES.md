# Selkie — ce qui reste avant une diffusion publique

État du 15 octobre 2026, prototype Android 0.16.1 (versionCode 27). Le bilan des 19 points reste 9 en place, 6 partiels, 4 hors périmètre actuel : il ne mesure pas une conformité juridique.

## Terminé dans le prototype

- Retrait du dialogue IA, de la connexion du coach, des options de compilation connectées et du serveur inutilisé ; l’historique Git conserve l’ancienne préparation.
- Coach local français/anglais, question au-dessus de la réponse, bibliothèque classée et paginée : 1 152 / 23 040 / 50 688 questions selon l’offre. Les situations, contextes et intentions sont combinés, pas des réponses médicales ni des textes indépendants.
- Prix proposés 5/10 €/mois, 60/120 €/an sans réduction ; offres de démonstration gratuites, défis personnels et règles à zéro point conservés.
- Interface claire blanc doux, lavande, pêche et menthe ; cartes opaques arrondies et reliefs discrets. Anneau, billes et capsule à effet 3D dessinés dans des zones décoratives, deux lavis légers derrière le texte. Vagues nocturnes conservées en bleu nuit/lilas ; horloge partagée, arrêt selon réglage Android et cycle de vie.
- Lecture gratuite hors ligne : seize sélections FR/EN, dont neuf poésies et fables en vers, filtres et recherche. Provenance et export .txt séparés des recueils externes.
- Politique locale FR/EN actualisée ; export filtré, effacement, consentement facultatif des questions inconnues et documents légaux conservés.
- Calculs des réponses et recherche hors du thread de l’interface, annulation des résultats obsolètes, Tab pour naviguer et Ctrl+Entrée pour envoyer.
- Inventaire de 57 coordonnées de bibliothèques, licences Apache-2.0 consultables. Les droits du logo et de la marque restent un contrôle séparé.

## Fonctions ajoutées en 0.12.0

Le calendrier et le résumé hebdomadaire sont locaux, et les favoris sont gratuits. Premium ajoute les détails hebdomadaires, les créneaux et le widget ; Ultra ajoute les programmes guidés. Les prix de 5 €/mois pour Premium et de 10 €/mois pour Ultra sont conservés. Voir [les fonctions quotidiennes](docs/FONCTIONS_QUOTIDIENNES.md) et [la validation 0.12.0](downloads/VALIDATION-0.12.0.md) pour les contrôles réalisés, les limites et la persistance.

## Décisions laissées en attente par l’éditeur

Le responsable public et le public visé restent indécis. Ne pas inventer d’adresse, d’entreprise, d’accord de parent ou d’immatriculation. Le contact du test reste 1kabdm@gmail.com. La politique publique devra correspondre au responsable et au public réels.

## Dépendances réelles à résoudre

1. Compte Google Play du véritable titulaire, statut commercial et fiscal adapté, vérification d’identité et coordonnées requises. Ces démarches ne sont pas faites par un commit de code.
2. Choix et configuration de l’hébergement public des pages de confidentialité ; vérifier logs, responsable et hébergeur avant publication. Les fichiers legal-site sont prêts mais non hébergés. Aucune bannière cookie ajoutée : aucun script, formulaire, cookie ou traceur non nécessaire dans ces fichiers ; réexaminer si l’hébergeur en ajoute.
3. Faire fonctionner et sécuriser la messagerie de contact, préciser conservation et éventuels transferts ; traiter réellement les demandes de droits selon le registre. Aucun accès à cette boîte n’est fourni.
4. Vérifier droits du phoque, disponibilité du nom/logo, notices spécifiques de distribution et sources juridiques actuelles. L’accès aux sites juridiques officiels demeure limité par la politique réseau du cloud ; la sauvegarde d’un brouillon ne l’a pas modifiée.
5. Si vente choisie : Google Play Billing, produits mensuels/annuels, validation et restauration des achats, traitement des expirations, résiliation, remboursements légaux et CGV adaptées. Les prix affichés ne créent pas un abonnement. Il n’y a pas de paiement Stripe ou Google Pay à brancher dans ce prototype.
6. Distribution : préparer clé d’importation sauvegardée et AAB de production, vérifier cible Android exigée au moment du dépôt (le prototype cible API 34), règles Play, fiche, déclaration de données et tests demandés. Ne pas téléverser l’APK debug comme version commerciale.
7. Essais réels Samsung A16, permissions, surcouche et limites, TalkBack, grandes polices et navigation clavier complète. Les contrôles unitaires et essais d’émulateur consignés ne remplacent pas ces essais.

Aucun achat, hébergement public, publication Play ni démarche au nom d’un parent n’a été effectué. Ces étapes ne peuvent pas être déclarées terminées sans décisions, accès et vérifications correspondants. Les obligations de remboursement légales sont conservées ; une exclusion générale de tout remboursement n’a pas été ajoutée.

## Corrections du calendrier en 0.12.1

Choix des dates passées et futures, tâches gratuites (ajout, modification, case terminé, suppression), export schéma 3 et effacement global. L’historique de suivi conserve sa fenêtre de 90 jours. Aucun nouveau rappel ni accès au calendrier Android.

## Lecture en 0.13.0

Six sélections historiques relues, douze textes FR/EN, sources et notes d’édition, lecture hors ligne et export .txt. Aucun paiement, compte, suivi de lecture ou nouveau droit Android. Les copies et liens externes sont expliqués en confidentialité ; les passages extérieurs ne sont pas déclarés relus.

## Accueil en 0.14.0

Bienvenue en trois étapes, explication des rubriques, accès Android présentés avant activation, guide permanent avec navigation directe et accueil plus clair. Aucun changement des règles, droits Android ou catalogues. Voir docs/ACCUEIL_ET_GUIDE.md.

## Lectures d’Ibn Taymiyya — 0.14.1

Deux courts passages de l’original arabe, versions FR/EN indépendantes, références Al-ʿUbūdiyya p. 44 / Majmūʿ al-fatāwā 10/149. Huit sélections au total. Original et commentaire séparés ; pas de reprise de notes modernes ni de l’ouvrage complet.

## Apparence et accueil en 0.16.0

Les octogones clairs de 0.15.0 sont remplacés par une composition pastel et des volumes ombrés dessinés par le code. Aucun visuel des captures Dribbble n’est embarqué. Le résumé points/suivi et la liste des applications remontent avant les raccourcis et les détails. L’illustration et les textes occupent des zones distinctes ; les animations de l’accueil, de l’introduction et du fond utilisent une seule horloge. Les seize lectures et les catalogues du coach restent identiques. Voir [l’apparence](docs/APPARENCE_CLAIRE.md) et [la validation 0.16.0](downloads/VALIDATION-0.16.0.md) pour les contrôles réalisés et leurs limites.
