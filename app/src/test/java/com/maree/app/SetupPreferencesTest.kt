package com.maree.app

import android.content.SharedPreferences
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SetupPreferencesTest {
    @Test
    fun existingPermissionGrantsNeverCompleteTheIntroduction() {
        val setup = SetupPreferences(BooleanPreferences())
        val granted = PermissionStatus(true, true, true)

        assertTrue(granted.readyForMonitoring)
        assertFalse(setup.isCompleted())
        assertFalse(setup.isMonitoringEnabled())
        setup.setMonitoringEnabled(true)
        assertFalse(setup.isMonitoringEnabled())
    }

    @Test
    fun explorationPersistsWithoutEnablingMonitoring() {
        val prefs = BooleanPreferences()
        SetupPreferences(prefs).complete(false)

        val reloaded = SetupPreferences(prefs)
        assertTrue(reloaded.isCompleted())
        assertFalse(reloaded.isMonitoringEnabled())
    }

    @Test
    fun completingEnablesMonitoringAndPausingPreservesTheIntroduction() {
        val prefs = BooleanPreferences()
        val setup = SetupPreferences(prefs)
        setup.complete()
        assertTrue(SetupPreferences(prefs).isMonitoringEnabled())

        setup.setMonitoringEnabled(false)
        assertTrue(setup.isCompleted())
        assertFalse(setup.isMonitoringEnabled())
        setup.setMonitoringEnabled(true)
        assertTrue(SetupPreferences(prefs).isMonitoringEnabled())
    }

    @Test
    fun resettingRequiresTheIntroductionAgainAndDisablesMonitoring() {
        val prefs = BooleanPreferences()
        val setup = SetupPreferences(prefs)
        setup.complete()
        assertTrue(setup.reset())

        assertFalse(SetupPreferences(prefs).isCompleted())
        assertFalse(SetupPreferences(prefs).isMonitoringEnabled())
    }

    @Test
    fun failedDeletionIsReportedAndCanBeRetriedWithoutRestartingPausedMonitoring() {
        val prefs = BooleanPreferences()
        val setup = SetupPreferences(prefs)
        setup.complete()
        setup.setMonitoringEnabled(false)
        prefs.failCommit = true

        assertFalse(setup.reset())
        assertFalse(SetupPreferences(prefs).isMonitoringEnabled())

        prefs.failCommit = false
        assertTrue(setup.reset())
        assertFalse(SetupPreferences(prefs).isCompleted())
        assertFalse(SetupPreferences(prefs).isMonitoringEnabled())
    }

    @Test
    fun onlyUsageAndOverlayPermissionsDetermineReadiness() {
        for (usage in listOf(false, true)) {
            for (overlay in listOf(false, true)) {
                val withNotifications = PermissionStatus(usage, overlay, true)
                val withoutNotifications = PermissionStatus(usage, overlay, false)
                if (usage && overlay) {
                    assertTrue(withNotifications.readyForMonitoring)
                    assertTrue(withoutNotifications.readyForMonitoring)
                } else {
                    assertFalse(withNotifications.readyForMonitoring)
                    assertFalse(withoutNotifications.readyForMonitoring)
                }
            }
        }
    }
}

private class BooleanPreferences : SharedPreferences {
    private val values = mutableMapOf<String, Boolean>()
    var failCommit = false

    override fun getBoolean(key: String, defValue: Boolean): Boolean = values[key] ?: defValue
    override fun contains(key: String): Boolean = values.containsKey(key)
    override fun getAll(): MutableMap<String, *> = values.toMutableMap()
    override fun getString(key: String, defValue: String?): String? = error("Not used for setup")
    override fun getStringSet(key: String, defValues: MutableSet<String>?): MutableSet<String>? = error("Not used for setup")
    override fun getInt(key: String, defValue: Int): Int = error("Not used for setup")
    override fun getLong(key: String, defValue: Long): Long = error("Not used for setup")
    override fun getFloat(key: String, defValue: Float): Float = error("Not used for setup")
    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) = Unit
    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) = Unit

    override fun edit(): SharedPreferences.Editor = object : SharedPreferences.Editor {
        private val changes = mutableMapOf<String, Boolean?>()
        private var clear = false

        override fun putBoolean(key: String, value: Boolean) = apply { changes[key] = value }
        override fun remove(key: String) = apply { changes[key] = null }
        override fun clear() = apply { clear = true }
        override fun commit(): Boolean {
            if (failCommit) return false
            if (clear) values.clear()
            changes.forEach { (key, value) ->
                if (value == null) values.remove(key) else values[key] = value
            }
            return true
        }
        override fun apply() { commit() }
        override fun putString(key: String, value: String?): SharedPreferences.Editor = error("Not used for setup")
        override fun putStringSet(key: String, values: MutableSet<String>?): SharedPreferences.Editor = error("Not used for setup")
        override fun putInt(key: String, value: Int): SharedPreferences.Editor = error("Not used for setup")
        override fun putLong(key: String, value: Long): SharedPreferences.Editor = error("Not used for setup")
        override fun putFloat(key: String, value: Float): SharedPreferences.Editor = error("Not used for setup")
    }
}
