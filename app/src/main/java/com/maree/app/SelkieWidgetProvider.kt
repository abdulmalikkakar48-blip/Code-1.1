package com.maree.app

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.widget.RemoteViews
import org.json.JSONObject
import java.time.LocalDate
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

internal data class SelkieWidgetData(val paid: Boolean, val pointUnits: Int, val challengeIndex: Int?)

/** Read stored progress without querying Android usage or awarding period bonuses. */
internal fun widgetData(state: JSONObject, plan: PlanType, pointUnits: Int): SelkieWidgetData {
    if (plan == PlanType.FREE) return SelkieWidgetData(false, 0, null)
    val challenges = state.optJSONObject("standardChallenges")?.optJSONArray("challenges")
    var selected: Int? = null
    if (challenges != null) for (index in 0 until challenges.length()) {
        val row = challenges.optJSONObject(index) ?: continue
        val challenge = row.opt("index") as? Int ?: continue
        if (row.opt("inCurrentSeries") == true && row.opt("completed") == false && challenge in 0..19) {
            selected = challenge
            break
        }
    }
    return SelkieWidgetData(true, pointUnits.coerceIn(0, MAX_POINT_UNITS), selected)
}

class SelkieWidgetProvider : AppWidgetProvider() {
    override fun onEnabled(context: Context) { installed = true }
    override fun onDisabled(context: Context) { installed = false }
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        installed = ids.isNotEmpty()
        refreshNow(context)
    }

    companion object {
        private val worker = Executors.newSingleThreadExecutor()
        private val queued = AtomicBoolean(false)
        @Volatile private var installed: Boolean? = null
        @Volatile private var lastUpdate = 0L
        private var previous: String? = null

        /** Bounded event updates; the system also refreshes at its 30-minute interval. */
        fun refresh(context: Context, force: Boolean = false) {
            if (!force && installed == false) return
            if (!force && SystemClock.elapsedRealtime() - lastUpdate < 60_000L) return
            if (!force && !queued.compareAndSet(false, true)) return
            val app = context.applicationContext
            worker.execute {
                try { refreshNow(app, force) } finally { if (!force) queued.set(false) }
            }
        }

        /** Erasure can await this call: no old read may redraw personal progress afterwards. */
        fun refreshNow(context: Context, force: Boolean = true): Boolean = synchronized(LocalFeatureLock.lock) {
            try {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, SelkieWidgetProvider::class.java))
            installed = ids.isNotEmpty()
            if (ids.isEmpty()) return@synchronized true
            val store = Store(context)
            val data = widgetData(store.widgetLocalState(), store.planType(), store.pointUnits())
            val identity = "$data:${AppLanguage.current}:${LocalDate.now()}"
            val now = SystemClock.elapsedRealtime()
            if (!force && (identity == previous || now - lastUpdate < 60_000L)) return@synchronized true
            ids.forEach { id ->
                val views = RemoteViews(context.packageName, R.layout.selkie_widget)
                views.setTextViewText(R.id.widget_title, "Selkie")
                views.setTextViewText(R.id.widget_points, if (data.paid)
                    tr("${formatPointUnits(data.pointUnits)} points", "${formatPointUnits(data.pointUnits)} points")
                    else tr("Widget Premium", "Premium widget"))
                views.setTextViewText(R.id.widget_challenge, if (data.paid)
                    data.challengeIndex?.let { CHALLENGES[it] }
                        ?: tr("Retrouve les défis de ton quotidien.", "Find your daily challenges.")
                    else tr("Tes points et un défi sur l’écran d’accueil, avec Premium ou Ultra.", "Your points and a challenge on your home screen with Premium or Ultra."))
                views.setTextViewText(R.id.widget_action, if (data.paid) tr("Voir mes défis", "View challenges")
                    else tr("Voir les offres", "View plans"))
                views.setOnClickPendingIntent(R.id.widget_root, destination(context, id * 2,
                    if (data.paid) "daily" else "subscriptions"))
                views.setOnClickPendingIntent(R.id.widget_action, destination(context, id * 2 + 1,
                    if (data.paid) "challenges" else "subscriptions"))
                manager.updateAppWidget(id, views)
            }
            previous = identity
            lastUpdate = now
            true
            } catch (_: Exception) { false }
        }

        private fun destination(context: Context, requestCode: Int, destination: String): PendingIntent =
            PendingIntent.getActivity(context, requestCode,
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    putExtra("selkie_destination", destination)
                }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }
}
