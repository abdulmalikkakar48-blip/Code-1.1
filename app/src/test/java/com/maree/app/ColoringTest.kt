package com.maree.app

import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class ColoringTest {
    private fun painted(index: Int, color: Int) = List(COLORING_CELLS) { if (it == index) color else 0 }
    private fun row(id: String, cells: List<Int>) = JSONObject().put("id", id).put("cells", JSONArray(cells))

    @Test fun pagesSurviveReopeningAndRemainIndependentWithoutChangingPoints() {
        val prefs = TestFeaturePreferences()
        prefs.edit().putInt("pointUnits", 400).commit()
        val store = ColoringStore(prefs)
        assertTrue(store.savePage("mosaic", painted(5, 3)))
        assertTrue(store.savePage("waves", painted(100, 10)))
        val reopened = ColoringStore(prefs)
        assertEquals(painted(5, 3), reopened.loadPage("mosaic"))
        assertEquals(painted(100, 10), reopened.loadPage("waves"))
        assertEquals(List(COLORING_CELLS) { 0 }, reopened.loadPage("sun"))
        assertEquals(400, prefs.getInt("pointUnits", 0))
    }

    @Test fun invalidGridLengthsPaletteIndicesAndUnknownPagesNeverWrite() {
        val prefs = TestFeaturePreferences()
        val store = ColoringStore(prefs)
        assertFalse(store.savePage("unknown", painted(0, 1)))
        assertFalse(store.resetPage("unknown"))
        assertFalse(store.savePage("mosaic", List(143) { 1 }))
        assertFalse(store.savePage("mosaic", List(145) { 1 }))
        assertFalse(store.savePage("mosaic", painted(0, -1)))
        assertFalse(store.savePage("mosaic", painted(0, 11)))
        assertFalse(prefs.contains(ColoringStore.KEY))
    }

    @Test fun exportOnlyContainsKnownPagesAndSanitizedIntegerCells() {
        val prefs = TestFeaturePreferences()
        val valid = row("waves", painted(4, 2)).put("privateExtra", "do-not-export")
        val fractional = JSONArray(List(COLORING_CELLS) { 0 }).put(1, 1.5)
        val rows = JSONArray().put(valid).put(row("waves", painted(5, 3)))
            .put(row("unknown", painted(0, 1)))
            .put(JSONObject().put("id", "sun").put("cells", fractional))
            .put(row("blank", painted(0, 11)))
        prefs.edit().putString(ColoringStore.KEY, rows.toString()).commit()
        val export = ColoringStore(prefs).exportJson()
        assertEquals(1, export.length())
        assertEquals(setOf("id", "cells"), export.getJSONObject(0).keys().asSequence().toSet())
        assertFalse(export.toString().contains("do-not-export"))
        assertEquals("waves", export.getJSONObject(0).getString("id"))
        assertEquals(2, export.getJSONObject(0).getJSONArray("cells").getInt(4))
    }

    @Test fun wrongPreferenceTypesOversizedAndMalformedDataDoNotCrashOrExport() {
        val prefs = TestFeaturePreferences()
        prefs.edit().putInt(ColoringStore.KEY, 3).commit()
        assertTrue(ColoringStore(prefs).entries().isEmpty())
        listOf("bad JSON", "x".repeat(20_001), JSONArray(List(9) { row("mosaic", painted(0, 1)) }).toString()).forEach { raw ->
            prefs.edit().putString(ColoringStore.KEY, raw).commit()
            assertEquals(0, ColoringStore(prefs).exportJson().length())
        }
    }

    @Test fun failedSaveAndResetReportFailureAndKeepPreviouslySavedData() {
        val prefs = TestFeaturePreferences()
        val store = ColoringStore(prefs)
        assertTrue(store.savePage("mosaic", painted(2, 1)))
        prefs.commitSucceeds = false
        assertFalse(store.savePage("mosaic", painted(3, 2)))
        assertFalse(store.resetPage("mosaic"))
        assertEquals(painted(2, 1), store.loadPage("mosaic"))
    }

    @Test fun resetCanBeRetriedWhenFailedCommitAlreadyChangedMemory() {
        val backing = TestFeaturePreferences()
        var reportFailure = false
        val prefs = object : SharedPreferences by backing {
            override fun edit(): SharedPreferences.Editor {
                val editor = backing.edit()
                return object : SharedPreferences.Editor by editor {
                    override fun putString(key: String, value: String?): SharedPreferences.Editor = apply { editor.putString(key, value) }
                    override fun remove(key: String): SharedPreferences.Editor = apply { editor.remove(key) }
                    override fun commit(): Boolean {
                        val committed = editor.commit()
                        return committed && !reportFailure
                    }
                }
            }
        }
        val store = ColoringStore(prefs)
        assertTrue(store.savePage("mosaic", painted(2, 1)))
        assertTrue(store.savePage("sun", painted(4, 5)))
        reportFailure = true
        assertFalse(store.resetPage("mosaic"))
        // Android may expose the changed memory even though durability was not confirmed.
        assertEquals(List(COLORING_CELLS) { 0 }, store.loadPage("mosaic"))
        reportFailure = false
        assertTrue(store.resetPage("mosaic"))
        assertEquals(listOf("sun"), store.entries().map { it.id })
        assertEquals(painted(4, 5), store.loadPage("sun"))
    }

    @Test fun globalEraseInvalidatesOldWriterAndNewSessionCanSave() {
        val prefs = TestFeaturePreferences()
        val store = ColoringStore(prefs)
        assertTrue(store.savePage("sun", painted(20, 3)))
        assertTrue(Store(prefs).clearPersonalData())
        assertFalse(store.savePage("sun", painted(20, 3)))
        assertFalse(store.resetPage("sun"))
        assertFalse(prefs.contains(ColoringStore.KEY))
        assertTrue(ColoringStore(prefs).savePage("sun", painted(1, 4)))
    }

    @Test fun resettingOnePagePreservesOthersAndBlankPagesNeedNoStorage() {
        val prefs = TestFeaturePreferences()
        val store = ColoringStore(prefs)
        assertEquals(0, store.exportJson().length())
        assertFalse(prefs.contains(ColoringStore.KEY))
        assertTrue(store.savePage("mosaic", painted(0, 1)))
        assertTrue(store.savePage("sun", painted(1, 2)))
        assertTrue(store.resetPage("mosaic"))
        assertEquals(painted(1, 2), store.loadPage("sun"))
        assertEquals(List(COLORING_CELLS) { 0 }, store.loadPage("mosaic"))
        assertTrue(store.savePage("sun", List(COLORING_CELLS) { 0 }))
        assertFalse(prefs.contains(ColoringStore.KEY))
    }

    @Test fun gestureHistoryRestoresWholeStrokeAndNewPaintingInvalidatesRedo() {
        assertEquals((0 until COLORING_SIDE).toList(), coloringSegment(0, COLORING_SIDE - 1))
        assertEquals((0 until COLORING_SIDE).map { it * (COLORING_SIDE + 1) }, coloringSegment(0, COLORING_CELLS - 1))
        assertTrue(coloringSegment(-1, 3).isEmpty())
        val empty = ColoringHistory()
        val stroke = List(COLORING_CELLS) { if (it in 0..8) 3 else 0 }
        val first = empty.paint(stroke)
        assertEquals(1, first.previous.size)
        assertEquals(empty.cells, first.undo().cells)
        assertEquals(stroke, first.undo().redo().cells)
        val branch = first.undo().paint(painted(50, 8))
        assertTrue(branch.following.isEmpty())
        assertEquals(painted(50, 8), branch.redo().cells)
        assertSame(first, first.paint(stroke))
        assertSame(first, first.paint(listOf(1)))
    }

    @Test fun historyCapsOldSnapshotsWithoutDroppingCurrentWork() {
        var history = ColoringHistory()
        repeat(70) { index -> history = history.paint(painted(index, 1 + index % COLORING_COLOR_COUNT)) }
        assertEquals(COLORING_HISTORY_LIMIT, history.previous.size)
        assertEquals(painted(69, 10), history.cells)
        repeat(70) { history = history.undo() }
        assertEquals(painted(19, 10), history.cells)
        assertEquals(COLORING_HISTORY_LIMIT, history.following.size)
        repeat(70) { history = history.redo() }
        assertEquals(painted(69, 10), history.cells)
    }
}
