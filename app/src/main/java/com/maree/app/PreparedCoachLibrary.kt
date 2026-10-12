package com.maree.app

import java.text.Normalizer
import java.util.Collections
import java.util.Locale

/** Both translations are authored together; no global language state or remote service is used. */
data class CoachLibraryText(val fr: String, val en: String) {
    fun forLanguage(language: AppLocale): String = if (language == AppLocale.ENGLISH) en else fr
}

/** A concrete context, with its own action, alternative, obstacle and realistic objective. */
data class CoachLibraryProfile(
    val id: String,
    val sectionId: String,
    val parentTopicId: String,
    val label: CoachLibraryText,
    val situation: CoachLibraryText,
    val smallAction: CoachLibraryText,
    val alternative: CoachLibraryText,
    val barrier: CoachLibraryText,
    val goal: CoachLibraryText
)

data class PreparedCoachSection(val id: String, val label: String)

data class PreparedCoachQuestion(
    val id: String,
    val sectionId: String,
    val sectionLabel: String,
    val topicId: String,
    val question: String,
    val answer: String
)

private data class CoachLibraryIntent(val id: String, val question: CoachLibraryText)

// These are different coaching tasks, rather than spelling aliases or variants of one reply.
private val preparedCoachIntents = listOf(
    CoachLibraryIntent("notice_trigger", CoachLibraryText("Comment repérer ce qui me pousse vers mon téléphone", "How can I spot what makes me reach for my phone")),
    CoachLibraryIntent("two_minutes", CoachLibraryText("Quel premier pas puis-je faire en deux minutes", "What first step can I take in two minutes")),
    CoachLibraryIntent("choose_alternative", CoachLibraryText("Par quoi remplacer ce moment d'écran", "What can replace this screen moment")),
    CoachLibraryIntent("handle_obstacle", CoachLibraryText("Comment préparer l'obstacle qui risque de me faire céder", "How can I prepare for the obstacle that might pull me back")),
    CoachLibraryIntent("realistic_goal", CoachLibraryText("Comment choisir un objectif réaliste", "How can I choose a realistic goal")),
    CoachLibraryIntent("delay_urge", CoachLibraryText("Que faire quand l'envie de consulter revient tout de suite", "What can I do when the urge to check returns immediately")),
    CoachLibraryIntent("if_then", CoachLibraryText("Quelle règle simple si-alors puis-je préparer", "What simple if-then rule can I prepare")),
    CoachLibraryIntent("after_slip", CoachLibraryText("Comment repartir après un écart sans tout abandonner", "How can I restart after a slip without giving up")),
    CoachLibraryIntent("measure_progress", CoachLibraryText("Comment mesurer un progrès sans surveiller chaque minute", "How can I measure progress without watching every minute")),
    CoachLibraryIntent("tomorrow_plan", CoachLibraryText("Comment préparer un plan simple pour demain", "How can I prepare a simple plan for tomorrow")),
    CoachLibraryIntent("weekly_experiment", CoachLibraryText("Quel petit essai puis-je mener pendant une semaine", "What small experiment can I try for one week")),
    CoachLibraryIntent("low_energy", CoachLibraryText("Quelle version minimale choisir quand je manque d'énergie", "What is the smallest version I can choose when my energy is low")),
    CoachLibraryIntent("busy_day", CoachLibraryText("Comment garder ce repère pendant une journée chargée", "How can I keep this habit during a busy day")),
    CoachLibraryIntent("without_blocking", CoachLibraryText("Comment essayer de réduire sans bloquer ni supprimer une application", "How can I try reducing use without blocking or deleting an app")),
    CoachLibraryIntent("prepare_space", CoachLibraryText("Comment préparer mon environnement avant le moment difficile", "How can I prepare my surroundings before a difficult moment")),
    CoachLibraryIntent("finish_session", CoachLibraryText("Comment terminer une session au lieu de continuer par réflexe", "How can I end a session instead of carrying on automatically")),
    CoachLibraryIntent("keep_motivation", CoachLibraryText("Comment retrouver une raison concrète de continuer", "How can I find a concrete reason to keep going")),
    CoachLibraryIntent("avoid_perfection", CoachLibraryText("Comment éviter le piège du tout ou rien", "How can I avoid the all-or-nothing trap")),
    CoachLibraryIntent("ask_support", CoachLibraryText("Comment demander un soutien utile sans me sentir surveillé", "How can I ask for useful support without feeling monitored")),
    CoachLibraryIntent("weekly_review", CoachLibraryText("Quelles questions me poser au bilan de la semaine", "What questions can I ask at the end of the week")),
    CoachLibraryIntent("changed_routine", CoachLibraryText("Comment adapter mon plan si ma journée ne se passe pas comme prévu", "How can I adapt my plan when the day changes unexpectedly")),
    CoachLibraryIntent("need_phone", CoachLibraryText("Comment garder l'usage nécessaire du téléphone sans repartir dans le défilement", "How can I keep necessary phone use without sliding back into scrolling")),
    CoachLibraryIntent("reduce_gradually", CoachLibraryText("Comment réduire progressivement plutôt que tout arrêter", "How can I reduce gradually rather than stop everything")),
    CoachLibraryIntent("next_step", CoachLibraryText("Quel pas suivant choisir quand le premier devient facile", "What next step can I choose once the first one feels easy"))
)

private val preparedSectionTexts = linkedMapOf(
    "apps" to CoachLibraryText("Applications", "Apps"),
    "moments" to CoachLibraryText("Moments de la journée", "Daily moments"),
    "emotions" to CoachLibraryText("Émotions et envies", "Feelings and urges"),
    "work_study" to CoachLibraryText("Travail et études", "Work and study"),
    "environment" to CoachLibraryText("Environnement", "Surroundings"),
    "relationships" to CoachLibraryText("Relations", "Relationships"),
    "offline_activities" to CoachLibraryText("Activités sans écran", "Offline activities"),
    "consistency" to CoachLibraryText("Régularité", "Consistency")
)

private fun preparedAnswer(profile: CoachLibraryProfile, intent: String, language: AppLocale): String {
    val situation = profile.situation.forLanguage(language)
    val action = profile.smallAction.forLanguage(language)
    val alternative = profile.alternative.forLanguage(language)
    val barrier = profile.barrier.forLanguage(language)
    val goal = profile.goal.forLanguage(language)
    return if (language == AppLocale.ENGLISH) when (intent) {
        "notice_trigger" -> "$situation\n\n1. Before opening your phone, name the cue: place, time, feeling or an alert.\n2. Notice the obstacle without blaming yourself: $barrier\n3. At the next occurrence, try this instead of just counting minutes: $action"
        "two_minutes" -> "Start with one action, without promising to change the whole day.\n\n1. $action\n2. After two minutes, decide whether to continue with a useful alternative: $alternative\n3. Use this direction to choose, rather than a perfect score: $goal"
        "choose_alternative" -> "$situation\n\n1. Choose an alternative that can actually fit this moment: $alternative\n2. Prepare its first step now: $action\n3. Try it once before judging it. If it does not meet your need, change the alternative rather than forcing yourself."
        "handle_obstacle" -> "Plan for friction before it appears.\n\n1. Identify the specific obstacle: $barrier\n2. Rehearse one small response while the pressure is low: $action\n3. Keep a backup that does not depend on willpower alone: $alternative"
        "realistic_goal" -> "Choose a direction you can test, not a punishment.\n\n1. Start from this objective: $goal\n2. Turn it into one observable choice at a specific moment: $action\n3. Try it today. Tomorrow, adjust the difficulty using what happened instead of setting a harsher target."
        "delay_urge" -> "$situation\n\n1. Put a short pause between the urge and opening the app: take a breath and wait two minutes.\n2. Give those two minutes a purpose: $action\n3. If the urge stays, deliberately choose whether to use the phone or try this alternative: $alternative"
        "if_then" -> "Make the decision before the familiar moment arrives.\n\n1. Name the situation that will trigger your rule: $situation\n2. Write: “If this happens, I will take the following small action before opening another app.”\n3. Use this action as your response: $action\nKeep one rule for today so that it stays easy to remember."
        "after_slip" -> "One difficult moment does not cancel what you learned.\n\n1. Briefly name what happened, without adding a judgment about yourself.\n2. Look for the friction you can change next time: $barrier\n3. Restart with the smallest useful step: $action\nYour direction can stay the same: $goal"
        "measure_progress" -> "Choose one simple indicator for this context.\n\n1. Your direction is: $goal\n2. At the end of the day, record whether you made this choice at least once: $action\n3. Add one sentence about what helped. Use Selkie's recorded counters only when tracking is active; they cover the current tracking period, not necessarily a full day."
        "tomorrow_plan" -> "Prepare tomorrow's decision while you have room to think.\n\n1. Choose one likely moment: $situation\n2. Write your first action where you will see it: $action\n3. Prepare a fallback for a difficult moment: $alternative\nAim for this direction, without planning every hour: $goal"
        "weekly_experiment" -> "Treat the next seven days as an experiment, not a test of your worth.\n\n1. Days 1–2: try the small action once each day. $action\n2. Days 3–5: repeat it and note the obstacle that actually appears. $barrier\n3. Days 6–7: keep what helps and compare with this objective: $goal\nThis is a suggested exercise; it does not automatically validate a Selkie challenge or award points."
        "low_energy" -> "A lower-energy day needs a smaller step.\n\n1. Only do the first part of this action: $action\n2. You may stop after two minutes. If you want to continue, choose the easiest version of this alternative: $alternative\n3. Leave the larger objective for another moment: $goal"
        "busy_day" -> "Use one existing transition instead of adding a long routine.\n\n1. Attach a small pause to this familiar context: $situation\n2. During that pause, do only this action: $action\n3. If the day fills up, keep one attempt and postpone the longer alternative: $alternative"
        "without_blocking" -> "You can test a deliberate choice before changing technical restrictions.\n\n1. Notice the moment when automatic use starts: $situation\n2. Try this pause before reopening the app: $action\n3. Pick an alternative once, then decide whether this approach is sufficient: $alternative\nIt may help you choose; it does not guarantee that you will resist every urge."
        "prepare_space" -> "Make the useful choice easier to see and start.\n\n1. Inspect the friction in this context: $barrier\n2. Prepare the practical action before the next occurrence: $action\n3. Prepare what makes this alternative possible: $alternative\nKeep essential access, calls and accessibility needs available."
        "finish_session" -> "Choose an ending before another automatic opening.\n\n1. Pause and ask whether the original reason for using the phone is finished.\n2. Make the transition with this concrete action: $action\n3. Give the next moment somewhere to go: $alternative\nYour objective is a useful transition, not avoiding all necessary screen use: $goal"
        "keep_motivation" -> "Connect the effort to something you want to get back.\n\n1. Use this direction as a starting point: $goal\n2. Name a real benefit you care about, such as presence, time for an activity or finishing a task.\n3. Make it tangible with one action: $action\nAfterwards, notice whether the alternative gave you that benefit: $alternative"
        "avoid_perfection" -> "Keep the goal flexible enough to survive an imperfect day.\n\n1. Your direction is: $goal\n2. Define a minimum that still counts as an attempt: $action\n3. If an obstacle appears, change the setup rather than declare the day lost: $barrier\nA missed attempt is information; it is not a reason to give up tomorrow."
        "ask_support" -> "Ask for one clear kind of help, rather than constant checking.\n\n1. Explain the difficulty you want support with: $barrier\n2. Ask someone you trust for a practical arrangement or a shared pause, and agree on what they may remind you about.\n3. Keep the action your own choice: $action\nNobody needs access to your messages, passwords or private history to support you."
        "weekly_review" -> "Review the pattern, not just a total number.\n\n1. Which moments made this action easiest? $action\n2. Did the expected obstacle actually appear, or was it something else? $barrier\n3. Did you move toward this direction? $goal\nKeep one thing that helped and change one thing for next week."
        "changed_routine" -> "Keep the purpose while changing the timing.\n\n1. Check what no longer fits in this context: $situation\n2. Move the small action to the next realistic transition: $action\n3. Use a simpler fallback if the original plan is unavailable: $alternative\nMissing the planned time does not require abandoning the whole day."
        "need_phone" -> "Give necessary use a clear purpose and an exit.\n\n1. Before unlocking, name the exact task: a call, a route, a message or another essential action.\n2. Keep this cue to avoid extra openings: $action\n3. If the need is finished, move to the prepared alternative: $alternative\nKeep useful functions accessible; the direction is: $goal"
        "reduce_gradually" -> "Change one repeated moment first, rather than every use at once.\n\n1. Select this context for your first experiment: $situation\n2. Replace one automatic opening with this small action: $action\n3. Repeat at a similar moment for a few days, then check whether it helps this objective: $goal\nOnly add another change when the first one is manageable."
        "next_step" -> "Build on what already works instead of making every rule stricter.\n\n1. Keep the reliable starting action: $action\n2. Extend the useful alternative by a small amount that fits your day: $alternative\n3. Check that the change still serves this objective: $goal\nIf it creates too much friction, return to the smaller version and try another approach."
        else -> error("Unknown prepared-coach intention: $intent")
    } else when (intent) {
        "notice_trigger" -> "$situation\n\n1. Avant d'ouvrir ton téléphone, nomme le déclencheur : lieu, heure, émotion ou alerte.\n2. Repère l'obstacle sans te juger : $barrier\n3. À la prochaine occasion, essaie ce geste plutôt que de seulement compter les minutes : $action"
        "two_minutes" -> "Commence par un geste, sans promettre de changer toute ta journée.\n\n1. $action\n2. Après deux minutes, choisis si tu continues avec une alternative utile : $alternative\n3. Pour décider, appuie-toi sur cette direction plutôt que sur un score parfait : $goal"
        "choose_alternative" -> "$situation\n\n1. Choisis une alternative qui peut vraiment tenir dans ce moment : $alternative\n2. Prépare son premier pas maintenant : $action\n3. Essaie-la une fois avant de la juger. Si elle ne répond pas à ton besoin, change l'alternative plutôt que de te forcer."
        "handle_obstacle" -> "Prépare la difficulté avant qu'elle apparaisse.\n\n1. Identifie l'obstacle concret : $barrier\n2. Répète une petite réponse quand la pression est faible : $action\n3. Garde un plan de secours qui ne repose pas seulement sur ta volonté : $alternative"
        "realistic_goal" -> "Choisis une direction à essayer, pas une punition.\n\n1. Pars de cet objectif : $goal\n2. Transforme-le en un choix observable à un moment précis : $action\n3. Essaie aujourd'hui. Demain, ajuste la difficulté selon ce qui s'est passé au lieu de durcir automatiquement la règle."
        "delay_urge" -> "$situation\n\n1. Glisse une courte pause entre l'envie et l'ouverture de l'application : respire et attends deux minutes.\n2. Donne un rôle à ces deux minutes : $action\n3. Si l'envie reste, choisis consciemment si tu utilises le téléphone ou cette alternative : $alternative"
        "if_then" -> "Prépare ta décision avant que le moment habituel arrive.\n\n1. Nomme la situation qui déclenchera ta règle : $situation\n2. Écris : « Si cela arrive, je ferai le petit geste suivant avant d'ouvrir une autre application. »\n3. Utilise ce geste comme réponse : $action\nGarde une seule règle pour aujourd'hui afin qu'elle reste facile à retenir."
        "after_slip" -> "Un moment difficile n'efface pas ce que tu as appris.\n\n1. Nomme brièvement ce qui s'est passé, sans ajouter un jugement sur toi.\n2. Cherche la difficulté que tu peux modifier la prochaine fois : $barrier\n3. Repars avec le plus petit geste utile : $action\nTa direction peut rester la même : $goal"
        "measure_progress" -> "Choisis un indicateur simple pour ce contexte.\n\n1. Ta direction est : $goal\n2. En fin de journée, note si tu as fait ce choix au moins une fois : $action\n3. Ajoute une phrase sur ce qui t'a aidé. Les compteurs de Selkie ne s'utilisent que si le suivi est actif ; ils couvrent la période de suivi en cours, pas forcément une journée entière."
        "tomorrow_plan" -> "Prépare la décision de demain quand tu as le temps d'y réfléchir.\n\n1. Choisis un moment probable : $situation\n2. Note ton premier geste à un endroit visible : $action\n3. Prépare un secours pour un moment difficile : $alternative\nVise cette direction sans programmer chaque heure : $goal"
        "weekly_experiment" -> "Prends les sept prochains jours comme un essai, pas comme un test de ta valeur.\n\n1. Jours 1–2 : essaie le petit geste une fois par jour. $action\n2. Jours 3–5 : répète-le et note l'obstacle qui apparaît vraiment. $barrier\n3. Jours 6–7 : garde ce qui aide et compare avec cet objectif : $goal\nC'est un exercice proposé : il ne valide pas automatiquement un défi Selkie et ne donne pas de points à lui seul."
        "low_energy" -> "Une journée avec moins d'énergie demande un pas plus petit.\n\n1. Fais seulement le début de ce geste : $action\n2. Tu peux t'arrêter après deux minutes. Si tu veux continuer, choisis la version la plus facile de cette alternative : $alternative\n3. Garde l'objectif plus large pour un autre moment : $goal"
        "busy_day" -> "Utilise une transition déjà présente plutôt que d'ajouter une longue routine.\n\n1. Accroche une petite pause à ce contexte habituel : $situation\n2. Pendant cette pause, fais seulement ce geste : $action\n3. Si la journée déborde, garde un essai et reporte l'alternative plus longue : $alternative"
        "without_blocking" -> "Tu peux essayer un choix volontaire avant de changer les restrictions techniques.\n\n1. Repère le moment où l'usage automatique commence : $situation\n2. Essaie cette pause avant de rouvrir l'application : $action\n3. Choisis une alternative une fois, puis décide si cette approche suffit : $alternative\nCela peut t'aider à choisir ; ce n'est pas une garantie de résister à toutes les envies."
        "prepare_space" -> "Rends le choix utile plus visible et plus facile à commencer.\n\n1. Regarde la difficulté propre à ce contexte : $barrier\n2. Prépare le geste concret avant la prochaine occasion : $action\n3. Prépare ce qui rend cette alternative possible : $alternative\nGarde les accès essentiels, les appels et tes besoins d'accessibilité disponibles."
        "finish_session" -> "Choisis une fin avant une nouvelle ouverture automatique.\n\n1. Fais une pause et demande-toi si la raison initiale d'utiliser le téléphone est terminée.\n2. Fais la transition avec ce geste concret : $action\n3. Donne une destination au moment suivant : $alternative\nL'objectif est une transition utile, pas de supprimer tout usage nécessaire de l'écran : $goal"
        "keep_motivation" -> "Relie l'effort à quelque chose que tu veux retrouver.\n\n1. Prends cette direction comme point de départ : $goal\n2. Nomme un bénéfice réel qui compte pour toi : présence, temps pour une activité ou tâche terminée.\n3. Rends-le concret avec un geste : $action\nEnsuite, observe si l'alternative t'a apporté ce bénéfice : $alternative"
        "avoid_perfection" -> "Garde un objectif assez souple pour traverser une journée imparfaite.\n\n1. Ta direction est : $goal\n2. Définis un minimum qui reste un essai utile : $action\n3. Si un obstacle apparaît, change l'organisation plutôt que de déclarer la journée perdue : $barrier\nUn essai manqué donne une information ; il n'oblige pas à abandonner demain."
        "ask_support" -> "Demande une aide précise, plutôt qu'une vérification permanente.\n\n1. Explique la difficulté pour laquelle tu souhaites un soutien : $barrier\n2. Demande à une personne de confiance un arrangement pratique ou une pause partagée, et convenez de ce qu'elle peut te rappeler.\n3. Garde le geste comme ton propre choix : $action\nPersonne n'a besoin de tes messages, mots de passe ou de ton historique privé pour t'aider."
        "weekly_review" -> "Observe les habitudes, pas seulement un chiffre total.\n\n1. À quels moments ce geste était-il le plus facile ? $action\n2. L'obstacle prévu est-il apparu, ou était-ce autre chose ? $barrier\n3. As-tu avancé dans cette direction ? $goal\nGarde une chose qui aide et modifie une chose pour la semaine prochaine."
        "changed_routine" -> "Garde le but en changeant le moment.\n\n1. Regarde ce qui ne convient plus dans ce contexte : $situation\n2. Déplace le petit geste à la prochaine transition réaliste : $action\n3. Prends un secours plus simple si le plan initial est impossible : $alternative\nRater l'heure prévue n'oblige pas à abandonner toute la journée."
        "need_phone" -> "Donne à l'usage nécessaire un but précis et une sortie.\n\n1. Avant de déverrouiller, nomme la tâche exacte : appel, trajet, message ou autre action essentielle.\n2. Garde ce repère pour éviter les ouvertures supplémentaires : $action\n3. Si le besoin est terminé, passe à l'alternative préparée : $alternative\nGarde les fonctions utiles accessibles ; la direction est : $goal"
        "reduce_gradually" -> "Change d'abord un moment répété, plutôt que tous les usages d'un coup.\n\n1. Choisis ce contexte pour ton premier essai : $situation\n2. Remplace une ouverture automatique par ce petit geste : $action\n3. Répète à un moment similaire pendant quelques jours, puis regarde si cela aide cet objectif : $goal\nAjoute un autre changement seulement quand le premier est gérable."
        "next_step" -> "Construis sur ce qui marche déjà au lieu de rendre toutes les règles plus strictes.\n\n1. Garde le geste de départ qui fonctionne : $action\n2. Prolonge un peu l'alternative utile, selon la place disponible dans ta journée : $alternative\n3. Vérifie que le changement sert encore cet objectif : $goal\nSi cela complique trop les choses, reviens à la petite version et essaie une autre approche."
        else -> error("Unknown prepared-coach intention: $intent")
    }
}

private data class PreparedCoachCatalog(
    val questions: List<PreparedCoachQuestion>,
    val byId: Map<String, PreparedCoachQuestion>,
    val sections: List<PreparedCoachSection>,
    val searchable: List<PreparedCoachSearchEntry>
)

private data class PreparedCoachSearchEntry(
    val question: PreparedCoachQuestion,
    val normalizedQuestion: String,
    val tokens: List<String>
)

private fun buildPreparedCoachCatalog(language: AppLocale): PreparedCoachCatalog {
    val sections = Collections.unmodifiableList(preparedSectionTexts.map { (id, label) ->
        PreparedCoachSection(id, label.forLanguage(language))
    })
    val questions = Collections.unmodifiableList(coachLibraryProfiles.flatMap { profile ->
        val section = requireNotNull(preparedSectionTexts[profile.sectionId]) {
            "Unknown prepared-coach section: ${profile.sectionId}"
        }.forLanguage(language)
        preparedCoachIntents.map { intent ->
            PreparedCoachQuestion(
                id = "${profile.id}.${intent.id}",
                sectionId = profile.sectionId,
                sectionLabel = section,
                topicId = profile.parentTopicId,
                question = "${intent.question.forLanguage(language)} — ${profile.label.forLanguage(language)} ?",
                answer = preparedAnswer(profile, intent.id, language)
            )
        }
    })
    check(questions.map { it.id }.distinct().size == questions.size) { "Duplicate prepared-coach IDs" }
    return PreparedCoachCatalog(questions, questions.associateBy { it.id }, sections,
        questions.map { question ->
            val normalizedQuestion = normalizePreparedCoachText(question.question)
            PreparedCoachSearchEntry(question, normalizedQuestion,
                (normalizedQuestion + " " + normalizePreparedCoachText(question.sectionLabel))
                    .split(' ').distinct())
        })
}

// Built once per language and retained. The publicly returned lists cannot be mutated by a caller.
private val frenchPreparedCoachCatalog by lazy { buildPreparedCoachCatalog(AppLocale.FRENCH) }
private val englishPreparedCoachCatalog by lazy { buildPreparedCoachCatalog(AppLocale.ENGLISH) }
private fun preparedCoachCatalog(language: AppLocale): PreparedCoachCatalog =
    if (language == AppLocale.ENGLISH) englishPreparedCoachCatalog else frenchPreparedCoachCatalog

fun preparedCoachQuestionsFor(language: AppLocale): List<PreparedCoachQuestion> =
    preparedCoachCatalog(language).questions

fun preparedCoachSectionsFor(language: AppLocale): List<PreparedCoachSection> =
    preparedCoachCatalog(language).sections

fun preparedCoachQuestionById(id: String, language: AppLocale): PreparedCoachQuestion? =
    preparedCoachCatalog(language).byId[id]

private val preparedCoachQuestionIdsByText by lazy {
    val index = linkedMapOf<String, String>()
    for (language in AppLocale.entries) for (question in preparedCoachQuestionsFor(language)) {
        val normalized = normalizePreparedCoachText(question.question)
        val existing = index.put(normalized, question.id)
        check(existing == null || existing == question.id) { "Ambiguous prepared-coach question" }
    }
    index.toMap()
}

/** Only complete authored questions are intercepted; a topic name keeps its usual engine behavior. */
fun findPreparedCoachQuestion(message: String, language: AppLocale): PreparedCoachQuestion? {
    val id = preparedCoachQuestionIdsByText[normalizePreparedCoachText(message)] ?: return null
    return preparedCoachQuestionById(id, language)
}

private val preparedSearchStopWords = setOf("a", "ai", "au", "aux", "avec", "ce", "ces", "cet",
    "cette", "comment", "d", "de", "des", "du", "en", "et", "il", "je", "l", "la", "le",
    "les", "ma", "me", "mes", "mon", "ne", "ou", "par", "pas", "pour", "puis", "que",
    "quel", "quelle", "quelles", "quels", "qui", "s", "se", "si", "sur", "t", "te", "ton",
    "tu", "un", "une", "when", "what", "which", "who", "how", "can", "could", "i", "my",
    "me", "the", "a", "an", "to", "of", "for", "and", "or", "in", "at", "by", "do",
    "does", "is", "it", "this", "that", "with", "without", "from", "as")

/** Search authored questions and their section labels; never invents a question or an answer. */
fun searchPreparedCoachQuestions(
    query: String,
    language: AppLocale,
    sectionId: String? = null,
    limit: Int = 60
): List<PreparedCoachQuestion> {
    if (limit <= 0) return emptyList()
    val catalog = preparedCoachCatalog(language)
    val normalized = normalizePreparedCoachText(query)
    val tokens = normalized.split(' ').filter { it.isNotEmpty() && it !in preparedSearchStopWords }.distinct()
    return catalog.searchable.asSequence()
        .filter { entry -> sectionId == null || entry.question.sectionId == sectionId }
        .filter { entry -> tokens.all { sought -> entry.tokens.any { word ->
            word == sought || (sought.length >= 3 && word.startsWith(sought))
        } } }
        .map { entry -> entry.question to if (tokens.isEmpty()) 0 else
            tokens.fold(0) { total, token -> total + if (token in entry.tokens) 3 else 1 } +
                if (entry.normalizedQuestion.contains(normalized)) 5 else 0 }
        .sortedByDescending { it.second }
        .map { it.first }
        .take(limit.coerceAtMost(catalog.questions.size))
        .toList()
}

private val preparedWordAliases = mapOf("tiktoc" to "tiktok", "tikto" to "tiktok", "youtub" to "youtube",
    "insta" to "instagram", "instagrame" to "instagram", "whatsap" to "whatsapp", "snap" to "snapchat")
private val preparedCombiningMarks = Regex("\\p{M}+")
private val preparedPunctuation = Regex("[^\\p{L}\\p{N}]+")
private val preparedWhitespace = Regex("\\s+")

private fun normalizePreparedCoachText(text: String): String = Normalizer.normalize(
    text.lowercase(Locale.ROOT).replace("œ", "oe").replace("æ", "ae"), Normalizer.Form.NFD)
    .replace(preparedCombiningMarks, "")
    .replace(preparedPunctuation, " ")
    .trim().split(preparedWhitespace)
    .joinToString(" ") { preparedWordAliases[it] ?: it }
