package com.maree.app

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Locale

enum class AppLocale(val tag: String, val nativeLabel: String) {
    FRENCH("fr", "Français"), ENGLISH("en", "English")
}

/** A saved choice takes priority; otherwise English phones use English. */
internal fun preferredAppLocale(savedTag: String?, deviceLanguage: String): AppLocale =
    AppLocale.entries.firstOrNull { it.tag == savedTag }
        ?: if (deviceLanguage.substringBefore('-').substringBefore('_').equals("en", ignoreCase = true))
            AppLocale.ENGLISH else AppLocale.FRENCH

/** One local language choice shared by screens, the offline coach and notifications. */
object AppLanguage {
    private const val PREFERENCES = "selkie_language"
    private const val KEY = "language"
    private var selected by mutableStateOf(AppLocale.FRENCH)
    val current: AppLocale get() = selected
    val locale: Locale get() = if (current == AppLocale.ENGLISH) Locale.ENGLISH else Locale.FRENCH

    fun initialize(context: Context) {
        val deviceLanguage = context.resources.configuration.locales[0].language
        val savedTag = runCatching {
            context.applicationContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).getString(KEY, null)
        }.getOrNull()
        selected = preferredAppLocale(savedTag, deviceLanguage)
    }

    /** Change visible text only after confirming that the preference was saved. */
    fun set(context: Context, language: AppLocale): Boolean = runCatching {
        val saved = context.applicationContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .edit().putString(KEY, language.tag).commit()
        if (saved) selected = language
        saved
    }.getOrDefault(false)

    fun reset(context: Context): Boolean = runCatching {
        val cleared = context.applicationContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .edit().clear().commit()
        if (cleared) initialize(context)
        cleared
    }.getOrDefault(false)
}

/** Normal function so translations also work in callbacks and background services. */
internal fun tr(french: String, english: String): String =
    if (AppLanguage.current == AppLocale.ENGLISH) english else french

class SelkieApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppLanguage.initialize(this)
        AppAppearance.initialize(this)
    }
}
