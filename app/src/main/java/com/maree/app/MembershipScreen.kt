package com.maree.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal fun planName(plan: PlanType): String = plan.displayName

@Composable
internal fun MembershipSection(plan: PlanType, onDemoPlan: (PlanType) -> Unit) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Ton offre, à ton rythme", fontWeight = FontWeight.Bold, fontSize = 23.sp)
        Text("Abonnements en préparation. Les prix ci-dessous sont prévus pour leur lancement ; aucun paiement n’est disponible dans cette version.",
            color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp)
        if (BuildConfig.DEBUG) {
            Text("MODE DÉMONSTRATION GRATUIT · ${planName(plan)} sélectionné",
                color = MareePalette.Teal, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            Text("Essaie les limites et les défis personnels sans payer. Changer d’offre conserve tes données et tes points. Ce choix de test ne constitue pas un abonnement.",
                color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)
        }
        PlanType.entries.forEach { option ->
            val selected = option == plan
            val background = when (option) {
                PlanType.FREE -> MareePalette.Subtle
                PlanType.PREMIUM -> MareePalette.Surface
                PlanType.ULTRA -> MareePalette.Surface
            }
            val accent = when (option) {
                PlanType.FREE -> MareePalette.Teal
                PlanType.PREMIUM -> MareePalette.Lavender
                PlanType.ULTRA -> MareePalette.Amber
            }
            Card(
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = background),
                border = if (selected) BorderStroke(1.dp, accent) else null
            ) {
                Column(Modifier.fillMaxWidth().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Milo ${planName(option)}", color = accent,
                        fontWeight = FontWeight.Bold, fontSize = 21.sp)
                    Text(if (option == PlanType.FREE) "Gratuit" else "${option.monthlyPriceEuros} € / mois · à venir",
                        color = accent, fontWeight = FontWeight.Bold, fontSize = 23.sp)
                    Text(option.maxTrackedApps?.let { "Jusqu’à $it applications suivies" }
                        ?: "Applications illimitées · toutes celles compatibles sur ton téléphone",
                        fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Text("Défis Milo et suivi local du temps d’écran", color = MareePalette.Muted,
                        fontSize = 14.sp, lineHeight = 21.sp)
                    if (option.canCreateCustomChallenges) {
                        Text("Défis personnels : 3 étapes par jour, durée choisie dès 2 jours, +0,25 point à la fin",
                            color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp)
                    }
                    if (option == PlanType.ULTRA) {
                        Text("Recherche et tri des applications par temps passé ou proximité de la limite",
                            color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp)
                        Text("Coach IA par messages illimité · prévu au lancement, pas encore disponible",
                            color = accent, fontSize = 14.sp, lineHeight = 21.sp)
                    }
                    if (BuildConfig.DEBUG) {
                        if (selected) {
                            Text("Offre sélectionnée pour la démonstration", color = accent,
                                fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        } else if (option == PlanType.FREE) {
                            OutlinedButton(onClick = { onDemoPlan(option) }) { Text("Revenir à Gratuit") }
                        } else {
                            Button(onClick = { onDemoPlan(option) }) { Text("Tester ${planName(option)} gratuitement") }
                        }
                    } else if (option != PlanType.FREE) {
                        Text("Disponible prochainement", color = accent, fontSize = 13.sp)
                    }
                }
            }
        }
        Text("À 0 point, les ajouts et les modifications de limites restent bloqués dans toutes les offres. Un défi peut t’aider à retrouver des points. Une réinitialisation nécessite 10 points.",
            color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)
    }
}
