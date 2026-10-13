package com.maree.app

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WeeklyReviewTest {
    @Test
    fun summaryIncludesExactlyTheLastSevenLocalDatesAndCountsObservedDaysOnly() {
        val entries = listOf(
            measured(END.minusDays(7), 90_000L, 120_000L),
            measured(END.minusDays(6), 10_000L, 20_000L),
            DailyHistoryEntry(END.minusDays(4), validationIds = setOf("standard:1")),
            DailyHistoryEntry(END.minusDays(3), paused = true),
            measured(END, 5_000L, 10_000L),
            measured(END.plusDays(1), 80_000L, 100_000L)
        )
        val summary = summarizeHistoryWeek(entries, END)
        assertEquals(END.minusDays(6), summary.start)
        assertEquals(END, summary.end)
        assertEquals(2, summary.measuredDays)
        assertEquals(30_000L, summary.observationMillis)
        assertEquals(15_000L, summary.totalUsageMillis)
        assertEquals(1, summary.validationCount)
        assertEquals(0, summary.breachDays)
    }

    @Test
    fun missingUsageRemainsUnknownWhileValidationsAndBreachesStillAppear() {
        val entries = listOf(
            DailyHistoryEntry(END.minusDays(1), validationIds = setOf("standard:2", "custom:id", "program:id:day-1")),
            DailyHistoryEntry(END, breachedPackages = setOf(APP, "com.example.other"), paused = true)
        )
        val summary = summarizeHistoryWeek(entries, END)
        assertEquals(0, summary.measuredDays)
        assertEquals(0L, summary.observationMillis)
        assertEquals(0L, summary.totalUsageMillis)
        assertTrue(summary.usageMillis.isEmpty())
        assertEquals(3, summary.validationCount)
        assertEquals(1, summary.breachDays)
    }

    @Test
    fun duplicateDatesUseOneLatestRecordAndDoNotInflateDaysUsageOrValidations() {
        val old = measured(END, 1_000L, 10_000L).copy(validationIds = setOf("standard:0", "custom:old"))
        val latest = measured(END, 2_000L, 20_000L).copy(validationIds = setOf("custom:new"))
        val summary = summarizeHistoryWeek(listOf(old, latest), END)
        assertEquals(1, summary.measuredDays)
        assertEquals(2_000L, summary.totalUsageMillis)
        assertEquals(20_000L, summary.observationMillis)
        assertEquals(1, summary.validationCount)
    }

    @Test
    fun detailsAggregateByAppAndThePreviousWeekNeverOverlapsTheCurrentWeek() {
        val other = "com.example.other"
        val entries = listOf(
            measured(END.minusDays(13), 5_000L, 10_000L),
            measured(END.minusDays(7), 7_000L, 10_000L),
            measured(END.minusDays(6), 11_000L, 20_000L),
            measured(END, 3_000L, 10_000L).copy(usageMillis = mapOf(APP to 3_000L, other to 4_000L))
        )
        val current = summarizeHistoryWeek(entries, END)
        val previous = summarizeHistoryWeek(entries, END.minusDays(7))
        assertEquals(mapOf(APP to 14_000L, other to 4_000L), current.usageMillis)
        assertEquals(18_000L, current.totalUsageMillis)
        assertEquals(12_000L, previous.totalUsageMillis)
        assertEquals(2, previous.measuredDays)
        assertEquals(END.minusDays(7), previous.end)
        assertEquals(END.minusDays(13), previous.start)
    }

    @Test
    fun zeroUsageCanBeMeasuredAndValidationOnlyRecordsCannotFabricateUsage() {
        val entries = listOf(
            measured(END.minusDays(2), 0L, 10_000L),
            DailyHistoryEntry(END.minusDays(1), usageMillis = mapOf(APP to 500_000L), validationIds = setOf("standard:1"))
        )
        val summary = summarizeHistoryWeek(entries, END)
        assertEquals(1, summary.measuredDays)
        assertEquals(10_000L, summary.observationMillis)
        assertEquals(0L, summary.totalUsageMillis)
        assertEquals(1, summary.validationCount)
    }

    @Test
    fun weeklyWindowsFollowLocalDatesAcrossTheSpringDstSunday() {
        val end = LocalDate.parse("2026-03-31")
        val summary = summarizeHistoryWeek(
            listOf(measured(LocalDate.parse("2026-03-29"), 2_000L, 10_000L)), end
        )
        assertEquals(LocalDate.parse("2026-03-25"), summary.start)
        assertEquals(1, summary.measuredDays)
        assertEquals(2_000L, summary.totalUsageMillis)
    }

    @Test
    fun aggregationCannotWrapLargeDurationsIntoNegativeUsage() {
        val summary = summarizeHistoryWeek(listOf(
            measured(END.minusDays(1), Long.MAX_VALUE, Long.MAX_VALUE),
            measured(END, 1L, 1L)
        ), END)
        assertEquals(Long.MAX_VALUE, summary.totalUsageMillis)
        assertEquals(Long.MAX_VALUE, summary.observationMillis)
    }

    private fun measured(date: LocalDate, usage: Long, observation: Long) = DailyHistoryEntry(
        date = date, usageMillis = mapOf(APP to usage), observedMillis = observation, sampleCount = 2
    )

    companion object {
        private val END = LocalDate.parse("2026-10-13")
        private const val APP = "com.example.social"
    }
}
