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
        get() = tr("17 octobre 2026", "17 October 2026")
    val introduction: String
        get() = tr("Selkie 0.22.0 t’aide à réduire ton temps d’écran. Dans cette version de test gratuite, le suivi, le calendrier, les bilans, les plages horaires, les favoris, les programmes et les coloriages restent sur ton appareil, sans compte ni envoi sur Internet. Le Dé Selkie suggère des pauses que tu choisis de confirmer et propose un mode Points facultatif avec une perte annoncée. Le Coach local utilise des conseils préparés, sans modèle d’IA. Tu peux lire cette information sans autoriser le suivi.", "Selkie 0.22.0 helps you reduce your screen time. In this free test version, tracking, the calendar, summaries, time slots, favourites, programmes and colouring pages stay on your device, without an account or sending data over the Internet. Selkie Dice suggests breaks that you choose whether to confirm and offers an optional Points mode with a disclosed deduction. The local Coach uses prepared advice, without an AI model. You can read this information without granting tracking access.")

    val sections: List<PrivacySection>
        get() = listOf(
            PrivacySection(tr("Qui est responsable ?", "Who is responsible?"), listOf(
                tr(
                    "Responsable du traitement de cette version de test : ${PrivacyIdentity.controllerName}, éditeur de Selkie à titre personnel. L’éditeur et le public de la future diffusion publique restent à définir.",
                    "Data controller for this test version: ${PrivacyIdentity.controllerName}, the individual who publishes Selkie. The publisher and audience for a future public release remain to be determined."
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
                    "Lecture propose des passages historiques sélectionnés, leur source et des traductions préparées pour Selkie, disponibles gratuitement hors ligne. L’application ne conserve aucun historique de lecture ni compte. Enregistrer un extrait crée un fichier texte à l’endroit que tu choisis avec Android ; cette copie reste séparée et n’est pas supprimée par l’effacement de Selkie. Ouvrir la source lance ton navigateur vers un site extérieur, dont les règles de confidentialité s’appliquent. Le site peut voir la requête et ton adresse réseau ; Selkie ne lui joint pas tes points, tâches ou statistiques de suivi. Les ouvrages extérieurs sont plus larges que les passages sélectionnés.",
                    "Reading offers selected historical passages, their sources and translations prepared for Selkie, available free and offline. The app keeps no reading history or account. Saving an excerpt creates a text file wherever you choose through Android; this separate copy is not deleted by resetting Selkie. Opening a source launches your browser to an external site, whose privacy rules apply. That site can see the request and your network address; Selkie does not attach your points, tasks or tracking statistics. External works are broader than the selected passages."
                ),
                tr(
                    "Le calendrier et le bilan hebdomadaire Gratuit présentent une fenêtre de 90 jours locaux glissants de durées observées, de dépassements observés et de validations. Seules les applications choisies et les périodes réellement observées pendant le suivi actif alimentent cet historique ; les pauses et les intervalles non observés ne sont pas comptés. Un intervalle réellement observé peut avoir un usage nul lorsqu’aucune application n’est choisie, mais les périodes inconnues ne deviennent pas des périodes à zéro. Aucune journée passée n’est reconstruite. Les moyennes portent uniquement sur les jours observés ; une absence de dépassement observé ne prouve pas une journée parfaite. Premium et Ultra ajoutent le détail hebdomadaire par application. Ce calendrier est propre à Selkie : l’application ne lit ni ne modifie le calendrier du téléphone et ne demande aucun accès Calendrier.",
                    "The Free calendar and weekly summary show a rolling window of 90 local days of observed usage, observed breaches and check-ins. This history includes only selected apps and intervals actually observed while tracking is active; pauses and unobserved gaps are not counted. An actually observed interval may have zero usage when no app is selected, but unknown intervals never become zero-usage intervals. Past days are never reconstructed. Averages cover observed days only; no observed breach does not prove a perfect day. Premium and Ultra add weekly details for each app. This calendar belongs to Selkie: the app does not read or change your phone’s calendar and requests no Calendar access."
                ),
                tr(
                    "Tu peux prévoir des tâches personnelles pour une date passée ou à venir, gratuitement. Selkie conserve leur texte, leur date et leur état terminé dans le stockage privé du téléphone, jusqu’à leur suppression, l’effacement global ou la désinstallation. Ces tâches sont distinctes de l’historique de suivi limité à 90 jours, ne donnent pas de points et n’envoient aucun rappel. Elles sont incluses dans l’export que tu choisis : évite les informations sensibles et protège le fichier exporté. Aucun accès au calendrier Android n’est demandé.",
                    "You can plan personal tasks for a past or future date on Free. Selkie keeps their text, date and completion state in private phone storage until you delete them, reset all data or uninstall the app. These tasks are separate from the 90-day tracking history, award no points and send no reminders. They are included in the export you choose: avoid sensitive information and protect the exported file. No Android Calendar access is requested."
                ),
                tr(
                    "Le Dé Selkie est gratuit. Ses modes Activité, Appli et Semaine proposent des pauses ; leurs choix et résultats non confirmés restent temporaires en mémoire, sans historique de ces lancers. Confirmer ajoute seulement des tâches personnelles au calendrier, avec leur texte, leur date et leur état terminé. Leur validation est manuelle, sans points supplémentaires, blocage automatique ni modification de tes limites existantes.",
                    "Selkie Dice is free. Its Activity, App and Week modes suggest breaks; their unconfirmed choices and results stay temporarily in memory, with no history of those rolls. Confirming only adds personal calendar tasks with their text, date and completion state. Check-ins are manual, with no extra points, automatic blocking or changes to your existing limits."
                ),
                tr(
                    "Ton solde commence à 100 points d’honneur et reste entre 0 et 100 : les anciens soldes supérieurs reviennent à 100, sans effacer les autres progrès. Les gains s’arrêtent au plafond. Dans le mode Points facultatif du dé, une face de 1 à 6 retire de 1 à 6 points, au maximum ton solde restant ; à zéro, aucun nouveau lancer n’est possible. Un éventuel bonus déjà dû est traité avant cette perte. Il n’y a ni gain ni argent en jeu. Le téléphone conserve seulement le dernier reçu de ce mode (identifiant de tentative, face, perte réelle et solde après le lancer) pour afficher le résultat et éviter un second retrait lors d’une nouvelle tentative de confirmation. Il est remplacé au lancer suivant, inclus dans l’export choisi et supprimé par l’effacement global ou la désinstallation.",
                    "Your honor balance starts at 100 and stays between 0 and 100: older higher balances return to 100 without erasing other progress. Rewards stop at the cap. In the optional Points dice mode, a face from 1 to 6 deducts 1 to 6 points, at most your remaining balance; no new roll is allowed at zero. Any bonus already due is settled before this deduction. There are no winnings or money involved. The phone keeps only this mode’s last receipt (attempt identifier, face, actual deduction and balance after the roll) to display the result and prevent another deduction when confirming the same attempt again. It is replaced by the next roll, included in your chosen export and deleted by global erasure or uninstalling."
                ),
                tr(
                    "Le coloriage gratuit propose ${coloringTemplates.size} pages de 12 × 12 cases, dont huit motifs géométriques et quatre petits dessins originaux, dix couleurs et une gomme. Selkie conserve le motif choisi et les couleurs de ses cases uniquement dans le stockage privé de ce téléphone. Aucun dessin n’est envoyé à un serveur ; aucun accès à tes photos ou fichiers n’est demandé. L’annulation et le rétablissement conservent au maximum cinquante gestes en mémoire, sans historique persistant. Chaque page reste enregistrée jusqu’à sa réinitialisation, l’effacement global des données ou la désinstallation. Les couleurs enregistrées sont incluses dans l’export JSON que tu choisis ; une copie exportée doit être supprimée séparément.",
                    "Free colouring offers ${coloringTemplates.size} pages with 12 × 12 cells, including eight geometric patterns and four original small drawings, ten colours and an eraser. Selkie keeps the selected pattern and its cell colours only in private storage on this phone. No drawing is sent to a server; no access to your photos or files is requested. Undo and redo retain at most fifty gestures in memory, without a saved history. Each page stays saved until you reset that page, erase all data or uninstall the app. Saved colours are included in the JSON export you choose; an exported copy must be deleted separately."
                ),
                tr(
                    "Illustrations propose vingt dessins originaux : huit dans Fleurs, sept dans Paysages et cinq dans Motifs. Les huit illustrations précédentes et leurs couleurs sont conservées. Tu remplis chaque zone au toucher, avec seize couleurs et une gomme ; le zoom et les repères numérotés sont facultatifs. Le téléphone conserve seulement l’identifiant du dessin et les couleurs choisies pour ses zones, séparément des douze pages à carreaux. Les cinquante dernières actions peuvent être annulées pendant que la page reste ouverte ; cet historique n’est pas enregistré. Chaque illustration reste sur ce téléphone jusqu’à son effacement, l’effacement global ou la désinstallation. L’export JSON choisi inclut ces couleurs ; sa copie doit être supprimée séparément. Aucun téléchargement d’image, envoi à un serveur ni nouvel accès Android n’est nécessaire.",
                    "Illustrations offers twenty original drawings: eight in Flowers, seven in Landscapes and five in Patterns. The eight earlier illustrations and their saved colors are preserved. Fill an area by tapping it, using sixteen colors and an eraser; zoom and number guides are optional. The phone keeps only the drawing identifier and your selected area colors, separately from the twelve grid pages. You can undo the last fifty actions while the page stays open; this history is not saved. Each illustration stays on this phone until you clear it, erase all data or uninstall. Your chosen JSON export includes these colors; its copy must be deleted separately. No image download, server transmission or new Android access is needed."
                ),
                tr(
                    "Pour comparer deux observations consécutives espacées d’au plus 30 secondes, Selkie conserve le dernier relevé technique : heure, fuseau horaire, identifiants et durées des applications choisies. Ce relevé est remplacé à l’observation suivante et supprimé à la mise en pause ou au nettoyage de l’historique ; il n’est pas exporté. L’historique a des plafonds techniques de 500 identifiants d’applications et 64 observations de validations par jour local. Ces plafonds concernent l’historique, pas le nombre d’applications qu’Ultra permet de suivre ni les règles de validation des défis.",
                    "To compare consecutive observations no more than 30 seconds apart, Selkie keeps the last technical reading: time, time zone, identifiers and usage durations of selected apps. This reading is replaced at the next observation and deleted when tracking is paused or history is cleaned; it is not exported. History has technical limits of 500 app identifiers and 64 check-in observations per local day. These limits concern history, not the number of apps Ultra allows you to track or challenge check-in rules."
                ),
                tr(
                    "Premium et Ultra peuvent conserver jusqu’à 20 plages horaires avec leur nom, l’application, les jours, les heures, la limite et l’état activé. Une plage modifie la limite du compteur en cours, sans remettre son temps à zéro ; un verrouillage de 24 h continue même après sa fin. À 0 point, créer, modifier, activer ou supprimer une plage reste bloqué. En Gratuit, les plages sont conservées mais ne s’appliquent pas.",
                    "Premium and Ultra can save up to 20 time slots with their name, app, days, times, limit and enabled state. A slot changes the current counter’s allowance without resetting its usage; a 24-hour lock continues even after the slot ends. At 0 points, creating, editing, enabling or deleting a slot stays blocked. On Free, slots are kept but do not apply."
                ),
                tr(
                    "Tu peux sélectionner jusqu’à 50 favoris de conseils rédigés et préparés dès l’offre Gratuit. Ils conservent la référence du conseil préparé ou la réponse rédigée sélectionnée, sa langue d’origine et son offre minimale, sans saisie brute, phrase non comprise ou phrase sensible. Les réponses payantes des conseils préparés ne sont pas enregistrées dans les favoris ni recopiées dans l’export. Un favori réservé à une offre supérieure reste enregistré mais sa lecture est bloquée après un passage à une offre inférieure. Les programmes Ultra Sommeil (7 jours), Études (14 jours) et Réseaux sociaux (30 jours) conservent l’identifiant du programme choisi et les dates de validations manuelles, au maximum une validation par jour entre tous les programmes. Ils ne donnent pas de points supplémentaires.",
                    "You can select up to 50 favourites of authored, prepared advice starting on Free. They retain the prepared tip reference or selected authored reply, its original language and minimum plan, with no raw input, unrecognised message or sensitive message. Paid answers to prepared tips are not stored in favourites or copied into the export. A favourite requiring a higher plan stays saved but cannot be read after a downgrade. The Ultra Sleep (7 days), Studies (14 days) and Social media (30 days) programmes keep the chosen programme identifier and manual check-in dates, with at most one check-in per day across all programmes. They award no extra points."
                ),
                tr(
                    "Le widget Premium ou Ultra affiche tes points et un défi proposé sur l’écran d’accueil Android. Ces informations peuvent être vues par toute personne ayant accès à cet écran. Pour l’ajouter, appuie longuement sur l’écran d’accueil puis Widgets → Selkie ; tu peux le retirer depuis le lanceur. Il ne crée aucune transmission réseau.",
                    "The Premium or Ultra widget displays your points and a suggested challenge on the Android home screen. Anyone with access to that screen may see this information. To add it, long-press the home screen, then choose Widgets → Selkie; you can remove it from your launcher. It creates no network transmission."
                ),
                tr(
                    "La préférence de langue français/anglais et le choix d’apparence clair, nuit ou comme le téléphone sont également conservés uniquement sur l’appareil. Changer la langue ou l’apparence n’envoie aucune donnée à un serveur.",
                    "Your French/English language preference and light, night or phone-controlled appearance choice are also stored only on your device. Changing language or appearance sends no data to a server."
                )
            )),
            PrivacySection(tr("Pourquoi, et sur quelle base ?", "Why is data used, and on what basis?"), listOf(
                tr(
                    "Ces données servent uniquement à fournir le service que tu demandes : choisir des limites et des plages horaires, mesurer leur utilisation, afficher des alertes et des écrans de blocage, présenter ta progression dans le calendrier, les bilans et le widget, retrouver tes conseils favoris, suivre les programmes choisis, planifier les pauses que tu confirmes et reprendre tes coloriages. La base juridique des traitements nécessaires à ce service est son exécution à ta demande, au titre de l’article 6, paragraphe 1, point b du RGPD.",
                    "This data is used only to provide the service you request: setting limits and time slots, measuring usage, displaying alerts and blocking screens, presenting progress in the calendar, summaries and widget, finding favourite advice, following selected programmes, planning breaks you confirm and resuming your colouring pages. The legal basis for processing necessary to provide this service is performance of the service at your request, under Article 6(1)(b) of the GDPR."
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
                    "Les réglages, les plages horaires, les favoris et la progression des programmes restent sur l’appareil jusqu’à leur suppression disponible dans Selkie, l’effacement global ou la désinstallation. L’historique du calendrier, les bilans et leur export utilisent les 90 jours locaux les plus récents. Le nettoyage des entrées plus anciennes reprend au prochain usage de l’application, changement de jour traité ou observation, et leur suppression du stockage dépend de la réussite de l’écriture ; aucun minuteur ne garantit leur suppression physique pendant que l’application ne fonctionne pas. Retirer une application du suivi supprime son entrée, sa limite et son verrou, mais pas les observations encore présentes dans cette fenêtre ; les points et totaux de défis restent jusqu’à l’effacement complet. Les compteurs en cours sont recalculés depuis les événements Android et leurs références, séparément de cet historique.",
                    "Settings, time slots, favourites and programme progress remain on your device until you use their available deletion controls, reset all data or uninstall the app. Calendar history, summaries and their export use the most recent 90 local days. Cleaning older entries resumes at the next app use, processed day change or observation, and deletion from storage depends on the write succeeding; no timer guarantees their physical deletion while the app is not running. Removing an app from tracking deletes its entry, limit and lock, but not observations still within this window; points and challenge totals remain until a full data reset. Current counters are recalculated from Android events and their references, separately from this history."
                ),
                tr(
                    "Effacer les données supprime toutes les préférences privées maree de Selkie, dont les applications suivies, les verrous, les points, le choix d’offre de démonstration, les défis personnels avec leurs étapes et leurs dates, l’historique des 90 jours, les tâches du calendrier y compris les pauses confirmées du dé, les coloriages à carreaux et les illustrations enregistrés, les plages horaires, les favoris, les programmes et leurs validations, ainsi que les phrases non comprises du Coach et leur option de conservation. À la prochaine utilisation, la progression repart avec les valeurs initiales. Les alertes sont retirées et le widget ne présente plus les anciennes données. Si un effacement ne peut pas être confirmé, un message t’invite à réessayer. Cette action ne supprime pas les statistiques d’Android et ne révoque pas automatiquement les autorisations du téléphone. Elle efface aussi la préférence de langue : Selkie revient à l’anglais si le téléphone est en anglais, et au français sinon. L’apparence revient à Clair.",
                    "Deleting data clears all Selkie’s private maree preferences, including tracked apps, locks, points, the selected demonstration plan, personal challenges with their steps and dates, the 90-day history, calendar tasks including confirmed dice breaks, saved grid pages and illustrations, time slots, favourites, programmes and their check-ins, and saved unrecognised Coach messages and their saving preference. On your next use, progress starts again from the initial values. Alerts are removed and the widget no longer shows old data. If deletion cannot be confirmed, a message asks you to try again. This action does not delete Android statistics or automatically revoke your phone’s permissions. It also clears your language preference: Selkie returns to English if the phone’s language is English, and to French otherwise. Appearance returns to Light."
                ),
                tr(
                    "Les dates de validation de la série de défis proposés sont effacées lors de son renouvellement tous les 21 jours ; les observations de validations déjà enregistrées dans l’historique restent dans sa fenêtre de 90 jours. Le nombre total de défis terminés et les points restent jusqu’à l’effacement complet. Les défis personnels et leurs dates restent jusqu’à leur suppression individuelle ou à l’effacement complet ; ils ne sont pas réinitialisés par le changement de série.",
                    "Check-in dates for the current built-in challenge set are deleted when it renews every 21 days; check-in observations already recorded in history remain within its 90-day window. The total number of completed challenges and points remain until a full data reset. Personal challenges and their dates remain until you delete them individually or reset all data; they are not reset when the set changes."
                ),
                tr(
                    "Les programmes conservent leur date de début et au maximum 7, 14 ou 30 dates de validation selon le programme, jusqu’à son retrait, sa réinitialisation ou l’effacement global. La dernière date de validation commune reste enregistrée même après un changement, un retrait ou une réinitialisation de programme pour faire respecter la limite d’une validation par jour ; l’effacement global la supprime aussi. Après une sortie d’Ultra, la progression reste conservée mais sa poursuite est bloquée.",
                    "Programmes keep their start date and at most 7, 14 or 30 check-in dates depending on the programme, until you remove it, reset it or erase all data. The shared last check-in date remains saved even after switching, removing or resetting a programme to enforce the once-per-day limit; erasing all data removes it too. After leaving Ultra, progress remains saved but cannot be continued."
                ),
                tr(
                    "La conversation du Coach local reste uniquement en mémoire pendant l’ouverture de son onglet. Les phrases non comprises conservées sur option restent jusqu’à leur effacement, la désactivation de l’option, l’effacement global ou la désinstallation. Au-delà de cinquante phrases, les plus anciennes sont retirées. Effacer seulement les phrases garde ton choix de conservation pour les futures questions ; l’effacement global le remet sur désactivé.",
                    "The local Coach conversation stays only in memory while its tab is open. Unrecognised messages saved through the optional setting remain until you delete them, turn saving off, reset all data or uninstall the app. Beyond fifty messages, the oldest are removed. Deleting only the messages keeps your saving preference for future questions; a full data reset turns it off."
                )
            )),
            PrivacySection(tr("Stockage local et traceurs", "Local storage and trackers"), listOf(
                tr(
                    "Cette version native n’utilise aucun cookie ni traceur publicitaire, SDK publicitaire, mesure d’audience ou identifiant de suivi publicitaire. Le même stockage privé maree conserve les données fonctionnelles des options demandées ; l’enregistrement facultatif des questions non comprises reste séparé et désactivé par défaut. Une bannière de cookies n’est pas ajoutée pour ces seules fonctions locales.",
                    "This native version uses no cookies, advertising trackers, advertising SDK, audience analytics or advertising tracking identifier. The same private maree storage keeps functional data for the features you request; optional saving of unrecognised questions remains separate and off by default. No cookie banner is added for these local features alone."
                ),
                tr(
                    "La version de test hors ligne ne déclare pas la permission Internet. Les liens que tu choisis d’ouvrir, le service de messagerie et le fournisseur de fichiers que tu sélectionnes sont des applications ou services distincts, avec leurs propres informations de confidentialité. Ajouter des traceurs ou un service connecté demanderait une nouvelle analyse et, lorsque nécessaire, un consentement préalable.",
                    "The offline test version does not declare Internet permission. Links you choose to open, your email app and the file provider you select are separate apps or services with their own privacy information. Adding trackers or an online service would require a new assessment and, where required, prior consent."
                )
            )),
            PrivacySection(tr("Exporter tes réglages et ta progression", "Export your settings and progress"), listOf(
                tr(
                    "Le bouton Exporter mes données prépare un fichier JSON local de tes réglages de suivi, points, défis et progression. Il inclut les observations structurées de l’historique des 90 jours, les tâches personnelles du calendrier dont les pauses confirmées du dé, les coloriages enregistrés, les règles des plages horaires, les références ou conseils favoris avec leur langue et leur offre minimale, ainsi que les identifiants de programmes et les dates de validations. Tu choisis où l’enregistrer avec Android. Le dernier relevé technique, les résultats non confirmés du dé, les saisies et phrases brutes du Coach, les jetons de connexion et les adresses e-mail sont exclus. Aucun fichier n’est envoyé automatiquement à l’éditeur.",
                    "Export my data prepares a local JSON file of your tracking settings, points, challenges and progress. It includes structured observations within the 90-day history, personal calendar tasks including confirmed dice breaks, saved colouring pages, time-slot rules, favourite references or authored advice with their language and minimum plan, and programme identifiers and check-in dates. You choose where to save it using Android. The last technical reading, unconfirmed dice results, raw Coach input and messages, sign-in tokens and email addresses are excluded. No file is automatically sent to the publisher."
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
            PrivacySection(tr("Si Selkie évolue", "If Selkie changes"), listOf(
                tr(
                    "Une synchronisation, une publicité ou un paiement nécessiterait une nouvelle analyse et une information mise à jour avant activation. Cette politique décrit seulement les fonctions de la version actuelle.",
                    "Synchronisation, advertising or payments would require a new assessment and updated information before activation. This policy describes only the features of the current version."
                )
            ))
        )
}
