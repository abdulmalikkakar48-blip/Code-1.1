package com.maree.app

/**
 * Forty-eight distinct situations for the prepared coach library.
 * Each situation supplies its own concrete actions; spelling variants never add entries.
 */
val coachLibraryProfiles: List<CoachLibraryProfile> = listOf(
    CoachLibraryProfile(
        id = "tiktok_short_videos", sectionId = "apps", parentTopicId = "tiktok",
        label = profileText("Les vidéos courtes sur TikTok", "Short videos on TikTok"),
        situation = profileText("Une vidéo courte en appelle une autre, sans point d’arrêt évident.", "One short video leads to another, with no obvious stopping point."),
        smallAction = profileText("Ferme TikTok après la vidéo en cours et pose le téléphone, écran vers le bas.", "Close TikTok after the current video and put your phone face down."),
        alternative = profileText("Regarde par la fenêtre et remarque trois choses autour de toi.", "Look out of the window and notice three things around you."),
        barrier = profileText("Le raccourci reste très visible et la prochaine vidéo arrive sans vraie pause.", "The shortcut stays highly visible and the next video arrives without a real pause."),
        goal = profileText("Choisis un seul créneau de vidéos, avec une heure de fin décidée avant d’ouvrir l’application.", "Choose one video session with an end time decided before opening the app.")
    ),
    CoachLibraryProfile(
        id = "instagram_comparison", sectionId = "apps", parentTopicId = "instagram",
        label = profileText("La comparaison sur Instagram", "Comparison on Instagram"),
        situation = profileText("Les photos et les stories peuvent t’amener à comparer ta journée à celle des autres.", "Photos and stories can make you compare your day with other people's lives."),
        smallAction = profileText("Quitte le fil et note une chose concrète que tu apprécies dans ta propre journée.", "Leave the feed and write down one concrete thing you appreciate about your own day."),
        alternative = profileText("Fais une petite activité qui te plaît sans la photographier ni la publier.", "Do a small activity you enjoy without photographing or posting it."),
        barrier = profileText("Les publications choisies et retouchées peuvent sembler représenter toute la vie des autres.", "Selected and edited posts can appear to represent other people's entire lives."),
        goal = profileText("Ouvre Instagram pour une intention précise, comme répondre à un ami, puis quitte le fil.", "Open Instagram for a specific purpose, such as replying to a friend, then leave the feed.")
    ),
    CoachLibraryProfile(
        id = "youtube_autoplay", sectionId = "apps", parentTopicId = "youtube",
        label = profileText("La lecture automatique sur YouTube", "Autoplay on YouTube"),
        situation = profileText("La vidéo suivante peut démarrer avant que tu aies choisi de continuer.", "The next video can start before you have chosen to keep watching."),
        smallAction = profileText("Désactive la lecture automatique et ferme la liste des vidéos recommandées.", "Turn off autoplay and close the list of recommended videos."),
        alternative = profileText("Résume en une phrase ce que tu viens de voir, puis reprends une activité prévue.", "Summarise what you just watched in one sentence, then return to a planned activity."),
        barrier = profileText("La lecture automatique et les recommandations proposent une suite avant que tu choisisses de t’arrêter.", "Autoplay and recommendations offer a continuation before you choose to stop."),
        goal = profileText("Regarde une vidéo choisie à l’avance plutôt qu’une suite de recommandations.", "Watch one video chosen in advance rather than a chain of recommendations.")
    ),
    CoachLibraryProfile(
        id = "snapchat_streak_pressure", sectionId = "apps", parentTopicId = "snapchat",
        label = profileText("Les flammes sur Snapchat", "Snapchat streaks"),
        situation = profileText("Conserver une série de flammes peut te donner l’impression de devoir ouvrir Snapchat sans envie réelle.", "Keeping a streak can make you feel you must open Snapchat even when you do not really want to."),
        smallAction = profileText("Demande-toi si tu veux parler à cette personne ou seulement conserver un compteur.", "Ask yourself whether you want to talk to that person or only keep a counter going."),
        alternative = profileText("Privilégie un échange que tu choisis vraiment, à un moment qui te convient.", "Choose a conversation you actually want, at a time that suits you."),
        barrier = profileText("Le compteur de flammes et les attentes des amis peuvent peser davantage que ton envie d’échanger.", "The streak counter and friends' expectations can weigh more than your desire to talk."),
        goal = profileText("Garde des échanges utiles ou agréables, sans laisser un compteur décider de tes connexions.", "Keep useful or enjoyable conversations without letting a counter decide when you go online.")
    ),
    CoachLibraryProfile(
        id = "whatsapp_repeated_checks", sectionId = "apps", parentTopicId = "whatsapp",
        label = profileText("Les vérifications répétées de WhatsApp", "Repeated WhatsApp checks"),
        situation = profileText("Tu peux rouvrir la même conversation plusieurs fois en attendant une réponse.", "You may reopen the same conversation several times while waiting for a reply."),
        smallAction = profileText("Ferme la conversation et choisis une prochaine heure de consultation.", "Close the conversation and choose the next time you will check it."),
        alternative = profileText("Termine une petite tâche indépendante pendant que l’autre personne répond à son rythme.", "Finish a small independent task while the other person replies at their own pace."),
        barrier = profileText("L’attente d’une réponse peut donner envie de vérifier même sans nouveau message.", "Waiting for a reply can make you want to check even when there is no new message."),
        goal = profileText("Consulte les messages par créneaux plutôt que de vérifier la même discussion sans nouveauté.", "Check messages in sessions rather than reopening the same chat with nothing new.")
    ),
    CoachLibraryProfile(
        id = "games_extra_round", sectionId = "apps", parentTopicId = "games",
        label = profileText("La partie de plus", "One more game"),
        situation = profileText("Le bouton de revanche peut prolonger une session alors que tu pensais avoir terminé.", "The rematch button can extend a session you thought you had finished."),
        smallAction = profileText("À la fin de la partie en cours, quitte le salon avant de lancer une nouvelle partie.", "At the end of the current game, leave the lobby before starting another round."),
        alternative = profileText("Lève-toi et détends doucement les mains avant de choisir la suite de ta journée.", "Stand up and gently relax your hands before deciding what to do next."),
        barrier = profileText("Une revanche proposée immédiatement ou un groupe qui continue peut rendre l’arrêt difficile.", "An immediate rematch or a group that keeps playing can make stopping difficult."),
        goal = profileText("Décide du nombre de parties ou de l’heure de fin avant de commencer à jouer.", "Decide the number of rounds or the finishing time before you start playing.")
    ),

    CoachLibraryProfile(
        id = "wake_up_notifications", sectionId = "moments", parentTopicId = "wake_up",
        label = profileText("Le téléphone dès le réveil", "Checking your phone on waking"),
        situation = profileText("Les premières notifications peuvent prendre la place de ton début de journée.", "The first notifications can take over the start of your day."),
        smallAction = profileText("Pose le téléphone et ouvre les rideaux avant de consulter le fil d’actualité.", "Put your phone down and open the curtains before checking any feeds."),
        alternative = profileText("Prépare un verre d’eau ou les vêtements de la journée avant les réseaux sociaux.", "Get a glass of water or prepare your clothes for the day before using social media."),
        barrier = profileText("Le téléphone sert de réveil et présente les notifications dès que tu le prends.", "Your phone serves as an alarm and shows notifications as soon as you pick it up."),
        goal = profileText("Commence par un geste de ta routine du matin avant ta première consultation.", "Start with one step of your morning routine before your first phone check.")
    ),
    CoachLibraryProfile(
        id = "breakfast_scroll", sectionId = "moments", parentTopicId = "meals",
        label = profileText("Le défilement au petit-déjeuner", "Scrolling at breakfast"),
        situation = profileText("Le petit-déjeuner peut se transformer en consultation de nouvelles et de vidéos.", "Breakfast can turn into a session of news and videos."),
        smallAction = profileText("Éloigne le téléphone de la table pour les premières bouchées.", "Move your phone away from the table for the first few bites."),
        alternative = profileText("Prends le temps de goûter ton repas ou de discuter avec la personne présente.", "Take time to taste your food or talk to the person with you."),
        barrier = profileText("Le téléphone reste devant ton assiette et se consulte entre les bouchées.", "Your phone stays in front of your plate and gets checked between bites."),
        goal = profileText("Choisis un petit-déjeuner sans fil d’actualité, même si le reste de la matinée reste connecté.", "Choose a breakfast without feeds, even if the rest of your morning is online.")
    ),
    CoachLibraryProfile(
        id = "commute_waiting", sectionId = "moments", parentTopicId = "boredom",
        label = profileText("L’attente dans les transports", "Waiting on public transport"),
        situation = profileText("À l’arrêt ou dans les transports, quelques minutes d’attente peuvent lancer un long défilement.", "At a stop or on public transport, a few minutes of waiting can start a long scroll."),
        smallAction = profileText("Vérifie ton trajet une fois, puis verrouille le téléphone pendant une minute.", "Check your route once, then lock your phone for a minute."),
        alternative = profileText("Observe les environs ou lis une courte page, en restant attentif à ton trajet.", "Notice your surroundings or read a short page while staying aware of your journey."),
        barrier = profileText("Aucune petite activité n’est prête pour l’attente et le téléphone reste le choix le plus immédiat.", "No small waiting activity is ready and your phone remains the most immediate choice."),
        goal = profileText("Garde une partie de l’attente sans réseaux, tout en restant disponible pour les informations de transport.", "Keep part of the wait free from social media while staying available for travel information.")
    ),
    CoachLibraryProfile(
        id = "after_school_decompression", sectionId = "moments", parentTopicId = "breaks",
        label = profileText("Le retour après les cours", "Getting home after class"),
        situation = profileText("En rentrant, tu peux ouvrir une application pour souffler et y rester plus longtemps que prévu.", "When you get home, you may open an app to unwind and stay longer than planned."),
        smallAction = profileText("Pose ton sac et prends deux minutes de pause avant d’ouvrir une application.", "Put your bag down and take a two-minute break before opening an app."),
        alternative = profileText("Prends une collation ou change de vêtements pour marquer le passage à la maison.", "Have a snack or change clothes to mark the transition home."),
        barrier = profileText("Le téléphone devient le premier geste de détente avant même que tu choisisses ta pause.", "Your phone becomes the first unwinding action before you even choose your break."),
        goal = profileText("Détends-toi d’abord avec une pause choisie, puis décide de la place des écrans dans ta soirée.", "Unwind first with a chosen break, then decide where screens fit into your evening.")
    ),
    CoachLibraryProfile(
        id = "evening_second_screen", sectionId = "moments", parentTopicId = "evening",
        label = profileText("Le deuxième écran sur le canapé", "A second screen on the sofa"),
        situation = profileText("Tu peux faire défiler le téléphone en regardant autre chose, sans profiter vraiment de l’un ou de l’autre.", "You may scroll on your phone while watching something else without really enjoying either."),
        smallAction = profileText("Choisis l’écran que tu souhaites regarder et pose l’autre pendant deux minutes.", "Choose the screen you want to watch and put the other one down for two minutes."),
        alternative = profileText("Discute du programme ou installe-toi avec une boisson sans garder le téléphone en main.", "Talk about the programme or settle down with a drink without holding your phone."),
        barrier = profileText("Le téléphone reste dans ta main pendant que l’autre écran fonctionne.", "Your phone stays in your hand while the other screen is on."),
        goal = profileText("Fais de la soirée un moment choisi, avec un seul écran à la fois quand tu en utilises un.", "Make the evening a chosen activity, using one screen at a time when you use one.")
    ),
    CoachLibraryProfile(
        id = "bedtime_videos", sectionId = "moments", parentTopicId = "sleep",
        label = profileText("Les vidéos avant de dormir", "Videos before sleep"),
        situation = profileText("Une vidéo au lit peut en appeler plusieurs et repousser l’heure à laquelle tu voulais te coucher.", "One video in bed can lead to several and delay the bedtime you had planned."),
        smallAction = profileText("Ferme la vidéo et place le téléphone sur son point de charge hors du lit.", "Close the video and put your phone at its charging spot away from the bed."),
        alternative = profileText("Lis quelques lignes sur papier ou prépare tranquillement les affaires du lendemain.", "Read a few lines on paper or calmly prepare your things for tomorrow."),
        barrier = profileText("Le téléphone est dans le lit et aucune fin de session n’a été choisie avant la fatigue.", "Your phone is in bed and no session end was chosen before you became tired."),
        goal = profileText("Protège le début de ta routine du coucher plutôt que d’attendre d’être trop fatigué pour décider.", "Protect the start of your bedtime routine instead of waiting until you are too tired to decide.")
    ),

    CoachLibraryProfile(
        id = "boredom_empty_minutes", sectionId = "emotions", parentTopicId = "boredom",
        label = profileText("Les minutes vides et l’ennui", "Empty minutes and boredom"),
        situation = profileText("Un moment sans activité peut suffire à ouvrir le téléphone sans raison précise.", "A moment without an activity can be enough to open your phone with no clear reason."),
        smallAction = profileText("Attends une minute avant d’ouvrir une application et nomme ce que tu voudrais faire.", "Wait one minute before opening an app and name what you would like to do."),
        alternative = profileText("Essaie un petit jeu sur papier, un rangement rapide ou quelques lignes de lecture.", "Try a small paper game, a quick tidy-up or a few lines of reading."),
        barrier = profileText("Aucune autre idée n’est prête lorsque quelques minutes se libèrent.", "No other idea is ready when a few free minutes appear."),
        goal = profileText("Accepte un petit moment vide sans le remplir automatiquement avec un fil d’actualité.", "Allow a small empty moment without automatically filling it with a feed.")
    ),
    CoachLibraryProfile(
        id = "stress_avoidance", sectionId = "emotions", parentTopicId = "stress",
        label = profileText("Le défilement pour éviter une pression", "Scrolling to avoid pressure"),
        situation = profileText("Quand une tâche te pèse, le téléphone peut offrir une distraction qui laisse la tâche en attente.", "When a task feels heavy, your phone can offer a distraction that leaves the task waiting."),
        smallAction = profileText("Pose le téléphone et écris seulement la toute prochaine action de la tâche.", "Put your phone down and write only the very next step of the task."),
        alternative = profileText("Fais une courte pause à la fenêtre, puis choisis un début assez petit pour être faisable.", "Take a short break by the window, then choose a start small enough to be manageable."),
        barrier = profileText("La tâche paraît trop grande ou trop vague, tandis que la distraction est immédiatement disponible.", "The task feels too large or vague while the distraction is immediately available."),
        goal = profileText("Fais avancer une seule étape avant de décider si tu as besoin d’une nouvelle pause.", "Move one step forward before deciding whether you need another break.")
    ),
    CoachLibraryProfile(
        id = "loneliness_feed", sectionId = "emotions", parentTopicId = "loneliness",
        label = profileText("Le fil d’actualité quand tu te sens seul", "The feed when you feel lonely"),
        situation = profileText("Voir beaucoup de publications ne donne pas toujours le sentiment d’avoir partagé un vrai moment.", "Seeing many posts does not always feel like sharing a real moment."),
        smallAction = profileText("Choisis une personne de confiance à qui proposer un échange quand cela vous convient.", "Choose a trusted person to invite for a conversation when it suits both of you."),
        alternative = profileText("Rejoins une activité locale ou un espace commun accessible, si tu en as envie.", "Join a local activity or an accessible shared space if you feel like it."),
        barrier = profileText("Le fil montre beaucoup de personnes, mais ne crée pas forcément un échange avec toi.", "The feed shows many people but does not necessarily create an exchange with you."),
        goal = profileText("Privilégie un échange choisi ou une présence réelle plutôt qu’une longue suite de publications.", "Prefer a chosen conversation or real company over a long stream of posts.")
    ),
    CoachLibraryProfile(
        id = "fomo_updates", sectionId = "emotions", parentTopicId = "fomo",
        label = profileText("La peur de rater les nouveautés", "Fear of missing updates"),
        situation = profileText("Actualiser sans cesse peut donner l’impression que chaque nouveauté demande une réponse immédiate.", "Refreshing constantly can make every update feel as if it needs an immediate response."),
        smallAction = profileText("Choisis une heure de prochaine consultation et ferme le fil jusque-là.", "Choose your next checking time and close the feed until then."),
        alternative = profileText("Note les deux informations vraiment importantes pour toi, sans chercher toutes les nouveautés.", "Write down the two kinds of information that really matter to you without seeking every update."),
        barrier = profileText("Le flux se renouvelle sans fin et chaque suggestion peut sembler importante sur le moment.", "The feed keeps renewing and every suggestion can seem important in the moment."),
        goal = profileText("Reste informé dans un créneau choisi, sans essayer de suivre tout ce qui apparaît.", "Stay informed in a chosen time slot without trying to follow everything that appears.")
    ),
    CoachLibraryProfile(
        id = "tired_automatic_scroll", sectionId = "emotions", parentTopicId = "tiredness",
        label = profileText("Le défilement automatique quand tu es fatigué", "Automatic scrolling when tired"),
        situation = profileText("Quand tu es fatigué, continuer à faire défiler peut sembler plus facile que décider de t’arrêter.", "When you are tired, continuing to scroll can feel easier than deciding to stop."),
        smallAction = profileText("Verrouille le téléphone et prends une minute assis sans contenu à regarder.", "Lock your phone and sit for a minute without content to watch."),
        alternative = profileText("Choisis un repos simple, comme t’installer au calme ou préparer ton coucher.", "Choose simple rest, such as settling somewhere quiet or preparing for bed."),
        barrier = profileText("Aucun geste d’arrêt n’est prêt quand la fatigue rend les décisions moins faciles.", "No stopping step is ready when tiredness makes decisions less easy."),
        goal = profileText("Choisis du repos quand tu en as besoin, plutôt qu’une session qui se prolonge sans intention.", "Choose rest when you need it rather than a session that continues without intention.")
    ),
    CoachLibraryProfile(
        id = "frustration_reopening", sectionId = "emotions", parentTopicId = "relapse",
        label = profileText("Le téléphone après une journée frustrante", "Your phone after a frustrating day"),
        situation = profileText("Après une contrariété, tu peux chercher un changement d’humeur en rouvrant plusieurs applications.", "After an annoyance, you may look for a change of mood by reopening several apps."),
        smallAction = profileText("Pose le téléphone et nomme la contrariété en une phrase, sans te juger.", "Put your phone down and name the annoyance in one sentence without judging yourself."),
        alternative = profileText("Fais une activité familière et courte, comme dessiner quelques traits ou ranger un objet.", "Do a short familiar activity, such as drawing a few lines or putting one thing away."),
        barrier = profileText("Ouvrir une application est devenu le réflexe le plus rapide après une contrariété.", "Opening an app has become the quickest reflex after an annoyance."),
        goal = profileText("Reprends la main sur la prochaine action, même si la journée ne s’est pas passée comme prévu.", "Take charge of the next action even if the day did not go as planned.")
    ),

    CoachLibraryProfile(
        id = "homework_start", sectionId = "work_study", parentTopicId = "school",
        label = profileText("Commencer les devoirs", "Starting homework"),
        situation = profileText("Ouvrir le téléphone avant les devoirs peut retarder le premier exercice.", "Opening your phone before homework can delay the first exercise."),
        smallAction = profileText("Pose le téléphone et lis seulement la consigne du premier exercice.", "Put your phone down and read only the instructions for the first exercise."),
        alternative = profileText("Prépare le cahier et le matériel nécessaires avant de chercher une nouvelle distraction.", "Prepare your notebook and the materials you need before looking for another distraction."),
        barrier = profileText("Le premier exercice n’est pas encore ouvert et le téléphone est déjà dans ta main.", "The first exercise is not open yet and your phone is already in your hand."),
        goal = profileText("Commence un exercice précis avant de consulter une application de loisir.", "Start a specific exercise before checking an entertainment app.")
    ),
    CoachLibraryProfile(
        id = "revision_break", sectionId = "work_study", parentTopicId = "school",
        label = profileText("Les pauses entre deux révisions", "Breaks between study sessions"),
        situation = profileText("Une pause de révision peut durer longtemps si elle commence par un fil sans fin.", "A study break can last a long time if it starts with an endless feed."),
        smallAction = profileText("Lève-toi du bureau et laisse le téléphone à sa place pendant la première minute de pause.", "Stand up from your desk and leave your phone where it is for the first minute of the break."),
        alternative = profileText("Prends de l’eau ou fais quelques pas, puis prépare la page de révision suivante.", "Get some water or take a few steps, then prepare the next page to study."),
        barrier = profileText("La pause commence sans durée ni point de reprise, ce qui laisse le fil décider de sa fin.", "The break starts with no duration or restart point, letting the feed decide when it ends."),
        goal = profileText("Reviens à une page précise après la pause, avec une reprise facile à identifier.", "Return to a specific page after the break with a clear place to restart.")
    ),
    CoachLibraryProfile(
        id = "remote_work_tabs", sectionId = "work_study", parentTopicId = "work",
        label = profileText("Les onglets de loisir pendant le travail", "Entertainment tabs during work"),
        situation = profileText("Passer d’un document à un réseau social peut couper le fil de la tâche en cours.", "Switching from a document to social media can break the thread of your current task."),
        smallAction = profileText("Ferme un onglet de loisir et écris la prochaine phrase ou action du travail en cours.", "Close one entertainment tab and write the next sentence or action for your current work."),
        alternative = profileText("Fais une pause debout loin du clavier plutôt qu’une pause dans un autre fil.", "Take a standing break away from the keyboard instead of a break in another feed."),
        barrier = profileText("Les onglets de loisir restent ouverts juste à côté des documents utiles.", "Entertainment tabs stay open right next to useful documents."),
        goal = profileText("Termine un petit livrable identifiable avant de changer d’activité.", "Finish a small, identifiable piece of work before changing activities.")
    ),
    CoachLibraryProfile(
        id = "work_message_interruptions", sectionId = "work_study", parentTopicId = "notifications",
        label = profileText("Les messages qui interrompent le travail", "Messages interrupting work"),
        situation = profileText("Un message peut interrompre une tâche même quand il ne demande pas de réponse immédiate.", "A message can interrupt a task even when it does not require an immediate reply."),
        smallAction = profileText("Note ton point de reprise avant de consulter un message que tu dois traiter.", "Note where to restart before checking a message you need to handle."),
        alternative = profileText("Regroupe les réponses peu urgentes dans un créneau compatible avec tes obligations.", "Group less urgent replies into a time slot that fits your responsibilities."),
        barrier = profileText("L’urgence réelle des messages n’est pas claire et tu te sens obligé de consulter chaque alerte.", "The actual urgency of messages is unclear and you feel obliged to check every alert."),
        goal = profileText("Protège une courte période de travail tout en restant joignable selon tes obligations réelles.", "Protect a short work period while remaining reachable for your actual responsibilities.")
    ),
    CoachLibraryProfile(
        id = "reading_distracted", sectionId = "work_study", parentTopicId = "school",
        label = profileText("Lire sans vérifier le téléphone", "Reading without checking your phone"),
        situation = profileText("Vérifier le téléphone entre deux paragraphes peut te faire perdre le sens de la lecture.", "Checking your phone between paragraphs can make you lose the thread of your reading."),
        smallAction = profileText("Pose le téléphone et lis un paragraphe entier avant de changer d’activité.", "Put your phone down and read one full paragraph before changing activities."),
        alternative = profileText("Note un mot-clé de la page pour garder le fil de ce que tu lis.", "Write down one keyword from the page to keep track of what you are reading."),
        barrier = profileText("Le téléphone reste à côté du texte et interrompt la lecture avant la fin d’un paragraphe.", "Your phone stays beside the text and interrupts reading before a paragraph ends."),
        goal = profileText("Lis une petite section choisie, puis décide consciemment de la pause suivante.", "Read a small chosen section, then consciously decide on your next break.")
    ),
    CoachLibraryProfile(
        id = "group_study", sectionId = "work_study", parentTopicId = "school",
        label = profileText("Réviser à plusieurs", "Studying with others"),
        situation = profileText("Dans un groupe de révision, un seul téléphone peut détourner plusieurs personnes de l’exercice.", "In a study group, one phone can distract several people from the exercise."),
        smallAction = profileText("Propose de poser les téléphones pendant la prochaine question de l’exercice.", "Suggest putting phones down for the next exercise question."),
        alternative = profileText("Explique une réponse à une autre personne ou demande-lui d’expliquer son raisonnement.", "Explain an answer to another person or ask them to explain their reasoning."),
        barrier = profileText("Le groupe n’a pas distingué les recherches utiles des consultations de loisir.", "The group has not distinguished useful research from entertainment checks."),
        goal = profileText("Terminez un exercice commun avant une pause de consultation choisie ensemble.", "Finish one shared exercise before a checking break chosen together.")
    ),

    CoachLibraryProfile(
        id = "repeated_push_alerts", sectionId = "environment", parentTopicId = "notifications",
        label = profileText("Les notifications répétées", "Repeated notifications"),
        situation = profileText("Les sons, vibrations et badges peuvent attirer ton attention sans que tu aies choisi une consultation.", "Sounds, vibrations and badges can draw your attention without you choosing to check."),
        smallAction = profileText("Coupe une catégorie de notifications non essentielles dans les réglages du téléphone.", "Turn off one category of non-essential notifications in your phone settings."),
        alternative = profileText("Consulte cette application à une heure choisie plutôt qu’à chaque signal.", "Check that app at a chosen time rather than at every alert."),
        barrier = profileText("Les promotions et recommandations utilisent les mêmes signaux que des messages réellement utiles.", "Promotions and recommendations use the same signals as genuinely useful messages."),
        goal = profileText("Réduis les interruptions inutiles sans couper les moyens de contact dont tu as besoin.", "Reduce unnecessary interruptions without cutting off the contact methods you need.")
    ),
    CoachLibraryProfile(
        id = "home_screen_shortcuts", sectionId = "environment", parentTopicId = "distance",
        label = profileText("Les raccourcis trop visibles", "Shortcuts that are too visible"),
        situation = profileText("Une icône placée au premier écran peut être ouverte par habitude avant même d’y réfléchir.", "An icon on the first screen can be opened out of habit before you even think about it."),
        smallAction = profileText("Déplace une application de loisir hors de la première page d’accueil.", "Move one entertainment app off the first home-screen page."),
        alternative = profileText("Place à cet endroit un outil utile ou laisse simplement un espace vide.", "Put a useful tool there or simply leave an empty space."),
        barrier = profileText("L’icône de loisir est visible à chaque déverrouillage, avant les outils dont tu as besoin.", "The entertainment icon is visible at every unlock before the tools you need."),
        goal = profileText("Fais de l’ouverture de cette application un choix explicite, plutôt qu’un premier geste automatique.", "Make opening that app an explicit choice rather than an automatic first action.")
    ),
    CoachLibraryProfile(
        id = "phone_on_desk", sectionId = "environment", parentTopicId = "distance",
        label = profileText("Le téléphone posé sur le bureau", "Your phone on the desk"),
        situation = profileText("Un téléphone visible près du travail peut inviter à le vérifier entre deux petites actions.", "A visible phone near your work can invite you to check it between small actions."),
        smallAction = profileText("Place le téléphone hors de ta vue, à un endroit accessible si tu en as réellement besoin.", "Put your phone out of sight somewhere accessible if you actually need it."),
        alternative = profileText("Garde une feuille pour noter ce que tu voulais chercher, puis consulte-la à la pause.", "Keep a sheet of paper to note what you wanted to look up, then check it during your break."),
        barrier = profileText("Le téléphone est visible et accessible dès qu’une petite difficulté apparaît.", "Your phone is visible and within reach whenever a small difficulty appears."),
        goal = profileText("Fais une courte tâche sans prendre le téléphone entre chaque étape.", "Complete a short task without picking up your phone between every step.")
    ),
    CoachLibraryProfile(
        id = "bedside_charger", sectionId = "environment", parentTopicId = "bedroom",
        label = profileText("Le chargeur près du lit", "The charger beside the bed"),
        situation = profileText("Charger le téléphone à portée de main peut prolonger son utilisation une fois au lit.", "Charging your phone within reach can extend your use once you are in bed."),
        smallAction = profileText("Choisis un point de charge stable éloigné de l’oreiller, en gardant le réveil et les appels nécessaires accessibles.", "Choose a stable charging spot away from your pillow while keeping your alarm and necessary calls accessible."),
        alternative = profileText("Garde près du lit un livre ou un autre objet de ta routine calme.", "Keep a book or another quiet-routine item near your bed."),
        barrier = profileText("Tu comptes peut-être sur le téléphone près du lit pour le réveil ou pour des appels importants.", "You may rely on your phone beside the bed for the alarm or important calls."),
        goal = profileText("Sépare le lieu de charge et le lieu de repos autant que ton organisation le permet.", "Separate the charging place from the resting place as far as your arrangements allow.")
    ),
    CoachLibraryProfile(
        id = "grayscale_visual_pull", sectionId = "environment", parentTopicId = "grayscale",
        label = profileText("L’attrait visuel des couleurs", "The visual pull of colours"),
        situation = profileText("Les couleurs vives et les badges peuvent rendre une application difficile à ignorer.", "Bright colours and badges can make an app hard to ignore."),
        smallAction = profileText("Essaie temporairement le noir et blanc dans les réglages d’accessibilité ou de coucher de ton téléphone.", "Try grayscale temporarily in your phone's accessibility or bedtime settings."),
        alternative = profileText("Choisis une activité dont les couleurs sont dans ton environnement, comme regarder un dessin ou une plante.", "Choose an activity with colours in your surroundings, such as looking at a drawing or a plant."),
        barrier = profileText("Les couleurs et les badges attirent l’œil, mais certaines tâches demandent aussi de distinguer les couleurs.", "Colours and badges attract the eye, but some tasks also require distinguishing colours."),
        goal = profileText("Teste si un écran moins attirant t’aide à consulter avec une intention plus claire.", "Test whether a less tempting screen helps you check with a clearer intention.")
    ),
    CoachLibraryProfile(
        id = "unused_app_reinstallation", sectionId = "environment", parentTopicId = "uninstall",
        label = profileText("Une application que tu ne veux plus garder", "An app you no longer want to keep"),
        situation = profileText("Garder une application par habitude peut te faire revenir à un usage que tu avais choisi de réduire.", "Keeping an app out of habit can bring you back to use you had chosen to reduce."),
        smallAction = profileText("Écris ce que cette application t’apporte vraiment avant de décider de la garder ou de la supprimer.", "Write down what that app actually gives you before deciding to keep or remove it."),
        alternative = profileText("Choisis un autre moyen pour sa fonction utile, comme un navigateur pour une recherche ponctuelle.", "Choose another way to use its useful function, such as a browser for an occasional search."),
        barrier = profileText("Tu hésites à supprimer l’application parce qu’elle contient peut-être un accès ou des données utiles.", "You hesitate to remove the app because it may contain useful access or data."),
        goal = profileText("Garde les outils dont tu as besoin et retire seulement les applications que tu choisis de quitter.", "Keep the tools you need and remove only the apps you choose to leave.")
    ),

    CoachLibraryProfile(
        id = "family_meal", sectionId = "relationships", parentTopicId = "meals",
        label = profileText("Le repas en famille", "A family meal"),
        situation = profileText("Un téléphone à table peut interrompre la discussion sans qu’on s’en rende compte.", "A phone at the table can interrupt conversation without anyone noticing."),
        smallAction = profileText("Pose le téléphone hors de la table et pose une question simple à une personne présente.", "Put your phone off the table and ask someone present a simple question."),
        alternative = profileText("Écoute une réponse jusqu’au bout ou participe à une petite tâche du repas.", "Listen to an answer to the end or help with a small part of the meal."),
        barrier = profileText("Chacun garde son téléphone à portée de main et aucune habitude commune n’a été choisie.", "Everyone keeps a phone within reach and no shared habit has been chosen."),
        goal = profileText("Partage une partie du repas avec les personnes présentes sans consultation répétée.", "Share part of the meal with the people present without repeated checking.")
    ),
    CoachLibraryProfile(
        id = "friend_conversation", sectionId = "relationships", parentTopicId = "distance",
        label = profileText("Écouter un ami sans écran", "Listening to a friend without a screen"),
        situation = profileText("Jeter un œil au téléphone pendant une conversation peut te faire manquer ce que l’autre dit.", "Glancing at your phone during a conversation can make you miss what the other person says."),
        smallAction = profileText("Pose le téléphone et écoute la prochaine réponse de ton ami sans le reprendre.", "Put your phone down and listen to your friend's next answer without picking it up."),
        alternative = profileText("Pose une question sur ce que la personne vient de raconter.", "Ask a question about what the person just told you."),
        barrier = profileText("Un appel attendu ou des notifications peuvent diviser ton attention pendant la discussion.", "An expected call or notifications can divide your attention during the conversation."),
        goal = profileText("Accorde une présence claire à la conversation, même pour un moment court.", "Give the conversation your clear attention, even for a short time.")
    ),
    CoachLibraryProfile(
        id = "group_chat_noise", sectionId = "relationships", parentTopicId = "whatsapp",
        label = profileText("Les groupes de discussion très actifs", "Very active group chats"),
        situation = profileText("Un groupe très actif peut attirer ton attention même quand la discussion ne te concerne pas.", "A very active group can pull your attention even when the discussion does not concern you."),
        smallAction = profileText("Mets temporairement en sourdine un groupe non urgent.", "Temporarily mute one non-urgent group."),
        alternative = profileText("Retrouve les messages utiles à un moment choisi, sans relire chaque réaction.", "Catch up on useful messages at a chosen time without rereading every reaction."),
        barrier = profileText("Les réactions non urgentes arrivent en continu, au même endroit que les messages indispensables.", "Non-urgent reactions keep arriving in the same place as essential messages."),
        goal = profileText("Reste membre du groupe sans suivre chaque message au moment où il arrive.", "Stay in the group without following every message as it arrives.")
    ),
    CoachLibraryProfile(
        id = "instant_reply_pressure", sectionId = "relationships", parentTopicId = "fomo",
        label = profileText("La pression de répondre tout de suite", "Pressure to reply immediately"),
        situation = profileText("Le statut en ligne ou une confirmation de lecture peut donner l’impression de devoir répondre immédiatement.", "An online status or read receipt can make you feel you must reply immediately."),
        smallAction = profileText("Détermine si le message est réellement urgent avant de quitter ton activité.", "Decide whether the message is actually urgent before leaving your activity."),
        alternative = profileText("Réponds dans un créneau où tu peux être disponible plutôt qu’en fragments toute la journée.", "Reply in a time slot when you can be available instead of in fragments all day."),
        barrier = profileText("Tu ne sais pas si les autres attendent une réponse immédiate ou s’ils accepteraient un autre rythme.", "You do not know whether others expect an immediate reply or would accept another rhythm."),
        goal = profileText("Choisis un rythme de réponse compatible avec tes relations et tes obligations.", "Choose a reply rhythm that fits your relationships and responsibilities.")
    ),
    CoachLibraryProfile(
        id = "social_outing_recording", sectionId = "relationships", parentTopicId = "instagram",
        label = profileText("Profiter d’une sortie sans tout publier", "Enjoying an outing without posting everything"),
        situation = profileText("Photographier et publier chaque moment peut prendre la place de la sortie elle-même.", "Photographing and posting every moment can take the place of the outing itself."),
        smallAction = profileText("Après une photo choisie, range le téléphone et regarde ce qui se passe autour de toi.", "After one chosen photo, put your phone away and watch what is happening around you."),
        alternative = profileText("Garde un souvenir en décrivant un détail à une personne présente, sans le publier.", "Keep a memory by describing a detail to someone present without posting it."),
        barrier = profileText("Tu prépares les publications au moment même où la sortie se déroule.", "You prepare posts at the very moment the outing is happening."),
        goal = profileText("Profite d’une partie de la sortie sans préparer une publication en même temps.", "Enjoy part of the outing without preparing a post at the same time.")
    ),
    CoachLibraryProfile(
        id = "shared_weekend_activity", sectionId = "relationships", parentTopicId = "goals",
        label = profileText("Une activité partagée le week-end", "A shared weekend activity"),
        situation = profileText("Du temps ensemble peut devenir du temps chacun sur son téléphone si rien n’est prévu.", "Time together can become time on separate phones if nothing is planned."),
        smallAction = profileText("Propose une activité simple à faire ensemble, comme une courte promenade ou un jeu de table.", "Suggest a simple activity to do together, such as a short walk or a tabletop game."),
        alternative = profileText("Choisissez une activité que chacun peut apprécier sans demander une grande préparation.", "Choose an activity everyone can enjoy without much preparation."),
        barrier = profileText("Aucune activité ni heure de début n’est décidée et chacun attend avec son téléphone.", "No activity or starting time is decided and everyone waits with their phone."),
        goal = profileText("Réserve un petit moment partagé avant de reprendre les consultations individuelles.", "Set aside a small shared moment before returning to individual phone checks.")
    ),

    CoachLibraryProfile(
        id = "paper_reading", sectionId = "offline_activities", parentTopicId = "start",
        label = profileText("Reprendre un livre papier", "Returning to a paper book"),
        situation = profileText("Lire peut sembler difficile à démarrer si le téléphone offre une distraction immédiate.", "Reading can feel hard to start when your phone offers an immediate distraction."),
        smallAction = profileText("Ouvre le livre au marque-page et lis seulement un paragraphe.", "Open the book at the bookmark and read only one paragraph."),
        alternative = profileText("Choisis un texte court ou un chapitre que tu as réellement envie de lire.", "Choose a short text or a chapter you actually want to read."),
        barrier = profileText("Le livre n’est pas prêt à la bonne page, alors que le téléphone s’ouvre immédiatement.", "The book is not ready at the right page while your phone opens immediately."),
        goal = profileText("Commence une petite lecture choisie sans te donner une quantité excessive à terminer.", "Start a small chosen reading session without setting an excessive amount to finish.")
    ),
    CoachLibraryProfile(
        id = "short_outdoor_walk", sectionId = "offline_activities", parentTopicId = "breaks",
        label = profileText("Une petite promenade sans défilement", "A short walk without scrolling"),
        situation = profileText("Une pause dehors peut rester centrée sur le téléphone si tu le gardes ouvert en marchant.", "An outdoor break can stay focused on your phone if you keep it open while walking."),
        smallAction = profileText("Choisis un trajet adapté et mets le téléphone dans une poche avant de marcher.", "Choose a suitable route and put your phone in a pocket before walking."),
        alternative = profileText("Regarde ton environnement et garde ton attention sur le trajet plutôt que sur un écran.", "Look at your surroundings and keep your attention on the route rather than a screen."),
        barrier = profileText("Le téléphone reste ouvert pour le trajet et les autres contenus peuvent détourner ton attention.", "Your phone stays open for the route and other content can distract your attention."),
        goal = profileText("Fais une courte promenade choisie en restant attentif à ce qui t’entoure.", "Take a short chosen walk while staying aware of your surroundings.")
    ),
    CoachLibraryProfile(
        id = "gentle_movement", sectionId = "offline_activities", parentTopicId = "breaks",
        label = profileText("Bouger un peu entre deux sessions", "Moving a little between sessions"),
        situation = profileText("Tu peux enchaîner les écrans sans changer de position ni faire une pause choisie.", "You may move from screen to screen without changing position or taking a chosen break."),
        smallAction = profileText("Pose le téléphone et fais quelques mouvements doux qui te conviennent.", "Put your phone down and make a few gentle movements that suit you."),
        alternative = profileText("Marche un peu dans la pièce ou change de position confortablement.", "Walk a little around the room or change to a comfortable position."),
        barrier = profileText("Aucun repère de pause n’est prévu et l’espace ou les mouvements envisagés ne te conviennent peut-être pas.", "No break cue is planned and the space or intended movements may not suit you."),
        goal = profileText("Insère une petite pause de mouvement adaptée entre deux activités sur écran.", "Add a small, suitable movement break between two screen activities.")
    ),
    CoachLibraryProfile(
        id = "cook_without_feed", sectionId = "offline_activities", parentTopicId = "meals",
        label = profileText("Préparer un repas sans fil d’actualité", "Preparing food without a feed"),
        situation = profileText("Consulter des vidéos pendant la préparation d’un repas peut détourner ton attention des gestes à faire.", "Checking videos while preparing food can distract you from the tasks at hand."),
        smallAction = profileText("Lis l’étape utile de la recette, puis range le téléphone avant de manipuler les ingrédients.", "Read the useful recipe step, then put your phone away before handling ingredients."),
        alternative = profileText("Prépare un ingrédient ou dispose le matériel nécessaire en te concentrant sur ce geste.", "Prepare one ingredient or set out the equipment you need while focusing on that step."),
        barrier = profileText("La recette et les contenus de loisir sont sur le même appareil pendant des gestes qui demandent de l’attention.", "The recipe and entertainment content share the same device during tasks that require attention."),
        goal = profileText("Garde l’écran pour la recette utile et mets le divertissement de côté pendant les gestes de cuisine.", "Keep the screen for useful recipe information and set entertainment aside during cooking tasks.")
    ),
    CoachLibraryProfile(
        id = "creative_hobby", sectionId = "offline_activities", parentTopicId = "boredom",
        label = profileText("Dessiner ou créer au lieu de regarder", "Drawing or making instead of watching"),
        situation = profileText("Regarder les créations des autres peut prendre tout le temps que tu voulais consacrer à essayer toi-même.", "Looking at other people's creations can take all the time you wanted to spend trying yourself."),
        smallAction = profileText("Prends une feuille et trace quelques formes sans chercher un résultat à publier.", "Take a sheet of paper and draw a few shapes without aiming for something to post."),
        alternative = profileText("Essaie une petite étape de ton loisir, comme quelques notes, un croquis ou un pliage.", "Try one small step of your hobby, such as a few notes, a sketch or a fold."),
        barrier = profileText("Le matériel n’est pas prêt et la recherche d’inspiration prend la place de l’essai personnel.", "The equipment is not ready and searching for inspiration replaces trying for yourself."),
        goal = profileText("Passe d’un contenu regardé à un petit geste créatif personnel.", "Move from watching content to one small creative action of your own.")
    ),
    CoachLibraryProfile(
        id = "small_tidy_up", sectionId = "offline_activities", parentTopicId = "start",
        label = profileText("Ranger un petit espace", "Tidying a small space"),
        situation = profileText("Un grand rangement peut sembler décourageant et te faire retourner au téléphone avant de commencer.", "A big tidy-up can feel discouraging and send you back to your phone before starting."),
        smallAction = profileText("Range seulement trois objets à leur place, puis décide si tu veux continuer.", "Put just three things where they belong, then decide whether you want to continue."),
        alternative = profileText("Choisis une seule surface ou une petite zone plutôt que toute la pièce.", "Choose one surface or one small area rather than the whole room."),
        barrier = profileText("L’objectif semble être toute la pièce et tu ne sais pas par quel objet commencer.", "The goal seems to be the whole room and you do not know which item to start with."),
        goal = profileText("Termine un rangement limité et visible au lieu d’attendre la motivation pour tout faire.", "Finish a limited, visible tidy-up instead of waiting for motivation to do everything.")
    ),

    CoachLibraryProfile(
        id = "realistic_daily_budget", sectionId = "consistency", parentTopicId = "goals",
        label = profileText("Choisir une limite réaliste", "Choosing a realistic limit"),
        situation = profileText("Une limite très éloignée de tes habitudes peut être difficile à respecter dès le premier jour.", "A limit far from your habits can be hard to follow from the first day."),
        smallAction = profileText("Compare ton usage mesuré à l’activité que tu veux protéger avant de choisir une limite.", "Compare your measured use with the activity you want to protect before choosing a limit."),
        alternative = profileText("Choisis une petite réduction ou un moment précis sans téléphone pour commencer.", "Choose a small reduction or a specific phone-free moment to start."),
        barrier = profileText("La limite imaginée ne tient peut-être pas compte de tes usages utiles ni de ton point de départ.", "The limit you imagine may not account for your useful uses or your starting point."),
        goal = profileText("Fixe un objectif que tu peux expliquer simplement et essayer dans ta vraie journée.", "Set a goal you can explain simply and try during your actual day.")
    ),
    CoachLibraryProfile(
        id = "limit_reached_next_step", sectionId = "consistency", parentTopicId = "exceeded",
        label = profileText("La prochaine action après une limite atteinte", "The next action after reaching a limit"),
        situation = profileText("Atteindre une limite peut te donner envie d’abandonner l’objectif pour le reste de la journée.", "Reaching a limit can make you want to abandon the goal for the rest of the day."),
        smallAction = profileText("Ferme l’application concernée et choisis la prochaine activité que tu peux commencer maintenant.", "Close the app concerned and choose the next activity you can start now."),
        alternative = profileText("Reviens à une tâche courte hors de cette application, sans essayer de récupérer toute la journée.", "Return to a short task outside that app without trying to recover the whole day."),
        barrier = profileText("La pensée que la journée est déjà perdue peut te pousser à continuer sans choisir.", "The thought that the day is already lost can push you to continue without choosing."),
        goal = profileText("Protège la prochaine portion de temps, même si la limite a déjà été atteinte.", "Protect the next portion of time even if the limit has already been reached.")
    ),
    CoachLibraryProfile(
        id = "restart_after_lapse", sectionId = "consistency", parentTopicId = "relapse",
        label = profileText("Reprendre après un écart", "Restarting after a lapse"),
        situation = profileText("Un écart peut te faire penser que tous les efforts précédents ne comptent plus.", "A lapse can make you think all your previous efforts no longer count."),
        smallAction = profileText("Note le moment de l’écart et choisis une seule petite action pour reprendre.", "Note when the lapse happened and choose one small action to restart."),
        alternative = profileText("Reprends une habitude facile que tu avais déjà réussie, sans tout durcir d’un coup.", "Return to an easy habit you have already managed without making everything stricter at once."),
        barrier = profileText("Tu juges l’écart au lieu d’identifier le moment et le contexte qui l’ont facilité.", "You judge the lapse instead of identifying the moment and context that made it easier."),
        goal = profileText("Reprends sur la prochaine action plutôt que de juger toute ta progression à partir d’un écart.", "Restart with the next action instead of judging all your progress from one lapse.")
    ),
    CoachLibraryProfile(
        id = "multi_day_challenge_habit", sectionId = "consistency", parentTopicId = "goals",
        label = profileText("Tenir une habitude sur plusieurs jours", "Keeping a habit over several days"),
        situation = profileText("Une action prévue sur plusieurs jours peut être oubliée si elle n’a pas de place claire dans la routine.", "An action planned over several days can be forgotten if it has no clear place in your routine."),
        smallAction = profileText("Choisis le moment et le matériel pour la prochaine journée de ton habitude.", "Choose the time and materials for the next day of your habit."),
        alternative = profileText("Associe l’action à un repère existant, comme après le petit-déjeuner ou avant le coucher.", "Link the action to an existing cue, such as after breakfast or before bedtime."),
        barrier = profileText("L’action n’a pas de moment précis et le matériel n’est pas toujours disponible quand tu y penses.", "The action has no specific time and the materials are not always available when you think of it."),
        goal = profileText("Rends la prochaine journée facile à commencer, sans chercher à réaliser toutes les journées d’un coup.", "Make the next day easy to start without trying to complete every day at once.")
    ),
    CoachLibraryProfile(
        id = "notice_small_wins", sectionId = "consistency", parentTopicId = "success",
        label = profileText("Remarquer les petites réussites", "Noticing small wins"),
        situation = profileText("Une petite pause réussie peut passer inaperçue si tu ne regardes que les moments où tu as trop utilisé le téléphone.", "A successful small break can go unnoticed if you only look at times when you used your phone too much."),
        smallAction = profileText("Note un moment concret où tu as choisi ce que tu voulais faire à la place du téléphone.", "Note one concrete moment when you chose what you wanted to do instead of using your phone."),
        alternative = profileText("Apprécie le temps utilisé pour cette activité sans devoir le publier ni le comparer.", "Appreciate the time spent on that activity without needing to post or compare it."),
        barrier = profileText("Le bilan se concentre uniquement sur les dépassements et laisse de côté les choix utiles.", "The review focuses only on going over limits and leaves out useful choices."),
        goal = profileText("Identifie une action utile que tu aimerais répéter demain.", "Identify one useful action you would like to repeat tomorrow.")
    ),
    CoachLibraryProfile(
        id = "weekend_routine_change", sectionId = "consistency", parentTopicId = "streak",
        label = profileText("Garder un repère le week-end", "Keeping a cue at the weekend"),
        situation = profileText("Un emploi du temps plus libre peut faire disparaître les repères qui t’aident en semaine.", "A freer schedule can remove the cues that help you during the week."),
        smallAction = profileText("Choisis un seul moment du week-end où tu veux garder le téléphone de côté.", "Choose one weekend moment when you want to keep your phone aside."),
        alternative = profileText("Prévois une activité agréable compatible avec ton temps libre, sans copier toute la routine de semaine.", "Plan an enjoyable activity that fits your free time without copying your whole weekday routine."),
        barrier = profileText("Les repères de semaine ont disparu et les projets du week-end changent parfois au dernier moment.", "Weekday cues have disappeared and weekend plans sometimes change at the last minute."),
        goal = profileText("Garde un repère souple le week-end plutôt que d’exiger une journée identique à celle de la semaine.", "Keep a flexible weekend cue instead of demanding a day identical to a weekday.")
    )
)

private fun profileText(fr: String, en: String): CoachLibraryText = CoachLibraryText(fr, en)

/** Practical constraints change the proposed exercise, rather than just its spelling. */
internal data class CoachLibraryScenario(
    val id: String,
    val minimumPlan: PlanType,
    val label: CoachLibraryText,
    val questionContext: CoachLibraryText,
    val situation: CoachLibraryText,
    val smallAction: CoachLibraryText,
    val alternative: CoachLibraryText,
    val barrier: CoachLibraryText,
    val goal: CoachLibraryText
)

internal val coachLibraryScenarios: List<CoachLibraryScenario> = listOf(
    CoachLibraryScenario(
        id = "shared_space", minimumPlan = PlanType.PREMIUM,
        label = profileText("Dans un lieu partagé", "In a shared space"),
        questionContext = profileText("quand je partage la pièce et dois rester discret", "when I share the space and need to be considerate"),
        situation = profileText("La pièce et les objets ne sont pas tous à toi ; une pause doit respecter le calme et les usages des autres.", "The room and its items are not all yours; a break needs to respect other people's quiet and use of the space."),
        smallAction = profileText("Choisis un geste silencieux qui concerne seulement ton téléphone ou tes affaires. Garde les objets communs à leur place et ne demande pas aux autres de suivre ta règle.", "Choose a quiet action involving only your phone or belongings. Leave shared items where they are and do not ask others to follow your rule."),
        alternative = profileText("Depuis ta place, remarque un détail autour de toi ou réfléchis silencieusement à la prochaine étape. Si une conversation convient au lieu et que l'autre personne est disponible, elle reste une option choisie.", "From where you are, notice one detail nearby or silently think about the next step. If a conversation suits the space and the other person is available, it remains a deliberate option."),
        barrier = profileText("Déplacer un objet commun, lancer un son ou solliciter quelqu'un peut gêner les autres, même si cela t'aiderait dans un lieu privé.", "Moving a shared item, playing a sound or involving someone can disturb others even if it would help in a private space."),
        goal = profileText("Essaie une seule transition discrète que tu peux décider pour toi-même. Le calme du lieu et les besoins d'accessibilité restent prioritaires.", "Try one quiet transition you can choose for yourself. The space's quiet and accessibility needs remain priorities.")
    ),
    CoachLibraryScenario(
        id = "essential_contact", minimumPlan = PlanType.PREMIUM,
        label = profileText("En restant joignable", "While staying reachable"),
        questionContext = profileText("quand je dois pouvoir recevoir un contact essentiel", "when I need to receive essential contact"),
        situation = profileText("Tu attends un contact ou une information indispensable ; réduire le loisir ne doit pas empêcher de le recevoir ou d'y répondre.", "You are expecting essential contact or information; reducing entertainment must not prevent you from receiving or responding to it."),
        smallAction = profileText("Repère le canal dont tu as besoin et garde ses alertes, son accès et tes outils d'accessibilité disponibles. Traite d'abord le besoin essentiel ; interromps seulement le contenu facultatif une fois son usage terminé.", "Identify the channel you need and keep its alerts, access and your accessibility tools available. Handle the essential need first; pause only optional content once its purpose is finished."),
        alternative = profileText("Choisis une étape courte sur place que tu peux interrompre dès le contact attendu. Si un message utile arrive, réponds puis retrouve ton point de reprise ; ce contact n'est pas un écart par rapport à cet essai.", "Choose a short step where you are that you can stop as soon as the expected contact arrives. If a useful message comes, respond and return to your restart point; that contact is not a lapse in this exercise."),
        barrier = profileText("Un même téléphone, groupe ou signal peut mêler loisir et contact indispensable. Couper tout le canal ou éloigner le téléphone sans vérifier sa réception peut supprimer l'accès dont tu as besoin.", "The same phone, group or alert can mix entertainment with essential contact. Silencing the whole channel or moving the phone without checking reception can remove the access you need."),
        goal = profileText("Réduis une consultation facultative tout en gardant une réponse fiable aux besoins réels. Une interruption nécessaire peut faire partie d'une journée réussie.", "Reduce one optional check while keeping a reliable response to real needs. A necessary interruption can be part of a successful day.")
    ),
    CoachLibraryScenario(
        id = "changed_setup", minimumPlan = PlanType.PREMIUM,
        label = profileText("Avec une installation différente", "With a different setup"),
        questionContext = profileText("quand je voyage ou que mes repères habituels ont changé", "when I am travelling or my usual cues have changed"),
        situation = profileText("Le lieu, les horaires ou les ressources ont changé. Le bureau, le point de charge ou la personne de ta routine habituelle ne sont peut-être pas disponibles.", "The place, schedule or resources have changed. Your usual desk, charging spot or companion may not be available."),
        smallAction = profileText("Repère une transition réellement présente ici, comme la fin d'une tâche utile. À cette transition, décide si le contenu facultatif est terminé, sans attendre de retrouver toute ton installation habituelle.", "Find a transition that actually exists here, such as finishing a useful task. At that transition, decide whether optional content is finished without waiting to recreate your whole usual setup."),
        alternative = profileText("Choisis un petit geste possible depuis ta place avec ce qui est déjà accessible, ou garde un instant sans nouveau contenu. En déplacement, reste attentif au trajet et conserve les informations et fonctions nécessaires.", "Choose a small action you can take from where you are with what is already accessible, or spend a moment without new content. While travelling, stay aware of your route and keep necessary information and functions available."),
        barrier = profileText("Attendre le bon lieu ou la bonne heure peut laisser le téléphone décider à ta place. Les trajets et les lieux inconnus peuvent aussi demander un usage utile de l'écran.", "Waiting for the right place or time can leave the phone deciding for you. Journeys and unfamiliar places can also require useful screen use."),
        goal = profileText("Garde un repère portable : une transition et un choix volontaire. Adapte le moyen au lieu actuel, sans exiger la même organisation que chez toi.", "Keep a portable cue: one transition and one deliberate choice. Adapt the method to your current surroundings without requiring the same setup as at home.")
    ),
    CoachLibraryScenario(
        id = "limited_time", minimumPlan = PlanType.PREMIUM,
        label = profileText("Avec très peu de temps", "With very little time"),
        questionContext = profileText("quand je n'ai que quelques secondes disponibles", "when I only have a few seconds available"),
        situation = profileText("Tu as moins de temps que pour l'exercice complet. Une petite décision doit pouvoir s'arrêter sans ajouter de retard à tes obligations.", "You have less time than the complete exercise needs. A small decision needs to end without making you late for your responsibilities."),
        smallAction = profileText("Choisis une seule décision : quitter un contenu facultatif dont le but est déjà atteint, ou nommer mentalement le point où reprendre ton activité. Tu peux t'arrêter dès que le temps disponible est écoulé.", "Choose just one decision: end optional content whose purpose is already finished, or mentally name where to resume your activity. You can stop as soon as your available time is up."),
        alternative = profileText("Garde seulement le prochain pas en tête, sans le réaliser maintenant. Reprends-le à la prochaine transition disponible ; inutile de finir une page, une partie ou une sortie pour que cet essai compte.", "Keep only the next step in mind without carrying it out now. Return to it at the next available transition; you do not need to finish a page, a game or an outing for this attempt to count."),
        barrier = profileText("Un objectif prévu en minutes peut sembler impossible dans un court intervalle. Vouloir finir toute l'activité peut transformer une pause utile en nouvelle contrainte.", "A goal planned in minutes can seem impossible in a short gap. Trying to finish the whole activity can turn a useful pause into another demand."),
        goal = profileText("Fais un choix observable dans le temps réellement disponible. Ne rattrape pas l'exercice en ajoutant du retard ou en supprimant un besoin essentiel.", "Make one observable choice within the time actually available. Do not catch up on the exercise by making yourself late or removing an essential need.")
    ),
    CoachLibraryScenario(
        id = "low_energy", minimumPlan = PlanType.ULTRA,
        label = profileText("Avec peu d'énergie", "With little energy"),
        questionContext = profileText("quand même une petite activité me demande trop d'effort", "when even a small activity takes too much effort"),
        situation = profileText("Te lever, préparer du matériel ou prendre plusieurs décisions demande aujourd'hui plus d'effort que tu ne souhaites fournir.", "Standing up, preparing materials or making several decisions takes more effort today than you want to spend."),
        smallAction = profileText("Depuis une position confortable, arrête seulement une interaction facultative dont le but est terminé. Si ce geste demande déjà trop d'effort, décide simplement de ne pas lancer un nouveau contenu ; le repos suffit.", "From a comfortable position, stop only an optional interaction whose purpose is finished. If that already takes too much effort, simply choose not to start new content; resting is enough."),
        alternative = profileText("Reste dans une position qui te convient, sans matériel ni déplacement. Tu peux remarquer un détail familier ou ne rien ajouter pendant un instant ; aucun exercice physique ni résultat à produire n'est nécessaire.", "Stay in a position that suits you without equipment or movement. You can notice one familiar detail or add nothing for a moment; no physical exercise or output is needed."),
        barrier = profileText("Une alternative présentée comme facile peut encore exiger de marcher, ranger ou préparer quelque chose. Ajouter une obligation quand l'énergie manque peut rendre la reprise plus difficile.", "An alternative described as easy can still require walking, tidying or preparing something. Adding a demand when energy is low can make restarting harder."),
        goal = profileText("Protège un bref repos choisi et garde tes accès utiles. Un essai sans déplacement ni préparation est suffisant ; tu peux reporter l'activité plus exigeante.", "Protect a brief, chosen rest and keep useful access available. An attempt without movement or preparation is enough; you can postpone the more demanding activity.")
    ),
    CoachLibraryScenario(
        id = "caring_for_someone", minimumPlan = PlanType.ULTRA,
        label = profileText("En s'occupant de quelqu'un", "While caring for someone"),
        questionContext = profileText("quand une personne dépend de ma présence ou de mon attention", "when someone depends on my presence or attention"),
        situation = profileText("Tu t'occupes d'une personne et dois pouvoir répondre à ses besoins. Ton petit essai peut être interrompu à tout moment.", "You are caring for someone and need to respond to their needs. Your small experiment can be interrupted at any time."),
        smallAction = profileText("Réponds d'abord au besoin de la personne. Lorsqu'un intervalle est réellement disponible, quitte seulement le contenu facultatif et garde les moyens de contact et de supervision nécessaires ; ne t'éloigne pas pour cet exercice.", "Respond to the person's need first. When a gap is actually available, leave only optional content and keep necessary contact and supervision available; do not move away for this exercise."),
        alternative = profileText("Si la situation le permet, prends un instant sur place sans nouveau contenu, en gardant ton attention disponible. Le besoin de la personne reprend immédiatement la priorité ; tu peux noter mentalement où reprendre plus tard.", "If the situation allows, spend a moment where you are without new content while keeping your attention available. The person's need immediately takes priority; you can mentally note where to resume later."),
        barrier = profileText("Une pause planifiée peut être interrompue, et quitter la pièce ou ranger le téléphone peut être incompatible avec ton rôle. Ces besoins réels ne sont pas un manque de volonté.", "A planned break can be interrupted, and leaving the room or putting the phone away may conflict with your role. These real needs are not a lack of willpower."),
        goal = profileText("Réduis un usage facultatif dans un intervalle sûr, sans retarder une réponse ni réduire la présence nécessaire. Reporter l'essai est une décision valable.", "Reduce optional use during a safe gap without delaying a response or reducing necessary presence. Postponing the attempt is a valid decision.")
    ),
    CoachLibraryScenario(
        id = "unchanged_settings", minimumPlan = PlanType.ULTRA,
        label = profileText("Sans changer les réglages", "Without changing settings"),
        questionContext = profileText("quand je ne peux pas ou ne veux pas modifier mon téléphone", "when I cannot or do not want to change my phone"),
        situation = profileText("Tu gardes les réglages, les icônes, les applications et les notifications tels quels. L'essai porte sur une décision d'usage, sans restriction technique.", "You are keeping settings, icons, apps and notifications as they are. The experiment concerns a usage decision without a technical restriction."),
        smallAction = profileText("Avant une ouverture facultative, nomme ce que tu veux faire et ce qui marquera la fin. Une fois ce but atteint, quitte le contenu choisi sans toucher aux réglages ni supprimer une application.", "Before an optional opening, name what you want to do and what will mark its end. Once that purpose is met, leave the chosen content without touching settings or deleting an app."),
        alternative = profileText("Choisis une tâche déjà accessible, ou garde quelques instants sans nouveau contenu. Un signal peut rester présent sans obliger à ouvrir un fil non urgent ; réponds aux besoins essentiels selon tes obligations.", "Choose a task already available or spend a few moments without new content. An alert can stay present without requiring you to open a non-urgent feed; respond to essential needs according to your responsibilities."),
        barrier = profileText("Les signaux et raccourcis restent visibles, et le téléphone ne fera pas la pause à ta place. Le choix volontaire peut aider sans garantir que toutes les ouvertures automatiques disparaissent.", "Alerts and shortcuts remain visible, and the phone will not create the pause for you. A deliberate choice can help without guaranteeing that every automatic opening disappears."),
        goal = profileText("Essaie une seule ouverture avec un but et une fin choisis, en conservant exactement ta configuration. Mesure ce choix, sans attendre qu'un réglage te protège automatiquement.", "Try one opening with a chosen purpose and ending while keeping your configuration exactly as it is. Observe that choice without expecting a setting to protect you automatically.")
    ),
    CoachLibraryScenario(
        id = "no_equipment", minimumPlan = PlanType.ULTRA,
        label = profileText("Sans matériel ni achat", "Without equipment or purchases"),
        questionContext = profileText("quand le matériel prévu est absent et que je ne veux rien acheter", "when the planned materials are missing and I do not want to buy anything"),
        situation = profileText("Le livre, la feuille ou le matériel prévu n'est pas disponible. Tu peux garder l'intention de la pause sans acheter ni préparer un remplacement.", "The planned book, paper or equipment is unavailable. You can keep the purpose of the break without buying or preparing a replacement."),
        smallAction = profileText("Choisis un premier pas qui n'utilise aucun nouvel objet : remarque un détail, imagine une petite idée ou décide mentalement de la prochaine étape. Utilise un outil déjà accessible uniquement s'il répond directement au besoin.", "Choose a first step needing no new item: notice one detail, imagine a small idea or mentally decide the next step. Use an already accessible tool only if it directly meets the need."),
        alternative = profileText("Sans rien préparer, imagine une forme, rappelle-toi un passage connu ou réfléchis à une étape de l'activité. Sinon, garde simplement un instant sans fil d'actualité ; inutile d'aller chercher du matériel.", "Without preparing anything, imagine a shape, recall a familiar passage or think about one step of the activity. Otherwise, simply spend a moment without a feed; you do not need to fetch equipment."),
        barrier = profileText("Le matériel absent peut faire croire que seul l'écran reste possible. Chercher un achat ou tout préparer risque d'ajouter une nouvelle tâche au lieu d'offrir une pause.", "Missing equipment can make it seem that only the screen remains possible. Seeking a purchase or preparing everything can add a new task instead of offering a break."),
        goal = profileText("Fais une pause choisie avec ce qui existe déjà, même sans produire ni terminer l'activité prévue. Garde l'activité équipée pour un moment où ses ressources seront déjà disponibles.", "Take a chosen break with what already exists, even without producing or finishing the planned activity. Save the equipped activity for a time when its resources are already available.")
    )
)

/**
 * The present exercise follows the constraint. An incompatible original exercise is a later
 * option, never a prerequisite for completing the constrained one. Topic and section stay stable.
 */
internal fun adaptedCoachLibraryProfile(
    base: CoachLibraryProfile,
    scenario: CoachLibraryScenario
): CoachLibraryProfile {
    val constrainedAction = constrainedLibraryAction(base, scenario)
    val direction = constrainedLibraryDirection(base, scenario)
    return base.copy(
        id = "${base.id}~${scenario.id}",
        label = profileText("${base.label.fr} — ${scenario.label.fr}", "${base.label.en} — ${scenario.label.en}"),
        situation = profileText("${base.situation.fr} ${scenario.situation.fr}", "${base.situation.en} ${scenario.situation.en}"),
        smallAction = profileText(
            "${scenario.smallAction.fr}\n\n${constrainedAction.fr} Préserve les contacts essentiels et tes outils d'accessibilité pendant cet essai.",
            "${scenario.smallAction.en}\n\n${constrainedAction.en} Preserve essential contact and your accessibility tools during this exercise."
        ),
        alternative = profileText(
            "${scenario.alternative.fr} Applique ce repli à ton contexte : ${base.label.fr}.",
            "${scenario.alternative.en} Apply this fallback to your context: ${base.label.en}."
        ),
        barrier = profileText("${base.barrier.fr} ${scenario.barrier.fr}", "${base.barrier.en} ${scenario.barrier.en}"),
        goal = profileText("${scenario.goal.fr}\n\n${direction.fr}", "${scenario.goal.en}\n\n${direction.en}")
    )
}

private fun constrainedLibraryAction(base: CoachLibraryProfile, scenario: CoachLibraryScenario): CoachLibraryText {
    // These original exercises change the device. Replace them outright in the no-settings case.
    if (scenario.id == "unchanged_settings") {
        val replacement = when (base.id) {
            "youtube_autoplay" -> profileText("À la fin de la vidéo choisie, arrête manuellement la lecture ou quitte la vidéo, sans changer la lecture automatique.", "At the end of the chosen video, manually stop playback or leave the video without changing autoplay.")
            "repeated_push_alerts" -> profileText("À une alerte, distingue une demande essentielle d'une promotion. Traite le besoin essentiel ; garde la consultation de loisir pour le moment choisi, sans modifier les notifications.", "When an alert arrives, distinguish an essential request from a promotion. Handle the essential need; keep the entertainment check for your chosen time without changing notifications.")
            "home_screen_shortcuts" -> profileText("Avant de toucher une icône visible, nomme l'outil dont tu as réellement besoin. Ouvre uniquement cet outil, sans déplacer ni remplacer les raccourcis.", "Before touching a visible icon, name the tool you actually need. Open only that tool without moving or replacing shortcuts.")
            "grayscale_visual_pull" -> profileText("Laisse les couleurs telles quelles. Avant d'ouvrir une application attirante, choisis une tâche précise ; quand elle est terminée, quitte son contenu sans changer l'affichage.", "Leave the colours as they are. Before opening a tempting app, choose a specific task; when it is finished, leave its content without changing the display.")
            "group_chat_noise" -> profileText("Lis uniquement les informations dont tu as besoin dans le groupe, puis quitte la conversation sans la mettre en sourdine. Reste disponible pour les messages essentiels selon tes obligations.", "Read only the information you need in the group, then leave the conversation without muting it. Stay available for essential messages according to your responsibilities.")
            "unused_app_reinstallation" -> profileText("Garde l'application installée et nomme sa fonction utile avant de l'ouvrir. Une fois cette fonction utilisée, quitte le contenu facultatif sans supprimer l'application.", "Keep the app installed and name its useful function before opening it. Once that function is used, leave optional content without deleting the app.")
            else -> null
        }
        if (replacement != null) return replacement
        return profileText(
            "Pour ${base.label.fr}, tu peux essayer ce geste sans modifier la configuration, s'il convient à ton lieu et préserve tes accès essentiels : ${base.smallAction.fr}",
            "For ${base.label.en}, you can try this action without changing the configuration if it suits your surroundings and preserves essential access: ${base.smallAction.en}"
        )
    }

    return when (scenario.id) {
        "shared_space" -> profileText(
            "Pour ${base.label.fr}, commence en silence depuis ta place : identifie la consultation facultative ou la petite étape personnelle que tu veux choisir. Réserve les échanges parlés et les changements d'espace à un moment où ils conviennent aux personnes présentes.",
            "For ${base.label.en}, start quietly from where you are: identify the optional check or small personal step you want to choose. Save spoken exchanges and changes to the space for a time when they suit the people present."
        )
        "essential_contact" -> profileText(
            "Pour ${base.label.fr}, sépare la fonction indispensable du contenu facultatif avant de décider de fermer quoi que ce soit. Si le même écran ou groupe sert au contact attendu, garde-le accessible et quitte seulement le loisir lorsque cela préserve sa réception.",
            "For ${base.label.en}, separate the essential function from optional content before deciding to close anything. If the same screen or group serves the expected contact, keep it accessible and leave only entertainment when that preserves reception."
        )
        "changed_setup" -> profileText(
            "Pour ${base.label.fr}, cherche ici un point de reprise que tu peux reconnaître sans ton ancien matériel ni un réglage nouveau. Choisis le prochain geste possible dans ce lieu ; si aucun ne convient, garde seulement la décision pour plus tard.",
            "For ${base.label.en}, find a restart point here that you can recognise without your old equipment or a new setting. Choose the next action possible in these surroundings; if none fits, keep only the decision for later."
        )
        "limited_time" -> profileText(
            "Pour ${base.label.fr}, nomme seulement la prochaine étape ou la fin du contenu facultatif. N'attends pas de terminer toute l'activité : ce repère suffit maintenant, et l'activité peut reprendre quand tu auras le temps.",
            "For ${base.label.en}, name only the next step or the ending of optional content. Do not wait to finish the whole activity: this cue is enough now, and the activity can resume when you have time."
        )
        "low_energy" -> profileText(
            "Pour ${base.label.fr}, garde uniquement le choix de ne pas enchaîner un nouveau contenu facultatif, depuis ta position actuelle. Tu n'as pas besoin de te lever, de ranger ou de terminer une activité pour commencer cet essai.",
            "For ${base.label.en}, keep only the choice not to start more optional content from your current position. You do not need to stand up, tidy anything or finish an activity to begin this attempt."
        )
        "caring_for_someone" -> profileText(
            "Pour ${base.label.fr}, choisis ton point de reprise mentalement si une pause est possible. Garde tout déplacement, échange non nécessaire ou activité longue pour un moment où les besoins et la supervision de la personne sont réellement couverts.",
            "For ${base.label.en}, mentally choose your restart point if a pause is possible. Save any movement away, unnecessary conversation or lengthy activity for a time when the person's needs and supervision are actually covered."
        )
        "no_equipment" -> when (base.id) {
            "paper_reading", "reading_distracted" -> profileText("Pour cette lecture, rappelle-toi une idée d'un texte connu, ou lis quelques lignes d'un texte déjà accessible si tu en as un. Sans texte disponible, choisis seulement ce que tu aimerais lire plus tard ; aucun livre ni achat n'est requis.", "For this reading context, recall an idea from a familiar text, or read a few lines of an already accessible text if you have one. Without a text available, simply choose what you would like to read later; no book or purchase is required.")
            "creative_hobby" -> profileText("Pour le loisir créatif, imagine quelques formes ou une petite idée sans la dessiner ni la publier. Une idée imaginée suffit ; inutile de chercher une feuille, un instrument ou un autre matériel.", "For the creative hobby, imagine a few shapes or one small idea without drawing or posting it. An imagined idea is enough; you do not need to find paper, an instrument or other equipment.")
            "cook_without_feed" -> profileText("Pour la cuisine, décide mentalement d'une étape possible avec les ingrédients et outils déjà présents. S'ils manquent, reporte la préparation ; quitte seulement le contenu de loisir, sans manipuler l'écran pendant un geste de cuisine.", "For cooking, mentally choose one step possible with ingredients and tools already present. If they are missing, postpone preparation; leave only entertainment content without handling the screen during a cooking task.")
            "short_outdoor_walk", "gentle_movement" -> profileText("Pour cette pause de mouvement, choisis seulement un repère autour de toi depuis une position adaptée. Sortir ou bouger reste facultatif et dépend de ton confort et du lieu ; cette pause peut rester immobile.", "For this movement break, simply choose one cue around you from a suitable position. Going out or moving remains optional and depends on your comfort and surroundings; this break can stay still.")
            else -> profileText(
                "Pour ${base.label.fr}, garde le premier pas comme une décision mentale : choisis ce qui serait utile avec les ressources déjà présentes. Si l'activité ne peut pas commencer sans matériel, reporte-la et garde une pause sans nouveau contenu.",
                "For ${base.label.en}, keep the first step as a mental decision: choose what would help with resources already present. If the activity cannot start without equipment, postpone it and keep a break without new content."
            )
        }
        else -> error("Unknown prepared-coach scenario: ${scenario.id}")
    }
}

private fun constrainedLibraryDirection(base: CoachLibraryProfile, scenario: CoachLibraryScenario): CoachLibraryText {
    // Every no-settings goal is authored for voluntary use; none quotes an original directive.
    if (scenario.id == "unchanged_settings") return unchangedSettingsLibraryDirection(base)
    val condition = when (scenario.id) {
        "shared_space" -> profileText("dans un moment où le lieu, les personnes et tes besoins d'accessibilité le permettent", "at a time when the space, people and your accessibility needs allow it")
        "essential_contact" -> profileText("uniquement si la réception et la réponse au contact essentiel restent fiables", "only if receiving and responding to essential contact remain reliable")
        "changed_setup" -> profileText("avec un moyen réellement disponible dans le lieu actuel, ou plus tard si ce moyen manque", "with a method actually available in your current surroundings, or later if it is missing")
        "limited_time" -> profileText("plus tard, quand le temps nécessaire est disponible sans retarder tes obligations", "later, when the needed time is available without delaying your responsibilities")
        "low_energy" -> profileText("plus tard, si l'effort te convient ; le repos choisi suffit aujourd'hui", "later, if the effort suits you; chosen rest is enough today")
        "caring_for_someone" -> profileText("plus tard, seulement quand les besoins et la supervision de la personne restent couverts", "later, only when the person's needs and supervision remain covered")
        "no_equipment" -> profileText("plus tard si le matériel est déjà disponible, sans achat requis ; la pause actuelle peut rester sans matériel", "later if equipment is already available, with no purchase required; the present break can stay equipment-free")
        else -> error("Unknown prepared-coach scenario: ${scenario.id}")
    }
    return profileText(
        "Pour ${base.label.fr}, la direction d'origine reste une option ${condition.fr} : « ${base.goal.fr} » Ce n'est pas une obligation pour l'essai actuel.",
        "For ${base.label.en}, the original direction remains an option ${condition.en}: “${base.goal.en}” It is not a requirement for the present attempt."
    )
}

private fun unchangedSettingsLibraryDirection(base: CoachLibraryProfile): CoachLibraryText {
    val purpose = when (base.id) {
        "tiktok_short_videos" -> profileText("Choisis une session de vidéos et son point de fin avant de commencer, puis quitte manuellement le fil à ce point.", "Choose a video session and its ending before starting, then manually leave the feed at that point.")
        "instagram_comparison" -> profileText("Ouvre Instagram pour une intention que tu choisis, comme répondre à un ami. Quand ce besoin est satisfait, quitte le fil plutôt que de chercher de nouvelles comparaisons.", "Open Instagram for a purpose you choose, such as replying to a friend. When that need is met, leave the feed rather than seeking new comparisons.")
        "youtube_autoplay" -> profileText("Regarde une vidéo choisie et décide toi-même de la fin : arrête la lecture ou quitte la vidéo sans suivre automatiquement la recommandation suivante.", "Watch a chosen video and decide its ending yourself: stop playback or leave the video without automatically following the next recommendation.")
        "snapchat_streak_pressure" -> profileText("Choisis un échange utile ou agréable avec une personne, puis quitte l'application quand cet échange est terminé. Le compteur ne décide pas de ta prochaine ouverture.", "Choose a useful or enjoyable exchange with someone, then leave the app when that exchange is finished. The counter does not decide your next opening.")
        "whatsapp_repeated_checks" -> profileText("Consulte une conversation pour lire ou répondre à une information utile, puis quitte-la quand le besoin est traité. Une réponse nécessaire reste prioritaire ; une vérification sans nouveauté peut attendre.", "Check a conversation to read or reply to useful information, then leave when the need is handled. A necessary response stays a priority; a check with nothing new can wait.")
        "games_extra_round" -> profileText("Choisis à l'avance le nombre de parties ou un point de fin adapté, puis décide de quitter le jeu à ce point sans lancer une partie supplémentaire par réflexe.", "Choose the number of rounds or a suitable ending in advance, then choose to leave the game at that point without starting another round out of habit.")
        "wake_up_notifications" -> profileText("Choisis un premier geste du matin qui compte pour toi, avant le fil de loisir si les besoins essentiels le permettent. Termine manuellement toute consultation qui n'a plus de but utile.", "Choose a first morning action that matters to you, before an entertainment feed if essential needs allow it. Manually end any check that no longer has a useful purpose.")
        "breakfast_scroll" -> profileText("Choisis une petite partie du petit-déjeuner pour goûter le repas. Après une consultation utile, quitte le fil facultatif pour retrouver ce moment, sans changer les alertes.", "Choose a small part of breakfast to taste your food. After a useful check, leave the optional feed to return to that moment without changing alerts.")
        "commute_waiting" -> profileText("Utilise l'écran pour les informations de transport nécessaires, puis quitte le loisir quand ce besoin est traité. Garde ton attention disponible pour le trajet et ses changements.", "Use the screen for necessary travel information, then leave entertainment when that need is handled. Keep your attention available for your journey and any changes.")
        "after_school_decompression" -> profileText("Choisis comment tu veux souffler après les cours, puis décide de la place d'une session de loisir et de sa fin. La détente reste un choix, sans nouvelle restriction du téléphone.", "Choose how you want to unwind after class, then decide where an entertainment session fits and how it ends. Unwinding remains a choice without a new phone restriction.")
        "evening_second_screen" -> profileText("Choisis le programme ou l'activité que tu veux suivre. Quitte manuellement le contenu de l'autre écran quand il ne sert pas cette intention, en gardant les fonctions utiles accessibles.", "Choose the programme or activity you want to follow. Manually leave content on the other screen when it does not serve that intention while keeping useful functions accessible.")
        "bedtime_videos" -> profileText("Choisis le moment où le divertissement laisse place au coucher, puis ferme manuellement la vidéo à ce moment. Garde le réveil et les contacts nécessaires disponibles sans changer la configuration.", "Choose when entertainment gives way to bedtime, then manually close the video at that moment. Keep your alarm and necessary contact available without changing the configuration.")
        "boredom_empty_minutes" -> profileText("Laisse un bref moment vide si cela te convient, ou choisis une activité pour une raison précise. Un fil facultatif peut se terminer dès qu'il ne répond plus à cette raison.", "Leave a brief moment empty if that suits you, or choose an activity for a specific reason. An optional feed can end as soon as it no longer serves that reason.")
        "stress_avoidance" -> profileText("Choisis une étape assez petite de la tâche qui te pèse, ou une pause avec une fin que tu peux reconnaître. Quitte le contenu de loisir quand tu décides de reprendre cette étape.", "Choose a small enough step of the task weighing on you, or a break with an ending you can recognise. Leave entertainment content when you choose to return to that step.")
        "loneliness_feed" -> profileText("Choisis un échange réel quand il convient aux deux personnes, plutôt qu'une suite de publications sans but. L'application peut rester utile pour ce contact ; quitte seulement le fil qui ne le sert pas.", "Choose a real exchange when it suits both people rather than a stream of posts without a purpose. The app can remain useful for that contact; leave only the feed that does not serve it.")
        "fomo_updates" -> profileText("Choisis l'information que tu veux réellement connaître et vérifie-la à un moment compatible avec tes obligations. Quand tu l'as obtenue, quitte le fil sans chercher à épuiser toutes les nouveautés.", "Choose the information you actually want to know and check it at a time compatible with your responsibilities. When you have it, leave the feed without trying to exhaust every update.")
        "tired_automatic_scroll" -> profileText("Choisis du repos lorsque tu en as besoin. Tu peux terminer manuellement le contenu facultatif ou simplement ne pas en lancer un autre, sans ajouter de tâche à accomplir.", "Choose rest when you need it. You can manually end optional content or simply not start more without adding a task to complete.")
        "frustration_reopening" -> profileText("Après une contrariété, choisis seulement la prochaine action ou une pause volontaire. Termine le contenu qui ne sert plus ce choix au lieu de rouvrir plusieurs applications sans intention.", "After an annoyance, choose only the next action or a deliberate break. End content that no longer serves that choice instead of reopening several apps without an intention.")
        "homework_start" -> profileText("Choisis un exercice précis et son premier pas faisable. Termine manuellement la consultation de loisir lorsque tu décides de commencer, tout en gardant les recherches utiles possibles.", "Choose a specific exercise and its first feasible step. Manually end the entertainment check when you decide to start while keeping useful research possible.")
        "revision_break" -> profileText("Choisis un point clair où reprendre la révision et une fin pour le loisir de la pause. Reviens volontairement à ce point quand la pause choisie se termine.", "Choose a clear place to resume studying and an ending for entertainment during the break. Deliberately return to that point when your chosen break ends.")
        "remote_work_tabs" -> profileText("Choisis la prochaine phrase ou étape du travail. Quand tu reprends cette tâche, quitte manuellement le contenu de loisir sans modifier les outils ni leurs réglages.", "Choose the next sentence or step of your work. When you resume that task, manually leave entertainment content without changing tools or their settings.")
        "work_message_interruptions" -> profileText("Garde un point de reprise clair pour le travail. Traite les messages nécessaires selon tes obligations, puis quitte les échanges facultatifs lorsqu'ils ne servent plus un besoin réel.", "Keep a clear restart point for work. Handle necessary messages according to your responsibilities, then leave optional exchanges when they no longer serve a real need.")
        "reading_distracted" -> profileText("Choisis une petite portion de lecture que tu peux suivre. Après une consultation nécessaire, quitte le contenu facultatif et retrouve le dernier point que tu avais compris.", "Choose a small portion of reading you can follow. After a necessary check, leave optional content and return to the last point you understood.")
        "group_study" -> profileText("Choisis ta prochaine contribution à l'exercice commun. Garde les recherches utiles disponibles et termine ton propre contenu de loisir, sans imposer une règle aux autres téléphones.", "Choose your next contribution to the shared exercise. Keep useful research available and end your own entertainment content without imposing a rule on other people's phones.")
        "repeated_push_alerts" -> profileText("Choisis de traiter les alertes utiles selon leur besoin réel. Une promotion peut rester présente sans ouvrir son fil ; les notifications et les moyens de contact restent inchangés.", "Choose to handle useful alerts according to their actual need. A promotion can stay present without opening its feed; notifications and contact methods remain unchanged.")
        "home_screen_shortcuts" -> profileText("Choisis l'outil nécessaire avant de toucher une icône. Ouvre-le pour cette tâche, puis quitte le contenu facultatif une fois le but atteint, en gardant chaque raccourci à sa place.", "Choose the necessary tool before touching an icon. Open it for that task, then leave optional content once the purpose is met while keeping every shortcut in place.")
        "phone_on_desk" -> profileText("Choisis une courte étape de travail malgré la présence du téléphone. Ne l'ouvre que pour un besoin précis ; après ce besoin, quitte le contenu facultatif et retrouve ton point de reprise.", "Choose a short work step despite the phone being present. Open it only for a specific need; after that need, leave optional content and return to your restart point.")
        "bedside_charger" -> profileText("Tu peux conserver ton point de charge actuel ; choisis une fin manuelle au divertissement avant le repos. Le réveil, les appels et les outils nécessaires restent accessibles.", "You can keep your current charging spot; choose a manual end to entertainment before resting. Your alarm, calls and necessary tools stay accessible.")
        "grayscale_visual_pull" -> profileText("Garde les couleurs telles quelles et choisis une tâche précise avant l'ouverture. Quand elle est terminée, quitte manuellement le contenu attirant sans changer l'affichage ni l'accessibilité.", "Keep colours as they are and choose a specific task before opening. When it is finished, manually leave tempting content without changing the display or accessibility.")
        "unused_app_reinstallation" -> profileText("Garde l'application installée et choisis seulement la fonction qui t'est utile. Une fois ce besoin traité, quitte le contenu facultatif ; aucune suppression n'est nécessaire.", "Keep the app installed and choose only the function useful to you. Once that need is handled, leave optional content; no removal is needed.")
        "family_meal" -> profileText("Choisis un petit moment d'attention au repas et aux personnes présentes. Après les usages nécessaires du téléphone, quitte le contenu facultatif pour retrouver ce moment.", "Choose a small moment of attention to the meal and people present. After necessary phone use, leave optional content to return to that moment.")
        "friend_conversation" -> profileText("Choisis de suivre une réponse de ton ami jusqu'au bout, si la conversation est disponible. Termine seulement le contenu qui détourne ton attention, en gardant les contacts nécessaires possibles.", "Choose to follow one of your friend's replies to the end if the conversation is available. End only content that diverts your attention while keeping necessary contact possible.")
        "group_chat_noise" -> profileText("Reste dans le groupe et lis les informations qui répondent à ton besoin, puis quitte manuellement la conversation. Garde ses alertes telles quelles et réponds aux demandes essentielles.", "Stay in the group and read information that meets your need, then manually leave the conversation. Keep its alerts as they are and respond to essential requests.")
        "instant_reply_pressure" -> profileText("Choisis quand répondre en fonction du besoin réel et de tes obligations. Une fois la réponse utile envoyée, quitte les échanges facultatifs sans changer les moyens de contact.", "Choose when to reply according to the real need and your responsibilities. Once the useful reply is sent, leave optional exchanges without changing contact methods.")
        "social_outing_recording" -> profileText("Choisis ce que tu veux garder de la sortie, puis décide de la fin des photos ou de la publication. Quitte ce contenu pour retrouver la scène présente lorsque cela te convient.", "Choose what you want to keep from the outing, then decide when photographs or posting end. Leave that content to return to the present scene when that suits you.")
        "shared_weekend_activity" -> profileText("Choisis un petit moment partagé qui convient aux personnes présentes. Termine ton propre contenu de loisir pour y participer, sans imposer de changement aux téléphones des autres.", "Choose a small shared moment that suits the people present. End your own entertainment content to take part without imposing changes on anyone else's phone.")
        "paper_reading" -> profileText("Choisis une petite lecture réellement accessible et son premier passage. Termine le contenu facultatif au moment où tu décides de lire ; aucune nouvelle restriction du téléphone n'est nécessaire.", "Choose a small reading session that is actually accessible and its first passage. End optional content when you choose to read; no new phone restriction is needed.")
        "short_outdoor_walk" -> profileText("Choisis une courte promenade adaptée si elle est possible, puis garde ton attention sur le trajet. Termine le contenu facultatif et conserve l'accès aux informations et contacts utiles.", "Choose a suitable short walk if possible, then keep your attention on the route. End optional content and preserve access to useful information and contact.")
        "gentle_movement" -> profileText("Choisis une pause de mouvement qui te convient, ou une pause immobile si tu préfères. Termine le contenu facultatif lorsque tu prends cette pause, sans changer la configuration.", "Choose a movement break that suits you, or a still break if you prefer. End optional content when you take that break without changing the configuration.")
        "cook_without_feed" -> profileText("Utilise l'écran pour l'étape utile de la recette, puis quitte le loisir avant les gestes de cuisine. La fin est une décision manuelle ; le téléphone reste disponible pour un besoin réel.", "Use the screen for the useful recipe step, then leave entertainment before cooking tasks. Ending is a manual decision; the phone stays available for a real need.")
        "creative_hobby" -> profileText("Choisis une petite idée créative ou un geste faisable avec ce qui est présent. Termine le contenu facultatif lorsque tu veux essayer cette idée, sans chercher à produire quelque chose à publier.", "Choose one small creative idea or an action possible with what is present. End optional content when you want to try that idea without aiming to produce something to post.")
        "small_tidy_up" -> profileText("Choisis une zone limitée de tes propres affaires si le rangement convient au moment. Termine le contenu facultatif quand tu commences cette étape et décide toi-même si tu continues.", "Choose a limited area of your own belongings if tidying suits the moment. End optional content when you start that step and decide for yourself whether to continue.")
        "realistic_daily_budget" -> profileText("Choisis un moment d'usage facultatif et une fin que tu peux essayer dans ta vraie journée. Termine manuellement à ce point, sans transformer le repère en blocage du téléphone.", "Choose one optional-use moment and an ending you can try during your actual day. Manually end at that point without turning the cue into a phone block.")
        "limit_reached_next_step" -> profileText("Après une limite atteinte, choisis seulement la prochaine portion de temps à protéger. Termine le contenu facultatif lorsque tu reprends l'activité choisie, sans chercher à récupérer toute la journée.", "After reaching a limit, choose only the next portion of time to protect. End optional content when you return to the chosen activity without trying to recover the whole day.")
        "restart_after_lapse" -> profileText("Choisis une prochaine action assez simple après l'écart. Termine volontairement le contenu qui ne la sert pas et observe cette reprise, sans durcir les réglages.", "Choose a simple enough next action after a lapse. Deliberately end content that does not serve it and notice that restart without tightening settings.")
        "multi_day_challenge_habit" -> profileText("Choisis un repère et un premier pas faisable pour la prochaine journée de l'habitude. Termine manuellement le contenu facultatif lorsque tu décides de reprendre ce pas.", "Choose a cue and a feasible first step for the next day of the habit. Manually end optional content when you choose to return to that step.")
        "notice_small_wins" -> profileText("Repère un moment où tu as terminé un contenu facultatif pour retrouver une activité choisie. Garde ce choix comme exemple à répéter, même sans modification du téléphone.", "Notice one moment when you ended optional content to return to a chosen activity. Keep that choice as an example to repeat even without a phone change.")
        "weekend_routine_change" -> profileText("Choisis un repère souple du week-end et une fin manuelle pour une session de loisir. Retrouve une activité choisie à ce repère sans chercher à reproduire toute la semaine.", "Choose a flexible weekend cue and a manual ending for an entertainment session. Return to a chosen activity at that cue without trying to reproduce the whole week.")
        else -> profileText("Choisis un besoin utile et un point où le contenu facultatif peut se terminer. Quitte-le manuellement lorsque ce besoin est traité.", "Choose a useful need and a point where optional content can end. Leave it manually when that need is handled.")
    }
    return profileText(
        "Dans le contexte « ${base.label.fr} » : ${purpose.fr} Les fonctions essentielles et tes outils d'accessibilité restent disponibles ; tous les réglages restent inchangés.",
        "In the context of “${base.label.en}”: ${purpose.en} Essential functions and your accessibility tools stay available; all settings remain unchanged."
    )
}
