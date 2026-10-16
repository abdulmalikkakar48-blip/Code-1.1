package com.maree.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

internal data class ColoringPageEntry(val id: String, val cells: List<Int>)

/** Fixed original grids, kept on the device. Old sessions cannot restore erased data. */
internal class ColoringStore internal constructor(private val prefs: SharedPreferences) {
    constructor(context: Context) : this(context.applicationContext.getSharedPreferences("maree", Context.MODE_PRIVATE))
    private val generation = synchronized(LocalFeatureLock.lock) { LocalFeatureLock.erasureGeneration }
    fun entries(): List<ColoringPageEntry> = synchronized(LocalFeatureLock.lock) { read() }
    fun loadPage(pageId: String): List<Int> = synchronized(LocalFeatureLock.lock) {
        read().firstOrNull { it.id == pageId }?.cells ?: List(COLORING_CELLS) { 0 }
    }
    fun savePage(pageId: String, cells: List<Int>): Boolean = synchronized(LocalFeatureLock.lock) {
        if (generation != LocalFeatureLock.erasureGeneration || coloringTemplates.none { it.id == pageId } || !validColoringCells(cells)) return@synchronized false
        runCatching {
            val rows = read().filterNot { it.id == pageId }.toMutableList()
            if (cells.any { it != 0 }) rows.add(ColoringPageEntry(pageId, cells.toList()))
            write(rows)
        }.getOrDefault(false)
    }
    fun resetPage(pageId: String): Boolean = synchronized(LocalFeatureLock.lock) {
        if (generation != LocalFeatureLock.erasureGeneration || coloringTemplates.none { it.id == pageId }) return@synchronized false
        runCatching { write(read().filterNot { it.id == pageId }) }.getOrDefault(false)
    }
    fun exportJson(): JSONArray = synchronized(LocalFeatureLock.lock) { serialize(read()) }
    // A failed Android commit may already have changed the in-memory preferences. Never
    // report success or assume rollback: the UI keeps the requested save/reset for retry.
    @SuppressLint("ApplySharedPref") // UI callers use Dispatchers.IO and require the commit result.
    private fun write(rows: List<ColoringPageEntry>): Boolean {
        val serialized = serialize(rows)
        return if (serialized.length() == 0) prefs.edit().remove(KEY).commit()
        else prefs.edit().putString(KEY, serialized.toString()).commit()
    }
    private fun serialize(rows: List<ColoringPageEntry>) = JSONArray().apply {
        coloringTemplates.forEach { template -> rows.firstOrNull { it.id == template.id }?.let { entry ->
            put(JSONObject().put("id", entry.id).put("cells", JSONArray(entry.cells)))
        } }
    }
    private fun read(): List<ColoringPageEntry> {
        val raw = runCatching { prefs.getString(KEY, null) }.getOrNull() ?: return emptyList()
        if (raw.length > 20_000) return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            if (array.length() > coloringTemplates.size) return@runCatching emptyList()
            val seen = mutableSetOf<String>()
            (0 until array.length()).mapNotNull { index -> runCatching {
                val row = array.getJSONObject(index)
                val id = row.get("id") as? String ?: error("Invalid ID")
                require(coloringTemplates.any { it.id == id })
                val values = row.getJSONArray("cells")
                require(values.length() == COLORING_CELLS)
                val cells = (0 until values.length()).map { values.get(it) as? Int ?: error("Invalid color") }
                require(validColoringCells(cells) && seen.add(id))
                ColoringPageEntry(id, cells)
            }.getOrNull() }
        }.getOrDefault(emptyList())
    }
    companion object { internal const val KEY = "coloringPages_v1" }
}
