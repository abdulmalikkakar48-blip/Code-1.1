package com.maree.app

import android.content.SharedPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StoreTest {
    private lateinit var prefs: MemoryPreferences
    private lateinit var store: Store

    @Before
    fun setUp() {
        prefs = MemoryPreferences()
        prefs.edit().putInt("points", 100)
            .putLong("periodStart", NOW)
            .putLong("challengeStart", NOW)
            .putString("day", SimpleDateFormat("yyyyMMdd", Locale.ROOT).format(Date(NOW)))
            .apply()
        store = Store(prefs)
        assertNull(store.addApp(PACKAGE, 5, 0))
    }

    @Test
    fun uiRefreshDoesNotConsumeTheWarning() {
        val usage = reading(4 * MINUTE)
        repeat(3) { store.readSnapshot(usage, NOW) }

        assertEquals(listOf(PACKAGE to 1), store.tick(usage, NOW).warnings)
        assertTrue(Store(prefs).tick(usage, NOW + 1).warnings.isEmpty())
    }

    @Test
    fun uiRefreshLeavesTheInitialLockForTheMonitor() {
        val usage = reading(5 * MINUTE)

        assertEquals(0L, store.readSnapshot(usage, NOW).apps.single().lockedUntil)
        val tick = store.tick(usage, NOW)
        assertEquals(setOf(PACKAGE), tick.justLocked)
        assertEquals(NOW + DAY, tick.snapshot.apps.single().lockedUntil)
        assertEquals(100, tick.snapshot.points)
        assertTrue(Store(prefs).tick(usage, NOW + 1).justLocked.isEmpty())
    }

    @Test
    fun currentBreachDoesNotCancelACleanPreviousPeriodOrDisappear() {
        prefs.edit().putLong("periodStart", NOW - DAY).apply()

        val initialLock = store.tick(reading(5 * MINUTE), NOW)
        assertEquals(110, initialLock.snapshot.points)
        assertTrue(prefs.getBoolean("periodBreach", false))

        val nextPeriod = store.tick(reading(0), NOW + DAY)
        assertEquals(110, nextPeriod.snapshot.points)
        assertFalse(prefs.getBoolean("periodBreach", true))
    }

    @Test
    fun aPreviousBreachDoesNotCancelTheFollowingCleanPeriod() {
        prefs.edit().putLong("periodStart", NOW - DAY)
            .putBoolean("periodBreach", true).apply()

        assertEquals(100, store.tick(reading(0), NOW).snapshot.points)
        assertEquals(110, store.tick(reading(0), NOW + DAY).snapshot.points)
    }

    @Test
    fun snapshotCanAdvanceRewardsWithoutRepeatingThem() {
        prefs.edit().putLong("periodStart", NOW - DAY).apply()

        assertEquals(110, store.readSnapshot(reading(0), NOW).points)
        assertEquals(110, store.tick(reading(0), NOW).snapshot.points)
        assertEquals(110, Store(prefs).readSnapshot(reading(0), NOW).points)
    }

    @Test
    fun reopeningPenaltyPreservesItsCooldownAndPointsFloor() {
        store.tick(reading(5 * MINUTE), NOW)
        prefs.edit().putInt("points", 3).apply()

        assertTrue(store.penalizeReopening(PACKAGE, NOW + 1))
        assertEquals(0, store.points())
        assertFalse(store.penalizeReopening(PACKAGE, NOW + 29_999))
        assertTrue(store.penalizeReopening(PACKAGE, NOW + 30_001))
        assertEquals(0, store.points())
    }

    @Test
    fun lockExpiresWithAFullNewAllowanceAndResetCostsTenPoints() {
        store.tick(reading(5 * MINUTE), NOW)

        val expired = store.tick(reading(2 * MINUTE), NOW + DAY)
        assertEquals(0L, expired.snapshot.apps.single().lockedUntil)
        assertEquals(0L, expired.snapshot.apps.single().usedMillis)

        store.tick(reading(7 * MINUTE), NOW + DAY + 1)
        assertNull(store.resetApp(PACKAGE, 7 * MINUTE))
        val reset = store.readSnapshot(reading(7 * MINUTE), NOW + DAY + 2)
        assertEquals(0L, reset.apps.single().lockedUntil)
        assertEquals(0L, reset.apps.single().usedMillis)
        assertEquals(90, reset.points)
    }

    @Test
    fun challengesRewardEachFiveDistinctCompletionsAndAlternateAfterTwentyOneDays() {
        repeat(5) { assertTrue(store.completeChallenge(it, NOW)) }
        assertEquals(105, store.points())
        assertFalse(store.completeChallenge(0, NOW))
        assertFalse(store.completeChallenge(10, NOW))

        val nextCycle = store.readSnapshot(reading(0), NOW + 21 * DAY)
        assertEquals(1, nextCycle.challengeSeries)
        assertTrue(nextCycle.checkedChallenges.isEmpty())
        assertTrue(store.completeChallenge(10, NOW + 21 * DAY))
        assertFalse(store.completeChallenge(0, NOW + 21 * DAY))
    }

    @Test
    fun deletingPersonalDataRemovesLocksProgressAndAllStoredKeysBeforeRestart() {
        store.tick(reading(5 * MINUTE), NOW)
        assertTrue(store.penalizeReopening(PACKAGE, NOW + 1))
        repeat(5) { assertTrue(store.completeChallenge(it, NOW)) }
        // Même une clé ajoutée à l'avenir ne doit pas survivre à l'effacement complet.
        prefs.edit().putBoolean("premium", true).putString("futurePersonalSetting", "private").apply()
        assertTrue(prefs.contains("apps"))
        assertTrue(prefs.contains("checked"))
        assertTrue(prefs.contains("completedCount"))

        assertTrue(store.clearPersonalData())

        assertTrue(prefs.all.isEmpty())
        assertTrue(store.packages().isEmpty())
        assertEquals(100, store.points())
        assertFalse(store.isPremium())
        val restarted = Store(prefs)
        val snapshot = restarted.readSnapshot(UsageReading(emptyMap(), null))
        assertTrue(snapshot.apps.isEmpty())
        assertEquals(100, snapshot.points)
        assertEquals(0, snapshot.challengeSeries)
        assertTrue(snapshot.checkedChallenges.isEmpty())
        assertEquals(0, snapshot.totalCompletedChallenges)
        assertEquals(21, snapshot.daysUntilChallenges)
        assertFalse(prefs.contains("futurePersonalSetting"))
        assertTrue(prefs.contains("periodStart"))
        assertTrue(prefs.contains("challengeStart"))
    }

    private fun reading(millis: Long) = UsageReading(mapOf(PACKAGE to millis), PACKAGE)

    companion object {
        private const val NOW = 1_800_000_000_000L
        private const val MINUTE = 60_000L
        private const val DAY = 24 * 60 * MINUTE
        private const val PACKAGE = "com.example.social"
    }
}

/** Le contrat SharedPreferences suffit ici : aucun service Android ni émulateur n'est requis. */
private class MemoryPreferences : SharedPreferences {
    private val values = mutableMapOf<String, Any>()

    override fun getAll(): MutableMap<String, *> = values.toMutableMap()
    override fun getString(key: String, defValue: String?): String? = values[key] as? String ?: defValue
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
            if (clearValues) values.clear()
            pending.forEach { (key, value) ->
                if (value == null) values.remove(key) else values[key] = value
            }
            return true
        }
        override fun apply() { commit() }
    }
}
