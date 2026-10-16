package com.maree.app

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val coloringPalette = listOf(
    Color.White, Color(0xFF344A40), Color(0xFF42A98F), Color(0xFF91CFB5),
    Color(0xFF6C5AA8), Color(0xFFB2A3DB), Color(0xFF4479AE),
    Color(0xFFF0BA57), Color(0xFFD67F62), Color(0xFFC87E9C), Color(0xFF454253)
)
private fun coloringColorName(index: Int): String = listOf(
    tr("Gomme", "Eraser"), tr("Vert forêt", "Forest green"), tr("Menthe", "Mint"),
    tr("Sauge", "Sage"), tr("Violet", "Purple"), tr("Lavande", "Lavender"),
    tr("Bleu", "Blue"), tr("Or", "Gold"), tr("Terracotta", "Terracotta"),
    tr("Rose", "Rose"), tr("Charbon", "Charcoal")
)[index]

/** A small, offline creative pause. No account, images, points or sharing required. */
@Composable
internal fun ColoringSection() {
    val context = LocalContext.current
    val store = remember(context) { ColoringStore(context) }
    val scope = rememberCoroutineScope()
    var pageId by rememberSaveable { mutableStateOf(coloringTemplates.first().id) }
    val template = coloringTemplates.firstOrNull { it.id == pageId } ?: coloringTemplates.first()
    var color by rememberSaveable { mutableIntStateOf(2) }
    var history by remember(template.id) { mutableStateOf(ColoringHistory()) }
    var preview by remember(template.id) { mutableStateOf<List<Int>?>(null) }
    var loading by remember(template.id) { mutableStateOf(true) }
    var saving by remember(template.id) { mutableStateOf(false) }
    var saveFailed by remember(template.id) { mutableStateOf(false) }
    var hasSaved by remember(template.id) { mutableStateOf(false) }
    var pendingReset by remember(template.id) { mutableStateOf(false) }
    var showReset by remember { mutableStateOf(false) }
    var row by rememberSaveable { mutableIntStateOf(0) }
    var column by rememberSaveable { mutableIntStateOf(0) }
    val idle = !loading && !saving && preview == null
    val editable = idle && !pendingReset

    LaunchedEffect(template.id, store) {
        val loaded = withContext(Dispatchers.IO) { store.loadPage(template.id) }
        history = ColoringHistory(loaded)
        loading = false
    }
    fun save(next: ColoringHistory) {
        if (loading || saving || pendingReset) return
        history = next
        saving = true
        saveFailed = false
        scope.launch {
            val succeeded = withContext(Dispatchers.IO) { store.savePage(template.id, next.cells) }
            saveFailed = !succeeded
            hasSaved = succeeded
            saving = false
        }
    }

    fun reset() {
        if (loading || saving) return
        pendingReset = true
        saving = true
        saveFailed = false
        scope.launch {
            val succeeded = withContext(Dispatchers.IO) { store.resetPage(template.id) }
            if (succeeded) {
                history = ColoringHistory()
                pendingReset = false
            }
            saveFailed = !succeeded
            hasSaved = succeeded
            saving = false
        }
    }

    AppCardSurface {
        SectionLabel(tr("Pause créative", "Creative pause"))
        Text(tr("Choisis un motif et une couleur, puis touche ou parcours les carreaux. Chaque page se conserve sur ce téléphone.",
            "Choose a pattern and color, then tap or move across the squares. Each page stays on this phone."), color = MareePalette.Muted)
        Text(tr("Une pause libre, sans points ni objectif à terminer.", "An open-ended break, with no points or completion target."), color = MareePalette.Muted)
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            coloringTemplates.forEach { item ->
                FilterChip(selected = item.id == template.id, onClick = {
                    if (!loading && !saving && preview == null && !saveFailed && !pendingReset) pageId = item.id
                }, enabled = editable && !saveFailed, label = { Text(item.name()) })
            }
        }
        Text(template.name(), color = MareePalette.Ink, fontWeight = FontWeight.SemiBold)
        if (loading) CircularProgressIndicator(Modifier.size(28.dp))
        ColoringGrid(template, preview ?: history.cells, color, !loading && !saving && !pendingReset,
            row, column, onPreview = { preview = it }, onFinished = { cells, cell ->
                preview = null
                row = cell / COLORING_SIDE
                column = cell % COLORING_SIDE
                val next = history.paint(cells)
                if (next != history) save(next)
            })
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            coloringPalette.forEachIndexed { index, shade ->
                FilterChip(selected = color == index, onClick = { color = index }, enabled = editable,
                    label = { Text(coloringColorName(index)) },
                    leadingIcon = { Box(Modifier.size(18.dp).background(shade, CircleShape)) })
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = { save(history.undo()) }, enabled = editable && history.previous.isNotEmpty(), modifier = Modifier.heightIn(min = 48.dp)) {
                Text(tr("Annuler", "Undo"))
            }
            TextButton(onClick = { save(history.redo()) }, enabled = editable && history.following.isNotEmpty(), modifier = Modifier.heightIn(min = 48.dp)) {
                Text(tr("Rétablir", "Redo"))
            }
        }
        Text(tr("Pour colorier sans viser un petit carreau", "To color without targeting a small square"), color = MareePalette.Ink, fontWeight = FontWeight.SemiBold)
        ColoringCoordinateSelector(tr("Ligne", "Row"), row, editable, onChange = { row = it })
        ColoringCoordinateSelector(tr("Colonne", "Column"), column, editable, onChange = { column = it })
        Text(tr("Carreau : ligne ${row + 1}, colonne ${column + 1} · ${coloringColorName(history.cells[row * COLORING_SIDE + column])}",
            "Square: row ${row + 1}, column ${column + 1} · ${coloringColorName(history.cells[row * COLORING_SIDE + column])}"),
            color = MareePalette.Muted, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
        PrimaryAction(tr("Appliquer : ${coloringColorName(color)}", "Apply: ${coloringColorName(color)}"), enabled = editable, onClick = {
            val next = history.paint(history.cells.toMutableList().also { it[row * COLORING_SIDE + column] = color })
            if (next != history) save(next)
        })
        Text(when {
            saving && pendingReset -> tr("Effacement…", "Clearing…")
            saving -> tr("Enregistrement…", "Saving…")
            saveFailed && pendingReset -> tr("L’effacement n’a pas pu être confirmé. Réessaie pour terminer l’effacement de cette page.", "The clearing could not be confirmed. Try again to finish clearing this page.")
            saveFailed -> tr("La modification n’a pas été enregistrée. Réessaie avant de quitter cette page.", "Your change was not saved. Try again before leaving this page.")
            hasSaved -> tr("Enregistré sur ce téléphone.", "Saved on this phone.")
            else -> tr("Enregistrement automatique après chaque geste. Les 50 dernières actions peuvent être annulées tant que cette page reste ouverte.",
                "Automatically saved after each gesture. You can undo the last 50 actions while this page stays open.")
        }, color = if (saveFailed) MareePalette.Danger else MareePalette.Muted,
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
        if (saveFailed) {
            PrimaryAction(if (pendingReset) tr("Réessayer l’effacement", "Retry clearing") else tr("Réessayer l’enregistrement", "Retry saving"),
                enabled = idle, onClick = { if (pendingReset) reset() else save(history) })
        }
        TextButton(onClick = { showReset = true }, enabled = editable, modifier = Modifier.heightIn(min = 48.dp)) {
            Text(tr("Effacer cette page", "Clear this page"), color = MareePalette.Danger)
        }
    }
    if (showReset) AlertDialog(onDismissRequest = { showReset = false },
        title = { Text(tr("Effacer ${template.name()} ?", "Clear ${template.name()}?")) },
        text = { Text(tr("Seul ce coloriage sera effacé. Les autres pages sont conservées. Cette action ne peut pas être annulée.",
            "Only this coloring page will be cleared. Other pages are kept. This cannot be undone.")) },
        confirmButton = { TextButton(onClick = {
            showReset = false
            reset()
        }) { Text(tr("Effacer", "Clear")) } },
        dismissButton = { TextButton(onClick = { showReset = false }) { Text(tr("Annuler", "Cancel")) } })
}

@Composable
private fun ColoringCoordinateSelector(label: String, value: Int, enabled: Boolean, onChange: (Int) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        TextButton(onClick = { onChange(value - 1) }, enabled = enabled && value > 0,
            modifier = Modifier.heightIn(min = 48.dp).semantics { contentDescription = tr("$label : diminuer", "$label: decrease") }) { Text("−") }
        Text("$label ${value + 1} / $COLORING_SIDE", color = MareePalette.Ink)
        TextButton(onClick = { onChange(value + 1) }, enabled = enabled && value < COLORING_SIDE - 1,
            modifier = Modifier.heightIn(min = 48.dp).semantics { contentDescription = tr("$label : augmenter", "$label: increase") }) { Text("+") }
    }
}

@Composable
private fun ColoringGrid(template: ColoringTemplate, cells: List<Int>, color: Int, enabled: Boolean,
    selectedRow: Int, selectedColumn: Int, onPreview: (List<Int>?) -> Unit, onFinished: (List<Int>, Int) -> Unit) {
    val currentCells by rememberUpdatedState(cells)
    val previewCallback by rememberUpdatedState(onPreview)
    val finishedCallback by rememberUpdatedState(onFinished)
    val description = tr("Grille de coloriage de 12 lignes et 12 colonnes. Les commandes de ligne et de colonne sous la palette permettent aussi de colorier.",
        "Coloring grid with 12 rows and 12 columns. Row and column controls below the palette also let you color.")
    Canvas(Modifier.fillMaxWidth().aspectRatio(1f).background(Color.White)
        .semantics { contentDescription = description }
        .pointerInput(template.id, color, enabled) {
            awaitEachGesture {
                val first = awaitFirstDown()
                if (enabled) {
                    val painted = currentCells.toMutableList()
                    var lastCell: Int? = null
                    fun paint(position: Offset) {
                        val column = (position.x / (size.width / COLORING_SIDE.toFloat())).toInt().coerceIn(0, COLORING_SIDE - 1)
                        val row = (position.y / (size.height / COLORING_SIDE.toFloat())).toInt().coerceIn(0, COLORING_SIDE - 1)
                        val nextCell = row * COLORING_SIDE + column
                        coloringSegment(lastCell ?: nextCell, nextCell).forEach { painted[it] = color }
                        lastCell = nextCell
                        previewCallback(painted.toList())
                    }
                    first.consume()
                    paint(first.position)
                    try {
                        do {
                            val event = awaitPointerEvent()
                            event.changes.forEach { change ->
                                if (change.id == first.id) { paint(change.position); change.consume() }
                            }
                        } while (event.changes.any { it.id == first.id && it.pressed })
                        finishedCallback(painted.toList(), lastCell ?: 0)
                    } finally { previewCallback(null) }
                }
            }
        }) {
        val cell = size.width / COLORING_SIDE
        val ink = Color(0xFF34323E)
        for (row in 0 until COLORING_SIDE) for (column in 0 until COLORING_SIDE) {
            val position = Offset(column * cell, row * cell)
            drawRect(coloringPalette[cells[row * COLORING_SIDE + column]], position, Size(cell, cell))
            drawRect(ink.copy(alpha = 0.23f), position, Size(cell, cell), style = Stroke(0.65.dp.toPx()))
            val region = template.region(row, column)
            if (row == 0 || template.region(row - 1, column) != region)
                drawLine(ink, position, position + Offset(cell, 0f), 1.5.dp.toPx())
            if (column == 0 || template.region(row, column - 1) != region)
                drawLine(ink, position, position + Offset(0f, cell), 1.5.dp.toPx())
        }
        drawRect(ink, style = Stroke(2.dp.toPx()))
        val selection = Offset(selectedColumn * cell + 2.dp.toPx(), selectedRow * cell + 2.dp.toPx())
        val selectionSize = Size(cell - 4.dp.toPx(), cell - 4.dp.toPx())
        drawRect(Color.White, selection, selectionSize, style = Stroke(3.dp.toPx()))
        drawRect(ink, selection, selectionSize, style = Stroke(1.5.dp.toPx()))
    }
}
