package com.maree.app

import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import kotlinx.coroutines.delay

/** Inserted in Daily's existing scroll container; enrollment and check-ins remain local. */
@Composable
internal fun GuidedProgramsSection(
    plan: PlanType,
    onOffers: () -> Unit,
    onValidated: (String) -> Unit
) {
    val context = LocalContext.current
    val store = remember(context) { GuidedProgramStore(context) }
    val prefs = remember(context) { context.applicationContext.getSharedPreferences("maree", 0) }
    var revision by remember { mutableIntStateOf(0) }
    var today by remember { mutableStateOf(LocalDate.now()) }
    var issue by remember { mutableStateOf<String?>(null) }
    var notice by remember { mutableStateOf<String?>(null) }
    var resetId by rememberSaveable { mutableStateOf<String?>(null) }
    var discardId by rememberSaveable { mutableStateOf<String?>(null) }
    val entries = remember(store, revision, today, plan) { store.entries(today) }
    val ultra = plan == PlanType.ULTRA

    // Refresh after global erasure, a plan change, or a second store instance's write.
    DisposableEffect(prefs) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == null || key == GuidedProgramStore.KEY || key == "plan") revision++
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }
    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000)
            today = LocalDate.now()
        }
    }

    fun report(result: GuidedProgramResult, success: String) {
        today = LocalDate.now()
        revision++
        issue = if (result == GuidedProgramResult.SUCCESS) null else result.message()
        notice = if (result == GuidedProgramResult.SUCCESS) success else null
    }

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        AppCardSurface {
            SectionLabel(tr("Programmes guidés", "Guided programs"), "Ultra")
            Text(tr("51 activités préparées, disponibles hors ligne en français et en anglais.",
                "51 prepared activities, available offline in French and English."),
                color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp)
            Text(tr("Une activité à la fois, une validation guidée au maximum par jour. Les journées peuvent être espacées : 7, 14 ou 30 journées de pratique peuvent prendre plus longtemps. Aucun rattrapage de dates passées.",
                "One activity at a time, at most one guided check-in per day. Days can be spaced out: 7, 14 or 30 practice days can take longer. Past dates cannot be filled in."),
                color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)
            Text(tr("Pas de points supplémentaires. Coche toi-même l'activité accomplie aujourd'hui : aucune validation automatique, aucune photo demandée. Adapte les activités à tes contraintes.",
                "No extra points. Tick the activity you completed today yourself: no automatic check-ins and no photo required. Adapt activities to your circumstances."),
                color = MareePalette.Ink, fontSize = 13.sp, lineHeight = 20.sp)
            if (!ultra) {
                Text(tr("L'aperçu est ouvert à tous. Ultra permet de démarrer, reprendre et valider. Ta progression est conservée si tu changes d'offre et redevient accessible avec Ultra.",
                    "Everyone can preview the activities. Ultra lets you start, resume and check in. Progress is kept when you change plans and becomes available again with Ultra."),
                    color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)
                OutlinedButton(onClick = onOffers, modifier = Modifier.fillMaxWidth()) {
                    Text(tr("Découvrir Ultra", "Explore Ultra"))
                }
            }
        }
        issue?.let { Text(it, color = MareePalette.Danger, fontSize = 14.sp) }
        notice?.let { Text(it, color = MareePalette.Teal, fontSize = 14.sp) }
        entries.forEach { entry ->
            GuidedProgramCard(entry, ultra, today,
                onOffers = onOffers,
                onStart = {
                    report(store.start(entry.program.id), tr("Programme choisi. Les progrès des autres programmes sont conservés.",
                        "Program selected. Your progress in other programs is kept."))
                },
                onValidate = { checked ->
                    val result = store.validateToday(entry.program.id, checked, today)
                    report(result, tr("Activité validée pour aujourd'hui. La suivante sera disponible un autre jour.",
                        "Activity checked in for today. The next one will be available on another day."))
                    if (result == GuidedProgramResult.SUCCESS) onValidated(entry.program.id)
                    result == GuidedProgramResult.SUCCESS
                },
                onReset = { resetId = entry.program.id },
                onDiscard = { discardId = entry.program.id })
        }
    }

    val resetProgram = entries.firstOrNull { it.program.id == resetId }?.program
    if (resetProgram != null) {
        AlertDialog(onDismissRequest = { resetId = null },
            title = { Text(tr("Recommencer ce programme ?", "Restart this program?")) },
            text = { Text(tr("Les journées validées de « ${resetProgram.title.localized} » seront effacées. Les autres programmes restent conservés. Si tu as déjà validé aujourd'hui, tu devras attendre un autre jour.",
                "The completed days in '${resetProgram.title.localized}' will be erased. Other programs are kept. If you have already checked in today, you must wait for another day.")) },
            confirmButton = { TextButton(onClick = {
                report(store.reset(resetProgram.id), tr("Programme remis à zéro.", "Program reset."))
                resetId = null
            }) { Text(tr("Effacer et recommencer", "Erase and restart")) } },
            dismissButton = { TextButton(onClick = { resetId = null }) { Text(tr("Annuler", "Cancel")) } })
    }
    val discardedProgram = entries.firstOrNull { it.program.id == discardId }?.program
    if (discardedProgram != null) {
        AlertDialog(onDismissRequest = { discardId = null },
            title = { Text(tr("Abandonner et effacer ce programme ?", "Discard this program and its progress?")) },
            text = { Text(tr("L'inscription et les dates de « ${discardedProgram.title.localized} » seront supprimées. Tu pourras le démarrer à nouveau avec Ultra. La limite d'une validation guidée par jour reste en place.",
                "Enrollment and dates for '${discardedProgram.title.localized}' will be deleted. You can start it again with Ultra. The one-guided-check-in-per-day limit remains.")) },
            confirmButton = { TextButton(onClick = {
                report(store.discard(discardedProgram.id), tr("Programme et progression supprimés.", "Program and progress deleted."))
                discardId = null
            }) { Text(tr("Abandonner et effacer", "Discard and erase")) } },
            dismissButton = { TextButton(onClick = { discardId = null }) { Text(tr("Annuler", "Cancel")) } })
    }
}

@Composable
private fun GuidedProgramCard(
    entry: GuidedProgramEntry,
    ultra: Boolean,
    today: LocalDate,
    onOffers: () -> Unit,
    onStart: () -> Unit,
    onValidate: (Boolean) -> Boolean,
    onReset: () -> Unit,
    onDiscard: () -> Unit
) {
    val program = entry.program
    val progress = entry.progress
    var expanded by rememberSaveable(program.id) { mutableStateOf(false) }
    // A conscious check is intentionally not stored or restored across days/activities.
    var checked by remember(program.id, progress?.currentDay, progress?.active, ultra, today) { mutableStateOf(false) }
    val currentDay = if (progress?.active == true) progress.currentDay else null
    AppCardSurface {
        Text(program.title.localized, color = MareePalette.Ink,
            fontWeight = FontWeight.SemiBold, fontSize = 19.sp, lineHeight = 25.sp,
            modifier = Modifier.semantics { heading() })
        Text(program.description.localized, color = MareePalette.Muted,
            fontSize = 14.sp, lineHeight = 21.sp)
        if (progress != null) {
            StatusPill(tr("${progress.completedDays} / ${program.requiredDays} journées validées",
                "${progress.completedDays} / ${program.requiredDays} days completed"))
            Text(when {
                !ultra -> tr("Progression conservée · accès Ultra requis", "Progress kept · Ultra access required")
                progress.completed -> tr("Programme terminé", "Program completed")
                progress.active -> tr("Programme choisi", "Selected program")
                else -> tr("En pause · progression conservée", "Paused · progress kept")
            }, color = MareePalette.Muted, fontSize = 13.sp)
        }
        if (ultra && progress?.active != true && progress?.completed != true) {
            PrimaryAction(if (progress == null) tr("Démarrer ce programme", "Start this program")
                else tr("Reprendre ce programme", "Resume this program"),
                onClick = onStart, modifier = Modifier.fillMaxWidth())
            Text(tr("Choisir ce programme met l'autre en pause sans effacer ses progrès.",
                "Selecting this program pauses the other one and keeps its progress."),
                color = MareePalette.Muted, fontSize = 12.sp, lineHeight = 18.sp)
        }
        if (!ultra) {
            OutlinedButton(onClick = onOffers, modifier = Modifier.fillMaxWidth()) {
                Text(tr("Démarrer ou reprendre avec Ultra", "Start or resume with Ultra"))
            }
        }
        if (currentDay != null && progress != null) {
            val activity = program.days[currentDay - 1]
            HorizontalDivider(color = MareePalette.Outline)
            Text(tr("Journée $currentDay · ${activity.title.localized}", "Day $currentDay · ${activity.title.localized}"),
                color = MareePalette.Ink, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 23.sp)
            Text(activity.activity.localized, color = MareePalette.Ink, fontSize = 14.sp, lineHeight = 22.sp)
            when {
                progress.dateRollback -> Text(tr("Une date enregistrée est après aujourd'hui. Vérifie la date du téléphone ; la progression reste conservée.",
                    "A saved date is later than today. Check your phone's date; your progress is kept."),
                    color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)
                progress.hasValidationToday -> Text(tr("Tu as déjà validé une activité guidée aujourd'hui. Cette activité sera à faire et à valider un autre jour.",
                    "You already checked in a guided activity today. Do and check in this activity on another day."),
                    color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)
            }
            Row(Modifier.fillMaxWidth().toggleable(value = checked,
                enabled = ultra && progress.canValidateToday,
                role = Role.Checkbox, onValueChange = { checked = it }).padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Checkbox(checked = checked, onCheckedChange = null,
                    enabled = ultra && progress.canValidateToday)
                Text(tr("J'ai réalisé cette activité aujourd'hui", "I completed this activity today"),
                    color = MareePalette.Ink, fontSize = 14.sp, lineHeight = 21.sp,
                    modifier = Modifier.weight(1f))
            }
            PrimaryAction(tr("Valider la journée $currentDay", "Check in day $currentDay"),
                onClick = { if (onValidate(checked)) checked = false },
                enabled = ultra && progress.canValidateToday && checked,
                modifier = Modifier.fillMaxWidth())
        }
        TextButton(onClick = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()) {
            Text(if (expanded) tr("Masquer les journées", "Hide days")
                else tr("Voir les ${program.requiredDays} activités dans l'ordre", "See all ${program.requiredDays} activities in order"))
        }
        if (expanded) {
            program.days.forEachIndexed { index, day ->
                HorizontalDivider(color = MareePalette.Outline)
                Column(Modifier.fillMaxWidth().semantics(mergeDescendants = true) {},
                    verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(tr("Jour ${index + 1} · ${day.title.localized}", "Day ${index + 1} · ${day.title.localized}"),
                        color = MareePalette.Ink, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 21.sp)
                    Text(when {
                        index < (progress?.completedDays ?: 0) -> tr("Validé le ${progress!!.validatedDates[index]}",
                            "Completed on ${progress!!.validatedDates[index]}")
                        index + 1 == currentDay -> tr("Activité actuelle", "Current activity")
                        else -> tr("À venir", "Upcoming")
                    }, color = MareePalette.Teal, fontSize = 12.sp)
                    Text(day.activity.localized, color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)
                }
            }
        }
        if (progress != null) {
            if (ultra) TextButton(onClick = onReset, modifier = Modifier.fillMaxWidth()) {
                Text(tr("Recommencer ce programme", "Restart this program"))
            }
            TextButton(onClick = onDiscard, modifier = Modifier.fillMaxWidth()) {
                Text(tr("Abandonner et effacer la progression", "Discard and erase progress"), color = MareePalette.Danger)
            }
        }
    }
}

private fun GuidedProgramResult.message(): String = when (this) {
    GuidedProgramResult.SUCCESS -> ""
    GuidedProgramResult.ULTRA_REQUIRED -> tr("Ultra est nécessaire pour cette action. Ta progression est conservée.", "Ultra is required for this action. Your progress is kept.")
    GuidedProgramResult.UNKNOWN_PROGRAM -> tr("Ce programme n'est pas disponible.", "This program is unavailable.")
    GuidedProgramResult.NOT_ACTIVE -> tr("Choisis ce programme avant de valider.", "Select this program before checking in.")
    GuidedProgramResult.ALREADY_VALIDATED_TODAY -> tr("Une activité guidée a déjà été validée aujourd'hui.", "A guided activity has already been checked in today.")
    GuidedProgramResult.DATE_ROLLBACK -> tr("Vérifie la date du téléphone : une date enregistrée est dans le futur.", "Check your phone's date: a saved date is in the future.")
    GuidedProgramResult.DAY_CHANGED -> tr("La date a changé. Réalise l'activité du jour, puis coche-la de nouveau.", "The date has changed. Complete today's activity, then tick it again.")
    GuidedProgramResult.ACTIVITY_NOT_CHECKED -> tr("Coche l'activité seulement après l'avoir réalisée aujourd'hui.", "Tick the activity only after completing it today.")
    GuidedProgramResult.COMPLETED -> tr("Ce programme est terminé. Tu peux choisir de le recommencer.", "This program is complete. You can choose to restart it.")
    GuidedProgramResult.STORAGE_ERROR -> tr("La progression n'a pas pu être enregistrée. Réessaie.", "Progress could not be saved. Try again.")
}
