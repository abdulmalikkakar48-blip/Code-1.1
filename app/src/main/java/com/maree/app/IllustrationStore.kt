package com.maree.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

internal data class IllustrationPageEntry(val id: String, val colors: List<Int>)

/** Curved drawings have their own storage; existing pixel art is never migrated or overwritten. */
internal class IllustrationStore internal constructor(private val prefs: SharedPreferences) {
    constructor(context: Context) : this(context.applicationContext.getSharedPreferences("maree", Context.MODE_PRIVATE))

    private val generation = synchronized(LocalFeatureLock.lock) { LocalFeatureLock.erasureGeneration }

    fun page(pageId: String): List<Int> = synchronized(LocalFeatureLock.lock) {
        val template = illustrationPages.firstOrNull { it.id == pageId } ?: return@synchronized emptyList()
        read().firstOrNull { it.id == pageId }?.colors ?: List(template.regions.size) { 0 }
    }

    fun savePage(pageId: String, colors: List<Int>): Boolean = synchronized(LocalFeatureLock.lock) {
        val template = illustrationPages.firstOrNull { it.id == pageId }
        if (generation != LocalFeatureLock.erasureGeneration || template == null ||
            !validIllustrationColors(colors, template.regions.size)) return@synchronized false
        runCatching {
            val rows = read().filterNot { it.id == pageId }.toMutableList()
            if (colors.any { it != 0 }) rows.add(IllustrationPageEntry(pageId, colors.toList()))
            write(rows)
        }.getOrDefault(false)
    }

    fun resetPage(pageId: String): Boolean = synchronized(LocalFeatureLock.lock) {
        if (generation != LocalFeatureLock.erasureGeneration || illustrationPages.none { it.id == pageId })
            return@synchronized false
        runCatching { write(read().filterNot { it.id == pageId }) }.getOrDefault(false)
    }

    fun pagesForExport(): JSONArray = synchronized(LocalFeatureLock.lock) { serializeIllustrations(read()) }

    private fun read(): List<IllustrationPageEntry> = readIllustrationPages(
        runCatching { prefs.getString(KEY, null) }.getOrNull()
    )

    // A failed Android commit can already have changed memory. The caller must keep its
    // desired colors/reset and retry instead of assuming a failed commit rolled back.
    @SuppressLint("ApplySharedPref") // UI performs these writes on Dispatchers.IO and needs durability confirmation.
    private fun write(rows: List<IllustrationPageEntry>): Boolean {
        val serialized = serializeIllustrations(rows)
        if (serialized.length() == 0) return prefs.edit().remove(KEY).commit()
        val raw = serialized.toString()
        if (raw.length > MAX_SERIALIZED_CHARACTERS) return false
        return prefs.edit().putString(KEY, raw).commit()
    }

    companion object {
        internal const val KEY = "illustrationPages_v1"
        internal const val MAX_SERIALIZED_CHARACTERS = 30_000
    }
}

/** Whitelist each ID, region count and integer palette index before either loading or exporting. */
internal fun readIllustrationPages(raw: String?): List<IllustrationPageEntry> {
    if (raw == null || raw.length > IllustrationStore.MAX_SERIALIZED_CHARACTERS) return emptyList()
    return runCatching {
        val array = JSONArray(raw)
        if (array.length() > illustrationPages.size) return@runCatching emptyList()
        val seen = mutableSetOf<String>()
        (0 until array.length()).mapNotNull { index -> runCatching {
            val row = array.getJSONObject(index)
            val id = row.get("id") as? String ?: error("Invalid page ID")
            val template = illustrationPages.firstOrNull { it.id == id } ?: error("Unknown page")
            val values = row.getJSONArray("colors")
            require(values.length() == template.regions.size)
            val colors = (0 until values.length()).map { values.get(it) as? Int ?: error("Invalid color") }
            require(validIllustrationColors(colors, template.regions.size) && seen.add(id))
            if (colors.none { it != 0 }) null else IllustrationPageEntry(id, colors)
        }.getOrNull() }
    }.getOrDefault(emptyList())
}

private fun serializeIllustrations(rows: List<IllustrationPageEntry>): JSONArray = JSONArray().apply {
    illustrationPages.forEach { template ->
        rows.firstOrNull { it.id == template.id }?.let { entry ->
            put(JSONObject().put("id", entry.id).put("colors", JSONArray(entry.colors)))
        }
    }
}
