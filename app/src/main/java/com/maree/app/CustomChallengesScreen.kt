package com.maree.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.graphics.Color
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

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Tes défis personnels", color = MareePalette.Ink,
            fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 30.sp)
        PersonalCard(MareePalette.Lilac) {
            Text("Une habitude qui te ressemble", fontWeight = FontWeight.Bold,
                color = MareePalette.Lavender, fontSize = 19.sp)
            Text(
                "Choisis ton objectif, exactement 3 étapes et une durée de $CUSTOM_MIN_DAYS à $CUSTOM_MAX_DAYS jours. Accomplis les 3 étapes, puis valide ta journée.",
                color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp
            )
            Text("+0,25 point à la fin du défi, une seule fois.",
                color = MareePalette.Teal, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            TextButton(onClick = { showRules = !showRules }) {
                Text(if (showRules) "Masquer les détails" else "Comment ça marche ?")
            }
            if (showRules) {
                Text("Les jours peuvent être espacés. Les cases se renouvellent chaque jour. Tes défis personnels restent conservés quand la série Milo change.",
                    color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)
                Text("Ils ne participent pas au bonus de +5 points des défis proposés par Milo.",
                    color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)
            }
            if (canCreate) {
                Text("$activeCount / $MAX_ACTIVE_CUSTOM_CHALLENGES défis personnels en cours", color = MareePalette.Muted, fontSize = 12.sp)
                Button(onClick = { creating = true; issue = null }, enabled = activeCount < MAX_ACTIVE_CUSTOM_CHALLENGES) {
                    Text("Créer mon défi")
                }
                if (activeCount >= MAX_ACTIVE_CUSTOM_CHALLENGES) {
                    Text("Termine ou supprime un défi en cours pour en créer un nouveau.",
                        color = MareePalette.Muted, fontSize = 12.sp)
                }
            } else {
                Text("La création et la validation des défis personnels sont disponibles avec Premium et Ultra. Tes défis proposés par Milo restent accessibles à tous.",
                    color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)
                OutlinedButton(onClick = onOffers) { Text("Découvrir les offres") }
            }
        }

        if (issue != null) {
            Text(issue!!, color = MareePalette.Danger, fontSize = 14.sp)
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
    PersonalCard(if (challenge.completed) MareePalette.Subtle else MareePalette.Surface) {
        Text(challenge.title, fontWeight = FontWeight.Bold, fontSize = 19.sp,
            lineHeight = 26.sp, color = MareePalette.Ink)
        Text("${challenge.completedDays} / ${challenge.requiredDays} jours validés",
            color = MareePalette.Teal, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        LinearProgressIndicator(
            progress = { (challenge.completedDays / challenge.requiredDays.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
            color = MareePalette.Teal,
            trackColor = MareePalette.Lilac
        )
        ChallengeDays(challenge.requiredDays, challenge.completedDays, challenge.validatedToday)
        if (!challenge.completed) {
            Text("Tes 3 étapes d’aujourd’hui",
                color = MareePalette.Muted, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
        challenge.steps.forEachIndexed { index, step ->
            val checked = challenge.completed || index in challenge.todayCheckedSteps
            Row(
                Modifier.fillMaxWidth().toggleable(
                    value = checked,
                    enabled = editable,
                    role = Role.Checkbox,
                    onValueChange = { onToggleStep(index, it) }
                ).padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(checked = checked, onCheckedChange = null, enabled = editable)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("Étape ${index + 1}", color = MareePalette.Teal,
                        fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                    Text(step, color = MareePalette.Ink, fontSize = 15.sp, lineHeight = 22.sp)
                }
            }
        }
        when {
            challenge.completed -> {
                Text("Défi terminé", color = MareePalette.Teal,
                    fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(if (challenge.rewardClaimed) "Récompense de 0,25 point déjà accordée." else "La récompense de ce défi ne peut être obtenue qu’une fois.",
                    color = MareePalette.Muted, fontSize = 13.sp)
            }
            challenge.validatedToday -> Text("Validé aujourd’hui · reviens demain", color = MareePalette.Lavender,
                fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 21.sp)
            !planAllowsValidation -> {
                Text("La validation de ce défi personnel nécessite Premium ou Ultra.",
                    color = MareePalette.Muted, fontSize = 13.sp)
                TextButton(onClick = onOffers) { Text("Voir les offres") }
            }
            else -> {
                Button(onClick = onValidate, enabled = challenge.canValidateToday,
                    modifier = Modifier.fillMaxWidth()) {
                    Text("Valider aujourd’hui")
                }
                Text(
                    if (!challenge.canEditToday) "La date du téléphone précède une validation déjà enregistrée. Vérifie les réglages de date pour continuer."
                    else if (challenge.todayCheckedSteps.size < 3) "Accomplis et coche les 3 étapes avant de valider."
                    else "Ta journée est prête à être validée.",
                    color = MareePalette.Muted, fontSize = 12.sp, lineHeight = 18.sp
                )
            }
        }
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

@Composable
private fun PersonalCard(
    color: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color, contentColor = MareePalette.Ink)) {
        Column(Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
    }
}
