package com.maree.app

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class ThemeMode(val tag: String) {
    LIGHT("light"), DARK("dark"), SYSTEM("system")
}

/** Keep the existing light design until the person explicitly chooses another appearance. */
internal fun preferredThemeMode(savedTag: String?): ThemeMode =
    ThemeMode.entries.firstOrNull { it.tag == savedTag } ?: ThemeMode.LIGHT

internal fun usesNightPalette(mode: ThemeMode, systemNight: Boolean): Boolean =
    when (mode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> systemNight
    }

/** Appearance is a separate local preference; it never changes tracking or membership data. */
object AppAppearance {
    private const val PREFERENCES = "selkie_appearance"
    private const val KEY = "theme"
    private var selected by mutableStateOf(ThemeMode.LIGHT)
    private var systemNight by mutableStateOf(false)
    val current: ThemeMode get() = selected
    internal val isNight: Boolean get() = usesNightPalette(current, systemNight)

    private fun preferences(context: Context): SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

    private fun phoneUsesNight(context: Context): Boolean =
        context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
            Configuration.UI_MODE_NIGHT_YES

    fun initialize(context: Context) {
        val night = phoneUsesNight(context)
        val saved = runCatching { preferences(context).getString(KEY, null) }.getOrNull()
        selected = preferredThemeMode(saved)
        systemNight = night
    }

    internal fun updateSystemAppearance(night: Boolean) {
        systemNight = night
    }

    fun set(context: Context, mode: ThemeMode): Boolean = runCatching {
        set(preferences(context), mode)
    }.getOrDefault(false)

    /** Update Compose only after the local write succeeds. */
    internal fun set(prefs: SharedPreferences, mode: ThemeMode): Boolean = runCatching {
        val saved = prefs.edit().putString(KEY, mode.tag).commit()
        if (saved) selected = mode
        saved
    }.getOrDefault(false)

    fun reset(context: Context): Boolean = runCatching {
        val cleared = reset(preferences(context))
        if (cleared) systemNight = phoneUsesNight(context)
        cleared
    }.getOrDefault(false)

    internal fun reset(prefs: SharedPreferences): Boolean = runCatching {
        val cleared = prefs.edit().clear().commit()
        if (cleared) selected = ThemeMode.LIGHT
        cleared
    }.getOrDefault(false)
}
