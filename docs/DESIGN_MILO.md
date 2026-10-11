# Design mobile de Milo

Milo 0.7.0 reprend les écrans Android avec une identité cohérente et une hiérarchie plus simple. Le nom a été choisi par l’éditeur ; sa disponibilité commerciale reste à vérifier.

## Règles de présentation

- Fond clair `#F5F8F6`, cartes blanches, texte foncé `#193B32`, texte secondaire `#5B6F65`.
- Une couleur principale pour les actions : vert `#216852`. La menthe et le fond léger servent aux repères ; le rouge reste réservé aux alertes et suppressions.
- Titres d’écran à 28 sp environ, textes courants 14–16 sp, marges horizontales 20 dp, cartes arrondies à 20 dp.
- Une action principale compréhensible dans chaque carte. Les règles longues se consultent à la demande ; les prix de démonstration et l’état du coach restent explicitement indiqués.
- Navigation constante : Accueil, Quotidien, Défis, Coach et Plus. Les offres personnelles ouvrent Plus ; le bouton du quotidien ouvre Coach.
- Chaque étape personnelle occupe une ligne et porte son numéro. Chaque journée de pratique occupe une ligne, dans l’ordre. Pour une durée supérieure à sept jours, la liste autour du progrès actuel peut être développée entièrement.

## Coach et confidentialité

Coach est un écran accessible directement, avec des situations et des conseils locaux. Les réponses sont écrites à l’avance. Aucune clé, connexion ou réponse produite par une IA n’est ajoutée à l’APK. Le futur dialogue avec l’IA demande une intégration Android et serveur avant activation.

La confidentialité affiche d’abord le lieu de stockage, les accès utilisés, les possibilités d’arrêt et d’effacement, l’état du coach et le contact. Le bouton « Lire la politique complète » conserve l’accès hors ligne à tous les détails. L’identité Abdul Malik Kakar et son adresse de contact sont conservées. Le résumé ne constitue pas une certification juridique.

## Outils utiles pour la suite

Un connecteur Figma, s’il est proposé par le client et installé, permettrait de consulter des maquettes et leurs composants. Un skill de design mobile peut formaliser ces règles, les états vide/chargement/erreur et les contrôles sur petits écrans. Aucun connecteur Figma ni skill de design spécialisé n’est installé dans cette session ; l’outil de génération d’images a servi au nouveau logo.

Les plugins de stockage ou de gestion de projet ne remplacent pas ce travail de conception. Une intégration navigateur peut aider à tester une maquette web ; pour cette application native, la validation finale demande Android et un appareil ou émulateur fonctionnel.

## Vérification avant diffusion

Compiler l’application, exécuter les tests des règles existantes et Android Lint. Sur téléphone, contrôler les cinq onglets, le retour de la confidentialité, les étapes dans l’ordre, les grandes durées, le texte agrandi, les choix de permissions, les boutons et les changements d’offre. La compilation seule ne prouve pas le rendu visuel ; les captures finales doivent venir de la version Android réellement installée.
