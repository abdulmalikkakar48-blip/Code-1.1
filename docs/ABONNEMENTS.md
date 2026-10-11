# Encaisser les abonnements Android

**Le prototype actuel ne prélève aucun argent.** Ses boutons Premium/Ultra sélectionnent gratuitement une offre de démonstration enregistrée sur le téléphone. Aucun abonnement Google Play, Stripe ou serveur de vérification des achats n’est actif. Le client du dialogue IA est préparé dans le code, mais attend la configuration et les essais des services ; voir [son activation](ACTIVER_COACH_IA.md).

Le nom de travail est Selkie ; sa disponibilité commerciale doit encore être vérifiée. Le guide [Publication Google Play](PUBLICATION_GOOGLE_PLAY.md) décrit le compte et le bundle nécessaires avant une distribution commerciale.

## Prix et fonctions prévus

| Offre | Prix prévu | Applications suivies | Fonctions prévues dans l’offre |
| --- | --- | --- | --- |
| Gratuit | 0 € | 10 | Limites locales et défis proposés à tous |
| Premium | 10 € par mois | 50 | Fonctions gratuites et défis personnels à trois étapes |
| Ultra | 20 € par mois | Toutes les applications compatibles, sans plafond commercial | Fonctions Premium, recherche et tri des applications |

Les plafonds ont été recalibrés en version 0.6.0 ; voir [la justification](CALIBRAGE_OFFRES.md). La recherche et le tri fonctionnent déjà dans la démonstration Ultra. Les défis personnels demandent trois étapes par journée, une durée choisie de 2 à 365 jours et donnent 0,25 point une seule fois à la fin. Le coach IA illimité reste un projet futur : ne le vends pas comme une fonction disponible tant qu’il n’est pas construit, testé et expliqué dans la politique de confidentialité.

À 0 point, ajouter une application ou modifier sa limite reste bloqué dans **toutes** les offres. Réinitialiser une limite exige réellement 10 points. Un abonnement ne doit pas contourner ces règles.

## Google Play Billing ou Stripe ?

Pour des fonctions numériques consommées dans une application distribuée par Google Play, la règle générale connue impose **Google Play Billing**. Les offres Premium/Ultra sont des fonctions numériques. Google Play Billing est donc le parcours à préparer pour le lancement habituel sur Google Play.

**Ajouter un bouton Stripe ou un lien de paiement dans l’application n’est pas automatiquement autorisé.** Certains pays et programmes, notamment dans l’Espace économique européen, permettent des systèmes de facturation ou offres alternatives sous conditions. Il faut vérifier l’éligibilité de l’application, s’inscrire au programme approprié et appliquer ses obligations de présentation, d’intégration, de déclaration et de service client. Un paiement Stripe sur un site ne dispense pas automatiquement l’application de ces règles. Des frais Google peuvent rester dus, en plus des frais Stripe.

La commission standard connue pour les **abonnements à renouvellement automatique** est de **15 %**. Les programmes alternatifs ont des règles et frais propres : ne suppose pas une commission nulle ou une réduction donnée sans lire les conditions applicables à ton compte, aux pays et au programme. Le prix payé par le client n’est pas le montant net reçu : taxes, frais, remboursements et ajustements interviennent aussi.

**Actualité des sources :** au 10 octobre 2026, Google Support et Android Developers sont inaccessibles depuis cet environnement avec un refus proxy CONNECT HTTP 403, même avec TLS actif. Les règles et frais ci-dessus sont des indications connues, pas une vérification de leur version actuelle. Avant de choisir un paiement alternatif ou un prix commercial, consulte : [Politique Google Play relative aux paiements](https://support.google.com/googleplay/android-developer/answer/9858738?hl=fr), [Frais de service](https://support.google.com/googleplay/android-developer/answer/112622?hl=fr), [Programme de facturation au choix de l’utilisateur](https://support.google.com/googleplay/android-developer/answer/12570971?hl=fr).

## 1. Préparer la réception de l’argent

Dans Play Console, suis la procédure pour créer ou associer ton **profil de paiement marchand**. Fournis les informations légales, bancaires et fiscales demandées et termine leurs vérifications. Google verse les recettes selon les conditions de ton profil et des pays concernés ; ce guide ne garantit pas un montant net ou une date de virement.

Tu fais toi-même cette configuration dans ton compte. Aucun numéro de carte, mot de passe ou accès bancaire ne doit être placé dans le dépôt de code.

## 2. Créer les deux abonnements dans Play Console

Après création de l’application et préparation d’une version intégrant Billing, ouvre la partie de la console consacrée aux abonnements. Les libellés et conditions d’activation doivent être vérifiés dans la console actuelle.

Pour chaque offre :

1. Crée un produit d’abonnement avec un identifiant stable, par exemple `premium` et `ultra`. Ce sont des propositions d’identifiants : aucun produit n’a été créé dans ton compte.
2. Ajoute un **forfait de base**, par exemple `mensuel`, à renouvellement automatique avec une période d’un mois.
3. Choisis les pays disponibles et configure les prix : **10 € pour Premium, 20 € pour Ultra** dans le marché en euros visé. Examine les prix locaux et le traitement des taxes présentés par Google. Évite un essai ou une remise pour la première version si tu n’en as pas besoin.
4. Renseigne des noms et descriptions fidèles aux fonctions disponibles, puis active les forfaits selon le parcours proposé.
5. Associe chaque identifiant au bon niveau de droits dans le code et dans le serveur. L’identifiant et le forfait doivent correspondre exactement aux données renvoyées par Google.

Référence : [Créer et gérer des abonnements](https://support.google.com/googleplay/android-developer/answer/140504?hl=fr). L’application doit afficher le **prix localisé renvoyé par Google Play**, ainsi que la période, le renouvellement et la façon d’annuler, et non un prix de démonstration codé en dur.

## 3. Développer les vrais achats

Cette partie demande un développement Android et un serveur ; elle ne se réalise pas seulement en créant les produits dans la console.

1. Intégrer une version de la bibliothèque **Google Play Billing** autorisée à la date de publication. Vérifier [les échéances des versions Billing](https://developer.android.com/google/play/billing/deprecation-faq), sans figer ici une version supposée actuelle.
2. Se connecter au service Billing, demander les informations des produits/forfaits et ouvrir la fenêtre d’achat Google Play avec le forfait choisi.
3. Traiter tous les résultats : achat réussi, annulation, erreur, achat déjà détenu et paiement en attente. **Un achat en attente ne donne aucun droit payant avant sa confirmation.**
4. Envoyer le jeton d’achat au serveur de l’application. Le serveur vérifie l’achat avec l’API Google Play Developer, contrôle l’application, le produit, l’état et la date d’expiration, puis enregistre les droits. Une préférence locale `plan = ULTRA` ou un simple retour de fenêtre d’achat ne prouve pas un abonnement.
5. Confirmer la prise en compte de l’achat avec l’**acknowledgement** Google Play après vérification. Le délai connu est de trois jours après l’état acheté ; sans confirmation, l’achat peut être remboursé et révoqué. Les renouvellements ne sont pas à confirmer comme un nouvel achat initial. Réexaminer les exigences actuelles dans la documentation.
6. Conserver de façon durable l’état de vérification et rendre le traitement répétable sans attribuer deux fois les mêmes droits. Les secrets et identifiants du compte de service Google restent **sur le serveur**, jamais dans l’APK.

Références : [Intégrer Billing](https://developer.android.com/google/play/billing/integrate), [Sécurité et vérification des achats](https://developer.android.com/google/play/billing/security), [Cycle de vie des abonnements](https://developer.android.com/google/play/billing/lifecycle/subscriptions).

L’application fonctionne aujourd’hui sans inscription. Il faut définir comment retrouver les droits de l’utilisateur sur une réinstallation ou un autre téléphone, sans inventer une identité Google accessible au code. Un compte propre à l’application n’est pas automatiquement obligatoire pour acheter avec Google Play, mais le rattachement des droits et leur restauration doivent être conçus et testés.

## 4. Restaurer et maintenir les droits

Au lancement et au retour dans l’application, rechercher les achats connus par Google Play (`queryPurchasesAsync`) et demander au serveur de revérifier les droits. Fournir aussi une action compréhensible comme « Restaurer mes achats ». Après réinstallation, utiliser le compte Google Play qui détient l’abonnement.

Le serveur doit suivre les changements d’abonnement, par exemple avec les notifications développeur en temps réel, puis revérifier auprès de l’API Google. Une notification reçue seule ne suffit pas à attribuer des droits.

| Situation vérifiée | Comportement attendu |
| --- | --- |
| Paiement en attente | Ne pas activer Premium/Ultra avant confirmation |
| Abonnement actif | Donner les droits de l’offre achetée jusqu’à la date confirmée |
| Renouvellement annulé par le client | Conserver les droits jusqu’à la fin de la période déjà payée |
| Délai de grâce actif | Suivre les droits autorisés par Google pour cette période |
| Abonnement expiré, révoqué ou en suspension pour impayé | Retirer les droits payants selon l’état vérifié ; ne pas se fier à un ancien cache local |
| Changement Premium ↔ Ultra | Utiliser le parcours de remplacement d’abonnement Google Play et son effet immédiat ou différé ; éviter deux abonnements simultanés involontaires |

Une baisse d’offre **ne supprime ni les applications suivies, ni les points, ni les défis personnels**. Elle bloque les nouveaux ajouts au-delà du nouveau plafond et suspend la création/validation des défis personnels avec Gratuit. Les données restent disponibles pour une reprise ou une suppression choisie par l’utilisateur. Le choix de démonstration des anciennes versions ne doit ni créer un abonnement ni déclencher un prélèvement.

Donner accès à la gestion et à l’annulation : [Abonnements du compte Google Play](https://play.google.com/store/account/subscriptions). Annuler un renouvellement n’est pas la même chose que rembourser une période déjà payée.

## 5. Tester avant de facturer de vrais clients

Utilise les **testeurs de licence** configurés dans Play Console et une version distribuée sur une piste de test. L’inscription au test fermé et l’ajout comme testeur de licence sont deux configurations distinctes. Sur le téléphone, vérifie le compte Google connecté et la présence des moyens de paiement de test Google : ne remplace pas un test manquant par un vrai paiement.

Référence : [Tester l’intégration Billing](https://developer.android.com/google/play/billing/test).

Vérifie au minimum :

- Premium et Ultra donnent le bon plafond, avec le prix et la période exacts ;
- l’annulation de la fenêtre d’achat et un paiement refusé ne donnent aucun droit ;
- un achat en attente n’active rien avant confirmation ;
- achat confirmé, acknowledgement, renouvellement, annulation et expiration suivent les états du serveur ;
- fermer l’application, perdre le réseau, redémarrer ou recevoir deux fois un événement ne double pas le traitement ;
- restaurer après réinstallation et changer d’offre donnent les bons droits, sans achat double ;
- à 0 point les restrictions restent actives, un reset ne passe pas à 9,75 points, et une baisse d’offre conserve les données ;
- la version commerciale ne permet plus d’obtenir des droits payants en cliquant sur le choix gratuit de démonstration.

Avant d’activer la vente, mettre à jour la confidentialité et la sécurité des données pour les jetons d’achat, droits, identifiants, serveur et fournisseurs réellement utilisés. Le futur coach IA doit être développé et déclaré séparément avant d’être vendu comme avantage disponible.

## Prochaine étape concrète

Créer le compte Play Console, confirmer ses exigences actuelles, préparer le bundle signé et les tests, puis développer Billing et sa vérification serveur. Les prix et fonctions sont déjà définis ; l’encaissement n’est pas encore branché. Pour une première publication Google Play, partir sur Google Play Billing évite de supposer une éligibilité à un programme de paiement alternatif non vérifiée.
