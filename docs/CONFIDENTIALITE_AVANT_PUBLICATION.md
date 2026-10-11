# Confidentialité : vérifications avant une diffusion publique

État au 10 octobre 2026. Ce document s’adresse à l’éditeur ; il ne doit pas être présenté comme une certification de conformité. Le texte utilisateur est embarqué dans `PrivacyContent.kt` et repris dans `CONFIDENTIALITE.md`. Aucune politique n’a été publiée sur un site et aucun service cloud n’a été déployé.

## Informations à compléter

- Le responsable est **Abdul Malik Kakar**, personne physique, conformément à l’identité fournie explicitement par l’éditeur et enregistrée dans `PrivacyIdentity.controllerName`. Aucun nom n’a été déduit du dépôt ou de l’adresse de courriel. Tenir cette information à jour si le responsable change. La création d’une entreprise n’est pas une condition technique pour développer un prototype ; une mise en activité, une monétisation et des obligations commerciales nécessitent leur propre analyse.
- Confirmer la surveillance et la sécurisation de `abdulmalikkakar48@gmail.com`, fourni comme contact public par l’éditeur. Limiter les personnes qui accèdent à la boîte. Définir les modalités de réponse et de suppression des courriels, les durées et les exceptions de conservation réellement nécessaires. La politique actuelle prévoit la suppression après réponse, sauf obligation légale supplémentaire : ne pas conserver les messages indéfiniment.
- Examiner le contrat et les conditions du fournisseur de messagerie, son rôle, les sous-traitants, les lieux de traitement, et les éventuels transferts hors EEE ainsi que leurs garanties applicables. Ne pas inventer un hébergement en France, une clause contractuelle ou une certification. Mettre à jour l’information de l’article 13 si des transferts existent. L’adresse Gmail implique un traitement distinct, même si l’APK fonctionne localement sans Internet.
- Mettre le texte à jour à chaque changement fonctionnel. Une nouvelle identité ou une nouvelle adresse publique impose une nouvelle version de l’APK et de la politique publiée ; le texte intégré ne reçoit pas automatiquement une mise à jour à distance.

## Vérifier les traitements et les bases juridiques

- Conserver une description des finalités, données, destinataires, durées, mesures de sécurité et bases juridiques. Contrôler les obligations applicables en matière de registre et d’analyse d’impact selon le déploiement, les utilisateurs et les risques. Le fonctionnement local réduit les transmissions ; il ne constitue pas une exemption générale de droit.
- Le service de limites demandé par l’utilisateur utilise uniquement les données nécessaires à sa réalisation, sur la base de l’article 6(1)(b) du RGPD. Vérifier que la prestation et l’information fournie permettent réellement de retenir cette base ; elle ne s’étend pas à des traitements facultatifs ou étrangers au service.
- Distinguer les bases RGPD des autorisations Android et des règles applicables à l’accès au terminal, notamment l’article 82 de la loi Informatique et Libertés. Vérifier si chaque accès est strictement nécessaire au service expressément demandé. Ajouter une fonction de publicité, d’audience ou d’IA ne serait pas couvert par les permissions actuelles.
- Les demandes d’exercice des droits reçues par courriel relèvent des obligations légales (article 6(1)(c)) ; les autres demandes reçues relèvent ici de l’intérêt légitime à y répondre (article 6(1)(f)). Vérifier et documenter cette analyse, informer du droit d’opposition correspondant et ajuster le texte si la pratique diffère.
- Ne pas imposer une case « J’accepte la politique » pour rendre légal le service. L’information doit être accessible avant les autorisations et après. Toute future demande de consentement doit être spécifique, libre, éclairée, révocable et distincte des autorisations techniques.
- Le lecteur d’usage parcourt les événements depuis minuit, dont ceux d’applications non suivies pour déterminer l’avant-plan, mais ne persiste pas leur historique. Vérifier cette distinction et la minimisation à chaque évolution. Ne pas décrire le fonctionnement comme « accès uniquement aux événements des applications choisies ».

## Vérifier la protection et les droits

- Contrôler le manifeste fusionné et l’APK final : absence de permission Internet, absence de collecte par un SDK, absence de publicité, d’analyse d’audience et de backend actif. Les fichiers de backend présents dans le dépôt ne sont pas une preuve de service déployé. Une bibliothèque ajoutée ultérieurement doit être examinée.
- `android:allowBackup="false"` limite la sauvegarde standard. Vérifier séparément les règles de transfert entre appareils des versions Android et des constructeurs ciblés ; ne pas promettre une impossibilité universelle de migration.
- Les noms d’applications peuvent apparaître dans les alertes. Expliquer à l’utilisateur les réglages Android de visibilité sur écran verrouillé ; éviter tout détail sensible inutile dans les notifications et les journaux.
- Vérifier l’arrêt du service avant l’effacement, la confirmation, la suppression de toutes les préférences Selkie, les données des éventuels autres fichiers de préférences, l’absence de réapparition de verrous ou de défis, et le résultat persistant après relance. `Store.clearPersonalData()` utilise un `commit()` synchrone sous verrou commun et ne doit pas être annoncé comme réussi si l’écriture échoue. Les autorisations et statistiques Android ne sont pas effacées par cette méthode.
- Prévoir une procédure de réponse aux demandes de droits dans les délais du RGPD (en principe un mois), proportionnée à l’identité et aux données réellement détenues. Ne pas demander automatiquement une pièce d’identité ni des captures privées. Vérifier les modalités d’accès et de portabilité applicables aux données locales ; l’éditeur ne peut pas affirmer fournir ou supprimer à distance des données qu’il ne reçoit pas.
- Analyser les exigences si des enfants sont ciblés. Le seuil français de consentement numérique n’est pas une règle générale autorisant tous les traitements ; les conditions d’un consentement éventuel et de la prestation doivent être analysées selon le public et les fonctions.

## Distribution et évolutions

- Pour Google Play, publier une politique sur une URL publique stable, accessible sans connexion, disponible dans l’application et correspondant aux traitements réels, aux développeurs et à l’identité déclarée. Le seul fichier Markdown du dépôt et l’écran intégré ne prouvent pas qu’une URL publique adéquate existe. Aucun lien hébergé n’a été créé dans cette tâche.
- Compléter avec exactitude la section Sécurité des données, la déclaration d’utilisation du service au premier plan et les demandes d’autorisations. Les règles Play et leur champ d’application doivent être vérifiés au moment du dépôt ; ne pas affirmer « collecte » ou « partage » sans examiner les définitions de la plateforme.
- Si des comptes sont ajoutés, prévoir la sécurité, les droits, les durées, les destinataires et la suppression de compte ; appliquer les exigences Play en vigueur. Un compte n’est pas nécessaire pour la version locale actuelle et créerait de nouveaux traitements.
- Avant synchronisation, IA, abonnement, paiement ou publicité, examiner notamment les fournisseurs, contrats, bases juridiques, transferts, information, consentement lorsque requis, règles de consommation et règles du magasin. Réviser la politique avant l’activation. Ne pas afficher une offre payante comme active si l’achat n’est pas implémenté.
- L’ensemble de ces points dépend de la façon dont le service est exploité. Aucun texte seul ne garantit l’absence de sanctions ; demander un avis juridique ciblé si la diffusion ou le modèle économique le justifie.

## Références officielles

Les liens suivants servent à vérifier les exigences, ils ne sont pas des preuves d’une validation de l’application. Les pages externes n’ont pas pu être consultées depuis cet environnement : le proxy a renvoyé un refus HTTP 403 le 10 octobre 2026. Relire les textes officiels et les politiques de la plateforme avant publication.

- [RGPD, texte officiel sur EUR-Lex](https://eur-lex.europa.eu/eli/reg/2016/679/oj/fra), notamment articles 5, 6, 12, 13, 15 à 22, 25, 28, 32 et 44 et suivants.
- [RGPD présenté par la CNIL](https://www.cnil.fr/fr/reglement-europeen-protection-donnees), notamment l’article 13 pour l’identité du responsable, les finalités, bases, destinataires, transferts, durées et droits.
- [Droits sur les données personnelles, CNIL](https://www.cnil.fr/fr/les-droits-pour-maitriser-vos-donnees-personnelles).
- [Adresser une plainte à la CNIL](https://www.cnil.fr/fr/adresser-une-plainte).
- [CNIL](https://www.cnil.fr/) : consulter les recommandations applicables aux applications mobiles, à l’accès au terminal et à la protection des mineurs.
- [Google Play : données utilisateur et politique de confidentialité](https://support.google.com/googleplay/android-developer/answer/10144311).
- [Google Play : section Sécurité des données](https://support.google.com/googleplay/android-developer/answer/10787469).
- [Règles de confidentialité Google](https://policies.google.com/privacy?hl=fr), à examiner pour le contact Gmail, avec les contrats et garanties effectivement applicables au compte utilisé.
