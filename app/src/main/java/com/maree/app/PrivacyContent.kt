package com.maree.app

/** Identité et contact fournis par l’éditeur pour la confidentialité. */
object PrivacyIdentity {
    const val controllerName = "Abdul Malik Kakar"
    const val contactEmail = "1kabdm@gmail.com"
}

data class PrivacySection(val title: String, val paragraphs: List<String>)

/** Texte embarqué : sa consultation ne dépend d'aucun serveur ni compte. */
object PrivacyContent {
    val updatedOn: String
        get() = tr("12 octobre 2026", "12 October 2026")
    val introduction: String
        get() = tr("Selkie t’aide à réduire ton temps d’écran. Dans cette version de test gratuite, tes données de suivi restent sur ton appareil et le suivi ne demande pas de compte. Le Coach local utilise des conseils préparés, sans compte ni envoi sur Internet. Tu peux lire cette information sans autoriser le suivi.", "Selkie helps you reduce your screen time. In this free test version, tracking data stays on your device and tracking does not require an account. The local Coach uses prepared advice, without an account or sending data over the Internet. You can read this information without granting tracking access.")

    val sections: List<PrivacySection>
        get() = listOf(
            PrivacySection(tr("Qui est responsable ?", "Who is responsible?"), listOf(
                tr(
                    "Responsable du traitement : ${PrivacyIdentity.controllerName}, éditeur de Selkie à titre personnel.",
                    "Data controller: ${PrivacyIdentity.controllerName}, the individual who publishes Selkie."
                ),
                tr(
                    "Contact confidentialité : ${PrivacyIdentity.contactEmail}.",
                    "Privacy contact: ${PrivacyIdentity.contactEmail}."
                )
            )),
            PrivacySection(tr("Quelles données sont utilisées ?", "What data does Selkie use?"), listOf(
                tr(
                    "Selkie consulte localement les noms, icônes et identifiants des applications accessibles depuis le lanceur pour te permettre de choisir celles à suivre.",
                    "Selkie checks the names, icons and identifiers of apps accessible from your launcher locally, so you can choose which apps to track."
                ),
                tr(
                    "Après autorisation de l’accès aux données d’utilisation, Selkie lit les événements Android depuis minuit, heure locale, pour calculer les durées des applications suivies et savoir si une application bloquée est au premier plan. L’identifiant de l’application au premier plan peut aussi concerner une application non suivie : il est utilisé temporairement en mémoire, sans historique enregistré par Selkie.",
                    "After you grant usage access, Selkie reads Android events from midnight in your local time zone to calculate how long you use tracked apps and check whether a blocked app is in the foreground. The foreground app identifier may also refer to an app you do not track: it is used temporarily in memory, without Selkie saving a history of it."
                ),
                tr(
                    "Selkie enregistre sur l’appareil : les identifiants des applications choisies, leurs limites, les identifiants et dates de création des défis personnels, les durées de référence utilisées pour les réinitialisations, les dates de verrouillage, d’alerte et de pénalité, les points, les défis accomplis et les dates locales des validations quotidiennes, les titres et les trois étapes des défis personnels, leur durée et leurs cases du jour, le choix d’offre de démonstration et les dates des cycles de progression. Android conserve ses propres statistiques d’utilisation, indépendamment de Selkie.",
                    "Selkie stores the following on your device: the identifiers of selected apps, their limits, personal challenge identifiers and creation dates, the reference usage durations used for resets, lock, alert and penalty dates, points, completed challenges and local dates of daily check-ins, the titles and three steps of personal challenges, their duration and daily checkboxes, the selected demonstration plan and progress cycle dates. Android keeps its own usage statistics independently of Selkie."
                ),
                tr(
                    "Selkie ne lit pas les messages de tes autres applications, le contenu de tes écrans, tes contacts, tes photos ou ta position. Il n’y a pas de synchronisation du suivi, de publicité, de mesure d’audience ni de paiement actif.",
                    "Selkie does not read messages from your other apps, screen content, contacts, photos or location. There is no tracking synchronisation, advertising, audience analytics or active payment service."
                ),
                tr(
                    "Le Coach local choisit des conseils et des réponses sur Selkie dans un catalogue préparé. Les phrases saisies et, lorsque le conseil le nécessite, tes points et les durées des applications suivies sont utilisés uniquement sur le téléphone. Ce coach n’est pas un modèle d’IA et ne peut pas répondre à toutes les questions nouvelles.",
                    "The local Coach chooses advice and answers about Selkie from a prepared catalogue. Your messages and, when needed for a particular tip, your points and tracked app usage durations are used only on your phone. This coach is not an AI model and cannot answer every new question."
                ),
                tr(
                    "Les échanges restent en mémoire pendant l’ouverture de l’onglet Coach. La conservation des phrases non comprises est une option désactivée par défaut. Si tu l’actives, jusqu’à cinquante phrases distinctes de huit cents caractères maximum restent dans le stockage privé du téléphone. Aucune phrase n’est envoyée automatiquement à l’éditeur. Les messages reconnus comme sensibles ou liés à une crise ne sont pas enregistrés ; cette détection peut être imparfaite, évite de saisir des informations privées.",
                    "Conversations stay in memory while the Coach tab is open. Saving messages the Coach did not understand is an option that is off by default. If you enable it, up to fifty distinct messages of no more than eight hundred characters each remain in the phone’s private storage. No message is automatically sent to the publisher. Messages recognised as sensitive or related to a crisis are not saved; this detection may be imperfect, so avoid entering private information."
                ),
                tr(
                    "La préférence de langue français/anglais et le choix d’apparence clair, nuit ou comme le téléphone sont également conservés uniquement sur l’appareil. Changer la langue ou l’apparence n’envoie aucune donnée à un serveur.",
                    "Your French/English language preference and light, night or phone-controlled appearance choice are also stored only on your device. Changing language or appearance sends no data to a server."
                )
            )),
            PrivacySection(tr("Pourquoi, et sur quelle base ?", "Why is data used, and on what basis?"), listOf(
                tr(
                    "Ces données servent uniquement à fournir le service que tu demandes : choisir des limites, mesurer leur utilisation, afficher des alertes et des écrans de blocage, et conserver ta progression. La base juridique des traitements nécessaires à ce service est son exécution à ta demande, au titre de l’article 6, paragraphe 1, point b du RGPD.",
                    "This data is used only to provide the service you request: setting limits, measuring usage, displaying alerts and blocking screens, and keeping your progress. The legal basis for processing necessary to provide this service is performance of the service at your request, under Article 6(1)(b) of the GDPR."
                ),
                tr(
                    "Les autorisations Android contrôlent l’accès technique aux fonctions du téléphone. Elles ne remplacent pas une base juridique au sens du RGPD et ne valent pas consentement à de la publicité ou à un partage de données. Lire cette politique n’oblige pas à l’accepter et n’active pas le suivi.",
                    "Android permissions control technical access to phone features. They do not replace a legal basis under the GDPR and do not count as consent to advertising or data sharing. Reading this policy does not require you to accept it and does not start tracking."
                ),
                tr(
                    "La conservation facultative des phrases non comprises sert à les retrouver sur ton appareil ; elle repose sur ton consentement, au titre de l’article 6, paragraphe 1, point a du RGPD. Tu peux retirer ce choix à tout moment : désactiver l’option efface les phrases déjà enregistrées. Refuser leur conservation n’empêche pas d’utiliser le Coach, les limites ou les défis.",
                    "Optional saving of messages the Coach did not understand lets you find them again on your device and is based on your consent under Article 6(1)(a) of the GDPR. You can withdraw that choice at any time: turning the option off deletes messages already saved. Declining to save them does not prevent you from using the Coach, limits or challenges."
                )
            )),
            PrivacySection(tr("À quoi servent les autorisations ?", "What are permissions for?"), listOf(
                tr(
                    "Accès aux données d’utilisation : calculer les durées et reconnaître l’application au premier plan. Affichage par-dessus les autres applications : afficher l’écran de blocage quand une limite est atteinte. Ces deux accès sont nécessaires au suivi avec blocage ; sans eux, cette fonction ne démarre pas.",
                    "Usage access: calculating usage durations and identifying the foreground app. Display over other apps: showing the blocking screen when a limit is reached. Both accesses are required for tracking with blocking; without them, this feature does not start."
                ),
                tr(
                    "Notifications : prévenir avant une limite et rendre le suivi visible. Les alertes peuvent afficher le nom d’une application suivie ; tu peux modifier leur visibilité sur l’écran verrouillé dans les réglages Android. Le refus des notifications n’autorise aucun autre accès et reste possible.",
                    "Notifications: warning you before a limit and making tracking visible. Alerts may show the name of a tracked app; you can change their lock-screen visibility in Android settings. You can decline notifications, and doing so does not grant any other access."
                ),
                tr(
                    "Le service au premier plan poursuit le suivi local et peut redémarrer après le redémarrage du téléphone si les accès nécessaires restent autorisés. Tu peux retirer chaque autorisation dans les paramètres Android ; le retrait d’un accès nécessaire arrête le suivi avec blocage.",
                    "The foreground service continues local tracking and may restart after the phone restarts if the required accesses remain granted. You can revoke each permission in Android settings; revoking a required access stops tracking with blocking."
                )
            )),
            PrivacySection(tr("Qui reçoit les données ?", "Who receives the data?"), listOf(
                tr(
                    "Les données de suivi et les échanges du Coach local ne sont pas envoyés au serveur ou à l’éditeur. Le Coach local ne demande pas de compte, ne fait pas de requête réseau et ne propose pas d’export automatique de ses phrases sauvegardées. Ouvrir l’application ou le Coach n’envoie aucun message.",
                    "Tracking data and local Coach conversations are not sent to a server or to the publisher. The local Coach does not require an account, make network requests or automatically export saved messages. Opening the app or the Coach sends no message."
                ),
                tr(
                    "La sauvegarde Android de Selkie est désactivée. Une migration entre appareils peut toutefois dépendre du constructeur et de ses outils ; cette politique ne garantit pas leur fonctionnement. Les traitements propres à Android, au constructeur ou à la plateforme de téléchargement relèvent de leurs informations de confidentialité.",
                    "Android backup is disabled for Selkie. However, migration between devices may depend on the manufacturer and its tools; this policy does not guarantee how they work. Processing by Android, the manufacturer or the download platform is covered by their own privacy information."
                ),
                tr(
                    "Si tu nous écris, ton adresse et le contenu de ton message sont transmis par ton service de messagerie et reçus sur l’adresse de contact, avec les informations techniques liées au courriel. L’éditeur et son fournisseur de messagerie traitent alors ce message. N’inclus pas de données sensibles ni de captures contenant des informations privées.",
                    "If you email us, your address and message are transmitted through your email service and received at the contact address, together with technical information associated with the email. The publisher and their email provider then process that message. Do not include sensitive data or screenshots containing private information."
                ),
                tr(
                    "Les demandes d’exercice des droits sont traitées pour respecter les obligations du RGPD (article 6, paragraphe 1, point c). Les autres questions sont traitées sur la base de l’intérêt légitime à répondre aux personnes qui contactent l’éditeur (article 6, paragraphe 1, point f) ; tu peux t’y opposer. Les messages sont conservés le temps de répondre, puis supprimés, sauf si une obligation légale impose une conservation supplémentaire.",
                    "Requests to exercise your rights are processed to meet GDPR obligations under Article 6(1)(c). Other questions are processed on the basis of the legitimate interest in replying to people who contact the publisher, under Article 6(1)(f); you may object. Messages are kept while replying, then deleted, unless a legal obligation requires further retention."
                ),
                tr(
                    "Le contact utilise Gmail. Les conditions d’hébergement et les éventuels transferts de la messagerie hors de l’Espace économique européen doivent encore être vérifiés et précisés par le responsable avant diffusion publique. Aucun hébergement ou transfert des données locales de suivi n’est effectué par Selkie.",
                    "The contact address uses Gmail. The controller must still check and specify email hosting arrangements and any transfers outside the European Economic Area before public distribution. Selkie does not host or transfer your local tracking data."
                )
            )),
            PrivacySection(tr("Combien de temps sont-elles gardées ?", "How long is data kept?"), listOf(
                tr(
                    "Les réglages et la progression restent sur l’appareil jusqu’à leur suppression depuis Selkie ou la désinstallation. Retirer une application du suivi supprime son entrée, sa limite et son verrou ; les points et totaux de défis restent jusqu’à l’effacement complet. Les durées du jour sont recalculées depuis les événements Android ; Selkie n’en conserve pas un historique détaillé.",
                    "Settings and progress remain on your device until you delete them from Selkie or uninstall it. Removing an app from tracking deletes its entry, limit and lock; points and challenge totals remain until a full data reset. Today’s usage durations are recalculated from Android events; Selkie does not keep a detailed usage history."
                ),
                tr(
                    "Effacer les données supprime les données locales de Selkie, dont les applications suivies, les verrous, les points, le choix d’offre de démonstration et les défis personnels avec leurs étapes et leurs dates, ainsi que les phrases non comprises du Coach et leur option de conservation. À la prochaine utilisation, la progression repart avec les valeurs initiales. Les alertes affichées par Selkie sont retirées. Si un effacement ne peut pas être confirmé, un message t’invite à réessayer. Cette action ne supprime pas les statistiques d’Android et ne révoque pas automatiquement les autorisations accordées dans les paramètres du téléphone. Elle efface aussi la préférence de langue : Selkie revient à l’anglais si le téléphone est en anglais, et au français sinon. Le choix d’apparence est réinitialisé sur Clair.",
                    "Deleting data removes Selkie’s local data, including tracked apps, locks, points, the selected demonstration plan, personal challenges with their steps and dates, and saved unrecognised Coach messages and their saving preference. On your next use, progress starts again from the initial values. Selkie alerts are removed. If deletion cannot be confirmed, a message asks you to try again. This action does not delete Android statistics or automatically revoke permissions granted in the phone’s settings. It also clears your language preference: Selkie returns to English if the phone’s language is English, and to French otherwise. Appearance returns to Light."
                ),
                tr(
                    "Les dates de validation des défis proposés par Selkie sont effacées au renouvellement de la série, tous les 21 jours. Le nombre total de défis terminés et les points restent jusqu’à l’effacement complet. Les défis personnels et leurs dates restent jusqu’à leur suppression individuelle ou à l’effacement complet ; ils ne sont pas réinitialisés par le changement de série.",
                    "Check-in dates for Selkie’s built-in challenges are deleted when the challenge cycle renews, every 21 days. The total number of completed challenges and points remain until a full data reset. Personal challenges and their dates remain until you delete them individually or reset all data; they are not reset when the cycle changes."
                ),
                tr(
                    "La conversation du Coach local reste uniquement en mémoire pendant l’ouverture de son onglet. Les phrases non comprises conservées sur option restent jusqu’à leur effacement, la désactivation de l’option, l’effacement global ou la désinstallation. Au-delà de cinquante phrases, les plus anciennes sont retirées. Effacer seulement les phrases garde ton choix de conservation pour les futures questions ; l’effacement global le remet sur désactivé.",
                    "The local Coach conversation stays only in memory while its tab is open. Unrecognised messages saved through the optional setting remain until you delete them, turn saving off, reset all data or uninstall the app. Beyond fifty messages, the oldest are removed. Deleting only the messages keeps your saving preference for future questions; a full data reset turns it off."
                )
            )),
            PrivacySection(tr("Stockage local et traceurs", "Local storage and trackers"), listOf(
                tr(
                    "Cette version native n’utilise pas de cookies publicitaires, de SDK publicitaire, de mesure d’audience ou d’identifiant de suivi publicitaire. Les préférences locales servent aux fonctions que tu demandes ; l’enregistrement facultatif des questions non comprises reste séparé et désactivé par défaut. Une bannière de cookies n’est pas ajoutée pour ces seules fonctions locales.",
                    "This native version uses no advertising cookies, advertising SDK, audience analytics or advertising tracking identifier. Local preferences support the features you request; optional saving of unrecognised questions is separate and off by default. No cookie banner is added for these local features alone."
                ),
                tr(
                    "La version de test hors ligne ne déclare pas la permission Internet. Les liens que tu choisis d’ouvrir, le service de messagerie et le fournisseur de fichiers que tu sélectionnes sont des applications ou services distincts, avec leurs propres informations de confidentialité. Ajouter des traceurs ou un service connecté demanderait une nouvelle analyse et, lorsque nécessaire, un consentement préalable.",
                    "The offline test version does not declare Internet permission. Links you choose to open, your email app and the file provider you select are separate apps or services with their own privacy information. Adding trackers or an online service would require a new assessment and, where required, prior consent."
                )
            )),
            PrivacySection(tr("Exporter tes réglages et ta progression", "Export your settings and progress"), listOf(
                tr(
                    "Le bouton Exporter mes données prépare un fichier JSON local de tes réglages de suivi et de ta progression. Tu choisis où l’enregistrer avec Android. Les phrases du Coach et les jetons de connexion sont exclus. Aucun fichier n’est envoyé automatiquement à l’éditeur.",
                    "Export my data prepares a local JSON file of your tracking settings and progress. You choose where to save it using Android. Coach messages and sign-in tokens are excluded. No file is automatically sent to the publisher."
                ),
                tr(
                    "Ce fichier peut contenir des informations privées, notamment les applications que tu suis et les textes de tes défis. Si tu choisis un fournisseur de stockage en ligne, celui-ci peut synchroniser le fichier selon ses propres règles. Une copie exportée reste à l’endroit choisi même après l’effacement ou la désinstallation de Selkie ; supprime-la séparément si tu le souhaites.",
                    "The file may contain private information, including tracked apps and your challenge text. If you choose an online storage provider, it may synchronise the file under its own rules. An exported copy remains in your chosen location after resetting or uninstalling Selkie; delete it separately if you wish."
                )
            )),
            PrivacySection(tr("Tes droits et tes choix", "Your rights and choices"), listOf(
                tr(
                    "Tu peux consulter et modifier tes limites, retirer une application et effacer les données de Selkie depuis l’application ou les réglages Android. Tu peux demander l’accès, la rectification, l’effacement, la limitation et, lorsque ses conditions sont réunies, la portabilité de tes données. Le droit d’opposition s’applique aux traitements qui le prévoient ; ce service nécessaire repose sur l’exécution à ta demande. Pour toute question ou demande relative à ces droits, utilise le contact ci-dessus.",
                    "You can view and change your limits, remove an app and delete Selkie data from the app or Android settings. You can request access, rectification, erasure, restriction and, where the conditions are met, portability of your data. The right to object applies to processing that provides for it; the necessary service is based on performance at your request. Use the contact above for any question or request about these rights."
                ),
                tr(
                    "L’éditeur ne reçoit pas les données locales de suivi ni les phrases du Coach et ne peut donc pas les consulter ou les effacer à distance. Ne les envoie pas spontanément pour exercer tes droits. Tu peux également adresser une réclamation à la CNIL : www.cnil.fr/fr/adresser-une-plainte.",
                    "The publisher does not receive your local tracking data or Coach messages and therefore cannot view or delete them remotely. Do not send them unsolicited to exercise your rights. You can also lodge a complaint with the CNIL, the French data protection authority: www.cnil.fr/fr/adresser-une-plainte."
                ),
                tr(
                    "Les blocages et les points sont des aides personnelles au temps d’écran : ils ne constituent pas une décision produisant des effets juridiques ou des effets similaires significatifs au sens de l’article 22 du RGPD.",
                    "Blocking and points are personal screen-time aids: they are not decisions producing legal effects or similarly significant effects within the meaning of Article 22 of the GDPR."
                ),
                tr(
                    "Les demandes relatives à tes droits reçoivent en principe une réponse dans un délai d’un mois. En cas de complexité ou de demandes nombreuses, ce délai peut être prolongé de deux mois ; tu dois alors être informé du motif pendant le premier mois. N’envoie pas spontanément de pièce d’identité : une vérification proportionnée peut être demandée seulement en cas de doute raisonnable.",
                    "Requests about your rights normally receive a response within one month. Complex or numerous requests may require a two-month extension; you must be told the reason during the first month. Do not send identity documents unsolicited: proportionate verification may be requested only where there is reasonable doubt."
                )
            )),
            PrivacySection(tr("Un éventuel Coach IA en ligne", "A possible future online AI Coach"), listOf(
                tr(
                    "Un dialogue IA avec Supabase et OpenAI est préparé dans le code, mais il est distinct du Coach local et n’est pas activé. S’il est proposé à l’avenir, une information et un accord précéderont la connexion par e-mail et les envois. Il utilisera l’adresse e-mail, le compte, les jetons de session et un compteur de questions.",
                    "AI dialogue using Supabase and OpenAI is prepared in the code, but is separate from the local Coach and is not enabled. If offered in the future, information and agreement will come before email sign-in and sending messages. It would use your email address, account, session tokens and a question counter."
                ),
                tr(
                    "Ce service transmettrait la question et au plus douze messages précédents à Supabase, puis à OpenAI pour la réponse, sans joindre automatiquement les applications suivies, points ou défis. Le code demande de ne pas stocker les réponses via l’API, sans garantir l’absence de conservation technique par les fournisseurs. Les échanges resteraient en mémoire dans l’application ; les jetons de session seraient conservés localement jusqu’à une déconnexion ou un effacement. Ces actions ne supprimeraient pas le compte ni le quota serveur : leur suppression pourrait être demandée au responsable.",
                    "This service would send your question and up to twelve previous messages to Supabase, then to OpenAI for a response, without automatically attaching tracked apps, points or challenges. The code asks the API not to store responses, without guaranteeing that providers retain no technical data. Conversations would stay in the app’s memory; session tokens would be kept locally until you sign out or delete data. These actions would not delete the account or server quota: their deletion could be requested from the controller."
                ),
                tr(
                    "Les bases juridiques, la région du projet, les contrats, les durées de conservation et les éventuels transferts hors EEE devront être précisés avant son ouverture. Une compilation connectée devrait déclarer la permission Internet ; cette permission seule ne prouverait pas qu’un service IA est actif.",
                    "Legal bases, the project region, contracts, retention periods and any transfers outside the EEA must be specified before this service opens. An online build would need Internet permission; that permission alone would not establish that an AI service is active."
                )
            )),
            PrivacySection(tr("Si Selkie évolue", "If Selkie changes"), listOf(
                tr(
                    "L’ouverture publique du coach, une synchronisation, une publicité ou un paiement nécessiterait une nouvelle analyse et une information mise à jour avant activation. Cette politique décrit seulement les fonctions de la version actuelle et distingue le Coach local d’une éventuelle connexion future.",
                    "Publicly opening the online coach, synchronisation, advertising or payments would require a new assessment and updated information before activation. This policy describes only the features of the current version and distinguishes the local Coach from a possible future connection."
                )
            ))
        )
}
