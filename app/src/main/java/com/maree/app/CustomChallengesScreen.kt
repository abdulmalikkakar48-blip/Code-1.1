package com.maree.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Section sans défilement propre : elle s'insère dans l'onglet Défis. */
@Composable
internal fun CustomChallengesSection(
    snapshot: MareeSnapshot,
    onOffers: () -> Unit,
    onCreate: (String, List<String>, Int) -> String?,
    onToggleStep: (String, Int, Boolean) -> String?,
    onValidateToday: (String) -> String?,
    onDelete: (String) -> String?
) {
    var creating by rememberSaveable { mutableStateOf(false) }
    var showRules by rememberSaveable { mutableStateOf(false) }
    var deletingId by rememberSaveable { mutableStateOf<String?>(null) }
    var issue by rememberSaveable { mutableStateOf<String?>(null) }
    val canCreate = snapshot.plan.canCreateCustomChallenges
    val pendingDelete = snapshot.customChallenges.firstOrNull { it.id == deletingId }
    val activeCount = snapshot.customChallenges.count { !it.completed }

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        AppCardSurface {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Ton objectif, ton rythme", modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.SemiBold, color = MareePalette.Ink,
                    fontSize = 19.sp, lineHeight = 25.sp)
                StatusPill("+0,25 pt")
            }
            Text(
                "3 étapes par jour · de $CUSTOM_MIN_DAYS à $CUSTOM_MAX_DAYS journées",
                color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp
            )
            if (canCreate) {
                PrimaryAction("Créer un défi", onClick = { creating = true; issue = null },
                    enabled = activeCount < MAX_ACTIVE_CUSTOM_CHALLENGES,
                    modifier = Modifier.fillMaxWidth())
                if (activeCount >= MAX_ACTIVE_CUSTOM_CHALLENGES) {
                    Text("Termine ou supprime un défi en cours pour en créer un nouveau.",
                        color = MareePalette.Muted, fontSize = 12.sp)
                }
            } else {
                Text("Crée et valide tes propres défis avec Premium ou Ultra.",
                    color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)
                OutlinedButton(onClick = onOffers, modifier = Modifier.fillMaxWidth()) {
                    Text("Découvrir les offres")
                }
            }
            TextButton(onClick = { showRules = !showRules }) {
                Text(if (showRules) "Masquer les règles" else "Comment ça marche ?", fontSize = 13.sp)
            }
            if (showRules) {
                HorizontalDivider(color = MareePalette.Outline)
                Text("Choisis un objectif et exactement 3 étapes. Accomplis et coche ces 3 étapes avant de valider ta journée. Les cases se renouvellent chaque jour et les journées peuvent être espacées.",
                    color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)
                Text("Tu gagnes 0,25 point une seule fois, à la fin de la durée choisie. Les défis personnels sont conservés au changement de série et ne participent pas au bonus des défis proposés.",
                    color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)
            }
        }

        if (issue != null) {
            Text(issue!!, color = MareePalette.Danger, fontSize = 14.sp)
        }

        if (snapshot.customChallenges.isNotEmpty()) {
            SectionLabel("Tes défis personnels", "$activeCount en cours")
        }
        snapshot.customChallenges.forEach { challenge ->
            PersonalChallengeCard(
                challenge = challenge,
                planAllowsValidation = canCreate,
                onToggleStep = { index, checked -> issue = onToggleStep(challenge.id, index, checked) },
                onValidate = { issue = onValidateToday(challenge.id) },
                onDelete = { deletingId = challenge.id; issue = null },
                onOffers = onOffers
            )
        }
    }

    if (creating) {
        CreatePersonalChallengeDialog(
            onDismiss = { creating = false },
            onCreate = { title, steps, requiredDays ->
                val error = onCreate(title, steps, requiredDays)
                if (error == null) { creating = false; issue = null }
                error
            }
        )
    }

    if (pendingDelete != null) {
        AlertDialog(
            onDismissRequest = { deletingId = null },
            title = { Text("Supprimer ce défi ?") },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("« ${pendingDelete.title} » et sa progression seront retirés. Cette action ne peut pas être annulée.")
                    Text("Les points déjà obtenus sont conservés. Tu pourras créer un nouveau défi, qui commencera à zéro et nécessitera de nouvelles journées de pratique.",
                        color = MareePalette.Muted, fontSize = 13.sp)
                    if (issue != null) Text(issue!!, color = MareePalette.Danger)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val error = onDelete(pendingDelete.id)
                    issue = error
                    if (error == null) deletingId = null
                }) { Text("Supprimer", color = MareePalette.Danger) }
            },
            dismissButton = { TextButton(onClick = { deletingId = null }) { Text("Garder mon défi") } }
        )
    }
}

@Composable
private fun PersonalChallengeCard(
    challenge: CustomChallenge,
    planAllowsValidation: Boolean,
    onToggleStep: (Int, Boolean) -> Unit,
    onValidate: () -> Unit,
    onDelete: () -> Unit,
    onOffers: () -> Unit
) {
    val editable = planAllowsValidation && challenge.canEditToday
    AppCardSurface {
        Text(challenge.title, fontWeight = FontWeight.SemiBold, fontSize = 20.sp,
            lineHeight = 26.sp, color = MareePalette.Ink)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text("${challenge.completedDays} / ${challenge.requiredDays} journées",
                color = MareePalette.Muted, fontWeight = FontWeight.Medium, fontSize = 13.sp)
            StatusPill(if (challenge.completed) "Terminé" else "+0,25 pt à la fin")
        }
        LinearProgressIndicator(
            progress = { (challenge.completedDays / challenge.requiredDays.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(8.dp)),
            color = MareePalette.Teal,
            trackColor = MareePalette.Subtle
        )
        SectionLabel(if (challenge.completed) "Les 3 étapes" else "Aujourd’hui · 3 étapes")
        challenge.steps.forEachIndexed { index, step ->
            val checked = challenge.completed || index in challenge.todayCheckedSteps
            Row(
                Modifier.fillMaxWidth().toggleable(
                    value = checked,
                    enabled = editable,
                    role = Role.Checkbox,
                    onValueChange = { onToggleStep(index, it) }
                ).padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(checked = checked, onCheckedChange = null, enabled = editable)
                Text("${index + 1}.", color = MareePalette.Teal,
                    fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(step, modifier = Modifier.weight(1f), color = MareePalette.Ink,
                    fontSize = 15.sp, lineHeight = 22.sp)
            }
            if (index != challenge.steps.lastIndex) HorizontalDivider(color = MareePalette.Outline)
        }
        when {
            challenge.completed -> {
                Text(if (challenge.rewardClaimed) "Récompense de 0,25 point déjà accordée." else "La récompense de ce défi ne peut être obtenue qu’une fois.",
                    color = MareePalette.Muted, fontSize = 13.sp)
            }
            challenge.validatedToday -> StatusPill("Journée validée · à demain")
            !planAllowsValidation -> {
                Text("La validation de ce défi personnel nécessite Premium ou Ultra.",
                    color = MareePalette.Muted, fontSize = 13.sp)
                TextButton(onClick = onOffers) { Text("Voir les offres") }
            }
            else -> {
                PrimaryAction("Valider ma journée", onClick = onValidate,
                    enabled = challenge.canValidateToday, modifier = Modifier.fillMaxWidth())
                Text(
                    if (!challenge.canEditToday) "La date du téléphone précède une validation déjà enregistrée. Vérifie les réglages de date pour continuer."
                    else if (challenge.todayCheckedSteps.size < 3) "Accomplis et coche les 3 étapes avant de valider."
                    else "Ta journée est prête à être validée.",
                    color = MareePalette.Muted, fontSize = 12.sp, lineHeight = 18.sp
                )
            }
        }
        HorizontalDivider(color = MareePalette.Outline)
        SectionLabel("Tes journées", "${challenge.completedDays} validées")
        ChallengeDays(challenge.requiredDays, challenge.completedDays, challenge.validatedToday)
        TextButton(onClick = onDelete, modifier = Modifier.align(Alignment.End)) {
            Text("Supprimer ce défi", color = MareePalette.Muted, fontSize = 12.sp)
        }
    }
}

@Composable
private fun CreatePersonalChallengeDialog(
    onDismiss: () -> Unit,
    onCreate: (String, List<String>, Int) -> String?
) {
    var title by rememberSaveable { mutableStateOf("") }
    var firstStep by rememberSaveable { mutableStateOf("") }
    var secondStep by rememberSaveable { mutableStateOf("") }
    var thirdStep by rememberSaveable { mutableStateOf("") }
    var duration by rememberSaveable { mutableStateOf("7") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    val steps = listOf(firstStep, secondStep, thirdStep)
    val requiredDays = duration.toIntOrNull()
    val durationValid = requiredDays != null && requiredDays in CUSTOM_MIN_DAYS..CUSTOM_MAX_DAYS
    val ready = title.isNotBlank() && steps.all { it.isNotBlank() } && durationValid

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Créer mon défi") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Choisis 3 petites actions réalistes que tu peux répéter. Les 3 sont obligatoires.",
                    color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp)
                OutlinedTextField(value = title, onValueChange = { title = it; error = null },
                    label = { Text("Nom du défi") }, placeholder = { Text("Retrouver le plaisir de lire") },
                    modifier = Modifier.fillMaxWidth(), maxLines = 2)
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it.filter { digit -> digit in '0'..'9' }.take(3); error = null },
                    label = { Text("Durée en jours") },
                    supportingText = { Text("De $CUSTOM_MIN_DAYS à $CUSTOM_MAX_DAYS jours différents, qui peuvent être espacés.") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = duration.isNotEmpty() && !durationValid,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(value = firstStep, onValueChange = { firstStep = it; error = null },
                    label = { Text("Étape 1") }, placeholder = { Text("Poser mon téléphone loin de moi") },
                    modifier = Modifier.fillMaxWidth(), maxLines = 3)
                OutlinedTextField(value = secondStep, onValueChange = { secondStep = it; error = null },
                    label = { Text("Étape 2") }, placeholder = { Text("Lire 10 pages") },
                    modifier = Modifier.fillMaxWidth(), maxLines = 3)
                OutlinedTextField(value = thirdStep, onValueChange = { thirdStep = it; error = null },
                    label = { Text("Étape 3") }, placeholder = { Text("Noter une idée de ma lecture") },
                    modifier = Modifier.fillMaxWidth(), maxLines = 3)
                Text("Chaque journée demande les 3 étapes. +0,25 point une seule fois, après la durée choisie.",
                    color = MareePalette.Muted, fontSize = 12.sp, lineHeight = 18.sp)
                if (error != null) Text(error!!, color = MareePalette.Danger, fontSize = 13.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (requiredDays != null && durationValid) {
                    error = onCreate(title.trim(), steps.map { it.trim() }, requiredDays)
                }
            }, enabled = ready) {
                Text("Créer le défi")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}
