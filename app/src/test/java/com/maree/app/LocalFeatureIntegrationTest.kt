package com.maree.app

import android.content.SharedPreferences
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.TimeZone
import org.json.JSONArray
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** Real Store interactions with all local repositories, without Android managers. */
class LocalFeatureIntegrationTest {
    private lateinit var originalTimeZone: TimeZone
    private lateinit var prefs: TestFeaturePreferences
    private lateinit var store: Store
    private lateinit var history: DailyHistoryStore
    private lateinit var schedules: LimitScheduleStore
    private lateinit var favorites: CoachFavoritesStore
    private lateinit var programs: GuidedProgramStore

    @Before
    fun setUp() {
        originalTimeZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        prefs = TestFeaturePreferences()
        seedClock(NOW)
        store = Store(prefs)
        history = DailyHistoryStore(prefs)
        schedules = LimitScheduleStore(prefs)
        favorites = CoachFavoritesStore(prefs)
        programs = GuidedProgramStore(prefs)
    }

    @After
    fun restoreTimeZone() {
        TimeZone.setDefault(originalTimeZone)
    }

    @Test
    fun onlyActiveTicksRecordSelectedRawUsageAndUiRefreshesCannotBackfillHistory() {
        assertNull(store.addApp(APP, 30, 600_000L))
        store.readSnapshot(reading(600_000L), NOW)
        assertTrue(history.entries(NOW).isEmpty())

        store.tick(reading(600_000L), NOW)
        val next = UsageReading(mapOf(APP to 603_000L, UNTRACKED to 900_000L), APP)
        store.tick(next, NOW + 10_000L)
        val measured = history.entries(NOW + 10_000L).single()
        assertEquals(mapOf(APP to 3_000L), measured.usageMillis)
        assertEquals(10_000L, measured.observedMillis)
        assertFalse(measured.usageMillis.containsKey(UNTRACKED))

        repeat(3) { store.readSnapshot(reading(900_000L), NOW + 15_000L) }
        assertEquals(measured, history.entries(NOW + 15_000L).single())
        store.tick(UsageReading(emptyMap(), null), NOW + 20_000L)
        store.tick(reading(903_000L), NOW + 25_000L)
        val resumed = history.entries(NOW + 25_000L).single()
        assertEquals(3_000L, resumed.totalUsageMillis)
        assertEquals(10_000L, resumed.observedMillis)
    }

    @Test
    fun midnightAndPaidCounterResetKeepObservedUsageSeparateFromTheLiveCounter() {
        val beforeMidnight = epoch("2026-10-13T23:59:40Z")
        seedClock(beforeMidnight)
        assertNull(store.addApp(APP, 20, 60_000L))
        store.tick(reading(60_000L), beforeMidnight)
        store.tick(reading(62_000L), beforeMidnight + 10_000L)
        val midnight = beforeMidnight + 20_000L
        store.tick(reading(1_000L), midnight)
        store.tick(reading(2_000L), midnight + 10_000L)
        assertNull(store.resetApp(APP, 2_000L))
        assertEquals(0L, store.readSnapshot(reading(2_000L), midnight + 10_000L).apps.single().usedMillis)
        store.tick(reading(3_000L), midnight + 20_000L)

        val recorded = history.entries(midnight + 20_000L)
        assertEquals(listOf(LocalDate.parse("2026-10-13"), LocalDate.parse("2026-10-14")), recorded.map { it.date })
        assertEquals(2_000L, recorded[0].totalUsageMillis)
        assertEquals(2_000L, recorded[1].totalUsageMillis)
        assertEquals(20_000L, recorded[1].observedMillis)
        assertEquals(1_000L, store.readSnapshot(reading(3_000L), midnight + 20_000L).apps.single().usedMillis)
        assertEquals(20, savedBaseLimit())
    }

    @Test
    fun stoppingThroughStoreClearsTheActiveBaselineBeforeTheNextServiceTick() {
        assertNull(store.addApp(APP, 30, 0L))
        store.tick(reading(0L), NOW)
        store.tick(reading(1_000L), NOW + 5_000L)
        assertTrue(store.pauseObservation(NOW + 6_000L))
        store.tick(reading(901_000L), NOW + 10_000L)
        val resumed = history.entries(NOW + 10_000L).single()
        assertEquals(1_000L, resumed.totalUsageMillis)
        assertEquals(5_000L, resumed.observedMillis)
        assertTrue(resumed.paused)
    }

    @Test
    fun scheduledThresholdLocksAtFiveMinutesWithoutOverwritingTheTwentyMinuteBase() {
        store.setDemoPlan(PlanType.PREMIUM)
        assertNull(store.addApp(APP, 20, 0L))
        assertNull(schedules.save(rule(), store.planType(), store.pointUnits()))
        val outside = store.tick(reading(4 * MINUTE), NOW)
        assertTrue(outside.justLocked.isEmpty())
        assertEquals(20, outside.snapshot.apps.single().limitMinutes)
        assertEquals(20, outside.snapshot.apps.single().configuredLimitMinutes)

        val windowStart = epoch("2026-10-12T09:00:00Z")
        val inside = store.tick(reading(5 * MINUTE), windowStart)
        val lockedUntil = windowStart + DAY
        assertEquals(setOf(APP), inside.justLocked)
        assertEquals(5, inside.snapshot.apps.single().limitMinutes)
        assertEquals(20, inside.snapshot.apps.single().configuredLimitMinutes)
        assertEquals(lockedUntil, inside.snapshot.apps.single().lockedUntil)
        assertEquals(20, savedBaseLimit())
        assertEquals(setOf(APP), history.entries(windowStart).single().breachedPackages)

        val windowEnd = epoch("2026-10-12T10:00:00Z")
        val ended = store.tick(reading(6 * MINUTE), windowEnd)
        assertTrue(ended.justLocked.isEmpty())
        assertEquals(20, ended.snapshot.apps.single().limitMinutes)
        assertEquals(20, ended.snapshot.apps.single().configuredLimitMinutes)
        assertEquals(lockedUntil, ended.snapshot.apps.single().lockedUntil)
        assertEquals(20, savedBaseLimit())
        assertEquals(lockedUntil, Store(prefs).readSnapshot(reading(6 * MINUTE), windowEnd).apps.single().lockedUntil)
    }

    @Test
    fun downgradeUsesTheBaseAndStalePositiveBalancesCannotMutateSchedulesAtZeroPoints() {
        store.setDemoPlan(PlanType.PREMIUM)
        assertNull(store.addApp(APP, 20, 0L))
        val initial = rule()
        assertNull(schedules.save(initial, PlanType.PREMIUM, store.pointUnits()))
        val slot = epoch("2026-10-12T09:00:00Z")
        store.setDemoPlan(PlanType.FREE)
        val free = store.tick(reading(5 * MINUTE), slot)
        assertEquals(20, free.snapshot.apps.single().limitMinutes)
        assertEquals(20, free.snapshot.apps.single().configuredLimitMinutes)
        assertTrue(free.justLocked.isEmpty())
        assertNotNull(schedules.save(initial.copy(limitMinutes = 10), PlanType.PREMIUM, 400))
        assertEquals(5, schedules.entries().single().limitMinutes)

        store.setDemoPlan(PlanType.PREMIUM)
        prefs.edit().putInt("pointUnits", 0).commit()
        assertNotNull(schedules.save(initial.copy(limitMinutes = 10), PlanType.PREMIUM, 400))
        assertNotNull(schedules.remove(initial.id, PlanType.PREMIUM, 400))
        assertNotNull(store.editLimit(APP, 10))
        assertNotNull(store.resetApp(APP, 5 * MINUTE))
        assertEquals(0, store.pointUnits())
        assertEquals(initial, schedules.entries().single())
        assertEquals(20, savedBaseLimit())
        // Previously configured tracking remains effective; zero points guard edits.
        assertEquals(setOf(APP), store.tick(reading(5 * MINUTE), slot + 1_000L).justLocked)
    }

    @Test
    fun successfulStandardCustomAndProgramCallbackCheckinsReachHistoryOnceWithoutMeasuringUsage() {
        store.setDemoPlan(PlanType.PREMIUM)
        assertTrue(store.completeChallenge(0, NOW))
        assertFalse(store.completeChallenge(0, NOW + 1L))
        assertNull(store.createCustomChallenge("My local challenge", listOf("Walk", "Read", "Stretch"), 2, NOW))
        val customId = store.readSnapshot(UsageReading(emptyMap(), null), NOW).customChallenges.single().id
        assertNotNull(store.validateCustomChallengeToday(customId, NOW))
        repeat(3) { assertNull(store.setCustomChallengeStep(customId, it, true, NOW)) }
        assertNull(store.validateCustomChallengeToday(customId, NOW))
        assertNotNull(store.validateCustomChallengeToday(customId, NOW + 1L))
        store.setDemoPlan(PlanType.ULTRA)
        val program = GUIDED_PROGRAMS.first()
        assertEquals(GuidedProgramResult.SUCCESS, programs.startOn(program.id, DATE))
        assertEquals(GuidedProgramResult.SUCCESS, programs.validateOn(program.id, true, DATE))
        repeat(2) { assertTrue(store.recordProgramValidation(program.id, NOW)) }

        val day = history.entries(NOW + 1L).single()
        assertEquals(setOf("standard:0-0", "custom:$customId", "program:${program.id}"), day.validationIds)
        assertFalse(day.hasMeasuredUsage)
        assertEquals(0L, day.totalUsageMillis)
        assertEquals(1, store.readSnapshot(UsageReading(emptyMap(), null), NOW).customChallenges.single().completedDays)
        assertFalse(history.exportJson(NOW).toString().contains("My local challenge"))
    }

    @Test
    fun globalExportWhitelistsAllFeatureDataAndExcludesArbitraryPreferencesAndNestedText() {
        populateFeatures()
        val secret = "sentinel-secret-coach-message"
        prefs.edit().putString("apiToken", secret).putString("rawCoachMessage", secret).commit()
        val favoriteJson = JSONArray(prefs.getString(CoachFavoritesStore.KEY, null)!!)
        favoriteJson.getJSONObject(0).put("reply", secret).put("arbitrary", secret)
        val scheduleJson = JSONArray(prefs.getString("limitSchedules", null)!!)
        scheduleJson.getJSONObject(0).put("secret", secret)
        val dailyJson = JSONArray(prefs.getString(DailyHistoryStore.HISTORY_KEY, null)!!)
        dailyJson.getJSONObject(0).put("coachText", secret)
        val programJson = JSONObject(prefs.getString(GuidedProgramStore.KEY, null)!!).put("question", secret)
        prefs.edit().putString(CoachFavoritesStore.KEY, favoriteJson.toString())
            .putString("limitSchedules", scheduleJson.toString())
            .putString(DailyHistoryStore.HISTORY_KEY, dailyJson.toString())
            .putString(GuidedProgramStore.KEY, programJson.toString()).commit()
        val before = prefs.all.toMap()

        val exported = store.exportLocalState(NOW + 10_000L)
        assertEquals(1, exported.getJSONArray("dailyHistory").length())
        assertEquals(1, exported.getJSONArray("limitSchedules").length())
        assertEquals(1, exported.getJSONArray("coachFavorites").length())
        assertEquals(1, exported.getJSONArray("guidedPrograms").length())
        assertEquals(1_000L, exported.getJSONArray("dailyHistory").getJSONObject(0)
            .getJSONObject("usageMillis").getLong(APP))
        assertEquals(20, exported.getJSONObject("tracking").getJSONArray("trackedApps")
            .getJSONObject(0).getInt("limitMinutes"))
        assertFalse(exported.getJSONArray("coachFavorites").getJSONObject(0).has("reply"))
        assertFalse(exported.toString().contains(secret))
        assertFalse(exported.has("apiToken"))
        assertFalse(exported.has("rawCoachMessage"))
        assertFalse(exported.toString().contains(DailyHistoryStore.SAMPLE_KEY))
        assertEquals(before, prefs.all)
    }

    @Test
    fun erasureRemovesEveryFeatureAndOldRepositoryInstancesCannotRestorePersonalData() {
        populateFeatures()
        assertFalse(history.entries(NOW + 10_000L).isEmpty())
        assertFalse(schedules.entries().isEmpty())
        assertFalse(favorites.entries().isEmpty())
        assertNotNull(programs.activeProgramId())
        assertTrue(store.clearPersonalData())
        assertTrue(prefs.all.isEmpty())

        assertTrue(history.entries(NOW + 10_000L).isEmpty())
        assertTrue(schedules.entries().isEmpty())
        assertTrue(favorites.entries().isEmpty())
        assertNull(programs.activeProgramId())
        assertTrue(programs.entries(DATE).all { it.progress == null })
        val exported = store.exportLocalState(NOW + 10_000L)
        listOf("dailyHistory", "limitSchedules", "coachFavorites", "guidedPrograms").forEach {
            assertEquals(0, exported.getJSONArray(it).length())
        }
        assertTrue(prefs.all.isEmpty())

        // New use starts a fresh baseline even through an instance created before erasure.
        assertNull(store.addApp(APP, 20, 900_000L))
        store.tick(reading(900_000L), NOW + 20_000L)
        val fresh = history.entries(NOW + 20_000L).single()
        assertFalse(fresh.hasMeasuredUsage)
        assertEquals(0L, fresh.totalUsageMillis)
        assertTrue(fresh.validationIds.isEmpty())
        assertTrue(schedules.entries().isEmpty())
        assertTrue(favorites.entries().isEmpty())
        assertNull(programs.activeProgramId())
    }

    private fun populateFeatures() {
        store.setDemoPlan(PlanType.ULTRA)
        assertNull(store.addApp(APP, 20, 0L))
        assertNull(schedules.save(rule(), store.planType(), store.pointUnits()))
        val question = preparedCoachQuestionsFor(AppLocale.ENGLISH, PlanType.FREE).first()
        assertNotNull(favorites.savePrepared(question.id, AppLocale.ENGLISH))
        val program = GUIDED_PROGRAMS.first()
        assertEquals(GuidedProgramResult.SUCCESS, programs.startOn(program.id, DATE))
        assertEquals(GuidedProgramResult.SUCCESS, programs.validateOn(program.id, true, DATE))
        assertTrue(store.recordProgramValidation(program.id, NOW))
        assertTrue(store.completeChallenge(0, NOW))
        store.tick(reading(0L), NOW)
        store.tick(reading(1_000L), NOW + 10_000L)
    }

    private fun seedClock(now: Long) {
        prefs.edit().putInt("pointUnits", 400).putLong("periodStart", now)
            .putLong("challengeStart", now).putString("plan", PlanType.FREE.name)
            .putString("day", Instant.ofEpochMilli(now).atZone(UTC).toLocalDate()
                .format(DateTimeFormatter.BASIC_ISO_DATE)).commit()
    }

    private fun savedBaseLimit(): Int = JSONArray(prefs.getString("apps", null)!!).getJSONObject(0).getInt("limit")
    private fun reading(raw: Long) = UsageReading(mapOf(APP to raw), APP)
    private fun rule() = LimitSchedule(
        id = "11111111-1111-4111-8111-111111111111", name = "Morning", packageName = APP,
        weekdays = setOf(1), startMinute = 9 * 60, endMinute = 10 * 60, limitMinutes = 5
    )

    companion object {
        private val UTC = ZoneId.of("UTC")
        private val DATE = LocalDate.parse("2026-10-12")
        private val NOW = epoch("2026-10-12T08:59:00Z")
        private const val APP = "com.example.social"
        private const val UNTRACKED = "com.example.untracked"
        private const val MINUTE = 60_000L
        private const val DAY = 86_400_000L
        private fun epoch(value: String): Long = Instant.parse(value).toEpochMilli()
    }
}

/** SharedPreferences fixture usable by other JVM integration tests in this package. */
internal class TestFeaturePreferences : SharedPreferences {
    private val values = linkedMapOf<String, Any>()
    var commitSucceeds = true
    override fun getAll(): MutableMap<String, *> = values.toMutableMap()
    override fun getString(key: String, defValue: String?): String? = values[key]?.let { it as String } ?: defValue
    @Suppress("UNCHECKED_CAST")
    override fun getStringSet(key: String, defValues: MutableSet<String>?): MutableSet<String>? =
        (values[key] as? Set<String>)?.toMutableSet() ?: defValues
    override fun getInt(key: String, defValue: Int): Int = values[key]?.let { it as Int } ?: defValue
    override fun getLong(key: String, defValue: Long): Long = values[key]?.let { it as Long } ?: defValue
    override fun getFloat(key: String, defValue: Float): Float = values[key]?.let { it as Float } ?: defValue
    override fun getBoolean(key: String, defValue: Boolean): Boolean = values[key]?.let { it as Boolean } ?: defValue
    override fun contains(key: String): Boolean = values.containsKey(key)
    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) = Unit
    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) = Unit
    override fun edit(): SharedPreferences.Editor = object : SharedPreferences.Editor {
        private val pending = linkedMapOf<String, Any?>()
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
            if (!commitSucceeds) return false
            if (clearValues) values.clear()
            pending.forEach { (key, value) -> if (value == null) values.remove(key) else values[key] = value }
            return true
        }
        override fun apply() { commit() }
    }
}
