package com.maree.app

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.util.UUID

internal data class CalendarTask(val id: String, val date: LocalDate, val title: String, val completed: Boolean)

/** Independent of observed usage and challenge rewards; local, manually managed planning. */
internal class CalendarTaskStore internal constructor(private val prefs: SharedPreferences) {
    constructor(context: Context) : this(context.applicationContext.getSharedPreferences("maree", Context.MODE_PRIVATE))
    private val generation = synchronized(LocalFeatureLock.lock) { LocalFeatureLock.erasureGeneration }

    fun entries(): List<CalendarTask> = synchronized(LocalFeatureLock.lock) { read() }
    fun save(date: LocalDate, title: String, id: String? = null): Boolean = synchronized(LocalFeatureLock.lock) {
        if (generation != LocalFeatureLock.erasureGeneration || title.trim().isEmpty() || title.trim().length > 240 || date.year !in 1900..2200) return@synchronized false
        val rows = read().toMutableList()
        val index = rows.indexOfFirst { it.id == id }
        if (id != null && index < 0) return@synchronized false
        if (index < 0 && rows.size >= 500) return@synchronized false
        val task = CalendarTask(id ?: UUID.randomUUID().toString(), date, title.trim(), if (index >= 0) rows[index].completed else false)
        if (index >= 0) rows[index] = task else rows.add(task)
        write(rows)
    }
    fun setCompleted(id: String, completed: Boolean): Boolean = synchronized(LocalFeatureLock.lock) {
        if (generation != LocalFeatureLock.erasureGeneration) return@synchronized false
        val rows = read(); if (rows.none { it.id == id }) return@synchronized false
        write(rows.map { if (it.id == id) it.copy(completed = completed) else it })
    }
    fun delete(id: String): Boolean = synchronized(LocalFeatureLock.lock) {
        if (generation != LocalFeatureLock.erasureGeneration) return@synchronized false
        val rows = read(); if (rows.none { it.id == id }) return@synchronized false
        write(rows.filterNot { it.id == id })
    }
    fun exportJson(): JSONArray = synchronized(LocalFeatureLock.lock) { serialize(read()) }
    private fun write(rows: List<CalendarTask>): Boolean = prefs.edit().putString("calendarTasks_v1", serialize(rows).toString()).commit()
    private fun serialize(rows: List<CalendarTask>) = JSONArray().apply {
        rows.forEach { row -> put(JSONObject().put("id", row.id).put("date", row.date.toString()).put("title", row.title).put("completed", row.completed)) }
    }
    private fun read(): List<CalendarTask> {
        val raw = runCatching { prefs.getString("calendarTasks_v1", null) }.getOrNull() ?: return emptyList()
        if (raw.length > 500_000) return emptyList()
        return runCatching {
            val array = JSONArray(raw); val ids = mutableSetOf<String>()
            (0 until minOf(array.length(), 500)).mapNotNull { index -> runCatching {
                val row = array.getJSONObject(index)
                val id = row.getString("id"); require(UUID.fromString(id).toString() == id)
                val date = LocalDate.parse(row.getString("date")); require(date.year in 1900..2200)
                val title = row.getString("title").trim(); require(title.isNotEmpty() && title.length <= 240)
                val completed = row.get("completed") as? Boolean ?: error("Invalid completion")
                require(ids.add(id)); CalendarTask(id, date, title, completed)
            }.getOrNull() }
        }.getOrDefault(emptyList())
    }
}
