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
            eyebrow = tr("LES OFFRES", "PLANS"),
            title = tr("À ton rythme.", "At your own pace."),
            description = tr("Commence gratuitement. Choisis plus de liberté quand tu en as besoin.", "Start for free. Choose more flexibility when you need it.")
        )

        AppCardSurface {
            SectionLabel(tr("Ton offre actuelle", "Your current plan"), if (BuildConfig.DEBUG) tr("Démonstration", "Demo") else null)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text(planName(plan), modifier = Modifier.weight(1f), color = MareePalette.Ink,
                    fontSize = 22.sp, fontWeight = FontWeight.Bold)
                StatusPill(plan.maxTrackedApps?.let { tr("$it applications", "$it apps") } ?: tr("Sans plafond", "No app limit"))
            }
            Text(
                if (BuildConfig.DEBUG) tr("Les changements d’offre sont gratuits dans cette version de test. Tes points et ton suivi sont conservés.", "Switching plans is free in this test version. Your points and tracking are kept.")
                else tr("Les abonnements ne sont pas encore disponibles.", "Subscriptions are not available yet."),
                color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp
            )
        }

        Text(tr("Premium et Ultra sont en préparation. Leurs prix sont prévus pour le lancement ; aucun paiement n’est possible ici.", "Premium and Ultra are being prepared. These are the planned launch prices; you cannot make a payment here."),
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
            tr("Une année payée en une fois, au prix de douze mensualités. Même contenu que l’offre mensuelle.", "Pay for a year upfront at the price of twelve monthly payments. The features are the same as the monthly plan.")
            else tr("Un paiement chaque mois. Tu peux aussi choisir un paiement annuel.", "One payment each month. You can also choose annual payment."),
            color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)

        PlanType.entries.forEach { option ->
            MembershipPlanCard(option, option == plan, period, onDemoPlan)
        }

        AppCardSurface {
            SectionLabel(tr("Renouvellement et résiliation", "Renewal and cancellation"))
            Text(tr("Pour les futurs achats Google Play, la formule mensuelle se renouvellera chaque mois, et la formule annuelle chaque année, sauf annulation. Les conditions et la date de renouvellement seront affichées avant l’achat.", "For future Google Play purchases, monthly plans will renew each month and annual plans each year, unless cancelled. The terms and renewal date will be shown before purchase."),
                color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)
            Text(tr("Pour annuler : Google Play → photo de profil → Paiements et abonnements → Abonnements → Selkie → Annuler l’abonnement. L’accès continue normalement jusqu’à la fin de la période déjà payée. Désinstaller Selkie n’annule pas un abonnement.", "To cancel: Google Play → profile picture → Payments & subscriptions → Subscriptions → Selkie → Cancel subscription. Access normally continues until the end of the paid period. Uninstalling Selkie does not cancel a subscription."),
                color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)
            Text(tr("Aucun abonnement n’est facturé par cet APK de test.", "This test APK does not charge for any subscription."),
                color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)
            TextButton(onClick = {
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/account/subscriptions")))
                    playUnavailable = false
                } catch (_: ActivityNotFoundException) { playUnavailable = true }
            }) { Text(tr("Gérer mes abonnements Google Play", "Manage my Google Play subscriptions")) }
            if (playUnavailable) Text(tr("Ouvre Google Play puis Paiements et abonnements → Abonnements.", "Open Google Play, then Payments & subscriptions → Subscriptions."),
                color = MareePalette.Muted, fontSize = 13.sp)
        }

        AppCardSurface {
            Text(tr("Les mêmes règles pour tout le monde", "The same rules for everyone"), color = MareePalette.Ink,
                fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(tr("À 0 point, ajouter une application ou modifier une limite reste bloqué, quelle que soit l’offre. Réinitialiser une limite demande 10 points.", "At 0 points, adding an app or changing a limit stays blocked on every plan. Resetting a limit requires 10 points."),
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
                if (selected) StatusPill(tr("Sélectionnée", "Selected"))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Bottom) {
                Text(tr("${option.priceEuros(period)} €", "€${option.priceEuros(period)}"),
                    color = MareePalette.DeepTeal, fontWeight = FontWeight.Bold, fontSize = 28.sp)
                Text(if (option == PlanType.FREE) tr("toujours gratuit", "always free") else tr("${period.priceSuffix} · prévu", "${period.priceSuffix} · planned"),
                    modifier = Modifier.weight(1f),
                    color = MareePalette.Muted, fontSize = 13.sp)
            }
            HorizontalDivider(color = MareePalette.Outline)

            OfferFeature(
                option.maxTrackedApps?.let { tr("$it applications suivies", "$it tracked apps") } ?: tr("Applications sans plafond", "No tracked-app limit"),
                if (option.maxTrackedApps == null) tr("Toutes les applications compatibles de ton téléphone.", "All compatible apps on your phone.") else null
            )
            OfferFeature(tr("Défis quotidiens inclus", "Daily challenges included"), tr("Des défis sur plusieurs jours pour retrouver du temps.", "Challenges spanning several days to reclaim your time."))
            OfferFeature(tr("Coach et aide Selkie inclus", "Coach and Selkie help included"), tr("Conseils et réponses sur l’application, sans compte ni frais par message.", "Tips and answers about the app, with no account or per-message fees."))
            OfferFeature(
                tr("${option.coachQuestionCount} questions-réponses préparées", "${option.coachQuestionCount} prepared question-answer pairs"),
                tr("Bibliothèque en français et en anglais, utilisable hors ligne. C’est un choix de contenus, pas un quota de messages ; la saisie libre et l’aide restent accessibles dans toutes les offres.",
                    "A French and English library you can use offline. This is a content library, not a message quota; free-text input and help remain available on every plan.")
            )
            if (option.canCreateCustomChallenges) {
                OfferFeature(tr("Tes propres défis", "Your own challenges"),
                    tr("3 étapes chaque jour · durée de 2 à 365 jours · +0,25 point une fois le défi terminé.", "3 steps each day · 2 to 365 days · +0.25 point when the challenge is completed."))
            }
            if (option == PlanType.ULTRA) {
                OfferFeature(tr("Recherche et tri du suivi", "Search and sort tracked apps"), tr("Par nom, temps passé ou proximité de la limite.", "By name, usage time or how close you are to the limit."))
            }

            when {
                BuildConfig.DEBUG && !selected -> PrimaryAction(
                    label = if (option == PlanType.FREE) tr("Choisir Gratuit", "Choose Free") else tr("Essayer ${planName(option)} gratuitement", "Try ${planName(option)} for free"),
                    onClick = { onDemoPlan(option) },
                    modifier = Modifier.fillMaxWidth()
                )
                !BuildConfig.DEBUG && option != PlanType.FREE -> PrimaryAction(
                    label = tr("Bientôt disponible", "Coming soon"),
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
