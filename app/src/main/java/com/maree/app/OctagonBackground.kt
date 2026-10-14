package com.maree.app

import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

/**
 * Actual drawing limits, also used when checking text contrast above the decoration.
 * Each tile is clipped to its own non-overlapping cell. At any pixel there can therefore
 * be, at most, one face, one facet, one outline, one halo and one narrow seam, in that order.
 * Shader alpha and the per-tile intensity only reduce these limits; antialiasing does too.
 */
internal object OctagonStyle {
    const val FACE_COLOR: Long = 0xFF2C3935L
    const val FACE_MAX_ALPHA = 12
    const val FACET_COLOR: Long = 0xFF1E302AL
    const val FACET_MAX_ALPHA = 8
    const val OUTLINE_COLOR: Long = 0xFF172922L
    const val OUTLINE_MAX_ALPHA = 40
    const val LIGHT_COLOR: Long = 0xFFBA493BL
    const val HALO_MAX_ALPHA = 18
    const val SEAM_MAX_ALPHA = 48
    const val MAX_TILES = 100
}

/**
 * A field of eight-sided, faceted tiles, with warm light travelling along their edges.
 * This renderer owns no timer: the shared lifecycle/reduced-motion loop supplies its phase.
 * Paths, shaders, matrices and the bounded layout are reused between frames.
 */
internal class SelkieOctagonField {
    private val facePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = OctagonStyle.FACE_COLOR.toInt()
        style = Paint.Style.FILL
    }
    private val facetPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = OctagonStyle.FACET_COLOR.toInt()
        alpha = OctagonStyle.FACET_MAX_ALPHA
        style = Paint.Style.FILL
    }
    private val edgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = OctagonStyle.OUTLINE_COLOR.toInt()
        alpha = OctagonStyle.OUTLINE_MAX_ALPHA
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
    }
    private val haloPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
    }
    private val seamPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
    }
    private val octagon = Path()
    private val facet = Path()
    private val lightMatrix = Matrix()
    private var haloGradient: LinearGradient? = null
    private var seamGradient: LinearGradient? = null
    private var centersX = FloatArray(0)
    private var centersY = FloatArray(0)
    private var seeds = FloatArray(0)
    private var cachedWidth = -1f
    private var cachedHeight = -1f
    private var cachedDensity = -1f
    private var cellSize = 0f

    fun draw(scope: DrawScope, phase: Float) {
        val width = scope.size.width
        val height = scope.size.height
        val density = scope.density
        if (!width.isFinite() || !height.isFinite() || !density.isFinite() ||
            width <= 0f || height <= 0f || density <= 0f || !phase.isFinite()) return
        if (width != cachedWidth || height != cachedHeight || density != cachedDensity) {
            rebuild(width, height, density)
        }
        // Integer harmonics make the shared phase's wrap seamless, including after a pause.
        val time = (phase % 1f) * (2.0 * PI).toFloat()
        val halfCell = cellSize / 2f
        scope.drawIntoCanvas { canvas ->
            val native = canvas.nativeCanvas
            for (index in centersX.indices) {
                val seed = seeds[index]
                val baseX = centersX[index]
                val baseY = centersY[index]
                val driftX = sin(time + seed) * 4.5f * density
                val driftY = cos(time + seed * 0.73f) * 4.5f * density
                val tilt = sin(time + seed * 0.37f) * 2.8f
                // Broad, slow variation; the light never flashes or switches abruptly off.
                val intensity = 0.60f + 0.40f * (0.5f + 0.5f * sin(time + seed * 0.61f))
                val intensityAlpha = (intensity * 255f).toInt().coerceIn(0, 255)

                native.save()
                // Separate cells cap cumulative opacity even while adjacent tiles move.
                native.clipRect(baseX - halfCell, baseY - halfCell,
                    baseX + halfCell, baseY + halfCell)
                native.translate(baseX + driftX, baseY + driftY)
                native.rotate(tilt)
                facePaint.alpha = if (index % 3 == 0) OctagonStyle.FACE_MAX_ALPHA else 9
                native.drawPath(octagon, facePaint)
                native.drawPath(facet, facetPaint)
                native.drawPath(octagon, edgePaint)

                // A rotating gradient moves a soft ember highlight around the chamfered edge.
                // These are two bounded strokes, not an additive blend or a full-screen glow.
                lightMatrix.setRotate(phase * 360f + seed * 57.29578f)
                haloGradient?.setLocalMatrix(lightMatrix)
                seamGradient?.setLocalMatrix(lightMatrix)
                haloPaint.alpha = intensityAlpha
                seamPaint.alpha = intensityAlpha
                native.drawPath(octagon, haloPaint)
                native.drawPath(octagon, seamPaint)
                native.restore()
            }
        }
    }

    private fun rebuild(width: Float, height: Float, density: Float) {
        var pitch = 82f * density
        var columns = ceil(width / pitch).toInt() + 2
        var rows = ceil(height / pitch).toInt() + 2
        // Retain recognisable tile sizes on phones while bounding work on tablets and rotation.
        while (columns * rows > OctagonStyle.MAX_TILES) {
            pitch *= 1.08f
            columns = ceil(width / pitch).toInt() + 2
            rows = ceil(height / pitch).toInt() + 2
        }
        cellSize = pitch
        val count = columns * rows
        centersX = FloatArray(count)
        centersY = FloatArray(count)
        seeds = FloatArray(count)
        for (row in 0 until rows) {
            for (column in 0 until columns) {
                val index = row * columns + column
                centersX[index] = (column - 0.5f) * pitch
                centersY[index] = (row - 0.5f) * pitch
                seeds[index] = ((row * 13 + column * 7) % 29) * 0.24166f
            }
        }

        val half = pitch * 0.385f
        val chamfer = half * 0.56f
        octagon.rewind()
        octagon.moveTo(-half + chamfer, -half)
        octagon.lineTo(half - chamfer, -half)
        octagon.lineTo(half, -half + chamfer)
        octagon.lineTo(half, half - chamfer)
        octagon.lineTo(half - chamfer, half)
        octagon.lineTo(-half + chamfer, half)
        octagon.lineTo(-half, half - chamfer)
        octagon.lineTo(-half, -half + chamfer)
        octagon.close()
        facet.rewind()
        facet.moveTo(0f, 0f)
        facet.lineTo(half, half - chamfer)
        facet.lineTo(half - chamfer, half)
        facet.lineTo(-half + chamfer, half)
        facet.lineTo(-half, half - chamfer)
        facet.close()

        edgePaint.strokeWidth = max(1f, 1.15f * density)
        haloPaint.strokeWidth = 4.5f * density
        seamPaint.strokeWidth = 1.45f * density
        haloGradient = edgeLight(half, OctagonStyle.HALO_MAX_ALPHA, 0.32f, 0.68f)
        seamGradient = edgeLight(half, OctagonStyle.SEAM_MAX_ALPHA, 0.43f, 0.57f)
        haloPaint.shader = haloGradient
        seamPaint.shader = seamGradient
        cachedWidth = width
        cachedHeight = height
        cachedDensity = density
    }

    private fun edgeLight(half: Float, alpha: Int, start: Float, end: Float): LinearGradient {
        val rgb = OctagonStyle.LIGHT_COLOR.toInt() and 0x00FFFFFF
        val light = rgb or (alpha shl 24)
        return LinearGradient(-half, -half, half, half,
            intArrayOf(rgb, light, light, rgb),
            floatArrayOf(0f, start, end, 1f), Shader.TileMode.CLAMP)
    }
}
