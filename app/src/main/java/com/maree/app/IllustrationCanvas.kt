package com.maree.app

import android.graphics.Paint
import android.graphics.Path
import android.graphics.Region
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.core.graphics.PathParser
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.floor
import kotlin.math.min
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val ILLUSTRATION_VIEWPORT = 1_000f
private const val ILLUSTRATION_INK = 0xFF293B34.toInt()

/** Paths and hit areas are built together so a tap follows the same painter order as the drawing. */
private data class IllustrationGeometry(
    val pageId: String,
    val paths: List<Path>,
    val hitAreas: List<Region>,
    val details: List<Path>
)

private val illustrationGeometryCache = ConcurrentHashMap<String, IllustrationGeometry>()

private fun illustrationGeometry(page: IllustrationPage): IllustrationGeometry =
    illustrationGeometryCache.getOrPut(page.id) {
        val clip = Region(0, 0, ILLUSTRATION_VIEWPORT.toInt(), ILLUSTRATION_VIEWPORT.toInt())
        val paths = page.regions.map { region ->
            requireNotNull(PathParser.createPathFromPathData(region.pathData)) {
                "Missing path for illustration ${page.id}, region ${region.id}"
            }
        }
        IllustrationGeometry(
            pageId = page.id,
            paths = paths,
            hitAreas = paths.map { path -> Region().apply { setPath(path, clip) } },
            details = page.detailPaths.map { data ->
                requireNotNull(PathParser.createPathFromPathData(data)) {
                    "Missing detail path for illustration ${page.id}"
                }
            }
        )
    }

private fun boundedIllustrationPan(pan: Offset, width: Float, height: Float, zoom: Float): Offset {
    // The original square must continue to cover the viewport when the paper is enlarged.
    val square = min(width, height)
    val maxX = ((square * zoom - width) / 2f).coerceAtLeast(0f)
    val maxY = ((square * zoom - height) / 2f).coerceAtLeast(0f)
    return Offset(pan.x.coerceIn(-maxX, maxX), pan.y.coerceIn(-maxY, maxY))
}

/**
 * A real, curved coloring page. Painting happens on a completed tap only: dragging a zoomed page
 * moves the paper, and scrolling past an unzoomed page never paints a trail across its regions.
 * The surrounding screen provides named region controls as an alternative to precise tapping.
 */
@Composable
internal fun IllustrationCanvas(
    page: IllustrationPage,
    colors: List<Int>,
    enabled: Boolean,
    showNumbers: Boolean,
    zoom: Float,
    onRegionTapped: (Int) -> Unit,
    modifier: Modifier = Modifier,
    preview: Boolean = false
) {
    val geometry by produceState<IllustrationGeometry?>(initialValue = null, page.id) {
        value = withContext(Dispatchers.Default) { illustrationGeometry(page) }
    }
    // produceState can briefly hold the preceding value when switching pages. Never draw or hit it.
    val currentGeometry = geometry?.takeIf { it.pageId == page.id }
    val boundedZoom = if (preview) 1f else zoom.takeIf { it.isFinite() }?.coerceIn(1f, 4f) ?: 1f
    var pan by remember(page.id) { mutableStateOf(Offset.Zero) }
    LaunchedEffect(boundedZoom) {
        if (boundedZoom == 1f) pan = Offset.Zero
    }
    val tapped by rememberUpdatedState(onRegionTapped)
    val currentEnabled by rememberUpdatedState(enabled)
    val fillPaint = remember { Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL } }
    val contourPaint = remember {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ILLUSTRATION_INK
            style = Paint.Style.STROKE
            strokeWidth = 3.6f
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
    }
    val detailPaint = remember {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ILLUSTRATION_INK
            style = Paint.Style.STROKE
            strokeWidth = 1.6f
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            alpha = 190
        }
    }
    val labelPaint = remember {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ILLUSTRATION_INK
            textAlign = Paint.Align.CENTER
            textSize = 25f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
    }
    val labelHaloPaint = remember {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = 25f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            style = Paint.Style.STROKE
            strokeWidth = 4.5f
            strokeJoin = Paint.Join.ROUND
        }
    }
    val pageName = tr(page.french, page.english)
    val description = if (preview) {
        tr("Aperçu coloré : $pageName", "Colored preview: $pageName")
    } else {
        tr(
            "$pageName. Touche une zone pour la colorier. Après avoir zoomé, fais glisser le dessin pour le déplacer. Les commandes sous le dessin permettent aussi de choisir une zone.",
            "$pageName. Tap a region to color it. After zooming, drag the drawing to move it. Controls below the drawing also let you choose a region."
        )
    }
    var canvasModifier = modifier.fillMaxWidth().aspectRatio(1f).clipToBounds().semantics {
        contentDescription = description
        role = Role.Image
    }
    if (!preview) {
        canvasModifier = canvasModifier.pointerInput(page.id, currentGeometry, enabled, boundedZoom) {
            detectTapGestures { position ->
                val ready = currentGeometry ?: return@detectTapGestures
                if (!currentEnabled || size.width <= 0 || size.height <= 0) return@detectTapGestures
                val visiblePan = boundedIllustrationPan(pan, size.width.toFloat(), size.height.toFloat(), boundedZoom)
                val scale = min(size.width, size.height).toFloat() / ILLUSTRATION_VIEWPORT * boundedZoom
                val originalX = (position.x - size.width / 2f - visiblePan.x) / scale + ILLUSTRATION_VIEWPORT / 2f
                val originalY = (position.y - size.height / 2f - visiblePan.y) / scale + ILLUSTRATION_VIEWPORT / 2f
                if (originalX < 0 || originalY < 0 || originalX >= ILLUSTRATION_VIEWPORT || originalY >= ILLUSTRATION_VIEWPORT) {
                    return@detectTapGestures
                }
                val x = floor(originalX).toInt()
                val y = floor(originalY).toInt()
                // Foreground petals and leaves hide background regions, exactly as during drawing.
                val index = ready.hitAreas.indexOfLast { area -> area.contains(x, y) }
                if (index >= 0) tapped(index)
            }
        }
        if (boundedZoom > 1f) {
            canvasModifier = canvasModifier.pointerInput(page.id, boundedZoom) {
                detectDragGestures { change, amount ->
                    change.consume()
                    pan = boundedIllustrationPan(pan + amount, size.width.toFloat(), size.height.toFloat(), boundedZoom)
                }
            }
        }
    }
    Canvas(canvasModifier) {
        drawIntoCanvas { canvas ->
            val native = canvas.nativeCanvas
            val saved = native.save()
            try {
                native.clipRect(0f, 0f, size.width, size.height)
                native.drawColor(android.graphics.Color.WHITE)
                val ready = currentGeometry ?: return@drawIntoCanvas
                val visiblePan = boundedIllustrationPan(pan, size.width, size.height, boundedZoom)
                val scale = min(size.width, size.height) / ILLUSTRATION_VIEWPORT * boundedZoom
                native.translate(size.width / 2f + visiblePan.x, size.height / 2f + visiblePan.y)
                native.scale(scale, scale)
                native.translate(-ILLUSTRATION_VIEWPORT / 2f, -ILLUSTRATION_VIEWPORT / 2f)
                ready.paths.forEachIndexed { index, path ->
                    val colorIndex = if (preview) page.regions[index].colorNumber else colors.getOrNull(index) ?: 0
                    fillPaint.color = illustrationPalette.getOrNull(colorIndex)?.toArgb() ?: android.graphics.Color.WHITE
                    native.drawPath(path, fillPaint)
                    native.drawPath(path, contourPaint)
                }
                ready.details.forEach { path -> native.drawPath(path, detailPaint) }
                if (showNumbers && !preview) {
                    page.regions.forEachIndexed { index, region ->
                        val labelRegion = ready.hitAreas.indexOfLast { area ->
                            area.contains(floor(region.labelX).toInt(), floor(region.labelY).toInt())
                        }
                        if ((colors.getOrNull(index) ?: 0) == 0 && labelRegion == index) {
                            val text = region.colorNumber.toString()
                            val baseline = region.labelY - (labelPaint.ascent() + labelPaint.descent()) / 2f
                            native.drawText(text, region.labelX, baseline, labelHaloPaint)
                            native.drawText(text, region.labelX, baseline, labelPaint)
                        }
                    }
                }
            } finally {
                native.restoreToCount(saved)
            }
        }
    }
}
