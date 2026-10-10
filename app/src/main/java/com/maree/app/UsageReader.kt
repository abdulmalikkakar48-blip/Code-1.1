package com.maree.app

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Process
import java.util.Calendar

/** Durées calculées à partir des événements d'avant-plan d'Android (millisecondes). */
data class UsageReading(
    val elapsedMillis: Map<String, Long>,
    val foregroundPackage: String?
)

class UsageReader(private val context: Context) {
    fun hasPermission(): Boolean {
        val ops = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        return ops.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(), context.packageName
        ) == AppOpsManager.MODE_ALLOWED
    }

    /** Les durées sont remises à zéro à minuit, heure locale. */
    fun read(packages: Set<String>, now: Long = System.currentTimeMillis()): UsageReading {
        if (!hasPermission()) return UsageReading(emptyMap(), null)
        val calendar = Calendar.getInstance().apply {
            timeInMillis = now
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val midnight = calendar.timeInMillis
        val starts = HashMap<String, Long>()
        val durations = packages.associateWith { 0L }.toMutableMap()
        var foreground: String? = null
        val service = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val events = service.queryEvents(midnight, now)
        val item = UsageEvents.Event()
        while (events.hasNextEvent()) {
            events.getNextEvent(item)
            val pkg = item.packageName ?: continue
            when (item.eventType) {
                UsageEvents.Event.ACTIVITY_RESUMED -> {
                    foreground = pkg
                    if (pkg in packages && pkg !in starts) starts[pkg] = item.timeStamp
                }
                UsageEvents.Event.ACTIVITY_PAUSED -> {
                    if (foreground == pkg) foreground = null
                    val started = starts.remove(pkg)
                    if (started != null) {
                        durations[pkg] = (durations[pkg] ?: 0L) +
                            (item.timeStamp - started).coerceAtLeast(0L)
                    }
                }
                UsageEvents.Event.DEVICE_SHUTDOWN -> {
                    starts.forEach { (activePkg, started) ->
                        durations[activePkg] = (durations[activePkg] ?: 0L) +
                            (item.timeStamp - started).coerceAtLeast(0L)
                    }
                    starts.clear()
                    foreground = null
                }
            }
        }
        for ((pkg, started) in starts) {
            durations[pkg] = (durations[pkg] ?: 0L) + (now - started).coerceAtLeast(0L)
        }
        return UsageReading(durations, foreground)
    }
}
