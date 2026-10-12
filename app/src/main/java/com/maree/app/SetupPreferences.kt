package com.maree.app

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat

/** Les choix d'accueil restent séparés des limites et des points. */
class SetupPreferences internal constructor(private val prefs: SharedPreferences) {
    constructor(context: Context) : this(
        context.applicationContext.getSharedPreferences("maree_setup", Context.MODE_PRIVATE)
    )

    fun isCompleted(): Boolean = prefs.getBoolean("completed", false)

    fun isMonitoringEnabled(): Boolean =
        isCompleted() && prefs.getBoolean("monitoringEnabled", false)

    fun complete(enableMonitoring: Boolean = true) {
        prefs.edit().putBoolean("completed", true)
            .putBoolean("monitoringEnabled", enableMonitoring).apply()
    }

    fun setMonitoringEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("monitoringEnabled", enabled && isCompleted()).apply()
    }

    /** L'effacement global doit vérifier la persistance des choix d'accueil et de suivi. */
    fun reset(): Boolean = try {
        prefs.edit().clear().commit()
    } catch (_: Exception) {
        false
    }
}

data class PermissionStatus(
    val usageGranted: Boolean,
    val overlayGranted: Boolean,
    val notificationsGranted: Boolean
) {
    /** Le refus des notifications ne bloque jamais le suivi. */
    val readyForMonitoring: Boolean get() = usageGranted && overlayGranted

    companion object {
        fun read(context: Context): PermissionStatus = PermissionStatus(
            usageGranted = UsageReader(context).hasPermission(),
            overlayGranted = Settings.canDrawOverlays(context),
            notificationsGranted = Build.VERSION.SDK_INT < 33 ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
}
