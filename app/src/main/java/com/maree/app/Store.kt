package com.maree.app

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ceil

private const val DAY = 24L * 60 * 60 * 1000
private const val CHALLENGE_CYCLE = 21L * DAY

/** 20 défis : les deux groupes alternent tous les 21 jours. */
val CHALLENGES = listOf(
    "Lire 10 pages d'un livre", "Marcher 20 minutes sans téléphone",
    "Manger un repas sans écran", "Désactiver une notification inutile",
    "Laisser son téléphone loin de son lit", "Faire 15 minutes de rangement",
    "Appeler un proche au lieu d'envoyer un message", "Écrire trois choses positives",
    "Faire une pause de 30 minutes sans écran", "Prendre l'air pendant 15 minutes",
    "Ne pas regarder son téléphone au réveil", "Faire 10 minutes d'étirements",
    "Lire un article long sans distraction", "Dessiner ou écrire pendant 20 minutes",
    "Préparer son lendemain sur papier", "Passer une heure sans réseau social",
    "Faire une promenade attentive", "Apprendre quelque chose hors écran",
    "Prendre son goûter sans téléphone", "Couper les écrans une heure avant de dormir"
)

data class TrackedApp(
    val packageName: String,
    val limitMinutes: Int,
    val baselineMillis: Long = 0,
    val lockedUntil: Long = 0,
    val warnedOn: String = "",
    val lastPenaltyAt: Long = 0
)

data class AppSnapshot(
    val packageName: String,
    val limitMinutes: Int,
    val usedMillis: Long,
    val lockedUntil: Long,
    val warnedOn: String
) {
    val usedMinutes: Int get() = (usedMillis / 60_000L).toInt()
    val progress: Float get() = (usedMillis / (limitMinutes * 60_000f)).coerceIn(0f, 1f)
    val isLocked: Boolean get() = lockedUntil > System.currentTimeMillis()
}

data class MareeSnapshot(
    val points: Int,
    val apps: List<AppSnapshot>,
    val premium: Boolean,
    val challengeSeries: Int,
    val checkedChallenges: Set<Int>,
    val daysUntilChallenges: Int,
    val totalCompletedChallenges: Int
)

data class TickResult(
    val snapshot: MareeSnapshot,
    val warnings: List<Pair<String, Int>>,
    val justLocked: Set<String>,
    val foregroundPackage: String?
)

class Store internal constructor(private val prefs: SharedPreferences) {
    constructor(context: Context) : this(
        context.applicationContext.getSharedPreferences("maree", Context.MODE_PRIVATE)
    )
    companion object { private val lock = Any() }

    init {
        synchronized(lock) {
            if (!prefs.contains("points")) prefs.edit().putInt("points", 100).apply()
            if (!prefs.contains("periodStart"))
                prefs.edit().putLong("periodStart", System.currentTimeMillis()).apply()
            if (!prefs.contains("challengeStart"))
                prefs.edit().putLong("challengeStart", System.currentTimeMillis()).apply()
            if (!prefs.contains("day"))
                prefs.edit().putString("day", SimpleDateFormat("yyyyMMdd", Locale.ROOT).format(Date())).apply()
        }
    }

    private fun readApps(): MutableList<TrackedApp> {
        val data = try { JSONArray(prefs.getString("apps", "[]")) } catch (_: Exception) { JSONArray() }
        return MutableList(data.length()) { i ->
            val value = data.getJSONObject(i)
            TrackedApp(
                value.getString("pkg"), value.optInt("limit", 30),
                value.optLong("base"), value.optLong("lock"),
                value.optString("warn"), value.optLong("penalty")
            )
        }
    }

    private fun saveApps(apps: List<TrackedApp>) {
        val data = JSONArray()
        for (app in apps) data.put(JSONObject().apply {
            put("pkg", app.packageName)
            put("limit", app.limitMinutes)
            put("base", app.baselineMillis)
            put("lock", app.lockedUntil)
            put("warn", app.warnedOn)
            put("penalty", app.lastPenaltyAt)
        })
        prefs.edit().putString("apps", data.toString()).apply()
    }

    fun packages(): Set<String> = synchronized(lock) { readApps().map { it.packageName }.toSet() }
    fun points(): Int = synchronized(lock) { prefs.getInt("points", 100) }
    fun isPremium(): Boolean = synchronized(lock) { prefs.getBoolean("premium", false) }
    fun trackedCount(): Int = synchronized(lock) { readApps().size }

    /**
     * Efface l'ensemble des données Marée, y compris les clés futures, avant de rendre la main.
     * Le service de suivi doit être arrêté par l'appelant avant cette action. Une nouvelle
     * instance de Store recrée les horloges initiales ; les points reviennent à 100 par défaut.
     * false signale un échec de persistance : l'interface ne doit pas annoncer un succès.
     */
    fun clearPersonalData(): Boolean = synchronized(lock) { prefs.edit().clear().commit() }

    /** Une éventuelle brèche de la période ne disparaît pas quand on retire l'application. */
    private fun recordBreach() = prefs.edit().putBoolean("periodBreach", true).apply()

    private fun canEdit(): Boolean = isPremium() || points() > 0

    fun addApp(packageName: String, limitMinutes: Int, rawTodayMillis: Long): String? = synchronized(lock) {
        if (!canEdit()) return@synchronized "Tu n'as plus de points. Fais un défi ou attends le prochain bonus."
        val apps = readApps()
        if (!isPremium() && apps.size >= 5) return@synchronized "5 applications maximum avec la version gratuite."
        if (apps.any { it.packageName == packageName }) return@synchronized "Cette application est déjà suivie."
        if (limitMinutes < 5 || limitMinutes % 5 != 0) return@synchronized "Choisis un multiple de 5 minutes."
        apps.add(TrackedApp(packageName, limitMinutes, rawTodayMillis))
        saveApps(apps)
        null
    }

    fun editLimit(packageName: String, minutes: Int): String? = synchronized(lock) {
        if (!canEdit()) return@synchronized "Impossible de modifier une limite à 0 point sans premium."
        if (minutes < 5 || minutes % 5 != 0) return@synchronized "Choisis un multiple de 5 minutes."
        val apps = readApps()
        val index = apps.indexOfFirst { it.packageName == packageName }
        if (index < 0) return@synchronized "Application introuvable."
        apps[index] = apps[index].copy(limitMinutes = minutes)
        saveApps(apps)
        null
    }

    fun removeApp(packageName: String) = synchronized(lock) {
        saveApps(readApps().filterNot { it.packageName == packageName })
    }

    fun resetApp(packageName: String, rawTodayMillis: Long): String? = synchronized(lock) {
        val points = points()
        if (points == 0 && !isPremium())
            return@synchronized "À 0 point, fais des défis, attends 24 h ou passe en premium."
        val apps = readApps()
        val index = apps.indexOfFirst { it.packageName == packageName }
        if (index < 0) return@synchronized "Application introuvable."
        apps[index] = apps[index].copy(baselineMillis = rawTodayMillis, lockedUntil = 0L)
        saveApps(apps)
        prefs.edit().putInt("points", (points - 10).coerceAtLeast(0)).apply()
        null
    }

    fun penalizeReopening(packageName: String, now: Long): Boolean = synchronized(lock) {
        val apps = readApps()
        val index = apps.indexOfFirst { it.packageName == packageName && it.lockedUntil > now }
        if (index == -1 || now - apps[index].lastPenaltyAt < 30_000L) return@synchronized false
        advancePeriods(now)
        apps[index] = apps[index].copy(lastPenaltyAt = now)
        saveApps(apps)
        prefs.edit().putInt("points", (points() - 5).coerceAtLeast(0)).apply()
        recordBreach()
        true
    }

    private fun updateCycle(now: Long) {
        val start = prefs.getLong("challengeStart", now)
        if (now < start + CHALLENGE_CYCLE) return
        val elapsedCycles = (now - start) / CHALLENGE_CYCLE
        prefs.edit()
            .putLong("challengeStart", start + elapsedCycles * CHALLENGE_CYCLE)
            .putLong("challengeNumber", prefs.getLong("challengeNumber", 0) + elapsedCycles)
            .putString("checked", "[]")
            .apply()
    }

    private fun checked(): Set<Int> {
        val result = mutableSetOf<Int>()
        val array = try { JSONArray(prefs.getString("checked", "[]")) } catch (_: Exception) { JSONArray() }
        for (i in 0 until array.length()) result.add(array.optInt(i))
        return result
    }

    fun completeChallenge(index: Int, now: Long = System.currentTimeMillis()): Boolean = synchronized(lock) {
        updateCycle(now)
        val series = (prefs.getLong("challengeNumber", 0) % 2).toInt()
        if (index !in (series * 10 until (series + 1) * 10) || index in checked()) return@synchronized false
        val done = checked() + index
        val array = JSONArray()
        done.sorted().forEach { array.put(it) }
        val count = prefs.getInt("completedCount", 0) + 1
        val reward = if (count % 5 == 0) 5 else 0
        prefs.edit().putString("checked", array.toString())
            .putInt("completedCount", count)
            .putInt("points", points() + reward).apply()
        true
    }

    private fun snapshot(apps: List<TrackedApp>, raw: Map<String, Long>, now: Long): MareeSnapshot {
        updateCycle(now)
        val cycleStart = prefs.getLong("challengeStart", now)
        val daysRemaining = ceil((cycleStart + CHALLENGE_CYCLE - now) / DAY.toDouble()).toInt().coerceAtLeast(0)
        return MareeSnapshot(
            points(),
            apps.map {
                AppSnapshot(it.packageName, it.limitMinutes,
                    ((raw[it.packageName] ?: 0L) - it.baselineMillis).coerceAtLeast(0L),
                    it.lockedUntil, it.warnedOn)
            },
            isPremium(),
            (prefs.getLong("challengeNumber", 0) % 2L).toInt(),
            checked(), daysRemaining, prefs.getInt("completedCount", 0)
        )
    }

    private fun normalizeApp(app: TrackedApp, raw: Long, now: Long, changedDay: Boolean): TrackedApp {
        val dailyApp = if (changedDay) app.copy(baselineMillis = 0, warnedOn = "") else app
        return if (dailyApp.lockedUntil != 0L && now >= dailyApp.lockedUntil)
            dailyApp.copy(lockedUntil = 0L, baselineMillis = raw)
        else dailyApp
    }

    /** L'interface consulte l'état sans consommer les alertes ni créer de verrous. */
    fun readSnapshot(usage: UsageReading, now: Long = System.currentTimeMillis()): MareeSnapshot = synchronized(lock) {
        advancePeriods(now)
        val today = SimpleDateFormat("yyyyMMdd", Locale.ROOT).format(Date(now))
        val changedDay = prefs.getString("day", "") != today
        val apps = readApps().map { app ->
            normalizeApp(app, usage.elapsedMillis[app.packageName] ?: 0L, now, changedDay)
        }
        snapshot(apps, usage.elapsedMillis, now)
    }

    fun tick(usage: UsageReading, now: Long = System.currentTimeMillis()): TickResult = synchronized(lock) {
        // Terminer les périodes précédentes avant d'enregistrer un incident observé maintenant.
        advancePeriods(now)
        val today = SimpleDateFormat("yyyyMMdd", Locale.ROOT).format(Date(now))
        val changedDay = prefs.getString("day", "") != today
        val warnings = mutableListOf<Pair<String, Int>>()
        val newLocks = mutableSetOf<String>()
        val apps = readApps().map { original ->
            val raw = usage.elapsedMillis[original.packageName] ?: 0L
            var app = normalizeApp(original, raw, now, changedDay)
            if (app.lockedUntil == 0L) {
                val used = (raw - app.baselineMillis).coerceAtLeast(0L)
                val threshold = app.limitMinutes * 60_000L
                if (used >= threshold) {
                    app = app.copy(lockedUntil = now + DAY)
                    newLocks.add(app.packageName)
                    recordBreach()
                } else if (used >= threshold * 0.8 && app.warnedOn != today) {
                    app = app.copy(warnedOn = today)
                    warnings.add(app.packageName to ceil((threshold - used) / 60_000.0).toInt().coerceAtLeast(1))
                }
            }
            app
        }
        saveApps(apps)
        if (changedDay) prefs.edit().putString("day", today).apply()

        TickResult(snapshot(apps, usage.elapsedMillis, now), warnings, newLocks, usage.foregroundPackage)
    }

    // Bonus de discipline tous les 24 h, au plus 7 périodes rattrapées.
    private fun advancePeriods(now: Long) {
        var periodStart = prefs.getLong("periodStart", now)
        var breach = prefs.getBoolean("periodBreach", false)
        var p = points()
        var processed = 0
        while (now - periodStart >= DAY && processed < 7) {
            if (!breach) p += 10
            periodStart += DAY
            breach = false
            processed++
        }
        if (now - periodStart >= DAY) {
            periodStart = now // limite de rattrapage ; ne pas créditer une durée non vérifiée
            breach = false
        }
        prefs.edit().putInt("points", p).putLong("periodStart", periodStart)
            .putBoolean("periodBreach", breach).apply()

    }
}
