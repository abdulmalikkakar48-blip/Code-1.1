package com.maree.app

/** Authored bilingual situations; combined with 48 profiles and 24 coaching decisions. */
internal val extendedCoachLibraryScenarios = listOf(
    CoachLibraryScenario(
        id = "exam_revision", minimumPlan = PlanType.PREMIUM,
        label = CoachLibraryText("pendant les révisions", "during exam revision"), questionContext = CoachLibraryText("pendant les révisions", "during exam revision"),
        situation = CoachLibraryText("Le moment à organiser : pendant les révisions.", "The moment to organise: during exam revision."),
        smallAction = CoachLibraryText("Termine un seul exercice avant la prochaine consultation facultative.", "Finish one exercise before your next optional check."), alternative = CoachLibraryText("Relis une consigne sur papier déjà disponible.", "Reread an instruction on paper you already have."),
        barrier = CoachLibraryText("Les recherches utiles se mélangent aux vidéos de loisir.", "Useful searches blend into entertainment videos."), goal = CoachLibraryText("Sépare une recherche scolaire de la suite des recommandations.", "Separate one study search from the recommendations that follow.")
    ),
    CoachLibraryScenario(
        id = "remote_meeting", minimumPlan = PlanType.PREMIUM,
        label = CoachLibraryText("entre deux réunions", "between meetings"), questionContext = CoachLibraryText("entre deux réunions", "between meetings"),
        situation = CoachLibraryText("Le moment à organiser : entre deux réunions.", "The moment to organise: between meetings."),
        smallAction = CoachLibraryText("À la fin de la réunion, quitte son écran et nomme ta prochaine tâche.", "When the meeting ends, leave its screen and name your next task."), alternative = CoachLibraryText("Prépare mentalement une phrase pour la prochaine réunion.", "Mentally prepare one sentence for the next meeting."),
        barrier = CoachLibraryText("Le navigateur reste ouvert et invite à une consultation sans but.", "An open browser invites aimless checking."), goal = CoachLibraryText("Garde une transition choisie entre deux rendez-vous.", "Keep a deliberate transition between appointments.")
    ),
    CoachLibraryScenario(
        id = "lunch_break", minimumPlan = PlanType.PREMIUM,
        label = CoachLibraryText("pendant une pause déjeuner", "during a lunch break"), questionContext = CoachLibraryText("pendant une pause déjeuner", "during a lunch break"),
        situation = CoachLibraryText("Le moment à organiser : pendant une pause déjeuner.", "The moment to organise: during a lunch break."),
        smallAction = CoachLibraryText("Décide si cette pause sert à manger, discuter ou consulter une information précise.", "Decide whether this break is for eating, talking or checking specific information."), alternative = CoachLibraryText("Prends quelques bouchées sans contenu nouveau.", "Have a few bites without new content."),
        barrier = CoachLibraryText("Une pause limitée peut être entièrement absorbée par le fil.", "A limited break can be swallowed by a feed."), goal = CoachLibraryText("Retrouve une partie du déjeuner pour toi.", "Keep part of lunch for yourself.")
    ),
    CoachLibraryScenario(
        id = "waiting_reply", minimumPlan = PlanType.PREMIUM,
        label = CoachLibraryText("en attendant une réponse importante", "while waiting for an important reply"), questionContext = CoachLibraryText("en attendant une réponse importante", "while waiting for an important reply"),
        situation = CoachLibraryText("Le moment à organiser : en attendant une réponse importante.", "The moment to organise: while waiting for an important reply."),
        smallAction = CoachLibraryText("Garde le canal utile disponible et évite de rouvrir la conversation sans nouveau message.", "Keep the useful channel available and avoid reopening the conversation without a new message."), alternative = CoachLibraryText("Choisis une tâche courte que tu peux interrompre.", "Choose a short task you can interrupt."),
        barrier = CoachLibraryText("Vérifier ne fait pas arriver la réponse plus vite.", "Checking does not make the reply arrive sooner."), goal = CoachLibraryText("Reste joignable sans consulter en continu.", "Stay reachable without checking continuously.")
    ),
    CoachLibraryScenario(
        id = "morning_rush", minimumPlan = PlanType.PREMIUM,
        label = CoachLibraryText("quand le matin est pressé", "on a rushed morning"), questionContext = CoachLibraryText("quand le matin est pressé", "on a rushed morning"),
        situation = CoachLibraryText("Le moment à organiser : quand le matin est pressé.", "The moment to organise: on a rushed morning."),
        smallAction = CoachLibraryText("Vérifie seulement une information nécessaire au départ, puis reviens à la préparation.", "Check only information needed for leaving, then return to getting ready."), alternative = CoachLibraryText("Prépare un objet dont tu as besoin pour sortir.", "Prepare an item you need before leaving."),
        barrier = CoachLibraryText("Les nouvelles peuvent retarder une obligation concrète.", "News can delay a concrete responsibility."), goal = CoachLibraryText("Protège le départ sans ajouter une routine compliquée.", "Protect your departure without adding a complex routine.")
    ),
    CoachLibraryScenario(
        id = "after_notification", minimumPlan = PlanType.PREMIUM,
        label = CoachLibraryText("après une notification non urgente", "after a non-urgent notification"), questionContext = CoachLibraryText("après une notification non urgente", "after a non-urgent notification"),
        situation = CoachLibraryText("Le moment à organiser : après une notification non urgente.", "The moment to organise: after a non-urgent notification."),
        smallAction = CoachLibraryText("Lis son aperçu si nécessaire, puis décide si une réponse est utile maintenant.", "Read its preview if necessary, then decide whether a reply is useful now."), alternative = CoachLibraryText("Reprends la dernière étape de ta tâche.", "Resume the last step of your task."),
        barrier = CoachLibraryText("Un signal attire vers plusieurs contenus sans lien avec lui.", "An alert pulls you into unrelated content."), goal = CoachLibraryText("Traite une alerte sans ouvrir toute une session.", "Handle one alert without starting a whole session.")
    ),
    CoachLibraryScenario(
        id = "weekend_plan", minimumPlan = PlanType.PREMIUM,
        label = CoachLibraryText("avant une sortie prévue", "before a planned outing"), questionContext = CoachLibraryText("avant une sortie prévue", "before a planned outing"),
        situation = CoachLibraryText("Le moment à organiser : avant une sortie prévue.", "The moment to organise: before a planned outing."),
        smallAction = CoachLibraryText("Consulte les détails utiles une fois et prépare ce qui manque pour partir.", "Check useful details once and prepare what you need before leaving."), alternative = CoachLibraryText("Repère mentalement le lieu et le trajet.", "Mentally identify the destination and route."),
        barrier = CoachLibraryText("Attendre la sortie peut déclencher une longue session.", "Waiting for the outing can trigger a long session."), goal = CoachLibraryText("Utilise le téléphone pour préparer la sortie réelle.", "Use your phone to prepare for the real outing.")
    ),
    CoachLibraryScenario(
        id = "friend_present", minimumPlan = PlanType.PREMIUM,
        label = CoachLibraryText("quand un ami est présent", "when a friend is present"), questionContext = CoachLibraryText("quand un ami est présent", "when a friend is present"),
        situation = CoachLibraryText("Le moment à organiser : quand un ami est présent.", "The moment to organise: when a friend is present."),
        smallAction = CoachLibraryText("Si le moment s’y prête, pose une question à ton ami avant de consulter le loisir.", "If the moment suits it, ask your friend a question before checking entertainment."), alternative = CoachLibraryText("Écoute une réponse sans regarder un autre contenu.", "Listen to an answer without watching other content."),
        barrier = CoachLibraryText("Les deux personnes peuvent consulter sans le choisir ensemble.", "Both people may check without choosing it together."), goal = CoachLibraryText("Choisis un échange sans imposer ta règle à ton ami.", "Choose a conversation without imposing your rule on your friend.")
    ),
    CoachLibraryScenario(
        id = "music_only", minimumPlan = PlanType.PREMIUM,
        label = CoachLibraryText("quand je veux seulement écouter de la musique", "when I only want to listen to music"), questionContext = CoachLibraryText("quand je veux seulement écouter de la musique", "when I only want to listen to music"),
        situation = CoachLibraryText("Le moment à organiser : quand je veux seulement écouter de la musique.", "The moment to organise: when I only want to listen to music."),
        smallAction = CoachLibraryText("Choisis la piste voulue, puis quitte le fil visuel sans interrompre l’écoute nécessaire.", "Choose your track, then leave the visual feed without interrupting the listening you need."), alternative = CoachLibraryText("Écoute un passage en portant attention à un instrument.", "Listen to a passage while noticing an instrument."),
        barrier = CoachLibraryText("Choisir une piste peut se transformer en navigation vidéo.", "Choosing a track can turn into video browsing."), goal = CoachLibraryText("Sépare l’écoute choisie du défilement.", "Separate deliberate listening from scrolling.")
    ),
    CoachLibraryScenario(
        id = "recipe_lookup", minimumPlan = PlanType.PREMIUM,
        label = CoachLibraryText("quand je cherche une recette", "when looking up a recipe"), questionContext = CoachLibraryText("quand je cherche une recette", "when looking up a recipe"),
        situation = CoachLibraryText("Le moment à organiser : quand je cherche une recette.", "The moment to organise: when looking up a recipe."),
        smallAction = CoachLibraryText("Repère la prochaine instruction, puis éloigne l’écran des gestes de cuisine.", "Find the next instruction, then keep the screen away from cooking tasks."), alternative = CoachLibraryText("Prépare un ingrédient déjà présent.", "Prepare an ingredient you already have."),
        barrier = CoachLibraryText("Les liens de recettes conduisent à des contenus supplémentaires.", "Recipe links lead to additional content."), goal = CoachLibraryText("Termine la recherche utile avant de cuisiner.", "Finish the useful search before cooking.")
    ),
    CoachLibraryScenario(
        id = "shopping_compare", minimumPlan = PlanType.PREMIUM,
        label = CoachLibraryText("quand je compare un achat", "when comparing a purchase"), questionContext = CoachLibraryText("quand je compare un achat", "when comparing a purchase"),
        situation = CoachLibraryText("Le moment à organiser : quand je compare un achat.", "The moment to organise: when comparing a purchase."),
        smallAction = CoachLibraryText("Note mentalement deux critères utiles et compare seulement les options qui y répondent.", "Name two useful criteria and compare only options that meet them."), alternative = CoachLibraryText("Reporte un achat facultatif si tu hésites encore.", "Postpone an optional purchase if you are still unsure."),
        barrier = CoachLibraryText("Les recommandations entretiennent une comparaison sans fin.", "Recommendations keep an endless comparison going."), goal = CoachLibraryText("Décide d’un point d’arrêt sans achat imposé.", "Choose a stopping point without being forced to buy.")
    ),
    CoachLibraryScenario(
        id = "news_overload", minimumPlan = PlanType.PREMIUM,
        label = CoachLibraryText("quand les actualités se répètent", "when news repeats"), questionContext = CoachLibraryText("quand les actualités se répètent", "when news repeats"),
        situation = CoachLibraryText("Le moment à organiser : quand les actualités se répètent.", "The moment to organise: when news repeats."),
        smallAction = CoachLibraryText("Choisis une information à vérifier, puis arrête les commentaires répétitifs.", "Choose one fact to verify, then stop repetitive comments."), alternative = CoachLibraryText("Retrouve une activité quotidienne déjà prévue.", "Return to an everyday activity you already planned."),
        barrier = CoachLibraryText("Relire les mêmes nouvelles peut maintenir une tension.", "Rereading the same news can maintain tension."), goal = CoachLibraryText("Reste informé sans suivre chaque répétition.", "Stay informed without following every repetition.")
    ),
    CoachLibraryScenario(
        id = "late_game_invite", minimumPlan = PlanType.PREMIUM,
        label = CoachLibraryText("quand une invitation de jeu arrive tard", "when a late game invitation arrives"), questionContext = CoachLibraryText("quand une invitation de jeu arrive tard", "when a late game invitation arrives"),
        situation = CoachLibraryText("Le moment à organiser : quand une invitation de jeu arrive tard.", "The moment to organise: when a late game invitation arrives."),
        smallAction = CoachLibraryText("Décide avant de rejoindre si le créneau convient à ton heure de repos.", "Before joining, decide whether the session fits your rest time."), alternative = CoachLibraryText("Propose un autre créneau si tu le souhaites.", "Suggest another time if you wish."),
        barrier = CoachLibraryText("La peur de décevoir peut remplacer ton propre choix.", "Fear of disappointing others can replace your own choice."), goal = CoachLibraryText("Garde une réponse claire et une fin choisie.", "Keep a clear reply and a chosen ending.")
    ),
    CoachLibraryScenario(
        id = "long_download", minimumPlan = PlanType.PREMIUM,
        label = CoachLibraryText("pendant un téléchargement", "during a download"), questionContext = CoachLibraryText("pendant un téléchargement", "during a download"),
        situation = CoachLibraryText("Le moment à organiser : pendant un téléchargement.", "The moment to organise: during a download."),
        smallAction = CoachLibraryText("Lance seulement le téléchargement utile et reviens à autre chose pendant l’attente.", "Start only the useful download and return to something else while waiting."), alternative = CoachLibraryText("Prépare la prochaine étape après réception du fichier.", "Prepare the next step after receiving the file."),
        barrier = CoachLibraryText("L’attente devant l’écran invite à ouvrir un fil.", "Waiting at the screen invites opening a feed."), goal = CoachLibraryText("Laisse une tâche technique avancer sans surveiller en continu.", "Let a technical task progress without watching it continuously.")
    ),
    CoachLibraryScenario(
        id = "photo_sorting", minimumPlan = PlanType.PREMIUM,
        label = CoachLibraryText("quand je trie mes photos", "when sorting photos"), questionContext = CoachLibraryText("quand je trie mes photos", "when sorting photos"),
        situation = CoachLibraryText("Le moment à organiser : quand je trie mes photos.", "The moment to organise: when sorting photos."),
        smallAction = CoachLibraryText("Choisis un petit lot de photos et termine ce lot avant de passer à autre chose.", "Choose a small batch of photos and finish that batch before doing something else."), alternative = CoachLibraryText("Garde un souvenir que tu apprécies sans le publier.", "Keep a memory you like without posting it."),
        barrier = CoachLibraryText("Le tri peut conduire aux réseaux et aux anciennes conversations.", "Sorting can lead to social feeds and old conversations."), goal = CoachLibraryText("Finis un lot utile sans prolonger la session.", "Finish a useful batch without extending the session.")
    ),
    CoachLibraryScenario(
        id = "podcast_end", minimumPlan = PlanType.ULTRA,
        label = CoachLibraryText("à la fin d’un podcast", "at the end of a podcast"), questionContext = CoachLibraryText("à la fin d’un podcast", "at the end of a podcast"),
        situation = CoachLibraryText("Le moment à organiser : à la fin d’un podcast.", "The moment to organise: at the end of a podcast."),
        smallAction = CoachLibraryText("Choisis si tu veux vraiment un nouvel épisode avant de le lancer.", "Choose whether you really want another episode before starting it."), alternative = CoachLibraryText("Résume mentalement une idée retenue.", "Mentally summarise one idea you remember."),
        barrier = CoachLibraryText("L’épisode suivant peut démarrer par habitude.", "The next episode can start out of habit."), goal = CoachLibraryText("Garde une écoute avec un début et une fin.", "Keep listening with a beginning and an ending.")
    ),
    CoachLibraryScenario(
        id = "study_group", minimumPlan = PlanType.ULTRA,
        label = CoachLibraryText("pendant un travail de groupe", "during group study"), questionContext = CoachLibraryText("pendant un travail de groupe", "during group study"),
        situation = CoachLibraryText("Le moment à organiser : pendant un travail de groupe.", "The moment to organise: during group study."),
        smallAction = CoachLibraryText("Distingue le canal du projet des discussions de loisir et traite la demande utile.", "Separate the project channel from entertainment chats and handle the useful request."), alternative = CoachLibraryText("Reprends une petite contribution au travail commun.", "Resume one small contribution to the shared work."),
        barrier = CoachLibraryText("Une discussion utile peut dériver sans que tu le remarques.", "A useful discussion can drift without you noticing."), goal = CoachLibraryText("Contribue au groupe sans consulter tous les fils.", "Contribute to the group without checking every feed.")
    ),
    CoachLibraryScenario(
        id = "task_finished", minimumPlan = PlanType.ULTRA,
        label = CoachLibraryText("juste après une tâche terminée", "right after finishing a task"), questionContext = CoachLibraryText("juste après une tâche terminée", "right after finishing a task"),
        situation = CoachLibraryText("Le moment à organiser : juste après une tâche terminée.", "The moment to organise: right after finishing a task."),
        smallAction = CoachLibraryText("Décide de la récompense que tu veux avant d’ouvrir une application.", "Decide which reward you want before opening an app."), alternative = CoachLibraryText("Profite quelques instants du résultat de ta tâche.", "Spend a moment enjoying the result of your task."),
        barrier = CoachLibraryText("Une récompense automatique peut durer bien plus que prévu.", "An automatic reward can last much longer than planned."), goal = CoachLibraryText("Choisis une pause qui a une fin reconnaissable.", "Choose a break with a recognisable ending.")
    ),
    CoachLibraryScenario(
        id = "week_reset", minimumPlan = PlanType.ULTRA,
        label = CoachLibraryText("au début d’une nouvelle semaine", "at the beginning of a new week"), questionContext = CoachLibraryText("au début d’une nouvelle semaine", "at the beginning of a new week"),
        situation = CoachLibraryText("Le moment à organiser : au début d’une nouvelle semaine.", "The moment to organise: at the beginning of a new week."),
        smallAction = CoachLibraryText("Choisis un seul moment où tu veux réduire le loisir cette semaine.", "Choose one moment when you want to reduce entertainment this week."), alternative = CoachLibraryText("Prépare mentalement une petite alternative pour ce moment.", "Mentally prepare a small alternative for that moment."),
        barrier = CoachLibraryText("Changer toutes les habitudes à la fois rend le plan lourd.", "Changing every habit at once makes the plan heavy."), goal = CoachLibraryText("Essaie un repère réaliste plutôt qu’un programme parfait.", "Try one realistic cue rather than a perfect programme.")
    ),
    CoachLibraryScenario(
        id = "rainy_day", minimumPlan = PlanType.ULTRA,
        label = CoachLibraryText("quand une sortie est annulée", "when an outing is cancelled"), questionContext = CoachLibraryText("quand une sortie est annulée", "when an outing is cancelled"),
        situation = CoachLibraryText("Le moment à organiser : quand une sortie est annulée.", "The moment to organise: when an outing is cancelled."),
        smallAction = CoachLibraryText("Choisis une activité intérieure déjà possible avant de remplir l’attente avec le fil.", "Choose an indoor activity already possible before filling the wait with a feed."), alternative = CoachLibraryText("Rappelle-toi une idée créative ou un texte connu.", "Recall a creative idea or a familiar text."),
        barrier = CoachLibraryText("Un plan annulé laisse du temps sans direction.", "A cancelled plan leaves time without direction."), goal = CoachLibraryText("Remplace le plan sans exiger un achat ou une sortie.", "Replace the plan without requiring a purchase or outing.")
    ),
    CoachLibraryScenario(
        id = "travel_delay", minimumPlan = PlanType.ULTRA,
        label = CoachLibraryText("quand mon trajet est retardé", "when my journey is delayed"), questionContext = CoachLibraryText("quand mon trajet est retardé", "when my journey is delayed"),
        situation = CoachLibraryText("Le moment à organiser : quand mon trajet est retardé.", "The moment to organise: when my journey is delayed."),
        smallAction = CoachLibraryText("Vérifie l’information de transport utile puis reste attentif aux annonces.", "Check useful travel information, then stay aware of announcements."), alternative = CoachLibraryText("Observe un détail depuis un endroit sûr.", "Notice a detail from a safe place."),
        barrier = CoachLibraryText("Les mises à jour et le loisir se mélangent pendant l’attente.", "Updates and entertainment blend during the wait."), goal = CoachLibraryText("Garde les informations utiles sans défilement continu.", "Keep useful information without continuous scrolling.")
    ),
    CoachLibraryScenario(
        id = "family_table", minimumPlan = PlanType.ULTRA,
        label = CoachLibraryText("pendant un repas en famille", "during a family meal"), questionContext = CoachLibraryText("pendant un repas en famille", "during a family meal"),
        situation = CoachLibraryText("Le moment à organiser : pendant un repas en famille.", "The moment to organise: during a family meal."),
        smallAction = CoachLibraryText("Si cela te convient, termine une consultation facultative avant de commencer l’échange.", "If it suits you, finish an optional check before joining the conversation."), alternative = CoachLibraryText("Écoute une personne présente sans lui imposer une règle.", "Listen to someone present without imposing a rule on them."),
        barrier = CoachLibraryText("Le téléphone sur la table peut attirer même sans alerte.", "A phone on the table can attract attention without alerts."), goal = CoachLibraryText("Choisis une présence que tu peux tenir pendant ce repas.", "Choose a level of presence you can keep during this meal.")
    ),
    CoachLibraryScenario(
        id = "sport_scores", minimumPlan = PlanType.ULTRA,
        label = CoachLibraryText("quand je consulte les résultats sportifs", "when checking sports scores"), questionContext = CoachLibraryText("quand je consulte les résultats sportifs", "when checking sports scores"),
        situation = CoachLibraryText("Le moment à organiser : quand je consulte les résultats sportifs.", "The moment to organise: when checking sports scores."),
        smallAction = CoachLibraryText("Vérifie le score voulu puis décide si tu souhaites réellement lire une analyse.", "Check the score you wanted, then decide whether you really want to read an analysis."), alternative = CoachLibraryText("Reprends l’activité interrompue après le résultat.", "Resume the interrupted activity after checking the result."),
        barrier = CoachLibraryText("Les commentaires peuvent prolonger une simple vérification.", "Comments can extend a simple check."), goal = CoachLibraryText("Sépare résultat utile et loisir choisi.", "Separate the useful result from deliberate entertainment.")
    ),
    CoachLibraryScenario(
        id = "serial_episode", minimumPlan = PlanType.ULTRA,
        label = CoachLibraryText("entre deux épisodes", "between episodes"), questionContext = CoachLibraryText("entre deux épisodes", "between episodes"),
        situation = CoachLibraryText("Le moment à organiser : entre deux épisodes.", "The moment to organise: between episodes."),
        smallAction = CoachLibraryText("À la fin de l’épisode, décide avant de lancer le suivant.", "At the end of the episode, decide before starting the next one."), alternative = CoachLibraryText("Remarque l’heure et ce que tu veux faire ensuite.", "Notice the time and what you want to do next."),
        barrier = CoachLibraryText("Le lancement automatique réduit le moment de décision.", "Autoplay reduces the moment available for deciding."), goal = CoachLibraryText("Garde une décision entre deux contenus.", "Keep a decision between pieces of content.")
    ),
    CoachLibraryScenario(
        id = "social_post", minimumPlan = PlanType.ULTRA,
        label = CoachLibraryText("après avoir publié quelque chose", "after posting something"), questionContext = CoachLibraryText("après avoir publié quelque chose", "after posting something"),
        situation = CoachLibraryText("Le moment à organiser : après avoir publié quelque chose.", "The moment to organise: after posting something."),
        smallAction = CoachLibraryText("Choisis une prochaine consultation plutôt que de surveiller chaque réaction.", "Choose your next check instead of watching every reaction."), alternative = CoachLibraryText("Reviens à une activité qui existe sans publication.", "Return to an activity that exists without posting."),
        barrier = CoachLibraryText("Le compteur peut devenir plus important que l’échange.", "The counter can become more important than the exchange."), goal = CoachLibraryText("Laisse les réactions arriver à leur rythme.", "Let reactions arrive at their own pace.")
    ),
    CoachLibraryScenario(
        id = "learning_video", minimumPlan = PlanType.ULTRA,
        label = CoachLibraryText("après une vidéo éducative", "after an educational video"), questionContext = CoachLibraryText("après une vidéo éducative", "after an educational video"),
        situation = CoachLibraryText("Le moment à organiser : après une vidéo éducative.", "The moment to organise: after an educational video."),
        smallAction = CoachLibraryText("Essaie une petite idée retenue avant de lancer une autre vidéo.", "Try one small idea you remember before starting another video."), alternative = CoachLibraryText("Résume ce que tu as appris en une phrase.", "Summarise what you learned in one sentence."),
        barrier = CoachLibraryText("Accumuler des vidéos peut remplacer la pratique.", "Collecting videos can replace practice."), goal = CoachLibraryText("Transforme une information en un essai concret.", "Turn one piece of information into a concrete attempt.")
    ),
    CoachLibraryScenario(
        id = "work_email", minimumPlan = PlanType.ULTRA,
        label = CoachLibraryText("après un e-mail de travail", "after a work email"), questionContext = CoachLibraryText("après un e-mail de travail", "after a work email"),
        situation = CoachLibraryText("Le moment à organiser : après un e-mail de travail.", "The moment to organise: after a work email."),
        smallAction = CoachLibraryText("Traite le besoin utile du message puis reviens à la tâche choisie.", "Handle the useful need in the message, then return to your chosen task."), alternative = CoachLibraryText("Prépare mentalement la prochaine étape du travail.", "Mentally prepare the next work step."),
        barrier = CoachLibraryText("La messagerie ouverte invite à vérifier d’autres canaux.", "An open inbox invites checking other channels."), goal = CoachLibraryText("Termine un échange sans lancer une session de loisir.", "Finish one exchange without starting an entertainment session.")
    ),
    CoachLibraryScenario(
        id = "calendar_check", minimumPlan = PlanType.ULTRA,
        label = CoachLibraryText("quand je consulte mon agenda", "when checking my calendar"), questionContext = CoachLibraryText("quand je consulte mon agenda", "when checking my calendar"),
        situation = CoachLibraryText("Le moment à organiser : quand je consulte mon agenda.", "The moment to organise: when checking my calendar."),
        smallAction = CoachLibraryText("Repère le rendez-vous et l’action nécessaire, puis ferme les contenus sans rapport.", "Find the appointment and necessary action, then close unrelated content."), alternative = CoachLibraryText("Prépare ce dont tu as besoin pour le rendez-vous.", "Prepare what you need for the appointment."),
        barrier = CoachLibraryText("Une consultation rapide peut ouvrir plusieurs applications.", "A quick check can open several apps."), goal = CoachLibraryText("Garde l’usage de l’agenda lié à un besoin précis.", "Keep calendar use connected to a specific need.")
    ),
    CoachLibraryScenario(
        id = "quiet_evening", minimumPlan = PlanType.ULTRA,
        label = CoachLibraryText("quand la soirée est sans programme", "on an evening without plans"), questionContext = CoachLibraryText("quand la soirée est sans programme", "on an evening without plans"),
        situation = CoachLibraryText("Le moment à organiser : quand la soirée est sans programme.", "The moment to organise: on an evening without plans."),
        smallAction = CoachLibraryText("Choisis d’abord ce qui ferait une pause agréable avant de consulter le fil.", "First choose what would make an enjoyable break before checking a feed."), alternative = CoachLibraryText("Garde un moment tranquille sans produire quoi que ce soit.", "Keep a quiet moment without producing anything."),
        barrier = CoachLibraryText("Un temps libre peut devenir une session par défaut.", "Free time can become a default session."), goal = CoachLibraryText("Choisis le repos plutôt que de mesurer une performance.", "Choose rest rather than measuring performance.")
    ),
    CoachLibraryScenario(
        id = "habit_restart", minimumPlan = PlanType.ULTRA,
        label = CoachLibraryText("après plusieurs jours sans mon repère", "after several days without my cue"), questionContext = CoachLibraryText("après plusieurs jours sans mon repère", "after several days without my cue"),
        situation = CoachLibraryText("Le moment à organiser : après plusieurs jours sans mon repère.", "The moment to organise: after several days without my cue."),
        smallAction = CoachLibraryText("Reprends un seul geste accessible, sans rattraper les jours passés.", "Resume one accessible action without making up for past days."), alternative = CoachLibraryText("Reporte ce geste si le moment ne convient pas.", "Postpone the action if the moment does not suit it."),
        barrier = CoachLibraryText("L’envie de rattraper peut rendre la reprise trop difficile.", "Trying to catch up can make restarting too difficult."), goal = CoachLibraryText("Retrouve une petite continuité sans punition.", "Recover a little continuity without punishment.")
    ),
    CoachLibraryScenario(
        id = "busy_inbox", minimumPlan = PlanType.ULTRA,
        label = CoachLibraryText("quand plusieurs messages arrivent ensemble", "when several messages arrive together"), questionContext = CoachLibraryText("quand plusieurs messages arrivent ensemble", "when several messages arrive together"),
        situation = CoachLibraryText("Le moment à organiser : quand plusieurs messages arrivent ensemble.", "The moment to organise: when several messages arrive together."),
        smallAction = CoachLibraryText("Distingue une demande urgente des autres et réponds selon tes obligations.", "Distinguish an urgent request from the others and reply according to your responsibilities."), alternative = CoachLibraryText("Garde les échanges facultatifs pour un créneau choisi.", "Save optional exchanges for a chosen time."),
        barrier = CoachLibraryText("Le nombre de messages peut donner une fausse urgence à tous.", "The number of messages can make every message feel urgent."), goal = CoachLibraryText("Priorise les besoins sans couper les contacts essentiels.", "Prioritise needs without cutting essential contact.")
    ),
    CoachLibraryScenario(
        id = "app_update", minimumPlan = PlanType.ULTRA,
        label = CoachLibraryText("après une mise à jour d’application", "after an app update"), questionContext = CoachLibraryText("après une mise à jour d’application", "after an app update"),
        situation = CoachLibraryText("Le moment à organiser : après une mise à jour d’application.", "The moment to organise: after an app update."),
        smallAction = CoachLibraryText("Vérifie seulement le changement qui concerne ton usage réel.", "Check only the change relevant to your actual use."), alternative = CoachLibraryText("Reviens à la fonction pour laquelle tu avais ouvert l’application.", "Return to the function for which you opened the app."),
        barrier = CoachLibraryText("La nouveauté encourage à explorer sans fin choisie.", "Novelty encourages exploring without a chosen ending."), goal = CoachLibraryText("Découvre une fonction utile sans te sentir obligé de tout voir.", "Discover a useful feature without feeling you must see everything.")
    ),
    CoachLibraryScenario(
        id = "week_progress", minimumPlan = PlanType.ULTRA,
        label = CoachLibraryText("quand je regarde mes progrès", "when looking at my progress"), questionContext = CoachLibraryText("quand je regarde mes progrès", "when looking at my progress"),
        situation = CoachLibraryText("Le moment à organiser : quand je regarde mes progrès.", "The moment to organise: when looking at my progress."),
        smallAction = CoachLibraryText("Remarque un choix utile et une difficulté concrète plutôt qu’une note sur toi-même.", "Notice one useful choice and one concrete difficulty rather than judging yourself."), alternative = CoachLibraryText("Garde la petite action qui t’a aidé.", "Keep the small action that helped you."),
        barrier = CoachLibraryText("Un compteur peut faire oublier le contexte réel.", "A counter can hide the real context."), goal = CoachLibraryText("Utilise les chiffres comme repères, pas comme valeur personnelle.", "Use numbers as cues, not as your personal worth.")
    ),
    CoachLibraryScenario(
        id = "free_afternoon", minimumPlan = PlanType.ULTRA,
        label = CoachLibraryText("pendant un après-midi libre", "during a free afternoon"), questionContext = CoachLibraryText("pendant un après-midi libre", "during a free afternoon"),
        situation = CoachLibraryText("Le moment à organiser : pendant un après-midi libre.", "The moment to organise: during a free afternoon."),
        smallAction = CoachLibraryText("Choisis une activité souhaitée et un moment de transition avant une session facultative.", "Choose a wanted activity and a transition point before an optional session."), alternative = CoachLibraryText("Commence seulement la première étape de cette activité.", "Start only the first step of that activity."),
        barrier = CoachLibraryText("L’absence d’horaire peut rendre la fin invisible.", "A lack of schedule can make the ending invisible."), goal = CoachLibraryText("Garde une fin souple qui protège ce que tu veux faire.", "Keep a flexible ending that protects what you want to do.")
    ),
    CoachLibraryScenario(
        id = "before_sleep_check", minimumPlan = PlanType.ULTRA,
        label = CoachLibraryText("pour une dernière vérification avant le repos", "for a final check before resting"), questionContext = CoachLibraryText("pour une dernière vérification avant le repos", "for a final check before resting"),
        situation = CoachLibraryText("Le moment à organiser : pour une dernière vérification avant le repos.", "The moment to organise: for a final check before resting."),
        smallAction = CoachLibraryText("Vérifie uniquement le besoin essentiel, puis laisse les recommandations pour plus tard.", "Check only the essential need, then leave recommendations for later."), alternative = CoachLibraryText("Reprends ta préparation au repos déjà commencée.", "Resume the preparation for rest you already started."),
        barrier = CoachLibraryText("Une dernière vérification peut relancer tout le fil.", "A final check can restart the whole feed."), goal = CoachLibraryText("Termine le besoin utile sans ajouter une nouvelle session.", "Finish the useful need without adding a new session.")
    )
)
