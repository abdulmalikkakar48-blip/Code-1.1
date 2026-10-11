package com.maree.app

/** Identité et contact fournis par l’éditeur pour la confidentialité. */
object PrivacyIdentity {
    const val controllerName = "Abdul Malik Kakar"
    const val contactEmail = "abdulmalikkakar48@gmail.com"
}

data class PrivacySection(val title: String, val paragraphs: List<String>)

/** Texte embarqué : sa consultation ne dépend d'aucun serveur ni compte. */
object PrivacyContent {
    const val updatedOn = "11 octobre 2026"
    const val introduction = "Selkie t’aide à réduire ton temps d’écran. Dans cette version, " +
        "tes données de suivi restent sur ton appareil et le suivi ne demande pas de compte. " +
        "Le dialogue IA facultatif, lorsqu’il est activé, demande une connexion par e-mail et un accord avant tout envoi. " +
        "Tu peux lire cette information sans autoriser le suivi."

    val sections: List<PrivacySection>
        get() = listOf(
            PrivacySection("Qui est responsable ?", listOf(
                "Responsable du traitement : ${PrivacyIdentity.controllerName}, éditeur de Selkie à titre personnel.",
                "Contact confidentialité : ${PrivacyIdentity.contactEmail}."
            )),
            PrivacySection("Quelles données sont utilisées ?", listOf(
                "Selkie consulte localement les noms, icônes et identifiants des applications " +
                    "accessibles depuis le lanceur pour te permettre de choisir celles à suivre.",
                "Après autorisation de l’accès aux données d’utilisation, Selkie lit les événements " +
                    "Android depuis minuit, heure locale, pour calculer les durées des applications " +
                    "suivies et savoir si une application bloquée est au premier plan. L’identifiant " +
                    "de l’application au premier plan peut aussi concerner une application non suivie : " +
                    "il est utilisé temporairement en mémoire, sans historique enregistré par Selkie.",
                "Selkie enregistre sur l’appareil : les identifiants des applications choisies, leurs " +
                    "limites, les durées de référence utilisées pour les réinitialisations, les dates " +
                    "de verrouillage, d’alerte et de pénalité, les points, les défis accomplis et " +
                    "les dates locales des validations quotidiennes, les titres et les trois étapes " +
                    "des défis personnels, leur durée et leurs cases du jour, le choix d’offre de " +
                    "démonstration et les dates des cycles de progression. Android conserve ses propres statistiques " +
                    "d’utilisation, indépendamment de Selkie.",
                "Selkie ne lit pas tes messages, le contenu de tes écrans, tes contacts, tes photos " +
                    "ou ta position. Il n’y a pas de synchronisation du suivi, de publicité, de " +
                    "mesure d’audience ni de paiement actif. Les conseils fixes fonctionnent hors ligne. " +
                    "Le dialogue IA, quand le serveur est configuré, utilise ton adresse e-mail, un identifiant " +
                    "de connexion, des jetons de session et un compteur de questions. Il envoie uniquement " +
                    "la question saisie et les douze derniers messages de la conversation, sans transmettre " +
                    "automatiquement les applications suivies, points ou défis."
            )),
            PrivacySection("Pourquoi, et sur quelle base ?", listOf(
                "Ces données servent uniquement à fournir le service que tu demandes : choisir " +
                    "des limites, mesurer leur utilisation, afficher des alertes et des écrans de " +
                    "blocage, et conserver ta progression. La base juridique des traitements nécessaires " +
                    "à ce service est son exécution à ta demande, au titre de l’article 6, paragraphe 1, " +
                    "point b du RGPD.",
                "Les autorisations Android contrôlent l’accès technique aux fonctions du téléphone. " +
                    "Elles ne remplacent pas une base juridique au sens du RGPD et ne valent pas " +
                    "consentement à de la publicité ou à un partage de données. Lire cette politique " +
                    "n’oblige pas à l’accepter et n’active pas le suivi. Le dialogue IA reste facultatif : " +
                    "une information et une case d’accord précèdent la connexion et les envois. Refuser " +
                    "ou quitter le dialogue n’empêche pas d’utiliser les limites et les défis locaux."
            )),
            PrivacySection("À quoi servent les autorisations ?", listOf(
                "Accès aux données d’utilisation : calculer les durées et reconnaître l’application " +
                    "au premier plan. Affichage par-dessus les autres applications : afficher l’écran " +
                    "de blocage quand une limite est atteinte. Ces deux accès sont nécessaires au " +
                    "suivi avec blocage ; sans eux, cette fonction ne démarre pas.",
                "Notifications : prévenir avant une limite et rendre le suivi visible. Les alertes " +
                    "peuvent afficher le nom d’une application suivie ; tu peux modifier leur visibilité " +
                    "sur l’écran verrouillé dans les réglages Android. Le refus des notifications " +
                    "n’autorise aucun autre accès et reste possible.",
                "Le service au premier plan poursuit le suivi local et peut redémarrer après " +
                    "le redémarrage du téléphone si les accès nécessaires restent autorisés. " +
                    "Tu peux retirer chaque autorisation dans les paramètres Android ; le retrait " +
                    "d’un accès nécessaire arrête le suivi avec blocage."
            )),
            PrivacySection("Qui reçoit les données ?", listOf(
                "Les données de suivi ne sont pas envoyées au serveur. La permission Internet permet " +
                    "uniquement les demandes volontaires de connexion et de dialogue avec le coach lorsque " +
                    "son service est activé ; ouvrir l’application ou le Coach n’envoie aucun message.",
                "Pour le dialogue IA, Supabase traite l’authentification par e-mail et le quota, et son serveur " +
                    "transmet les échanges à OpenAI pour produire une réponse. Le code demande de ne pas " +
                    "stocker les réponses via l’API, mais cela ne garantit pas l’absence de conservation " +
                    "technique par les fournisseurs. Leurs durées de conservation, la région du projet, " +
                    "les contrats et éventuels transferts hors EEE doivent être précisés avant une " +
                    "ouverture publique. N’envoie pas d’informations sensibles.",
                "La sauvegarde Android de Selkie est désactivée. Une migration entre appareils " +
                    "peut toutefois dépendre du constructeur et de ses outils ; cette politique " +
                    "ne garantit pas leur fonctionnement. Les traitements propres à Android, " +
                    "au constructeur ou à la plateforme de téléchargement relèvent de leurs " +
                    "informations de confidentialité.",
                "Si tu nous écris, ton adresse et le contenu de ton message sont transmis par " +
                    "ton service de messagerie et reçus sur l’adresse de contact, avec les " +
                    "informations techniques liées au courriel. L’éditeur et son fournisseur de " +
                    "messagerie traitent alors ce message. N’inclus pas de données sensibles ni " +
                    "de captures contenant des informations privées.",
                "Les demandes d’exercice des droits sont traitées pour respecter les obligations " +
                    "du RGPD (article 6, paragraphe 1, point c). Les autres questions sont traitées " +
                    "sur la base de l’intérêt légitime à répondre aux personnes qui contactent " +
                    "l’éditeur (article 6, paragraphe 1, point f) ; tu peux t’y opposer. Les " +
                    "messages sont conservés le temps de répondre, puis supprimés, sauf si une " +
                    "obligation légale impose une conservation supplémentaire.",
                "Le contact utilise Gmail. Les conditions d’hébergement et les éventuels transferts " +
                    "de la messagerie hors de l’Espace économique européen doivent encore être " +
                    "vérifiés et précisés par le responsable avant diffusion publique. Aucun " +
                    "hébergement ou transfert des données locales de suivi n’est effectué par Selkie."
            )),
            PrivacySection("Combien de temps sont-elles gardées ?", listOf(
                "Les réglages et la progression restent sur l’appareil jusqu’à leur suppression " +
                    "depuis Selkie ou la désinstallation. Retirer une application du suivi supprime " +
                    "son entrée, sa limite et son verrou ; les points et totaux de défis restent " +
                    "jusqu’à l’effacement complet. Les durées du jour sont recalculées depuis les " +
                    "événements Android ; Selkie n’en conserve pas un historique détaillé.",
                "Effacer les données supprime les données locales de Selkie, dont les applications " +
                    "suivies, les verrous, les points, le choix d’offre de démonstration et les défis " +
                    "personnels avec leurs étapes et leurs dates. À la prochaine utilisation, " +
                    "la progression repart avec les valeurs initiales. Cette action ne supprime " +
                    "pas les statistiques d’Android, ne supprime pas un éventuel compte du coach sur " +
                    "le serveur et ne révoque pas automatiquement les " +
                    "autorisations accordées dans les paramètres du téléphone.",
                "Les dates de validation des défis proposés par Selkie sont effacées au renouvellement de la série, " +
                    "tous les 21 jours. Le nombre total de défis terminés et les points restent " +
                    "jusqu’à l’effacement complet. Les défis personnels et leurs dates restent " +
                    "jusqu’à leur suppression individuelle ou à l’effacement complet ; ils ne " +
                    "sont pas réinitialisés par le changement de série.",
                "Les échanges du coach sont conservés uniquement en mémoire pendant l’ouverture de " +
                    "son onglet ; le code du serveur ne les inscrit pas dans une table ni dans ses journaux. " +
                    "Les jetons de session restent dans le stockage privé du téléphone jusqu’à une déconnexion " +
                    "ou un effacement local. L’adresse e-mail, le compte et le quota restent dans Supabase : " +
                    "contacte le responsable pour demander leur suppression. Les traitements techniques " +
                    "des fournisseurs sont distincts et doivent être documentés avant ouverture publique."
            )),
            PrivacySection("Tes droits et tes choix", listOf(
                "Tu peux consulter et modifier tes limites, retirer une application et effacer " +
                    "les données de Selkie depuis l’application ou les réglages Android. Tu peux " +
                    "demander l’accès, la rectification, l’effacement, la limitation et, lorsque " +
                    "ses conditions sont réunies, la portabilité de tes données. Le droit " +
                    "d’opposition s’applique aux traitements qui le prévoient ; ce service " +
                    "nécessaire repose sur l’exécution à ta demande. Pour toute question ou " +
                    "demande relative à ces droits, utilise le contact ci-dessus.",
                "L’éditeur ne reçoit pas les données locales de suivi et ne peut donc pas les " +
                    "consulter ou les effacer à distance. Ne les envoie pas spontanément pour " +
                    "exercer tes droits. Tu peux également adresser une réclamation à la CNIL : " +
                    "www.cnil.fr/fr/adresser-une-plainte.",
                "Les blocages et les points sont des aides personnelles au temps d’écran : " +
                    "ils ne constituent pas une décision produisant des effets juridiques ou " +
                    "des effets similaires significatifs au sens de l’article 22 du RGPD."
            )),
            PrivacySection("Si Selkie évolue", listOf(
                "L’ouverture publique du coach, une synchronisation, une publicité ou un paiement " +
                    "nécessiterait une nouvelle analyse et une information mise à jour avant " +
                    "activation. Cette politique décrit seulement les fonctions de la version " +
                    "actuelle et de la connexion facultative du coach."
            ))
        )
}
