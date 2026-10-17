package com.maree.app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.util.LruCache
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.min
import kotlin.math.roundToInt

private data class ImportedSource(val pixels: IntArray, val mask: ImportedRegionMask)
private data class ImportedFrame(val pageId: String, val bitmap: Bitmap, val mask: ImportedRegionMask)
private val importedSourceCache = LruCache<String, ImportedSource>(3)

private fun importedSource(context: Context, page: ImportedColoringPage): ImportedSource {
    synchronized(importedSourceCache) { importedSourceCache.get(page.id)?.let { return it } }
    val bitmap = context.assets.open(page.imageAsset).use { requireNotNull(BitmapFactory.decodeStream(it)) }
    require(bitmap.width == page.width && bitmap.height == page.height)
    val pixels = IntArray(page.width * page.height)
    bitmap.getPixels(pixels, 0, page.width, 0, 0, page.width, page.height)
    bitmap.recycle()
    val mask = context.assets.open(page.maskAsset).use { decodeImportedMask(it, page.width, page.height, page.regions.size) }
    val source = ImportedSource(pixels, mask)
    synchronized(importedSourceCache) { importedSourceCache.put(page.id, source) }
    return source
}

/** Multiply fills by the original RGB pixels: white accepts color, all ink and shading stay visible. */
private fun importedFrame(context: Context, page: ImportedColoringPage, colors: List<Int>, preview: Boolean): ImportedFrame {
    val source = importedSource(context, page)
    val palette = illustrationPalette.map { it.toArgb() }
    val output = IntArray(source.pixels.size) { index ->
        val region = source.mask.labels[index] - 1
        val color = if (region < 0) 0 else if (preview) 1 + ((region + 1) * 7) % 16 else colors.getOrNull(region) ?: 0
        val fill = palette[color.coerceIn(0, 16)]; val original = source.pixels[index]
        val r = ((original ushr 16) and 255) * ((fill ushr 16) and 255) / 255
        val g = ((original ushr 8) and 255) * ((fill ushr 8) and 255) / 255
        val b = (original and 255) * (fill and 255) / 255
        (255 shl 24) or (r shl 16) or (g shl 8) or b
    }
    return ImportedFrame(page.id, Bitmap.createBitmap(output, page.width, page.height, Bitmap.Config.ARGB_8888), source.mask)
}

private fun importedPan(pan: Offset, width: Float, height: Float, zoom: Float, page: ImportedColoringPage): Offset {
    val scale = min(width / page.width, height / page.height) * zoom
    val maxX = ((page.width * scale - width) / 2).coerceAtLeast(0f)
    val maxY = ((page.height * scale - height) / 2).coerceAtLeast(0f)
    return Offset(pan.x.coerceIn(-maxX, maxX), pan.y.coerceIn(-maxY, maxY))
}

@Composable
internal fun ImportedColoringCanvas(page: ImportedColoringPage, colors: List<Int>, enabled: Boolean,
    showNumbers: Boolean, zoom: Float, onRegionTapped: (Int) -> Unit, modifier: Modifier = Modifier, preview: Boolean = false) {
    val context = LocalContext.current.applicationContext
    var retry by remember(page.id) { mutableIntStateOf(0) }
    val result by produceState<Result<ImportedFrame>?>(null, page.id, colors, preview, retry) {
        value = withContext(Dispatchers.Default) { runCatching { importedFrame(context, page, colors, preview) } }
    }
    val ready = result?.getOrNull()?.takeIf { it.pageId == page.id }
    var pan by remember(page.id) { mutableStateOf(Offset.Zero) }
    val z = if (preview) 1f else zoom.coerceIn(1f, 3f)
    LaunchedEffect(z) { if (z == 1f) pan = Offset.Zero }
    val tap by rememberUpdatedState(onRegionTapped)
    val active by rememberUpdatedState(enabled)
    val bitmapPaint = remember { Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG) }
    val labelPaint = remember { Paint(Paint.ANTI_ALIAS_FLAG).apply { color = android.graphics.Color.BLACK; textAlign = Paint.Align.CENTER } }
    val labelHalo = remember { Paint(Paint.ANTI_ALIAS_FLAG).apply { color = android.graphics.Color.WHITE; textAlign = Paint.Align.CENTER; style = Paint.Style.STROKE; strokeWidth = 1.5f } }
    if (result?.isFailure == true) {
        Text(tr("Ce dessin n’a pas pu être chargé.", "This drawing could not be loaded."), color = MareePalette.Danger)
        TextButton(onClick = { retry++ }) { Text(tr("Réessayer", "Try again")) }
    }
    var canvasModifier = modifier.fillMaxWidth().aspectRatio(1f).clipToBounds().semantics {
        role = Role.Image
        contentDescription = if (preview) tr("Aperçu : ${page.name()}", "Preview: ${page.name()}") else tr("${page.name()}. Touche une zone pour la colorier ; zoome pour les détails.", "${page.name()}. Tap an area to fill it; zoom for details.")
    }
    if (!preview) {
        canvasModifier = canvasModifier.pointerInput(page.id, ready, z) {
            detectTapGestures { position ->
                val frame = ready ?: return@detectTapGestures
                if (!active) return@detectTapGestures
                val scale = min(size.width.toFloat() / page.width, size.height.toFloat() / page.height) * z
                val p = importedPan(pan, size.width.toFloat(), size.height.toFloat(), z, page)
                val sourceX = (position.x - size.width / 2f - p.x) / scale + page.width / 2f
                val sourceY = (position.y - size.height / 2f - p.y) / scale + page.height / 2f
                if (sourceX < 0 || sourceY < 0 || sourceX >= page.width || sourceY >= page.height) return@detectTapGestures
                val x = sourceX.toInt(); val y = sourceY.toInt()
                var region = frame.mask.regionAt(x, y)
                // A small source-pixel tolerance helps touch dark contour pixels on the small originals.
                if (region < 0) {
                    search@ for (radius in 1..2) for (dy in -radius..radius) for (dx in -radius..radius) {
                        val candidate = frame.mask.regionAt(x + dx, y + dy)
                        if (candidate >= 0) { region = candidate; break@search }
                    }
                }
                if (region >= 0) tap(region)
            }
        }
        if (z > 1f) canvasModifier = canvasModifier.pointerInput(page.id, z) {
            detectDragGestures { change, amount -> change.consume(); pan = importedPan(pan + amount, size.width.toFloat(), size.height.toFloat(), z, page) }
        }
    }
    Canvas(canvasModifier) {
        drawIntoCanvas { canvas ->
            val c = canvas.nativeCanvas; val save = c.save()
            try {
                c.clipRect(0f, 0f, size.width, size.height); c.drawColor(android.graphics.Color.WHITE)
                val frame = ready ?: return@drawIntoCanvas
                val p = importedPan(pan, size.width, size.height, z, page)
                val scale = min(size.width / page.width, size.height / page.height) * z
                c.translate(size.width / 2 + p.x, size.height / 2 + p.y); c.scale(scale, scale); c.translate(-page.width / 2f, -page.height / 2f)
                c.drawBitmap(frame.bitmap, 0f, 0f, bitmapPaint)
                if (showNumbers && !preview) {
                    val textSize = min(page.width, page.height) * 0.027f
                    labelPaint.textSize = textSize; labelHalo.textSize = textSize
                    page.regions.forEachIndexed { index, anchor ->
                        val dx = (textSize * 0.4f).roundToInt().coerceAtLeast(1); val dy = (textSize * 0.5f).roundToInt().coerceAtLeast(1)
                        if ((colors.getOrNull(index) ?: 0) == 0 && listOf(-1, 1).all { sx -> listOf(-1, 1).all { sy -> frame.mask.regionAt(anchor.x + sx * dx, anchor.y + sy * dy) == index } }) {
                            val text = (1 + ((index + 1) * 7) % 16).toString(); val baseline = anchor.y - (labelPaint.ascent() + labelPaint.descent()) / 2
                            c.drawText(text, anchor.x.toFloat(), baseline, labelHalo); c.drawText(text, anchor.x.toFloat(), baseline, labelPaint)
                        }
                    }
                }
            } finally { c.restoreToCount(save) }
        }
    }
    if (ready == null && result?.isFailure != true && !preview) CircularProgressIndicator(Modifier.size(24.dp))
}
