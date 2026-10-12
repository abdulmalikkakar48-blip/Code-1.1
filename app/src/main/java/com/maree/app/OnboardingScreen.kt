package com.maree.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Les réglages Android ne s'ouvrent qu'au clic. Les deux accès requis conditionnent le suivi. */
@Composable
fun OnboardingScreen(
    status: PermissionStatus,
    onUsagePermission: () -> Unit,
    onOverlayPermission: () -> Unit,
    onNotificationPermission: () -> Unit,
    onPrivacy: () -> Unit,
    onComplete: () -> Unit,
    onExplore: () -> Unit
) {
    val requiredAccessCount = listOf(status.usageGranted, status.overlayGranted).count { it }
    Surface(modifier = Modifier.fillMaxSize(), color = MareePalette.Background) {
        Column(
            modifier = Modifier.fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BrandIcon(36.dp)
                Spacer(Modifier.width(10.dp))
                Text("selkie", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = MareePalette.Ink)
            }
            LanguageSelector()
            ScreenHeading(
                eyebrow = tr("BIENVENUE", "WELCOME"),
                title = tr("Prépare ton suivi.", "Set up your tracking."),
                description = tr("Deux accès pour mesurer ton temps et afficher une pause. Les notifications sont facultatives.", "Two permissions to measure your time and show a pause. Notifications are optional.")
            )
            SectionLabel(tr("Les accès Android", "Android permissions"), tr("$requiredAccessCount / 2 nécessaires", "$requiredAccessCount / 2 required"))

            OnboardingPermissionCard(
                number = "01",
                title = tr("Mesurer ton temps", "Measure your time"),
                required = true,
                granted = status.usageGranted,
                description = tr("Consulte les durées des applications pour calculer tes limites.", "Checks app usage times to calculate your limits."),
                guidance = tr("Dans les réglages Android, choisis Selkie et active l’accès aux données d’utilisation.", "In Android settings, select Selkie and allow usage access."),
                action = tr("Autoriser les durées", "Allow usage access"),
                onRequest = onUsagePermission
            )
            OnboardingPermissionCard(
                number = "02",
                title = tr("Afficher une pause", "Show a pause"),
                required = true,
                granted = status.overlayGranted,
                description = tr("Affiche l’écran de pause quand la limite d’une application est atteinte.", "Shows a pause screen when an app reaches its limit."),
                guidance = tr("Dans les réglages Android, autorise Selkie à s’afficher par-dessus les autres applications.", "In Android settings, allow Selkie to display over other apps."),
                action = tr("Autoriser l’écran de pause", "Allow pause screen"),
                onRequest = onOverlayPermission
            )
            OnboardingPermissionCard(
                number = "03",
                title = tr("Recevoir une alerte", "Receive an alert"),
                required = false,
                granted = status.notificationsGranted,
                description = tr("Te prévient à l’approche d’une limite. Le suivi fonctionne aussi sans ces alertes.", "Warns you as you approach a limit. Tracking also works without these alerts."),
                guidance = null,
                action = tr("Autoriser les notifications", "Allow notifications"),
                onRequest = onNotificationPermission
            )

            AppCardSurface {
                Text(tr("Tes données restent sur ton téléphone", "Your data stays on your phone"), fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold, color = MareePalette.Ink)
                Text(tr("Selkie ne lit ni tes messages, ni tes photos, ni le contenu de ton écran.", "Selkie does not read your messages, photos, or screen content."),
                    color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp)
                TextButton(onClick = onPrivacy) { Text(tr("Lire la confidentialité", "Read the privacy policy")) }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PrimaryAction(
                    label = tr("Activer le suivi", "Turn on tracking"),
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = status.readyForMonitoring
                )
                if (!status.readyForMonitoring) {
                    Text(tr("Les deux premiers accès sont nécessaires pour activer le suivi.", "The first two permissions are required to turn on tracking."),
                        color = MareePalette.Muted, fontSize = 12.sp, lineHeight = 18.sp)
                }
                TextButton(onClick = onExplore, modifier = Modifier.fillMaxWidth()) {
                    Text(tr("Découvrir sans suivi", "Explore without tracking"), fontWeight = FontWeight.Medium)
                }
                Text(tr("Tu pourras activer le suivi plus tard dans Plus. En attendant, aucune application n’est surveillée ou bloquée.", "You can turn on tracking later in More. Until then, no apps are monitored or blocked."),
                    color = MareePalette.Muted, fontSize = 12.sp, lineHeight = 18.sp)
            }
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
                Text(number, color = MareePalette.Teal, fontSize = 14.sp,
                    fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, color = MareePalette.Ink, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                Text(if (required) tr("Nécessaire au suivi", "Required for tracking") else tr("Facultatif", "Optional"),
                    color = MareePalette.Muted, fontSize = 12.sp)
            }
        }
        Text(description, color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp)
        if (granted) {
            StatusPill(tr("✓ Accès accordé", "✓ Permission granted"))
        } else {
            guidance?.let {
                Text(it, color = MareePalette.Muted, fontSize = 12.sp, lineHeight = 18.sp)
            }
            OutlinedButton(onClick = onRequest, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)) {
                Text(action, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        }
    }
}
