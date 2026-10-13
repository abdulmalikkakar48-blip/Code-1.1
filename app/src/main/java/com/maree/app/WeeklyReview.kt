package com.maree.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

internal data class WeeklyHistorySummary(
    val start: LocalDate,
    val end: LocalDate,
    val measuredDays: Int,
    val observationMillis: Long,
    val usageMillis: Map<String, Long>,
    val validationCount: Int,
    val breachDays: Int
) {
    val totalUsageMillis: Long get() = usageMillis.values.fold(0L, ::addHistoryDuration)
}

/** Inclusive seven local dates; duplicate dates use the latest supplied record. */
internal fun summarizeHistoryWeek(entries: List<DailyHistoryEntry>, end: LocalDate): WeeklyHistorySummary {
    val start = end.minusDays(6)
    val days = entries.asSequence().filter { it.date in start..end }
        .associateBy { it.date }.values
    val usage = linkedMapOf<String, Long>()
    var observation = 0L
    days.filter { it.hasMeasuredUsage }.forEach { day ->
        observation = addHistoryDuration(observation, day.observedMillis)
        day.usageMillis.forEach { (packageName, duration) ->
            usage[packageName] = addHistoryDuration(usage[packageName] ?: 0L, duration)
        }
    }
    return WeeklyHistorySummary(
        start = start,
        end = end,
        measuredDays = days.count { it.hasMeasuredUsage },
        observationMillis = observation,
        usageMillis = usage.toMap(),
        validationCount = days.sumOf { it.validationIds.size },
        breachDays = days.count { it.breachedPackages.isNotEmpty() }
    )
}

private fun addHistoryDuration(left: Long, right: Long): Long {
    val value = right.coerceAtLeast(0L)
    return if (left > Long.MAX_VALUE - value) Long.MAX_VALUE else left + value
}

/** The free summary stays useful; Premium and Ultra add recorded detail and comparison. */
@Composable
internal fun WeeklyReviewSection(
    entries: List<DailyHistoryEntry>,
    plan: PlanType,
    getName: (String) -> String,
    onOffers: () -> Unit
) {
    val today = LocalDate.now()
    val current = remember(entries, today) { summarizeHistoryWeek(entries, today) }
    val previous = remember(entries, today) { summarizeHistoryWeek(entries, today.minusDays(7)) }
    var showAllApps by rememberSaveable { mutableStateOf(false) }
    val detailed = plan != PlanType.FREE

    AppCardSurface {
        SectionLabel(tr("Ton bilan de la semaine", "Your weekly review"))
        Text(weekRangeLabel(current), color = MareePalette.Muted,
            style = MaterialTheme.typography.bodySmall)
        Text(tr("${current.measuredDays} / 7 journées avec mesure d’usage",
            "${current.measuredDays} / 7 days with usage measurements"),
            color = MareePalette.Ink, fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleMedium)
        if (current.measuredDays > 0) {
            Text(tr("Usage enregistré : ${weekDurationLabel(current.totalUsageMillis)}",
                "Recorded usage: ${weekDurationLabel(current.totalUsageMillis)}"),
                color = MareePalette.Teal, fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall)
            Text(tr("Durée de suivi observée : ${weekDurationLabel(current.observationMillis)}",
                "Observed tracking time: ${weekDurationLabel(current.observationMillis)}"),
                color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall)
        } else {
            Text(tr("Aucune mesure d’usage disponible sur cette période.",
                "No usage measurements available for this period."),
                color = MareePalette.Muted, style = MaterialTheme.typography.bodyMedium)
        }
        Text(tr("${current.validationCount} validation${if (current.validationCount > 1) "s" else ""} enregistrée${if (current.validationCount > 1) "s" else ""}",
            "${current.validationCount} recorded check-in${if (current.validationCount != 1) "s" else ""}"),
            color = MareePalette.Ink, style = MaterialTheme.typography.bodyMedium)
        if (current.breachDays > 0) Text(
            tr("Limite atteinte dans les relevés de ${current.breachDays} journée${if (current.breachDays > 1) "s" else ""}.",
                "A limit was reached in records from ${current.breachDays} day${if (current.breachDays != 1) "s" else ""}."),
            color = MareePalette.Danger, style = MaterialTheme.typography.bodySmall
        )
        Text(tr("Les mesures couvrent seulement les périodes observées et les applications suivies. Les journées sans mesure restent inconnues ; aujourd’hui peut être incomplet.",
            "Measurements cover only observed periods and tracked apps. Days without measurements remain unknown; today may be incomplete."),
            color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall)

        HorizontalDivider(color = MareePalette.Outline)
        if (detailed) {
            SectionLabel(tr("Par application", "By app"))
            val apps = current.usageMillis.entries.sortedWith(
                compareByDescending<Map.Entry<String, Long>> { it.value }.thenBy { it.key }
            )
            if (apps.isEmpty()) {
                Text(tr("Aucun relevé par application sur cette période.",
                    "No app records for this period."),
                    color = MareePalette.Muted, style = MaterialTheme.typography.bodyMedium)
            } else {
                val visibleApps = if (showAllApps) apps else apps.take(5)
                visibleApps.forEach { (packageName, duration) ->
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(getName(packageName), color = MareePalette.Ink,
                            fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                        Text(tr("${weekDurationLabel(duration)} enregistrées", "${weekDurationLabel(duration)} recorded"),
                            color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall)
                    }
                }
                if (apps.size > 5) TextButton(onClick = { showAllApps = !showAllApps }) {
                    Text(if (showAllApps) tr("Réduire la liste", "Show fewer apps")
                    else tr("Voir les ${apps.size} applications", "See all ${apps.size} apps"))
                }
            }
            HorizontalDivider(color = MareePalette.Outline)
            SectionLabel(tr("Les 7 jours précédents", "The previous 7 days"))
            Text(weekRangeLabel(previous), color = MareePalette.Muted,
                style = MaterialTheme.typography.bodySmall)
            WeeklyComparisonRow(tr("Cette période", "This period"), current)
            WeeklyComparisonRow(tr("Période précédente", "Previous period"), previous)
            if (current.measuredDays > 0 && previous.measuredDays > 0) {
                val difference = current.totalUsageMillis - previous.totalUsageMillis
                val sign = if (difference > 0) "+" else if (difference < 0) "−" else ""
                val absolute = if (difference < 0) -difference else difference
                Text(tr("Écart entre les relevés : $sign${weekDurationLabel(absolute)}",
                    "Difference between records: $sign${weekDurationLabel(absolute)}"),
                    color = MareePalette.Ink, style = MaterialTheme.typography.bodyMedium)
            } else {
                Text(tr("Il manque des mesures sur au moins une période pour calculer un écart.",
                    "At least one period lacks measurements to calculate a difference."),
                    color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall)
            }
            Text(tr("Les durées de suivi et les applications peuvent varier entre les deux périodes. Ces totaux ne décrivent pas des journées entières.",
                "Tracking time and apps may differ between periods. These totals do not describe complete days."),
                color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall)
        } else {
            Text(tr("Avec Premium ou Ultra : retrouve le détail par application et compare les relevés des deux dernières semaines.",
                "With Premium or Ultra: see app details and compare records from the last two weeks."),
                color = MareePalette.Muted, style = MaterialTheme.typography.bodyMedium)
            OutlinedButton(onClick = onOffers, modifier = Modifier.fillMaxWidth()) {
                Text(tr("Découvrir les offres", "Explore plans"))
            }
        }
    }
}

@Composable
private fun WeeklyComparisonRow(label: String, summary: WeeklyHistorySummary) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(label, color = MareePalette.Ink, fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium)
        Text(if (summary.measuredDays > 0)
            tr("${weekDurationLabel(summary.totalUsageMillis)} d’usage enregistré · ${summary.measuredDays} / 7 journées mesurées",
                "${weekDurationLabel(summary.totalUsageMillis)} recorded usage · ${summary.measuredDays} / 7 measured days")
        else tr("Aucune mesure d’usage", "No usage measurements"),
            color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall)
        if (summary.measuredDays > 0) Text(
            tr("${weekDurationLabel(summary.observationMillis)} de suivi observé",
                "${weekDurationLabel(summary.observationMillis)} of observed tracking"),
            color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall
        )
    }
}

private fun weekRangeLabel(summary: WeeklyHistorySummary): String {
    val format = DateTimeFormatter.ofPattern(tr("d MMM yyyy", "MMM d, yyyy"), AppLanguage.locale)
    return "${summary.start.format(format)} – ${summary.end.format(format)}"
}

private fun weekDurationLabel(millis: Long): String {
    val duration = millis.coerceAtLeast(0L)
    if (duration in 1..59_999L) return "< 1 min"
    val minutes = duration / 60_000L
    return when {
        minutes < 60 -> "$minutes min"
        minutes % 60 == 0L -> "${minutes / 60} h"
        else -> "${minutes / 60} h ${minutes % 60} min"
    }
}
