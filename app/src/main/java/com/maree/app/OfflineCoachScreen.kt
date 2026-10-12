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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private data class OfflineCoachTurn(
    val id: Long,
    val fromUser: Boolean,
    val text: String,
    val suggestions: List<String> = emptyList()
)

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
    val scroll = rememberLazyListState()
    val topics = remember(language) { coachTopicsFor(language).associateBy { it.id } }
    val appNames = remember(language, snapshot.apps.map { it.packageName }) {
        snapshot.apps.associate { it.packageName to getName(it.packageName) }
    }
    val coachContext = OfflineCoachContext(snapshot.pointUnits, monitoringActive,
        snapshot.apps.map { OfflineCoachApp(appNames[it.packageName] ?: it.packageName, it.usedMillis) })
    var turns by remember(language) { mutableStateOf<List<OfflineCoachTurn>>(emptyList()) }
    var nextId by remember(language) { mutableStateOf(0L) }
    var question by remember(language) { mutableStateOf("") }
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

    LaunchedEffect(turns.size, nextId) {
        if (turns.isNotEmpty()) scroll.animateScrollToItem(3 + turns.size)
        else scroll.scrollToItem(0)
    }

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

    fun submit(text: String, topicId: String? = null) {
        if (saving) return
        val outgoing = text.trim().take(800)
        if (outgoing.isEmpty()) return
        val reply = if (topicId == null) engine.reply(outgoing, coachContext)
            else engine.replyForTopic(topicId, coachContext)
        turns = (turns + OfflineCoachTurn(nextId++, true, outgoing) +
            OfflineCoachTurn(nextId++, false, reply.text, reply.suggestions)).takeLast(40)
        question = ""
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
                tr("Gratuit, sans compte et disponible hors ligne.", "Free, without an account and available offline."))
        }
        item {
            AppCardSurface {
                StatusPill(tr("${topics.size} sujets · conseils et aide Selkie", "${topics.size} topics · advice and Selkie help"))
                Text(tr("Pose une question sur tes habitudes ou sur Selkie : points, limites, défis, offres et confidentialité. Je choisis des réponses écrites à l’avance pour les sujets connus.", "Ask about your habits or about Selkie: points, limits, challenges, plans and privacy. I choose prepared answers for topics I know."),
                    color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp)
                if (!monitoringActive) Text(tr("Le suivi est en pause : les durées ne seront pas utilisées dans mes conseils.", "Tracking is paused: recorded usage will not be used in my advice."),
                    color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 19.sp)
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (turns.isEmpty() && !showHelp) AppCardSurface {
                    SectionLabel(tr("Un conseil pour ta journée", "A tip for your day"))
                    TopicButtons(listOf("tiktok", "sleep", "boredom", "points"), topics, !saving) {
                        submit(it.label, it.id)
                    }
                }
                OutlinedButton(onClick = { showHelp = !showHelp }, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)) {
                    Text(if (showHelp) tr("Masquer l’aide Selkie", "Hide Selkie help") else tr("Comprendre Selkie · ${helpEntries.size} questions", "Understand Selkie · ${helpEntries.size} questions"))
                }
                if (showHelp) AppCardSurface {
                    SectionLabel(tr("L’aide de l’application", "App help"))
                    Text(tr("Choisis une rubrique, puis une question. Tu peux aussi écrire directement dans la discussion.", "Choose a section, then a question. You can also type directly in the chat."),
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
                        submit(it.label, it.id)
                        showHelp = false
                    }
                }
            }
        }
        items(turns, key = { it.id }) { turn ->
            AppCardSurface(containerColor = if (turn.fromUser) MareePalette.Subtle else MareePalette.Surface) {
                SectionLabel(if (turn.fromUser) tr("Toi", "You") else tr("Coach hors ligne", "Offline coach"))
                Text(turn.text, color = MareePalette.Ink, fontSize = 16.sp, lineHeight = 24.sp)
                if (turn.suggestions.isNotEmpty()) TopicButtons(turn.suggestions, topics, !saving) {
                    submit(it.label, it.id)
                }
            }
        }
        item {
            AppCardSurface {
                OutlinedTextField(value = question, onValueChange = { question = it.take(800) },
                    label = { Text(tr("Ta question", "Your question")) }, placeholder = { Text(tr("Je passe trop de temps sur tiktoc…", "I spend too much time on tiktoc…")) },
                    minLines = 2, maxLines = 5, modifier = Modifier.fillMaxWidth(),
                    supportingText = { Text("${question.length}/800") }, shape = RoundedCornerShape(14.dp))
                PrimaryAction(tr("Envoyer", "Send"), onClick = { submit(question) }, modifier = Modifier.fillMaxWidth(),
                    enabled = !saving && question.isNotBlank())
                Text(tr("Tes échanges restent en mémoire pendant l’ouverture de cet onglet. Aucun message n’est envoyé.", "Your conversation stays in memory while this tab is open. No messages are sent."),
                    color = MareePalette.Muted, fontSize = 12.sp, lineHeight = 18.sp)
                if (turns.isNotEmpty()) TextButton(onClick = { turns = emptyList(); question = "" }) {
                    Text(tr("Nouvelle conversation", "New conversation"))
                }
            }
        }
        item {
            TextButton(onClick = { showSettings = !showSettings }) {
                Text(if (showSettings) tr("Masquer mes choix de confidentialité", "Hide my privacy choices") else tr("Mes choix de confidentialité", "My privacy choices"))
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
