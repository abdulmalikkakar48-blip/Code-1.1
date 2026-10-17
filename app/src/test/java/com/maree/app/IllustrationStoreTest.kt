package com.maree.app

import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class IllustrationStoreTest {
    private val first get() = illustrationPages.first()
    private val second get() = illustrationPages[1]
    private fun colors(id: String, color: Int = 0, index: Int = 0): List<Int> =
        List(illustrationPages.first { it.id == id }.regions.size) { if (it == index) color else 0 }
    private fun row(id: String, values: List<Int>) = JSONObject().put("id", id).put("colors", JSONArray(values))

    @Test fun fourDrawingsSurviveReopeningWithoutTouchingPixelArtOrHonorPoints() {
        val prefs = TestFeaturePreferences()
        prefs.edit().putInt("pointUnits", 173).putString("apps", "[]").commit()
        val pixels = List(COLORING_CELLS) { if (it == 10) 7 else 0 }
        assertTrue(ColoringStore(prefs).savePage("flower", pixels))
        val before = prefs.all.toMap()
        val store = IllustrationStore(prefs)
        illustrationPages.take(4).forEachIndexed { index, template ->
            assertTrue(store.savePage(template.id, colors(template.id, 13 + index, template.regions.lastIndex)))
        }

        val reopened = IllustrationStore(prefs)
        illustrationPages.take(4).forEachIndexed { index, template ->
            assertEquals(colors(template.id, 13 + index, template.regions.lastIndex), reopened.page(template.id))
        }
        assertEquals(before, prefs.all.filterKeys { it != IllustrationStore.KEY })
        assertEquals(pixels, ColoringStore(prefs).loadPage("flower"))
        assertEquals(4, reopened.pagesForExport().length())
    }

    @Test fun addingLandscapesPreservesLegacyFlowerRowsAndGridColors() {
        val prefs = TestFeaturePreferences()
        // The original four pages have fixed lengths in the released 0.20.0 format.
        val legacy = listOf("rose_garden" to 48, "wild_bouquet" to 71,
            "spring_tulips" to 47, "botanical_leaves" to 55)
        val rows = JSONArray()
        legacy.forEachIndexed { index, (id, length) ->
            rows.put(row(id, List(length) { if (it % 3 == 0) index + 1 else 16 }))
        }
        val pixels = List(COLORING_CELLS) { if (it % 5 == 0) 4 else 0 }
        assertTrue(ColoringStore(prefs).savePage("landscape", pixels))
        prefs.edit().putString(IllustrationStore.KEY, rows.toString()).putInt("pointUnits", 400).commit()
        val before = prefs.all.toMap()
        val store = IllustrationStore(prefs)
        legacy.forEachIndexed { index, (id, length) ->
            assertEquals(List(length) { if (it % 3 == 0) index + 1 else 16 }, store.page(id))
        }
        assertEquals(before, prefs.all) // Reading the expanded catalog needs no migration write.
        val landscape = illustrationPages.first { it.id == "waterfall_valley" }
        assertEquals(List(landscape.regions.size) { 0 }, store.page(landscape.id))
        assertTrue(store.savePage(landscape.id, colors(landscape.id, 10, landscape.regions.lastIndex)))
        assertEquals(5, store.pagesForExport().length())
        assertTrue(store.resetPage(landscape.id))
        assertEquals(rows.toString(), store.pagesForExport().toString())
        assertEquals(before, prefs.all)
        assertEquals(pixels, ColoringStore(prefs).loadPage("landscape"))
    }

    @Test fun twentyPageCatalogReadsAllEightReleasedDrawingsWithoutRewritingTheirColors() {
        val prefs = TestFeaturePreferences()
        val released = listOf("rose_garden" to 48, "wild_bouquet" to 71,
            "spring_tulips" to 47, "botanical_leaves" to 55, "waterfall_valley" to 67,
            "alpine_lake" to 77, "coastal_garden" to 67, "patterned_tree" to 193)
        val legacy = JSONArray()
        released.forEachIndexed { index, (id, count) ->
            legacy.put(row(id, List(count) { if (it % 5 == 0) index + 1 else 0 }))
        }
        prefs.edit().putString(IllustrationStore.KEY, legacy.toString()).putInt("pointUnits", 400).commit()
        val before = prefs.all.toMap()
        val store = IllustrationStore(prefs)
        released.forEachIndexed { index, (id, count) ->
            assertEquals(List(count) { if (it % 5 == 0) index + 1 else 0 }, store.page(id))
        }
        assertEquals(before, prefs.all)
        val added = illustrationPages.first { it.id == "patterned_mountains" }
        assertEquals(List(added.regions.size) { 0 }, store.page(added.id))
        assertTrue(store.savePage(added.id, List(added.regions.size) { 14 }))
        val reopened = IllustrationStore(prefs)
        assertEquals(9, reopened.pagesForExport().length())
        assertEquals(List(added.regions.size) { 14 }, reopened.page(added.id))
        assertTrue(reopened.resetPage(added.id))
        assertEquals(legacy.toString(), reopened.pagesForExport().toString())
        assertEquals(before, prefs.all)
    }

    @Test fun previewsAndUnknownPagesNeverCreateStoredUserColors() {
        val prefs = TestFeaturePreferences()
        val store = IllustrationStore(prefs)
        illustrationPages.forEach { template -> assertEquals(List(template.regions.size) { 0 }, store.page(template.id)) }
        assertTrue(store.page("unknown").isEmpty())
        assertEquals(0, store.pagesForExport().length())
        assertTrue(prefs.all.isEmpty())
    }

    @Test fun invalidPageCountsAndPaletteValuesNeverWriteOrResetExistingArt() {
        val prefs = TestFeaturePreferences()
        val store = IllustrationStore(prefs)
        assertTrue(store.savePage(first.id, colors(first.id, 3)))
        val before = prefs.all.toMap()
        assertFalse(store.savePage("unknown", colors(first.id, 1)))
        assertFalse(store.resetPage("unknown"))
        assertFalse(store.savePage(first.id, colors(first.id).dropLast(1)))
        assertFalse(store.savePage(first.id, colors(first.id) + 0))
        assertFalse(store.savePage(first.id, colors(first.id, -1)))
        assertFalse(store.savePage(first.id, colors(first.id, 17)))
        assertEquals(before, prefs.all)
    }

    @Test fun filteredExportRejectsPrivateFieldsDuplicatePagesAndFractionalPaletteValues() {
        val prefs = TestFeaturePreferences()
        val fractional = JSONArray(colors(second.id)).put(0, 1.5)
        prefs.edit().putString(IllustrationStore.KEY, JSONArray()
            .put(row(first.id, colors(first.id, 16)).put("privateExtra", "synthetic-private-illustration-value"))
            .put(row(first.id, colors(first.id, 5)))
            .put(JSONObject().put("id", second.id).put("colors", fractional))
            .put(row("unknown", listOf(1))).toString()).commit()
        val before = prefs.all.toMap()

        val exported = IllustrationStore(prefs).pagesForExport()

        assertEquals(1, exported.length())
        assertEquals(setOf("id", "colors"), exported.getJSONObject(0).keys().asSequence().toSet())
        assertEquals(first.id, exported.getJSONObject(0).getString("id"))
        assertEquals(16, exported.getJSONObject(0).getJSONArray("colors").getInt(0))
        assertFalse(exported.toString().contains("synthetic-private-illustration-value"))
        assertEquals(before, prefs.all)
    }

    @Test fun malformedOversizedWronglyTypedAndExcessiveRowsNeverCrashOrExport() {
        val prefs = TestFeaturePreferences()
        prefs.edit().putInt(IllustrationStore.KEY, 3).commit()
        assertEquals(0, IllustrationStore(prefs).pagesForExport().length())
        val invalidRows = listOf(
            "invalid JSON", "{}", "null", "x".repeat(IllustrationStore.MAX_SERIALIZED_CHARACTERS + 1),
            JSONArray(List(illustrationPages.size + 1) { row(first.id, colors(first.id, 1)) }).toString(),
            JSONArray().put(row(first.id, colors(first.id).dropLast(1))).toString(),
            JSONArray().put(JSONObject().put("id", first.id).put("colors", JSONArray(colors(first.id)).put(0, "1"))).toString(),
            JSONArray().put(JSONObject().put("id", first.id).put("colors", JSONArray(colors(first.id)).put(0, true))).toString(),
            JSONArray().put(row(first.id, colors(first.id, 17))).toString()
        )
        invalidRows.forEach { raw ->
            prefs.edit().putString(IllustrationStore.KEY, raw).commit()
            assertEquals(0, IllustrationStore(prefs).pagesForExport().length())
            assertEquals(colors(first.id), IllustrationStore(prefs).page(first.id))
        }
    }

    @Test fun resetOnlyAffectsSelectedDrawingAndAnEmptyPageRemovesItsStorage() {
        val prefs = TestFeaturePreferences()
        val store = IllustrationStore(prefs)
        assertTrue(store.savePage(first.id, colors(first.id, 2)))
        assertTrue(store.savePage(second.id, colors(second.id, 16)))
        assertTrue(store.resetPage(first.id))
        assertEquals(colors(first.id), store.page(first.id))
        assertEquals(colors(second.id, 16), store.page(second.id))
        assertEquals(1, store.pagesForExport().length())
        assertTrue(store.savePage(second.id, colors(second.id)))
        assertFalse(prefs.contains(IllustrationStore.KEY))
    }

    @Test fun failedWritesReportFailureAndCanBeRetriedWithOriginalInMemoryPreferences() {
        val prefs = TestFeaturePreferences()
        val store = IllustrationStore(prefs)
        assertTrue(store.savePage(first.id, colors(first.id, 2)))
        prefs.commitSucceeds = false
        assertFalse(store.savePage(first.id, colors(first.id, 3)))
        assertFalse(store.resetPage(first.id))
        assertEquals(colors(first.id, 2), store.page(first.id))
        prefs.commitSucceeds = true
        assertTrue(store.savePage(first.id, colors(first.id, 3)))
        assertEquals(colors(first.id, 3), store.page(first.id))
    }

    @Test fun failedCommitThatAlreadyChangedMemoryCanRetrySaveAndResetWithoutLosingOtherArt() {
        val backing = TestFeaturePreferences()
        var reportFailure = false
        val prefs = object : SharedPreferences by backing {
            override fun edit(): SharedPreferences.Editor {
                val editor = backing.edit()
                return object : SharedPreferences.Editor by editor {
                    override fun putString(key: String, value: String?): SharedPreferences.Editor = apply { editor.putString(key, value) }
                    override fun remove(key: String): SharedPreferences.Editor = apply { editor.remove(key) }
                    override fun commit(): Boolean = editor.commit() && !reportFailure
                }
            }
        }
        val store = IllustrationStore(prefs)
        assertTrue(store.savePage(second.id, colors(second.id, 16)))
        reportFailure = true
        assertFalse(store.savePage(first.id, colors(first.id, 4)))
        assertEquals(colors(first.id, 4), store.page(first.id))
        reportFailure = false
        assertTrue(store.savePage(first.id, colors(first.id, 4)))
        reportFailure = true
        assertFalse(store.resetPage(first.id))
        assertEquals(colors(first.id), store.page(first.id))
        reportFailure = false
        assertTrue(store.resetPage(first.id))
        assertEquals(colors(second.id, 16), store.page(second.id))
        assertEquals(1, store.pagesForExport().length())
    }

    @Test fun fullGlobalEraseInvalidatesQueuedSavesAndResetsWhileFreshEditorCanSave() {
        val prefs = TestFeaturePreferences()
        val old = IllustrationStore(prefs)
        assertTrue(old.savePage(first.id, colors(first.id, 6)))
        assertTrue(Store(prefs).clearPersonalData())
        assertFalse(old.savePage(first.id, colors(first.id, 6)))
        assertFalse(old.resetPage(first.id))
        assertEquals(colors(first.id), old.page(first.id))
        assertTrue(prefs.all.isEmpty())
        assertTrue(IllustrationStore(prefs).savePage(second.id, colors(second.id, 5)))
    }

    @Test fun everyFullyColoredPageFitsTheBoundedStoreAndExportsInCatalogOrder() {
        val prefs = TestFeaturePreferences()
        val store = IllustrationStore(prefs)
        illustrationPages.asReversed().forEach { template ->
            assertTrue(store.savePage(template.id, List(template.regions.size) { 16 }))
        }
        assertTrue(prefs.getString(IllustrationStore.KEY, "")!!.length < IllustrationStore.MAX_SERIALIZED_CHARACTERS)
        val rows = store.pagesForExport()
        assertEquals(illustrationPages.map { it.id }, (0 until rows.length()).map { rows.getJSONObject(it).getString("id") })
        assertEquals(illustrationPages.map { it.regions.size }, (0 until rows.length()).map { rows.getJSONObject(it).getJSONArray("colors").length() })
    }
}
