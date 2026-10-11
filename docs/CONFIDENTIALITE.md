# Confidentialité de Selkie

Information mise à jour le **11 octobre 2026**. Elle décrit le suivi local et le dialogue IA facultatif préparé dans le code. Le serveur IA n’est pas encore activé ; l’APK 0.8.0 publié reste antérieur à cette connexion.

Selkie aide à réduire le temps d’écran. Les données de suivi restent sur l’appareil et aucun compte n’est nécessaire. Cette information peut être consultée hors ligne dans l’application, avant toute autorisation et depuis les réglages. Sa consultation ou son affichage n’active pas le suivi et aucune case d’acceptation obligatoire n’est demandée.

## Responsable et contact

**Responsable du traitement : Abdul Malik Kakar**, éditeur de Selkie à titre personnel (personne physique).

Contact confidentialité : **abdulmalikkakar48@gmail.com**.

## Données utilisées

- Les noms, icônes et identifiants des applications accessibles depuis le lanceur sont consultés localement pour choisir les applications à suivre.
- Après autorisation, les événements d’utilisation fournis par Android depuis minuit, heure locale, sont lus pour calculer les durées des applications suivies et reconnaître l’application au premier plan. L’identifiant de l’application au premier plan peut concerner une application non suivie : il est temporairement utilisé en mémoire, sans historique enregistré par Selkie.
- Les identifiants des applications choisies, leurs limites, les durées de référence servant aux réinitialisations, les dates de verrouillage, d’alerte et de pénalité, le solde de points et ses quarts de point, les défis accomplis, les dates locales des validations quotidiennes et les dates des cycles de progression sont enregistrés dans les données privées de Selkie sur l’appareil.
- Les titres des défis personnels, leurs trois étapes, la durée choisie, les cases du jour et leur progression sont enregistrés localement. Le choix d’offre dans le mode démonstration est également conservé ; il ne constitue pas un abonnement et ne donne lieu à aucun paiement.

Les conseils fixes du Coach fonctionnent hors ligne et n’envoient rien. Le dialogue IA, lorsque son serveur est activé, demande une connexion par code e-mail et un accord avant les échanges. Supabase traite l’adresse e-mail, le compte et le compteur de questions. Le téléphone transmet la question et au plus douze messages précédents au serveur, qui les transmet à OpenAI. Aucun bilan d’utilisation, point ou défi n’est joint automatiquement.

Selkie ne lit pas les messages des autres applications, le contenu des écrans, les contacts, les photos ou la position. Il n’y a ni synchronisation du suivi, ni publicité, ni mesure d’audience, ni paiement actif. Android conserve ses propres statistiques d’utilisation, indépendamment de Selkie.

## Finalités et bases juridiques

Les données de suivi servent uniquement à fournir le service demandé : choisir des limites, mesurer leur utilisation, afficher des alertes et des écrans de blocage, et conserver la progression. La base juridique des traitements nécessaires à ce service est son exécution à la demande de l’utilisateur, au titre de l’article 6, paragraphe 1, point b du RGPD.

Les autorisations Android contrôlent l’accès technique aux fonctions du téléphone. Elles ne remplacent pas une base juridique au sens du RGPD et ne valent pas consentement à de la publicité ou à un partage de données. Le dialogue IA est facultatif : l’utilisateur reçoit une information et accepte les transmissions avant de se connecter et d’envoyer une question. Refuser ou quitter le dialogue n’empêche pas le suivi local. Les bases et contrats applicables au service IA doivent être précisés avant son ouverture publique.

Les demandes d’exercice des droits reçues par courriel sont traitées pour respecter les obligations du RGPD (article 6, paragraphe 1, point c). Les autres questions sont traitées sur la base de l’intérêt légitime à répondre aux personnes qui contactent l’éditeur (article 6, paragraphe 1, point f), avec possibilité d’opposition.

## Autorisations du téléphone

- **Accès aux données d’utilisation** : calculer les durées et reconnaître l’application au premier plan.
- **Affichage par-dessus les autres applications** : afficher le blocage lorsqu’une limite est atteinte.
- **Notifications** : prévenir avant une limite et rendre le suivi visible. Elles peuvent afficher le nom d’une application suivie ; leur visibilité sur l’écran verrouillé se règle dans Android. Le refus des notifications reste possible.

Les deux premiers accès sont nécessaires au suivi avec blocage. Sans eux, cette fonction ne démarre pas. Le service au premier plan poursuit le suivi local et peut redémarrer après un redémarrage du téléphone si les accès nécessaires restent autorisés. Chaque accès peut être retiré dans les réglages Android. Le retrait d’un accès nécessaire arrête le suivi avec blocage.

## Destinataires, sauvegarde et transferts

Les données de suivi ne sont pas envoyées à l’éditeur ou à un serveur. Le code déclare désormais la permission Internet pour les demandes volontaires de connexion et de dialogue avec le coach ; l’ouverture de l’application ou de l’onglet Coach ne déclenche pas de requête.

Quand le service est activé, Supabase reçoit les données de connexion et traite le quota ; OpenAI reçoit les messages pour produire une réponse. Le code utilise `store: false`, sans garantie d’absence de conservation technique par les fournisseurs. La région du projet, les contrats, durées de conservation et éventuels transferts hors EEE doivent être renseignés avant une ouverture publique. Les messages ne doivent pas contenir d’informations sensibles.

La sauvegarde Android de Selkie est désactivée. Une migration entre appareils peut cependant dépendre du constructeur et de ses outils ; aucune garantie n’est donnée sur leur fonctionnement. Les traitements propres à Android, au constructeur et à la plateforme de téléchargement relèvent de leurs informations de confidentialité.

En contactant l’éditeur par courriel, l’adresse de l’expéditeur, le message et les informations techniques liées au courriel sont transmis par son service de messagerie puis reçus par l’éditeur et son fournisseur de messagerie. Il ne faut pas envoyer spontanément des données sensibles, des captures privées ou des données de suivi.

**Le contact utilise Gmail. Les conditions d’hébergement et les éventuels transferts de cette messagerie hors de l’Espace économique européen doivent encore être vérifiés et précisés par le responsable avant diffusion publique.** Aucun hébergement ou transfert des données de suivi n’est effectué par Selkie. Ce point concernant les courriels ne doit pas être confondu avec le fonctionnement local de l’APK.

## Conservation et suppression

Les réglages et la progression restent sur l’appareil jusqu’à leur suppression depuis Selkie ou sa désinstallation. Retirer une application du suivi supprime son entrée, sa limite et son verrou ; les points et les totaux de défis restent jusqu’à l’effacement complet. Les durées du jour sont recalculées à partir des événements Android : Selkie n’en conserve pas un historique détaillé.

Les dates de validation des défis proposés par Selkie appartiennent à la série en cours. Elles sont effacées lors de son renouvellement, tous les 21 jours ; le nombre total de défis terminés et les points restent conservés jusqu’à l’effacement complet. Les défis personnels sont indépendants de ces séries : leurs textes, étapes et dates restent jusqu’à leur suppression individuelle ou à l’effacement complet. Supprimer un défi personnel ne retire pas les points déjà gagnés.

Le bouton **Effacer les données de Selkie**, suivi d’une confirmation, supprime les applications suivies, les limites, les verrous, les points et leurs fractions, les défis et leurs dates enregistrées, les défis personnels et leurs trois étapes ainsi que le choix d’offre de démonstration. Lors d’une nouvelle utilisation, la progression repart avec les valeurs initiales. Cela ne supprime pas les statistiques conservées par Android et ne révoque pas les autorisations du téléphone ; ces dernières se retirent dans les réglages Android.

Les messages reçus au contact de confidentialité sont conservés le temps de répondre à la demande, puis supprimés, sauf lorsqu’une obligation légale impose une conservation supplémentaire. Le responsable doit appliquer effectivement cette règle et documenter toute exception légale avant diffusion publique.

La conversation IA reste en mémoire pendant l’ouverture de son onglet ; le code ne la stocke pas dans un fichier local ou une table serveur. Les jetons de session restent dans le stockage privé du téléphone, exclu des sauvegardes Android, jusqu’à une déconnexion locale ou un effacement des données. Ces actions ne suppriment pas le compte et le quota Supabase : leur suppression peut être demandée au responsable. Les conservations techniques des fournisseurs sont distinctes et doivent être documentées avant ouverture publique.

## Droits et recours

Les limites sont consultables et modifiables dans l’application. Une application peut être retirée du suivi et les données de Selkie peuvent être effacées depuis l’application ou les réglages Android.

Il est possible de demander l’accès, la rectification, l’effacement, la limitation et, lorsque ses conditions sont réunies, la portabilité des données. Le droit d’opposition s’applique aux traitements qui le prévoient, notamment aux autres questions reçues par courriel traitées sur la base de l’intérêt légitime. Le service nécessaire de suivi repose sur son exécution à la demande de l’utilisateur. Toute question ou demande peut être adressée au contact indiqué ci-dessus.

L’éditeur ne reçoit pas les données locales de suivi et ne peut pas les consulter ou les effacer à distance. Elles ne doivent pas être envoyées spontanément pour exercer un droit. Une demande relative aux informations effectivement reçues par courriel peut être traitée par le responsable sans accès à l’appareil.

Une réclamation peut être adressée à la **CNIL** : <https://www.cnil.fr/fr/adresser-une-plainte>.

Les blocages et les points sont des aides personnelles au temps d’écran. Ils ne constituent pas une décision produisant des effets juridiques ou des effets similaires significatifs au sens de l’article 22 du RGPD.

## Évolution de l’application

L’ouverture publique du coach, une synchronisation, une publicité ou un paiement nécessite une analyse et une information adaptées aux services réellement déployés. Cette politique décrit le suivi local et le dialogue facultatif préparé dans le code ; elle ne prouve pas qu’un service IA est actif.

Ce texte d’information et les fonctions de suppression constituent une base de travail. **Ils ne garantissent pas à eux seuls la conformité de l’ensemble du service ni l’absence d’amende.** Les modalités du traitement des courriels doivent notamment être vérifiées et précisées avant une diffusion publique.
