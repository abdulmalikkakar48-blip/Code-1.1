# Offres et comptes Selkie — décisions de la version 0.9.3

Les offres sont des **démonstrations gratuites** dans cet APK. Il ne crée, ne facture, ne renouvelle et ne résilie aucun abonnement. Les choix mensuel et annuel servent à présenter les tarifs prévus ; ils ne déclenchent pas un achat.

| Offre | Mensuel prévu | Annuel prévu, payé en une fois | Applications compatibles |
| --- | --- | --- | --- |
| Gratuit | 0 € | 0 € | 10 |
| Premium | 10 € | 120 € | 50 |
| Ultra | 20 € | 240 € | Sans plafond commercial |

L’année coûte douze mensualités, sans réduction. Les fonctions d’une offre sont les mêmes pour les deux durées. Les restrictions à zéro point restent identiques pour tous. Les prix doivent être définis et confirmés dans Google Play avant activation ; l’écran de confirmation Google Play sera la référence au moment de l’achat.

## Renouveler et arrêter

Le modèle prévu est un renouvellement automatique chaque mois pour le mensuel, chaque année pour l’annuel, sauf annulation. La durée, le montant et la prochaine échéance doivent être présentés avant l’achat.

Pour un achat réellement effectué via Google Play : ouvrir **Google Play → profil → Paiements et abonnements → Abonnements → Selkie → Annuler l’abonnement**. Vérifier le compte Google utilisé pour l’achat si l’abonnement n’apparaît pas. Le lien « Gérer mes abonnements Google Play » ouvre la page de gestion, pas un paiement Selkie. Après une résiliation ordinaire, l’accès reste normalement disponible jusqu’à la fin de la période payée ; la page Google Play indique l’échéance exacte. Désinstaller Selkie ou effacer ses données ne résilie pas un abonnement. Une annulation ne constitue pas automatiquement une demande de remboursement.

## Un compte est utile, mais distinct du paiement

Google Play peut gérer un abonnement sans inscription à un compte propre à Selkie. Un compte Selkie devient utile pour associer les droits vérifiés et les retrouver lors d’un changement d’appareil. Il ajoute aussi une responsabilité : connexion sécurisée, récupération, suppression du compte et information sur les données du serveur.

Choix demandé : **un seul téléphone connecté à la fois**, avec changement d’appareil possible. Une nouvelle connexion remplacerait la précédente. Cette règle ne doit pas condamner un utilisateur qui perd ou remplace son téléphone.

Cette version ne propose pas encore ce compte : aucun serveur de comptes Selkie n’est déployé ou configuré. La connexion par code e-mail déjà préparée pour le dialogue IA distant n’est pas un système de restauration des abonnements, et les boutons de démonstration ne créent aucun droit serveur.

## Ce qu’il faut réellement activer

1. Créer et configurer le service d’authentification, par exemple un projet Supabase, sous la responsabilité du véritable éditeur. Configurer la délivrance des codes e-mail et la récupération du compte.
2. Faire vérifier les achats et leur expiration par un serveur avec l’API Google Play ; associer chaque achat à un compte sans permettre son attribution simultanée à plusieurs comptes. Traiter les renouvellements, annulations et remboursements. Un choix Premium local n’est jamais une preuve de paiement.
3. Conserver côté serveur une seule session d’appareil active par compte, avec un identifiant aléatoire d’installation plutôt qu’un identifiant matériel. La nouvelle connexion doit remplacer la session précédente dans une opération atomique. Vérifier cette session à chaque requête protégée, y compris après un renouvellement du jeton : masquer seulement un bouton dans l’APK ne suffit pas.
4. Définir ce qui reste disponible sans Internet. Le suivi et les conseils gratuits peuvent rester locaux. Une ancienne session hors ligne ne peut pas recevoir instantanément sa révocation ; il faut décider et documenter la durée éventuelle d’accès payant en cache avant d’annoncer une restriction effective.
5. Ajouter dans l’application la connexion, la déconnexion, le changement d’appareil, la restauration d’achat et la suppression du compte. La suppression d’un compte ne doit pas être présentée comme la résiliation Google Play.
6. Adapter la politique aux services réellement déployés : responsable, e-mail dédié, données de compte et d’achat, destinataires, région, durées et suppression. Tester deux téléphones, une reconnexion, une perte d’appareil et un achat de test avant vente.

Le responsable actuellement déclaré reste **Abdul Malik Kakar**, avec **1kabdm@gmail.com** comme contact. Si son père devient effectivement l’éditeur et le responsable, les informations et les contrats doivent correspondre à ce rôle réel. L’identité du responsable doit être claire ; un simple nom d’application et une adresse de contact ne remplacent pas cette identification.
