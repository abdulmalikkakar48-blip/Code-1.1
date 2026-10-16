package com.maree.app

import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class PauseFeatureIntegrationTest {
    @Test
    fun confirmedGoalsAndDrawingsAreExportedWithoutChangingPointsLimitsOrPlan() {
        val prefs = TestFeaturePreferences()
        val store = Store(prefs)
        assertNull(store.addApp("com.example.reader", 30, 0L))
        val before = store.exportLocalState()
        val tasks = CalendarTaskStore(prefs)
        val art = ColoringStore(prefs)
        val today = LocalDate.of(2026, 10, 16)
        val result = DiceResult(DiceMode.ACTIVITY, 1, today)
        val batch = diceTaskBatch(result, emptyList(), "", AppLocale.FRENCH, today)!!
        assertTrue(tasks.saveBatch(batch))
        val cells = List(COLORING_CELLS) { if (it == 13) 3 else 0 }
        assertTrue(art.savePage("mosaic", cells))
        val prefsBeforeExport = prefs.all.toMap()

        val export = JSONObject(LocalDataExport.json(store))
        val data = export.getJSONObject("storedData")
        assertEquals(4, export.getInt("schemaVersion"))
        assertEquals(1, data.getJSONArray("calendarTasks").length())
        assertEquals(3, data.getJSONArray("coloringPages").getJSONObject(0).getJSONArray("cells").getInt(13))
        for (key in listOf("points", "tracking", "membership", "standardChallenges", "customChallenges")) {
            assertEquals(before.get(key).toString(), data.get(key).toString())
        }
        assertEquals(prefsBeforeExport, prefs.all)
    }

    @Test
    fun globalErasureRemovesBothFeaturesAndRejectsPendingWriters() {
        val prefs = TestFeaturePreferences()
        val store = Store(prefs)
        val tasks = CalendarTaskStore(prefs)
        val art = ColoringStore(prefs)
        val cells = List(COLORING_CELLS) { 2 }
        val batch = listOf(LocalDate.of(2026, 10, 16) to "Pause")
        assertTrue(tasks.saveBatch(batch))
        assertTrue(art.savePage("waves", cells))
        assertTrue(store.clearPersonalData())
        assertFalse(tasks.saveBatch(batch))
        assertFalse(art.savePage("waves", cells))
        assertTrue(prefs.all.isEmpty())
        val exported = store.exportLocalState()
        assertEquals(0, exported.getJSONArray("calendarTasks").length())
        assertEquals(0, exported.getJSONArray("coloringPages").length())
        assertTrue(prefs.all.isEmpty())
    }
}
