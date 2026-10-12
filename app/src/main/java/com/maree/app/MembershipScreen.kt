package com.maree.app

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal fun planName(plan: PlanType): String = plan.displayName

@Composable
internal fun MembershipSection(plan: PlanType, onDemoPlan: (PlanType) -> Unit) {
    var period by rememberSaveable { mutableStateOf(BillingPeriod.MONTHLY) }
    val context = LocalContext.current
    var playUnavailable by rememberSaveable { mutableStateOf(false) }
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

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BillingPeriod.entries.forEach { choice ->
                OutlinedButton(onClick = { period = choice }, modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)) {
                    Text(if (period == choice) "✓ ${choice.label}" else choice.label)
                }
            }
        }
        Text(if (period == BillingPeriod.ANNUAL)
            "Une année payée en une fois, avec l’équivalent d’un mois offert. Même contenu que l’offre mensuelle."
            else "Un paiement chaque mois. Le choix annuel offre une petite réduction.",
            color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)

        PlanType.entries.forEach { option ->
            MembershipPlanCard(option, option == plan, period, onDemoPlan)
        }

        AppCardSurface {
            SectionLabel("Renouvellement et résiliation")
            Text("Pour les futurs achats Google Play, la formule mensuelle se renouvellera chaque mois, et la formule annuelle chaque année, sauf annulation. Les conditions et la date de renouvellement seront affichées avant l’achat.",
                color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)
            Text("Pour annuler : Google Play → photo de profil → Paiements et abonnements → Abonnements → Selkie → Annuler l’abonnement. L’accès continue normalement jusqu’à la fin de la période déjà payée. Désinstaller Selkie n’annule pas un abonnement.",
                color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)
            Text("Aucun abonnement n’est facturé par cet APK de test.",
                color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)
            TextButton(onClick = {
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/account/subscriptions")))
                    playUnavailable = false
                } catch (_: ActivityNotFoundException) { playUnavailable = true }
            }) { Text("Gérer mes abonnements Google Play") }
            if (playUnavailable) Text("Ouvre Google Play puis Paiements et abonnements → Abonnements.",
                color = MareePalette.Muted, fontSize = 13.sp)
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
private fun MembershipPlanCard(option: PlanType, selected: Boolean, period: BillingPeriod,
    onDemoPlan: (PlanType) -> Unit) {
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
                Text("${option.priceEuros(period)} €",
                    color = MareePalette.DeepTeal, fontWeight = FontWeight.Bold, fontSize = 28.sp)
                Text(if (option == PlanType.FREE) "toujours gratuit" else "${period.priceSuffix} · prévu",
                    modifier = Modifier.weight(1f),
                    color = MareePalette.Muted, fontSize = 13.sp)
            }
            if (option != PlanType.FREE && period == BillingPeriod.ANNUAL) {
                Text("${option.annualSavingEuros} € économisés par rapport à 12 mensualités.",
                    color = MareePalette.DeepTeal, fontSize = 13.sp, lineHeight = 20.sp)
            }
            HorizontalDivider(color = MareePalette.Outline)

            OfferFeature(
                option.maxTrackedApps?.let { "$it applications suivies" } ?: "Applications sans plafond",
                if (option.maxTrackedApps == null) "Toutes les applications compatibles de ton téléphone." else null
            )
            OfferFeature("Défis quotidiens inclus", "Des défis sur plusieurs jours pour retrouver du temps.")
            OfferFeature("Coach et aide Selkie inclus", "Conseils et réponses sur l’application, sans compte ni frais par message.")
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
