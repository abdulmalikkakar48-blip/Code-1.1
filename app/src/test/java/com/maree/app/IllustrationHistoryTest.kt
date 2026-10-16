package com.maree.app

import org.junit.Assert.*
import org.junit.Test

class IllustrationHistoryTest {
    @Test fun completeGestureUndoAndRedoPreserveRegionColorsAndPaletteSixteen() {
        val initial = List(57) { 0 }
        val stroke = List(57) { if (it in 4..9) 16 else 0 }
        val painted = IllustrationHistory(initial).paint(stroke)
        assertEquals(1, painted.previous.size)
        assertEquals(initial, painted.undo().cells)
        assertEquals(stroke, painted.undo().redo().cells)
        assertSame(painted, painted.redo())
    }

    @Test fun historyCopiesCallerListsAndRejectsDifferentRegionCountsOrInvalidColors() {
        val initial = MutableList(51) { 0 }
        val history = IllustrationHistory(initial)
        initial[0] = 2
        assertEquals(0, history.cells[0])
        val next = MutableList(51) { if (it == 7) 5 else 0 }
        val painted = history.paint(next)
        next[7] = 11
        assertEquals(5, painted.cells[7])
        assertSame(painted, painted.paint(painted.cells))
        assertSame(painted, painted.paint(List(50) { 0 }))
        assertSame(painted, painted.paint(List(52) { 0 }))
        assertSame(painted, painted.paint(List(51) { -1 }))
        assertSame(painted, painted.paint(List(51) { 17 }))
    }

    @Test fun paintingAfterUndoInvalidatesRedoWithoutChangingTheUndoneSnapshot() {
        val first = IllustrationHistory(List(38) { 0 }).paint(List(38) { if (it == 3) 2 else 0 })
        val second = first.paint(List(38) { if (it == 3) 2 else if (it == 5) 4 else 0 })
        val undo = second.undo()
        val branch = undo.paint(List(38) { if (it == 12) 9 else 0 })
        assertEquals(first.cells, undo.cells)
        assertEquals(1, undo.following.size)
        assertTrue(branch.following.isEmpty())
        assertSame(branch, branch.redo())
    }

    @Test fun fiftyUndoLimitKeepsTheNewestWorkAndRedoRestoresIt() {
        var history = IllustrationHistory(List(60) { 0 })
        repeat(70) { index -> history = history.paint(List(60) { if (it == index % 60) 1 + index % 16 else 0 }) }
        val latest = history.cells
        assertEquals(ILLUSTRATION_HISTORY_LIMIT, history.previous.size)
        repeat(70) { history = history.undo() }
        assertEquals(List(60) { if (it == 19) 4 else 0 }, history.cells)
        assertEquals(ILLUSTRATION_HISTORY_LIMIT, history.following.size)
        repeat(70) { history = history.redo() }
        assertEquals(latest, history.cells)
        assertTrue(history.following.isEmpty())
    }
}
