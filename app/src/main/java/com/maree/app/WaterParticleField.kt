package com.maree.app

import android.graphics.Paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

private const val WATER_MAX_POINTS = 1_600
private const val WATER_TURN = 6.283185307179586

/**
 * A full-screen dotted water surface. Remember one instance next to the Canvas.
 * The caller owns time/lifecycle: phase 0..1 completes one seamless wave cycle.
 * Arrays resize only with the viewport or density; paints and buffers are reused.
 */
internal class SelkieWaterParticleField {
    private val lowPaint = waterPointPaint()
    private val mediumPaint = waterPointPaint()
    private val highPaint = waterPointPaint()

    private var basePoints = FloatArray(0)
    private var lowPoints = FloatArray(0)
    private var mediumPoints = FloatArray(0)
    private var highPoints = FloatArray(0)
    private var cachedWidth = -1f
    private var cachedHeight = -1f
    private var cachedDensity = -1f
    private var pointCount = 0

    private var primaryX = 0.0
    private var primaryY = 0.0
    private var secondaryX = 0.0
    private var secondaryY = 0.0
    private var horizontalX = 0.0
    private var horizontalY = 0.0
    private var primaryAmplitude = 0.0
    private var secondaryAmplitude = 0.0
    private var horizontalAmplitude = 0.0
    private var lastArgb = 0
    private var colorsInitialized = false

    fun draw(scope: DrawScope, phase: Float, color: Color) {
        val width = scope.size.width
        val height = scope.size.height
        val density = scope.density
        if (!width.isFinite() || !height.isFinite() || !density.isFinite() || !phase.isFinite() ||
            width <= 0f || height <= 0f || density <= 0f) return

        val argb = color.toArgb()
        val alpha = argb ushr 24
        if (alpha == 0) return
        if (width != cachedWidth || height != cachedHeight || density != cachedDensity) {
            if (!prepareGrid(width, height, density)) return
        }
        if (!colorsInitialized || argb != lastArgb) {
            val rgb = argb and 0x00ffffff
            lowPaint.color = rgb or ((alpha * 0.65f).roundToInt() shl 24)
            mediumPaint.color = rgb or ((alpha * 0.82f).roundToInt() shl 24)
            highPaint.color = argb
            lastArgb = argb
            colorsInitialized = true
        }

        val time = phase.toDouble() * WATER_TURN
        var lowCount = 0
        var mediumCount = 0
        var highCount = 0
        var offset = 0
        val coordinateCount = pointCount * 2
        while (offset < coordinateCount) {
            val x = basePoints[offset].toDouble()
            val y = basePoints[offset + 1].toDouble()
            // Neighbouring points share the same waves, preserving visible flowing rows.
            val crest = sin(x * primaryX + y * primaryY - time)
            val crossWave = cos(x * secondaryX - y * secondaryY + time * 2.0)
            val ripple = sin(sqrt((x - width * 0.5) * (x - width * 0.5) +
                (y - height * 0.45) * (y - height * 0.45)) / (90.0 * density) - time * 3.0)
            val movedX = (x + sin(y / (150.0 * density) + time * 2.0) * 4.0 * density + cos(x * horizontalX + y * horizontalY + time) *
                horizontalAmplitude).toFloat()
            val movedY = (y + crest * primaryAmplitude +
                crossWave * secondaryAmplitude + ripple * 5.0 * density).toFloat()
            when {
                crest > 0.36 -> {
                    highPoints[highCount] = movedX
                    highPoints[highCount + 1] = movedY
                    highCount += 2
                }
                crest < -0.36 -> {
                    lowPoints[lowCount] = movedX
                    lowPoints[lowCount + 1] = movedY
                    lowCount += 2
                }
                else -> {
                    mediumPoints[mediumCount] = movedX
                    mediumPoints[mediumCount + 1] = movedY
                    mediumCount += 2
                }
            }
            offset += 2
        }

        scope.drawIntoCanvas { canvas ->
            val nativeCanvas = canvas.nativeCanvas
            if (lowCount > 0) nativeCanvas.drawPoints(lowPoints, 0, lowCount, lowPaint)
            if (mediumCount > 0) nativeCanvas.drawPoints(mediumPoints, 0, mediumCount, mediumPaint)
            if (highCount > 0) nativeCanvas.drawPoints(highPoints, 0, highCount, highPaint)
        }
    }

    private fun prepareGrid(width: Float, height: Float, density: Float): Boolean {
        val widthDouble = width.toDouble()
        val heightDouble = height.toDouble()
        val densityDouble = density.toDouble()
        // The lower bound also keeps count arithmetic safe for extreme aspect ratios.
        var spacing = max(18.0 * densityDouble, max(widthDouble, heightDouble) / 260.0)
        var columns = ceil(widthDouble / spacing).toInt() + 5
        var rows = ceil(heightDouble / spacing).toInt() + 5
        while (columns * rows > WATER_MAX_POINTS) {
            spacing *= max(1.025, sqrt(columns.toDouble() * rows / WATER_MAX_POINTS) * 1.01)
            columns = ceil(widthDouble / spacing).toInt() + 5
            rows = ceil(heightDouble / spacing).toInt() + 5
        }
        val newPointCount = columns * rows
        val verticalMotion = 28.0 * densityDouble
        val horizontalMotion = 10.0 * densityDouble
        // Reject any geometry whose offscreen margin could overflow native float coordinates.
        if (!(spacing * 2.0 + verticalMotion).toFloat().isFinite() ||
            !((columns - 3) * spacing + horizontalMotion).toFloat().isFinite() ||
            !((rows - 3) * spacing + verticalMotion).toFloat().isFinite()) return false

        val nextBasePoints = FloatArray(newPointCount * 2)
        var row = 0
        var offset = 0
        while (row < rows) {
            val y = ((row - 2) * spacing).toFloat()
            var column = 0
            while (column < columns) {
                nextBasePoints[offset] = ((column - 2) * spacing).toFloat()
                nextBasePoints[offset + 1] = y
                offset += 2
                column++
            }
            row++
        }
        basePoints = nextBasePoints
        lowPoints = FloatArray(newPointCount * 2)
        mediumPoints = FloatArray(newPointCount * 2)
        highPoints = FloatArray(newPointCount * 2)
        pointCount = newPointCount
        cachedWidth = width
        cachedHeight = height
        cachedDensity = density

        primaryX = WATER_TURN / (235.0 * densityDouble)
        primaryY = WATER_TURN / (365.0 * densityDouble)
        secondaryX = WATER_TURN / (420.0 * densityDouble)
        secondaryY = WATER_TURN / (205.0 * densityDouble)
        horizontalX = WATER_TURN / (420.0 * densityDouble)
        horizontalY = WATER_TURN / (330.0 * densityDouble)
        primaryAmplitude = 18.0 * densityDouble
        secondaryAmplitude = 5.0 * densityDouble
        horizontalAmplitude = horizontalMotion
        val strokeWidth = (2.7 * densityDouble).toFloat()
        lowPaint.strokeWidth = strokeWidth
        mediumPaint.strokeWidth = strokeWidth
        highPaint.strokeWidth = strokeWidth
        return true
    }
}

private fun waterPointPaint(): Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    strokeCap = Paint.Cap.ROUND
    style = Paint.Style.STROKE
}
