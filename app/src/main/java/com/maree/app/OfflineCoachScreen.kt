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
                Text("‹ Revenir au coach hors ligne")
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
    val engine = remember { RuleCoach() }
    val history = remember { OfflineCoachHistory(context) }
    val scope = rememberCoroutineScope()
    val scroll = rememberLazyListState()
    val topics = remember { coachTopics.associateBy { it.id } }
    val appNames = remember(snapshot.apps.map { it.packageName }) {
        snapshot.apps.associate { it.packageName to getName(it.packageName) }
    }
    val coachContext = OfflineCoachContext(snapshot.pointUnits, monitoringActive,
        snapshot.apps.map { OfflineCoachApp(appNames[it.packageName] ?: it.packageName, it.usedMillis) })
    var turns by remember { mutableStateOf<List<OfflineCoachTurn>>(emptyList()) }
    var nextId by remember { mutableStateOf(0L) }
    var question by remember { mutableStateOf("") }
    var saving by remember { mutableStateOf(false) }
    var saveUnknown by remember { mutableStateOf(history.enabled) }
    var saved by remember { mutableStateOf(history.entries()) }
    var showSettings by rememberSaveable { mutableStateOf(false) }
    var showSaved by remember { mutableStateOf(false) }
    var notice by remember { mutableStateOf<String?>(null) }
    var showHelp by rememberSaveable { mutableStateOf(false) }
    val helpSections = remember { selkieHelpEntries.groupBy { it.section } }
    var helpSection by rememberSaveable { mutableStateOf(helpSections.keys.first().label) }
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
                if (!ok) notice = "Le changement n’a pas pu être enregistré. Réessaie."
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                notice = "Le stockage n’est pas disponible. Tes conseils restent accessibles."
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
            ScreenHeading("COACH", "Un conseil, un petit pas.",
                "Gratuit, sans compte et disponible hors ligne.")
        }
        item {
            AppCardSurface {
                StatusPill("${coachTopics.size} sujets · conseils et aide Selkie")
                Text("Pose une question sur tes habitudes ou sur Selkie : points, limites, défis, offres et confidentialité. Je choisis des réponses écrites à l’avance pour les sujets connus.",
                    color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp)
                if (!monitoringActive) Text("Le suivi est en pause : les durées ne seront pas utilisées dans mes conseils.",
                    color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 19.sp)
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (turns.isEmpty() && !showHelp) AppCardSurface {
                    SectionLabel("Un conseil pour ta journée")
                    TopicButtons(listOf("tiktok", "sleep", "boredom", "points"), topics, !saving) {
                        submit(it.label, it.id)
                    }
                }
                OutlinedButton(onClick = { showHelp = !showHelp }, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)) {
                    Text(if (showHelp) "Masquer l’aide Selkie" else "Comprendre Selkie · ${selkieHelpTopics.size} questions")
                }
                if (showHelp) AppCardSurface {
                    SectionLabel("L’aide de l’application")
                    Text("Choisis une rubrique, puis une question. Tu peux aussi écrire directement dans la discussion.",
                        color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp)
                    Box {
                        TextButton(onClick = { chooseHelpSection = true }) {
                            Text("$helpSection ▾")
                        }
                        DropdownMenu(expanded = chooseHelpSection,
                            onDismissRequest = { chooseHelpSection = false }) {
                            helpSections.keys.forEach { section ->
                                DropdownMenuItem(text = { Text(section.label) }, onClick = {
                                    helpSection = section.label
                                    chooseHelpSection = false
                                })
                            }
                        }
                    }
                    val entries = helpSections.entries.firstOrNull { it.key.label == helpSection }?.value
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
                SectionLabel(if (turn.fromUser) "Toi" else "Coach hors ligne")
                Text(turn.text, color = MareePalette.Ink, fontSize = 16.sp, lineHeight = 24.sp)
                if (turn.suggestions.isNotEmpty()) TopicButtons(turn.suggestions, topics, !saving) {
                    submit(it.label, it.id)
                }
            }
        }
        item {
            AppCardSurface {
                OutlinedTextField(value = question, onValueChange = { question = it.take(800) },
                    label = { Text("Ta question") }, placeholder = { Text("Je passe trop de temps sur tiktoc…") },
                    minLines = 2, maxLines = 5, modifier = Modifier.fillMaxWidth(),
                    supportingText = { Text("${question.length}/800") }, shape = RoundedCornerShape(14.dp))
                PrimaryAction("Envoyer", onClick = { submit(question) }, modifier = Modifier.fillMaxWidth(),
                    enabled = !saving && question.isNotBlank())
                Text("Tes échanges restent en mémoire pendant l’ouverture de cet onglet. Aucun message n’est envoyé.",
                    color = MareePalette.Muted, fontSize = 12.sp, lineHeight = 18.sp)
                if (turns.isNotEmpty()) TextButton(onClick = { turns = emptyList(); question = "" }) {
                    Text("Nouvelle conversation")
                }
            }
        }
        item {
            TextButton(onClick = { showSettings = !showSettings }) {
                Text(if (showSettings) "Masquer mes choix de confidentialité" else "Mes choix de confidentialité")
            }
        }
        if (showSettings) item {
            AppCardSurface {
                SectionLabel("Questions non comprises")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = saveUnknown, enabled = !saving, onCheckedChange = { enabled ->
                        val generation = history.generation()
                        storeAction { history.setEnabled(enabled, expectedGeneration = generation) }
                    })
                    Text("Les garder sur ce téléphone", color = MareePalette.Ink, fontSize = 14.sp,
                        modifier = Modifier.weight(1f), lineHeight = 21.sp)
                }
                Text("Option désactivée au départ. Jusqu’à 50 questions pour tes propres repères ; les plus anciennes sont remplacées. Rien n’est transmis à l’éditeur. Désactiver cette option les efface. Évite d’y écrire des informations privées.",
                    color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 19.sp)
                if (saved.isNotEmpty()) {
                    TextButton(onClick = { showSaved = !showSaved }) {
                        Text(if (showSaved) "Masquer les questions" else "Voir mes ${saved.size} questions")
                    }
                    if (showSaved) saved.forEach { Text(it, color = MareePalette.Ink,
                        fontSize = 14.sp, lineHeight = 21.sp) }
                    TextButton(enabled = !saving, onClick = {
                        val generation = history.generation()
                        storeAction { history.clearEntries(expectedGeneration = generation) }
                    }) { Text("Effacer les questions enregistrées") }
                }
            }
        }
        notice?.let { message -> item { Text(message, color = MareePalette.Muted, fontSize = 13.sp) } }
        if (onOnline != null) item {
            TextButton(onClick = onOnline) { Text("Ouvrir le dialogue IA en ligne") }
        }
    }
}
