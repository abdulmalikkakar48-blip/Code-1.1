package com.maree.app

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class DiceGoalsTest {
    private val today = LocalDate.of(2028, 2, 27)

    @Test fun sixActivityFacesGiveSixDistinctOfflineTasksInEachLanguage() {
        for (language in AppLocale.entries) {
            val titles = (1..6).map { face ->
                val tasks = diceTaskBatch(DiceResult(DiceMode.ACTIVITY, face, today), emptyList(), "", language, today)!!
                assertEquals(today, tasks.single().first)
                tasks.single().second
            }
            assertEquals(6, titles.distinct().size)
            assertTrue(titles.all { it.length <= 240 })
        }
        assertTrue(diceTaskBatch(DiceResult(DiceMode.ACTIVITY, 1, today), emptyList(), "", AppLocale.FRENCH, today)!!.single().second.contains("cinq pages"))
        assertTrue(diceTaskBatch(DiceResult(DiceMode.ACTIVITY, 6, today), emptyList(), "", AppLocale.ENGLISH, today)!!.single().second.contains("breathe naturally"))
    }

    @Test fun weeklyFacesMapToOnlyOneTwoOrThreeDays() {
        assertEquals(listOf(1, 1, 2, 2, 3, 3), (1..6).map(::diceWeekDayCount))
        for (invalid in listOf(0, 7, -1)) assertThrows(IllegalArgumentException::class.java) { diceWeekDayCount(invalid) }
    }

    @Test fun rollingSevenDaysIncludesTodayAndHandlesLeapDayAndNewYear() {
        assertEquals(7, diceWeekDates(today).size)
        assertEquals(today, diceWeekDates(today).first())
        assertEquals(LocalDate.of(2028, 3, 4), diceWeekDates(today).last())
        assertTrue(LocalDate.of(2028, 2, 29) in diceWeekDates(today))
        assertEquals(LocalDate.of(2029, 1, 6), diceWeekDates(LocalDate.of(2028, 12, 31)).last())
        assertEquals(listOf(LocalDate.of(2200, 12, 31)), diceWeekDates(LocalDate.of(2200, 12, 31)))
    }

    @Test fun weekRejectsWrongCountDuplicateOutOfWindowAndStaleDates() {
        val result = DiceResult(DiceMode.WEEK, 3, today, "app.one")
        fun batch(dates: List<LocalDate>, now: LocalDate = today) = diceTaskBatch(result, dates, "Videos", AppLocale.ENGLISH, now)
        assertNull(batch(listOf(today)))
        assertNull(batch(listOf(today, today)))
        assertNull(batch(listOf(today.minusDays(1), today)))
        assertNull(batch(listOf(today, today.plusDays(7))))
        assertNull(batch(listOf(today, today.plusDays(1)), today.plusDays(1)))
        val valid = batch(listOf(today.plusDays(6), today))!!
        assertEquals(listOf(today, today.plusDays(6)), valid.map { it.first })
        assertTrue(valid.all { it.second.contains("A day without Videos") })
    }

    @Test fun applicationSelectionUsesOnlyActualChoicesWithoutInventedSlots() {
        for (size in 1..6) {
            val selected = (1..size).map { "app.$it" }
            assertEquals(selected, selected.indices.map { diceSelectedApplication(selected, it) })
            assertNull(diceSelectedApplication(selected, -1))
            assertNull(diceSelectedApplication(selected, size))
        }
        assertNull(diceSelectedApplication(emptyList(), 0))
        assertNull(diceSelectedApplication(List(7) { "app.$it" }, 0))
        assertNull(diceSelectedApplication(listOf("same", "same"), 0))
        assertNull(diceSelectedApplication(listOf(" "), 0))
    }

    @Test fun applicationGoalIsTodayOnlyAndBoundsUntrustedDisplayLabels() {
        val result = DiceResult(DiceMode.APPLICATION, 6, today, "app.one")
        val task = diceTaskBatch(result, emptyList(), "X\n".repeat(300), AppLocale.FRENCH, today)!!.single()
        assertEquals(today, task.first)
        assertTrue(task.second.contains("20 minutes"))
        assertTrue(task.second.length <= 240)
        assertFalse(task.second.contains('\n'))
        assertNull(diceTaskBatch(result, listOf(today), "App", AppLocale.FRENCH, today))
        assertNull(diceTaskBatch(result, emptyList(), " ", AppLocale.FRENCH, today))
        assertNull(diceTaskBatch(result.copy(appPackage = null), emptyList(), "App", AppLocale.FRENCH, today))
    }

    @Test fun invalidActivityFacesAndAccidentalDatesCannotCreateTasks() {
        for (face in listOf(0, 7)) assertNull(diceTaskBatch(DiceResult(DiceMode.ACTIVITY, face, today), emptyList(), "", AppLocale.FRENCH, today))
        assertNull(diceTaskBatch(DiceResult(DiceMode.ACTIVITY, 1, today), listOf(today), "", AppLocale.FRENCH, today))
        assertNull(diceTaskBatch(DiceResult(DiceMode.ACTIVITY, 1, today), emptyList(), "", AppLocale.FRENCH, today.plusDays(1)))
    }
}
