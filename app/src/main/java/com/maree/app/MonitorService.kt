package com.maree.app

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MonitorService : Service() {
    companion object {
        private const val SERVICE_CHANNEL = "maree_suivi"
        private const val ALERT_CHANNEL = "maree_alertes"
        private const val SERVICE_NOTIFICATION = 10

        fun start(context: Context) {
            if (!SetupPreferences(context).isMonitoringEnabled()) return
            if (!UsageReader(context).hasPermission() || !Settings.canDrawOverlays(context)) return
            try {
                ContextCompat.startForegroundService(context, Intent(context, MonitorService::class.java))
            } catch (_: Exception) {
                // Des versions Android peuvent interdire le démarrage en arrière-plan.
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, MonitorService::class.java))
        }
    }

    private val main = Handler(Looper.getMainLooper())
    private val worker = Executors.newSingleThreadScheduledExecutor()
    private lateinit var store: Store
    private lateinit var reader: UsageReader
    private lateinit var overlay: LockOverlay
    private lateinit var setup: SetupPreferences
    private var previousForeground: String? = null
    private var hasPreviousForeground = false

    override fun onCreate() {
        super.onCreate()
        setup = SetupPreferences(this)
        if (!setup.isMonitoringEnabled() || !PermissionStatus.read(this).readyForMonitoring) {
            stopSelf()
            return
        }
        store = Store(this)
        reader = UsageReader(this)
        overlay = LockOverlay(this)
        createNotificationChannels()
        val intent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(this, SERVICE_CHANNEL)
            .setSmallIcon(com.maree.app.R.drawable.ic_maree)
            .setContentTitle("Marée protège ton temps")
            .setContentText("Le suivi du temps d’écran est actif")
            .setContentIntent(intent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        startForeground(SERVICE_NOTIFICATION, notification)
        worker.scheduleWithFixedDelay({
            try { monitor() } catch (_: Exception) { /* ne pas interrompre le suivi */ }
        }, 0, 2, TimeUnit.SECONDS)
    }

    private fun createNotificationChannels() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(NotificationChannel(
            SERVICE_CHANNEL, "Suivi Marée", NotificationManager.IMPORTANCE_LOW
        ).apply { description = "Notification de suivi permanent" })
        manager.createNotificationChannel(NotificationChannel(
            ALERT_CHANNEL, "Alertes de limite", NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "Préventions avant la limite de temps" })
    }

    private fun label(pkg: String): String = try {
        val application = packageManager.getApplicationInfo(pkg, 0)
        packageManager.getApplicationLabel(application).toString()
    } catch (_: Exception) { pkg.substringAfterLast('.') }

    private fun monitor() {
        if (!setup.isMonitoringEnabled() || !reader.hasPermission() || !Settings.canDrawOverlays(this)) {
            previousForeground = null
            hasPreviousForeground = false
            main.post { overlay.hide(); stopSelf() }
            return
        }
        val packages = store.packages()
        val usage = reader.read(packages)
        // Une lecture Android lente peut se terminer après une pause ou une suppression des données.
        if (!setup.isMonitoringEnabled()) {
            main.post { overlay.hide(); stopSelf() }
            return
        }
        val tick = store.tick(usage)
        val foreground = tick.foregroundPackage
        val locked = tick.snapshot.apps.firstOrNull {
            it.packageName == foreground && it.isLocked
        }
        if (hasPreviousForeground && locked != null && previousForeground != foreground && foreground !in tick.justLocked) {
            store.penalizeReopening(locked.packageName, System.currentTimeMillis())
        }
        previousForeground = foreground
        hasPreviousForeground = true
        val currentPoints = store.points()
        main.post {
            if (!setup.isMonitoringEnabled()) {
                overlay.hide()
                stopSelf()
                return@post
            }
            for ((pkg, remaining) in tick.warnings) alert(label(pkg), remaining, pkg.hashCode())
            if (locked != null && store.packages().contains(locked.packageName)) {
                overlay.show(label(locked.packageName), locked.lockedUntil, currentPoints)
            } else {
                overlay.hide()
            }
        }
    }

    private fun alert(name: String, remaining: Int, id: Int) {
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) return
        val message = "Plus que $remaining min sur $name"
        val notification: Notification = NotificationCompat.Builder(this, ALERT_CHANNEL)
            .setSmallIcon(R.drawable.ic_maree)
            .setContentTitle("Marée · Dernière ligne droite")
            .setContentText(message)
            .setAutoCancel(true)
            .build()
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(id, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!setup.isMonitoringEnabled() || !PermissionStatus.read(this).readyForMonitoring) {
            stopSelf()
            return START_NOT_STICKY
        }
        return START_STICKY
    }
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onDestroy() {
        worker.shutdownNow()
        if (::overlay.isInitialized) main.post { overlay.hide() }
        super.onDestroy()
    }
}
