package com.maree.app

import android.content.Context
import android.content.SharedPreferences
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import org.json.JSONArray
import org.json.JSONObject

/** A local date has partial observations, never an assumed full day of success. */
data class DailyHistoryEntry(
    val date: LocalDate,
    val usageMillis: Map<String, Long> = emptyMap(),
    val validationIds: Set<String> = emptySet(),
    val breachedPackages: Set<String> = emptySet(),
    val observedMillis: Long = 0L,
    val sampleCount: Int = 0,
    /** At least one tracking pause was recorded on this date. */
    val paused: Boolean = false
) {
    val hasMeasuredUsage: Boolean get() = observedMillis > 0L
    val totalUsageMillis: Long get() = usageMillis.values.sum()
}

/**
 * Only consecutive active Android readings contribute deltas. The baseline lives in
 * the same preferences as history, so erasure also prevents old in-memory samples
 * from reappearing. No challenge titles or coach messages enter this store.
 */
class DailyHistoryStore internal constructor(private val prefs: SharedPreferences) {
    constructor(context: Context) : this(
        context.applicationContext.getSharedPreferences("maree", Context.MODE_PRIVATE)
    )

    fun recordUsage(
        raw: Map<String, Long>,
        now: Long = System.currentTimeMillis(),
        zone: ZoneId = ZoneId.systemDefault()
    ): Boolean = synchronized(LocalFeatureLock.lock) {
        val today = localDate(now, zone)
        val history = readEntries(today).associateByTo(sortedMapOf()) { it.date }
        val readings = raw.asSequence().filter { validPackage(it.key) && it.value in 0..MAX_DAY_MILLIS }
            .sortedBy { it.key }.take(MAX_APPS).associate { it.key to it.value }
        if (readings.isEmpty()) return@synchronized write(history.values.toList(), null)

        val previous = readSample()
        val sample = Sample(now, today, zone.id, readings)
        val entry = history[today] ?: DailyHistoryEntry(today)
        val repeated = previous?.let { it.timestamp == now && it.date == today && it.zone == zone.id } == true
        if (repeated) return@synchronized write(history.values.toList(), sample)

        val gap = previous?.let { now - it.timestamp } ?: 0L
        val shared = previous?.raw?.keys?.intersect(readings.keys).orEmpty()
        val consecutive = previous != null && previous.date == today && previous.zone == zone.id &&
            now > previous.timestamp && gap in 1..MAX_SAMPLE_GAP_MILLIS && shared.isNotEmpty() &&
            shared.none { readings.getValue(it) < previous.raw.getValue(it) }
        val accumulated = entry.usageMillis.toMutableMap()
        if (consecutive) {
            for (pkg in shared.sorted()) {
                // The cumulative reader may correct an old event. Never attribute more
                // than this observed interval to an app or backfill a newly added app.
                val delta = (readings.getValue(pkg) - previous!!.raw.getValue(pkg)).coerceAtMost(gap)
                if (pkg in accumulated || accumulated.size < MAX_APPS) {
                    accumulated[pkg] = ((accumulated[pkg] ?: 0L) + delta).coerceAtMost(MAX_DAY_MILLIS)
                }
            }
        }
        history[today] = entry.copy(
            usageMillis = accumulated.toSortedMap(),
            observedMillis = (entry.observedMillis + if (consecutive) gap else 0L).coerceAtMost(MAX_DAY_MILLIS),
            sampleCount = (entry.sampleCount + 1).coerceAtMost(MAX_SAMPLE_COUNT)
        )
        write(history.values.toList(), sample)
    }

    /** Clears the baseline immediately; use during every explicit stop of tracking. */
    fun pause(
        now: Long = System.currentTimeMillis(),
        zone: ZoneId = ZoneId.systemDefault()
    ): Boolean = synchronized(LocalFeatureLock.lock) {
        val today = localDate(now, zone)
        val history = readEntries(today).associateByTo(sortedMapOf()) { it.date }
        history[today] = (history[today] ?: DailyHistoryEntry(today)).copy(paused = true)
        write(history.values.toList(), null)
    }

    /** Namespaced, bounded machine IDs keep standard, custom and program events distinct. */
    fun recordValidation(
        id: String,
        now: Long = System.currentTimeMillis(),
        zone: ZoneId = ZoneId.systemDefault()
    ): Boolean = synchronized(LocalFeatureLock.lock) {
        if (!validValidationId(id)) return@synchronized false
        updateDay(now, zone) { entry ->
            if (id !in entry.validationIds && entry.validationIds.size >= MAX_VALIDATIONS) null
            else entry.copy(validationIds = (entry.validationIds + id).toSortedSet())
        }
    }

    fun recordBreach(
        pkg: String,
        now: Long = System.currentTimeMillis(),
        zone: ZoneId = ZoneId.systemDefault()
    ): Boolean = synchronized(LocalFeatureLock.lock) {
        if (!validPackage(pkg)) return@synchronized false
        updateDay(now, zone) { entry ->
            if (pkg !in entry.breachedPackages && entry.breachedPackages.size >= MAX_APPS) null
            else entry.copy(breachedPackages = (entry.breachedPackages + pkg).toSortedSet())
        }
    }

    fun entries(
        now: Long = System.currentTimeMillis(),
        zone: ZoneId = ZoneId.systemDefault()
    ): List<DailyHistoryEntry> = synchronized(LocalFeatureLock.lock) {
        readEntries(localDate(now, zone))
    }

    /** Export only validated history fields, never arbitrary preference values or the raw baseline. */
    fun exportJson(
        now: Long = System.currentTimeMillis(),
        zone: ZoneId = ZoneId.systemDefault()
    ): JSONArray = synchronized(LocalFeatureLock.lock) {
        encodeEntries(readEntries(localDate(now, zone)))
    }

    /** Durable cleanup on next use; reads and exports intentionally never write. */
    fun prune(
        now: Long = System.currentTimeMillis(),
        zone: ZoneId = ZoneId.systemDefault()
    ): Boolean = synchronized(LocalFeatureLock.lock) {
        runCatching {
            val historyPresent = prefs.contains(HISTORY_KEY)
            val samplePresent = prefs.contains(SAMPLE_KEY)
            if (!historyPresent && !samplePresent) return@runCatching true
            val today = localDate(now, zone)
            val historyText = encodeBoundedEntries(readEntries(today))
            val sampleText = encodeSample(readSample()?.takeIf {
                it.date == today && it.zone == zone.id && it.timestamp <= now &&
                    now - it.timestamp <= MAX_SAMPLE_GAP_MILLIS
            })
            val currentHistory = runCatching { prefs.getString(HISTORY_KEY, null) }.getOrNull()
            val currentSample = runCatching { prefs.getString(SAMPLE_KEY, null) }.getOrNull()
            val historyChanged = if (historyText == null) historyPresent else currentHistory != historyText
            val sampleChanged = if (sampleText == null) samplePresent else currentSample != sampleText
            if (!historyChanged && !sampleChanged) return@runCatching true
            val editor = prefs.edit()
            if (historyChanged) {
                if (historyText == null) editor.remove(HISTORY_KEY) else editor.putString(HISTORY_KEY, historyText)
            }
            if (sampleChanged) {
                if (sampleText == null) editor.remove(SAMPLE_KEY) else editor.putString(SAMPLE_KEY, sampleText)
            }
            editor.commit()
        }.getOrDefault(false)
    }

    private fun updateDay(
        now: Long,
        zone: ZoneId,
        transform: (DailyHistoryEntry) -> DailyHistoryEntry?
    ): Boolean {
        val today = localDate(now, zone)
        val history = readEntries(today).associateByTo(sortedMapOf()) { it.date }
        val updated = transform(history[today] ?: DailyHistoryEntry(today)) ?: return false
        history[today] = updated
        return write(history.values.toList(), readSample()?.takeIf {
            it.date == today && it.zone == zone.id && it.timestamp <= now
        })
    }

    private fun readEntries(today: LocalDate): List<DailyHistoryEntry> = runCatching {
        val text = prefs.getString(HISTORY_KEY, null) ?: return@runCatching emptyList()
        if (text.length > MAX_HISTORY_CHARS) return@runCatching emptyList()
        val json = JSONArray(text)
        val first = today.minusDays(RETENTION_DAYS - 1L)
        val entries = sortedMapOf<LocalDate, DailyHistoryEntry>()
        // Bound work even when an invalid preference has thousands of tiny rows.
        for (index in 0 until json.length().coerceAtMost(MAX_INPUT_ROWS)) {
            val item = json.optJSONObject(index) ?: continue
            val date = parseDate(item.opt("date")) ?: continue
            if (date < first || date > today) continue
            val samples = boundedLong(item.opt("sampleCount"), MAX_SAMPLE_COUNT.toLong())?.toInt() ?: 0
            val observed = if (samples >= 2) boundedLong(item.opt("observedMillis"), MAX_DAY_MILLIS) ?: 0L else 0L
            entries[date] = DailyHistoryEntry(
                date = date,
                usageMillis = readUsage(item.optJSONObject("usageMillis"), observed, keepZero = observed > 0L),
                validationIds = readStrings(item.optJSONArray("validationIds"), MAX_VALIDATIONS, ::validValidationId),
                breachedPackages = readStrings(item.optJSONArray("breachedPackages"), MAX_APPS, ::validPackage),
                observedMillis = observed,
                sampleCount = samples,
                paused = item.opt("paused") == true
            )
        }
        entries.values.toList().takeLast(RETENTION_DAYS)
    }.getOrDefault(emptyList())

    private fun readSample(): Sample? = runCatching {
        val text = prefs.getString(SAMPLE_KEY, null) ?: return@runCatching null
        if (text.length > MAX_SAMPLE_CHARS) return@runCatching null
        val item = JSONObject(text)
        val timestamp = boundedLong(item.opt("timestamp"), Long.MAX_VALUE) ?: return@runCatching null
        val date = parseDate(item.opt("date")) ?: return@runCatching null
        val zone = item.opt("zone") as? String ?: return@runCatching null
        if (zone.length > 80 || localDate(timestamp, ZoneId.of(zone)) != date) return@runCatching null
        val raw = readUsage(item.optJSONObject("raw"), MAX_DAY_MILLIS, keepZero = true)
        if (raw.isEmpty()) null else Sample(timestamp, date, zone, raw)
    }.getOrNull()

    private fun write(entries: List<DailyHistoryEntry>, sample: Sample?): Boolean = runCatching {
        val historyText = encodeBoundedEntries(entries)
        val sampleText = encodeSample(sample)
        val editor = prefs.edit()
        if (historyText == null) editor.remove(HISTORY_KEY) else editor.putString(HISTORY_KEY, historyText)
        if (sampleText == null) editor.remove(SAMPLE_KEY) else editor.putString(SAMPLE_KEY, sampleText)
        editor.commit()
    }.getOrDefault(false)

    private fun encodeBoundedEntries(entries: List<DailyHistoryEntry>): String? {
        var retained = entries.takeLast(RETENTION_DAYS)
        var encoded = encodeEntries(retained).toString()
        while (encoded.length > MAX_HISTORY_CHARS && retained.isNotEmpty()) {
            retained = retained.drop(1)
            encoded = encodeEntries(retained).toString()
        }
        return encoded.takeIf { retained.isNotEmpty() }
    }

    private fun encodeSample(sample: Sample?): String? = sample?.let {
        JSONObject().put("timestamp", it.timestamp).put("date", it.date.toString())
            .put("zone", it.zone).put("raw", JSONObject(it.raw)).toString()
    }

    private fun encodeEntries(entries: List<DailyHistoryEntry>): JSONArray = JSONArray().apply {
        entries.forEach { entry ->
            put(JSONObject().put("date", entry.date.toString())
                .put("usageMillis", JSONObject(entry.usageMillis))
                .put("validationIds", JSONArray(entry.validationIds.sorted()))
                .put("breachedPackages", JSONArray(entry.breachedPackages.sorted()))
                .put("observedMillis", entry.observedMillis).put("sampleCount", entry.sampleCount)
                .put("paused", entry.paused))
        }
    }

    private fun readUsage(item: JSONObject?, max: Long, keepZero: Boolean): Map<String, Long> {
        if (item == null) return emptyMap()
        val result = sortedMapOf<String, Long>()
        val keys = item.keys()
        while (keys.hasNext() && result.size < MAX_APPS) {
            val pkg = keys.next()
            if (!validPackage(pkg)) continue
            val duration = boundedLong(item.opt(pkg), max) ?: continue
            if (keepZero || duration > 0L) result[pkg] = duration
        }
        return result
    }

    private fun readStrings(item: JSONArray?, max: Int, valid: (String) -> Boolean): Set<String> {
        if (item == null) return emptySet()
        val result = sortedSetOf<String>()
        for (index in 0 until item.length().coerceAtMost(MAX_INPUT_ROWS)) {
            val value = item.opt(index) as? String ?: continue
            if (valid(value)) result.add(value)
            if (result.size >= max) break
        }
        return result
    }

    private data class Sample(val timestamp: Long, val date: LocalDate, val zone: String, val raw: Map<String, Long>)

    companion object {
        internal const val HISTORY_KEY = "dailyHistory_v1"
        internal const val SAMPLE_KEY = "dailyHistory_sample_v1"
        internal const val RETENTION_DAYS = 90
        internal const val MAX_APPS = 500
        internal const val MAX_VALIDATIONS = 64
        internal const val MAX_SAMPLE_GAP_MILLIS = 30_000L
        // Includes a 25-hour DST day and leaves room for rare local clock transitions.
        private const val MAX_DAY_MILLIS = 26 * 60 * 60 * 1_000L
        private const val MAX_SAMPLE_COUNT = 100_000
        private const val MAX_HISTORY_CHARS = 4_000_000
        private const val MAX_SAMPLE_CHARS = 160_000
        private const val MAX_INPUT_ROWS = 900
        private val packagePattern = Regex("[A-Za-z][A-Za-z0-9_]*(?:\\.[A-Za-z0-9_]+)+")
        private val validationPattern = Regex("(?:standard|custom|program):[A-Za-z0-9][A-Za-z0-9._:-]*")
        private val datePattern = Regex("\\d{4}-\\d{2}-\\d{2}")

        private fun validPackage(pkg: String): Boolean = pkg.length <= 255 && packagePattern.matches(pkg)
        private fun validValidationId(id: String): Boolean = id.length <= 128 && validationPattern.matches(id)
        private fun localDate(now: Long, zone: ZoneId): LocalDate = Instant.ofEpochMilli(now).atZone(zone).toLocalDate()
        private fun parseDate(value: Any?): LocalDate? = (value as? String)?.takeIf(datePattern::matches)
            ?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        private fun boundedLong(value: Any?, maximum: Long): Long? = when (value) {
            is Int -> value.toLong()
            is Long -> value
            else -> null
        }?.takeIf { it in 0..maximum }
    }
}
