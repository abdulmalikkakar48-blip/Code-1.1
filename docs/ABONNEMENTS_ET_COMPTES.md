# Offres et comptes Selkie — version de test 0.12.0

Version 0.12.0, `versionCode` 21 — 13 octobre 2026. Les tarifs sans réduction fixés en 0.9.3 sont conservés. Les offres sont accessibles dans **le menu ☰ en haut → Abonnements**. En **DEBUG**, Premium et Ultra sont des **démonstrations gratuites** ; choisir une offre modifie uniquement le plan local, sans achat ni droit serveur. En **release**, les offres payantes sont indiquées « Bientôt disponible » et leur activation est désactivée. Aucun de ces APK ne crée, ne facture, ne renouvelle ou ne résilie un abonnement. Les choix mensuel et annuel servent à présenter les tarifs prévus ; ils ne déclenchent pas un achat.

| Offre | Mensuel prévu | Annuel prévu, payé en une fois | Applications compatibles |
| --- | --- | --- | --- |
| Gratuit | 0 € | 0 € | 10 |
| Premium | 5 € | 60 € | 50 |
| Ultra | 10 € | 120 € | Sans plafond commercial |

L’année coûte douze mensualités, sans réduction. Les fonctions d’une offre sont les mêmes pour les deux durées. Les restrictions à zéro point restent identiques pour tous. Les prix doivent être définis et confirmés dans Google Play avant activation ; l’écran de confirmation Google Play sera la référence au moment de l’achat.

## Fonctions locales par offre

**Gratuit** comprend les fonctions de base : jusqu’à 10 applications compatibles suivies, les défis quotidiens, le coach et l’aide hors ligne, ainsi que le calendrier, le bilan hebdomadaire général et jusqu’à 50 conseils favoris. La bibliothèque de questions-réponses préparées dépend de l’offre ; ce nombre décrit les contenus disponibles, pas un quota de messages.

Le calendrier présente **90 jours glissants** d’usage réellement observé pour les seules applications sélectionnées et de validations enregistrées. L’observation commence avec l’utilisation de cette fonction : aucun passé n’est reconstitué ou inventé. Les périodes de pause ou d’observation interrompue ne sont pas comptées. Une journée sans observation est inconnue ; une journée partiellement observée reste partielle. Un intervalle effectivement observé peut afficher zéro usage lorsqu’aucune application n’est sélectionnée, sans transformer les périodes inconnues en périodes à zéro. Le bilan hebdomadaire présente le nombre de jours observés, la somme de leur usage et la couverture des observations. Toute moyenne dérivée de ces données doit porter uniquement sur les jours observés ; l’écran ne présente pas actuellement de moyenne. Les badges décrivent les dépassements observés : une absence de dépassement observé ne constitue pas une « journée parfaite ». Le calendrier est interne à Selkie et ne demande aucune permission d’accès au calendrier du téléphone.

Les favoris sont limités à **50 conseils rédigés pour Selkie ou réponses de la bibliothèque préparée**. Ils ne conservent pas les saisies libres, les questions non reconnues ou les échanges sensibles. Chaque favori garde sa langue d’origine et l’offre minimale du contenu. Un retour à une offre inférieure conserve le favori mais verrouille tout contenu qui demande une offre supérieure ; un favori n’accorde aucun accès payant.

**Premium** comprend toutes les fonctions gratuites, jusqu’à 50 applications compatibles, une bibliothèque préparée plus étendue, les défis personnalisés, le **bilan hebdomadaire détaillé par application**, jusqu’à **20 règles de créneaux horaires** et le **widget d’accueil**. Un créneau applique sa limite au compteur actuel de l’application : il ne redémarre pas le compteur et ne met pas fin à un verrou de 24 heures déjà engagé. Les restrictions à zéro point restent applicables. Après un retour à Gratuit, les créneaux conservés sont inactifs ; le verrou déjà engagé continue jusqu’à son terme. Pour installer le widget : **appui long sur l’écran d’accueil → Widgets → Selkie**. Il affiche les points et un défi suggéré ; ces informations peuvent être visibles à toute personne qui voit l’écran d’accueil.

**Ultra** comprend toutes les fonctions Premium, les applications compatibles sans plafond commercial, une bibliothèque préparée plus étendue, la recherche et le tri du suivi, ainsi que trois **programmes guidés** : **Sommeil, 7 jours ; Études, 14 jours ; Réseaux sociaux, 30 jours**. L’utilisateur choisit son programme et valide manuellement sa progression. Au maximum **une validation par jour, tous programmes confondus**, est possible ; aucun point supplémentaire n’est attribué. Les jours non validés ne sont pas complétés automatiquement et ces conseils ne constituent pas un suivi médical. Après un retour à une offre inférieure, la progression reste conservée mais l’accès aux programmes est verrouillé.

## Stockage, effacement et export

L’historique, les créneaux, les favoris et la progression des programmes restent dans le même stockage privé local **`maree`** que les réglages et la progression existants. La remise à zéro des données Selkie les efface également. Les entrées hors de la fenêtre de 90 jours sont nettoyées durablement au prochain usage ou à la prochaine observation ; le nettoyage initial peut être asynchrone. Aucune minuterie de suppression n’est garantie lorsque l’application ne fonctionne pas. Les bornes techniques de l’historique, détaillées dans le [registre des traitements](REGISTRE_TRAITEMENTS.md), concernent ses enregistrements et ne créent pas de plafond commercial d’applications suivies pour Ultra.

L’export volontaire contient uniquement des champs structurés validés : réglages et progression, historique observé, règles horaires, favoris autorisés, identifiants et dates des programmes. Il exclut le dernier échantillon technique de mesure, les saisies brutes du coach, les questions inconnues conservées, les jetons et les e-mails. Cet échantillon sert uniquement à relier deux observations consécutives espacées d’au plus 30 secondes ; il est écarté à la pause, remplacé à la mesure suivante ou supprimé au nettoyage. La copie exportée reste sous la responsabilité de l’utilisateur et doit être supprimée séparément ; le fournisseur de fichiers choisi peut la synchroniser.

La conservation facultative des questions inconnues reste distincte des favoris : **désactivée par défaut**, elle peut retenir au maximum 50 phrases distinctes de 800 caractères avec l’accord de l’utilisateur, et cesse avec suppression à la désactivation. Ses limites et son filtre sensible imparfait restent décrits dans la politique de confidentialité. Ces nouvelles fonctions n’ajoutent ni permission Internet, ni IA distante, ni compte Selkie, ni paiement réel, ni cookies ou traceurs. Le widget lit les données locales ; les liens externes sont ouverts volontairement dans une autre application.

## Renouveler et arrêter

Le modèle prévu est un renouvellement automatique chaque mois pour le mensuel, chaque année pour l’annuel, sauf annulation. La durée, le montant et la prochaine échéance doivent être présentés avant l’achat.

Pour un achat réellement effectué via Google Play : ouvrir **Google Play → profil → Paiements et abonnements → Abonnements → Selkie → Annuler l’abonnement**. Vérifier le compte Google utilisé pour l’achat si l’abonnement n’apparaît pas. Le lien « Gérer mes abonnements Google Play » ouvre la page de gestion, pas un paiement Selkie. Après une résiliation ordinaire, l’accès reste normalement disponible jusqu’à la fin de la période payée ; la page Google Play indique l’échéance exacte. Désinstaller Selkie ou effacer ses données ne résilie pas un abonnement. Une annulation ne constitue pas automatiquement une demande de remboursement.

## Remboursement et informations légales

**Menu ☰ → Confidentialité et données → Abonnements et résiliation** explique les projets d’offres, l’annulation et les demandes de remboursement ; les conditions d’utilisation et les mentions de l’éditeur sont disponibles sur la même page. Une démonstration gratuite ne constitue pas un achat à rembourser.

**Pas de remboursement automatique pour une période déjà utilisée.** Une simple annulation ne déclenche pas de remboursement automatique d’un mois ou d’une période déjà écoulée. Aucun geste commercial ni remboursement discrétionnaire n’est promis. Les droits obligatoires restent applicables, notamment la rétractation lorsqu’elle s’applique, les garanties légales en cas de non-conformité et la restitution de sommes indûment prélevées ; les règles applicables du magasin doivent être respectées.

Pour un futur achat Google Play réel, retrouver la commande dans l’historique du compte utilisé et suivre l’option de demande de remboursement lorsqu’elle est proposée, ou écrire à **1kabdm@gmail.com** avec le numéro de commande, la date et le problème. Ne pas envoyer de RIB, de carte bancaire ou de mot de passe. L’examen dépend du contrat et du droit applicable ; un remboursement n’est pas garanti par cette page.

En France, la rétractation à distance est en principe de 14 jours, sous conditions et exceptions. Elle ne doit pas être considérée comme automatiquement perdue à l’ouverture de l’application. Les [conditions de test FR/EN](CONDITIONS_UTILISATION.md) détaillent les distinctions entre service et contenu numérique. Les futures CGV devront être adaptées au vrai vendeur et au vrai contrat avant toute vente ; les textes légaux actuels n’ont pas pu être vérifiés en ligne en 2026.

## Un compte est utile, mais distinct du paiement

Google Play peut gérer un abonnement sans inscription à un compte propre à Selkie. Un compte Selkie devient utile pour associer les droits vérifiés et les retrouver lors d’un changement d’appareil. Il ajoute aussi une responsabilité : connexion sécurisée, récupération, suppression du compte et information sur les données du serveur.

Choix demandé : **un seul téléphone connecté à la fois**, avec changement d’appareil possible. Une nouvelle connexion remplacerait la précédente. Cette règle ne doit pas condamner un utilisateur qui perd ou remplace son téléphone.

Cette version ne propose pas encore ce compte : aucun serveur de comptes Selkie n’est déployé ou configuré. Le coach est entièrement local. Les boutons de démonstration ne créent aucun droit serveur.

## Ce qu’il faut réellement activer

1. Créer et configurer le service d’authentification, par exemple un projet Supabase, sous la responsabilité du véritable éditeur. Configurer la délivrance des codes e-mail et la récupération du compte.
2. Faire vérifier les achats et leur expiration par un serveur avec l’API Google Play ; associer chaque achat à un compte sans permettre son attribution simultanée à plusieurs comptes. Traiter les renouvellements, annulations et remboursements. Un choix Premium local n’est jamais une preuve de paiement.
3. Conserver côté serveur une seule session d’appareil active par compte, avec un identifiant aléatoire d’installation plutôt qu’un identifiant matériel. La nouvelle connexion doit remplacer la session précédente dans une opération atomique. Vérifier cette session à chaque requête protégée, y compris après un renouvellement du jeton : masquer seulement un bouton dans l’APK ne suffit pas.
4. Définir ce qui reste disponible sans Internet. Le suivi et les conseils gratuits peuvent rester locaux. Une ancienne session hors ligne ne peut pas recevoir instantanément sa révocation ; il faut décider et documenter la durée éventuelle d’accès payant en cache avant d’annoncer une restriction effective.
5. Ajouter dans l’application la connexion, la déconnexion, le changement d’appareil, la restauration d’achat et la suppression du compte. La suppression d’un compte ne doit pas être présentée comme la résiliation Google Play.
6. Adapter la politique aux services réellement déployés : responsable, e-mail dédié, données de compte et d’achat, destinataires, région, durées et suppression. Tester deux téléphones, une reconnexion, une perte d’appareil et un achat de test avant vente.

Le responsable actuellement déclaré pour le test reste **Abdul Malik Kakar**, avec **1kabdm@gmail.com** comme contact. L’éditeur de la future diffusion publique et son public cible n’ont pas encore été choisis. Confirmer l’identité, la capacité à contracter et les mentions applicables du véritable vendeur avant diffusion ou vente ; aucune identité de parent ou d’entreprise n’est présumée. Le [bilan de lancement](BILAN_CONFORMITE_FRANCE.md) et les [vérifications opérateur](CONFIDENTIALITE_AVANT_PUBLICATION.md) précisent les points restants. Les sources officielles n’ont pas pu être consultées depuis le cloud au 13 octobre 2026 ; cette documentation ne garantit ni leur actualité ni la conformité d’une future vente. Aucun compte vendeur, paiement, site public ou publication Play n’est créé par cette documentation.
