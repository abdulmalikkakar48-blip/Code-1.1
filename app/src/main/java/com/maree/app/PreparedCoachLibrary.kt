package com.maree.app

import java.text.Normalizer
import java.util.Collections
import java.util.LinkedHashMap
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

private fun preparedAnswer(
    profile: CoachLibraryProfile, intent: String, language: AppLocale, scenario: CoachLibraryScenario? = null
): String {
    val situation = profile.situation.forLanguage(language)
    val action = profile.smallAction.forLanguage(language)
    val alternative = profile.alternative.forLanguage(language)
    val barrier = profile.barrier.forLanguage(language)
    val goal = profile.goal.forLanguage(language)
    if (scenario != null) {
        val adapted = if (language == AppLocale.ENGLISH) when (intent) {
            "notice_trigger" -> "Notice the cue for optional use when doing so fits; necessary access comes first.\n\n1. Your context is: $situation\n2. Name the cue mentally, now or after handling a real need. Notice this obstacle without blame: $barrier\n3. At a suitable transition, try this response: $action"
            "choose_alternative" -> "Choose a substitute that fits the present constraint, rather than forcing the original activity.\n\n1. Consider this available fallback: $alternative\n2. If a suitable opportunity exists, try its smallest entry point: $action\n3. If neither fits, postpone the attempt. The alternative should meet your need, not add a new obligation."
            "handle_obstacle" -> "Prepare a response mentally without requiring equipment or a change of settings.\n\n1. Identify this likely friction: $barrier\n2. Keep a possible response in mind: $action\n3. If the situation prevents it, choose a compatible fallback or postpone: $alternative"
            "realistic_goal" -> "Choose a realistic direction within the constraint, not a compulsory attempt today.\n\n1. Use this purpose as a starting point: $goal\n2. Look for a genuinely available opportunity for one observable choice: $action\n3. If no opportunity fits, keep the idea for later. Adjust from experience instead of making the requirement harsher."
            "after_slip" -> "An automatic opening does not cancel the useful choices you already made.\n\n1. Name what happened without blaming yourself; essential contact or care is not a lapse in this exercise.\n2. Notice which constraint or friction mattered: $barrier\n3. At the next suitable moment, restart with this compatible step: $action\nYour direction can remain: $goal"
            "measure_progress" -> "Measure a choice within your real constraint, without watching every minute.\n\n1. Your direction is: $goal\n2. Mentally note whether a suitable opportunity allowed this choice: $action\n3. Notice what helped, or why postponing was appropriate. Selkie counters only describe the current active tracking period; they do not necessarily represent a full day."
            "tomorrow_plan" -> "Prepare a flexible idea for tomorrow without needing a written note or extra materials.\n\n1. Keep one likely opportunity in mind: $situation\n2. Remember a first step only if it fits tomorrow's responsibilities: $action\n3. Keep this fallback, or postpone if it is unavailable: $alternative\nLet the plan serve this direction: $goal"
            "busy_day" -> "Use an existing opportunity without adding a task to a full day.\n\n1. Look for a transition that actually fits this context: $situation\n2. If a pause is available, keep only this small choice: $action\n3. If the day leaves no room, postpone rather than catch up. This fallback remains optional: $alternative"
            "without_blocking" -> "Keep the current configuration and test a voluntary choice for optional content.\n\n1. Notice when automatic entertainment starts, after necessary access is handled: $situation\n2. Only if the opportunity fits, try this compatible response: $action\n3. Choose an available fallback or postpone: $alternative\nNo new block, deletion or change of settings is required; this does not guarantee every urge will disappear."
            "prepare_space" -> "Look for what is already usable without requiring purchases, preparation or changes.\n\n1. Identify the practical friction: $barrier\n2. Consider this action only within your current resources and responsibilities: $action\n3. Locate an already available fallback, or keep it for a later opportunity: $alternative\nEssential access and accessibility remain available."
            "finish_session" -> "Choose an ending for optional content without interrupting necessary use.\n\n1. Finish the essential task first, then ask whether the optional session still serves your chosen purpose.\n2. When a transition fits, use this action: $action\n3. Choose this fallback only if it is compatible now: $alternative\nKeep the purpose of the transition: $goal"
            "keep_motivation" -> "Connect the constrained attempt to a benefit you actually want.\n\n1. Start with this purpose: $goal\n2. Name one benefit that fits your real day, such as rest, presence or returning to a useful task.\n3. At an available opportunity, make it concrete with this choice: $action\nLater, notice whether this fallback helped: $alternative"
            "avoid_perfection" -> "An attempt can be smaller or postponed without abandoning its purpose.\n\n1. Keep this flexible direction: $goal\n2. Your compatible minimum is: $action\n3. If this obstacle appears, change the timing or approach rather than the phone's configuration: $barrier\nA missed opportunity is information, not a debt to catch up on."
            "ask_support" -> "Ask for help only when the person and setting make it appropriate.\n\n1. Identify the difficulty you may want to explain: $barrier\n2. If someone trusted is available, agree on a small form of support that respects their time, your privacy and the constraint. Otherwise keep the plan your own.\n3. The action remains your choice: $action\nSupport never requires access to messages, passwords or private history."
            "weekly_review" -> "Review opportunities and constraints rather than treating a week as a pass-or-fail test.\n\n1. At which suitable moments did this choice fit? $action\n2. Did this expected friction appear, and was postponing sometimes the right decision? $barrier\n3. What served this direction? $goal\nKeep one useful approach; change the timing if your circumstances require it."
            "changed_routine" -> "Adapt the opportunity while respecting what the current situation permits.\n\n1. Identify what changed here: $situation\n2. Move this optional attempt to a genuinely suitable transition: $action\n3. Choose a compatible fallback or postpone: $alternative\nAn unavailable opportunity does not cancel the rest of the day."
            "reduce_gradually" -> "Change one optional opening within the constraint rather than trying to change everything.\n\n1. Choose a suitable occurrence of this context: $situation\n2. Try one compatible response when the opportunity exists: $action\n3. Observe whether it serves this direction: $goal\nRepeat only at suitable moments; no catch-up or extra restriction is required."
            "next_step" -> "Build on a useful choice without making the constraint harder to live with.\n\n1. Keep the part of this action that already fits: $action\n2. If there is genuinely more room, try a little more of this compatible fallback: $alternative\n3. Check that the change still serves this purpose: $goal\nOtherwise keep the smaller version; increasing effort is not compulsory."
            "two_minutes" -> "Choose a step lasting at most two minutes, or only the time actually available.\n\n1. $action\n2. Stop whenever your responsibilities or comfort require it. Keep this fallback for a suitable moment: $alternative\n3. Use this direction, not a duration you must complete: $goal"
            "low_energy" -> "The minimum can be a decision without movement, preparation or waiting.\n\n1. $action\n2. You can rest or stop immediately. This fallback is an option, not another obligation: $alternative\n3. Keep a manageable direction: $goal"
            "delay_urge" -> "Delay only an optional opening; never delay essential contact, care or accessibility access.\n\n1. If a pause fits, notice the urge without promising to wait for a fixed duration.\n2. $action\n3. Handle necessary use first, then choose whether this alternative fits: $alternative"
            "if_then" -> "Build a rule around optional use, with an exception for genuine needs.\n\n1. Your context is: $situation\n2. Keep in mind: “If I am about to open optional content and a pause fits, I will try this action.” $action\n3. Necessary contact, care and accessibility come first; a postponed attempt remains valid."
            "weekly_experiment" -> "Observe a week without making daily practice compulsory.\n\n1. Choose a genuinely available opportunity for this action: $action\n2. After an attempt, note the actual obstacle: $barrier\n3. At the end of the week, keep what fits this direction: $goal\nThis suggested exercise does not automatically validate a Selkie challenge or award points."
            "need_phone" -> "Necessary phone use comes before the optional exercise.\n\n1. Complete the essential call, message, route or accessibility task without inserting a delay.\n2. When a suitable transition exists, use this cue for optional content: $action\n3. If that fallback fits your responsibilities, choose it; otherwise postpone it: $alternative"
            else -> error("Unknown prepared-coach intention: $intent")
        } else when (intent) {
            "notice_trigger" -> "Repère le déclencheur d'un usage facultatif seulement si cela convient ; les accès nécessaires passent avant.\n\n1. Ton contexte est : $situation\n2. Nomme le déclencheur mentalement, maintenant ou après avoir traité un vrai besoin. Observe cet obstacle sans te juger : $barrier\n3. À une transition adaptée, essaie cette réponse : $action"
            "choose_alternative" -> "Choisis un remplacement compatible avec la contrainte actuelle, sans forcer l'activité prévue au départ.\n\n1. Envisage ce repli disponible : $alternative\n2. Si une occasion adaptée existe, essaie sa plus petite entrée : $action\n3. Si rien ne convient, reporte l'essai. L'alternative doit répondre à ton besoin, pas ajouter une obligation."
            "handle_obstacle" -> "Prépare une réponse mentalement, sans exiger de matériel ni de changement de réglages.\n\n1. Repère cette difficulté probable : $barrier\n2. Garde une réponse possible en tête : $action\n3. Si la situation l'empêche, choisis un repli compatible ou reporte : $alternative"
            "realistic_goal" -> "Choisis une direction réaliste dans la contrainte, pas un essai obligatoire aujourd'hui.\n\n1. Pars de cette intention : $goal\n2. Cherche une occasion réellement disponible pour un choix observable : $action\n3. Si aucune occasion ne convient, garde l'idée pour plus tard. Ajuste selon l'expérience au lieu de durcir l'exigence."
            "after_slip" -> "Une ouverture automatique n'annule pas les choix utiles déjà faits.\n\n1. Nomme ce qui s'est passé sans te juger ; un contact essentiel ou un soin n'est pas un écart par rapport à cet essai.\n2. Repère la contrainte ou la difficulté qui a compté : $barrier\n3. Au prochain moment adapté, repars avec ce geste compatible : $action\nTa direction peut rester : $goal"
            "measure_progress" -> "Mesure un choix dans ta contrainte réelle, sans surveiller chaque minute.\n\n1. Ta direction est : $goal\n2. Note mentalement si une occasion adaptée a permis ce choix : $action\n3. Repère ce qui a aidé, ou pourquoi reporter convenait. Les compteurs de Selkie décrivent seulement la période de suivi actif en cours, pas nécessairement une journée entière."
            "tomorrow_plan" -> "Prépare une idée souple pour demain sans exiger de note écrite ni de matériel supplémentaire.\n\n1. Garde une occasion probable en tête : $situation\n2. Retiens un premier geste seulement s'il convient aux obligations de demain : $action\n3. Garde ce repli, ou reporte s'il est indisponible : $alternative\nLaisse le plan servir cette direction : $goal"
            "busy_day" -> "Utilise une occasion existante sans ajouter de tâche à une journée pleine.\n\n1. Cherche une transition qui convient réellement à ce contexte : $situation\n2. Si une pause est disponible, garde seulement ce petit choix : $action\n3. Si la journée ne laisse aucune place, reporte au lieu de rattraper. Ce repli reste facultatif : $alternative"
            "without_blocking" -> "Garde la configuration actuelle et essaie un choix volontaire pour le contenu facultatif.\n\n1. Repère le début du loisir automatique après avoir traité les accès nécessaires : $situation\n2. Seulement si l'occasion convient, essaie cette réponse compatible : $action\n3. Choisis un repli disponible ou reporte : $alternative\nAucun nouveau blocage, suppression ou changement de réglages n'est requis ; cela ne garantit pas la disparition de toutes les envies."
            "prepare_space" -> "Repère ce qui est déjà utilisable sans exiger d'achat, de préparation ni de modification.\n\n1. Identifie la difficulté pratique : $barrier\n2. Envisage ce geste seulement avec tes ressources et obligations actuelles : $action\n3. Repère un repli déjà disponible, ou garde-le pour une occasion ultérieure : $alternative\nLes accès essentiels et l'accessibilité restent disponibles."
            "finish_session" -> "Choisis une fin pour le contenu facultatif sans interrompre l'usage nécessaire.\n\n1. Termine d'abord la tâche essentielle, puis demande-toi si la session facultative sert encore ton intention.\n2. Lorsqu'une transition convient, utilise ce geste : $action\n3. Choisis ce repli seulement s'il est compatible maintenant : $alternative\nGarde l'intention de la transition : $goal"
            "keep_motivation" -> "Relie l'essai adapté à un bénéfice que tu veux réellement retrouver.\n\n1. Pars de cette intention : $goal\n2. Nomme un bénéfice compatible avec ta vraie journée : repos, présence ou reprise d'une tâche utile.\n3. À une occasion disponible, rends-le concret avec ce choix : $action\nObserve ensuite si ce repli a aidé : $alternative"
            "avoid_perfection" -> "Un essai peut être plus petit ou reporté sans abandonner son intention.\n\n1. Garde cette direction souple : $goal\n2. Ton minimum compatible est : $action\n3. Si cet obstacle apparaît, change le moment ou l'approche plutôt que la configuration du téléphone : $barrier\nUne occasion manquée donne une information, pas une dette à rattraper."
            "ask_support" -> "Demande de l'aide seulement quand la personne et le lieu s'y prêtent.\n\n1. Repère la difficulté que tu pourrais expliquer : $barrier\n2. Si une personne de confiance est disponible, convenez d'une petite aide qui respecte son temps, ta vie privée et la contrainte. Sinon, garde le plan pour toi.\n3. Le geste reste ton choix : $action\nLe soutien n'exige jamais l'accès aux messages, mots de passe ou historiques privés."
            "weekly_review" -> "Relis les occasions et les contraintes sans traiter la semaine comme un test réussi ou raté.\n\n1. À quels moments adaptés ce choix convenait-il ? $action\n2. Cette difficulté prévue est-elle apparue, et reporter était-il parfois la bonne décision ? $barrier\n3. Qu'est-ce qui a servi cette direction ? $goal\nGarde une approche utile ; change le moment si ta situation le demande."
            "changed_routine" -> "Adapte l'occasion en respectant ce que la situation actuelle permet.\n\n1. Repère ce qui a changé ici : $situation\n2. Déplace cet essai facultatif à une transition réellement adaptée : $action\n3. Choisis un repli compatible ou reporte : $alternative\nUne occasion indisponible n'annule pas le reste de la journée."
            "reduce_gradually" -> "Change une ouverture facultative dans la contrainte plutôt que tous les usages à la fois.\n\n1. Choisis une occasion adaptée de ce contexte : $situation\n2. Essaie une réponse compatible lorsque l'occasion existe : $action\n3. Observe si elle sert cette direction : $goal\nRépète seulement aux moments adaptés ; aucun rattrapage ni restriction supplémentaire n'est requis."
            "next_step" -> "Construis sur un choix utile sans rendre la contrainte plus difficile à vivre.\n\n1. Garde la partie de ce geste qui convient déjà : $action\n2. Si davantage de place existe réellement, essaie un peu plus de ce repli compatible : $alternative\n3. Vérifie que le changement sert encore cette intention : $goal\nSinon, garde la petite version ; augmenter l'effort n'est pas obligatoire."
            "two_minutes" -> "Choisis un geste de deux minutes au maximum, ou seulement du temps réellement disponible.\n\n1. $action\n2. Arrête dès que tes obligations ou ton confort le demandent. Garde ce repli pour un moment adapté : $alternative\n3. Vise cette direction, pas une durée à terminer : $goal"
            "low_energy" -> "Le minimum peut être une décision sans déplacement, préparation ni attente.\n\n1. $action\n2. Tu peux te reposer ou arrêter immédiatement. Ce repli est une option, pas une obligation supplémentaire : $alternative\n3. Garde une direction accessible : $goal"
            "delay_urge" -> "Reporte seulement une ouverture facultative ; ne retarde jamais un contact essentiel, un soin ou un accès d'accessibilité.\n\n1. Si une pause convient, remarque l'envie sans promettre d'attendre une durée fixe.\n2. $action\n3. Traite d'abord l'usage nécessaire, puis choisis si cette alternative convient : $alternative"
            "if_then" -> "Prépare une règle pour l'usage facultatif, avec une exception pour les vrais besoins.\n\n1. Ton contexte est : $situation\n2. Retiens mentalement : « Si je vais ouvrir un contenu facultatif et qu'une pause convient, j'essaierai ce geste. » $action\n3. Les contacts nécessaires, les soins et l'accessibilité passent avant ; un essai reporté reste valable."
            "weekly_experiment" -> "Observe une semaine sans rendre la pratique quotidienne obligatoire.\n\n1. Choisis une occasion réellement disponible pour ce geste : $action\n2. Après un essai, note la difficulté rencontrée : $barrier\n3. En fin de semaine, garde ce qui convient à cette direction : $goal\nCet exercice proposé ne valide pas automatiquement un défi Selkie et ne donne pas de points à lui seul."
            "need_phone" -> "L'usage nécessaire du téléphone passe avant l'exercice facultatif.\n\n1. Termine l'appel, le message, le trajet ou l'usage d'accessibilité essentiel sans ajouter de délai.\n2. Lorsqu'une transition adaptée existe, utilise ce repère pour le contenu facultatif : $action\n3. Si ce repli convient à tes obligations, choisis-le ; sinon, reporte-le : $alternative"
            else -> error("Unknown prepared-coach intention: $intent")
        }
        return adapted
    }
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

private data class PreparedCoachSearchEntry(
    val position: Int,
    val normalizedQuestion: String,
    val tokens: IntArray
)

/** Shared, bounded caches retain only recently requested answers and adapted profiles. */
private class PreparedCoachCache<K : Any, V : Any>(private val capacity: Int) {
    private val values = LinkedHashMap<K, V>(16, 0.75f, true)
    private var generatedValues = 0L

    @Synchronized
    fun getOrCreate(key: K, create: () -> V): V {
        values[key]?.let { return it }
        val value = create()
        if (values.size >= capacity) {
            val oldest = values.entries.iterator()
            if (oldest.hasNext()) { oldest.next(); oldest.remove() }
        }
        values[key] = value
        generatedValues++
        return value
    }

    @Synchronized
    fun statistics(): PreparedCoachAnswerCacheStats = PreparedCoachAnswerCacheStats(generatedValues, values.size)
}

/** Counts permit checking pagination without timing-sensitive performance assertions. */
internal data class PreparedCoachAnswerCacheStats(val generatedAnswers: Long, val cachedAnswers: Int)

private fun scenarioForProfile(position: Int): CoachLibraryScenario? {
    val group = position / coachLibraryProfiles.size
    return if (group == 0) null else coachLibraryScenarios[group - 1]
}

private fun baseProfileAt(position: Int): CoachLibraryProfile =
    coachLibraryProfiles[position % coachLibraryProfiles.size]

private val adaptedProfiles = PreparedCoachCache<Int, CoachLibraryProfile>(64)
private fun profileAt(position: Int): CoachLibraryProfile {
    val scenario = scenarioForProfile(position) ?: return baseProfileAt(position)
    return adaptedProfiles.getOrCreate(position) { adaptedCoachLibraryProfile(baseProfileAt(position), scenario) }
}

private val profilesByPlan by lazy {
    PlanType.entries.associateWith { plan ->
        val groups = listOf(0) + coachLibraryScenarios.mapIndexedNotNull { index, scenario ->
            if (scenario.minimumPlan.ordinal <= plan.ordinal) index + 1 else null
        }
        groups.flatMap { group -> coachLibraryProfiles.indices.map { group * coachLibraryProfiles.size + it } }
    }
}

private val normalizedIntentPrefixes by lazy {
    AppLocale.entries.associateWith { language -> preparedCoachIntents.map {
        normalizePreparedCoachText(it.question.forLanguage(language)) + " "
    } }
}
private val normalizedBaseLabels by lazy {
    AppLocale.entries.associateWith { language -> coachLibraryProfiles.map {
        normalizePreparedCoachText(it.label.forLanguage(language))
    } }
}

/** Derived from authored content, never from a commercial quota or spelling aliases. */
fun preparedCoachQuestionCount(plan: PlanType = PlanType.FREE): Int =
    profilesByPlan.getValue(plan).size * preparedCoachIntents.size

private fun preparedQuestionText(profilePosition: Int, intentPosition: Int, language: AppLocale): String {
    val base = baseProfileAt(profilePosition)
    val intent = preparedCoachIntents[intentPosition].question.forLanguage(language)
    val scenario = scenarioForProfile(profilePosition)
    return if (scenario == null) "$intent — ${base.label.forLanguage(language)} ?"
    else "$intent — ${base.label.forLanguage(language)} : ${scenario.questionContext.forLanguage(language)} ?"
}

private class PreparedCoachCatalog(private val language: AppLocale) {
    val sections: List<PreparedCoachSection> = Collections.unmodifiableList(preparedSectionTexts.map { (id, text) ->
        PreparedCoachSection(id, text.forLanguage(language))
    })
    private val answers = PreparedCoachCache<Int, PreparedCoachQuestion>(256)
    // Compact token IDs share vocabulary across questions; a global search never rebuilds an evicted section.
    private val searchProfiles = mutableMapOf<Int, List<PreparedCoachSearchEntry>>()
    private val tokenIds = linkedMapOf<String, Int>()
    private val views: Map<PlanType, List<PreparedCoachQuestion>> = PlanType.entries.associateWith { plan ->
        val profilePositions = profilesByPlan.getValue(plan)
        Collections.unmodifiableList(object : AbstractList<PreparedCoachQuestion>() {
            override val size: Int get() = profilePositions.size * preparedCoachIntents.size
            override fun get(index: Int): PreparedCoachQuestion {
                if (index !in 0 until size) throw IndexOutOfBoundsException("Question index: $index")
                return question(profilePositions[index / preparedCoachIntents.size] * preparedCoachIntents.size +
                    index % preparedCoachIntents.size)
            }
        })
    }

    fun questions(plan: PlanType): List<PreparedCoachQuestion> = views.getValue(plan)

    fun answerCacheStats(): PreparedCoachAnswerCacheStats = answers.statistics()

    fun indexedQuestions(size: Int, positionAt: (Int) -> Int): List<PreparedCoachQuestion> =
        Collections.unmodifiableList(object : AbstractList<PreparedCoachQuestion>() {
            override val size: Int = size
            override fun get(index: Int): PreparedCoachQuestion {
                if (index !in 0 until size) throw IndexOutOfBoundsException("Question index: $index")
                return question(positionAt(index))
            }
        })

    fun question(position: Int): PreparedCoachQuestion = answers.getOrCreate(position) {
        val profilePosition = position / preparedCoachIntents.size
        val intentPosition = position % preparedCoachIntents.size
        val profile = profileAt(profilePosition)
        val intent = preparedCoachIntents[intentPosition]
        PreparedCoachQuestion("${profile.id}.${intent.id}", profile.sectionId,
            preparedSectionTexts.getValue(profile.sectionId).forLanguage(language), profile.parentTopicId,
            preparedQuestionText(profilePosition, intentPosition, language),
            preparedAnswer(profile, intent.id, language, scenarioForProfile(profilePosition)))
    }

    @Synchronized
    fun searchable(profilePosition: Int): List<PreparedCoachSearchEntry> = searchProfiles.getOrPut(profilePosition) {
        val section = normalizePreparedCoachText(preparedSectionTexts.getValue(baseProfileAt(profilePosition).sectionId)
            .forLanguage(language))
        preparedCoachIntents.indices.map { intentPosition ->
            val normalized = normalizePreparedCoachText(preparedQuestionText(profilePosition, intentPosition, language))
            PreparedCoachSearchEntry(profilePosition * preparedCoachIntents.size + intentPosition, normalized,
                (normalized + " " + section).split(' ').distinct()
                    .map { token -> tokenIds.getOrPut(token) { tokenIds.size } }.toIntArray())
        }
    }

    @Synchronized
    fun matchingTokens(token: String): IntArray = tokenIds.entries.asSequence()
        .filter { it.key == token || (token.length >= 3 && it.key.startsWith(token)) }
        .map { it.value }.toList().toIntArray()

    @Synchronized
    fun exactToken(token: String): Int? = tokenIds[token]
}

// Two language catalogues share all plan views; answers are generated only when requested.
private val frenchPreparedCoachCatalog by lazy { PreparedCoachCatalog(AppLocale.FRENCH) }
private val englishPreparedCoachCatalog by lazy { PreparedCoachCatalog(AppLocale.ENGLISH) }
private fun preparedCoachCatalog(language: AppLocale): PreparedCoachCatalog =
    if (language == AppLocale.ENGLISH) englishPreparedCoachCatalog else frenchPreparedCoachCatalog

fun preparedCoachQuestionsFor(language: AppLocale, plan: PlanType = PlanType.FREE): List<PreparedCoachQuestion> =
    preparedCoachCatalog(language).questions(plan)

internal fun preparedCoachAnswerCacheStats(language: AppLocale): PreparedCoachAnswerCacheStats =
    preparedCoachCatalog(language).answerCacheStats()

fun preparedCoachSectionsFor(language: AppLocale, plan: PlanType = PlanType.FREE): List<PreparedCoachSection> =
    preparedCoachCatalog(language).sections

fun preparedCoachQuestionById(
    id: String, language: AppLocale, plan: PlanType = PlanType.FREE
): PreparedCoachQuestion? {
    val parts = id.split('.', limit = 2)
    if (parts.size != 2) return null
    val intent = preparedCoachIntents.indexOfFirst { it.id == parts[1] }
    if (intent < 0) return null
    val profileParts = parts[0].split('~', limit = 2)
    val base = coachLibraryProfiles.indexOfFirst { it.id == profileParts[0] }
    if (base < 0) return null
    val group = if (profileParts.size == 1) 0 else {
        val scenario = coachLibraryScenarios.indexOfFirst { it.id == profileParts[1] }
        if (scenario < 0 || coachLibraryScenarios[scenario].minimumPlan.ordinal > plan.ordinal) return null
        scenario + 1
    }
    return preparedCoachCatalog(language).question((group * coachLibraryProfiles.size + base) *
        preparedCoachIntents.size + intent)
}

/** Only complete authored questions are intercepted; a topic name keeps its usual engine behavior. */
fun findPreparedCoachQuestion(
    message: String, language: AppLocale, plan: PlanType = PlanType.FREE
): PreparedCoachQuestion? {
    val normalized = normalizePreparedCoachText(message)
    if (normalized.isEmpty()) return null
    for (sourceLanguage in AppLocale.entries) {
        val intentPositions = preparedCoachIntents.indices.filter { index ->
            normalized.startsWith(normalizedIntentPrefixes.getValue(sourceLanguage)[index])
        }
        if (intentPositions.isEmpty()) continue
        for (profilePosition in profilesByPlan.getValue(plan)) {
            if (!normalized.contains(normalizedBaseLabels.getValue(sourceLanguage)[profilePosition % coachLibraryProfiles.size]))
                continue
            for (intentPosition in intentPositions) {
                if (normalized == normalizePreparedCoachText(preparedQuestionText(profilePosition, intentPosition, sourceLanguage)))
                    return preparedCoachCatalog(language).question(profilePosition * preparedCoachIntents.size + intentPosition)
            }
        }
    }
    return null
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
    limit: Int = 60,
    plan: PlanType = PlanType.FREE
): List<PreparedCoachQuestion> {
    if (limit <= 0) return emptyList()
    val catalog = preparedCoachCatalog(language)
    val normalized = normalizePreparedCoachText(query)
    val tokens = normalized.split(' ').filter { it.isNotEmpty() && it !in preparedSearchStopWords }.distinct()
    val profiles = profilesByPlan.getValue(plan)
        .filter { sectionId == null || baseProfileAt(it).sectionId == sectionId }
    if (tokens.isEmpty()) return catalog.indexedQuestions(
        minOf(limit, profiles.size * preparedCoachIntents.size)
    ) { index -> profiles[index / preparedCoachIntents.size] * preparedCoachIntents.size +
        index % preparedCoachIntents.size }
    val searchable = profiles.map(catalog::searchable)
    val matchingTokens = tokens.map(catalog::matchingTokens)
    if (matchingTokens.any { it.isEmpty() }) return emptyList()
    val exactTokens = tokens.map(catalog::exactToken)
    val positions = searchable.asSequence().flatMap { it.asSequence() }
        .filter { entry -> matchingTokens.all { matches -> matches.any { it in entry.tokens } } }
        .map { entry ->
            val score = exactTokens.fold(0) { total, token ->
                total + if (token != null && token in entry.tokens) 3 else 1
            }
            entry.position to (score + if (entry.normalizedQuestion.contains(normalized)) 5 else 0)
        }
        .sortedByDescending { it.second }
        .take(limit.coerceAtMost(preparedCoachQuestionCount(plan)))
        .map { it.first }.toList().toIntArray()
    return catalog.indexedQuestions(positions.size) { positions[it] }
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
