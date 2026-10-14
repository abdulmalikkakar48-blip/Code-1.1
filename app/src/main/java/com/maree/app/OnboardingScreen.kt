package com.maree.app

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** Progression explicite ; aucun réglage Android ni accès n'est demandé automatiquement. */
@Composable
fun OnboardingScreen(
    status: PermissionStatus,
    onUsagePermission: () -> Unit,
    onOverlayPermission: () -> Unit,
    onNotificationPermission: () -> Unit,
    onPrivacy: () -> Unit,
    onComplete: () -> Unit,
    onExplore: () -> Unit,
    currentStep: Int = 0,
    onStepChange: (Int) -> Unit = {},
    permissionsOnly: Boolean = false
) {
    val step = if (permissionsOnly) 2 else currentStep.coerceIn(0, 2)
    val scroll = rememberScrollState()
    LaunchedEffect(step) { scroll.scrollTo(0) }
    BackHandler(enabled = step > 0 && !permissionsOnly) { onStepChange(step - 1) }

    Surface(modifier = Modifier.fillMaxSize(), color = MareePalette.Background) {
        Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
            Column(
                Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BrandIcon(42.dp)
                    Spacer(Modifier.width(12.dp))
                    Text("selkie", style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold, color = MareePalette.Ink,
                        modifier = Modifier.weight(1f))
                    if (!permissionsOnly) {
                        StatusPill(tr("Étape ${step + 1} / 3", "Step ${step + 1} / 3"))
                    }
                }
                if (!permissionsOnly) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        repeat(3) { index ->
                            Box(Modifier.weight(1f).height(4.dp).clip(RoundedCornerShape(4.dp))
                                .background(if (index <= step) MareePalette.Teal else MareePalette.Outline))
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(scroll)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                when (step) {
                    0 -> WelcomePurpose()
                    1 -> WelcomeNavigation()
                    else -> WelcomePermissions(status, onUsagePermission,
                        onOverlayPermission, onNotificationPermission)
                }
                TextButton(onClick = onPrivacy, modifier = Modifier.fillMaxWidth()) {
                    Text(tr("Lire la politique de confidentialité", "Read the privacy policy"))
                }
            }

            Surface(color = MareePalette.Surface) {
                Column {
                    HorizontalDivider(color = MareePalette.Outline)
                    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            if (step > 0 && !permissionsOnly) {
                                OutlinedButton(onClick = { onStepChange(step - 1) },
                                    shape = RoundedCornerShape(18.dp),
                                    modifier = Modifier.heightIn(min = 52.dp)) {
                                    Text(tr("Retour", "Back"))
                                }
                            }
                            PrimaryAction(
                                label = when (step) {
                                    0 -> tr("Découvrir Selkie", "Discover Selkie")
                                    1 -> tr("Choisir les accès", "Choose permissions")
                                    else -> tr("Activer le suivi", "Turn on tracking")
                                },
                                onClick = { if (step < 2) onStepChange(step + 1) else onComplete() },
                                modifier = Modifier.weight(1f),
                                enabled = step < 2 || status.readyForMonitoring
                            )
                        }
                        TextButton(onClick = onExplore, modifier = Modifier.fillMaxWidth()) {
                            Text(tr("Découvrir sans suivi", "Explore without tracking"),
                                style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WelcomePurpose() {
    WelcomeHeading(
        title = tr("Moins d’écran.\nPlus de place pour toi.", "Less screen time.\nMore room for you."),
        description = tr("Selkie t’aide à limiter les distractions et à construire de bonnes habitudes, à ton rythme.",
            "Selkie helps you limit distractions and build good habits, at your own pace.")
    )
    AppCardSurface(containerColor = MareePalette.HeroBackground) {
        Text(tr("Commence avec une application", "Start with one app"),
            color = MareePalette.OnHero, style = MaterialTheme.typography.titleLarge)
        Text(tr("Choisis l’application qui te prend le plus de temps et fixe une limite. Selkie affiche une pause quand cette limite est atteinte.",
            "Choose the app that takes up most of your time and set a limit. Selkie shows a pause when you reach it."),
            color = MareePalette.OnHeroMuted, style = MaterialTheme.typography.bodyMedium)
    }
    AppCardSurface {
        WelcomeBenefit("01", tr("Reprends ton temps", "Take back your time"),
            tr("Suis les applications que tu choisis et leur limite quotidienne.",
                "Track the apps you choose and their daily limits."))
        HorizontalDivider(color = MareePalette.Outline)
        WelcomeBenefit("02", tr("Organise tes journées", "Organise your days"),
            tr("Note tes tâches dans le calendrier et retrouve des lectures courtes.",
                "Add tasks to your calendar and find short readings."))
        HorizontalDivider(color = MareePalette.Outline)
        WelcomeBenefit("03", tr("Avance par petites étapes", "Make small steps"),
            tr("Répète des défis sur plusieurs jours et trouve des conseils dans le coach.",
                "Repeat challenges over several days and find advice in the coach."))
    }
    LanguageSelector()
}

@Composable
private fun WelcomeNavigation() {
    WelcomeHeading(
        title = tr("Quatre onglets,\nun rôle chacun.", "Four tabs,\none purpose each."),
        description = tr("Tu les retrouveras en bas de l’application. Voici où aller selon ce que tu veux faire.",
            "You will find them at the bottom of the app. Here is where to go for each activity.")
    )
    AppCardSurface {
        WelcomeTab(R.drawable.ic_nav_home, tr("Accueil", "Home"),
            tr("Ajoute tes applications, choisis leurs limites et consulte ton temps d’utilisation et tes points.",
                "Add your apps, choose their limits and check your usage time and points."))
        HorizontalDivider(color = MareePalette.Outline)
        WelcomeTab(R.drawable.ic_nav_daily, tr("Quotidien", "Daily"),
            tr("Choisis une date, note tes tâches et consulte ton bilan. Lecture propose des textes courts ; Outils regroupe les créneaux, programmes guidés et widget, selon l’offre.",
                "Choose a date, add tasks and view your review. Reading offers short texts; Tools contains time slots, guided programs and the widget, depending on your plan."))
        HorizontalDivider(color = MareePalette.Outline)
        WelcomeTab(R.drawable.ic_nav_challenges, tr("Défis", "Challenges"),
            tr("Valide des activités sur plusieurs jours pour gagner des points. Chaque étape et chaque journée sont indiquées dans l’ordre.",
                "Check off activities over several days to earn points. Steps and days are shown in order."))
        HorizontalDivider(color = MareePalette.Outline)
        WelcomeTab(R.drawable.ic_nav_coach, tr("Coach", "Coach"),
            tr("Pose une question ou explore les sujets. Le coach utilise des réponses préparées et fonctionne hors ligne. Tu peux garder tes conseils favoris.",
                "Ask a question or explore topics. The coach uses prepared answers and works offline. You can save your favourite tips."))
    }
    AppCardSurface {
        WelcomeTab(R.drawable.ic_menu, tr("Le menu en haut", "The menu at the top"),
            tr("Retrouve la langue, l’apparence, les accès Android, les offres et les informations de confidentialité dans le menu ☰.",
                "Find language, appearance, Android permissions, plans and privacy information in the ☰ menu."))
    }
    Text(tr("Une limite atteinte crée un verrou de 24 heures. Les défis rapportent des points ; à 0 point, termine des défis avant d’ajouter ou de modifier une limite.",
        "Reaching a limit creates a 24-hour lock. Challenges earn points; at 0 points, complete challenges before adding or editing a limit."),
        color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall)
}

@Composable
private fun WelcomePermissions(
    status: PermissionStatus,
    onUsagePermission: () -> Unit,
    onOverlayPermission: () -> Unit,
    onNotificationPermission: () -> Unit
) {
    val requiredAccessCount = listOf(status.usageGranted, status.overlayGranted).count { it }
    WelcomeHeading(
        title = tr("Ton téléphone,\nton choix.", "Your phone,\nyour choice."),
        description = tr("Deux accès Android sont nécessaires au suivi. Tu peux aussi découvrir Selkie sans les accorder.",
            "Tracking needs two Android permissions. You can also explore Selkie without granting them.")
    )
    SectionLabel(tr("Pour activer le suivi", "To turn on tracking"),
        tr("$requiredAccessCount / 2 accordés", "$requiredAccessCount / 2 granted"))
    OnboardingPermissionCard(
        number = "01",
        title = tr("Mesurer ton temps", "Measure your time"),
        required = true,
        granted = status.usageGranted,
        description = tr("Consulte les durées d’utilisation pour calculer les limites des applications choisies.",
            "Reads usage times to calculate limits for your chosen apps."),
        guidance = tr("Dans les réglages Android, choisis Selkie et active l’accès aux données d’utilisation.",
            "In Android settings, choose Selkie and allow usage access."),
        action = tr("Autoriser les durées", "Allow usage access"),
        onRequest = onUsagePermission
    )
    OnboardingPermissionCard(
        number = "02",
        title = tr("Afficher une pause", "Show a pause"),
        required = true,
        granted = status.overlayGranted,
        description = tr("Affiche l’écran de pause par-dessus une application quand sa limite est atteinte.",
            "Shows the pause screen over an app when its limit is reached."),
        guidance = tr("Dans les réglages Android, autorise Selkie à s’afficher par-dessus les autres applications.",
            "In Android settings, allow Selkie to display over other apps."),
        action = tr("Autoriser l’écran de pause", "Allow pause screen"),
        onRequest = onOverlayPermission
    )
    OnboardingPermissionCard(
        number = "03",
        title = tr("Recevoir une alerte", "Receive an alert"),
        required = false,
        granted = status.notificationsGranted,
        description = tr("Te prévient à l’approche d’une limite. Le suivi fonctionne aussi sans ces alertes.",
            "Warns you as you approach a limit. Tracking also works without these alerts."),
        guidance = null,
        action = tr("Autoriser les notifications", "Allow notifications"),
        onRequest = onNotificationPermission
    )
    AppCardSurface {
        Text(tr("Tes données restent sur ton téléphone", "Your data stays on your phone"),
            color = MareePalette.Ink, style = MaterialTheme.typography.titleMedium)
        Text(tr("Selkie ne lit ni les messages de tes autres applications, ni tes photos, ni le contenu de ton écran.",
            "Selkie does not read messages from your other apps, photos or screen content."),
            color = MareePalette.Muted, style = MaterialTheme.typography.bodyMedium)
        Text(tr("Sans suivi, aucune application n’est surveillée ou bloquée. Tu pourras l’activer plus tard : menu ☰ → Accès Android.",
            "Without tracking, no apps are monitored or blocked. You can enable it later: ☰ menu → Android access."),
            color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun WelcomeHeading(title: String, description: String) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(title, color = MareePalette.Ink, style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.semantics { heading() })
        Text(description, color = MareePalette.Muted, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun WelcomeBenefit(number: String, title: String, description: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.Top) {
        Text(number, color = MareePalette.Teal, style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 3.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, color = MareePalette.Ink, style = MaterialTheme.typography.titleMedium)
            Text(description, color = MareePalette.Muted, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun WelcomeTab(icon: Int, title: String, description: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.Top) {
        Surface(shape = RoundedCornerShape(12.dp), color = MareePalette.Subtle) {
            Icon(painterResource(icon), contentDescription = null, tint = MareePalette.Teal,
                modifier = Modifier.padding(10.dp).size(22.dp))
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, color = MareePalette.Ink, style = MaterialTheme.typography.titleMedium)
            Text(description, color = MareePalette.Muted, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun OnboardingPermissionCard(
    number: String,
    title: String,
    required: Boolean,
    granted: Boolean,
    description: String,
    guidance: String?,
    action: String,
    onRequest: () -> Unit
) {
    AppCardSurface {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(color = MareePalette.Subtle, shape = RoundedCornerShape(12.dp)) {
                Text(number, color = MareePalette.Teal, style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(12.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, color = MareePalette.Ink, style = MaterialTheme.typography.titleMedium)
                Text(if (required) tr("Nécessaire au suivi", "Required for tracking") else tr("Facultatif", "Optional"),
                    color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall)
            }
        }
        Text(description, color = MareePalette.Muted, style = MaterialTheme.typography.bodyMedium)
        if (granted) {
            StatusPill(tr("✓ Accès accordé", "✓ Permission granted"))
        } else {
            guidance?.let {
                Text(it, color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall)
            }
            OutlinedButton(onClick = onRequest, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                shape = RoundedCornerShape(14.dp)) {
                Text(action, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
