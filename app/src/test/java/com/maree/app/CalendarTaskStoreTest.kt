package com.maree.app

import android.content.SharedPreferences
import org.junit.Assert.*
import org.junit.Test
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.util.UUID

class CalendarTaskStoreTest {
    private val day = LocalDate.of(2027, 2, 28)
    @Test fun futureAndPastTasksSurviveReopeningAndStayOnTheirOwnDates() {
        val prefs = TestFeaturePreferences(); val store = CalendarTaskStore(prefs)
        assertTrue(store.save(day, "Lire sans écran"))
        assertTrue(store.save(day.minusYears(2), "Réviser"))
        assertEquals(listOf(day, day.minusYears(2)), CalendarTaskStore(prefs).entries().map { it.date })
        assertFalse(store.entries().first().completed)
    }
    @Test fun editAndCompletionDoNotCreateDuplicateTasksOrChangeRewards() {
        val prefs = TestFeaturePreferences(); prefs.edit().putInt("pointUnits", 400).commit()
        val store = CalendarTaskStore(prefs); assertTrue(store.save(day, "Lire"))
        val id = store.entries().single().id
        assertTrue(store.setCompleted(id, true)); assertTrue(store.save(day, "Lire 10 pages", id))
        assertEquals(CalendarTask(id, day, "Lire 10 pages", true), store.entries().single())
        assertEquals(400, prefs.getInt("pointUnits", 0))
        assertTrue(store.setCompleted(id, false)); assertTrue(store.delete(id))
        assertTrue(store.entries().isEmpty()); assertFalse(store.setCompleted(id, true))
    }
    @Test fun rejectsEmptyTooLongAndUnsupportedDatesWithoutWriting() {
        val prefs = TestFeaturePreferences(); val store = CalendarTaskStore(prefs)
        assertFalse(store.save(day, "   ")); assertFalse(store.save(day, "a".repeat(241)))
        assertFalse(store.save(day.withYear(2201), "Lire")); assertFalse(store.save(day, "Lire", UUID.randomUUID().toString()))
        assertFalse(prefs.contains("calendarTasks_v1"))
    }
    @Test fun malformedRowsAndUnexpectedFieldsAreExcludedFromExport() {
        val prefs = TestFeaturePreferences(); val id = UUID.randomUUID().toString()
        prefs.edit().putString("calendarTasks_v1", JSONArray().put(JSONObject().put("id", id).put("date", day.toString()).put("title", "Lire").put("completed", false).put("secret", "private-extra"))
            .put(JSONObject().put("id", id).put("date", day.toString()).put("title", "duplicate").put("completed", false))
            .put(JSONObject().put("id", UUID.randomUUID().toString()).put("date", "bad").put("title", "bad").put("completed", false)).toString()).commit()
        val export = CalendarTaskStore(prefs).exportJson()
        assertEquals(1, export.length()); assertFalse(export.toString().contains("private-extra"))
        assertEquals(setOf("id", "date", "title", "completed"), export.getJSONObject(0).keys().asSequence().toSet())
    }
    @Test fun globalErasureIncludesTasksAndInvalidatesPendingWriter() {
        val prefs = TestFeaturePreferences(); val core = Store(prefs); val tasks = CalendarTaskStore(prefs)
        assertTrue(tasks.save(day, "Lire")); assertEquals(1, core.exportLocalState().getJSONArray("calendarTasks").length())
        assertTrue(core.clearPersonalData()); assertTrue(tasks.entries().isEmpty())
        assertFalse(tasks.save(day, "Late write")); assertFalse(prefs.contains("calendarTasks_v1"))
        assertTrue(CalendarTaskStore(prefs).save(day, "New session"))
    }
    @Test fun capacityDoesNotPreventEditingOrDeletingExistingTask() {
        val prefs = TestFeaturePreferences(); val rows = JSONArray()
        repeat(500) { rows.put(JSONObject().put("id", UUID.randomUUID().toString()).put("date", day.toString()).put("title", "Task $it").put("completed", false)) }
        prefs.edit().putString("calendarTasks_v1", rows.toString()).commit(); val store = CalendarTaskStore(prefs)
        assertFalse(store.save(day, "Overflow")); val id = store.entries().first().id
        assertTrue(store.save(day, "Edited", id)); assertTrue(store.delete(id)); assertTrue(store.save(day, "Replacement"))
        assertEquals(500, store.entries().size)
    }
    @Test fun failedCommitIsReportedAndWrongPreferenceTypeDoesNotCrash() {
        val prefs = TestFeaturePreferences(); val store = CalendarTaskStore(prefs)
        prefs.commitSucceeds = false
        assertFalse(store.save(day, "Read")); assertTrue(store.entries().isEmpty())
        prefs.commitSucceeds = true; prefs.edit().putInt("calendarTasks_v1", 123).commit()
        assertTrue(store.entries().isEmpty()); assertEquals(0, store.exportJson().length())
    }

    @Test fun diceBatchWritesEveryDateTogetherWithoutChangingPointsLimitsOrLocks() {
        val prefs = TestFeaturePreferences()
        prefs.edit().putInt("pointUnits", 400).putString("apps", "keep-existing-limits-and-locks").commit()
        val store = CalendarTaskStore(prefs)
        assertTrue(store.saveBatch(listOf(day to "First", day.plusDays(2) to "Second", day.plusDays(6) to "Third")))
        assertEquals(listOf(day, day.plusDays(2), day.plusDays(6)), store.entries().map { it.date })
        assertTrue(store.entries().none { it.completed })
        assertEquals(3, store.entries().map { it.id }.distinct().size)
        assertEquals(400, prefs.getInt("pointUnits", 0))
        assertEquals("keep-existing-limits-and-locks", prefs.getString("apps", null))
    }

    @Test fun diceBatchCapacityAndInvalidInputNeverSavePartialGoals() {
        val prefs = TestFeaturePreferences(); val store = CalendarTaskStore(prefs)
        assertFalse(store.saveBatch(emptyList()))
        assertFalse(store.saveBatch(listOf(day to "Valid", day.plusDays(1) to " ")))
        assertFalse(store.saveBatch(listOf(day to "First", day to "Duplicate day")))
        assertFalse(store.saveBatch((0L..3L).map { day.plusDays(it) to "Too many" }))
        assertTrue(store.entries().isEmpty())
        val rows = JSONArray()
        repeat(499) { rows.put(JSONObject().put("id", UUID.randomUUID().toString()).put("date", day.toString()).put("title", "Task $it").put("completed", false)) }
        prefs.edit().putString("calendarTasks_v1", rows.toString()).commit()
        val before = prefs.getString("calendarTasks_v1", null)
        assertFalse(store.saveBatch(listOf(day to "First", day.plusDays(1) to "Second")))
        assertEquals(before, prefs.getString("calendarTasks_v1", null))
        val lastOperation = UUID.randomUUID().toString()
        assertTrue(store.saveBatch(listOf(day to "Last slot"), lastOperation))
        assertEquals(500, store.entries().size)
        assertTrue(store.saveBatch(listOf(day to "Last slot"), lastOperation))
        assertEquals(500, store.entries().size)
    }

    @Test fun diceBatchCommitFailureAndErasureInvalidateTheWholePendingBatch() {
        val prefs = TestFeaturePreferences(); val core = Store(prefs); val pending = CalendarTaskStore(prefs)
        prefs.commitSucceeds = false
        assertFalse(pending.saveBatch(listOf(day to "First", day.plusDays(1) to "Second")))
        assertTrue(pending.entries().isEmpty())
        prefs.commitSucceeds = true
        assertTrue(core.clearPersonalData())
        assertFalse(pending.saveBatch(listOf(day to "Late batch")))
        assertFalse(prefs.contains("calendarTasks_v1"))
        assertTrue(CalendarTaskStore(prefs).saveBatch(listOf(day to "New session")))
    }

    @Test fun diceBatchRetryUsesStableIdsWhenFailedCommitAlreadyUpdatedMemory() {
        val memory = TestFeaturePreferences()
        var failNextCommit = true
        val prefs = object : SharedPreferences by memory {
            override fun edit(): SharedPreferences.Editor {
                val editor = memory.edit()
                return object : SharedPreferences.Editor by editor {
                    override fun putString(key: String, value: String?): SharedPreferences.Editor = apply { editor.putString(key, value) }
                    override fun commit(): Boolean {
                        val persisted = editor.commit() // Android updates the memory map even if disk persistence fails.
                        return if (failNextCommit) { failNextCommit = false; false } else persisted
                    }
                }
            }
        }
        val store = CalendarTaskStore(prefs)
        val tasks = listOf(day to "First", day.plusDays(1) to "Second")
        val operation = DiceResult(DiceMode.WEEK, 3, day, "app.one").id
        assertFalse(store.saveBatch(tasks, operation))
        val pendingIds = store.entries().map { it.id }
        assertEquals(2, pendingIds.size)
        assertTrue(store.saveBatch(tasks, operation))
        assertEquals(pendingIds, CalendarTaskStore(prefs).entries().map { it.id })
        assertTrue(store.saveBatch(tasks, DiceResult(DiceMode.WEEK, 3, day, "app.one").id))
        assertEquals(4, store.entries().map { it.id }.distinct().size)
    }

    @Test fun diceBatchRejectsChangedOperationContentAndPreservesCompletedRetry() {
        val prefs = TestFeaturePreferences(); val store = CalendarTaskStore(prefs)
        val operation = UUID.randomUUID().toString()
        val tasks = listOf(day to "Read", day.plusDays(1) to "Read")
        assertTrue(store.saveBatch(tasks, operation))
        assertTrue(store.setCompleted(store.entries().first().id, true))
        assertTrue(store.saveBatch(tasks, operation))
        assertTrue(store.entries().first().completed)
        val before = prefs.getString("calendarTasks_v1", null)
        assertFalse(store.saveBatch(listOf(day to "Changed", day.plusDays(1) to "Read"), operation))
        assertFalse(store.saveBatch(listOf(day to "Read"), operation))
        assertFalse(store.saveBatch(tasks + (day.plusDays(2) to "Extra"), operation))
        assertFalse(store.saveBatch(tasks, "invalid-operation"))
        assertEquals(before, prefs.getString("calendarTasks_v1", null))
    }

}
