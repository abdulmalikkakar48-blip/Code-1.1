package com.maree.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/** Bilan des compteurs disponibles, sans historique ni temps gagné inventé. */
@Composable
@OptIn(ExperimentalFoundationApi::class)
internal fun DailyTab(
    snapshot: MareeSnapshot,
    onCoach: () -> Unit,
    getName: (String) -> String,
    onAddApp: () -> Unit,
    monitoringActive: Boolean,
    onActivateTracking: () -> Unit,
    history: List<DailyHistoryEntry> = emptyList(),
    onOffers: () -> Unit = {},
    onToolsChanged: () -> Unit = {},
    onProgramValidated: (String) -> Unit = {},
    section: String = "today",
    onSectionChange: (String) -> Unit = {},
    honorPendingId: String? = null,
    onHonorPendingIdChange: (String?) -> Unit = {}
) {
    val today = LocalDate.now()
    val tips = dailyTips
    val tip = tips[Math.floorMod(today.toEpochDay(), tips.size.toLong()).toInt()]
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
    val scrollState = rememberScrollState()
    val selectedSection = remember { BringIntoViewRequester() }
    LaunchedEffect(section) {
        scrollState.scrollTo(0)
        selectedSection.bringIntoView()
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        if (section !in setOf("dice", "calendar", "coloring")) ScreenHeading(
            eyebrow = today.format(DateTimeFormatter.ofPattern(tr("EEEE d MMMM", "EEEE, MMMM d"), AppLanguage.locale))
                .replaceFirstChar { it.titlecase(AppLanguage.locale) },
            title = tr("Un peu plus de vie.", "A little more life."),
            description = tr("Une pause, un repère, un petit pas à la fois.", "A break, a fresh perspective, one small step at a time.")
        )
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("today" to tr("Jour", "Day"), "calendar" to tr("Calendrier", "Calendar"),
                "dice" to tr("Dé", "Dice"), "coloring" to tr("Coloriage", "Coloring"),
                "reading" to tr("Lecture", "Reading"),
                "week" to tr("Bilan", "Review"),
                "tools" to tr("Outils", "Tools")).forEach { (id, label) ->
                FilterChip(selected = section == id, onClick = { onSectionChange(id) },
                    modifier = if (section == id) Modifier.bringIntoViewRequester(selectedSection) else Modifier,
                    label = { Text(label) })
            }
        }
        if (section == "today") {
        AppCardSurface(containerColor = MareePalette.Mint) {
            SectionLabel(tr("Choisis ta pause", "Choose your break"))
            Text(tr("Une idée ou un dessin ?", "An idea or a drawing?"),
                color = MareePalette.Ink, fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp)
            Text(tr("Laisse le dé proposer une pause ou prends quelques minutes pour colorier. Gratuit, sans points à gagner.",
                "Let the dice suggest a break or take a few minutes to color. Free, with no points to earn."),
                color = MareePalette.Muted)
            PrimaryAction(tr("Lancer le dé Selkie", "Roll the Selkie dice"), onClick = { onSectionChange("dice") },
                modifier = Modifier.fillMaxWidth())
            OutlinedButton(onClick = { onSectionChange("coloring") }, modifier = Modifier.fillMaxWidth()) {
                Text(tr("Coloriage · ouvrir les dessins", "Coloring · open the drawings"))
            }
            Text(tr("${illustrationPages.size} illustrations de fleurs, paysages et motifs, plus ${coloringTemplates.size} pages à carreaux, à colorier ici avec sauvegarde sur ce téléphone.",
                "${illustrationPages.size} flower, landscape and pattern illustrations, plus ${coloringTemplates.size} grid pages to color right here, saved on this phone."),
                color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 19.sp)
        }
        AppCardSurface {
            SectionLabel(tr("Ta pause lecture", "Your reading break"))
            Text(tr("${readingResources.size} textes courts sur l’apprentissage, l’observation et les habitudes, à lire hors ligne.",
                "${readingResources.size} short texts on learning, observation and habits, available offline."), color = MareePalette.Muted)
            PrimaryAction(tr("Découvrir les lectures", "Explore readings"), onClick = { onSectionChange("reading") })
        }

        AppCardSurface(containerColor = MareePalette.Mint) {
            SectionLabel(tr("Ta pause du jour", "Your daily pause"))
            Text(tip.first, color = MareePalette.Ink, fontWeight = FontWeight.Bold,
                fontSize = 22.sp, lineHeight = 28.sp)
            Text(tip.second, color = MareePalette.Muted, fontSize = 16.sp, lineHeight = 24.sp)
            Text(tr("Une idée à essayer, sans validation ni points.", "An idea to try, with no check-ins or points."),
                color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 19.sp)
        }
        if (!monitoringActive) {
            AppCardSurface {
                StatusPill(tr("Suivi en pause", "Tracking paused"))
                Text(tr("Tes repères t’attendent", "Your progress is waiting"), fontWeight = FontWeight.Bold,
                    fontSize = 20.sp, color = MareePalette.Ink)
                Text(tr("Active le suivi pour actualiser tes durées. Les conseils et les défis restent disponibles.", "Turn on tracking to update your usage times. Tips and challenges are still available."),
                    color = MareePalette.Muted, fontSize = 15.sp, lineHeight = 22.sp)
                PrimaryAction(tr("Configurer le suivi", "Set up tracking"), onClick = onActivateTracking)
            }
        } else if (snapshot.apps.isEmpty()) {
            AppCardSurface {
                SectionLabel(tr("Ton premier repère", "Your first step"))
                Text(tr("Commence avec une application", "Start with one app"), fontWeight = FontWeight.Bold,
                    fontSize = 20.sp, color = MareePalette.Ink)
                Text(tr("Choisis celle que tu souhaites moins consulter. Tu retrouveras son compteur ici.", "Choose an app you want to check less often. Its usage counter will appear here."),
                    color = MareePalette.Muted, fontSize = 15.sp, lineHeight = 22.sp)
                PrimaryAction(tr("Choisir une application", "Choose an app"), onClick = onAddApp,
                    enabled = snapshot.points > 0)
                if (snapshot.points <= 0) {
                    Text(tr("Il te faut des points pour ajouter une application.", "You need points to add an app."),
                        color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 19.sp)
                }
            }
        } else {
            AppCardSurface {
                SectionLabel(tr("Tes compteurs", "Your usage counters"))
                Text(durationLabel(usedMillis / 60_000L), color = MareePalette.Teal,
                    fontWeight = FontWeight.Bold, fontSize = 36.sp, lineHeight = 42.sp)
                Text(tr("Sur ${snapshot.apps.size} application${if (snapshot.apps.size > 1) "s" else ""} suivie${if (snapshot.apps.size > 1) "s" else ""}", "Across ${snapshot.apps.size} tracked app${if (snapshot.apps.size > 1) "s" else ""}"),
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
                Text(tr("${durationLabel(limitMinutes)} de limites cumulées", "${durationLabel(limitMinutes)} in combined limits"),
                    color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp)
                Text(tr("$withinLimit sous la limite · $lockedCount verrouillée${if (lockedCount > 1) "s" else ""}", "$withinLimit under the limit · $lockedCount locked"),
                    color = MareePalette.Ink, fontWeight = FontWeight.Medium,
                    fontSize = 14.sp, lineHeight = 21.sp)
                TextButton(onClick = { showCounterDetails = !showCounterDetails }) {
                    Text(if (showCounterDetails) tr("Masquer l’explication", "Hide explanation") else tr("Que mesure ce bilan ?", "What does this summary measure?"))
                }
                if (showCounterDetails) {
                    Text(tr("Ce bilan additionne les compteurs de tes applications suivies. Il ne mesure pas tout ton temps d’écran. Ajouter une application, réinitialiser sa limite ou la débloquer remet son compteur à zéro.", "This summary adds up the counters for your tracked apps. It does not measure all your screen time. Adding an app, resetting its limit or unlocking it resets its counter to zero."),
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
                            Text(if (app.isLocked) tr("En pause · application verrouillée", "Paused · app locked") else tr("Limite atteinte", "Limit reached"),
                                color = MareePalette.Danger, fontSize = 13.sp, lineHeight = 19.sp)
                        }
                    }
                }
                if (snapshot.apps.size > 3) {
                    TextButton(onClick = { showAllApps = !showAllApps }) {
                        Text(if (showAllApps) tr("Réduire la liste", "Show fewer apps") else tr("Voir les ${snapshot.apps.size} applications", "See all ${snapshot.apps.size} apps"))
                    }
                }
            }
        }
        AppCardSurface {
            SectionLabel(tr("Tes petits pas", "Your small steps"))
            Text(tr("$validatedToday défi${if (validatedToday > 1) "s" else ""} validé${if (validatedToday > 1) "s" else ""} aujourd’hui", "$validatedToday challenge${if (validatedToday != 1) "s" else ""} checked in today"),
                color = MareePalette.Ink, fontWeight = FontWeight.Bold,
                fontSize = 20.sp, lineHeight = 27.sp)
            if (snapshot.customChallenges.isNotEmpty()) {
                Text(tr("$personalValidationsToday défi${if (personalValidationsToday > 1) "s" else ""} personnel${if (personalValidationsToday > 1) "s" else ""} validé${if (personalValidationsToday > 1) "s" else ""} aujourd’hui", "$personalValidationsToday custom challenge${if (personalValidationsToday != 1) "s" else ""} checked in today"),
                    color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp)
            }
            HorizontalDivider(color = MareePalette.Outline)
            Text(tr("${snapshot.checkedChallenges.size} / 10 défis terminés dans ta série", "${snapshot.checkedChallenges.size} / 10 challenges completed in your set"),
                color = MareePalette.Teal, fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp, lineHeight = 22.sp)
            Text(tr("${snapshot.totalCompletedChallenges} terminé${if (snapshot.totalCompletedChallenges > 1) "s" else ""} au total", "${snapshot.totalCompletedChallenges} completed in total"),
                color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp)
            Text(tr("Chaque défi de la série demande $CHALLENGE_REQUIRED_DAYS journées. Elles peuvent être espacées.", "Each challenge in the set takes $CHALLENGE_REQUIRED_DAYS days. These do not have to be consecutive."),
                color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 19.sp)
        }
        AppCardSurface {
            Text(tr("Besoin d’un coup de pouce ?", "Need a little help?"), color = MareePalette.Ink,
                fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 27.sp)
            Text(tr("Pose ta question sur tes habitudes ou sur Selkie : conseils et aide disponibles hors ligne.", "Ask about your habits or Selkie: tips and app help are available offline."),
                color = MareePalette.Muted, fontSize = 15.sp, lineHeight = 22.sp)
            PrimaryAction(tr("Ouvrir le coach", "Open the coach"), onClick = onCoach)
        }
        }
        when (section) {
            "dice" -> DiceSection(snapshot, getName,
                onCalendar = { onSectionChange("calendar") }, onTasksChanged = onToolsChanged,
                honorPendingId = honorPendingId, onHonorPendingIdChange = onHonorPendingIdChange)
            "coloring" -> ColoringSection()
            "reading" -> ReadingSection()
            "calendar" -> ProgressCalendarSection(history)
            "week" -> WeeklyReviewSection(history, snapshot.plan, getName, onOffers)
            "tools" -> {
                LimitSchedulesSection(snapshot, getName, onOffers, onToolsChanged)
                GuidedProgramsSection(snapshot.plan, onOffers, onProgramValidated)
                HomeWidgetSection(snapshot.plan, onOffers)
            }
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

private val dailyTips: List<Pair<String, String>> get() = listOf(
    tr("Fais une vraie pause", "Take a real break") to tr("Pose ton téléphone hors de portée pendant dix minutes. Bois un verre d’eau, regarde dehors ou ne fais simplement rien.", "Put your phone out of reach for ten minutes. Have a glass of water, look outside or simply do nothing."),
    tr("Choisis avant d’ouvrir", "Decide before opening") to tr("Avant de lancer une application, demande-toi ce que tu veux y faire. Quand c’est fait, referme-la.", "Before opening an app, ask yourself what you want to do there. Once you are done, close it."),
    tr("Allège tes notifications", "Reduce your notifications") to tr("Choisis une application qui te sollicite trop et coupe ses notifications non essentielles dans les réglages de ton téléphone.", "Choose an app that interrupts you too often and turn off its non-essential notifications in your phone settings."),
    tr("Garde un repas pour toi", "Enjoy a phone-free meal") to tr("À ton prochain repas, laisse ton téléphone dans une autre pièce. Profite de ce que tu manges et de la conversation.", "At your next meal, leave your phone in another room. Enjoy your food and the conversation."),
    tr("Prends l’air", "Get some fresh air") to tr("Accorde-toi une petite promenade sans consulter ton écran. Regarde ce qui t’entoure, à ton rythme.", "Take a short walk without checking your screen. Notice what is around you, at your own pace."),
    tr("Prépare une alternative", "Keep an alternative ready") to tr("Place un livre, un carnet ou une activité que tu aimes à côté de toi. Tu auras une autre option quand l’envie de scroller revient.", "Keep a book, a notebook or an activity you enjoy nearby. You will have another option when the urge to scroll returns."),
    tr("Préserve ton repos", "Protect your rest") to tr("Ce soir, choisis un endroit pour poser ton téléphone loin de ton lit. Une petite distance peut aider à fermer l’écran.", "Tonight, choose a spot for your phone away from your bed. A little distance can make it easier to put the screen away.")
)
