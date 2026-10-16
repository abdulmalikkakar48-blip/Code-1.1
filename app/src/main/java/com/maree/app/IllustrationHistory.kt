package com.maree.app

internal const val ILLUSTRATION_COLOR_COUNT = 16
internal const val ILLUSTRATION_HISTORY_LIMIT = 50

internal fun validIllustrationColors(colors: List<Int>, regionCount: Int): Boolean =
    regionCount > 0 && colors.size == regionCount && colors.all { it in 0..ILLUSTRATION_COLOR_COUNT }

/** A tap or drag is one operation. History stays in this editing session, never in preferences. */
internal class IllustrationHistory private constructor(
    val cells: List<Int>,
    val previous: List<List<Int>>,
    val following: List<List<Int>>
) {
    constructor(cells: List<Int>) : this(cells.toList(), emptyList(), emptyList()) {
        require(validIllustrationColors(cells, cells.size))
    }

    fun paint(next: List<Int>): IllustrationHistory {
        if (!validIllustrationColors(next, cells.size) || next == cells) return this
        return IllustrationHistory(next.toList(), (previous + listOf(cells)).takeLast(ILLUSTRATION_HISTORY_LIMIT), emptyList())
    }

    fun undo(): IllustrationHistory = if (previous.isEmpty()) this else
        IllustrationHistory(previous.last(), previous.dropLast(1), (following + listOf(cells)).takeLast(ILLUSTRATION_HISTORY_LIMIT))

    fun redo(): IllustrationHistory = if (following.isEmpty()) this else
        IllustrationHistory(following.last(), (previous + listOf(cells)).takeLast(ILLUSTRATION_HISTORY_LIMIT), following.dropLast(1))
}
