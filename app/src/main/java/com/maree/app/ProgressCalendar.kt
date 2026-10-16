package com.maree.app

import android.app.DatePickerDialog
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
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
    val firstMonth = YearMonth.of(1900, 1)
    val lastMonth = YearMonth.of(2200, 12)
    val context = LocalContext.current
    val taskStore = remember(context) { CalendarTaskStore(context) }
    var taskRevision by remember { mutableStateOf(0) }
    var taskDates by remember { mutableStateOf<Set<LocalDate>>(emptySet()) }
    LaunchedEffect(taskRevision) {
        taskDates = withContext(Dispatchers.IO) { taskStore.entries().map { it.date }.toSet() }
    }
    var monthKey by rememberSaveable(today.toString()) { mutableStateOf(YearMonth.from(today).toString()) }
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
        selectedKey = candidate.toString()
    }

    AppCardSurface {
        Surface(color = MareePalette.Lilac, shape = RoundedCornerShape(22.dp)) {
            Row(Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    SectionLabel(tr("Ton calendrier", "Your calendar"))
                    Text(tr("Une place pour tes projets.", "Make room for your plans."),
                        color = MareePalette.Muted, style = MaterialTheme.typography.bodyMedium)
                }
                if (LocalDensity.current.fontScale <= 1.3f) CalendarArtwork(Modifier.size(62.dp))
            }
        }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            TextButton(
                onClick = { showMonth(month.minusMonths(1)) },
                enabled = month > firstMonth,
                modifier = Modifier.width(48.dp).heightIn(min = 48.dp).semantics {
                    contentDescription = tr("Mois précédent", "Previous month")
                }
            ) { Text("‹", modifier = Modifier.clearAndSetSemantics {}) }
            Text(monthLabel, modifier = Modifier.weight(1f), textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold, color = MareePalette.Ink,
                style = MaterialTheme.typography.titleMedium)
            TextButton(
                onClick = { showMonth(month.plusMonths(1)) },
                enabled = month < lastMonth,
                modifier = Modifier.width(48.dp).heightIn(min = 48.dp).semantics {
                    contentDescription = tr("Mois suivant", "Next month")
                }
            ) { Text("›", modifier = Modifier.clearAndSetSemantics {}) }
        }
        CalendarDateActions(onChoose = {
            DatePickerDialog(context, { _, year, monthIndex, day ->
                val chosen = LocalDate.of(year, monthIndex + 1, day)
                if (chosen.year in 1900..2200) {
                    monthKey = YearMonth.from(chosen).toString()
                    selectedKey = chosen.toString()
                }
            }, selectedDate.year, selectedDate.monthValue - 1, selectedDate.dayOfMonth).apply {
                datePicker.minDate = LocalDate.of(1900, 1, 1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                datePicker.maxDate = LocalDate.of(2200, 12, 31).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                show()
            }
        }, onToday = {
            monthKey = YearMonth.from(today).toString()
            selectedKey = today.toString()
        })
        // Seven columns fit normal text on narrow phones. The date picker above provides
        // a full-size alternative; enlarged system text can scroll the grid horizontally.
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val fontScale = LocalDensity.current.fontScale
            val cellWidth = if (fontScale > 1.3f) (maxWidth / 7).coerceAtLeast(44.dp * fontScale)
                else maxWidth / 7
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
                                Box(Modifier.width(cellWidth).heightIn(min = 54.dp))
                            } else {
                                val date = month.atDay(day)
                                key(date.toString()) {
                                    CalendarDateButton(
                                        date = date,
                                        entry = byDate[date],
                                        enabled = date.year in 1900..2200,
                                        hasTask = date in taskDates,
                                        isSelected = date == selectedDate,
                                        isToday = date == today,
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
        Text(tr("○ tâche · • validation · ! limite atteinte · — usage mesuré\nContour violet : aujourd’hui.",
            "○ task · • check-in · ! limit reached · — measured usage\nPurple outline: today."),
            color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall)
        Text(tr("Une case sans marque ne prouve pas une journée sans écran. Choisis une date pour préparer ta journée.",
            "An unmarked day does not prove a screen-free day. Choose a date to plan your day."),
            color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall)
        HorizontalDivider(color = MareePalette.Outline)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Surface(color = MareePalette.Peach, shape = RoundedCornerShape(16.dp)) {
                Text(selectedDate.dayOfMonth.toString(), color = MareePalette.Coral,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        .clearAndSetSemantics {})
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(tr("JOURNÉE SÉLECTIONNÉE", "SELECTED DAY"), color = MareePalette.Lavender,
                    style = MaterialTheme.typography.labelSmall)
                Text(calendarDateLabel(selectedDate).replaceFirstChar { it.titlecase(AppLanguage.locale) },
                    color = MareePalette.Ink, fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium)
            }
        }
        CalendarTaskSection(selectedDate, onTasksChanged = { taskRevision++ })
        HorizontalDivider(color = MareePalette.Outline)
        SectionLabel(tr("Historique du suivi", "Tracking history"))
        if (selectedDate > today) {
            Text(tr("Journée à venir : aucune mesure de suivi pour l’instant.", "Future day: no tracking measurements yet."), color = MareePalette.Muted)
        } else if (selectedEntry == null) {
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
    hasTask: Boolean,
    isSelected: Boolean,
    isToday: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val hasValidation = entry?.validationIds?.isNotEmpty() == true
    val hasBreach = entry?.breachedPackages?.isNotEmpty() == true
    val label = calendarDateLabel(date) + ". " +
        (if (isToday) tr("Aujourd’hui. ", "Today. ") else "") + if (!enabled)
        tr("Hors de l’historique disponible", "Outside the available history")
    else (if (hasTask) tr("Tâches prévues. ", "Planned tasks. ") else "") + calendarEntryStatus(entry)
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.padding(1.dp).heightIn(min = 52.dp).semantics {
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
        border = when {
            isSelected -> BorderStroke(1.dp, MareePalette.Teal)
            isToday -> BorderStroke(1.5.dp, MareePalette.Lavender)
            else -> null
        }
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
            Row(Modifier.heightIn(min = 14.dp), horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically) {
                if (hasTask) Canvas(Modifier.size(6.dp)) {
                    drawCircle(color, style = Stroke(width = 1.2.dp.toPx()))
                }
                if (hasValidation) Canvas(Modifier.size(5.dp)) { drawCircle(color) }
                if (hasBreach) Text("!", color = if (isSelected) color else MareePalette.Danger,
                    style = MaterialTheme.typography.labelSmall)
                if (!hasTask && !hasValidation && !hasBreach && entry?.hasMeasuredUsage == true)
                    Text("—", color = color, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun CalendarDateActions(onChoose: () -> Unit, onToday: () -> Unit) {
    if (LocalDensity.current.fontScale > 1.3f) {
        Column(Modifier.fillMaxWidth()) {
            TextButton(onClick = onChoose, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Text(tr("Choisir une date", "Choose a date"))
            }
            TextButton(onClick = onToday, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Text(tr("Aujourd’hui", "Today"))
            }
        }
    } else Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        TextButton(onClick = onChoose, modifier = Modifier.heightIn(min = 48.dp)) {
            Text(tr("Choisir une date", "Choose a date"))
        }
        TextButton(onClick = onToday, modifier = Modifier.heightIn(min = 48.dp)) {
            Text(tr("Aujourd’hui", "Today"))
        }
    }
}

/** Small original paper-calendar illustration; purely decorative. */
@Composable
private fun CalendarArtwork(modifier: Modifier = Modifier) {
    val lilac = MareePalette.Lavender
    val surface = MareePalette.Surface
    val peach = MareePalette.Coral
    val mint = MareePalette.Mint
    Canvas(modifier.clearAndSetSemantics {}) {
        val unit = size.minDimension / 62f
        drawCircle(mint, 24f * unit, Offset(33f * unit, 33f * unit))
        drawRoundRect(lilac.copy(alpha = .16f), Offset(10f * unit, 14f * unit),
            Size(44f * unit, 45f * unit), CornerRadius(9f * unit))
        drawRoundRect(surface, Offset(6f * unit, 8f * unit), Size(44f * unit, 45f * unit), CornerRadius(9f * unit))
        drawRoundRect(lilac, Offset(6f * unit, 8f * unit), Size(44f * unit, 13f * unit), CornerRadius(7f * unit))
        listOf(17f, 39f).forEach { x ->
            drawLine(peach, Offset(x * unit, 4f * unit), Offset(x * unit, 13f * unit), 3f * unit,
                cap = androidx.compose.ui.graphics.StrokeCap.Round)
        }
        repeat(3) { row -> repeat(3) { column ->
            val point = Offset((17f + 11f * column) * unit, (29f + 8f * row) * unit)
            drawCircle(if (row == 1 && column == 1) peach else lilac.copy(alpha = .42f),
                if (row == 1 && column == 1) 3.7f * unit else 2f * unit, point)
        } }
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
