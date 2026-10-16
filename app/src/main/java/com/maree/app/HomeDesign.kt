package com.maree.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun HomeWelcomeCard(onGuide: () -> Unit) {
    AppCardSurface(containerColor = MareePalette.Lilac) {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val largeText = LocalDensity.current.fontScale > 1.25f
            val narrow = maxWidth < 265.dp
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(tr("FAIS DE LA PLACE À TA VIE", "MAKE ROOM FOR YOUR LIFE"),
                    color = MareePalette.Lavender, style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 0.8.sp)
                if (largeText) {
                    WelcomeTitle()
                    SelkieSculpture(Modifier.align(Alignment.End).size(132.dp, 104.dp))
                } else {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) { WelcomeTitle() }
                        SelkieSculpture(Modifier.size(if (narrow) 84.dp else 110.dp, 96.dp))
                    }
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(tr("Tes limites. Tes défis. Tes pauses.", "Your limits. Your challenges. Your breaks."),
                        color = MareePalette.Muted, style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f))
                    TextButton(onClick = onGuide, modifier = Modifier.heightIn(min = 48.dp)) {
                        Text(tr("Guide  →", "Guide  →"))
                    }
                }
            }
        }
    }
}

@Composable
private fun WelcomeTitle() {
    Text(tr("Moins d’écran.\nPlus de vie.", "Less screen.\nMore life."),
        color = MareePalette.Ink, fontSize = 24.sp, lineHeight = 29.sp,
        letterSpacing = (-0.7).sp, fontWeight = FontWeight.Bold,
        modifier = Modifier.semantics { heading() })
}

@Composable
internal fun HomeTrackingStrip(points: Double, active: Boolean, onChallenges: () -> Unit, onSetup: () -> Unit) {
    Surface(color = MareePalette.Surface, shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, MareePalette.Outline)) {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val stack = maxWidth < 280.dp || LocalDensity.current.fontScale > 1.25f || formatPoints(points).length > 6
            Column(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 4.dp)) {
                if (stack) Column {
                    PointsShortcut(points, onChallenges)
                    TrackingShortcut(active, onSetup)
                } else Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    PointsShortcut(points, onChallenges)
                    TrackingShortcut(active, onSetup)
                }
                if (!active) Text(tr("Suivi en pause · tes outils restent disponibles.",
                    "Tracking paused · your tools are still available."),
                    color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 12.dp, end = 8.dp, bottom = 9.dp))
            }
        }
    }
}

@Composable
private fun PointsShortcut(points: Double, onClick: () -> Unit) {
    TextButton(onClick = onClick, modifier = Modifier.heightIn(min = 48.dp)) {
        Icon(painterResource(R.drawable.ic_nav_challenges), null,
            tint = MareePalette.Amber, modifier = Modifier.size(18.dp))
        Text("${formatPoints(points)} / $MAX_HONOR_POINTS", modifier = Modifier.padding(start = 7.dp).semantics {
            contentDescription = tr("${formatPoints(points)} points sur $MAX_HONOR_POINTS",
                "${formatPoints(points)} out of $MAX_HONOR_POINTS points")
        },
            color = MareePalette.Ink, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun TrackingShortcut(active: Boolean, onSetup: () -> Unit) {
    if (active) StatusPill(tr("Suivi actif", "Tracking on"))
    else TextButton(onClick = onSetup, modifier = Modifier.heightIn(min = 48.dp)) {
        Text(tr("Activer le suivi", "Turn on tracking"))
    }
}

@Composable
internal fun HomeAppsHeader(count: Int, limit: Int?, canAdd: Boolean, onAdd: () -> Unit) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val stack = maxWidth < 280.dp || LocalDensity.current.fontScale > 1.25f
        if (stack) Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            AppsHeading(count, limit)
            PrimaryAction(tr("+ Ajouter", "+ Add"), onAdd, enabled = canAdd)
        } else Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { AppsHeading(count, limit) }
            PrimaryAction(tr("+ Ajouter", "+ Add"), onAdd, enabled = canAdd)
        }
    }
}

@Composable
private fun AppsHeading(count: Int, limit: Int?) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(tr("Tes applications", "Your apps"), color = MareePalette.Ink,
            fontSize = 20.sp, fontWeight = FontWeight.SemiBold,
            modifier = Modifier.semantics { heading() })
        Text(if (limit == null) tr("$count suivies · sans plafond", "$count tracked · no limit")
            else tr("$count sur $limit suivies", "$count of $limit tracked"),
            color = MareePalette.Muted, fontSize = 12.sp)
    }
}

@Composable
internal fun HomeShortcuts(onCalendar: () -> Unit, onCoach: () -> Unit) {
    // A column at large font sizes prevents the two button labels being squeezed.
    if (LocalDensity.current.fontScale > 1.25f) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ShortcutCard(true, onCalendar, Modifier.fillMaxWidth())
            ShortcutCard(false, onCoach, Modifier.fillMaxWidth())
        }
    } else Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        ShortcutCard(true, onCalendar, Modifier.weight(1f))
        ShortcutCard(false, onCoach, Modifier.weight(1f))
    }
}

@Composable
private fun ShortcutCard(calendar: Boolean, onClick: () -> Unit, modifier: Modifier) {
    OutlinedButton(onClick = onClick, modifier = modifier.heightIn(min = 118.dp),
        shape = RoundedCornerShape(24.dp), border = BorderStroke(1.dp, MareePalette.Outline),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (calendar) MareePalette.Peach else MareePalette.Mint,
            contentColor = MareePalette.Ink)) {
        Column(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(painterResource(if (calendar) R.drawable.ic_nav_daily else R.drawable.ic_nav_coach),
                contentDescription = null, modifier = Modifier.size(22.dp),
                tint = if (calendar) MareePalette.Coral else MareePalette.Teal)
            Text(if (calendar) tr("Calendrier  ↗", "Calendar  ↗") else tr("Coach  ↗", "Coach  ↗"),
                style = MaterialTheme.typography.titleMedium)
            Text(if (calendar) tr("Prépare ta journée", "Plan your day") else tr("Trouve ton conseil", "Find your advice"),
                color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall)
        }
    }
}
