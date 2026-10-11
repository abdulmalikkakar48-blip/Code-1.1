package com.maree.app

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.ceil

private const val DAY = 24L * 60 * 60 * 1000
private const val CHALLENGE_CYCLE = 21L * DAY
const val CHALLENGE_REQUIRED_DAYS = 5

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
    val points: Double,
    val apps: List<AppSnapshot>,
    val premium: Boolean,
    val challengeSeries: Int,
    val checkedChallenges: Set<Int>,
    val daysUntilChallenges: Int,
    val totalCompletedChallenges: Int,
    val challengeProgress: Map<Int, ChallengeProgress> = emptyMap(),
    val pointUnits: Int = (points * POINT_UNITS_PER_POINT).toInt(),
    val plan: PlanType = if (premium) PlanType.PREMIUM else PlanType.FREE,
    val customChallenges: List<CustomChallenge> = emptyList()
)

data class ChallengeProgress(
    val completedDays: Int = 0,
    val requiredDays: Int = CHALLENGE_REQUIRED_DAYS,
    val validatedToday: Boolean = false,
    val canValidateToday: Boolean = completedDays < requiredDays && !validatedToday
)

private data class StoredCustomChallenge(
    val id: String,
    val title: String,
    val steps: List<String>,
    val requiredDays: Int,
    val createdAt: Long,
    val days: List<LocalDate> = emptyList(),
    val stepDate: LocalDate? = null,
    val checkedSteps: Set<Int> = emptySet(),
    val rewardClaimed: Boolean = false
) {
    val completed: Boolean get() = days.size >= requiredDays
}

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
            if (!prefs.contains("pointUnits")) {
                val legacyPoints = prefs.getInt("points", 100).coerceAtLeast(0)
                prefs.edit().putInt("pointUnits", (legacyPoints.toLong() * POINT_UNITS_PER_POINT)
                    .coerceAtMost(Int.MAX_VALUE.toLong()).toInt()).remove("points").apply()
            }
            if (!prefs.contains("plan")) {
                val plan = if (prefs.getBoolean("premium", false)) PlanType.PREMIUM else PlanType.FREE
                prefs.edit().putString("plan", plan.name).remove("premium").apply()
            }
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
    fun pointUnits(): Int = synchronized(lock) { prefs.getInt("pointUnits", 100 * POINT_UNITS_PER_POINT).coerceAtLeast(0) }
    fun points(): Double = pointUnits() / POINT_UNITS_PER_POINT.toDouble()
    fun planType(): PlanType = synchronized(lock) {
        PlanType.entries.firstOrNull { it.name == prefs.getString("plan", PlanType.FREE.name) } ?: PlanType.FREE
    }
    fun isPremium(): Boolean = planType().canCreateCustomChallenges
    /** Simulation locale explicite ; cette méthode ne crée aucun achat ni abonnement. */
    fun setDemoPlan(plan: PlanType) = synchronized(lock) { prefs.edit().putString("plan", plan.name).apply() }
    fun trackedCount(): Int = synchronized(lock) { readApps().size }

    /**
     * Efface l'ensemble des données Milo, y compris les clés futures, avant de rendre la main.
     * Le service de suivi doit être arrêté par l'appelant avant cette action. Une nouvelle
     * instance de Store recrée les horloges initiales ; les points reviennent à 100 par défaut.
     * false signale un échec de persistance : l'interface ne doit pas annoncer un succès.
     */
    fun clearPersonalData(): Boolean = synchronized(lock) { prefs.edit().clear().commit() }

    /** Une éventuelle brèche de la période ne disparaît pas quand on retire l'application. */
    private fun recordBreach() = prefs.edit().putBoolean("periodBreach", true).apply()

    private fun canEdit(): Boolean = pointUnits() > 0

    private fun shiftedPointUnits(delta: Int): Int =
        (pointUnits().toLong() + delta).coerceIn(0, Int.MAX_VALUE.toLong()).toInt()

    fun addApp(packageName: String, limitMinutes: Int, rawTodayMillis: Long): String? = synchronized(lock) {
        if (!canEdit()) return@synchronized "Tu n'as plus de points. Fais un défi ou attends le prochain bonus."
        val apps = readApps()
        val plan = planType()
        val appLimit = plan.maxTrackedApps
        if (appLimit != null && apps.size >= appLimit)
            return@synchronized "$appLimit applications maximum avec l’offre ${plan.displayName}."
        if (apps.any { it.packageName == packageName }) return@synchronized "Cette application est déjà suivie."
        if (limitMinutes < 5 || limitMinutes % 5 != 0) return@synchronized "Choisis un multiple de 5 minutes."
        apps.add(TrackedApp(packageName, limitMinutes, rawTodayMillis))
        saveApps(apps)
        null
    }

    fun editLimit(packageName: String, minutes: Int): String? = synchronized(lock) {
        if (!canEdit()) return@synchronized "Impossible de modifier une limite à 0 point. Termine des défis pour en regagner."
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
        if (pointUnits() < 10 * POINT_UNITS_PER_POINT)
            return@synchronized "Il faut 10 points pour réinitialiser une limite."
        val apps = readApps()
        val index = apps.indexOfFirst { it.packageName == packageName }
        if (index < 0) return@synchronized "Application introuvable."
        apps[index] = apps[index].copy(baselineMillis = rawTodayMillis, lockedUntil = 0L)
        saveApps(apps)
        prefs.edit().putInt("pointUnits", shiftedPointUnits(-10 * POINT_UNITS_PER_POINT)).apply()
        null
    }

    fun penalizeReopening(packageName: String, now: Long): Boolean = synchronized(lock) {
        val apps = readApps()
        val index = apps.indexOfFirst { it.packageName == packageName && it.lockedUntil > now }
        if (index == -1 || now - apps[index].lastPenaltyAt < 30_000L) return@synchronized false
        advancePeriods(now)
        apps[index] = apps[index].copy(lastPenaltyAt = now)
        saveApps(apps)
        prefs.edit().putInt("pointUnits", shiftedPointUnits(-5 * POINT_UNITS_PER_POINT)).apply()
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
            .remove("challengeDays")
            .apply()
    }

    private fun checked(): Set<Int> {
        val result = mutableSetOf<Int>()
        val array = try { JSONArray(prefs.getString("checked", "[]")) } catch (_: Exception) { JSONArray() }
        for (i in 0 until array.length()) result.add(array.optInt(i))
        return result
    }

    private fun localDate(now: Long): LocalDate =
        Instant.ofEpochMilli(now).atZone(ZoneId.systemDefault()).toLocalDate()

    /** Dates locales, distinctes et ordonnées ; une journée sautée ne remet rien à zéro. */
    private fun readChallengeDays(): MutableMap<Int, List<LocalDate>> {
        val data = try { JSONObject(prefs.getString("challengeDays", "{}")) }
            catch (_: Exception) { JSONObject() }
        val result = mutableMapOf<Int, List<LocalDate>>()
        for (index in CHALLENGES.indices) {
            val array = data.optJSONArray(index.toString()) ?: continue
            val days = (0 until array.length()).mapNotNull { i ->
                try { LocalDate.parse(array.optString(i)) } catch (_: Exception) { null }
            }.distinct().sorted().take(CHALLENGE_REQUIRED_DAYS)
            if (days.isNotEmpty()) result[index] = days
        }
        return result
    }

    private fun challengeDaysJson(days: Map<Int, List<LocalDate>>): String {
        val data = JSONObject()
        for ((index, dates) in days.toSortedMap()) {
            val array = JSONArray()
            dates.forEach { array.put(it.toString()) }
            data.put(index.toString(), array)
        }
        return data.toString()
    }

    /** Valide uniquement la date d'aujourd'hui ; le cinquième jour termine le défi. */
    fun completeChallenge(index: Int, now: Long = System.currentTimeMillis()): Boolean = synchronized(lock) {
        updateCycle(now)
        val series = (prefs.getLong("challengeNumber", 0) % 2).toInt()
        val done = checked()
        if (index !in (series * 10 until (series + 1) * 10) || index in done ||
            now < prefs.getLong("challengeStart", now)) return@synchronized false
        val today = localDate(now)
        val days = readChallengeDays()
        val previous = days[index].orEmpty()
        if (previous.size >= CHALLENGE_REQUIRED_DAYS ||
            previous.lastOrNull()?.let { today <= it } == true) return@synchronized false
        val current = previous + today
        days[index] = current
        val editor = prefs.edit().putString("challengeDays", challengeDaysJson(days))
        if (current.size == CHALLENGE_REQUIRED_DAYS) {
            val array = JSONArray()
            (done + index).sorted().forEach { array.put(it) }
            val count = prefs.getInt("completedCount", 0) + 1
            val reward = if (count % 5 == 0) 5 else 0
            editor.putString("checked", array.toString())
                .putInt("completedCount", count)
                .putInt("pointUnits", shiftedPointUnits(reward * POINT_UNITS_PER_POINT))
        }
        // Dates, défi terminé, compteur et récompense sont enregistrés ensemble.
        editor.apply()
        true
    }

    private fun readCustomChallenges(): MutableList<StoredCustomChallenge> {
        val array = try { JSONArray(prefs.getString("customChallenges", "[]")) }
            catch (_: Exception) { JSONArray() }
        val result = mutableListOf<StoredCustomChallenge>()
        for (i in 0 until array.length()) {
            val value = array.optJSONObject(i) ?: continue
            val id = value.optString("id")
            val title = value.optString("title")
            val requiredDays = value.optInt("requiredDays", CUSTOM_MIN_DAYS)
            val stepsArray = value.optJSONArray("steps") ?: continue
            val steps = (0 until stepsArray.length()).map { stepsArray.optString(it) }
            if (id.isBlank() || title.isBlank() || steps.size != 3 || steps.any { it.isBlank() } ||
                requiredDays !in CUSTOM_MIN_DAYS..CUSTOM_MAX_DAYS || result.any { it.id == id }) continue
            val dates = value.optJSONArray("days") ?: JSONArray()
            val days = (0 until dates.length()).mapNotNull { j ->
                try { LocalDate.parse(dates.optString(j)) } catch (_: Exception) { null }
            }.distinct().sorted().take(requiredDays)
            val checked = value.optJSONArray("checkedSteps") ?: JSONArray()
            val checkedSteps = (0 until checked.length()).map { checked.optInt(it, -1) }
                .filter { it in 0..2 }.toSet()
            val stepDate = try { LocalDate.parse(value.optString("stepDate")) }
                catch (_: Exception) { null }
            result.add(StoredCustomChallenge(id, title, steps, requiredDays, value.optLong("createdAt"),
                days, stepDate, checkedSteps, value.optBoolean("rewardClaimed", false)))
        }
        return result
    }

    private fun customChallengesJson(challenges: List<StoredCustomChallenge>): String {
        val array = JSONArray()
        challenges.forEach { challenge ->
            array.put(JSONObject().apply {
                put("id", challenge.id)
                put("title", challenge.title)
                put("steps", JSONArray(challenge.steps))
                put("requiredDays", challenge.requiredDays)
                put("createdAt", challenge.createdAt)
                put("days", JSONArray(challenge.days.map { it.toString() }))
                put("stepDate", challenge.stepDate?.toString() ?: "")
                put("checkedSteps", JSONArray(challenge.checkedSteps.sorted()))
                put("rewardClaimed", challenge.rewardClaimed)
            })
        }
        return array.toString()
    }

    private fun canEditCustomToday(challenge: StoredCustomChallenge, now: Long): Boolean {
        val today = localDate(now)
        return planType().canCreateCustomChallenges && !challenge.completed && now >= challenge.createdAt &&
            (challenge.days.lastOrNull()?.let { today > it } ?: true) &&
            (challenge.stepDate?.let { today >= it } ?: true)
    }

    private fun customSnapshots(now: Long): List<CustomChallenge> {
        val today = localDate(now)
        return readCustomChallenges().map { challenge ->
            val checked = if (challenge.stepDate == today) challenge.checkedSteps else emptySet()
            val canEditToday = canEditCustomToday(challenge, now)
            CustomChallenge(challenge.id, challenge.title, challenge.steps, challenge.days.size,
                challenge.requiredDays, checked, today in challenge.days, canEditToday,
                canEditToday && checked.size == 3, challenge.completed, challenge.rewardClaimed)
        }
    }

    fun createCustomChallenge(
        title: String, steps: List<String>, requiredDays: Int,
        now: Long = System.currentTimeMillis()
    ): String? = synchronized(lock) {
        if (!planType().canCreateCustomChallenges) return@synchronized "Les défis personnalisés sont disponibles avec Premium et Ultra."
        val cleanTitle = title.trim()
        val cleanSteps = steps.map { it.trim() }
        if (cleanTitle.isEmpty()) return@synchronized "Donne un titre à ton défi."
        if (cleanSteps.size != 3 || cleanSteps.any { it.isEmpty() })
            return@synchronized "Un défi personnalisé demande exactement 3 étapes renseignées."
        if (requiredDays !in CUSTOM_MIN_DAYS..CUSTOM_MAX_DAYS)
            return@synchronized "Choisis une durée entre $CUSTOM_MIN_DAYS et $CUSTOM_MAX_DAYS jours."
        val challenges = readCustomChallenges()
        val active = challenges.filterNot { it.completed }
        if (active.size >= MAX_ACTIVE_CUSTOM_CHALLENGES)
            return@synchronized "$MAX_ACTIVE_CUSTOM_CHALLENGES défis personnalisés actifs maximum."
        fun normalized(text: String) = text.lowercase(Locale.ROOT).replace(Regex("\\s+"), " ").trim()
        if (active.any { normalized(it.title) == normalized(cleanTitle) &&
                it.steps.map(::normalized) == cleanSteps.map(::normalized) })
            return@synchronized "Ce défi personnalisé est déjà actif."
        challenges.add(StoredCustomChallenge(UUID.randomUUID().toString(), cleanTitle, cleanSteps, requiredDays, now))
        prefs.edit().putString("customChallenges", customChallengesJson(challenges)).apply()
        null
    }

    fun setCustomChallengeStep(
        id: String, index: Int, checked: Boolean, now: Long = System.currentTimeMillis()
    ): String? = synchronized(lock) {
        if (!planType().canCreateCustomChallenges) return@synchronized "Active Premium ou Ultra pour poursuivre ce défi personnalisé."
        if (index !in 0..2) return@synchronized "Étape introuvable."
        val challenges = readCustomChallenges()
        val position = challenges.indexOfFirst { it.id == id }
        if (position < 0) return@synchronized "Défi introuvable."
        val challenge = challenges[position]
        if (!canEditCustomToday(challenge, now)) return@synchronized "Cette journée ne peut plus être modifiée."
        val today = localDate(now)
        val steps = if (challenge.stepDate == today) challenge.checkedSteps else emptySet()
        challenges[position] = challenge.copy(stepDate = today,
            checkedSteps = if (checked) steps + index else steps - index)
        prefs.edit().putString("customChallenges", customChallengesJson(challenges)).apply()
        null
    }

    fun validateCustomChallengeToday(id: String, now: Long = System.currentTimeMillis()): String? = synchronized(lock) {
        if (!planType().canCreateCustomChallenges) return@synchronized "Active Premium ou Ultra pour poursuivre ce défi personnalisé."
        val challenges = readCustomChallenges()
        val position = challenges.indexOfFirst { it.id == id }
        if (position < 0) return@synchronized "Défi introuvable."
        val challenge = challenges[position]
        if (!canEditCustomToday(challenge, now)) return@synchronized "Cette journée est déjà validée ou indisponible."
        val today = localDate(now)
        if (challenge.stepDate != today || challenge.checkedSteps.size != 3)
            return@synchronized "Termine les 3 étapes d’aujourd’hui avant de valider."
        val days = challenge.days + today
        val earnsReward = days.size == challenge.requiredDays && !challenge.rewardClaimed
        challenges[position] = challenge.copy(days = days, rewardClaimed = challenge.rewardClaimed || earnsReward)
        val editor = prefs.edit().putString("customChallenges", customChallengesJson(challenges))
        if (earnsReward) editor.putInt("pointUnits", shiftedPointUnits(1))
        // Le solde exact et le marqueur de récompense sont dans une seule transaction.
        editor.apply()
        null
    }

    fun deleteCustomChallenge(id: String): String? = synchronized(lock) {
        val challenges = readCustomChallenges()
        if (challenges.none { it.id == id }) return@synchronized "Défi introuvable."
        prefs.edit().putString("customChallenges", customChallengesJson(challenges.filterNot { it.id == id })).apply()
        null
    }

    private fun snapshot(apps: List<TrackedApp>, raw: Map<String, Long>, now: Long): MareeSnapshot {
        updateCycle(now)
        val cycleStart = prefs.getLong("challengeStart", now)
        val daysRemaining = ceil((cycleStart + CHALLENGE_CYCLE - now) / DAY.toDouble()).toInt().coerceAtLeast(0)
        val series = (prefs.getLong("challengeNumber", 0) % 2L).toInt()
        val done = checked()
        val days = readChallengeDays()
        val today = localDate(now)
        val progress = (series * 10 until (series + 1) * 10).associateWith { index ->
            val dates = days[index].orEmpty()
            val completedDays = if (index in done) CHALLENGE_REQUIRED_DAYS else dates.size
            ChallengeProgress(
                completedDays = completedDays,
                validatedToday = today in dates,
                canValidateToday = completedDays < CHALLENGE_REQUIRED_DAYS && now >= cycleStart &&
                    (dates.lastOrNull()?.let { today > it } ?: true)
            )
        }
        return MareeSnapshot(
            points(),
            apps.map {
                AppSnapshot(it.packageName, it.limitMinutes,
                    ((raw[it.packageName] ?: 0L) - it.baselineMillis).coerceAtLeast(0L),
                    it.lockedUntil, it.warnedOn)
            },
            isPremium(),
            series,
            done, daysRemaining, prefs.getInt("completedCount", 0), progress,
            pointUnits(), planType(), customSnapshots(now)
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
        var p = pointUnits().toLong()
        var processed = 0
        while (now - periodStart >= DAY && processed < 7) {
            if (!breach) p += 10 * POINT_UNITS_PER_POINT
            periodStart += DAY
            breach = false
            processed++
        }
        if (now - periodStart >= DAY) {
            periodStart = now // limite de rattrapage ; ne pas créditer une durée non vérifiée
            breach = false
        }
        prefs.edit().putInt("pointUnits", p.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()).putLong("periodStart", periodStart)
            .putBoolean("periodBreach", breach).apply()

    }
}
