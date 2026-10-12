package com.maree.app

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
private fun TopicButtons(ids: List<String>, topics: Map<String, CoachTopic>, enabled: Boolean,
    onSelect: (CoachTopic) -> Unit) {
    ids.mapNotNull(topics::get).forEach { topic ->
        OutlinedButton(onClick = { onSelect(topic) }, enabled = enabled,
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
            Text(topic.label)
        }
    }
}

@Composable
internal fun CoachTab(snapshot: MareeSnapshot, monitoringActive: Boolean, getName: (String) -> String) {
    var online by rememberSaveable { mutableStateOf(false) }
    val configured = BuildConfig.SUPABASE_URL.isNotBlank() && BuildConfig.SUPABASE_PUBLIC_KEY.isNotBlank()
    BackHandler(enabled = online) { online = false }
    if (online && configured) {
        Column(Modifier.fillMaxSize()) {
            TextButton(onClick = { online = false }, modifier = Modifier.padding(horizontal = 12.dp)) {
                Text(tr("‹ Revenir au coach hors ligne", "‹ Back to the offline coach"))
            }
            OnlineCoachTab()
        }
    } else {
        OfflineCoachTab(snapshot, monitoringActive, getName,
            onOnline = if (configured) ({ online = true }) else null)
    }
}

@Composable
private fun OfflineCoachTab(
    snapshot: MareeSnapshot,
    monitoringActive: Boolean,
    getName: (String) -> String,
    onOnline: (() -> Unit)?
) {
    val context = LocalContext.current
    val language = AppLanguage.current
    val engine = remember(language) { RuleCoach(language = language) }
    val history = remember { OfflineCoachHistory(context) }
    val scope = rememberCoroutineScope()
    val focus = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    val scroll = rememberLazyListState()
    val topics = remember(language) { coachTopicsFor(language).associateBy { it.id } }
    val appNames = remember(language, snapshot.apps.map { it.packageName }) {
        snapshot.apps.associate { it.packageName to getName(it.packageName) }
    }
    val coachContext = OfflineCoachContext(snapshot.pointUnits, monitoringActive,
        snapshot.apps.map { OfflineCoachApp(appNames[it.packageName] ?: it.packageName, it.usedMillis) })
    var question by remember(language) { mutableStateOf("") }
    var answeredQuestion by remember(language) { mutableStateOf("") }
    var answer by remember(language) { mutableStateOf<OfflineCoachReply?>(null) }
    var saving by remember { mutableStateOf(false) }
    var saveUnknown by remember { mutableStateOf(history.enabled) }
    var saved by remember { mutableStateOf(history.entries()) }
    var showSettings by rememberSaveable { mutableStateOf(false) }
    var showSaved by remember { mutableStateOf(false) }
    var notice by remember(language) { mutableStateOf<String?>(null) }
    var showHelp by rememberSaveable { mutableStateOf(false) }
    val helpEntries = remember(language) { helpEntriesFor(language) }
    val helpSections = remember(language) { helpEntries.groupBy { it.section } }
    var helpSection by rememberSaveable { mutableStateOf(helpSections.keys.first().name) }
    var chooseHelpSection by remember { mutableStateOf(false) }
    val preparedQuestions = remember(language) { preparedCoachQuestionsFor(language) }
    val preparedSections = remember(language) { preparedCoachSectionsFor(language) }
    var showLibrary by rememberSaveable { mutableStateOf(false) }
    var libraryQuery by remember(language) { mutableStateOf("") }
    var librarySection by rememberSaveable { mutableStateOf("all") }
    var chooseLibrarySection by remember { mutableStateOf(false) }
    var libraryPage by remember(language) { mutableStateOf(0) }
    val libraryResults = remember(language, libraryQuery, librarySection) {
        searchPreparedCoachQuestions(libraryQuery, language,
            sectionId = librarySection.takeUnless { it == "all" }, limit = preparedQuestions.size)
    }
    val pageSize = 20
    val lastPage = ((libraryResults.size - 1).coerceAtLeast(0)) / pageSize
    val currentPage = libraryPage.coerceIn(0, lastPage)
    val visibleQuestions = libraryResults.drop(currentPage * pageSize).take(pageSize)
    val visibleAnswer = answer.takeIf { question.trim() == answeredQuestion }

    fun storeAction(action: () -> Boolean) {
        if (saving) return
        saving = true
        notice = null
        scope.launch {
            try {
                val ok = withContext(Dispatchers.IO) { action() }
                saveUnknown = history.enabled
                saved = history.entries()
                if (!ok) notice = tr("Le changement n’a pas pu être enregistré. Réessaie.", "The change could not be saved. Try again.")
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                notice = tr("Le stockage n’est pas disponible. Tes conseils restent accessibles.", "Storage is unavailable. You can still get advice.")
            } finally { saving = false }
        }
    }

    fun submit(text: String, topicId: String? = null, preparedId: String? = null,
        returnToQuestion: Boolean = false) {
        if (saving) return
        val outgoing = text.trim().take(800)
        if (outgoing.isEmpty()) return
        val reply = when {
            preparedId != null -> engine.replyForPreparedQuestion(preparedId, coachContext)
            topicId != null -> engine.replyForTopic(topicId, coachContext)
            else -> engine.reply(outgoing, coachContext)
        }
        question = outgoing
        answeredQuestion = outgoing
        answer = reply
        focus.clearFocus()
        keyboard?.hide()
        if (returnToQuestion) scope.launch { scroll.animateScrollToItem(1) }
        if (reply.unrecognized && !reply.sensitive && saveUnknown) {
            val generation = history.generation()
            storeAction { history.recordUnknown(outgoing, expectedGeneration = generation) }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().imePadding().padding(horizontal = 20.dp, vertical = 4.dp),
        state = scroll,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            ScreenHeading("COACH", tr("Un conseil, un petit pas.", "One tip, one small step."),
                tr("Tes questions en haut. Mes conseils juste dessous.", "Your questions above. My advice just below."))
        }
        item {
            AppCardSurface {
                SectionLabel(tr("Ta question", "Your question"))
                OutlinedTextField(value = question, onValueChange = { question = it.take(800) },
                    label = { Text(tr("Écris ta question", "Write your question")) },
                    placeholder = { Text(tr("Comment arrêter de scroller le soir ?", "How can I stop scrolling in the evening?")) },
                    minLines = 2, maxLines = 5, modifier = Modifier.fillMaxWidth(),
                    supportingText = { Text("${question.length}/800") }, shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { submit(question) }))
                PrimaryAction(tr("Envoyer", "Send"), onClick = { submit(question) },
                    modifier = Modifier.fillMaxWidth(), enabled = !saving && question.isNotBlank())
                if (question.isNotBlank() || answer != null) TextButton(onClick = {
                    question = ""; answeredQuestion = ""; answer = null
                }) { Text(tr("Nouvelle question", "New question")) }
            }
        }
        item {
            AppCardSurface {
                SectionLabel(tr("La réponse du coach", "The coach’s answer"))
                if (visibleAnswer != null) {
                    Text(visibleAnswer.text, color = MareePalette.Ink, fontSize = 16.sp, lineHeight = 24.sp)
                    if (visibleAnswer.suggestions.isNotEmpty()) TopicButtons(visibleAnswer.suggestions, topics, !saving) {
                        submit(it.label, it.id, returnToQuestion = true)
                    }
                } else {
                    Text(if (answer != null) tr("Ta question a changé. Appuie sur Envoyer pour obtenir le conseil correspondant.",
                        "Your question has changed. Tap Send to get advice for the new question.")
                        else tr("Écris une question au-dessus, ou choisis un sujet pour commencer.",
                            "Write a question above, or choose a topic to get started."),
                        color = MareePalette.Muted, fontSize = 15.sp, lineHeight = 22.sp)
                }
                if (answer == null) {
                    TopicButtons(listOf("tiktok", "sleep", "boredom", "points"), topics, !saving) {
                        submit(it.label, it.id, returnToQuestion = true)
                    }
                }
                Text(tr("Conseils écrits à l’avance · gratuits et hors ligne. Le coach ne peut pas répondre à toutes les questions nouvelles.",
                    "Prepared advice · free and offline. The coach cannot answer every new question."),
                    color = MareePalette.Muted, fontSize = 12.sp, lineHeight = 18.sp)
                if (!monitoringActive) Text(tr("Le suivi est en pause : les durées ne sont pas utilisées dans les conseils.",
                    "Tracking is paused: recorded usage is not used in advice."),
                    color = MareePalette.Muted, fontSize = 12.sp, lineHeight = 18.sp)
            }
        }
        item {
            OutlinedButton(onClick = { showLibrary = !showLibrary }, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)) {
                Text(if (showLibrary) tr("Masquer la bibliothèque", "Hide the library")
                    else tr("Explorer ${preparedQuestions.size} questions et réponses", "Explore ${preparedQuestions.size} questions and answers"))
            }
        }
        if (showLibrary) item {
            AppCardSurface {
                SectionLabel(tr("La bibliothèque du coach", "The coach library"))
                Text(tr("Des situations concrètes pour trouver un conseil utile. Choisis une question : elle s’affichera en haut, avec sa réponse juste dessous.",
                    "Real-life situations to find useful advice. Choose a question: it will appear above, with its answer just below."),
                    color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp)
                OutlinedTextField(value = libraryQuery, onValueChange = {
                    libraryQuery = it.take(120); libraryPage = 0
                }, label = { Text(tr("Rechercher une question", "Search questions")) }, singleLine = true,
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp))
                Box {
                    TextButton(onClick = { chooseLibrarySection = true }) {
                        Text("${preparedSections.firstOrNull { it.id == librarySection }?.label ?: tr("Toutes les rubriques", "All sections")} ▾")
                    }
                    DropdownMenu(expanded = chooseLibrarySection,
                        onDismissRequest = { chooseLibrarySection = false }) {
                        DropdownMenuItem(text = { Text(tr("Toutes les rubriques", "All sections")) }, onClick = {
                            librarySection = "all"; libraryPage = 0; chooseLibrarySection = false
                        })
                        preparedSections.forEach { section ->
                            DropdownMenuItem(text = { Text(section.label) }, onClick = {
                                librarySection = section.id; libraryPage = 0; chooseLibrarySection = false
                            })
                        }
                    }
                }
                Text(tr("${libraryResults.size} questions trouvées", "${libraryResults.size} questions found"),
                    color = MareePalette.Muted, fontSize = 13.sp)
                if (visibleQuestions.isEmpty()) Text(tr("Aucune question pour cette recherche. Essaie un mot plus simple ou une autre rubrique.",
                    "No questions found. Try a simpler word or another section."),
                    color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp)
                visibleQuestions.forEach { prepared ->
                    OutlinedButton(onClick = {
                        submit(prepared.question, preparedId = prepared.id, returnToQuestion = true)
                    }, enabled = !saving, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                        Text(prepared.question, modifier = Modifier.fillMaxWidth(), lineHeight = 21.sp)
                    }
                }
                if (lastPage > 0) Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = {
                        libraryPage = currentPage - 1
                        scope.launch { scroll.animateScrollToItem(4) }
                    }, enabled = currentPage > 0) {
                        Text(tr("‹ Précédent", "‹ Previous"))
                    }
                    Text("${currentPage + 1} / ${lastPage + 1}", color = MareePalette.Muted, fontSize = 13.sp)
                    TextButton(onClick = {
                        libraryPage = currentPage + 1
                        scope.launch { scroll.animateScrollToItem(4) }
                    }, enabled = currentPage < lastPage) {
                        Text(tr("Suivant ›", "Next ›"))
                    }
                }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { showHelp = !showHelp }, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)) {
                    Text(if (showHelp) tr("Masquer l’aide Selkie", "Hide Selkie help") else tr("Comprendre Selkie · ${helpEntries.size} questions", "Understand Selkie · ${helpEntries.size} questions"))
                }
                if (showHelp) AppCardSurface {
                    SectionLabel(tr("L’aide de l’application", "App help"))
                    Text(tr("Choisis une rubrique, puis une question. La réponse s’affichera au-dessus. Tu peux aussi écrire dans le champ Ta question.", "Choose a section, then a question. Its answer will appear above. You can also type in the Your question field."),
                        color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp)
                    Box {
                        TextButton(onClick = { chooseHelpSection = true }) {
                            Text("${helpSections.keys.firstOrNull { it.name == helpSection }?.label ?: helpSections.keys.first().label} ▾")
                        }
                        DropdownMenu(expanded = chooseHelpSection,
                            onDismissRequest = { chooseHelpSection = false }) {
                            helpSections.keys.forEach { section ->
                                DropdownMenuItem(text = { Text(section.label) }, onClick = {
                                    helpSection = section.name
                                    chooseHelpSection = false
                                })
                            }
                        }
                    }
                    val entries = helpSections.entries.firstOrNull { it.key.name == helpSection }?.value
                        ?: helpSections.values.first()
                    TopicButtons(entries.map { it.topic.id }, topics, !saving) {
                        submit(it.label, it.id, returnToQuestion = true)
                        showHelp = false
                    }
                }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(tr("Ta question et sa réponse restent en mémoire pendant l’ouverture de cet onglet. Aucun message n’est envoyé.",
                    "Your question and its answer stay in memory while this tab is open. No messages are sent."),
                    color = MareePalette.Muted, fontSize = 12.sp, lineHeight = 18.sp)
                TextButton(onClick = { showSettings = !showSettings }) {
                    Text(if (showSettings) tr("Masquer mes choix de confidentialité", "Hide my privacy choices") else tr("Mes choix de confidentialité", "My privacy choices"))
                }
            }
        }
        if (showSettings) item {
            AppCardSurface {
                SectionLabel(tr("Questions non comprises", "Unrecognised questions"))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = saveUnknown, enabled = !saving, onCheckedChange = { enabled ->
                        val generation = history.generation()
                        storeAction { history.setEnabled(enabled, expectedGeneration = generation) }
                    })
                    Text(tr("Les garder sur ce téléphone", "Keep them on this phone"), color = MareePalette.Ink, fontSize = 14.sp,
                        modifier = Modifier.weight(1f), lineHeight = 21.sp)
                }
                Text(tr("Option désactivée au départ. Jusqu’à 50 questions pour tes propres repères ; les plus anciennes sont remplacées. Rien n’est transmis à l’éditeur. Désactiver cette option les efface. Évite d’y écrire des informations privées.", "Off by default. Keep up to 50 questions for your own reference; the oldest are replaced. Nothing is sent to the publisher. Turning this off deletes them. Avoid entering private information."),
                    color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 19.sp)
                if (saved.isNotEmpty()) {
                    TextButton(onClick = { showSaved = !showSaved }) {
                        Text(if (showSaved) tr("Masquer les questions", "Hide questions") else tr("Voir mes ${saved.size} questions", "View my ${saved.size} questions"))
                    }
                    if (showSaved) saved.forEach { Text(it, color = MareePalette.Ink,
                        fontSize = 14.sp, lineHeight = 21.sp) }
                    TextButton(enabled = !saving, onClick = {
                        val generation = history.generation()
                        storeAction { history.clearEntries(expectedGeneration = generation) }
                    }) { Text(tr("Effacer les questions enregistrées", "Delete saved questions")) }
                }
            }
        }
        notice?.let { message -> item { Text(message, color = MareePalette.Muted, fontSize = 13.sp) } }
        if (onOnline != null) item {
            TextButton(onClick = onOnline) { Text(tr("Ouvrir le dialogue IA en ligne", "Open online AI chat")) }
        }
    }
}
