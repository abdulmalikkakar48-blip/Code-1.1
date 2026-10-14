package com.maree.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    SectionLabel(tr("À faire ce jour-là", "To do on this day"))
    Text(tr("Tes tâches personnelles, sans points ni rappel automatique.", "Your personal tasks, without points or automatic reminders."), color = MareePalette.Muted)
    if (!loaded) Text(tr("Chargement…", "Loading…"), color = MareePalette.Muted)
    else if (tasks.none { it.date == date }) Text(tr("Aucune tâche prévue. Ajoute ton premier petit objectif.", "Nothing planned. Add your first small goal."), color = MareePalette.Muted)
    tasks.filter { it.date == date }.take(shown).forEach { task ->
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(Modifier.fillMaxWidth().heightIn(min = 48.dp).toggleable(
                value = task.completed, enabled = !busy, role = Role.Checkbox,
                onValueChange = { checked -> change { store.setCompleted(task.id, checked) } }
            ), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = task.completed, onCheckedChange = null, enabled = !busy)
                Text(task.title, modifier = Modifier.weight(1f), color = MareePalette.Ink)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TextButton(enabled = !busy, onClick = { editId = task.id; title = task.title; editing = true }) { Text(tr("Modifier", "Edit")) }
                TextButton(enabled = !busy, onClick = { deleting = task }) { Text(tr("Supprimer", "Delete")) }
            }
        }
    }
    if (tasks.count { it.date == date } > shown) TextButton(onClick = { shown += 20 }) {
        Text(tr("Afficher plus de tâches", "Show more tasks"))
    }
    PrimaryAction(tr("Ajouter une tâche", "Add a task"), enabled = loaded && !busy && tasks.size < 500,
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
