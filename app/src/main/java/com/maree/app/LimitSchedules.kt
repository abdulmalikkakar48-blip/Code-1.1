package com.maree.app

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant
import java.time.ZoneId
import java.util.Locale
import java.util.UUID

const val MAX_LIMIT_SCHEDULES = 20
private const val SCHEDULES_KEY = "limitSchedules"
private const val MINUTES_PER_DAY = 1_440
private const val MINUTES_PER_WEEK = 7 * MINUTES_PER_DAY

/** Selected weekdays name the start of a window, including one ending the next morning. */
data class LimitSchedule(
    val id: String,
    val name: String,
    val packageName: String,
    val weekdays: Set<Int>,
    val startMinute: Int,
    val endMinute: Int,
    val limitMinutes: Int,
    val enabled: Boolean = true
)

/**
 * Changes only the allowance of the existing usage counter. Its usage and any existing
 * 24-hour lock must be preserved by the caller when a window starts or ends.
 */
fun effectiveScheduledLimit(
    rules: List<LimitSchedule>,
    packageName: String,
    baseLimit: Int,
    plan: PlanType,
    now: Long = System.currentTimeMillis(),
    zone: ZoneId = ZoneId.systemDefault()
): Int {
    if (plan == PlanType.FREE) return baseLimit
    val local = Instant.ofEpochMilli(now).atZone(zone)
    val weekday = local.dayOfWeek.value
    val previousWeekday = if (weekday == 1) 7 else weekday - 1
    val minute = local.hour * 60 + local.minute
    return rules.asSequence().filter { rule ->
        rule.enabled && rule.packageName == packageName && validSchedule(rule) &&
            if (rule.startMinute < rule.endMinute) {
                weekday in rule.weekdays && minute >= rule.startMinute && minute < rule.endMinute
            } else {
                (weekday in rule.weekdays && minute >= rule.startMinute) ||
                    (previousWeekday in rule.weekdays && minute < rule.endMinute)
            }
    }.map { it.limitMinutes }.minOrNull() ?: baseLimit
}

/** Offline persistence shared with tracking, export and personal-data erasure. */
class LimitScheduleStore internal constructor(private val prefs: SharedPreferences) {
    constructor(context: Context) : this(
        context.applicationContext.getSharedPreferences("maree", Context.MODE_PRIVATE)
    )

    fun entries(): List<LimitSchedule> = synchronized(LocalFeatureLock.lock) { readEntries() }

    fun save(rule: LimitSchedule, plan: PlanType, pointUnits: Int): String? =
        synchronized(LocalFeatureLock.lock) {
            mutationIssue(plan, pointUnits)?.let { return@synchronized it }
            val normalized = rule.copy(
                id = rule.id.lowercase(Locale.ROOT),
                name = rule.name.trim(),
                packageName = rule.packageName.trim(),
                weekdays = rule.weekdays.toSet()
            )
            if (!validSchedule(normalized)) return@synchronized tr(
                "Vérifie le nom, l’application, les jours, les horaires et la limite de 5 à 240 minutes.",
                "Check the name, app, days, times and the limit of 5 to 240 minutes."
            )
            val current = readEntries()
            val existing = current.indexOfFirst { it.id == normalized.id }
            if (existing < 0 && current.size >= MAX_LIMIT_SCHEDULES) return@synchronized tr(
                "Tu peux enregistrer au maximum $MAX_LIMIT_SCHEDULES plages horaires.",
                "You can save up to $MAX_LIMIT_SCHEDULES time slots."
            )
            if (current.any { it.id != normalized.id && schedulesOverlap(it, normalized) }) {
                return@synchronized tr(
                    "Ces horaires chevauchent une autre plage active pour cette application. Choisis des horaires distincts.",
                    "These times overlap another active slot for this app. Choose separate times."
                )
            }
            val next = current.toMutableList()
            if (existing >= 0) next[existing] = normalized else next.add(normalized)
            persist(next)
        }

    fun remove(id: String, plan: PlanType, pointUnits: Int): String? =
        synchronized(LocalFeatureLock.lock) {
            mutationIssue(plan, pointUnits)?.let { return@synchronized it }
            val current = readEntries()
            val normalizedId = id.lowercase(Locale.ROOT)
            if (current.none { it.id == normalizedId }) return@synchronized tr(
                "Cette plage horaire n’existe plus.", "This time slot no longer exists."
            )
            persist(current.filterNot { it.id == normalizedId })
        }

    fun exportJson(): JSONArray = synchronized(LocalFeatureLock.lock) { schedulesJson(readEntries()) }

    private fun mutationIssue(plan: PlanType, pointUnits: Int): String? {
        // The saved balance and plan guard against a dialog using an older snapshot.
        val currentPlan = try {
            if (prefs.contains("plan")) {
                PlanType.entries.firstOrNull { it.name == prefs.getString("plan", null) } ?: PlanType.FREE
            } else plan
        } catch (_: ClassCastException) { PlanType.FREE }
        val currentPoints = try {
            if (prefs.contains("pointUnits")) prefs.getInt("pointUnits", 0) else pointUnits
        } catch (_: ClassCastException) { 0 }
        if (plan == PlanType.FREE || currentPlan == PlanType.FREE) return tr(
            "Les plages horaires nécessitent Premium ou Ultra.", "Time slots require Premium or Ultra."
        )
        if (pointUnits <= 0 || currentPoints <= 0) return tr(
            "À 0 point, créer, modifier, activer ou supprimer une plage horaire reste bloqué.",
            "At 0 points, creating, editing, enabling or deleting a time slot stays blocked."
        )
        return null
    }

    private fun readEntries(): List<LimitSchedule> {
        val data = try { JSONArray(prefs.getString(SCHEDULES_KEY, "[]")) }
        catch (_: Exception) { return emptyList() }
        val result = mutableListOf<LimitSchedule>()
        for (index in 0 until data.length()) {
            if (result.size >= MAX_LIMIT_SCHEDULES) break
            val rule = parseSchedule(data.optJSONObject(index)) ?: continue
            if (result.any { it.id == rule.id || schedulesOverlap(it, rule) }) continue
            result.add(rule)
        }
        return result
    }

    private fun persist(rules: List<LimitSchedule>): String? =
        if (prefs.edit().putString(SCHEDULES_KEY, schedulesJson(rules).toString()).commit()) null
        else tr("La plage horaire n’a pas pu être enregistrée. Réessaie.",
            "The time slot could not be saved. Try again.")
}

private fun validSchedule(rule: LimitSchedule): Boolean {
    val validId = try { UUID.fromString(rule.id).toString().equals(rule.id, ignoreCase = true) }
        catch (_: IllegalArgumentException) { false }
    return validId && rule.name.trim().isNotEmpty() && rule.name.length <= 40 &&
        rule.packageName.trim().isNotEmpty() && rule.packageName.length <= 250 &&
        rule.weekdays.isNotEmpty() && rule.weekdays.all { it in 1..7 } &&
        rule.startMinute in 0..1439 && rule.endMinute in 0..1439 &&
        rule.startMinute != rule.endMinute && rule.limitMinutes in 5..240
}

private fun parseSchedule(value: JSONObject?): LimitSchedule? {
    if (value == null) return null
    return try {
        val days = value.get("weekdays") as? JSONArray ?: return null
        val weekdays = (0 until days.length()).map { integerValue(days.get(it)) ?: return null }.toSet()
        val rule = LimitSchedule(
            (value.get("id") as? String ?: return null).lowercase(Locale.ROOT),
            value.get("name") as? String ?: return null,
            value.get("packageName") as? String ?: return null,
            weekdays,
            integerValue(value.get("startMinute")) ?: return null,
            integerValue(value.get("endMinute")) ?: return null,
            integerValue(value.get("limitMinutes")) ?: return null,
            value.get("enabled") as? Boolean ?: return null
        )
        if (validSchedule(rule)) rule.copy(name = rule.name.trim(), packageName = rule.packageName.trim()) else null
    } catch (_: Exception) { null }
}

private fun integerValue(value: Any?): Int? = when (value) {
    is Int -> value
    is Long -> if (value in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong()) value.toInt() else null
    else -> null
}

private fun schedulesJson(rules: List<LimitSchedule>): JSONArray = JSONArray().apply {
    rules.forEach { rule ->
        put(JSONObject().apply {
            put("id", rule.id)
            put("name", rule.name)
            put("packageName", rule.packageName)
            put("weekdays", JSONArray(rule.weekdays.sorted()))
            put("startMinute", rule.startMinute)
            put("endMinute", rule.endMinute)
            put("limitMinutes", rule.limitMinutes)
            put("enabled", rule.enabled)
        })
    }
}

private fun schedulesOverlap(first: LimitSchedule, second: LimitSchedule): Boolean =
    first.enabled && second.enabled && first.packageName == second.packageName &&
        weeklyWindows(first).any { left ->
            weeklyWindows(second).any { right -> left.first < right.second && right.first < left.second }
        }

private fun weeklyWindows(rule: LimitSchedule): List<Pair<Int, Int>> = rule.weekdays.flatMap { day ->
    val start = (day - 1) * MINUTES_PER_DAY + rule.startMinute
    val duration = Math.floorMod(rule.endMinute - rule.startMinute, MINUTES_PER_DAY)
    val end = start + duration
    if (end <= MINUTES_PER_WEEK) listOf(start to end)
    else listOf(start to MINUTES_PER_WEEK, 0 to (end - MINUTES_PER_WEEK))
}
