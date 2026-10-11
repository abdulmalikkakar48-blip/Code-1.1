package com.maree.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Aucune demande n'est lancée sans clic ; l'activité gère le retour des réglages Android. */
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
    val colors = MaterialTheme.colorScheme
    Surface(modifier = Modifier.fillMaxSize(), color = colors.background) {
        Column(
            modifier = Modifier.fillMaxSize()
                .background(Brush.verticalGradient(listOf(colors.primary.copy(alpha = 0.08f), colors.background)))
                .statusBarsPadding().navigationBarsPadding()
                .verticalScroll(rememberScrollState()).padding(horizontal = 22.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painterResource(R.drawable.brand_mark), contentDescription = null,
                    modifier = Modifier.size(48.dp))
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("marée", color = colors.onBackground, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    Text("DU TEMPS POUR TOI", color = colors.primary, fontSize = 10.sp, letterSpacing = 1.8.sp)
                }
            }
            Text("Avant de\ncommencer.", fontSize = 35.sp, lineHeight = 40.sp,
                fontWeight = FontWeight.Bold, color = colors.onBackground)
            Text("Choisis les autorisations qui permettent à Marée de protéger ton temps.",
                color = colors.onSurfaceVariant, fontSize = 15.sp, lineHeight = 22.sp)

            Card(shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.primary.copy(alpha = 0.1f))) {
                Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Tes données, sur ton téléphone", color = colors.primary,
                        fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Text("Le suivi fonctionne localement. Marée ne lit pas tes messages ni le contenu de ton écran.",
                        color = colors.onBackground, fontSize = 13.sp, lineHeight = 19.sp)
                    OutlinedButton(onClick = onPrivacy) { Text("Lire la confidentialité") }
                }
            }

            OnboardingPermissionCard(
                title = "Suivre le temps d’écran", symbol = "◷", accent = colors.primary,
                required = true, granted = status.usageGranted,
                description = "Marée consulte les applications ouvertes et leur durée d’utilisation pour calculer tes limites. Dans les réglages Android, choisis Marée puis active l’accès aux données d’utilisation.",
                action = "Autoriser le suivi", onRequest = onUsagePermission
            )
            OnboardingPermissionCard(
                title = "Afficher l’écran de pause", symbol = "▧", accent = colors.secondary,
                required = true, granted = status.overlayGranted,
                description = "Quand une limite est atteinte, Marée affiche un écran de pause au-dessus de l’application suivie. Dans les réglages Android, choisis Marée puis autorise l’affichage par-dessus les autres applications.",
                action = "Autoriser l’écran de pause", onRequest = onOverlayPermission
            )
            OnboardingPermissionCard(
                title = "Recevoir une alerte", symbol = "✦", accent = colors.tertiary,
                required = false, granted = status.notificationsGranted,
                description = "Une notification te prévient à l’approche de ta limite. Tu peux continuer sans notifications : ton choix ne bloque pas l’accès à Marée ni le suivi.",
                action = "Autoriser les notifications", onRequest = onNotificationPermission
            )

            Spacer(Modifier.height(2.dp))
            Button(onClick = onComplete, enabled = status.readyForMonitoring,
                modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(18.dp)) {
                Text("Activer le suivi", fontWeight = FontWeight.SemiBold)
            }
            if (!status.readyForMonitoring) {
                Text("Pour activer le suivi, autorise les données d’utilisation et l’écran de pause.",
                    color = colors.onSurfaceVariant, fontSize = 13.sp, lineHeight = 18.sp)
            }
            TextButton(onClick = onExplore, modifier = Modifier.fillMaxWidth()) {
                Text("Découvrir sans suivi")
            }
            Text("En mode découverte, le suivi et les écrans de pause restent désactivés. Tu pourras les activer dans l’onglet Plus.",
                color = colors.onSurfaceVariant, fontSize = 12.sp, lineHeight = 18.sp)
        }
    }
}

@Composable
private fun OnboardingPermissionCard(
    title: String,
    symbol: String,
    accent: Color,
    required: Boolean,
    granted: Boolean,
    description: String,
    action: String,
    onRequest: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Card(shape = RoundedCornerShape(22.dp), border = BorderStroke(1.dp, accent.copy(alpha = 0.25f)),
        colors = CardDefaults.cardColors(containerColor = colors.surface)) {
        Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = accent.copy(alpha = 0.14f), shape = RoundedCornerShape(12.dp)) {
                    Text(symbol, fontSize = 25.sp, color = accent,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(title, color = colors.onSurface, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                    Text(if (required) "NÉCESSAIRE AU SUIVI" else "FACULTATIF", color = accent,
                        fontSize = 10.sp, letterSpacing = 0.7.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            Text(description, color = colors.onSurfaceVariant, fontSize = 13.sp, lineHeight = 19.sp)
            if (granted) {
                Text("✓ Autorisation accordée", color = accent, fontWeight = FontWeight.Medium, fontSize = 13.sp)
            } else {
                Button(onClick = onRequest, colors = ButtonDefaults.buttonColors(
                    containerColor = accent, contentColor = colors.background),
                    shape = RoundedCornerShape(12.dp)) { Text(action, fontSize = 13.sp) }
            }
        }
    }
}
