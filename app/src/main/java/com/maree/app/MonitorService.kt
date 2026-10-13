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
        private const val APP_ALERT_NOTIFICATION = 11

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
            Store(context).pauseObservation()
            context.stopService(Intent(context, MonitorService::class.java))
            clearAlerts(context)
        }

        /** Les notifications appartiennent à Selkie ; aucune statistique Android n'est modifiée. */
        fun clearAlerts(context: Context) {
            context.getSystemService(NotificationManager::class.java)?.cancelAll()
        }

        fun clearAppAlert(context: Context, packageName: String) {
            context.getSystemService(NotificationManager::class.java)?.let { manager ->
                manager.cancel(packageName, APP_ALERT_NOTIFICATION)
                // Retire aussi une alerte émise par une ancienne version de l'application.
                manager.cancel(packageName.hashCode())
            }
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
    private var notificationLocale: AppLocale? = null
    @Volatile private var destroyed = false

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
        startForeground(SERVICE_NOTIFICATION, trackingNotification())
        notificationLocale = AppLanguage.current
        worker.scheduleWithFixedDelay({
            try { monitor() } catch (_: Exception) { /* ne pas interrompre le suivi */ }
        }, 0, 2, TimeUnit.SECONDS)
    }

    private fun trackingNotification(): Notification {
        val intent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(this, SERVICE_CHANNEL)
            .setSmallIcon(com.maree.app.R.drawable.ic_notification)
            .setContentTitle(tr("Selkie protège ton temps", "Selkie protects your time"))
            .setContentText(tr("Le suivi du temps d’écran est actif", "Screen time tracking is active"))
            .setContentIntent(intent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    /** Actualise les textes sans interrompre le suivi ni consommer une nouvelle alerte. */
    private fun refreshNotificationLanguage() {
        if (notificationLocale == AppLanguage.current) return
        createNotificationChannels()
        // Le service est déjà au premier plan : cette mise à jour conserve le suivi
        // même lorsque les notifications facultatives sont refusées sur Android 13+.
        startForeground(SERVICE_NOTIFICATION, trackingNotification())
        notificationLocale = AppLanguage.current
    }

    private fun createNotificationChannels() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(NotificationChannel(
            SERVICE_CHANNEL, tr("Suivi Selkie", "Selkie tracking"), NotificationManager.IMPORTANCE_LOW
        ).apply { description = tr("Notification de suivi permanent", "Ongoing tracking notification") })
        manager.createNotificationChannel(NotificationChannel(
            ALERT_CHANNEL, tr("Alertes de limite", "Time limit alerts"), NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = tr("Préventions avant la limite de temps", "Warnings before the time limit") })
    }

    private fun label(pkg: String): String = try {
        val application = packageManager.getApplicationInfo(pkg, 0)
        packageManager.getApplicationLabel(application).toString()
    } catch (_: Exception) { pkg.substringAfterLast('.') }

    private fun monitor() {
        if (destroyed) return
        if (!setup.isMonitoringEnabled() || !reader.hasPermission() || !Settings.canDrawOverlays(this)) {
            previousForeground = null
            hasPreviousForeground = false
            main.post { overlay.hide(); stopSelf() }
            return
        }
        val packages = store.packages()
        val usage = reader.read(packages)
        // Une lecture Android lente peut se terminer après une pause ou une suppression des données.
        if (destroyed || !setup.isMonitoringEnabled()) {
            main.post { overlay.hide(); stopSelf() }
            return
        }
        val tick = synchronized(LocalFeatureLock.lock) {
            if (destroyed || !setup.isMonitoringEnabled()) return
            store.tick(usage)
        }
        SelkieWidgetProvider.refresh(this)
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
            if (destroyed || !setup.isMonitoringEnabled() || !PermissionStatus.read(this).readyForMonitoring) {
                overlay.hide()
                stopSelf()
                return@post
            }
            refreshNotificationLanguage()
            val trackedPackages = store.packages()
            for ((pkg, remaining) in tick.warnings) {
                if (pkg in trackedPackages) alert(pkg, remaining)
            }
            if (locked != null && locked.packageName in trackedPackages) {
                overlay.show(label(locked.packageName), locked.lockedUntil, currentPoints)
            } else {
                overlay.hide()
            }
        }
    }

    private fun alert(packageName: String, remaining: Int) {
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) return
        val name = label(packageName)
        val message = tr("Plus que $remaining min sur $name", "Only $remaining min left on $name")
        val notification: Notification = NotificationCompat.Builder(this, ALERT_CHANNEL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(tr("Selkie · Dernière ligne droite", "Selkie · Almost at your limit"))
            .setContentText(message)
            .setAutoCancel(true)
            .build()
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Le tag identifie exactement l'application et ne peut pas remplacer la notification du service.
        manager.notify(packageName, APP_ALERT_NOTIFICATION, notification)
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
        destroyed = true
        worker.shutdownNow()
        if (::store.isInitialized) synchronized(LocalFeatureLock.lock) {
            // A delayed service teardown must not recreate history after a full reset.
            if (setup.isCompleted()) store.pauseObservation()
        }
        main.removeCallbacksAndMessages(null)
        if (::overlay.isInitialized) overlay.hide()
        clearAlerts(this)
        super.onDestroy()
    }
}
