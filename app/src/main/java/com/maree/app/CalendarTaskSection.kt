package com.maree.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

@Composable
internal fun CalendarTaskSection(date: LocalDate, onTasksChanged: () -> Unit) {
    val context = LocalContext.current
    val store = remember(context) { CalendarTaskStore(context) }
    val scope = rememberCoroutineScope()
    var tasks by remember { mutableStateOf<List<CalendarTask>>(emptyList()) }
    var loaded by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    var shown by remember(date) { mutableIntStateOf(20) }
    var issue by remember { mutableStateOf(false) }
    var editing by remember(date) { mutableStateOf(false) }
    var editId by remember(date) { mutableStateOf<String?>(null) }
    var title by remember(date) { mutableStateOf("") }
    var deleting by remember(date) { mutableStateOf<CalendarTask?>(null) }
    var generation by remember { mutableIntStateOf(0) }
    LaunchedEffect(store) {
        val token = generation
        val rows = withContext(Dispatchers.IO) { store.entries() }
        if (token == generation) { tasks = rows; loaded = true }
    }
    fun change(action: () -> Boolean) {
        if (busy) return
        busy = true; generation++; issue = false
        scope.launch {
            try {
                val result = withContext(Dispatchers.IO) { action() to store.entries() }
                tasks = result.second; loaded = true; issue = !result.first
                if (result.first) { editing = false; deleting = null; onTasksChanged() }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) { issue = true
            } finally { busy = false }
        }
    }
    val dayTasks = tasks.filter { it.date == date }
    SectionLabel(tr("Tes tâches", "Your tasks"))
    if (loaded && dayTasks.isNotEmpty()) Text(
        tr("${dayTasks.count { it.completed }} sur ${dayTasks.size} terminées",
            "${dayTasks.count { it.completed }} of ${dayTasks.size} completed"),
        color = MareePalette.Teal, style = MaterialTheme.typography.labelLarge)
    Text(tr("Coche une tâche après l’avoir faite. Sans points ni rappel automatique.",
        "Check off a task when you have done it. No points or automatic reminders."),
        color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall)
    if (!loaded) Text(tr("Chargement…", "Loading…"), color = MareePalette.Muted)
    else if (dayTasks.isEmpty()) Surface(color = MareePalette.Peach,
        shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(tr("Une journée à imaginer", "A day to make your own"),
                color = MareePalette.Coral, style = MaterialTheme.typography.titleMedium)
            Text(tr("Lecture, promenade, projet… Ajoute un petit objectif pour cette date.",
                "Reading, a walk, a project… Add a small goal for this date."),
                color = MareePalette.Muted, style = MaterialTheme.typography.bodyMedium)
        }
    }
    dayTasks.take(shown).forEach { task ->
        Surface(color = if (task.completed) MareePalette.Mint else MareePalette.Subtle,
            shape = RoundedCornerShape(18.dp), border = BorderStroke(1.dp, MareePalette.Outline),
            modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(Modifier.fillMaxWidth().heightIn(min = 48.dp).toggleable(
                    value = task.completed, enabled = !busy, role = Role.Checkbox,
                    onValueChange = { checked -> change { store.setCompleted(task.id, checked) } }
                ), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = task.completed, onCheckedChange = null, enabled = !busy)
                    Column(Modifier.weight(1f)) {
                        Text(task.title, color = MareePalette.Ink,
                            style = MaterialTheme.typography.bodyMedium,
                            textDecoration = if (task.completed) TextDecoration.LineThrough else null)
                        Text(if (task.completed) tr("Terminée", "Completed") else tr("À faire", "To do"),
                            color = MareePalette.Muted, style = MaterialTheme.typography.labelSmall)
                    }
                }
                if (LocalDensity.current.fontScale > 1.3f) Column {
                    TextButton(enabled = !busy, modifier = Modifier.heightIn(min = 48.dp),
                        onClick = { editId = task.id; title = task.title; editing = true }) { Text(tr("Modifier", "Edit")) }
                    TextButton(enabled = !busy, modifier = Modifier.heightIn(min = 48.dp),
                        onClick = { deleting = task }) { Text(tr("Supprimer", "Delete")) }
                } else Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TextButton(enabled = !busy, modifier = Modifier.heightIn(min = 48.dp),
                        onClick = { editId = task.id; title = task.title; editing = true }) { Text(tr("Modifier", "Edit")) }
                    TextButton(enabled = !busy, modifier = Modifier.heightIn(min = 48.dp),
                        onClick = { deleting = task }) { Text(tr("Supprimer", "Delete")) }
                }
            }
        }
    }
    if (tasks.count { it.date == date } > shown) TextButton(onClick = { shown += 20 }) {
        Text(tr("Afficher plus de tâches", "Show more tasks"))
    }
    PrimaryAction(tr("+ Ajouter une tâche", "+ Add a task"), modifier = Modifier.fillMaxWidth(), enabled = loaded && !busy && tasks.size < 500,
        onClick = { editId = null; title = ""; editing = true })
    if (tasks.size >= 500) Text(tr("500 tâches enregistrées. Supprime les anciennes pour en ajouter.", "500 tasks saved. Delete older ones to add more."), color = MareePalette.Muted)
    if (issue) Text(tr("Le changement n’a pas pu être enregistré. Réessaie.", "The change could not be saved. Try again."), color = MareePalette.Danger)
    if (editing) AlertDialog(
        onDismissRequest = { if (!busy) editing = false },
        title = { Text(if (editId == null) tr("Nouvelle tâche", "New task") else tr("Modifier la tâche", "Edit task")) },
        text = { OutlinedTextField(value = title, onValueChange = { if (it.length <= 240) title = it },
            label = { Text(tr("Que veux-tu faire ?", "What would you like to do?")) },
            supportingText = { Text("${title.length}/240") }, enabled = !busy, modifier = Modifier.fillMaxWidth()) },
        confirmButton = { TextButton(enabled = !busy && title.isNotBlank(), onClick = {
            val targetDate = date; val targetTitle = title; val targetId = editId
            change { store.save(targetDate, targetTitle, targetId) }
        }) { Text(tr("Enregistrer", "Save")) } },
        dismissButton = { TextButton(enabled = !busy, onClick = { editing = false }) { Text(tr("Annuler", "Cancel")) } }
    )
    deleting?.let { target -> AlertDialog(
        onDismissRequest = { if (!busy) deleting = null },
        title = { Text(tr("Supprimer cette tâche ?", "Delete this task?")) },
        text = { Text(target.title) },
        confirmButton = { TextButton(enabled = !busy, onClick = { change { store.delete(target.id) } }) { Text(tr("Supprimer", "Delete")) } },
        dismissButton = { TextButton(enabled = !busy, onClick = { deleting = null }) { Text(tr("Annuler", "Cancel")) } }
    ) }
}
