package com.maree.app

import android.content.SharedPreferences
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GuidedProgramStoreTest {
    private val date = LocalDate.of(2026, 10, 13)

    @Test
    fun threeCompleteProgramsHave51DistinctBilingualActivitiesInOrder() {
        assertEquals(listOf("sleep7days", "studies14days", "socialmedia30days"), GUIDED_PROGRAMS.map { it.id })
        assertEquals(listOf(7, 14, 30), GUIDED_PROGRAMS.map { it.requiredDays })
        val days = GUIDED_PROGRAMS.flatMap { it.days }
        assertEquals(51, days.size)
        for (locale in AppLocale.entries) {
            assertEquals(51, days.map { it.activity.text(locale) }.distinct().size)
            assertEquals(51, days.map { it.title.text(locale) }.distinct().size)
            assertTrue(days.all { it.activity.text(locale).length >= 70 && it.title.text(locale).isNotBlank() })
        }
        assertTrue(GUIDED_PROGRAMS.all { it.title.french != it.title.english && it.description.french != it.description.english })
    }

    @Test
    fun previewNeverEnrollsAndStartingDoesNotValidateAnyDayOrSpendPoints() {
        val (store, prefs) = fixture()
        prefs.edit().putInt("pointUnits", 0).putString("privateQuestion", "synthetic-private-question").commit()
        val before = prefs.all
        assertEquals(3, store.entries(date).size)
        assertTrue(store.entries(date).all { it.progress == null })
        assertEquals(before, prefs.all)
        prefs.durableWrites = 0

        assertEquals(GuidedProgramResult.SUCCESS, store.startOn(SLEEP, date))
        assertEquals(0, store.progress(SLEEP, date)!!.completedDays)
        assertEquals(1, store.progress(SLEEP, date)!!.currentDay)
        assertEquals(0, prefs.getInt("pointUnits", -1))
        assertEquals("synthetic-private-question", prefs.getString("privateQuestion", null))
        assertEquals(setOf("plan", "pointUnits", "privateQuestion", GuidedProgramStore.KEY), prefs.all.keys)
        assertEquals(1, prefs.durableWrites)
        assertEquals(0, prefs.asyncWrites)
    }

    @Test
    fun consciousCheckAndActiveEnrollmentAreRequired() {
        val (store, _) = fixture()
        assertEquals(GuidedProgramResult.NOT_ACTIVE, store.validateOn(SLEEP, true, date))
        assertEquals(GuidedProgramResult.UNKNOWN_PROGRAM, store.startOn("invented", date))
        assertEquals(GuidedProgramResult.SUCCESS, store.startOn(SLEEP, date))
        assertEquals(GuidedProgramResult.ACTIVITY_NOT_CHECKED, store.validateOn(SLEEP, false, date))
        assertEquals(0, store.progress(SLEEP, date)!!.completedDays)
        assertEquals(GuidedProgramResult.SUCCESS, store.validateOn(SLEEP, true, date))
        assertEquals(listOf(date), store.progress(SLEEP, date)!!.validatedDates)
        assertFalse(store.progress(SLEEP, date)!!.canValidateToday)
        assertEquals(GuidedProgramResult.ALREADY_VALIDATED_TODAY, store.validateOn(SLEEP, true, date))
    }

    @Test
    fun oneGuidedValidationPerDateAcrossSwitchResetAndDiscard() {
        val (store, _) = fixture()
        assertEquals(GuidedProgramResult.SUCCESS, store.startOn(SLEEP, date))
        assertEquals(GuidedProgramResult.SUCCESS, store.validateOn(SLEEP, true, date))
        assertEquals(GuidedProgramResult.SUCCESS, store.startOn(STUDIES, date))
        assertEquals(GuidedProgramResult.NOT_ACTIVE, store.validateOn(SLEEP, true, date.plusDays(1)))
        assertEquals(GuidedProgramResult.ALREADY_VALIDATED_TODAY, store.validateOn(STUDIES, true, date))
        assertEquals(GuidedProgramResult.SUCCESS, store.resetOn(SLEEP, date))
        assertEquals(0, store.progress(SLEEP, date)!!.completedDays)
        assertEquals(GuidedProgramResult.SUCCESS, store.discard(SLEEP))
        assertEquals(GuidedProgramResult.SUCCESS, store.discard(STUDIES))
        assertEquals(GuidedProgramResult.SUCCESS, store.startOn(SOCIAL, date))
        assertEquals(GuidedProgramResult.ALREADY_VALIDATED_TODAY, store.validateOn(SOCIAL, true, date))
        assertEquals(GuidedProgramResult.SUCCESS, store.validateOn(SOCIAL, true, date.plusDays(1)))
    }

    @Test
    fun practiceDaysCanSpanMoreCalendarDaysAndStopAtTheProgramLength() {
        val (store, prefs) = fixture()
        assertEquals(GuidedProgramResult.SUCCESS, store.startOn(SLEEP, date))
        repeat(7) { offset ->
            val practiceDate = date.plusDays(offset * 3L)
            assertEquals(offset + 1, store.progress(SLEEP, practiceDate)!!.currentDay)
            assertEquals(GuidedProgramResult.SUCCESS, store.validateOn(SLEEP, true, practiceDate))
        }
        val progress = store.progress(SLEEP, date.plusDays(19))!!
        assertEquals(7, progress.completedDays)
        assertTrue(progress.completed)
        assertNull(progress.currentDay)
        assertFalse(progress.canValidateToday)
        assertEquals(date.plusDays(18), progress.validatedDates.last())
        assertEquals(GuidedProgramResult.COMPLETED, store.validateOn(SLEEP, true, date.plusDays(19)))
        assertFalse(prefs.contains("pointUnits"))
        assertFalse(prefs.contains("points"))
    }

    @Test
    fun allThreeProgramsCompleteWithoutExtraRewards() {
        val (store, prefs) = fixture()
        prefs.edit().putInt("pointUnits", 0).commit()
        var practiceDate = date
        GUIDED_PROGRAMS.forEach { program ->
            assertEquals(GuidedProgramResult.SUCCESS, store.startOn(program.id, practiceDate))
            repeat(program.requiredDays) {
                assertEquals(GuidedProgramResult.SUCCESS, store.validateOn(program.id, true, practiceDate))
                practiceDate = practiceDate.plusDays(1)
            }
            assertTrue(store.progress(program.id, practiceDate)!!.completed)
        }
        assertEquals(3, store.exportJson().length())
        assertEquals(51, store.entries(practiceDate).sumOf { it.progress!!.completedDays })
        assertEquals(0, prefs.getInt("pointUnits", -1))
        assertFalse(prefs.contains("points"))
    }

    @Test
    fun switchAndNewInstancesRetainProgressWithoutAutomaticallyResuming() {
        val (store, prefs) = fixture()
        store.startOn(SLEEP, date)
        store.validateOn(SLEEP, true, date)
        store.startOn(STUDIES, date.plusDays(2))
        store.validateOn(STUDIES, true, date.plusDays(2))
        val reopened = GuidedProgramStore(prefs)
        assertEquals(STUDIES, reopened.activeProgramId())
        assertEquals(listOf(date), reopened.progress(SLEEP, date.plusDays(3))!!.validatedDates)
        assertFalse(reopened.progress(SLEEP, date.plusDays(3))!!.active)
        assertEquals(GuidedProgramResult.SUCCESS, reopened.startOn(SLEEP, date.plusDays(3)))
        assertEquals(GuidedProgramResult.SUCCESS, reopened.validateOn(SLEEP, true, date.plusDays(3)))
        assertEquals(listOf(date, date.plusDays(3)), reopened.progress(SLEEP, date.plusDays(3))!!.validatedDates)
        assertEquals(listOf(date.plusDays(2)), reopened.progress(STUDIES, date.plusDays(3))!!.validatedDates)
    }

    @Test
    fun freeAndPremiumPreviewWhileOnlyUltraEnrollsAndValidates() {
        val (store, prefs) = fixture()
        for (plan in listOf(PlanType.FREE, PlanType.PREMIUM)) {
            prefs.edit().putString("plan", plan.name).commit()
            assertEquals(3, store.entries(date).size)
            assertEquals(GuidedProgramResult.ULTRA_REQUIRED, store.startOn(SLEEP, date))
            assertEquals(GuidedProgramResult.ULTRA_REQUIRED, store.validateOn(SLEEP, true, date))
            assertFalse(prefs.contains(GuidedProgramStore.KEY))
        }
        prefs.edit().putString("plan", PlanType.ULTRA.name).commit()
        assertEquals(GuidedProgramResult.SUCCESS, store.startOn(SLEEP, date))
        assertEquals(GuidedProgramResult.SUCCESS, store.validateOn(SLEEP, true, date))
        val saved = prefs.getString(GuidedProgramStore.KEY, null)
        for (plan in listOf(PlanType.FREE, PlanType.PREMIUM)) {
            prefs.edit().putString("plan", plan.name).commit()
            assertEquals(GuidedProgramResult.ULTRA_REQUIRED, store.startOn(STUDIES, date.plusDays(1)))
            assertEquals(GuidedProgramResult.ULTRA_REQUIRED, store.validateOn(SLEEP, true, date.plusDays(1)))
            assertEquals(GuidedProgramResult.ULTRA_REQUIRED, store.resetOn(SLEEP, date.plusDays(1)))
            assertEquals(saved, prefs.getString(GuidedProgramStore.KEY, null))
            assertEquals(1, store.progress(SLEEP, date.plusDays(1))!!.completedDays)
            assertFalse(store.progress(SLEEP, date.plusDays(1))!!.canValidateToday)
        }
        prefs.edit().putString("plan", PlanType.ULTRA.name).commit()
        assertTrue(store.progress(SLEEP, date.plusDays(1))!!.canValidateToday)
        assertEquals(GuidedProgramResult.SUCCESS, store.validateOn(SLEEP, true, date.plusDays(1)))
        assertEquals(2, store.progress(SLEEP, date.plusDays(1))!!.completedDays)
    }

    @Test
    fun backwardClockAndFutureSavedDatesAreLockedWithoutDeletingHistory() {
        val (store, prefs) = fixture()
        store.startOn(SLEEP, date)
        store.validateOn(SLEEP, true, date.plusDays(3))
        val saved = prefs.getString(GuidedProgramStore.KEY, null)
        assertEquals(GuidedProgramResult.DATE_ROLLBACK, store.validateOn(SLEEP, true, date.plusDays(2)))
        assertEquals(GuidedProgramResult.DATE_ROLLBACK, store.startOn(STUDIES, date.plusDays(2)))
        assertEquals(GuidedProgramResult.DATE_ROLLBACK, store.resetOn(SLEEP, date.plusDays(2)))
        assertTrue(store.progress(SLEEP, date)!!.dateRollback)
        assertFalse(store.progress(SLEEP, date)!!.canValidateToday)
        assertEquals(saved, prefs.getString(GuidedProgramStore.KEY, null))
        assertEquals(GuidedProgramResult.ALREADY_VALIDATED_TODAY, store.validateOn(SLEEP, true, date.plusDays(3)))
        assertEquals(GuidedProgramResult.SUCCESS, store.validateOn(SLEEP, true, date.plusDays(4)))
    }

    @Test
    fun enrollmentDatePreventsValidatingBeforeEnrollmentEvenWithoutCompletedDays() {
        val (store, _) = fixture()
        store.startOn(SLEEP, date)
        assertEquals(GuidedProgramResult.DATE_ROLLBACK, store.validateOn(SLEEP, true, date.minusDays(1)))
        assertEquals(GuidedProgramResult.DATE_ROLLBACK, store.startOn(STUDIES, date.minusDays(1)))
        assertEquals(GuidedProgramResult.SUCCESS, store.validateOn(SLEEP, true, date))
    }

    @Test
    fun publicValidationRejectsAStaleCheckboxDateWithoutSubmittingRetroactiveProgress() {
        val (store, prefs) = fixture()
        val today = LocalDate.now()
        store.startOn(SLEEP, today)
        val before = prefs.all
        assertEquals(GuidedProgramResult.DAY_CHANGED, store.validateToday(SLEEP, true, today.minusDays(1)))
        assertEquals(GuidedProgramResult.DAY_CHANGED, store.validateToday(SLEEP, true, today.plusDays(1)))
        assertEquals(before, prefs.all)
        assertEquals(GuidedProgramResult.SUCCESS, store.validateToday(SLEEP, true, today))
        assertEquals(listOf(today), store.progress(SLEEP, today)!!.validatedDates)
    }

    @Test
    fun localDatesHandleShortAndLongDstDaysAndRepeatedHours() {
        val paris = ZoneId.of("Europe/Paris")
        val springStart = Instant.parse("2026-03-28T23:30:00Z")
        val springSame = Instant.parse("2026-03-29T21:30:00Z")
        val springNext = Instant.parse("2026-03-29T22:30:00Z")
        assertEquals(23, Duration.between(springStart, springNext).toHours())
        val (spring, _) = fixture()
        spring.startOn(SLEEP, springStart.atZone(paris).toLocalDate())
        assertEquals(GuidedProgramResult.SUCCESS, spring.validateOn(SLEEP, true, springStart.atZone(paris).toLocalDate()))
        assertEquals(GuidedProgramResult.ALREADY_VALIDATED_TODAY, spring.validateOn(SLEEP, true, springSame.atZone(paris).toLocalDate()))
        assertEquals(GuidedProgramResult.SUCCESS, spring.validateOn(SLEEP, true, springNext.atZone(paris).toLocalDate()))

        val fallStart = Instant.parse("2026-10-24T22:30:00Z")
        val fallFirstHour = Instant.parse("2026-10-25T00:30:00Z")
        val fallRepeatedHour = Instant.parse("2026-10-25T01:30:00Z")
        val fallNext = Instant.parse("2026-10-25T23:30:00Z")
        assertEquals(25, Duration.between(fallStart, fallNext).toHours())
        val (fall, _) = fixture()
        fall.startOn(SLEEP, fallStart.atZone(paris).toLocalDate())
        assertEquals(GuidedProgramResult.SUCCESS, fall.validateOn(SLEEP, true, fallFirstHour.atZone(paris).toLocalDate()))
        assertEquals(GuidedProgramResult.ALREADY_VALIDATED_TODAY, fall.validateOn(SLEEP, true, fallRepeatedHour.atZone(paris).toLocalDate()))
        assertEquals(GuidedProgramResult.SUCCESS, fall.validateOn(SLEEP, true, fallNext.atZone(paris).toLocalDate()))
    }

    @Test
    fun malformedJsonAndWrongPreferenceTypeFallBackToSafeEmptyPreview() {
        val (store, prefs) = fixture()
        for (raw in listOf("not json", "[]", "{", "x".repeat(20_001))) {
            prefs.edit().putString(GuidedProgramStore.KEY, raw).commit()
            val before = prefs.all
            assertNull(store.activeProgramId())
            assertTrue(store.entries(date).all { it.progress == null })
            assertEquals(0, store.exportJson().length())
            assertEquals(before, prefs.all)
        }
        prefs.edit().putInt(GuidedProgramStore.KEY, 42).commit()
        assertTrue(store.entries(date).all { it.progress == null })
    }

    @Test
    fun corruptObjectsAreWhitelistedBoundedAndDatesRemainStrictlyOrdered() {
        val (store, prefs) = fixture()
        val dates = JSONArray().put(date.minusDays(1).toString()).put("bad-date").put(date.toString())
            .put(date.toString()).put(date.plusDays(2).toString()).put(date.plusDays(1).toString()).put(date.plusDays(3).toString())
        val objects = JSONArray().put(JSONObject().put("programId", "invented").put("startedOn", date.toString())
            .put("validatedDates", JSONArray().put(date.toString())))
            .put(stored(SLEEP, dates).put("privateQuestion", "nested-private-question"))
            .put(stored(SLEEP, JSONArray().put(date.plusDays(8).toString())))
            .put(stored(STUDIES, JSONArray((0 until 40).map { date.plusDays(it.toLong()).toString() })))
            .put(stored(SOCIAL, JSONArray()))
        prefs.edit().putString(GuidedProgramStore.KEY, JSONObject().put("activeProgramId", "invented")
            .put("lastValidationDate", "2026-1-2").put("programs", objects).put("privateQuestion", "outer-private-question").toString()).commit()
        val before = prefs.all
        assertNull(store.activeProgramId())
        assertEquals(listOf(date, date.plusDays(2), date.plusDays(3)), store.progress(SLEEP, date)!!.validatedDates)
        assertEquals(14, store.progress(STUDIES, date)!!.completedDays)
        assertEquals(3, store.exportJson().length())
        assertEquals(before, prefs.all)
        val export = store.exportJson().toString()
        assertFalse(export.contains("invented"))
        assertFalse(export.contains("privateQuestion"))
        assertFalse(export.contains("private-question"))
        assertFalse(export.contains(GUIDED_PROGRAMS.first().days.first().activity.french))
        assertTrue(store.progress(SLEEP, date)!!.dateRollback)
    }

    @Test
    fun exportIsReadOnlyAndRetainsOnlyProgramIdsAndIsoDates() {
        val (store, prefs) = fixture()
        prefs.edit().putString("accountEmail", "private@example.invalid").commit()
        store.startOn(SLEEP, date)
        store.validateOn(SLEEP, true, date)
        val before = prefs.all
        val exported = store.exportJson().getJSONObject(0)
        assertEquals(before, prefs.all)
        assertEquals(setOf("programId", "startedOn", "validatedDates", "activeProgramId", "lastValidationDate"),
            exported.keys().asSequence().toSet())
        assertEquals(SLEEP, exported.getString("programId"))
        assertEquals(SLEEP, exported.getString("activeProgramId"))
        assertEquals(date.toString(), exported.getString("startedOn"))
        assertEquals(date.toString(), exported.getString("lastValidationDate"))
        assertEquals(listOf(date.toString()), exported.getJSONArray("validatedDates").let { array ->
            (0 until array.length()).map { array.getString(it) }
        })
        assertFalse(exported.toString().contains("private@example.invalid"))
    }

    @Test
    fun durableWriteFailuresNeverReportSuccessfulEnrollmentOrValidation() {
        val (store, prefs) = fixture()
        prefs.rejectCommit = true
        assertEquals(GuidedProgramResult.STORAGE_ERROR, store.startOn(SLEEP, date))
        assertNull(store.progress(SLEEP, date))
        prefs.rejectCommit = false
        store.startOn(SLEEP, date)
        val before = prefs.all
        prefs.rejectCommit = true
        assertEquals(GuidedProgramResult.STORAGE_ERROR, store.validateOn(SLEEP, true, date))
        assertEquals(GuidedProgramResult.STORAGE_ERROR, store.resetOn(SLEEP, date))
        assertEquals(GuidedProgramResult.STORAGE_ERROR, store.discard(SLEEP))
        assertEquals(before, prefs.all)
        assertEquals(0, store.progress(SLEEP, date)!!.completedDays)
    }

    @Test
    fun globalErasureClearsEnrollmentDailyGuardAndProgressForExistingStoreInstances() {
        val (store, prefs) = fixture()
        store.startOn(SLEEP, date)
        store.validateOn(SLEEP, true, date)
        assertTrue(Store(prefs).clearPersonalData())
        assertTrue(prefs.all.isEmpty())
        assertNull(store.activeProgramId())
        assertTrue(store.entries(date).all { it.progress == null })
        assertEquals(0, store.exportJson().length())
        assertEquals(GuidedProgramResult.ULTRA_REQUIRED, store.startOn(SLEEP, date))
        prefs.edit().putString("plan", PlanType.ULTRA.name).commit()
        assertEquals(GuidedProgramResult.SUCCESS, store.startOn(SLEEP, date))
        assertEquals(GuidedProgramResult.SUCCESS, store.validateOn(SLEEP, true, date))
    }

    @Test
    fun discardRemainsAvailableAfterDowngradeAndDoesNotDeleteOtherPrograms() {
        val (store, prefs) = fixture()
        store.startOn(SLEEP, date)
        store.validateOn(SLEEP, true, date)
        store.startOn(STUDIES, date.plusDays(1))
        prefs.edit().putString("plan", PlanType.FREE.name).commit()
        assertEquals(GuidedProgramResult.SUCCESS, store.discard(SLEEP))
        assertNull(store.progress(SLEEP, date.plusDays(1)))
        assertEquals(STUDIES, store.activeProgramId())
        assertEquals(0, store.progress(STUDIES, date.plusDays(1))!!.completedDays)
    }

    @Test
    fun discardingAllProgressExportsRetainedDailyGuardUntilGlobalErasure() {
        val (store, prefs) = fixture()
        store.startOn(SLEEP, date)
        store.validateOn(SLEEP, true, date)
        assertEquals(GuidedProgramResult.SUCCESS, store.discard(SLEEP))
        assertTrue(store.entries(date).all { it.progress == null })
        val metadata = store.exportJson()
        assertEquals(1, metadata.length())
        assertEquals(setOf("lastValidationDate"), metadata.getJSONObject(0).keys().asSequence().toSet())
        assertEquals(date.toString(), metadata.getJSONObject(0).getString("lastValidationDate"))
        store.startOn(STUDIES, date)
        assertEquals(GuidedProgramResult.ALREADY_VALIDATED_TODAY, store.validateOn(STUDIES, true, date))
        assertTrue(Store(prefs).clearPersonalData())
        assertEquals(0, store.exportJson().length())
        assertFalse(prefs.contains(GuidedProgramStore.KEY))
    }

    private fun stored(id: String, dates: JSONArray): JSONObject = JSONObject().put("programId", id)
        .put("startedOn", date.toString()).put("validatedDates", dates)

    private fun fixture(): Pair<GuidedProgramStore, GuidedPreferences> {
        val prefs = GuidedPreferences()
        prefs.edit().putString("plan", PlanType.ULTRA.name).commit()
        prefs.durableWrites = 0
        return GuidedProgramStore(prefs) to prefs
    }

    companion object {
        private const val SLEEP = "sleep7days"
        private const val STUDIES = "studies14days"
        private const val SOCIAL = "socialmedia30days"
    }
}

private class GuidedPreferences : SharedPreferences {
    private val values = mutableMapOf<String, Any>()
    var rejectCommit = false
    var durableWrites = 0
    var asyncWrites = 0
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
    override fun edit(): SharedPreferences.Editor = object : SharedPreferences.Editor {
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
            durableWrites++
            if (rejectCommit) return false
            if (clearValues) values.clear()
            pending.forEach { (key, value) -> if (value == null) values.remove(key) else values[key] = value }
            return true
        }
        override fun apply() { asyncWrites++; commit() }
    }
}
