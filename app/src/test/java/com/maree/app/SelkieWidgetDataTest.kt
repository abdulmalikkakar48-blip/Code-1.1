package com.maree.app

import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SelkieWidgetDataTest {
    @Test fun freeEntitlementNeverExposesStoredPaidPointsOrChallenge() {
        val (store, _) = fixture(PlanType.ULTRA, series = 1, completed = listOf(10, 11))
        val exported = store.exportLocalState()
        assertEquals("ULTRA", exported.getJSONObject("membership").getString("plan"))
        assertEquals(13, exported.getJSONObject("points").getInt("quarterUnits"))

        val data = widgetData(exported, PlanType.FREE, store.pointUnits())

        assertFalse(data.paid)
        assertEquals(0, data.pointUnits)
        assertNull(data.challengeIndex)
    }

    @Test fun paidPlansChooseTheFirstUnfinishedChallengeFromTheStoredCurrentSeries() {
        for (plan in listOf(PlanType.PREMIUM, PlanType.ULTRA)) {
            val (store, _) = fixture(plan, series = 1, completed = listOf(10, 11, 14))
            val exported = store.exportLocalState()
            val group = exported.getJSONObject("standardChallenges")
            assertEquals(1, group.getInt("currentSeries"))
            assertEquals(20, group.getJSONArray("challenges").length())

            val data = widgetData(exported, plan, store.pointUnits())

            assertTrue(data.paid)
            assertEquals(12, data.challengeIndex)
            assertEquals(13, data.pointUnits)
        }
    }

    @Test fun partialDailyValidationRemainsUnfinishedAndCanBeSuggested() {
        val (store, prefs) = fixture(PlanType.PREMIUM)
        prefs.edit().putString("challengeDays", JSONObject().put("0", JSONArray()
            .put("2026-10-11").put("2026-10-12").put("2026-10-13")).toString()).commit()
        val exported = store.exportLocalState()
        val first = exported.getJSONObject("standardChallenges").getJSONArray("challenges").getJSONObject(0)
        assertEquals(3, first.getInt("completedDays"))
        assertFalse(first.getBoolean("completed"))

        assertEquals(0, widgetData(exported, PlanType.PREMIUM, store.pointUnits()).challengeIndex)
    }

    @Test fun allCompletedCurrentSeriesDoesNotSuggestAnUnfinishedOtherSeries() {
        for (series in listOf(0, 1)) {
            val (store, _) = fixture(PlanType.PREMIUM, series, (series * 10 until series * 10 + 10).toList())
            val data = widgetData(store.exportLocalState(), PlanType.PREMIUM, store.pointUnits())
            assertTrue(data.paid)
            assertNull(data.challengeIndex)
            assertEquals(13, data.pointUnits)
        }
    }

    @Test fun absentOrMalformedChallengeContainersGiveNoChallenge() {
        val (store, _) = fixture(PlanType.ULTRA)
        val exported = store.exportLocalState()
        val invalidStates = listOf(
            JSONObject(exported.toString()).apply { remove("standardChallenges") },
            JSONObject(exported.toString()).put("standardChallenges", "broken"),
            JSONObject(exported.toString()).put("standardChallenges", JSONArray()),
            JSONObject(exported.toString()).put("standardChallenges", JSONObject().put("challenges", "broken")),
            JSONObject(exported.toString()).put("standardChallenges", JSONObject().put("challenges", JSONArray()))
        )
        for (state in invalidStates) {
            val data = widgetData(state, PlanType.ULTRA, store.pointUnits())
            assertTrue(data.paid)
            assertEquals(13, data.pointUnits)
            assertNull(data.challengeIndex)
        }
    }

    @Test fun malformedRowsAndIndexesAreSkippedBeforeAValidAuthoredChallenge() {
        val (store, _) = fixture(PlanType.PREMIUM)
        val exported = store.exportLocalState()
        val rows = JSONArray()
            .put(JSONObject.NULL).put("not a challenge")
            .put(row(-1)).put(row(20)).put(row("wrong"))
            .put(row(0).apply { remove("index") })
            .put(row(1).put("completed", true))
            .put(row(2).put("inCurrentSeries", false))
            .put(row(3).apply { remove("completed") })
            .put(row(4).put("inCurrentSeries", "true"))
            .put(row(5).put("completed", "false"))
            .put(row(6.5))
            .put(row("7"))
            .put(row(19))
        exported.getJSONObject("standardChallenges").put("challenges", rows)

        assertEquals(19, widgetData(exported, PlanType.PREMIUM, store.pointUnits()).challengeIndex)
        rows.remove(rows.length() - 1)
        assertNull(widgetData(exported, PlanType.PREMIUM, store.pointUnits()).challengeIndex)
    }

    @Test fun quartersArePreservedWithoutPenaltyRewardOrCycleMutation() {
        val (store, prefs) = fixture(PlanType.PREMIUM, series = 1, completed = listOf(10))
        prefs.edit().putLong("periodStart", 1L).putLong("challengeStart", 1L)
            .putBoolean("periodBreach", true).commit()
        val before = prefs.all
        val exported = store.exportLocalState()

        for (units in listOf(-9, 0, 1, 2, 3, 13, 399, 400, 640, Int.MAX_VALUE)) {
            val data = widgetData(exported, PlanType.PREMIUM, units)
            assertEquals(units.coerceIn(0, MAX_POINT_UNITS), data.pointUnits)
            assertEquals(11, data.challengeIndex)
        }
        assertEquals(before, prefs.all)
        assertEquals(13, store.pointUnits())
        assertEquals(exported.toString(), store.exportLocalState().toString())
    }

    private fun row(index: Any): JSONObject = JSONObject().put("index", index)
        .put("inCurrentSeries", true).put("completed", false)

    private fun fixture(plan: PlanType, series: Int = 0, completed: List<Int> = emptyList()):
        Pair<Store, WidgetPreferences> {
        val prefs = WidgetPreferences()
        prefs.edit().putInt("pointUnits", 13).putLong("challengeNumber", series.toLong())
            .putString("checked", JSONArray(completed).toString()).commit()
        val store = Store(prefs)
        store.setDemoPlan(plan)
        return store to prefs
    }
}

private class WidgetPreferences : SharedPreferences {
    private val values = mutableMapOf<String, Any>()
    override fun getAll(): MutableMap<String, *> = values.toMutableMap()
    override fun getString(key: String, defValue: String?): String? = values[key] as? String ?: defValue
    override fun getStringSet(key: String, defValues: MutableSet<String>?): MutableSet<String>? = defValues
    override fun getInt(key: String, defValue: Int): Int = values[key] as? Int ?: defValue
    override fun getLong(key: String, defValue: Long): Long = values[key] as? Long ?: defValue
    override fun getFloat(key: String, defValue: Float): Float = values[key] as? Float ?: defValue
    override fun getBoolean(key: String, defValue: Boolean): Boolean = values[key] as? Boolean ?: defValue
    override fun contains(key: String): Boolean = values.containsKey(key)
    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) = Unit
    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) = Unit
    override fun edit(): SharedPreferences.Editor = object : SharedPreferences.Editor {
        private val changes = mutableMapOf<String, Any?>()
        private var clear = false
        override fun putString(key: String, value: String?) = apply { changes[key] = value }
        override fun putStringSet(key: String, values: MutableSet<String>?) = apply { changes[key] = values }
        override fun putInt(key: String, value: Int) = apply { changes[key] = value }
        override fun putLong(key: String, value: Long) = apply { changes[key] = value }
        override fun putFloat(key: String, value: Float) = apply { changes[key] = value }
        override fun putBoolean(key: String, value: Boolean) = apply { changes[key] = value }
        override fun remove(key: String) = apply { changes[key] = null }
        override fun clear() = apply { clear = true }
        override fun commit(): Boolean {
            if (clear) values.clear()
            changes.forEach { (key, value) -> if (value == null) values.remove(key) else values[key] = value }
            return true
        }
        override fun apply() { commit() }
    }
}
