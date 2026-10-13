package com.maree.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle

/** Recorded progress only: missing usage coverage never becomes a successful day. */
@Composable
internal fun ProgressCalendarSection(entries: List<DailyHistoryEntry>) {
    val today = LocalDate.now()
    val earliest = today.minusDays(89)
    val firstMonth = YearMonth.from(earliest)
    val lastMonth = YearMonth.from(today)
    var monthKey by rememberSaveable(today.toString()) { mutableStateOf(lastMonth.toString()) }
    var selectedKey by rememberSaveable(today.toString()) { mutableStateOf(today.toString()) }
    val month = YearMonth.parse(monthKey)
    val selectedDate = LocalDate.parse(selectedKey)
    val byDate = remember(entries) { entries.associateBy { it.date } }
    val selectedEntry = byDate[selectedDate]
    val monthLabel = month.format(DateTimeFormatter.ofPattern("MMMM yyyy", AppLanguage.locale))
        .replaceFirstChar { it.titlecase(AppLanguage.locale) }

    fun showMonth(nextMonth: YearMonth) {
        val candidate = nextMonth.atDay(selectedDate.dayOfMonth.coerceAtMost(nextMonth.lengthOfMonth()))
        monthKey = nextMonth.toString()
        selectedKey = candidate.coerceIn(earliest, today).toString()
    }

    AppCardSurface {
        SectionLabel(tr("Ton calendrier de progrès", "Your progress calendar"))
        Text(
            tr("Tes validations et tes mesures sur les 90 derniers jours. Les journées sans mesure restent inconnues.",
                "Your check-ins and measurements over the last 90 days. Days without measurements remain unknown."),
            color = MareePalette.Muted, style = MaterialTheme.typography.bodyMedium
        )
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            TextButton(
                onClick = { showMonth(month.minusMonths(1)) },
                enabled = month > firstMonth,
                modifier = Modifier.semantics {
                    contentDescription = tr("Mois précédent", "Previous month")
                }
            ) { Text("‹", modifier = Modifier.clearAndSetSemantics {}) }
            Text(monthLabel, modifier = Modifier.weight(1f), textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold, color = MareePalette.Ink,
                style = MaterialTheme.typography.titleMedium)
            TextButton(
                onClick = { showMonth(month.plusMonths(1)) },
                enabled = month < lastMonth,
                modifier = Modifier.semantics {
                    contentDescription = tr("Mois suivant", "Next month")
                }
            ) { Text("›", modifier = Modifier.clearAndSetSemantics {}) }
        }
        // Keep date targets at least 48 dp wide, including on a narrow phone or large text.
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val cellWidth = (maxWidth / 7).coerceAtLeast(52.dp)
            Column(Modifier.horizontalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row {
                    DayOfWeek.values().forEach { weekday ->
                        Text(weekday.getDisplayName(TextStyle.SHORT, AppLanguage.locale),
                            modifier = Modifier.width(cellWidth).padding(vertical = 6.dp)
                                .clearAndSetSemantics {
                                    contentDescription = weekday.getDisplayName(TextStyle.FULL, AppLanguage.locale)
                                },
                            textAlign = TextAlign.Center, color = MareePalette.Muted,
                            style = MaterialTheme.typography.labelSmall)
                    }
                }
                val leading = month.atDay(1).dayOfWeek.value - 1
                val weekCount = (leading + month.lengthOfMonth() + 6) / 7
                repeat(weekCount) { week ->
                    Row {
                        repeat(7) { column ->
                            val day = week * 7 + column - leading + 1
                            if (day !in 1..month.lengthOfMonth()) {
                                Box(Modifier.width(cellWidth).heightIn(min = 60.dp))
                            } else {
                                val date = month.atDay(day)
                                key(date.toString()) {
                                    CalendarDateButton(
                                        date = date,
                                        entry = byDate[date],
                                        enabled = date in earliest..today,
                                        isSelected = date == selectedDate,
                                        modifier = Modifier.width(cellWidth),
                                        onClick = { selectedKey = date.toString() }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        Text(tr("• validation · ! limite atteinte · — usage mesuré · ? aucune mesure",
            "• check-in · ! limit reached · — measured usage · ? no measurement"),
            color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall)
        HorizontalDivider(color = MareePalette.Outline)
        Text(calendarDateLabel(selectedDate).replaceFirstChar { it.titlecase(AppLanguage.locale) },
            color = MareePalette.Ink, fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleMedium)
        if (selectedEntry == null) {
            Text(tr("Aucune donnée enregistrée pour cette journée.", "No data recorded for this day."),
                color = MareePalette.Muted, style = MaterialTheme.typography.bodyMedium)
        } else {
            if (selectedEntry.hasMeasuredUsage) {
                Text(tr("Usage enregistré : ${calendarDurationLabel(selectedEntry.totalUsageMillis)}",
                    "Recorded usage: ${calendarDurationLabel(selectedEntry.totalUsageMillis)}"),
                    color = MareePalette.Teal, fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyLarge)
                Text(tr("Durée de suivi observée : ${calendarDurationLabel(selectedEntry.observedMillis)}",
                    "Observed tracking time: ${calendarDurationLabel(selectedEntry.observedMillis)}"),
                    color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall)
            } else {
                Text(tr("Aucune mesure d’usage enregistrée.", "No usage measurement recorded."),
                    color = MareePalette.Muted, style = MaterialTheme.typography.bodyMedium)
            }
            Text(calendarValidationLabel(selectedEntry.validationIds.size),
                color = MareePalette.Ink, style = MaterialTheme.typography.bodyMedium)
            val breachCount = selectedEntry.breachedPackages.size
            Text(if (breachCount > 0)
                tr("Limite atteinte sur $breachCount application${if (breachCount > 1) "s" else ""} dans les relevés.",
                    "Limit reached on $breachCount app${if (breachCount != 1) "s" else ""} in the records.")
            else tr("Aucune limite atteinte dans les relevés disponibles.",
                "No limit reached in the available records."),
                color = if (breachCount > 0) MareePalette.Danger else MareePalette.Muted,
                style = MaterialTheme.typography.bodySmall)
            if (selectedEntry.paused) Text(
                tr("Une pause du suivi a été enregistrée ce jour-là.", "A tracking pause was recorded on this day."),
                color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall
            )
        }
        Text(tr("Les relevés peuvent couvrir une partie de la journée et seulement les applications suivies. Une absence de mesure ne signifie pas zéro usage.",
            "Records may cover part of the day and only tracked apps. No measurement does not mean zero usage."),
            color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun CalendarDateButton(
    date: LocalDate,
    entry: DailyHistoryEntry?,
    enabled: Boolean,
    isSelected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val hasValidation = entry?.validationIds?.isNotEmpty() == true
    val hasBreach = entry?.breachedPackages?.isNotEmpty() == true
    val marker = when {
        !enabled -> ""
        hasValidation && hasBreach -> "• !"
        hasBreach -> "!"
        hasValidation -> "•"
        entry?.hasMeasuredUsage == true -> "—"
        else -> "?"
    }
    val label = calendarDateLabel(date) + ". " + if (!enabled)
        tr("Hors de l’historique disponible", "Outside the available history")
    else calendarEntryStatus(entry)
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.padding(2.dp).heightIn(min = 56.dp).semantics {
            contentDescription = label
            selected = isSelected
            role = Role.Button
        },
        shape = RoundedCornerShape(12.dp),
        color = when {
            isSelected -> MareePalette.Teal
            hasValidation && enabled -> MareePalette.Mint
            else -> MareePalette.Surface
        },
        border = if (isSelected) BorderStroke(1.dp, MareePalette.Teal) else null
    ) {
        Column(Modifier.padding(vertical = 7.dp).clearAndSetSemantics {},
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)) {
            val color = when {
                !enabled -> MareePalette.Muted
                isSelected -> MareePalette.OnPrimary
                else -> MareePalette.Ink
            }
            Text(date.dayOfMonth.toString(), color = color,
                style = MaterialTheme.typography.labelLarge)
            Text(marker, color = if (hasBreach && !isSelected && enabled) MareePalette.Danger else color,
                style = MaterialTheme.typography.labelSmall)
        }
    }
}

private fun calendarDateLabel(date: LocalDate): String =
    date.format(DateTimeFormatter.ofPattern(tr("EEEE d MMMM yyyy", "EEEE, MMMM d, yyyy"), AppLanguage.locale))

private fun calendarEntryStatus(entry: DailyHistoryEntry?): String {
    if (entry == null) return tr("Aucune donnée enregistrée", "No data recorded")
    val status = mutableListOf(
        if (entry.hasMeasuredUsage) tr("Usage mesuré sur une période observée", "Usage measured during an observed period")
        else tr("Aucune mesure d’usage", "No usage measurement"),
        calendarValidationLabel(entry.validationIds.size)
    )
    if (entry.breachedPackages.isNotEmpty()) status += tr("Limite atteinte enregistrée", "Limit reached recorded")
    if (entry.paused) status += tr("Pause du suivi enregistrée", "Tracking pause recorded")
    return status.joinToString(". ")
}

private fun calendarValidationLabel(count: Int): String =
    tr("$count validation${if (count > 1) "s" else ""} enregistrée${if (count > 1) "s" else ""}",
        "$count recorded check-in${if (count != 1) "s" else ""}")

private fun calendarDurationLabel(millis: Long): String {
    val duration = millis.coerceAtLeast(0L)
    if (duration in 1..59_999L) return "< 1 min"
    val minutes = duration / 60_000L
    return when {
        minutes < 60 -> "$minutes min"
        minutes % 60 == 0L -> "${minutes / 60} h"
        else -> "${minutes / 60} h ${minutes % 60} min"
    }
}
