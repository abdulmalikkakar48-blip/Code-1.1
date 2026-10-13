package com.maree.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import java.util.UUID

/** A section inside the daily page; storage and dialogs remain local to this phone. */
@Composable
internal fun LimitSchedulesSection(
    snapshot: MareeSnapshot,
    getName: (String) -> String,
    onOffers: () -> Unit,
    onChanged: () -> Unit
) {
    val context = LocalContext.current
    val store = remember(context) { LimitScheduleStore(context) }
    var revision by remember { mutableStateOf(0) }
    val schedules = remember(snapshot, revision) { store.entries() }
    var creating by rememberSaveable { mutableStateOf(false) }
    var editingId by rememberSaveable { mutableStateOf<String?>(null) }
    var deletingId by rememberSaveable { mutableStateOf<String?>(null) }
    var issue by rememberSaveable { mutableStateOf<String?>(null) }
    val paid = snapshot.plan != PlanType.FREE
    val canModify = paid && snapshot.pointUnits > 0
    val editing = schedules.firstOrNull { it.id == editingId }
    val deleting = schedules.firstOrNull { it.id == deletingId }

    fun refresh() {
        revision += 1
        issue = null
        onChanged()
    }

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        AppCardSurface {
            SectionLabel(tr("Tes plages horaires", "Your time slots"))
            StatusPill("Premium · Ultra")
            Text(tr("Choisis une limite différente selon les jours et les heures pour une application déjà suivie. Tout reste sur ce téléphone, utilisable hors ligne.",
                "Choose a different limit for selected days and times on an app you already track. Everything stays on this phone and works offline."),
                color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp)
            Text(tr("La plage modifie la limite du compteur en cours, sans remettre son temps à zéro. Une limite déjà atteinte peut verrouiller l’application pendant 24 h : ce verrouillage continue même après la fin de la plage.",
                "A time slot changes the limit of the current counter without resetting its usage. Reaching a limit can lock the app for 24 hours: that lock continues even after the slot ends."),
                color = MareePalette.Ink, fontSize = 13.sp, lineHeight = 20.sp)
            if (paid) {
                PrimaryAction(tr("Ajouter une plage horaire", "Add a time slot"),
                    onClick = { creating = true; issue = null },
                    enabled = canModify && snapshot.apps.isNotEmpty() && schedules.size < MAX_LIMIT_SCHEDULES,
                    modifier = Modifier.fillMaxWidth())
                when {
                    !canModify -> Text(tr("À 0 point, la création, la modification, l’activation et la suppression sont bloquées.",
                        "At 0 points, creating, editing, enabling and deleting slots are blocked."),
                        color = MareePalette.Muted, fontSize = 13.sp)
                    snapshot.apps.isEmpty() -> Text(tr("Ajoute d’abord une application au suivi.", "Add an app to tracking first."),
                        color = MareePalette.Muted, fontSize = 13.sp)
                    schedules.size >= MAX_LIMIT_SCHEDULES -> Text(tr("La limite de $MAX_LIMIT_SCHEDULES plages est atteinte.",
                        "You have reached the limit of $MAX_LIMIT_SCHEDULES slots."), color = MareePalette.Muted, fontSize = 13.sp)
                }
            } else {
                Text(tr("Aperçu : limite de 15 min les soirs de semaine, de 20:00 à 22:00. Avec Gratuit, tes plages enregistrées sont conservées mais ne s’appliquent pas.",
                    "Preview: a 15-minute limit on weekday evenings, from 20:00 to 22:00. On Free, saved slots are kept but do not apply."),
                    color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)
                OutlinedButton(onClick = onOffers, modifier = Modifier.fillMaxWidth()) {
                    Text(tr("Découvrir Premium et Ultra", "Explore Premium and Ultra"))
                }
            }
        }
        if (issue != null) Text(issue!!, color = MareePalette.Danger, fontSize = 14.sp)
        schedules.forEach { rule ->
            val tracked = snapshot.apps.any { it.packageName == rule.packageName }
            AppCardSurface {
                Text(rule.name, fontWeight = FontWeight.SemiBold, color = MareePalette.Ink,
                    fontSize = 18.sp, lineHeight = 24.sp)
                Text(getName(rule.packageName), color = MareePalette.Muted, fontSize = 14.sp)
                Text(rule.weekdays.sorted().joinToString(" · ") { scheduleDayName(it) },
                    color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)
                Text("${scheduleTime(rule.startMinute)} – ${scheduleTime(rule.endMinute)}" +
                    if (rule.endMinute < rule.startMinute) tr(" (lendemain)", " (next day)") else "",
                    color = MareePalette.Ink, fontSize = 15.sp)
                Text(tr("Limite du compteur : ${rule.limitMinutes} min", "Counter limit: ${rule.limitMinutes} min"),
                    color = MareePalette.Teal, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Row(Modifier.fillMaxWidth().toggleable(
                    value = rule.enabled, enabled = canModify, role = Role.Switch,
                    onValueChange = { enabled ->
                        val result = store.save(rule.copy(enabled = enabled), snapshot.plan, snapshot.pointUnits)
                        if (result == null) refresh() else issue = result
                    }
                ).heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(if (rule.enabled) tr("Plage activée", "Slot enabled") else tr("Plage désactivée", "Slot disabled"),
                        modifier = Modifier.weight(1f), color = MareePalette.Ink, fontSize = 14.sp)
                    Switch(checked = rule.enabled, onCheckedChange = null, enabled = canModify)
                }
                if (!paid) Text(tr("Conservée · ne s’applique pas avec Gratuit", "Saved · does not apply on Free"),
                    color = MareePalette.Muted, fontSize = 13.sp)
                if (!tracked) Text(tr("Cette application n’est plus suivie. La plage reste enregistrée et ne suit aucune nouvelle application.",
                    "This app is no longer tracked. The slot stays saved and does not track any new app."),
                    color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = { editingId = rule.id; issue = null }, enabled = canModify,
                        modifier = Modifier.weight(1f)) { Text(tr("Modifier", "Edit")) }
                    TextButton(onClick = { deletingId = rule.id; issue = null }, enabled = canModify,
                        modifier = Modifier.weight(1f)) { Text(tr("Supprimer", "Delete"),
                        color = if (canModify) MareePalette.Danger else MareePalette.Muted) }
                }
            }
        }
    }

    if (creating || editing != null) {
        LimitScheduleDialog(
            initial = editing,
            apps = snapshot.apps,
            canModify = canModify,
            getName = getName,
            onDismiss = { creating = false; editingId = null },
            onSave = { rule ->
                val result = store.save(rule, snapshot.plan, snapshot.pointUnits)
                if (result == null) { creating = false; editingId = null; refresh() }
                result
            }
        )
    }
    if (deleting != null) {
        AlertDialog(
            onDismissRequest = { deletingId = null },
            title = { Text(tr("Supprimer cette plage ?", "Delete this time slot?")) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(tr("« ${deleting.name} » sera supprimée. Le compteur en cours et tout verrouillage de 24 h sont conservés.",
                        "“${deleting.name}” will be deleted. The current counter and any 24-hour lock are preserved."))
                    if (issue != null) Text(issue!!, color = MareePalette.Danger)
                }
            },
            confirmButton = {
                TextButton(enabled = canModify, onClick = {
                    val result = store.remove(deleting.id, snapshot.plan, snapshot.pointUnits)
                    if (result == null) { deletingId = null; refresh() } else issue = result
                }) { Text(tr("Supprimer", "Delete"), color = if (canModify) MareePalette.Danger else MareePalette.Muted) }
            },
            dismissButton = { TextButton(onClick = { deletingId = null }) { Text(tr("Annuler", "Cancel")) } }
        )
    }
}

@Composable
private fun LimitScheduleDialog(
    initial: LimitSchedule?,
    apps: List<AppSnapshot>,
    canModify: Boolean,
    getName: (String) -> String,
    onDismiss: () -> Unit,
    onSave: (LimitSchedule) -> String?
) {
    val stateKey = initial?.id ?: "new"
    val id = rememberSaveable(stateKey) { initial?.id ?: UUID.randomUUID().toString() }
    var name by rememberSaveable(stateKey) { mutableStateOf(initial?.name.orEmpty()) }
    var packageName by rememberSaveable(stateKey) { mutableStateOf(initial?.packageName?.takeIf { saved ->
        apps.any { it.packageName == saved }
    }.orEmpty()) }
    var daysMask by rememberSaveable(stateKey) { mutableStateOf(initial?.weekdays?.fold(0) { mask, day ->
        mask or (1 shl (day - 1))
    } ?: 31) }
    var startText by rememberSaveable(stateKey) { mutableStateOf(scheduleTime(initial?.startMinute ?: 1200)) }
    var endText by rememberSaveable(stateKey) { mutableStateOf(scheduleTime(initial?.endMinute ?: 1320)) }
    var limitText by rememberSaveable(stateKey) { mutableStateOf((initial?.limitMinutes ?: 15).toString()) }
    var enabled by rememberSaveable(stateKey) { mutableStateOf(initial?.enabled ?: true) }
    var choosingApp by remember { mutableStateOf(false) }
    var error by rememberSaveable(stateKey) { mutableStateOf<String?>(null) }
    val start = parseScheduleTime(startText)
    val end = parseScheduleTime(endText)
    val limit = limitText.toIntOrNull()
    val validApp = apps.any { it.packageName == packageName }
    val ready = canModify && name.trim().isNotEmpty() && validApp && daysMask != 0 &&
        start != null && end != null && start != end && limit != null && limit in 5..240

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) tr("Nouvelle plage horaire", "New time slot") else tr("Modifier la plage", "Edit time slot")) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it.take(40); error = null },
                    enabled = canModify, singleLine = true,
                    label = { Text(tr("Nom de la plage", "Slot name")) },
                    supportingText = { Text(tr("40 caractères maximum", "Up to 40 characters")) },
                    modifier = Modifier.fillMaxWidth())
                Text(tr("Application suivie", "Tracked app"), color = MareePalette.Ink,
                    fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Box(Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = { choosingApp = true }, enabled = canModify && apps.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth()) {
                        Text(if (validApp) getName(packageName) else tr("Choisir une application", "Choose an app"))
                    }
                    DropdownMenu(expanded = choosingApp, onDismissRequest = { choosingApp = false },
                        modifier = Modifier.heightIn(max = 300.dp)) {
                        apps.forEach { app ->
                            DropdownMenuItem(text = { Text(getName(app.packageName)) }, onClick = {
                                packageName = app.packageName; choosingApp = false; error = null
                            })
                        }
                    }
                }
                if (!validApp) Text(tr("Sélectionne une application actuellement suivie.", "Select an app that is currently tracked."),
                    color = MareePalette.Muted, fontSize = 12.sp)
                Text(tr("Jours de début", "Start days"), color = MareePalette.Ink, fontWeight = FontWeight.Medium,
                    fontSize = 14.sp)
                (1..7).forEach { day ->
                    val selected = daysMask and (1 shl (day - 1)) != 0
                    Row(Modifier.fillMaxWidth().toggleable(value = selected, enabled = canModify,
                        role = Role.Checkbox, onValueChange = { checked ->
                            daysMask = if (checked) daysMask or (1 shl (day - 1))
                            else daysMask and (1 shl (day - 1)).inv()
                            error = null
                        }).heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Checkbox(checked = selected, onCheckedChange = null, enabled = canModify)
                        Text(scheduleDayName(day), color = MareePalette.Ink, fontSize = 14.sp)
                    }
                }
                if (daysMask == 0) Text(tr("Choisis au moins un jour.", "Choose at least one day."),
                    color = MareePalette.Danger, fontSize = 12.sp)
                ScheduleTimeField(startText, { startText = it; error = null },
                    tr("Heure de début", "Start time"), start == null, canModify)
                ScheduleTimeField(endText, { endText = it; error = null },
                    tr("Heure de fin", "End time"), end == null, canModify)
                if (start != null && end != null) {
                    if (start == end) Text(tr("Le début et la fin doivent être différents.", "Start and end must be different."),
                        color = MareePalette.Danger, fontSize = 12.sp)
                    else if (end < start) Text(tr("La plage se termine le lendemain du jour sélectionné.",
                        "The slot ends on the day after each selected day."), color = MareePalette.Muted, fontSize = 12.sp)
                }
                OutlinedTextField(value = limitText, onValueChange = {
                    limitText = it.filter { character -> character in '0'..'9' }.take(3); error = null
                }, label = { Text(tr("Limite en minutes", "Limit in minutes")) },
                    supportingText = { Text(tr("De 5 à 240 min pour le compteur existant", "5 to 240 minutes for the existing counter")) },
                    isError = limit == null || limit !in 5..240, enabled = canModify, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                Row(Modifier.fillMaxWidth().toggleable(value = enabled, enabled = canModify, role = Role.Switch,
                    onValueChange = { enabled = it; error = null }).heightIn(min = 48.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(tr("Activer cette plage", "Enable this slot"), modifier = Modifier.weight(1f), color = MareePalette.Ink)
                    Switch(checked = enabled, onCheckedChange = null, enabled = canModify)
                }
                Text(tr("Heure locale du téléphone. Le temps déjà utilisé et les verrouillages ne sont jamais remis à zéro par une plage.",
                    "Uses your phone’s local time. A slot never resets usage already counted or existing locks."),
                    color = MareePalette.Muted, fontSize = 12.sp, lineHeight = 18.sp)
                if (!canModify) Text(tr("Il faut Premium ou Ultra et un solde supérieur à 0 point pour enregistrer.",
                    "Saving requires Premium or Ultra and more than 0 points."), color = MareePalette.Danger, fontSize = 13.sp)
                if (error != null) Text(error!!, color = MareePalette.Danger, fontSize = 13.sp)
            }
        },
        confirmButton = {
            TextButton(enabled = ready, onClick = {
                if (ready && start != null && end != null && limit != null) {
                    error = onSave(LimitSchedule(id, name.trim(), packageName,
                        (1..7).filter { daysMask and (1 shl (it - 1)) != 0 }.toSet(), start, end, limit, enabled))
                }
            }) { Text(tr("Enregistrer", "Save")) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(tr("Annuler", "Cancel")) } }
    )
}

@Composable
private fun ScheduleTimeField(value: String, onChange: (String) -> Unit, label: String,
    invalid: Boolean, editable: Boolean) {
    OutlinedTextField(value = value, onValueChange = { input ->
        onChange(input.filter { it in '0'..'9' || it == ':' }.take(5))
    }, label = { Text(label) }, supportingText = { Text(tr("Format HH:mm, de 00:00 à 23:59", "HH:mm format, from 00:00 to 23:59")) },
        placeholder = { Text("HH:mm") }, singleLine = true, isError = invalid, enabled = editable,
        trailingIcon = {
            TextButton(enabled = editable && value.isNotEmpty(), onClick = { onChange("") },
                modifier = Modifier.semantics { contentDescription = tr("Effacer : $label", "Clear: $label") }) {
                Text(tr("Effacer", "Clear"), fontSize = 12.sp)
            }
        }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text), modifier = Modifier.fillMaxWidth())
}

private fun parseScheduleTime(value: String): Int? {
    if (!value.matches(Regex("[0-9]{2}:[0-9]{2}"))) return null
    val hour = value.take(2).toInt()
    val minute = value.takeLast(2).toInt()
    return if (hour in 0..23 && minute in 0..59) hour * 60 + minute else null
}

private fun scheduleTime(minute: Int): String = String.format(Locale.ROOT, "%02d:%02d", minute / 60, minute % 60)

private fun scheduleDayName(day: Int): String = when (day) {
    1 -> tr("Lundi", "Monday")
    2 -> tr("Mardi", "Tuesday")
    3 -> tr("Mercredi", "Wednesday")
    4 -> tr("Jeudi", "Thursday")
    5 -> tr("Vendredi", "Friday")
    6 -> tr("Samedi", "Saturday")
    else -> tr("Dimanche", "Sunday")
}
