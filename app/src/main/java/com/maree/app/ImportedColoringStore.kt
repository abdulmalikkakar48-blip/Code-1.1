package com.maree.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

/** Separate, bounded storage: adding the supplied drawings never changes older coloring keys. */
internal class ImportedColoringStore internal constructor(private val prefs: SharedPreferences) {
    constructor(context: Context) : this(context.applicationContext.getSharedPreferences("maree", Context.MODE_PRIVATE))
    private val generation = synchronized(LocalFeatureLock.lock) { LocalFeatureLock.erasureGeneration }
    fun page(id: String): List<Int> = synchronized(LocalFeatureLock.lock) {
        val page = importedColoringPages.firstOrNull { it.id == id } ?: return@synchronized emptyList()
        read().firstOrNull { it.id == id }?.colors ?: List(page.regions.size) { 0 }
    }
    fun savePage(id: String, colors: List<Int>): Boolean = synchronized(LocalFeatureLock.lock) {
        val page = importedColoringPages.firstOrNull { it.id == id }
        if (generation != LocalFeatureLock.erasureGeneration || page == null || colors.size != page.regions.size || colors.any { it !in 0..16 }) return@synchronized false
        val rows = read().filterNot { it.id == id }.toMutableList()
        if (colors.any { it != 0 }) rows.add(IllustrationPageEntry(id, colors.toList()))
        write(rows)
    }
    fun resetPage(id: String): Boolean = synchronized(LocalFeatureLock.lock) {
        if (generation != LocalFeatureLock.erasureGeneration || importedColoringPages.none { it.id == id }) return@synchronized false
        write(read().filterNot { it.id == id })
    }
    fun pagesForExport(): JSONArray = synchronized(LocalFeatureLock.lock) { serialize(read()) }
    private fun read(): List<IllustrationPageEntry> {
        val raw = runCatching { prefs.getString(KEY, null) }.getOrNull() ?: return emptyList()
        if (raw.length > MAX_CHARACTERS) return emptyList()
        return runCatching {
            val array = JSONArray(raw); require(array.length() <= importedColoringPages.size)
            val seen = mutableSetOf<String>()
            (0 until array.length()).mapNotNull { index -> runCatching {
                val row = array.getJSONObject(index)
                val id = row.get("id") as? String ?: error("Invalid ID")
                val page = importedColoringPages.firstOrNull { it.id == id } ?: error("Unknown drawing")
                val values = row.getJSONArray("colors"); require(values.length() == page.regions.size)
                val colors = (0 until values.length()).map { values.get(it) as? Int ?: error("Invalid palette index") }
                require(colors.all { it in 0..16 } && seen.add(id))
                if (colors.none { it != 0 }) null else IllustrationPageEntry(id, colors)
            }.getOrNull() }
        }.getOrDefault(emptyList())
    }
    private fun serialize(rows: List<IllustrationPageEntry>) = JSONArray().apply {
        importedColoringPages.forEach { page -> rows.firstOrNull { it.id == page.id }?.let {
            put(JSONObject().put("id", it.id).put("colors", JSONArray(it.colors)))
        } }
    }
    @SuppressLint("ApplySharedPref")
    private fun write(rows: List<IllustrationPageEntry>): Boolean = runCatching {
        val data = serialize(rows)
        if (data.length() == 0) prefs.edit().remove(KEY).commit()
        else { val raw = data.toString(); raw.length <= MAX_CHARACTERS && prefs.edit().putString(KEY, raw).commit() }
    }.getOrDefault(false)
    companion object { internal const val KEY = "importedColoring_v1"; internal const val MAX_CHARACTERS = 100_000 }
}
