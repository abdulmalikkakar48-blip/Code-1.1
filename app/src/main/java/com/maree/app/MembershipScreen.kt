package com.maree.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal fun planName(plan: PlanType): String = plan.displayName

@Composable
internal fun MembershipSection(plan: PlanType, onDemoPlan: (PlanType) -> Unit) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ScreenHeading(
            eyebrow = "LES OFFRES",
            title = "À ton rythme.",
            description = "Commence gratuitement. Choisis plus de liberté quand tu en as besoin."
        )

        AppCardSurface {
            SectionLabel("Ton offre actuelle", if (BuildConfig.DEBUG) "Démonstration" else null)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text(planName(plan), modifier = Modifier.weight(1f), color = MareePalette.Ink,
                    fontSize = 22.sp, fontWeight = FontWeight.Bold)
                StatusPill(plan.maxTrackedApps?.let { "$it applications" } ?: "Sans plafond")
            }
            Text(
                if (BuildConfig.DEBUG) "Les changements d’offre sont gratuits dans cette version de test. Tes points et ton suivi sont conservés."
                else "Les abonnements ne sont pas encore disponibles.",
                color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp
            )
        }

        Text("Premium et Ultra sont en préparation. Leurs prix sont prévus pour le lancement ; aucun paiement n’est possible ici.",
            color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)

        PlanType.entries.forEach { option ->
            MembershipPlanCard(option, option == plan, onDemoPlan)
        }

        AppCardSurface {
            Text("Les mêmes règles pour tout le monde", color = MareePalette.Ink,
                fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text("À 0 point, ajouter une application ou modifier une limite reste bloqué, quelle que soit l’offre. Réinitialiser une limite demande 10 points.",
                color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)
        }
    }
}

@Composable
private fun MembershipPlanCard(option: PlanType, selected: Boolean, onDemoPlan: (PlanType) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MareePalette.Surface),
        border = BorderStroke(1.dp, if (selected) MareePalette.Teal else MareePalette.Outline)
    ) {
        Column(Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text(planName(option), modifier = Modifier.weight(1f), color = MareePalette.Ink,
                    fontWeight = FontWeight.Bold, fontSize = 21.sp)
                if (selected) StatusPill("Sélectionnée")
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Bottom) {
                Text(if (option == PlanType.FREE) "0 €" else "${option.monthlyPriceEuros} €",
                    color = MareePalette.DeepTeal, fontWeight = FontWeight.Bold, fontSize = 28.sp)
                Text(if (option == PlanType.FREE) "toujours gratuit" else "/ mois · prévu",
                    modifier = Modifier.weight(1f),
                    color = MareePalette.Muted, fontSize = 13.sp)
            }
            HorizontalDivider(color = MareePalette.Outline)

            OfferFeature(
                option.maxTrackedApps?.let { "$it applications suivies" } ?: "Applications sans plafond",
                if (option.maxTrackedApps == null) "Toutes les applications compatibles de ton téléphone." else null
            )
            OfferFeature("Défis quotidiens inclus", "Des défis sur plusieurs jours pour retrouver du temps.")
            OfferFeature("Coach hors ligne inclus", "40 sujets de conseils préparés, sans compte ni frais par message.")
            if (option.canCreateCustomChallenges) {
                OfferFeature("Tes propres défis",
                    "3 étapes chaque jour · durée de 2 à 365 jours · +0,25 point une fois le défi terminé.")
            }
            if (option == PlanType.ULTRA) {
                OfferFeature("Recherche et tri du suivi", "Par nom, temps passé ou proximité de la limite.")
                OfferFeature("Dialogue IA en ligne illimité", "Prévu pour Ultra · service à activer séparément.")
            }

            when {
                BuildConfig.DEBUG && !selected -> PrimaryAction(
                    label = if (option == PlanType.FREE) "Choisir Gratuit" else "Essayer ${planName(option)} gratuitement",
                    onClick = { onDemoPlan(option) },
                    modifier = Modifier.fillMaxWidth()
                )
                !BuildConfig.DEBUG && option != PlanType.FREE -> PrimaryAction(
                    label = "Bientôt disponible",
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                )
            }
        }
    }
}

@Composable
private fun OfferFeature(title: String, detail: String? = null) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("✓", color = MareePalette.Teal, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, color = MareePalette.Ink, fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp, lineHeight = 20.sp)
            if (detail != null) Text(detail, color = MareePalette.Muted,
                fontSize = 13.sp, lineHeight = 19.sp)
        }
    }
}
