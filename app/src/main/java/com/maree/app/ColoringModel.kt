package com.maree.app

import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin

internal const val COLORING_SIDE = 12
internal const val COLORING_CELLS = COLORING_SIDE * COLORING_SIDE
internal const val COLORING_COLOR_COUNT = 10
internal const val COLORING_HISTORY_LIMIT = 50

/** Original line patterns and pixel illustrations. Every square stays independently colorable. */
internal data class ColoringTemplate(val id: String, val french: String, val english: String) {
    fun name(): String = tr(french, english)
    val isIllustration: Boolean get() = id in coloringIllustrationPatterns
    fun region(row: Int, column: Int): Int {
        coloringIllustrationPatterns[id]?.let { return it[row][column].digitToInt() }
        val x = column - 5.5
        val y = row - 5.5
        return when (id) {
            "mosaic" -> (row / 3) * 4 + column / 3
            "checker" -> (row / 2) * 6 + column / 2
            "waves" -> floor((row + sin(column * 0.65) * 1.5) / 2).toInt()
            "rosette" -> if (hypot(x, y) < 2.5) 0 else 1 + floor((atan2(y, x) + Math.PI) / (Math.PI / 4)).toInt()
            "diamond" -> ((abs(x) + abs(y)) / 2).toInt()
            "mountain" -> if (row < 9 - abs(column - 5)) 0 else if (row < 10) 1 else 2
            "sun" -> if (hypot(x, y) < 3.3) 0 else 1 + (floor((atan2(y, x) + Math.PI) / (Math.PI / 6)).toInt() % 12)
            else -> row * COLORING_SIDE + column
        }
    }

    /** Example colors are only used by the gallery, never written to a user's page. */
    fun previewColor(row: Int, column: Int): Int {
        if (id == "blank") return 0
        val region = region(row, column)
        val illustrationColors = when (id) {
            "tree" -> listOf(0, 3, 1, 7, 8)
            "flower" -> listOf(0, 9, 1, 3, 8, 7)
            "open_book" -> listOf(0, 5, 3, 7, 10)
            "landscape" -> listOf(0, 7, 5, 2, 3, 6)
            else -> null
        }
        return illustrationColors?.get(region) ?: listOf(3, 5, 7, 6)[Math.floorMod(region, 4)]
    }
}

/** Drawn here, without downloaded assets. Digits identify the outlined regions. */
private val coloringIllustrationPatterns = mapOf(
    "tree" to listOf(
        "000000000000", "000001100000", "000111111000", "001111111100",
        "011113111110", "011111111110", "001111131100", "000111111000",
        "000002200000", "000002200000", "004444444400", "044444444440"
    ),
    "flower" to listOf(
        "000011110000", "000115511000", "001155551100", "001155551100",
        "000115511000", "000011110000", "000002200000", "000332233000",
        "003332233300", "000332233000", "000002200000", "000442244000"
    ),
    "open_book" to listOf(
        "000000000000", "000000000000", "011110222220", "011111222220",
        "013331233320", "011111222220", "013331233320", "011111222220",
        "011111222220", "044444444440", "000004400000", "000000000000"
    ),
    "landscape" to listOf(
        "000001110000", "000011111000", "000001110000", "000000000000",
        "000020030000", "000222333000", "002222333300", "022222333330",
        "444444444444", "444445544444", "444455554444", "444555555444"
    )
)

internal val coloringTemplates = listOf(
    ColoringTemplate("mosaic", "Mosaïque", "Mosaic"),
    ColoringTemplate("checker", "Damier", "Checkerboard"),
    ColoringTemplate("waves", "Vagues", "Waves"),
    ColoringTemplate("rosette", "Rosace", "Rosette"),
    ColoringTemplate("diamond", "Losanges", "Diamonds"),
    ColoringTemplate("mountain", "Montagne", "Mountain"),
    ColoringTemplate("sun", "Soleil", "Sun"),
    ColoringTemplate("blank", "Quadrillage libre", "Blank grid"),
    ColoringTemplate("tree", "Arbre", "Tree"),
    ColoringTemplate("flower", "Fleur", "Flower"),
    ColoringTemplate("open_book", "Livre ouvert", "Open book"),
    ColoringTemplate("landscape", "Paysage", "Landscape")
)

internal fun validColoringCells(cells: List<Int>): Boolean =
    cells.size == COLORING_CELLS && cells.all { it in 0..COLORING_COLOR_COUNT }

/** Fill intervening squares too, even when a fast finger movement skips pointer samples. */
internal fun coloringSegment(from: Int, to: Int): List<Int> {
    if (from !in 0 until COLORING_CELLS || to !in 0 until COLORING_CELLS) return emptyList()
    val fromRow = from / COLORING_SIDE
    val fromColumn = from % COLORING_SIDE
    val deltaRow = to / COLORING_SIDE - fromRow
    val deltaColumn = to % COLORING_SIDE - fromColumn
    val steps = max(abs(deltaRow), abs(deltaColumn)).coerceAtLeast(1)
    return (0..steps).map { step ->
        val row = (fromRow + deltaRow * step.toFloat() / steps).roundToInt()
        val column = (fromColumn + deltaColumn * step.toFloat() / steps).roundToInt()
        row * COLORING_SIDE + column
    }.distinct()
}

/** Immutable gesture history: a drag is one operation, not one operation per square. */
internal data class ColoringHistory(
    val cells: List<Int> = List(COLORING_CELLS) { 0 },
    val previous: List<List<Int>> = emptyList(),
    val following: List<List<Int>> = emptyList()
) {
    fun paint(next: List<Int>): ColoringHistory {
        if (!validColoringCells(next) || cells == next) return this
        return ColoringHistory(next.toList(), (previous + listOf(cells)).takeLast(COLORING_HISTORY_LIMIT))
    }
    fun undo(): ColoringHistory = if (previous.isEmpty()) this else
        ColoringHistory(previous.last(), previous.dropLast(1), (following + listOf(cells)).takeLast(COLORING_HISTORY_LIMIT))
    fun redo(): ColoringHistory = if (following.isEmpty()) this else
        ColoringHistory(following.last(), (previous + listOf(cells)).takeLast(COLORING_HISTORY_LIMIT), following.dropLast(1))
}
