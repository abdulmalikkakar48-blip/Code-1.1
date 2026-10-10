package com.maree.app

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Un bilan des compteurs réellement disponibles, sans historique ou temps gagné inventé. */
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
    val usedMillis = snapshot.apps.sumOf { it.usedMillis }
    val limitMinutes = snapshot.apps.sumOf { it.limitMinutes.toLong() }
    val withinLimit = snapshot.apps.count {
        !it.isLocked && it.usedMillis < it.limitMinutes * 60_000L
    }
    val lockedCount = snapshot.apps.count { it.isLocked }
    val tip = dailyTips[(today.toEpochDay() % dailyTips.size).toInt()]
    val currentChallenges = snapshot.checkedChallenges.size
    val challengeIndices = if (snapshot.challengeSeries == 0) 0..9 else 10..19
    val validatedToday = challengeIndices.count { snapshot.challengeProgress[it]?.validatedToday == true }
    val personalValidationsToday = snapshot.customChallenges.count { it.validatedToday }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            today.format(DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.FRENCH))
                .replaceFirstChar { it.titlecase(Locale.FRENCH) },
            color = MareePalette.Coral,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
        Text(
            "Ton quotidien,\nà ton rythme.",
            color = MareePalette.Ink,
            fontWeight = FontWeight.Bold,
            fontSize = 31.sp,
            lineHeight = 38.sp
        )
        Text(
            "Prends un instant pour observer tes habitudes et choisir ta prochaine pause.",
            color = MareePalette.Muted,
            fontSize = 15.sp
        )

        if (!monitoringActive) {
            DailyCard(MareePalette.Peach) {
                Text("Le bilan attend ton suivi", fontWeight = FontWeight.Bold, fontSize = 20.sp,
                    color = MareePalette.Coral)
                Text("Le suivi est en pause. Active-le pour actualiser les durées et le bilan de tes applications. Tes conseils et tes défis restent disponibles.",
                    color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp)
                Button(onClick = onActivateTracking) { Text("Configurer le suivi") }
            }
        } else if (snapshot.apps.isEmpty()) {
            DailyCard(MareePalette.Subtle) {
                Text("Ton premier repère", fontWeight = FontWeight.Bold, fontSize = 21.sp)
                Text(
                    "Choisis une application à suivre. Ton bilan se remplira avec son temps d’utilisation.",
                    color = MareePalette.Muted
                )
                Button(onClick = onAddApp, enabled = snapshot.points > 0) {
                    Text("Choisir une application")
                }
                if (snapshot.points <= 0) {
                    Text(
                        "L’ajout d’une application sera disponible quand tu auras retrouvé des points.",
                        color = MareePalette.Muted,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            DailyCard(MareePalette.Subtle) {
                Text(
                    "TEMPS COMPTÉ AUJOURD’HUI",
                    color = MareePalette.DeepTeal,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    durationLabel(usedMillis / 60_000L),
                    color = MareePalette.Teal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                    lineHeight = 46.sp
                )
                Text("sur tes ${snapshot.apps.size} application${if (snapshot.apps.size > 1) "s" else ""} suivie${if (snapshot.apps.size > 1) "s" else ""}",
                    color = MareePalette.Muted)
                LinearProgressIndicator(
                    progress = {
                        (usedMillis / (limitMinutes.coerceAtLeast(1) * 60_000f)).coerceIn(0f, 1f)
                    },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(8.dp)),
                    color = MareePalette.Teal,
                    trackColor = MareePalette.Teal.copy(alpha = .12f)
                )
                Text(
                    "${durationLabel(limitMinutes)} de limites cumulées",
                    color = MareePalette.DeepTeal,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
                Text(
                    "Ce bilan additionne les compteurs actuels, pas tout le temps passé sur ton téléphone. " +
                        "Ajouter une application, réinitialiser sa limite ou la débloquer remet son compteur à zéro.",
                    color = MareePalette.Muted,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DailyCard(MareePalette.Peach, Modifier.weight(1f)) {
                    Text("$withinLimit", color = MareePalette.Coral,
                        fontWeight = FontWeight.Bold, fontSize = 29.sp)
                    Text("Sous la limite", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
                DailyCard(MareePalette.Lilac, Modifier.weight(1f)) {
                    Text("$lockedCount", color = MareePalette.Lavender,
                        fontWeight = FontWeight.Bold, fontSize = 29.sp)
                    Text("Verrouillée${if (lockedCount > 1) "s" else ""}",
                        fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }

            DailyCard(MareePalette.Surface) {
                Text("Tes repères", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                snapshot.apps.forEachIndexed { index, app ->
                    if (index > 0) HorizontalDivider(color = MareePalette.Outline)
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text(getName(app.packageName), fontWeight = FontWeight.SemiBold)
                        Text(
                            "${durationLabel(app.usedMillis / 60_000L)} / ${durationLabel(app.limitMinutes.toLong())}",
                            color = MareePalette.Muted,
                            fontSize = 13.sp
                        )
                        Text(
                            when {
                                app.isLocked -> "En pause : application verrouillée"
                                app.usedMillis >= app.limitMinutes * 60_000L -> "Limite atteinte"
                                else -> "Ta limite est respectée pour le moment"
                            },
                            color = if (app.isLocked) MareePalette.Lavender else MareePalette.Teal,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        DailyCard(MareePalette.Sand) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DailySun(Modifier.size(36.dp))
                Text("La pause du jour", fontWeight = FontWeight.Bold,
                    fontSize = 21.sp, color = MareePalette.Ink,
                    modifier = Modifier.weight(1f))
            }
            Text(tip.first, fontWeight = FontWeight.SemiBold, fontSize = 17.sp,
                color = MareePalette.Amber)
            Text(tip.second, color = MareePalette.Muted, fontSize = 15.sp, lineHeight = 22.sp)
            Text("Une idée à essayer, sans objectif à cocher.",
                color = MareePalette.Muted, fontSize = 12.sp)
        }

        DailyCard(MareePalette.Surface) {
            Text("Chaque petit pas compte", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(
                "$validatedToday défi${if (validatedToday > 1) "s" else ""} Marée pratiqué${if (validatedToday > 1) "s" else ""} et validé${if (validatedToday > 1) "s" else ""} aujourd’hui.",
                color = MareePalette.Lavender,
                fontWeight = FontWeight.Medium
            )
            Text(
                "$currentChallenges / 10 défis entièrement terminés dans ta série actuelle.",
                color = MareePalette.Muted
            )
            Text(
                "${snapshot.totalCompletedChallenges} défi${if (snapshot.totalCompletedChallenges > 1) "s" else ""} terminé${if (snapshot.totalCompletedChallenges > 1) "s" else ""} au total.",
                color = MareePalette.Teal,
                fontWeight = FontWeight.Medium
            )
            Text("Un défi se termine après $CHALLENGE_REQUIRED_DAYS jours différents, qui peuvent être espacés. Les totaux de défis terminés couvrent plusieurs jours.",
                color = MareePalette.Muted, fontSize = 12.sp, lineHeight = 18.sp)
            if (snapshot.customChallenges.isNotEmpty()) {
                HorizontalDivider(color = MareePalette.Outline)
                Text(
                    "$personalValidationsToday défi${if (personalValidationsToday > 1) "s" else ""} personnel${if (personalValidationsToday > 1) "s" else ""} validé${if (personalValidationsToday > 1) "s" else ""} aujourd’hui.",
                    color = MareePalette.Lavender, fontWeight = FontWeight.Medium, fontSize = 14.sp
                )
                Text("Pour tes défis personnels, la durée est celle que tu as choisie. Chaque journée nécessite les 3 étapes.",
                    color = MareePalette.Muted, fontSize = 12.sp, lineHeight = 18.sp)
            }
        }

        DailyCard(MareePalette.Lilac) {
            Text("Ton coach, bientôt", color = MareePalette.Lavender,
                fontWeight = FontWeight.Bold, fontSize = 21.sp)
            Text(
                "Un espace pour réfléchir à tes habitudes et trouver des pistes adaptées à ton quotidien.",
                color = MareePalette.Muted
            )
            Text("Le coach IA n’est pas encore disponible. Les idées ci-dessus sont des conseils fixes, accessibles sans compte.",
                color = MareePalette.Muted, fontSize = 13.sp)
            TextButton(onClick = onCoach) { Text("Découvrir le coach") }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun DailyCard(
    color: Color,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = color, contentColor = MareePalette.Ink)
    ) {
        Column(Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp), content = content)
    }
}

@Composable
private fun DailySun(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val radius = size.minDimension / 5f
        drawCircle(MareePalette.Coral, radius = radius, center = center)
        for (ray in 0..7) {
            val angle = ray * Math.PI / 4
            val direction = Offset(kotlin.math.cos(angle).toFloat(), kotlin.math.sin(angle).toFloat())
            drawLine(MareePalette.Coral, center + direction * (radius * 1.5f),
                center + direction * (radius * 2.1f), strokeWidth = 2.dp.toPx())
        }
    }
}

private fun durationLabel(minutes: Long): String {
    val nonNegative = minutes.coerceAtLeast(0L)
    return if (nonNegative < 60) "$nonNegative min"
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
