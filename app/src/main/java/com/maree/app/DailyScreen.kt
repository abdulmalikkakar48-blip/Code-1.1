package com.maree.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Bilan des compteurs disponibles, sans historique ni temps gagné inventé. */
@Composable
internal fun DailyTab(
    snapshot: MareeSnapshot,
    onCoach: () -> Unit,
    getName: (String) -> String,
    onAddApp: () -> Unit,
    monitoringActive: Boolean,
    onActivateTracking: () -> Unit
) {
    val today = LocalDate.now()
    val tip = dailyTips[Math.floorMod(today.toEpochDay(), dailyTips.size.toLong()).toInt()]
    val usedMillis = snapshot.apps.sumOf { it.usedMillis }
    val limitMinutes = snapshot.apps.sumOf { it.limitMinutes.toLong() }
    val withinLimit = snapshot.apps.count {
        !it.isLocked && it.usedMillis < it.limitMinutes * 60_000L
    }
    val lockedCount = snapshot.apps.count { it.isLocked }
    val challengeIndices = if (snapshot.challengeSeries == 0) 0..9 else 10..19
    val validatedToday = challengeIndices.count {
        snapshot.challengeProgress[it]?.validatedToday == true
    }
    val personalValidationsToday = snapshot.customChallenges.count { it.validatedToday }
    var showAllApps by rememberSaveable { mutableStateOf(false) }
    var showCounterDetails by rememberSaveable { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ScreenHeading(
            eyebrow = today.format(DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.FRENCH))
                .replaceFirstChar { it.titlecase(Locale.FRENCH) },
            title = "Un peu plus de vie.",
            description = "Une pause, un repère, un petit pas à la fois."
        )
        AppCardSurface(containerColor = MareePalette.Mint) {
            SectionLabel("Ta pause du jour")
            Text(tip.first, color = MareePalette.Ink, fontWeight = FontWeight.Bold,
                fontSize = 22.sp, lineHeight = 28.sp)
            Text(tip.second, color = MareePalette.Muted, fontSize = 16.sp, lineHeight = 24.sp)
            Text("Une idée à essayer, sans validation ni points.",
                color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 19.sp)
        }
        if (!monitoringActive) {
            AppCardSurface {
                StatusPill("Suivi en pause")
                Text("Tes repères t’attendent", fontWeight = FontWeight.Bold,
                    fontSize = 20.sp, color = MareePalette.Ink)
                Text("Active le suivi pour actualiser tes durées. Les conseils et les défis restent disponibles.",
                    color = MareePalette.Muted, fontSize = 15.sp, lineHeight = 22.sp)
                PrimaryAction("Configurer le suivi", onClick = onActivateTracking)
            }
        } else if (snapshot.apps.isEmpty()) {
            AppCardSurface {
                SectionLabel("Ton premier repère")
                Text("Commence avec une application", fontWeight = FontWeight.Bold,
                    fontSize = 20.sp, color = MareePalette.Ink)
                Text("Choisis celle que tu souhaites moins consulter. Tu retrouveras son compteur ici.",
                    color = MareePalette.Muted, fontSize = 15.sp, lineHeight = 22.sp)
                PrimaryAction("Choisir une application", onClick = onAddApp,
                    enabled = snapshot.points > 0)
                if (snapshot.points <= 0) {
                    Text("Il te faut des points pour ajouter une application.",
                        color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 19.sp)
                }
            }
        } else {
            AppCardSurface {
                SectionLabel("Tes compteurs")
                Text(durationLabel(usedMillis / 60_000L), color = MareePalette.Teal,
                    fontWeight = FontWeight.Bold, fontSize = 36.sp, lineHeight = 42.sp)
                Text("Sur ${snapshot.apps.size} application${if (snapshot.apps.size > 1) "s" else ""} suivie${if (snapshot.apps.size > 1) "s" else ""}",
                    color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp)
                LinearProgressIndicator(
                    progress = {
                        (usedMillis / (limitMinutes.coerceAtLeast(1) * 60_000f)).coerceIn(0f, 1f)
                    },
                    modifier = Modifier.fillMaxWidth().height(10.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = MareePalette.Teal,
                    trackColor = MareePalette.Subtle
                )
                Text("${durationLabel(limitMinutes)} de limites cumulées",
                    color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp)
                Text("$withinLimit sous la limite · $lockedCount verrouillée${if (lockedCount > 1) "s" else ""}",
                    color = MareePalette.Ink, fontWeight = FontWeight.Medium,
                    fontSize = 14.sp, lineHeight = 21.sp)
                TextButton(onClick = { showCounterDetails = !showCounterDetails }) {
                    Text(if (showCounterDetails) "Masquer l’explication" else "Que mesure ce bilan ?")
                }
                if (showCounterDetails) {
                    Text("Ce bilan additionne les compteurs de tes applications suivies. Il ne mesure pas tout ton temps d’écran. Ajouter une application, réinitialiser sa limite ou la débloquer remet son compteur à zéro.",
                        color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp)
                }
                HorizontalDivider(color = MareePalette.Outline)
                val visibleApps = if (showAllApps) snapshot.apps else snapshot.apps.take(3)
                visibleApps.forEachIndexed { index, app ->
                    if (index > 0) HorizontalDivider(color = MareePalette.Outline)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(getName(app.packageName), color = MareePalette.Ink,
                            fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp)
                        Text("${durationLabel(app.usedMillis / 60_000L)} / ${durationLabel(app.limitMinutes.toLong())}",
                            color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp)
                        if (app.isLocked || app.usedMillis >= app.limitMinutes * 60_000L) {
                            Text(if (app.isLocked) "En pause · application verrouillée" else "Limite atteinte",
                                color = MareePalette.Danger, fontSize = 13.sp, lineHeight = 19.sp)
                        }
                    }
                }
                if (snapshot.apps.size > 3) {
                    TextButton(onClick = { showAllApps = !showAllApps }) {
                        Text(if (showAllApps) "Réduire la liste" else "Voir les ${snapshot.apps.size} applications")
                    }
                }
            }
        }
        AppCardSurface {
            SectionLabel("Tes petits pas")
            Text("$validatedToday défi${if (validatedToday > 1) "s" else ""} validé${if (validatedToday > 1) "s" else ""} aujourd’hui",
                color = MareePalette.Ink, fontWeight = FontWeight.Bold,
                fontSize = 20.sp, lineHeight = 27.sp)
            if (snapshot.customChallenges.isNotEmpty()) {
                Text("$personalValidationsToday défi${if (personalValidationsToday > 1) "s" else ""} personnel${if (personalValidationsToday > 1) "s" else ""} validé${if (personalValidationsToday > 1) "s" else ""} aujourd’hui",
                    color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp)
            }
            HorizontalDivider(color = MareePalette.Outline)
            Text("${snapshot.checkedChallenges.size} / 10 défis terminés dans ta série",
                color = MareePalette.Teal, fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp, lineHeight = 22.sp)
            Text("${snapshot.totalCompletedChallenges} terminé${if (snapshot.totalCompletedChallenges > 1) "s" else ""} au total",
                color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp)
            Text("Chaque défi de la série demande $CHALLENGE_REQUIRED_DAYS journées. Elles peuvent être espacées.",
                color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 19.sp)
        }
        AppCardSurface {
            Text("Besoin d’un coup de pouce ?", color = MareePalette.Ink,
                fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 27.sp)
            Text("Choisis un conseil concret, disponible hors ligne.",
                color = MareePalette.Muted, fontSize = 15.sp, lineHeight = 22.sp)
            PrimaryAction("Ouvrir le coach", onClick = onCoach)
        }
        Spacer(Modifier.height(4.dp))
    }
}

private fun durationLabel(minutes: Long): String {
    val nonNegative = minutes.coerceAtLeast(0L)
    return if (nonNegative < 60) "$nonNegative min"
    else if (nonNegative % 60 == 0L) "${nonNegative / 60} h"
    else "${nonNegative / 60} h ${nonNegative % 60} min"
}

private val dailyTips = listOf(
    "Fais une vraie pause" to "Pose ton téléphone hors de portée pendant dix minutes. Bois un verre d’eau, regarde dehors ou ne fais simplement rien.",
    "Choisis avant d’ouvrir" to "Avant de lancer une application, demande-toi ce que tu veux y faire. Quand c’est fait, referme-la.",
    "Allège tes notifications" to "Choisis une application qui te sollicite trop et coupe ses notifications non essentielles dans les réglages de ton téléphone.",
    "Garde un repas pour toi" to "À ton prochain repas, laisse ton téléphone dans une autre pièce. Profite de ce que tu manges et de la conversation.",
    "Prends l’air" to "Accorde-toi une petite promenade sans consulter ton écran. Regarde ce qui t’entoure, à ton rythme.",
    "Prépare une alternative" to "Place un livre, un carnet ou une activité que tu aimes à côté de toi. Tu auras une autre option quand l’envie de scroller revient.",
    "Préserve ton repos" to "Ce soir, choisis un endroit pour poser ton téléphone loin de ton lit. Une petite distance peut aider à fermer l’écran."
)
