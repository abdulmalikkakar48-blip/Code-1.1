package com.maree.app

import java.time.LocalDate
import java.util.UUID

internal enum class DiceMode { ACTIVITY, APPLICATION, WEEK }

/** Ephemeral result: only a subsequently confirmed calendar task is persisted. */
internal data class DiceResult(
    val mode: DiceMode, val face: Int, val date: LocalDate, val appPackage: String? = null,
    val id: String = UUID.randomUUID().toString()
)

internal data class DiceActivity(val french: String, val english: String)

internal val diceActivities = listOf(
    DiceActivity("Lire cinq pages d’un livre", "Read five pages of a book"),
    DiceActivity("Marcher dix minutes sans écran", "Take a ten-minute screen-free walk"),
    DiceActivity("Ranger un petit coin", "Tidy up one small area"),
    DiceActivity("Écrire trois idées sur papier", "Write three ideas on paper"),
    DiceActivity("Faire deux minutes d’étirements doux", "Do two minutes of gentle stretching"),
    DiceActivity("Prendre cinq minutes au calme et respirer naturellement", "Take five quiet minutes and breathe naturally")
)

internal fun diceWeekDayCount(face: Int): Int {
    require(face in 1..6)
    return (face + 1) / 2
}

/** Pick an index uniformly, independently of the decorative face, for any selection size from 1 to 6. */
internal fun diceSelectedApplication(selected: List<String>, randomIndex: Int): String? =
    selected.takeIf { it.size in 1..6 && it.none(String::isBlank) && it.distinct().size == it.size }
        ?.getOrNull(randomIndex)

internal fun diceWeekDates(today: LocalDate): List<LocalDate> =
    (0L..6L).map { today.plusDays(it) }.filter { it.year in 1900..2200 }

/** Reject stale rolls and partial/out-of-window dates before any calendar write. */
internal fun diceTaskBatch(
    result: DiceResult,
    chosenDates: List<LocalDate>,
    appLabel: String,
    language: AppLocale,
    today: LocalDate
): List<Pair<LocalDate, String>>? {
    if (result.face !in 1..6 || result.date != today || today.year !in 1900..2200) return null
    val english = language == AppLocale.ENGLISH
    val dates: List<LocalDate>
    val title: String
    if (result.mode == DiceMode.ACTIVITY) {
        if (chosenDates.isNotEmpty()) return null
        val activity = diceActivities[result.face - 1]
        title = if (english) "Selkie dice · ${activity.english}" else "Dé Selkie · ${activity.french}"
        dates = listOf(today)
    } else {
        if (result.appPackage.isNullOrBlank()) return null
        val safeLabel = appLabel.map { if (it.isISOControl()) ' ' else it }.joinToString("").trim().take(90)
        if (safeLabel.isBlank()) return null
        if (result.mode == DiceMode.APPLICATION) {
            if (chosenDates.isNotEmpty()) return null
            dates = listOf(today)
            title = if (english) "Selkie dice · 20 minutes without $safeLabel (personal goal)"
                else "Dé Selkie · 20 minutes sans $safeLabel (objectif personnel)"
        } else {
            if (chosenDates.size != diceWeekDayCount(result.face) || chosenDates.distinct().size != chosenDates.size ||
                chosenDates.any { it !in diceWeekDates(today) }) return null
            dates = chosenDates.sorted()
            title = if (english) "Selkie dice · A day without $safeLabel (personal goal)"
                else "Dé Selkie · Une journée sans $safeLabel (objectif personnel)"
        }
    }
    return dates.map { it to title }
}
