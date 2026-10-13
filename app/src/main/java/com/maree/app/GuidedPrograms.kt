package com.maree.app

import android.content.Context
import android.content.SharedPreferences
import java.time.LocalDate
import org.json.JSONArray
import org.json.JSONObject

/** Authored offline content; the user's progress never contains these texts. */
data class GuidedText(val french: String, val english: String) {
    fun text(locale: AppLocale): String = if (locale == AppLocale.ENGLISH) english else french
    val localized: String get() = text(AppLanguage.current)
}

data class GuidedProgramDay(val title: GuidedText, val activity: GuidedText)
data class GuidedProgram(
    val id: String,
    val title: GuidedText,
    val description: GuidedText,
    val days: List<GuidedProgramDay>
) {
    val requiredDays: Int get() = days.size
}

private fun guidedDay(frTitle: String, enTitle: String, fr: String, en: String) =
    GuidedProgramDay(GuidedText(frTitle, enTitle), GuidedText(fr, en))

/** Each day is a different practical activity, in its intended order. */
val GUIDED_PROGRAMS: List<GuidedProgram> = listOf(
    GuidedProgram("sleep7days", GuidedText("Sommeil · 7 journées", "Sleep · 7 days"),
        GuidedText("Prépare une fin de journée avec moins d'écrans, à ton rythme.",
            "Build a gentler end to the day with fewer screens, at your own pace."), listOf(
        guidedDay("Observer sa soirée", "Notice your evening",
            "Ce soir, repère le moment où tu consultes ton téléphone avant de te coucher. Note sur papier ce que tu étais venu y faire, sans te juger.",
            "Tonight, notice when you check your phone before bed. On paper, write what you opened it for, without judging yourself."),
        guidedDay("Choisir un endroit pour le téléphone", "Choose a place for your phone",
            "Choisis un endroit accessible mais hors du lit pour poser ton téléphone ce soir. Prévois comment garder les appels utiles accessibles si tu en as besoin.",
            "Choose an accessible spot outside your bed for your phone tonight. Plan how to keep essential calls available if you need them."),
        guidedDay("Alléger les sollicitations du soir", "Reduce evening interruptions",
            "Passe en revue les notifications d'une application et coupe une alerte non essentielle pour la soirée. Garde les contacts et alertes dont tu as besoin.",
            "Review one app's notifications and turn off one non-essential evening alert. Keep the contacts and alerts you need."),
        guidedDay("Préparer une pause de dix minutes", "Prepare a ten-minute pause",
            "Prépare un livre, un carnet ou une autre activité calme. Essaie dix minutes de cette activité sans écran avant de te coucher, selon tes contraintes.",
            "Set out a book, a notebook or another quiet activity. Try ten minutes of it without a screen before bed, around your other commitments."),
        guidedDay("Poser les idées sur papier", "Put your thoughts on paper",
            "Écris sur papier une tâche à reprendre demain et le premier petit geste pour la commencer. Range le papier, puis ferme les applications liées à cette tâche.",
            "On paper, write one task to return to tomorrow and a small first step. Put the paper away, then close the apps linked to that task."),
        guidedDay("Essayer sa routine courte", "Try your short routine",
            "Enchaîne ce soir deux gestes déjà essayés : poser le téléphone à son endroit et choisir dix minutes hors écran. Adapte la durée si ta soirée est chargée.",
            "Tonight, combine two steps you have tried: place your phone in its spot and choose ten screen-free minutes. Adapt the length if your evening is busy."),
        guidedDay("Garder ce qui te convient", "Keep what suits you",
            "Relis tes essais et choisis un geste que tu veux garder pour les prochaines soirées. Fais-le aujourd'hui et note une version plus courte pour les jours difficiles.",
            "Review what you tried and choose one step to keep for future evenings. Do it today and write a shorter version for difficult days.")
    )),
    GuidedProgram("studies14days", GuidedText("Études · 14 journées", "Studies · 14 days"),
        GuidedText("Organise de petites séances de travail et des pauses choisies.",
            "Organize small study sessions and intentional breaks."), listOf(
        guidedDay("Définir un petit objectif", "Set a small goal",
            "Choisis un cours ou un sujet et écris une tâche précise réalisable en dix minutes, par exemple relire une page. Essaie cette tâche aujourd'hui.",
            "Choose a course or topic and write one precise ten-minute task, such as rereading one page. Try that task today."),
        guidedDay("Préparer son espace", "Prepare your space",
            "Dégage une petite surface et prépare seulement le matériel utile à ta tâche. Travaille cinq à dix minutes dans cet espace.",
            "Clear a small surface and set out only the materials you need. Work there for five to ten minutes."),
        guidedDay("Choisir la place du téléphone", "Choose where your phone goes",
            "Pour une séance de dix minutes, pose ton téléphone hors de portée. Si tu étudies avec lui, garde uniquement la ressource utile ouverte.",
            "For a ten-minute session, put your phone out of reach. If you study on it, keep only the resource you need open."),
        guidedDay("Planifier une pause", "Plan a break",
            "Avant de travailler dix minutes, choisis une pause de cinq minutes sans fil d'actualité : boire de l'eau, bouger ou regarder dehors. Fais la séance et la pause.",
            "Before ten minutes of study, choose a five-minute break without a feed: drink water, move or look outside. Do the session and the break."),
        guidedDay("Découper une tâche", "Break down a task",
            "Divise un exercice ou un chapitre en trois petites étapes sur papier. Réalise la première, puis indique ce qui reste pour la prochaine séance.",
            "Split an exercise or chapter into three small steps on paper. Do the first, then mark what remains for your next session."),
        guidedDay("Fermer une distraction", "Close one distraction",
            "Ferme un onglet ou une application sans rapport avec ton travail. Fais une séance de dix à quinze minutes et garde une feuille pour noter ce que tu veux vérifier plus tard.",
            "Close one tab or app unrelated to your work. Study for ten to fifteen minutes and keep paper nearby to note things to check later."),
        guidedDay("Faire un premier bilan", "Make a first review",
            "Relis ta liste d'étapes. Choisis ce qui était faisable cette semaine, puis prépare et réalise une courte séance avec cette organisation.",
            "Review your steps. Choose what felt manageable this week, then prepare and complete a short session using that approach."),
        guidedDay("Reformuler une idée", "Explain an idea in your own words",
            "Lis un court passage, ferme la ressource et écris deux phrases pour l'expliquer avec tes mots. Rouvre ensuite le passage pour vérifier et corriger.",
            "Read a short passage, close the resource and write two sentences explaining it in your own words. Reopen it to check and correct them."),
        guidedDay("Créer une fiche papier", "Make a paper card",
            "Choisis une notion et crée une petite fiche papier avec une question d'un côté et ta réponse de l'autre. Essaie de répondre avant de retourner la fiche.",
            "Choose a concept and make a small paper card with a question on one side and your answer on the other. Try answering before turning it over."),
        guidedDay("Faire deux séances courtes", "Try two short sessions",
            "Prévois deux séances de dix minutes séparées par une pause. Donne un objectif simple à chacune et réalise-les si ton emploi du temps le permet ; raccourcis-les au besoin.",
            "Plan two ten-minute sessions with a break between them. Give each a simple goal and do them around your schedule; shorten them if needed."),
        guidedDay("Préparer une demande d'aide", "Prepare a request for help",
            "Repère un point difficile et écris une question précise avec ce que tu as déjà essayé. Consulte une ressource adaptée ou prépare cette question pour une personne qui peut t'aider.",
            "Identify a difficult point and write a precise question with what you have already tried. Consult a suitable resource or prepare the question for someone who can help."),
        guidedDay("Adapter une journée chargée", "Adapt a busy day",
            "Choisis une version de cinq minutes d'une tâche d'étude et fais-la aujourd'hui. Note comment reprendre la suite sans chercher à rattraper toutes les séances manquées.",
            "Choose a five-minute version of a study task and do it today. Note how to continue without trying to make up every missed session."),
        guidedDay("Revoir sans tout relire", "Review without rereading everything",
            "Choisis trois idées de ton sujet et essaie de les expliquer sur papier avant d'ouvrir tes notes. Vérifie ensuite uniquement les points incertains.",
            "Choose three ideas from your topic and try explaining them on paper before opening your notes. Then check only the uncertain points."),
        guidedDay("Écrire sa prochaine séance", "Write your next session plan",
            "Écris une petite routine : objectif, place du téléphone et pause. Fais une dernière séance avec elle et fixe un prochain créneau réaliste, que tu pourras ajuster.",
            "Write a small routine: goal, phone location and break. Use it for one final session and choose a realistic next time that you can adjust.")
    )),
    GuidedProgram("socialmedia30days", GuidedText("Réseaux sociaux · 30 journées", "Social media · 30 days"),
        GuidedText("Explore des usages choisis et des alternatives au défilement.",
            "Explore intentional use and alternatives to scrolling."), listOf(
        guidedDay("Repérer une ouverture automatique", "Notice an automatic opening",
            "Repère une fois où tu ouvres un réseau sans objectif précis. Note le contexte sur papier : attente, ennui ou habitude. Ferme l'application quand tu l'as remarqué.",
            "Notice one time you open a social app without a clear purpose. Write the context on paper: waiting, boredom or habit. Close the app once you notice."),
        guidedDay("Choisir avant d'ouvrir", "Choose before opening",
            "Avant une ouverture aujourd'hui, décide d'une action précise, comme répondre à un message. Réalise-la puis ferme le réseau.",
            "Before opening a social app today, choose one specific action, such as replying to a message. Do it, then close the app."),
        guidedDay("Couper une alerte facultative", "Turn off an optional alert",
            "Désactive une notification de réseau qui n'est pas nécessaire, par exemple une suggestion de publication. Garde les messages dont tu as besoin.",
            "Disable one social notification you do not need, such as a suggested post. Keep the messages you rely on."),
        guidedDay("Éloigner un raccourci", "Move a shortcut",
            "Retire le raccourci d'un réseau de ton écran d'accueil ou place-le dans un dossier. Essaie cette disposition pendant le reste de la journée.",
            "Remove one social app shortcut from your home screen or put it in a folder. Try that layout for the rest of today."),
        guidedDay("Préparer une alternative courte", "Prepare a short alternative",
            "Place un livre, un carnet ou un petit jeu hors écran près de toi. Lors d'une envie de défiler, essaie cette alternative pendant deux minutes.",
            "Keep a book, notebook or small offline game nearby. When you feel like scrolling, try that alternative for two minutes."),
        guidedDay("Garder un repas sans fil", "Keep a meal free of feeds",
            "Choisis un repas ou une collation et garde les réseaux fermés pendant ce moment. Pose le téléphone où il reste disponible pour un besoin essentiel.",
            "Choose a meal or snack and keep social feeds closed during it. Put your phone somewhere accessible for essential needs."),
        guidedDay("Relire ses premiers essais", "Review your first attempts",
            "Relis tes observations et choisis le geste le plus facile de ces six journées. Recommence ce geste aujourd'hui et note une difficulté concrète à prendre en compte.",
            "Review your observations and choose the easiest step from these six days. Repeat it today and note one practical difficulty to account for."),
        guidedDay("Choisir un moment de consultation", "Choose a time to check",
            "Choisis aujourd'hui un moment réaliste pour consulter un réseau. Avant ce moment, garde le fil fermé pendant une courte plage de dix minutes choisie à l'avance.",
            "Choose a realistic time to check a social app today. Before then, keep its feed closed for a short ten-minute period you choose in advance."),
        guidedDay("Donner une fin à sa visite", "Give your visit an ending",
            "Avant une visite, décide de ton point d'arrêt : répondre à deux messages ou regarder trois publications. Ferme ensuite le réseau, même si le fil continue.",
            "Before a visit, choose an endpoint: reply to two messages or view three posts. Then close the app even though the feed keeps going."),
        guidedDay("Trier une source", "Review one source",
            "Passe en revue un compte ou un groupe suivi. S'il ne t'intéresse plus, masque-le ou désabonne-toi ; sinon, garde-le en ayant formulé ce qu'il t'apporte.",
            "Review one account or group you follow. If it no longer interests you, mute or unfollow it; otherwise keep it after naming why it matters to you."),
        guidedDay("Attendre sans défiler", "Wait without scrolling",
            "Pendant une attente courte et sûre, laisse un réseau fermé deux minutes. Observe autour de toi ou prépare mentalement la prochaine chose à faire.",
            "During a short, safe wait, keep a social app closed for two minutes. Notice your surroundings or think about what you will do next."),
        guidedDay("Envoyer un message direct", "Send a direct message",
            "Si tu veux prendre des nouvelles, écris un message choisi à une personne plutôt que parcourir son fil. Referme le réseau après cet échange ou après l'envoi.",
            "If you want to catch up, send an intentional message to one person instead of browsing their feed. Close the app after the exchange or after sending."),
        guidedDay("Faire une activité de dix minutes", "Do a ten-minute activity",
            "Choisis une activité hors réseau de dix minutes : cuisiner, dessiner ou ranger un coin. Fais-la à un moment où tu aurais habituellement ouvert un fil.",
            "Choose a ten-minute activity outside social apps: cook, draw or tidy a corner. Do it at a time when you would usually open a feed."),
        guidedDay("Adapter son premier créneau", "Adjust your first check-in time",
            "Évalue le moment de consultation choisi au jour 8. Décale-le ou raccourcis-le selon ta journée, puis essaie ce nouveau cadre aujourd'hui.",
            "Review the check-in time from day 8. Move or shorten it to suit today, then try the adjusted plan."),
        guidedDay("Réserver un début de tâche", "Protect the start of a task",
            "Au début d'une tâche ordinaire, garde les réseaux fermés cinq minutes. Termine ce petit début avant de décider si tu souhaites les consulter.",
            "When starting an ordinary task, keep social apps closed for five minutes. Finish that small beginning before deciding whether to check them."),
        guidedDay("Repérer le défilement du soir", "Notice evening scrolling",
            "Choisis une courte plage de dix minutes en soirée et garde les fils fermés. Prépare à l'avance ce que tu feras à la place, sans te fixer une heure de coucher obligatoire.",
            "Choose a short ten-minute evening period and keep feeds closed. Decide in advance what to do instead, without setting yourself a mandatory bedtime."),
        guidedDay("Séparer messages et fil", "Separate messages from the feed",
            "Ouvre un réseau pour vérifier les messages utiles en allant directement à la messagerie. Après cette action, ferme-le avant de passer au fil.",
            "Open a social app to check useful messages by going straight to the inbox. After that action, close it before moving to the feed."),
        guidedDay("Créer un rappel visible", "Make a visible reminder",
            "Écris un petit rappel sur papier, comme « Qu'est-ce que je viens faire ? ». Pose-le près du téléphone et utilise-le avant une ouverture de réseau aujourd'hui.",
            "Write a small paper reminder, such as 'What am I here to do?' Place it near your phone and use it before opening a social app today."),
        guidedDay("Retrouver une activité appréciée", "Return to an activity you enjoy",
            "Choisis une activité que tu aimes hors écran et prépare son matériel. Consacre-lui quinze minutes ou une version plus courte si nécessaire.",
            "Choose an offline activity you enjoy and prepare its materials. Spend fifteen minutes on it, or a shorter amount if needed."),
        guidedDay("Prévoir une exception utile", "Plan a useful exception",
            "Identifie un besoin réel qui passe par un réseau, comme une information de groupe. Écris l'action utile, fais-la puis ferme le fil ; adapte ton cadre à ce besoin.",
            "Identify a real need that uses a social app, such as group information. Write the useful action, do it, then close the feed; adapt your plan to that need."),
        guidedDay("Faire un bilan intermédiaire", "Make a midway review",
            "Note deux gestes réalisables et un geste trop contraignant. Essaie l'un des gestes réalisables aujourd'hui et simplifie celui qui était difficile.",
            "Write two manageable steps and one that felt too demanding. Try one manageable step today and simplify the difficult one."),
        guidedDay("Choisir une limite courte", "Choose a short boundary",
            "Pour une visite de réseau, choisis une durée courte adaptée à ton besoin et utilise un minuteur si tu le souhaites. À la fin, ferme le fil consciemment.",
            "For one social visit, choose a short duration suited to your need and use a timer if you want. At the end, deliberately close the feed."),
        guidedDay("Garder un trajet sans fil", "Keep a journey free of feeds",
            "Sur un trajet où cela est sûr, garde les réseaux fermés pendant cinq minutes. Tu peux garder la navigation, les billets ou les appels nécessaires accessibles.",
            "During a journey where it is safe, keep social feeds closed for five minutes. Keep navigation, tickets or necessary calls available."),
        guidedDay("Choisir ce que l'on partage", "Choose what to share",
            "Si tu envisages une publication, prends une minute pour vérifier si tu souhaites vraiment la partager et avec qui. Publie, garde-la pour toi ou renonce, puis ferme le réseau.",
            "If you are considering a post, take a minute to check whether you want to share it and with whom. Post it, keep it private or skip it, then close the app."),
        guidedDay("Faire place à une conversation", "Make room for a conversation",
            "Lors d'un échange en personne ou d'un appel choisi, garde les fils fermés pendant quelques minutes. Si personne n'est disponible, prépare un message pour un échange futur.",
            "During an in-person conversation or a chosen call, keep feeds closed for a few minutes. If no one is available, prepare a message for a future conversation."),
        guidedDay("Préparer un moment à risque", "Prepare for a familiar scrolling moment",
            "Choisis un contexte où tu défiles souvent et prépare un autre premier geste : ouvrir ton carnet ou te lever un instant. Essaie ce geste quand ce contexte se présente aujourd'hui.",
            "Choose a setting where you often scroll and prepare another first step: open your notebook or stand up briefly. Try it when that setting appears today."),
        guidedDay("Essayer une plage plus longue", "Try a longer feed-free period",
            "Choisis jusqu'à trente minutes sans fil à un moment compatible avec tes obligations. Prévois les contacts utiles et une activité ; raccourcis la plage si nécessaire.",
            "Choose up to thirty minutes without feeds at a time that fits your responsibilities. Plan for useful contacts and an activity; shorten the period if needed."),
        guidedDay("Préparer les journées irrégulières", "Prepare for irregular days",
            "Écris une règle légère pour une journée chargée, par exemple fermer le fil après une action utile. Essaie-la aujourd'hui plutôt que reprendre tout le programme depuis le début.",
            "Write a light rule for a busy day, such as closing the feed after one useful action. Try it today instead of restarting the whole program."),
        guidedDay("Choisir ses repères durables", "Choose your continuing reminders",
            "Sélectionne deux essais que tu veux garder, comme une notification retirée et une alternative prête. Mets-les en place aujourd'hui et choisis quand les réévaluer.",
            "Choose two experiments to keep, such as a removed alert and a ready alternative. Put them in place today and choose when to review them."),
        guidedDay("Écrire son propre cadre", "Write your own plan",
            "Sur papier, décris à quoi les réseaux te servent, un moment choisi pour les consulter et une alternative. Essaie ce cadre aujourd'hui ; tu pourras le modifier selon tes besoins.",
            "On paper, describe what social apps are useful for, one chosen time to check them and an alternative. Try that plan today; you can adjust it as your needs change.")
    ))
)

data class GuidedProgramProgress(
    val programId: String,
    val startedOn: LocalDate,
    val validatedDates: List<LocalDate>,
    val requiredDays: Int,
    val active: Boolean,
    val ultraAccess: Boolean,
    val hasValidationToday: Boolean,
    val dateRollback: Boolean
) {
    val completedDays: Int get() = validatedDates.size
    val completed: Boolean get() = completedDays >= requiredDays
    val currentDay: Int? get() = if (completed) null else completedDays + 1
    val canValidateToday: Boolean get() = active && ultraAccess && !completed && !hasValidationToday && !dateRollback
}

data class GuidedProgramEntry(val program: GuidedProgram, val progress: GuidedProgramProgress?)

enum class GuidedProgramResult {
    SUCCESS, ULTRA_REQUIRED, UNKNOWN_PROGRAM, NOT_ACTIVE, ALREADY_VALIDATED_TODAY,
    DATE_ROLLBACK, DAY_CHANGED, ACTIVITY_NOT_CHECKED, COMPLETED, STORAGE_ERROR
}

private data class StoredGuidedProgress(val id: String, val startedOn: LocalDate, val dates: List<LocalDate>)
private data class GuidedState(
    val activeId: String? = null,
    val lastValidationDate: LocalDate? = null,
    val programs: List<StoredGuidedProgress> = emptyList()
) {
    fun latestDate(): LocalDate? = (programs.flatMap { it.dates + it.startedOn } + listOfNotNull(lastValidationDate)).maxOrNull()
}

/** Shares Selkie's local preferences and lock; all successful mutations use durable commit. */
class GuidedProgramStore internal constructor(private val prefs: SharedPreferences) {
    constructor(context: Context) : this(context.applicationContext.getSharedPreferences("maree", Context.MODE_PRIVATE))

    fun entries(today: LocalDate = LocalDate.now()): List<GuidedProgramEntry> = synchronized(LocalFeatureLock.lock) {
        val state = readState()
        GUIDED_PROGRAMS.map { GuidedProgramEntry(it, snapshot(state, it.id, today)) }
    }

    fun progress(id: String, today: LocalDate = LocalDate.now()): GuidedProgramProgress? = synchronized(LocalFeatureLock.lock) {
        snapshot(readState(), id, today)
    }

    fun activeProgramId(): String? = synchronized(LocalFeatureLock.lock) { readState().activeId }

    fun start(id: String): GuidedProgramResult = startOn(id, LocalDate.now())

    /** Calendar injection is internal; the UI cannot submit a past date. Resuming retains all days. */
    internal fun startOn(id: String, today: LocalDate): GuidedProgramResult = synchronized(LocalFeatureLock.lock) {
        if (!isUltra()) return@synchronized GuidedProgramResult.ULTRA_REQUIRED
        if (program(id) == null) return@synchronized GuidedProgramResult.UNKNOWN_PROGRAM
        val state = readState()
        if (state.latestDate()?.isAfter(today) == true) return@synchronized GuidedProgramResult.DATE_ROLLBACK
        val saved = state.programs.firstOrNull { it.id == id }
        val programs = if (saved == null) state.programs + StoredGuidedProgress(id, today, emptyList()) else state.programs
        save(state.copy(activeId = id, programs = programs))
    }

    /** expectedDate protects a checkbox left open across midnight; it cannot submit a past date. */
    fun validateToday(id: String, consciouslyChecked: Boolean, expectedDate: LocalDate = LocalDate.now()): GuidedProgramResult {
        val today = LocalDate.now()
        if (expectedDate != today) return GuidedProgramResult.DAY_CHANGED
        return validateOn(id, consciouslyChecked, today)
    }

    internal fun validateOn(id: String, consciouslyChecked: Boolean, today: LocalDate): GuidedProgramResult = synchronized(LocalFeatureLock.lock) {
        if (!isUltra()) return@synchronized GuidedProgramResult.ULTRA_REQUIRED
        val definition = program(id) ?: return@synchronized GuidedProgramResult.UNKNOWN_PROGRAM
        val state = readState()
        val progress = state.programs.firstOrNull { it.id == id }
        if (state.activeId != id || progress == null) return@synchronized GuidedProgramResult.NOT_ACTIVE
        if (state.latestDate()?.isAfter(today) == true) return@synchronized GuidedProgramResult.DATE_ROLLBACK
        if (state.lastValidationDate == today || state.programs.any { today in it.dates })
            return@synchronized GuidedProgramResult.ALREADY_VALIDATED_TODAY
        if (progress.dates.size >= definition.requiredDays) return@synchronized GuidedProgramResult.COMPLETED
        if (!consciouslyChecked) return@synchronized GuidedProgramResult.ACTIVITY_NOT_CHECKED
        save(state.copy(lastValidationDate = today, programs = state.programs.map {
            if (it.id == id) it.copy(dates = it.dates + today) else it
        }))
    }

    /** UI must confirm destructive actions. Deletion remains available after a downgrade. */
    fun discard(id: String): GuidedProgramResult = synchronized(LocalFeatureLock.lock) {
        if (program(id) == null) return@synchronized GuidedProgramResult.UNKNOWN_PROGRAM
        val state = readState()
        save(state.copy(activeId = state.activeId.takeUnless { it == id }, programs = state.programs.filterNot { it.id == id }))
    }

    /** Preserve the last validation date so a reset cannot create a second check-in that day. */
    fun reset(id: String): GuidedProgramResult = resetOn(id, LocalDate.now())

    internal fun resetOn(id: String, today: LocalDate): GuidedProgramResult = synchronized(LocalFeatureLock.lock) {
        if (!isUltra()) return@synchronized GuidedProgramResult.ULTRA_REQUIRED
        if (program(id) == null) return@synchronized GuidedProgramResult.UNKNOWN_PROGRAM
        val state = readState()
        if (state.latestDate()?.isAfter(today) == true) return@synchronized GuidedProgramResult.DATE_ROLLBACK
        if (state.programs.none { it.id == id }) return@synchronized GuidedProgramResult.NOT_ACTIVE
        save(state.copy(programs = state.programs.map { if (it.id == id) it.copy(startedOn = today, dates = emptyList()) else it }))
    }

    /** Read-only whitelist: no titles, activities, user questions, or unrelated preference keys. */
    fun exportJson(): JSONArray = synchronized(LocalFeatureLock.lock) {
        val state = readState()
        JSONArray().apply {
            if (state.programs.isEmpty() && state.lastValidationDate != null) {
                put(JSONObject().put("lastValidationDate", state.lastValidationDate.toString()))
            }
            state.programs.forEach { progress -> put(JSONObject().apply {
                put("programId", progress.id)
                put("startedOn", progress.startedOn.toString())
                put("validatedDates", JSONArray(progress.dates.map(LocalDate::toString)))
                put("activeProgramId", state.activeId ?: JSONObject.NULL)
                put("lastValidationDate", state.lastValidationDate?.toString() ?: JSONObject.NULL)
            }) }
        }
    }

    private fun snapshot(state: GuidedState, id: String, today: LocalDate): GuidedProgramProgress? {
        val definition = program(id) ?: return null
        val progress = state.programs.firstOrNull { it.id == id } ?: return null
        return GuidedProgramProgress(id, progress.startedOn, progress.dates.toList(), definition.requiredDays,
            state.activeId == id, isUltra(), state.lastValidationDate == today || state.programs.any { today in it.dates },
            state.latestDate()?.isAfter(today) == true)
    }

    private fun isUltra(): Boolean = runCatching { prefs.getString("plan", null) == PlanType.ULTRA.name }.getOrDefault(false)
    private fun program(id: String): GuidedProgram? = GUIDED_PROGRAMS.firstOrNull { it.id == id }

    private fun readState(): GuidedState = try {
        val raw = prefs.getString(KEY, null)
        if (raw == null || raw.length > MAX_JSON_LENGTH) GuidedState() else {
            val json = JSONObject(raw)
            val items = json.optJSONArray("programs") ?: JSONArray()
            val progresses = buildList<StoredGuidedProgress> {
                // Accept at most one record per authored ID, with bounded activity dates.
                for (index in 0 until minOf(items.length(), MAX_INPUT_ITEMS)) {
                    val item = items.optJSONObject(index) ?: continue
                    val id = item.opt("programId") as? String ?: continue
                    val definition = program(id) ?: continue
                    if (any { it.id == id }) continue
                    val startedOn = isoDate(item.opt("startedOn")) ?: continue
                    val dateArray = item.optJSONArray("validatedDates") ?: continue
                    val dates = buildList<LocalDate> {
                        for (dayIndex in 0 until minOf(dateArray.length(), definition.requiredDays)) {
                            val date = isoDate(dateArray.opt(dayIndex)) ?: continue
                            if (!date.isBefore(startedOn) && (lastOrNull()?.isBefore(date) != false)) add(date)
                        }
                    }
                    add(StoredGuidedProgress(id, startedOn, dates))
                }
            }
            val activeId = (json.opt("activeProgramId") as? String)?.takeIf { id -> progresses.any { it.id == id } }
            val lastValidation = (progresses.flatMap { it.dates } + listOfNotNull(isoDate(json.opt("lastValidationDate")))).maxOrNull()
            GuidedState(activeId, lastValidation, progresses)
        }
    } catch (_: Exception) { GuidedState() }

    private fun save(state: GuidedState): GuidedProgramResult {
        val data = JSONObject().apply {
            put("activeProgramId", state.activeId ?: JSONObject.NULL)
            put("lastValidationDate", state.lastValidationDate?.toString() ?: JSONObject.NULL)
            put("programs", JSONArray().apply {
                state.programs.take(GUIDED_PROGRAMS.size).forEach { progress -> put(JSONObject().apply {
                    put("programId", progress.id)
                    put("startedOn", progress.startedOn.toString())
                    put("validatedDates", JSONArray(progress.dates.map(LocalDate::toString)))
                }) }
            })
        }
        return if (runCatching { prefs.edit().putString(KEY, data.toString()).commit() }.getOrDefault(false))
            GuidedProgramResult.SUCCESS else GuidedProgramResult.STORAGE_ERROR
    }

    companion object {
        internal const val KEY = "guidedPrograms"
        private const val MAX_JSON_LENGTH = 20_000
        private const val MAX_INPUT_ITEMS = 12
        private fun isoDate(value: Any?): LocalDate? = (value as? String)?.takeIf { it.length == 10 }?.let {
            runCatching { LocalDate.parse(it).takeIf { date -> date.toString() == it } }.getOrNull()
        }
    }
}
