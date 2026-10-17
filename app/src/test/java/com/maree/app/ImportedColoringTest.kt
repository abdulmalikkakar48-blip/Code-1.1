package com.maree.app

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.File
import java.util.zip.GZIPOutputStream
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class ImportedColoringTest {
    private fun asset(name: String) = listOf(File("src/main/assets/$name"), File("app/src/main/assets/$name")).first { it.isFile }
    @Test fun allFortyFiveMasksHaveMatchingDimensionsAndVisibleAnchors() {
        assertEquals((1..45).map { "imported_%03d".format(it) }, importedColoringPages.map { it.id })
        assertEquals(7571, importedColoringPages.sumOf { it.regions.size })
        importedColoringPages.forEach { page ->
            val mask = asset(page.maskAsset).inputStream().use { decodeImportedMask(it, page.width, page.height, page.regions.size) }
            page.regions.forEachIndexed { index, anchor -> assertEquals("${page.id}/$index", index, mask.regionAt(anchor.x, anchor.y)) }
            assertEquals(-1, mask.regionAt(-1, 0))
            assertTrue(asset(page.imageAsset).length() > 0)
        }
    }
    private fun binary(width: Int, height: Int, count: Int, labels: List<Int>, extra: Boolean = false): ByteArray {
        val bytes = ByteArrayOutputStream()
        DataOutputStream(GZIPOutputStream(bytes)).use { out ->
            out.writeInt(0x53434d31); out.writeInt(width); out.writeInt(height); out.writeInt(count)
            labels.forEach { out.writeShort(it) }; if (extra) out.writeByte(0)
        }; return bytes.toByteArray()
    }
    @Test fun malformedMasksCannotAllocateArbitrarySizesOrPaintUnknownRegions() {
        assertEquals(0, decodeImportedMask(ByteArrayInputStream(binary(2, 1, 1, listOf(1, 0))), 2, 1, 1).regionAt(0, 0))
        val corrupt = listOf(binary(200000, 2, 1, listOf(1)), binary(2, 1, 1, listOf(1)),
            binary(2, 1, 1, listOf(1, 2)), binary(2, 1, 2, listOf(1, 1)), binary(2, 1, 1, listOf(1, 0), true))
        corrupt.forEach { bytes -> assertTrue(runCatching { decodeImportedMask(ByteArrayInputStream(bytes), 2, 1, 1) }.isFailure) }
        assertTrue(runCatching { decodeImportedMask(ByteArrayInputStream(byteArrayOf()), 200000, 2, 1) }.isFailure)
    }
    @Test fun allPagesPersistExportAndEraseWithoutChangingPreviousArtworkOrPoints() {
        val prefs = TestFeaturePreferences()
        prefs.edit().putInt("pointUnits", 400).commit()
        val old = illustrationPages.first(); val oldColors = List(old.regions.size) { 12 }
        assertTrue(IllustrationStore(prefs).savePage(old.id, oldColors))
        val before = prefs.all.toMap(); val imported = ImportedColoringStore(prefs)
        importedColoringPages.asReversed().forEach { page -> assertTrue(imported.savePage(page.id, List(page.regions.size) { 16 })) }
        assertEquals(45, imported.pagesForExport().length())
        assertEquals(importedColoringPages.map { it.id }, (0 until 45).map { imported.pagesForExport().getJSONObject(it).getString("id") })
        assertTrue(prefs.getString(ImportedColoringStore.KEY, "")!!.length < ImportedColoringStore.MAX_CHARACTERS)
        importedColoringPages.forEach { page -> assertEquals(List(page.regions.size) { 16 }, ImportedColoringStore(prefs).page(page.id)) }
        assertEquals(before, prefs.all.filterKeys { it != ImportedColoringStore.KEY })
        val export = JSONObject(LocalDataExport.json(Store(prefs))).getJSONObject("storedData").getJSONArray("importedColoring")
        assertEquals(45, export.length()); assertEquals(setOf("id", "colors"), export.getJSONObject(0).keys().asSequence().toSet())
        assertTrue(Store(prefs).clearPersonalData()); assertTrue(prefs.all.isEmpty())
        assertFalse(imported.savePage(importedColoringPages.first().id, List(importedColoringPages.first().regions.size) { 2 }))
        assertFalse(imported.resetPage(importedColoringPages.first().id))
        assertEquals(oldColors.size, old.regions.size)
    }
    @Test fun invalidDataAndFailedCommitsAreFilteredAndRetryDoesNotDestroyOtherPages() {
        val prefs = TestFeaturePreferences(); val store = ImportedColoringStore(prefs)
        val page = importedColoringPages.first(); val colors = List(page.regions.size) { 2 }
        assertFalse(store.savePage("unknown", colors)); assertFalse(store.savePage(page.id, colors.dropLast(1)))
        assertFalse(store.savePage(page.id, colors.map { 17 }))
        assertTrue(store.savePage(page.id, colors)); prefs.commitSucceeds = false
        assertFalse(store.resetPage(page.id)); prefs.commitSucceeds = true
        assertEquals(colors, store.page(page.id)); assertTrue(store.resetPage(page.id))
        assertFalse(prefs.contains(ImportedColoringStore.KEY))
        val invalid = JSONArray().put(JSONObject().put("id", page.id).put("colors", JSONArray(colors).put(0, 1.5)))
        prefs.edit().putString(ImportedColoringStore.KEY, invalid.toString()).commit()
        assertEquals(0, store.pagesForExport().length())
        prefs.edit().putString(ImportedColoringStore.KEY, "x".repeat(ImportedColoringStore.MAX_CHARACTERS + 1)).commit()
        assertEquals(0, store.pagesForExport().length())
    }
}
