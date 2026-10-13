package com.maree.app

import android.content.SharedPreferences
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DailyHistoryStoreTest {
    private lateinit var prefs: HistoryMemoryPreferences
    private lateinit var history: DailyHistoryStore

    @Before
    fun setUp() {
        prefs = HistoryMemoryPreferences()
        history = DailyHistoryStore(prefs)
    }

    @Test
    fun initialAndRepeatedSamplesNeverBackfillTheAndroidDailyCounter() {
        assertTrue(history.recordUsage(mapOf(APP to 600_000L), NOW, UTC))
        repeat(4) { assertTrue(history.recordUsage(mapOf(APP to 600_000L), NOW, UTC)) }
        val baseline = entries().single()
        assertEquals(0L, baseline.totalUsageMillis)
        assertEquals(0L, baseline.observedMillis)
        assertEquals(1, baseline.sampleCount)
        assertFalse(baseline.hasMeasuredUsage)

        assertTrue(DailyHistoryStore(prefs).recordUsage(mapOf(APP to 603_000L), NOW + 10_000L, UTC))
        val measured = entries(NOW + 10_000L).single()
        assertEquals(mapOf(APP to 3_000L), measured.usageMillis)
        assertEquals(10_000L, measured.observedMillis)
        assertEquals(2, measured.sampleCount)
        assertTrue(measured.hasMeasuredUsage)
        repeat(3) { history.recordUsage(mapOf(APP to 603_000L), NOW + 10_000L, UTC) }
        assertEquals(measured, entries(NOW + 10_000L).single())
    }

    @Test
    fun missingAndEmptyReadingsDoNotTurnUnknownDaysIntoMeasuredZeroDays() {
        assertTrue(history.recordUsage(emptyMap(), NOW, UTC))
        assertTrue(entries().isEmpty())
        history.recordValidation("standard:1", NOW, UTC)
        assertFalse(entries().single().hasMeasuredUsage)
        history.recordUsage(mapOf(APP to 0L), NOW, UTC)
        history.recordUsage(emptyMap(), NOW + 10_000L, UTC)
        history.recordUsage(mapOf(APP to 300_000L), NOW + 20_000L, UTC)
        assertEquals(0L, entries(NOW + 20_000L).single().totalUsageMillis)
        assertEquals(0L, entries(NOW + 20_000L).single().observedMillis)
    }

    @Test
    fun continuousUnchangedReadingsMeasureZeroUsageOnlyForTheObservedInterval() {
        history.recordUsage(mapOf(APP to 0L), NOW, UTC)
        history.recordUsage(mapOf(APP to 0L), NOW + 30_000L, UTC)
        val day = entries(NOW + 30_000L).single()
        assertEquals(30_000L, day.observedMillis)
        assertEquals(0L, day.totalUsageMillis)
        assertEquals(mapOf(APP to 0L), day.usageMillis)
        assertTrue(day.hasMeasuredUsage)
    }

    @Test
    fun largeGapsCounterResetsAndClockRollbackEstablishNewBaselines() {
        history.recordUsage(mapOf(APP to 1_000L), NOW, UTC)
        history.recordUsage(mapOf(APP to 601_000L), NOW + 30_001L, UTC)
        assertEquals(0L, entries(NOW + 30_001L).single().totalUsageMillis)
        history.recordUsage(mapOf(APP to 603_000L), NOW + 40_001L, UTC)
        history.recordUsage(mapOf(APP to 0L), NOW + 50_001L, UTC)
        history.recordUsage(mapOf(APP to 100L), NOW + 45_001L, UTC)
        val day = entries(NOW + 50_001L).single()
        assertEquals(2_000L, day.totalUsageMillis)
        assertEquals(10_000L, day.observedMillis)
    }

    @Test
    fun oneDecreasedCounterResetsTheWholeObservationWithoutInventingOtherUsage() {
        val other = "com.example.other"
        history.recordUsage(mapOf(APP to 2_000L, other to 10_000L), NOW, UTC)
        history.recordUsage(mapOf(APP to 0L, other to 20_000L), NOW + 10_000L, UTC)
        assertEquals(0L, entries(NOW + 10_000L).single().totalUsageMillis)
        assertFalse(entries(NOW + 10_000L).single().hasMeasuredUsage)
        history.recordUsage(mapOf(APP to 1_000L, other to 20_000L), NOW + 20_000L, UTC)
        assertEquals(1_000L, entries(NOW + 20_000L).single().totalUsageMillis)
    }

    @Test
    fun newAppsHaveTheirOwnBaselineAndUnexpectedReaderCorrectionsCannotBackfill() {
        val newApp = "com.example.new"
        history.recordUsage(mapOf(APP to 0L), NOW, UTC)
        history.recordUsage(mapOf(APP to 300_000L, newApp to 900_000L), NOW + 5_000L, UTC)
        assertEquals(mapOf(APP to 5_000L), entries(NOW + 5_000L).single().usageMillis)
        history.recordUsage(mapOf(APP to 301_000L, newApp to 901_000L), NOW + 10_000L, UTC)
        assertEquals(mapOf(APP to 6_000L, newApp to 1_000L), entries(NOW + 10_000L).single().usageMillis)
    }

    @Test
    fun pausePersistsTheDateButPausedUseAndFirstResumedSampleAreNeverCounted() {
        history.recordUsage(mapOf(APP to 0L), NOW, UTC)
        history.recordUsage(mapOf(APP to 1_000L), NOW + 5_000L, UTC)
        assertTrue(history.pause(NOW + 6_000L, UTC))
        assertFalse(prefs.contains(DailyHistoryStore.SAMPLE_KEY))
        repeat(3) { assertTrue(history.pause(NOW + 6_000L, UTC)) }
        DailyHistoryStore(prefs).recordUsage(mapOf(APP to 901_000L), NOW + 10_000L, UTC)
        val resumed = entries(NOW + 10_000L).single()
        assertEquals(1_000L, resumed.totalUsageMillis)
        assertEquals(5_000L, resumed.observedMillis)
        assertTrue(resumed.paused)
        history.recordUsage(mapOf(APP to 902_000L), NOW + 15_000L, UTC)
        assertEquals(2_000L, entries(NOW + 15_000L).single().totalUsageMillis)
    }

    @Test
    fun midnightUsesIsoDatesAndDoesNotAttributeTheOldDayCounterToTheNewDay() {
        val midnight = Instant.parse("2026-10-14T00:00:00Z").toEpochMilli()
        history.recordUsage(mapOf(APP to 800_000L), midnight - 15_000L, UTC)
        history.recordUsage(mapOf(APP to 802_000L), midnight - 5_000L, UTC)
        history.recordUsage(mapOf(APP to 1_000L), midnight + 5_000L, UTC)
        val days = entries(midnight + 5_000L)
        assertEquals(listOf(LocalDate.parse("2026-10-13"), LocalDate.parse("2026-10-14")), days.map { it.date })
        assertEquals(2_000L, days[0].totalUsageMillis)
        assertEquals(0L, days[1].totalUsageMillis)
        assertFalse(days[1].hasMeasuredUsage)
        assertTrue(history.exportJson(midnight + 5_000L, UTC).getJSONObject(1).getString("date") == "2026-10-14")
    }

    @Test
    fun springAndAutumnDstChangesUseElapsedInstantsWithinOneLocalDate() {
        val paris = ZoneId.of("Europe/Paris")
        for ((before, after) in listOf(
            "2026-03-29T01:59:50+01:00[Europe/Paris]" to "2026-03-29T03:00:00+02:00[Europe/Paris]",
            "2026-10-25T02:59:50+02:00[Europe/Paris]" to "2026-10-25T02:00:00+01:00[Europe/Paris]"
        )) {
            prefs.edit().clear().commit()
            val start = ZonedDateTime.parse(before).toInstant().toEpochMilli()
            val end = ZonedDateTime.parse(after).toInstant().toEpochMilli()
            history.recordUsage(mapOf(APP to 0L), start, paris)
            history.recordUsage(mapOf(APP to 5_000L), end, paris)
            val day = history.entries(end, paris).single()
            assertEquals(10_000L, day.observedMillis)
            assertEquals(5_000L, day.totalUsageMillis)
            assertEquals(ZonedDateTime.parse(after).toLocalDate(), day.date)
        }
    }

    @Test
    fun timezoneChangesKeepOriginalDatesAndResetTheSampleEvenOnTheSameDate() {
        val instant = Instant.parse("2026-10-13T05:00:00Z").toEpochMilli()
        history.recordUsage(mapOf(APP to 10_000L), instant, ZoneId.of("Pacific/Honolulu"))
        history.recordUsage(mapOf(APP to 20_000L), instant + 10_000L, UTC)
        history.recordUsage(mapOf(APP to 30_000L), instant + 20_000L, ZoneId.of("Europe/Paris"))
        val days = history.entries(instant + 20_000L, UTC)
        assertEquals(listOf(LocalDate.parse("2026-10-12"), LocalDate.parse("2026-10-13")), days.map { it.date })
        assertTrue(days.all { !it.hasMeasuredUsage && it.totalUsageMillis == 0L })
    }

    @Test
    fun validationsRemainDistinctByKindAreIdempotentAndNeverStoreFreeText() {
        listOf("standard:3", "custom:abc-123", "program:walking-7:day-2").forEach { id ->
            repeat(3) { assertTrue(history.recordValidation(id, NOW, UTC)) }
        }
        assertFalse(history.recordValidation("My private coach message", NOW, UTC))
        assertFalse(history.recordValidation("custom:My private challenge title", NOW, UTC))
        assertFalse(history.recordValidation("custom:" + "x".repeat(200), NOW, UTC))
        assertEquals(setOf("standard:3", "custom:abc-123", "program:walking-7:day-2"), entries().single().validationIds)
        history.recordValidation("standard:3", NOW + DAY, UTC)
        assertEquals(2, entries(NOW + DAY).size)
        assertEquals(setOf("standard:3"), entries(NOW + DAY).last().validationIds)
        assertFalse(history.exportJson(NOW + DAY, UTC).toString().contains("private"))
    }

    @Test
    fun breachEventsAreIdempotentAndDoNotPretendToMeasureTheWholeDay() {
        repeat(3) { assertTrue(history.recordBreach(APP, NOW, UTC)) }
        assertFalse(history.recordBreach("private title", NOW, UTC))
        val day = entries().single()
        assertEquals(setOf(APP), day.breachedPackages)
        assertFalse(day.hasMeasuredUsage)
        assertEquals(0L, day.totalUsageMillis)
    }

    @Test
    fun retentionRemovesExpiredAndFutureDatesAndLeavesMissingDaysUnknown() {
        val base = LocalDate.parse("2026-06-01")
        for (offset in 0L..100L) {
            val now = base.plusDays(offset).atTime(12, 0).atZone(UTC).toInstant().toEpochMilli()
            assertTrue(history.recordValidation("standard:1", now, UTC))
        }
        val end = base.plusDays(100L).atTime(12, 0).atZone(UTC).toInstant().toEpochMilli()
        val retained = history.entries(end, UTC)
        assertEquals(90, retained.size)
        assertEquals(base.plusDays(11L), retained.first().date)
        assertEquals(base.plusDays(100L), retained.last().date)
        assertTrue(history.entries(end + 91 * DAY, UTC).isEmpty())
        assertTrue(history.entries(base.atTime(12, 0).atZone(UTC).toInstant().toEpochMilli(), UTC).isEmpty())

        prefs.edit().clear().commit()
        history.recordValidation("standard:1", NOW, UTC)
        history.recordValidation("standard:2", NOW + 3 * DAY, UTC)
        assertEquals(listOf(DATE, DATE.plusDays(3L)), entries(NOW + 3 * DAY).map { it.date })
    }

    @Test
    fun dailyAppsAndValidationIdsAreBoundedAndOnlyHistoryKeysAreWritten() {
        val raw = (0 until 600).associate { "com.example.app$it" to 0L }
        history.recordUsage(raw, NOW, UTC)
        history.recordUsage(raw.mapValues { 1_000L }, NOW + 10_000L, UTC)
        repeat(65) { index ->
            assertEquals(index < 64, history.recordValidation("standard:$index", NOW + 10_000L, UTC))
        }
        repeat(505) { index -> history.recordBreach("com.example.app$index", NOW + 10_000L, UTC) }
        val day = entries(NOW + 10_000L).single()
        assertEquals(500, day.usageMillis.size)
        assertEquals(500, day.breachedPackages.size)
        assertEquals(64, day.validationIds.size)
        assertEquals(setOf(DailyHistoryStore.HISTORY_KEY, DailyHistoryStore.SAMPLE_KEY), prefs.all.keys)
        val rawSaved = JSONObject(prefs.getString(DailyHistoryStore.SAMPLE_KEY, null)!!).getJSONObject("raw")
        assertEquals(500, rawSaved.length())
    }

    @Test
    fun malformedJsonWrongPreferenceTypesAndInvalidValuesAreSafeAndExportIsValidated() {
        prefs.edit().putString(DailyHistoryStore.HISTORY_KEY, "{")
            .putString(DailyHistoryStore.SAMPLE_KEY, "null").commit()
        assertTrue(entries().isEmpty())
        assertEquals(0, history.exportJson(NOW, UTC).length())
        assertTrue(history.recordUsage(mapOf(APP to 1_000L), NOW, UTC))
        assertFalse(entries().single().hasMeasuredUsage)

        prefs.edit().putInt(DailyHistoryStore.HISTORY_KEY, 3).putInt(DailyHistoryStore.SAMPLE_KEY, 1).commit()
        assertTrue(entries().isEmpty())
        assertTrue(history.recordValidation("standard:2", NOW, UTC))

        val invalid = JSONObject().put("date", DATE.toString()).put("sampleCount", 2).put("observedMillis", 10_000L)
            .put("usageMillis", JSONObject().put(APP, 2_000L).put("com.example.over", 20_000L)
                .put("com.example.negative", -1).put("com.example.fraction", 1.5).put("raw title", 3))
            .put("validationIds", JSONArray(listOf("standard:4", "coach private text", 3)))
            .put("breachedPackages", JSONArray(listOf(APP, "private title")))
            .put("coachText", "secret conversation").put("paused", "true")
        val future = JSONObject(invalid.toString()).put("date", DATE.plusDays(1).toString())
        val brokenDate = JSONObject(invalid.toString()).put("date", "2026-02-30")
        prefs.edit().putString(DailyHistoryStore.HISTORY_KEY, JSONArray().put(invalid).put(future).put(brokenDate).toString()).commit()
        val parsed = entries().single()
        assertEquals(mapOf(APP to 2_000L), parsed.usageMillis)
        assertEquals(setOf("standard:4"), parsed.validationIds)
        assertEquals(setOf(APP), parsed.breachedPackages)
        assertFalse(parsed.paused)
        val exported = history.exportJson(NOW, UTC).toString()
        assertFalse(exported.contains("secret"))
        assertFalse(exported.contains("coachText"))
        assertFalse(exported.contains("raw"))
    }

    @Test
    fun duplicateStoredDatesCannotInflateExportAndUsageRequiresObservationMetadata() {
        val first = JSONObject().put("date", DATE.toString()).put("sampleCount", 2).put("observedMillis", 2_000L)
            .put("usageMillis", JSONObject().put(APP, 2_000L))
        val last = JSONObject().put("date", DATE.toString()).put("usageMillis", JSONObject().put(APP, 2_000L))
            .put("validationIds", JSONArray(listOf("custom:id")))
        prefs.edit().putString(DailyHistoryStore.HISTORY_KEY, JSONArray().put(first).put(last).toString()).commit()
        assertEquals(1, entries().size)
        assertEquals(0L, entries().single().totalUsageMillis)
        assertFalse(entries().single().hasMeasuredUsage)
        assertEquals(1, history.exportJson(NOW, UTC).length())
    }

    @Test
    fun clearingPreferencesErasesPersistedHistoryAndTheExistingInstanceCannotResurrectItsBaseline() {
        history.recordUsage(mapOf(APP to 0L), NOW, UTC)
        history.recordUsage(mapOf(APP to 1_000L), NOW + 5_000L, UTC)
        history.recordValidation("custom:old", NOW + 5_000L, UTC)
        prefs.edit().clear().commit()
        assertTrue(history.entries(NOW + 5_000L, UTC).isEmpty())
        assertEquals(0, history.exportJson(NOW + 5_000L, UTC).length())
        history.recordUsage(mapOf(APP to 901_000L), NOW + 10_000L, UTC)
        val fresh = entries(NOW + 10_000L).single()
        assertEquals(0L, fresh.totalUsageMillis)
        assertFalse(fresh.hasMeasuredUsage)
        assertTrue(fresh.validationIds.isEmpty())
        assertEquals(fresh, DailyHistoryStore(prefs).entries(NOW + 10_000L, UTC).single())
    }

    @Test
    fun failedPersistenceIsReportedWithoutClaimingTheEventWasSaved() {
        prefs.commitSucceeds = false
        assertFalse(history.recordUsage(mapOf(APP to 0L), NOW, UTC))
        assertFalse(history.recordValidation("standard:1", NOW, UTC))
        assertFalse(history.recordBreach(APP, NOW, UTC))
        assertFalse(history.pause(NOW, UTC))
        assertTrue(entries().isEmpty())
    }

    @Test
    fun nextStartupPrunePhysicallyRemovesInactiveExpiredRecordsAndTheirBaseline() {
        history.recordUsage(mapOf(APP to 1_000L), NOW, UTC)
        history.recordValidation("custom:old", NOW, UTC)
        prefs.edit().putString("otherFeature", "keep").commit()
        val nextStartup = NOW + 90 * DAY
        // Pure reads hide expired records while the persisted data awaits next-use cleanup.
        assertTrue(history.entries(nextStartup, UTC).isEmpty())
        assertEquals(0, history.exportJson(nextStartup, UTC).length())
        assertTrue(prefs.contains(DailyHistoryStore.HISTORY_KEY))
        assertTrue(prefs.contains(DailyHistoryStore.SAMPLE_KEY))

        assertTrue(DailyHistoryStore(prefs).prune(nextStartup, UTC))
        assertFalse(prefs.contains(DailyHistoryStore.HISTORY_KEY))
        assertFalse(prefs.contains(DailyHistoryStore.SAMPLE_KEY))
        assertEquals(mapOf("otherFeature" to "keep"), prefs.all)
    }

    @Test
    fun pruneRetainsCurrentDatesAndCanonicalizesInvalidRowsWithoutClearingAFreshBaseline() {
        history.recordUsage(mapOf(APP to 0L), NOW, UTC)
        val current = JSONObject(history.exportJson(NOW, UTC).getJSONObject(0).toString())
            .put("coachText", "discard this unrelated field")
        val expired = JSONObject(current.toString()).put("date", DATE.minusDays(90).toString())
        val future = JSONObject(current.toString()).put("date", DATE.plusDays(1).toString())
        prefs.edit().putString(DailyHistoryStore.HISTORY_KEY, JSONArray().put(expired).put(current)
            .put(future).put(JSONObject().put("date", "not-a-date")).toString()).commit()

        assertTrue(history.prune(NOW + 10_000L, UTC))
        val stored = JSONArray(prefs.getString(DailyHistoryStore.HISTORY_KEY, null)!!)
        assertEquals(1, stored.length())
        assertEquals(DATE.toString(), stored.getJSONObject(0).getString("date"))
        assertFalse(stored.getJSONObject(0).has("coachText"))
        assertTrue(prefs.contains(DailyHistoryStore.SAMPLE_KEY))
        history.recordUsage(mapOf(APP to 1_000L), NOW + 10_000L, UTC)
        assertEquals(1_000L, entries(NOW + 10_000L).single().totalUsageMillis)
        assertEquals(10_000L, entries(NOW + 10_000L).single().observedMillis)
    }

    @Test
    fun pruneIsANoOpForCanonicalCurrentHistoryAndTheThirtySecondBaselineBoundary() {
        history.recordUsage(mapOf(APP to 0L), NOW, UTC)
        val before = prefs.all.toMap()
        val attempts = prefs.commitAttempts
        val edits = prefs.editAttempts
        assertTrue(history.prune(NOW + 30_000L, UTC))
        assertEquals(before, prefs.all)
        assertEquals(attempts, prefs.commitAttempts)
        assertEquals(edits, prefs.editAttempts)
        prefs.commitSucceeds = false
        // Nothing needs persistence, so an unavailable writer does not make this fail.
        assertTrue(history.prune(NOW + 30_000L, UTC))
        assertEquals(attempts, prefs.commitAttempts)
        assertEquals(edits, prefs.editAttempts)
    }

    @Test
    fun pruneDropsExpiredAndFutureSamplesWithoutRemovingCurrentHistory() {
        history.recordUsage(mapOf(APP to 0L), NOW, UTC)
        val dayJson = prefs.getString(DailyHistoryStore.HISTORY_KEY, null)
        assertTrue(history.prune(NOW + 30_001L, UTC))
        assertFalse(prefs.contains(DailyHistoryStore.SAMPLE_KEY))
        assertEquals(dayJson, prefs.getString(DailyHistoryStore.HISTORY_KEY, null))
        history.recordUsage(mapOf(APP to 20_000L), NOW + 40_000L, UTC)
        assertTrue(prefs.contains(DailyHistoryStore.SAMPLE_KEY))
        assertTrue(history.prune(NOW + 35_000L, UTC))
        assertFalse(prefs.contains(DailyHistoryStore.SAMPLE_KEY))
        assertTrue(prefs.contains(DailyHistoryStore.HISTORY_KEY))
        assertFalse(entries(NOW + 40_000L).single().hasMeasuredUsage)
    }

    @Test
    fun pruneAfterErasureNeverCreatesKeysOrAttemptsACommit() {
        assertTrue(history.prune(NOW, UTC))
        assertEquals(0, prefs.commitAttempts)
        assertEquals(0, prefs.editAttempts)
        history.recordUsage(mapOf(APP to 0L), NOW, UTC)
        prefs.edit().clear().commit()
        val attempts = prefs.commitAttempts
        val edits = prefs.editAttempts
        prefs.commitSucceeds = false
        assertTrue(history.prune(NOW + 10_000L, UTC))
        assertTrue(prefs.all.isEmpty())
        assertEquals(attempts, prefs.commitAttempts)
        assertEquals(edits, prefs.editAttempts)
    }

    @Test
    fun pruneReportsFailedCommitWithoutClaimingSuccessfulDurableCleanup() {
        history.recordUsage(mapOf(APP to 0L), NOW, UTC)
        prefs.commitSucceeds = false
        assertFalse(history.prune(NOW + 90 * DAY, UTC))
    }

    @Test
    fun pruneDurablyRemovesMalformedJsonAndWrongPreferenceTypes() {
        prefs.edit().putString(DailyHistoryStore.HISTORY_KEY, "{invalid")
            .putInt(DailyHistoryStore.SAMPLE_KEY, 3).commit()
        assertTrue(history.prune(NOW, UTC))
        assertTrue(prefs.all.isEmpty())
    }

    private fun entries(now: Long = NOW) = history.entries(now, UTC)

    companion object {
        private val UTC = ZoneId.of("UTC")
        private val DATE = LocalDate.parse("2026-10-13")
        private val NOW = Instant.parse("2026-10-13T12:00:00Z").toEpochMilli()
        private const val APP = "com.example.social"
        private const val DAY = 86_400_000L
    }
}

/** The persistence contract is exercised without an Android service or emulator. */
private class HistoryMemoryPreferences : SharedPreferences {
    private val values = mutableMapOf<String, Any>()
    var commitSucceeds = true
    var commitAttempts = 0
    var editAttempts = 0
    override fun getAll(): MutableMap<String, *> = values.toMutableMap()
    override fun getString(key: String, defValue: String?): String? {
        val value = values[key] ?: return defValue
        return value as String
    }
    @Suppress("UNCHECKED_CAST")
    override fun getStringSet(key: String, defValues: MutableSet<String>?): MutableSet<String>? =
        (values[key] as? Set<String>)?.toMutableSet() ?: defValues
    override fun getInt(key: String, defValue: Int): Int = values[key] as? Int ?: defValue
    override fun getLong(key: String, defValue: Long): Long = values[key] as? Long ?: defValue
    override fun getFloat(key: String, defValue: Float): Float = values[key] as? Float ?: defValue
    override fun getBoolean(key: String, defValue: Boolean): Boolean = values[key] as? Boolean ?: defValue
    override fun contains(key: String): Boolean = values.containsKey(key)
    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) = Unit
    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) = Unit
    override fun edit(): SharedPreferences.Editor {
        editAttempts++
        return object : SharedPreferences.Editor {
            private val pending = mutableMapOf<String, Any?>()
            private var clearValues = false
            override fun putString(key: String, value: String?) = apply { pending[key] = value }
            override fun putStringSet(key: String, values: MutableSet<String>?) = apply { pending[key] = values?.toSet() }
            override fun putInt(key: String, value: Int) = apply { pending[key] = value }
            override fun putLong(key: String, value: Long) = apply { pending[key] = value }
            override fun putFloat(key: String, value: Float) = apply { pending[key] = value }
            override fun putBoolean(key: String, value: Boolean) = apply { pending[key] = value }
            override fun remove(key: String) = apply { pending[key] = null }
            override fun clear() = apply { clearValues = true }
            override fun commit(): Boolean {
                commitAttempts++
                if (!commitSucceeds) return false
                if (clearValues) values.clear()
                pending.forEach { (key, value) -> if (value == null) values.remove(key) else values[key] = value }
                return true
            }
            override fun apply() { commit() }
        }
    }
}
