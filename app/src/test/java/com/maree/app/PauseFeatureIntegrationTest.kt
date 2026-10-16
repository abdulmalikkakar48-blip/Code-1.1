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
        val illustrations = IllustrationStore(prefs)
        val today = LocalDate.of(2026, 10, 16)
        val result = DiceResult(DiceMode.ACTIVITY, 1, today)
        val batch = diceTaskBatch(result, emptyList(), "", AppLocale.FRENCH, today)!!
        assertTrue(tasks.saveBatch(batch))
        val cells = List(COLORING_CELLS) { if (it == 13) 3 else 0 }
        assertTrue(art.savePage("mosaic", cells))
        val illustration = illustrationPages.first()
        val colors = List(illustration.regions.size) { if (it == 7) 16 else 0 }
        assertTrue(illustrations.savePage(illustration.id, colors))
        val prefsBeforeExport = prefs.all.toMap()

        val export = JSONObject(LocalDataExport.json(store))
        val data = export.getJSONObject("storedData")
        assertEquals(6, export.getInt("schemaVersion"))
        assertEquals(1, data.getJSONArray("calendarTasks").length())
        assertEquals(3, data.getJSONArray("coloringPages").getJSONObject(0).getJSONArray("cells").getInt(13))
        assertEquals(16, data.getJSONArray("illustrations").getJSONObject(0).getJSONArray("colors").getInt(7))
        for (key in listOf("points", "honorDice", "tracking", "membership", "standardChallenges", "customChallenges")) {
            assertEquals(before.get(key).toString(), data.get(key).toString())
        }
        assertEquals(prefsBeforeExport, prefs.all)
    }

    @Test
    fun globalErasureRemovesPauseFeaturesAndHonorReceiptAndRejectsPendingWriters() {
        val prefs = TestFeaturePreferences()
        val store = Store(prefs)
        val tasks = CalendarTaskStore(prefs)
        val art = ColoringStore(prefs)
        val illustrations = IllustrationStore(prefs)
        val cells = List(COLORING_CELLS) { 2 }
        val batch = listOf(LocalDate.of(2026, 10, 16) to "Pause")
        assertTrue(tasks.saveBatch(batch))
        assertTrue(art.savePage("waves", cells))
        val illustration = illustrationPages.first()
        val colors = List(illustration.regions.size) { 11 }
        assertTrue(illustrations.savePage(illustration.id, colors))
        assertNotNull(store.rollHonorDie("55555555-5555-4555-8555-555555555555"))
        assertTrue(store.clearPersonalData())
        assertFalse(tasks.saveBatch(batch))
        assertFalse(art.savePage("waves", cells))
        assertFalse(illustrations.savePage(illustration.id, colors))
        assertTrue(prefs.all.isEmpty())
        val exported = store.exportLocalState()
        assertEquals(0, exported.getJSONArray("calendarTasks").length())
        assertEquals(0, exported.getJSONArray("coloringPages").length())
        assertEquals(0, exported.getJSONArray("illustrations").length())
        assertTrue(exported.isNull("honorDice"))
        assertTrue(prefs.all.isEmpty())
    }
}
