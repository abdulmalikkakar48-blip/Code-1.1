package com.maree.app

import android.content.SharedPreferences
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class AppAppearanceTest {
    @After fun restoreLightAppearance() {
        AppAppearance.reset(AppearanceMemoryPreferences())
        AppAppearance.updateSystemAppearance(false)
    }

    @Test fun missingOrUnknownPreferencePreservesTheExistingLightDesign() {
        assertEquals(ThemeMode.LIGHT, preferredThemeMode(null))
        assertEquals(ThemeMode.LIGHT, preferredThemeMode("unknown"))
    }

    @Test fun savedChoicesRemainRecognizableAfterRestart() {
        val prefs = AppearanceMemoryPreferences()
        ThemeMode.entries.forEach { mode ->
            assertTrue(AppAppearance.set(prefs, mode))
            assertEquals(mode, AppAppearance.current)
            assertEquals(mode, preferredThemeMode(prefs.getString("theme", null)))
        }
    }

    @Test fun explicitChoicesOverrideAndroidWhileSystemChoiceFollowsIt() {
        listOf(false, true).forEach { phoneNight ->
            assertFalse(usesNightPalette(ThemeMode.LIGHT, phoneNight))
            assertTrue(usesNightPalette(ThemeMode.DARK, phoneNight))
            assertEquals(phoneNight, usesNightPalette(ThemeMode.SYSTEM, phoneNight))
        }
    }

    @Test fun paletteUpdatesImmediatelyForTheSelectedModeAndAndroidChanges() {
        val prefs = AppearanceMemoryPreferences()
        assertTrue(AppAppearance.set(prefs, ThemeMode.DARK))
        assertEquals(androidx.compose.ui.graphics.Color(SelkieNightColors.background), MareePalette.Background)
        assertTrue(AppAppearance.set(prefs, ThemeMode.LIGHT))
        assertEquals(androidx.compose.ui.graphics.Color(SelkieLightColors.background), MareePalette.Background)
        assertTrue(AppAppearance.set(prefs, ThemeMode.SYSTEM))
        AppAppearance.updateSystemAppearance(true)
        assertEquals(androidx.compose.ui.graphics.Color(SelkieNightColors.background), MareePalette.Background)
        AppAppearance.updateSystemAppearance(false)
        assertEquals(androidx.compose.ui.graphics.Color(SelkieLightColors.background), MareePalette.Background)
    }

    @Test fun failedSaveDoesNotChangeTheDisplayedMode() {
        val prefs = AppearanceMemoryPreferences()
        assertTrue(AppAppearance.set(prefs, ThemeMode.LIGHT))
        prefs.rejectWrites = true
        assertFalse(AppAppearance.set(prefs, ThemeMode.DARK))
        assertEquals(ThemeMode.LIGHT, AppAppearance.current)
        assertEquals("light", prefs.getString("theme", null))
    }

    @Test fun deletingAppearanceReturnsToLightAndDoesNotTouchOtherPreferenceStores() {
        val appearance = AppearanceMemoryPreferences()
        val tracking = AppearanceMemoryPreferences()
        tracking.edit().putString("points", "35").commit()
        assertTrue(AppAppearance.set(appearance, ThemeMode.DARK))
        assertTrue(AppAppearance.reset(appearance))
        assertEquals(ThemeMode.LIGHT, AppAppearance.current)
        assertEquals(null, appearance.getString("theme", null))
        assertEquals("35", tracking.getString("points", null))
    }

    @Test fun failedDeletionDoesNotPretendToResetAppearance() {
        val prefs = AppearanceMemoryPreferences()
        assertTrue(AppAppearance.set(prefs, ThemeMode.DARK))
        prefs.rejectWrites = true
        assertFalse(AppAppearance.reset(prefs))
        assertEquals(ThemeMode.DARK, AppAppearance.current)
        assertEquals("dark", prefs.getString("theme", null))
    }

    @Test fun lightTextAndActionRolesMeetNormalTextContrast() {
        assertReadableRoles("light", SelkieLightColors)
    }

    @Test fun nightTextAndActionRolesMeetNormalTextContrast() {
        assertReadableRoles("night", SelkieNightColors)
    }

    @Test fun sealBackgroundIsPreservedWhenNightModeIsSelected() {
        val original = MareePalette.BrandBackground
        assertTrue(AppAppearance.set(AppearanceMemoryPreferences(), ThemeMode.DARK))
        assertEquals(original, MareePalette.BrandBackground)
        assertTrue(luminance(SelkieNightColors.background) < 0.01)
        assertTrue((SelkieNightColors.primary and 0xFF) > (SelkieNightColors.primary shr 16 and 0xFF))
    }

    private fun assertReadableRoles(name: String, colors: SelkieThemeColors) {
        listOf(colors.background, colors.surface, colors.subtle, colors.softAccent).forEach { background ->
            listOf(colors.ink, colors.muted, colors.primary, colors.strongInk).forEach { foreground ->
                assertContrast(name, foreground, background)
            }
        }
        listOf(colors.background, colors.surface, colors.subtle).forEach {
            assertContrast(name, colors.danger, it)
        }
        assertContrast(name, colors.onPrimary, colors.primary)
        assertContrast(name, colors.onDanger, colors.danger)
        assertContrast(name, colors.onHero, colors.heroBackground)
        assertContrast(name, colors.onHeroMuted, colors.heroBackground)
        assertContrast(name, colors.heroAccent, colors.heroBackground)
        assertContrast(name, colors.onHeroAccent, colors.heroAccent)
        assertContrast(name, colors.heroAccent, colors.navigationBackground)
        assertContrast(name, colors.navigationMuted, colors.navigationBackground)
    }

    private fun assertContrast(name: String, foreground: Long, background: Long) {
        val lighter = max(luminance(foreground), luminance(background))
        val darker = min(luminance(foreground), luminance(background))
        val ratio = (lighter + 0.05) / (darker + 0.05)
        assertTrue("$name text ${foreground.toString(16)} on ${background.toString(16)}: $ratio", ratio >= 4.5)
    }

    private fun luminance(argb: Long): Double {
        fun linear(channel: Long): Double {
            val value = channel.toDouble() / 255.0
            return if (value <= 0.04045) value / 12.92 else ((value + 0.055) / 1.055).pow(2.4)
        }
        return 0.2126 * linear(argb shr 16 and 0xFF) +
            0.7152 * linear(argb shr 8 and 0xFF) + 0.0722 * linear(argb and 0xFF)
    }
}

private class AppearanceMemoryPreferences : SharedPreferences {
    private val values = mutableMapOf<String, String>()
    var rejectWrites = false

    override fun getString(key: String, defValue: String?): String? = values[key] ?: defValue
    override fun contains(key: String): Boolean = values.containsKey(key)
    override fun getAll(): MutableMap<String, *> = values.toMutableMap()
    override fun getStringSet(key: String, defValues: MutableSet<String>?): MutableSet<String>? = error("Not used")
    override fun getBoolean(key: String, defValue: Boolean): Boolean = error("Not used")
    override fun getInt(key: String, defValue: Int): Int = error("Not used")
    override fun getLong(key: String, defValue: Long): Long = error("Not used")
    override fun getFloat(key: String, defValue: Float): Float = error("Not used")
    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) = Unit
    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) = Unit

    override fun edit(): SharedPreferences.Editor = object : SharedPreferences.Editor {
        private val changes = mutableMapOf<String, String?>()
        private var clear = false
        override fun putString(key: String, value: String?) = apply { changes[key] = value }
        override fun clear() = apply { clear = true }
        override fun remove(key: String) = apply { changes[key] = null }
        override fun commit(): Boolean {
            if (rejectWrites) return false
            if (clear) values.clear()
            changes.forEach { (key, value) ->
                if (value == null) values.remove(key) else values[key] = value
            }
            return true
        }
        override fun apply() { commit() }
        override fun putStringSet(key: String, values: MutableSet<String>?): SharedPreferences.Editor = error("Not used")
        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor = error("Not used")
        override fun putInt(key: String, value: Int): SharedPreferences.Editor = error("Not used")
        override fun putLong(key: String, value: Long): SharedPreferences.Editor = error("Not used")
        override fun putFloat(key: String, value: Float): SharedPreferences.Editor = error("Not used")
    }
}
