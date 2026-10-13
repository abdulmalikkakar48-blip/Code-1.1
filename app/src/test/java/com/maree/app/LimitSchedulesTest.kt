package com.maree.app

import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

class LimitSchedulesTest {
    @Test
    fun premiumAndUltraCanCreateEditToggleAndDeleteWithOneQuarterPoint() {
        for (plan in listOf(PlanType.PREMIUM, PlanType.ULTRA)) {
            val prefs = ScheduleMemoryPreferences()
            val store = LimitScheduleStore(prefs)
            val original = rule()

            assertNull(store.save(original, plan, 1))
            assertEquals(listOf(original), LimitScheduleStore(prefs).entries())
            val edited = original.copy(name = "Reading", limitMinutes = 45)
            assertNull(store.save(edited, plan, 1))
            assertEquals(listOf(edited), store.entries())
            assertNull(store.save(edited.copy(enabled = false), plan, 1))
            assertFalse(store.entries().single().enabled)
            assertNull(store.save(edited.copy(enabled = true), plan, 1))
            assertTrue(store.entries().single().enabled)
            assertNull(store.remove(original.id, plan, 1))
            assertTrue(LimitScheduleStore(prefs).entries().isEmpty())
        }
    }

    @Test
    fun freeAndNonPositivePointsRejectEveryMutationAndPreserveStoredRules() {
        for ((plan, points) in listOf(PlanType.FREE to 400, PlanType.PREMIUM to 0,
                PlanType.ULTRA to 0, PlanType.PREMIUM to -1)) {
            val prefs = ScheduleMemoryPreferences()
            val store = LimitScheduleStore(prefs)
            val original = rule()
            assertNull(store.save(original, PlanType.PREMIUM, 1))
            val before = prefs.all

            assertNotNull(store.save(rule(2, packageName = "com.example.other"), plan, points))
            assertNotNull(store.save(original.copy(name = "Changed"), plan, points))
            assertNotNull(store.save(original.copy(enabled = false), plan, points))
            assertNotNull(store.remove(original.id, plan, points))

            assertEquals(before, prefs.all)
            assertEquals(listOf(original), store.entries())
        }
    }

    @Test
    fun persistedDowngradeOrZeroPointsOverridesStaleMutationArguments() {
        for ((storedPlan, storedPoints) in listOf(PlanType.FREE to 400,
                PlanType.PREMIUM to 0, PlanType.ULTRA to 0)) {
            val prefs = ScheduleMemoryPreferences()
            val store = LimitScheduleStore(prefs)
            val original = rule()
            assertNull(store.save(original, PlanType.PREMIUM, 400))
            prefs.edit().putString("plan", storedPlan.name).putInt("pointUnits", storedPoints).apply()
            val before = prefs.all

            assertNotNull(store.save(original.copy(limitMinutes = 60), PlanType.ULTRA, 400))
            assertNotNull(store.save(original.copy(enabled = false), PlanType.ULTRA, 400))
            assertNotNull(store.save(rule(2, packageName = "com.example.other"), PlanType.ULTRA, 400))
            assertNotNull(store.remove(original.id, PlanType.ULTRA, 400))
            assertEquals(before, prefs.all)
        }
    }

    @Test
    fun trimmingAndCanonicalUuidArePersistedWithoutDroppingUnicode() {
        val prefs = ScheduleMemoryPreferences()
        val store = LimitScheduleStore(prefs)
        val original = rule().copy(id = "AABBCCDD-1234-5678-9ABC-001122334455",
            name = "  Déconnecter 🐚  ", packageName = "  $PACKAGE  ")

        assertNull(store.save(original, PlanType.PREMIUM, 1))

        assertEquals(original.copy(id = original.id.lowercase(), name = "Déconnecter 🐚",
            packageName = PACKAGE), LimitScheduleStore(prefs).entries().single())
    }

    @Test
    fun invalidBoundsAndAbbreviatedIdsAreRejectedWithoutClippingValues() {
        val prefs = ScheduleMemoryPreferences()
        val store = LimitScheduleStore(prefs)
        val invalid = listOf(
            rule().copy(id = ""), rule().copy(id = "not-a-uuid"),
            rule().copy(id = "1-1-1-1-1"), rule().copy(name = "   "),
            rule().copy(name = "x".repeat(41)), rule().copy(packageName = "   "),
            rule().copy(packageName = "p".repeat(251)), rule().copy(weekdays = emptySet()),
            rule().copy(weekdays = setOf(0)), rule().copy(weekdays = setOf(1, 8)),
            rule().copy(startMinute = -1), rule().copy(startMinute = 1440),
            rule().copy(endMinute = -1), rule().copy(endMinute = 1440),
            rule().copy(endMinute = rule().startMinute),
            rule().copy(limitMinutes = 4), rule().copy(limitMinutes = 241)
        )

        for (candidate in invalid) {
            val before = prefs.all
            assertNotNull("Accepted invalid rule: $candidate", store.save(candidate, PlanType.PREMIUM, 1))
            assertEquals(before, prefs.all)
            assertTrue(store.entries().isEmpty())
        }
    }

    @Test
    fun inclusiveValidationExtremesAreAccepted() {
        val store = LimitScheduleStore(ScheduleMemoryPreferences())
        val minimum = rule(name = "x", startMinute = 0, endMinute = 1, limitMinutes = 5)
        val maximum = rule(2, name = "x".repeat(40), packageName = "p".repeat(250),
            weekdays = (1..7).toSet(), startMinute = 1439, endMinute = 0, limitMinutes = 240)

        assertNull(store.save(minimum, PlanType.PREMIUM, 1))
        assertNull(store.save(maximum, PlanType.PREMIUM, 1))
        assertEquals(listOf(minimum, maximum), store.entries())
    }

    @Test
    fun twentyRulesAllowUpdatesButRejectTheTwentyFirst() {
        val prefs = ScheduleMemoryPreferences()
        val store = LimitScheduleStore(prefs)
        val rules = (1..20).map { rule(it, packageName = "com.example.app$it") }
        rules.forEach { assertNull(store.save(it, PlanType.PREMIUM, 1)) }
        val before = prefs.all

        assertNotNull(store.save(rule(21, packageName = "com.example.app21"), PlanType.ULTRA, 1))
        assertEquals(before, prefs.all)
        assertNull(store.save(rules.first().copy(limitMinutes = 60), PlanType.ULTRA, 1))
        assertEquals(20, store.entries().size)
        assertEquals(60, store.entries().single { it.id == rules.first().id }.limitMinutes)
        assertNull(store.remove(rules.last().id, PlanType.PREMIUM, 1))
        assertNull(store.save(rule(21, packageName = "com.example.app21"), PlanType.PREMIUM, 1))
        assertEquals(20, store.entries().size)
    }

    @Test
    fun samePackageOverlapsAreRejectedButAdjacentAndDifferentWeekdaysAreAllowed() {
        val prefs = ScheduleMemoryPreferences()
        val store = LimitScheduleStore(prefs)
        val first = rule(startMinute = 9 * 60, endMinute = 10 * 60)
        assertNull(store.save(first, PlanType.PREMIUM, 1))

        for (overlap in listOf(
                rule(2, startMinute = 9 * 60 + 30, endMinute = 10 * 60 + 30),
                rule(2, startMinute = 8 * 60, endMinute = 11 * 60),
                rule(2, startMinute = 9 * 60, endMinute = 10 * 60))) {
            val before = prefs.all
            assertNotNull(store.save(overlap, PlanType.PREMIUM, 1))
            assertEquals(before, prefs.all)
        }
        assertNull(store.save(rule(2, startMinute = 10 * 60, endMinute = 11 * 60), PlanType.PREMIUM, 1))
        assertNull(store.save(rule(3, weekdays = setOf(2), startMinute = 9 * 60,
            endMinute = 10 * 60), PlanType.PREMIUM, 1))
        assertNull(store.save(rule(4, packageName = "com.example.other", startMinute = 9 * 60,
            endMinute = 10 * 60), PlanType.PREMIUM, 1))
        // Editing the same id must not mistake its old window for a competing rule.
        assertNull(store.save(first.copy(name = "Renamed"), PlanType.PREMIUM, 1))
        assertEquals(4, store.entries().size)
    }

    @Test
    fun overnightOverlapUsesTheFollowingDayAndWrapsSundayToMonday() {
        for ((startDay, nextDay) in listOf(1 to 2, 7 to 1)) {
            val store = LimitScheduleStore(ScheduleMemoryPreferences())
            val overnight = rule(weekdays = setOf(startDay), startMinute = 22 * 60, endMinute = 2 * 60)
            assertNull(store.save(overnight, PlanType.PREMIUM, 1))

            assertNotNull(store.save(rule(2, weekdays = setOf(nextDay), startMinute = 60,
                endMinute = 3 * 60), PlanType.PREMIUM, 1))
            assertNotNull(store.save(rule(2, weekdays = setOf(startDay), startMinute = 23 * 60,
                endMinute = 60), PlanType.PREMIUM, 1))
            assertNull(store.save(rule(2, weekdays = setOf(nextDay), startMinute = 2 * 60,
                endMinute = 3 * 60), PlanType.PREMIUM, 1))
            assertNull(store.save(rule(3, weekdays = setOf(startDay), startMinute = 60,
                endMinute = 2 * 60), PlanType.PREMIUM, 1))
        }
    }

    @Test
    fun disabledOverlappingRulesCanBeStoredButEnablingMustPassOverlapValidation() {
        val prefs = ScheduleMemoryPreferences()
        val store = LimitScheduleStore(prefs)
        val active = rule()
        val disabled = rule(2, enabled = false)
        assertNull(store.save(active, PlanType.PREMIUM, 1))
        assertNull(store.save(disabled, PlanType.PREMIUM, 1))
        val before = prefs.all

        assertNotNull(store.save(disabled.copy(enabled = true), PlanType.PREMIUM, 1))
        assertEquals(before, prefs.all)
        assertNull(store.save(active.copy(enabled = false), PlanType.PREMIUM, 1))
        assertNull(store.save(disabled.copy(enabled = true), PlanType.PREMIUM, 1))
        assertEquals(listOf(false, true), store.entries().map { it.enabled })
    }

    @Test
    fun effectiveLimitUsesInclusiveStartExclusiveEndAndSelectedIsoWeekdays() {
        val rules = listOf(rule(weekdays = setOf(1, 3), startMinute = 9 * 60,
            endMinute = 10 * 60, limitMinutes = 15))

        assertEquals(90, effective(rules, "2026-10-12T08:59:59"))
        assertEquals(15, effective(rules, "2026-10-12T09:00:00"))
        assertEquals(15, effective(rules, "2026-10-12T09:59:59"))
        assertEquals(90, effective(rules, "2026-10-12T10:00:00"))
        assertEquals(90, effective(rules, "2026-10-13T09:30:00"))
        assertEquals(15, effective(rules, "2026-10-14T09:30:00"))
    }

    @Test
    fun overnightAppliesToTheStartWeekdayAndFollowingMorningOnly() {
        val rules = listOf(rule(weekdays = setOf(1), startMinute = 22 * 60,
            endMinute = 2 * 60, limitMinutes = 25))

        assertEquals(90, effective(rules, "2026-10-12T01:00:00"))
        assertEquals(90, effective(rules, "2026-10-12T21:59:59"))
        assertEquals(25, effective(rules, "2026-10-12T22:00:00"))
        assertEquals(25, effective(rules, "2026-10-13T00:00:00"))
        assertEquals(25, effective(rules, "2026-10-13T01:59:59"))
        assertEquals(90, effective(rules, "2026-10-13T02:00:00"))
        assertEquals(90, effective(rules, "2026-10-13T22:30:00"))
    }

    @Test
    fun sundayOvernightWrapsIntoMondayAndMidnightEndHasNoNextDayAllowance() {
        val sunday = listOf(rule(weekdays = setOf(7), startMinute = 23 * 60,
            endMinute = 60, limitMinutes = 10))
        assertEquals(10, effective(sunday, "2026-10-11T23:00:00"))
        assertEquals(10, effective(sunday, "2026-10-12T00:59:59"))
        assertEquals(90, effective(sunday, "2026-10-12T01:00:00"))

        val midnight = listOf(rule(startMinute = 23 * 60, endMinute = 0, limitMinutes = 10))
        assertEquals(10, effective(midnight, "2026-10-12T23:59:59"))
        assertEquals(90, effective(midnight, "2026-10-13T00:00:00"))
    }

    @Test
    fun effectiveLimitUsesExplicitTimezoneRatherThanUtcWeekday() {
        val rules = listOf(rule(weekdays = setOf(7), startMinute = 22 * 60,
            endMinute = 23 * 60, limitMinutes = 20))
        val now = LocalDateTime.parse("2026-10-12T05:30:00").atZone(UTC).toInstant().toEpochMilli()

        assertEquals(20, effectiveScheduledLimit(rules, PACKAGE, 90, PlanType.PREMIUM,
            now, ZoneId.of("America/Los_Angeles")))
        assertEquals(90, effectiveScheduledLimit(rules, PACKAGE, 90, PlanType.PREMIUM, now, UTC))
    }

    @Test
    fun freeDowngradeDisabledRuleAndDifferentPackageUseTheBaseLimit() {
        val active = rule(limitMinutes = 120)
        val time = "2026-10-12T09:30:00"

        // A scheduled limit replaces the base allowance in either direction.
        assertEquals(120, effective(listOf(active), time, plan = PlanType.PREMIUM))
        assertEquals(120, effective(listOf(active), time, plan = PlanType.ULTRA))
        assertEquals(90, effective(listOf(active), time, plan = PlanType.FREE))
        assertEquals(90, effective(listOf(active.copy(enabled = false)), time))
        assertEquals(90, effective(listOf(active), time, packageName = "com.example.other"))
        assertEquals(90, effective(emptyList(), time))
    }

    @Test
    fun malformedJsonAndWrongPreferenceTypeReturnNoRulesWithoutRewritingStorage() {
        for (raw in listOf("", "{", "{}", "null", "[null,17,\"text\"]")) {
            val prefs = ScheduleMemoryPreferences()
            prefs.edit().putString(PREF_KEY, raw).apply()
            val before = prefs.all

            assertTrue(LimitScheduleStore(prefs).entries().isEmpty())
            assertEquals(0, LimitScheduleStore(prefs).exportJson().length())
            assertEquals(before, prefs.all)
        }
        val prefs = ScheduleMemoryPreferences()
        prefs.edit().putInt(PREF_KEY, 123).apply()
        val before = prefs.all
        assertTrue(LimitScheduleStore(prefs).entries().isEmpty())
        assertEquals(before, prefs.all)
    }

    @Test
    fun malformedPersistedEntriesAreSkippedWhileValidNeighborsSurvive() {
        val prefs = ScheduleMemoryPreferences()
        val first = rule()
        val last = rule(50, packageName = "com.example.last")
        fun candidate(number: Int) = json(rule(number, packageName = "com.example.malformed$number"))
        val malformed = listOf(
            candidate(2).put("id", "1-1-1-1-1"),
            candidate(3).put("name", " "),
            candidate(4).put("name", 123),
            candidate(5).put("packageName", JSONObject.NULL),
            candidate(6).put("weekdays", JSONArray()),
            candidate(7).put("weekdays", JSONArray().put(0)),
            candidate(8).put("weekdays", JSONArray().put(8)),
            candidate(9).put("weekdays", JSONArray().put("1")),
            candidate(10).put("weekdays", JSONArray().put(1.5)),
            candidate(11).put("startMinute", "540"),
            candidate(12).put("startMinute", 540.5),
            candidate(13).put("endMinute", 1440),
            candidate(14).put("endMinute", 540),
            candidate(15).put("limitMinutes", 4),
            candidate(16).put("enabled", "true"),
            candidate(17).also { it.remove("id") }
        )
        val persisted = JSONArray().put(json(first))
        malformed.forEach { persisted.put(it) }
        persisted.put(json(last))
        prefs.edit().putString(PREF_KEY, persisted.toString()).apply()
        val before = prefs.all

        val store = LimitScheduleStore(prefs)
        assertEquals(listOf(first, last), store.entries())
        assertEquals(2, store.exportJson().length())
        assertEquals(before, prefs.all)
    }

    @Test
    fun persistedDuplicateIdsAndLaterOverlapsAreSkippedInStoredOrder() {
        val prefs = ScheduleMemoryPreferences()
        val first = rule()
        val adjacent = rule(4, startMinute = 10 * 60, endMinute = 11 * 60)
        val disabled = rule(5, enabled = false)
        val persisted = JSONArray().put(json(first))
            .put(json(first.copy(packageName = "com.example.duplicate")))
            .put(json(rule(3, startMinute = 9 * 60 + 30, endMinute = 10 * 60 + 30)))
            .put(json(adjacent)).put(json(disabled))
        prefs.edit().putString(PREF_KEY, persisted.toString()).apply()

        assertEquals(listOf(first, adjacent, disabled), LimitScheduleStore(prefs).entries())
    }

    @Test
    fun excessivePersistedRulesAreCappedAtTwentyWithoutChangingPreference() {
        val prefs = ScheduleMemoryPreferences()
        val rules = (1..21).map { rule(it, packageName = "com.example.app$it") }
        val array = JSONArray()
        rules.forEach { array.put(json(it)) }
        prefs.edit().putString(PREF_KEY, array.toString()).apply()
        val before = prefs.all

        assertEquals(rules.take(20), LimitScheduleStore(prefs).entries())
        assertEquals(before, prefs.all)
    }

    @Test
    fun exportUsesOnlyScheduleFieldsAndCannotMutateTheStore() {
        val prefs = ScheduleMemoryPreferences()
        val original = rule(name = "Lecture 🐚", weekdays = setOf(7, 1), enabled = false)
        val persisted = json(original).put("privateFutureField", "synthetic-secret")
        prefs.edit().putString(PREF_KEY, JSONArray().put(persisted).toString()).apply()
        val before = prefs.all
        val store = LimitScheduleStore(prefs)

        val exported = store.exportJson()
        val item = exported.getJSONObject(0)
        assertEquals(setOf("id", "name", "packageName", "weekdays", "startMinute",
            "endMinute", "limitMinutes", "enabled"), item.keys().asSequence().toSet())
        assertEquals(original.id, item.getString("id"))
        assertEquals(original.name, item.getString("name"))
        assertEquals(original.packageName, item.getString("packageName"))
        assertEquals(original.weekdays, (0 until item.getJSONArray("weekdays").length())
            .map { item.getJSONArray("weekdays").getInt(it) }.toSet())
        assertEquals(original.startMinute, item.getInt("startMinute"))
        assertEquals(original.endMinute, item.getInt("endMinute"))
        assertEquals(original.limitMinutes, item.getInt("limitMinutes"))
        assertFalse(item.getBoolean("enabled"))
        assertFalse(exported.toString().contains("synthetic-secret"))
        item.put("name", "Changed externally")
        assertEquals(listOf(original), store.entries())
        assertEquals(before, prefs.all)
    }

    @Test
    fun failedCommitsReportFailureAndKeepTheOriginalRulesForEveryMutation() {
        val prefs = ScheduleMemoryPreferences()
        val store = LimitScheduleStore(prefs)
        val original = rule()
        assertNull(store.save(original, PlanType.PREMIUM, 1))
        val before = prefs.all
        prefs.failCommit = true

        assertNotNull(store.save(rule(2, packageName = "com.example.other"), PlanType.PREMIUM, 1))
        assertNotNull(store.save(original.copy(limitMinutes = 60), PlanType.PREMIUM, 1))
        assertNotNull(store.save(original.copy(enabled = false), PlanType.PREMIUM, 1))
        assertNotNull(store.remove(original.id, PlanType.PREMIUM, 1))
        assertEquals(before, prefs.all)
        assertEquals(listOf(original), store.entries())
        assertEquals(listOf(original), LimitScheduleStore(prefs).entries())

        prefs.failCommit = false
        assertNull(store.save(original.copy(limitMinutes = 60), PlanType.PREMIUM, 1))
        assertEquals(60, store.entries().single().limitMinutes)
    }

    private fun effective(
        rules: List<LimitSchedule>,
        time: String,
        plan: PlanType = PlanType.PREMIUM,
        packageName: String = PACKAGE
    ): Int = effectiveScheduledLimit(rules, packageName, 90, plan,
        LocalDateTime.parse(time).atZone(UTC).toInstant().toEpochMilli(), UTC)

    private fun rule(
        number: Int = 1,
        name: String = "Morning",
        packageName: String = PACKAGE,
        weekdays: Set<Int> = setOf(1),
        startMinute: Int = 9 * 60,
        endMinute: Int = 10 * 60,
        limitMinutes: Int = 30,
        enabled: Boolean = true
    ) = LimitSchedule(UUID(0L, number.toLong()).toString(), name, packageName, weekdays,
        startMinute, endMinute, limitMinutes, enabled)

    private fun json(rule: LimitSchedule): JSONObject = JSONObject()
        .put("id", rule.id).put("name", rule.name).put("packageName", rule.packageName)
        .put("weekdays", JSONArray(rule.weekdays.sorted())).put("startMinute", rule.startMinute)
        .put("endMinute", rule.endMinute).put("limitMinutes", rule.limitMinutes)
        .put("enabled", rule.enabled)

    companion object {
        private const val PREF_KEY = "limitSchedules"
        private const val PACKAGE = "com.example.reader"
        private val UTC = ZoneId.of("UTC")
    }
}

/** In-memory Android preferences with atomic, controllable commit failures. */
private class ScheduleMemoryPreferences : SharedPreferences {
    private val values = mutableMapOf<String, Any>()
    var failCommit = false

    override fun getAll(): MutableMap<String, *> = values.toMutableMap()
    override fun getString(key: String, defValue: String?): String? = when (val value = values[key]) {
        null -> defValue
        is String -> value
        else -> throw ClassCastException("$key is not a String")
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
            if (failCommit) return false
            if (clearValues) values.clear()
            pending.forEach { (key, value) ->
                if (value == null) values.remove(key) else values[key] = value
            }
            return true
        }
        override fun apply() { commit() }
    }
}
