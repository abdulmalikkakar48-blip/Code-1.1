package com.maree.app

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val PARTICLE_TURN = 6.2831855f

/**
 * Four dotted outlines, 144 points altogether. Phase is a complete 0..1 turn;
 * all movement is periodic so the caller's 24-second loop closes without a jump.
 * This only draws: no state, pointer handlers, paths or per-frame collections.
 */
internal fun DrawScope.drawSelkieGeometryParticles(
    phase: Float,
    primary: Color,
    secondary: Color
) {
    val time = phase * PARTICLE_TURN
    val drift = 6.dp.toPx()
    val primaryDot = 1.2.dp.toPx()
    val secondaryDot = 1.05.dp.toPx()

    // The centres stay at the edges; opaque content cards continue to cover them.
    drawParticleContour(
        SelkieParticleContours.largeCircle,
        centerX = size.width * 1.01f + sin(time) * drift,
        centerY = size.height * 0.17f + cos(time) * drift * 0.8f,
        radius = 72.dp.toPx().coerceAtMost(size.width * 0.24f),
        wave = 5.5.dp.toPx(), time = time, seed = 0f,
        dotRadius = primaryDot, color = primary
    )
    drawParticleContour(
        SelkieParticleContours.smallCircle,
        centerX = -size.width * 0.02f + cos(time + 1.2f) * drift,
        centerY = size.height * 0.73f + sin(time + 1.2f) * drift * 0.8f,
        radius = 45.dp.toPx().coerceAtMost(size.width * 0.16f),
        wave = 4.dp.toPx(), time = time, seed = 1.2f,
        dotRadius = secondaryDot, color = secondary
    )
    drawParticleContour(
        SelkieParticleContours.roundedTriangle,
        centerX = size.width * 0.055f + sin(time + 2.4f) * drift,
        centerY = size.height * 0.42f + cos(time + 2.4f) * drift * 0.8f,
        radius = 34.dp.toPx().coerceAtMost(size.width * 0.13f),
        wave = 3.5.dp.toPx(), time = time, seed = 2.4f,
        dotRadius = primaryDot, color = primary
    )
    drawParticleContour(
        SelkieParticleContours.diamond,
        centerX = size.width * 0.965f + cos(time + 3.6f) * drift,
        centerY = size.height * 0.87f + sin(time + 3.6f) * drift * 0.8f,
        radius = 27.dp.toPx().coerceAtMost(size.width * 0.11f),
        wave = 3.dp.toPx(), time = time, seed = 3.6f,
        dotRadius = secondaryDot, color = secondary
    )
}

private fun DrawScope.drawParticleContour(
    contour: FloatArray,
    centerX: Float,
    centerY: Float,
    radius: Float,
    wave: Float,
    time: Float,
    seed: Float,
    dotRadius: Float,
    color: Color
) {
    val count = contour.size / 4
    var point = 0
    while (point < count) {
        val offset = point * 4
        val normalX = contour[offset + 2]
        val normalY = contour[offset + 3]
        // Two small waves travel around the outline, rather than pulsing in unison.
        val wavePhase = point.toFloat() / count * PARTICLE_TURN * 2f - time + seed
        val outward = sin(wavePhase) * wave
        val along = cos(wavePhase) * wave * 0.3f
        drawCircle(
            color = color,
            radius = dotRadius,
            center = Offset(
                centerX + contour[offset] * radius + normalX * outward - normalY * along,
                centerY + contour[offset + 1] * radius + normalY * outward + normalX * along
            )
        )
        point++
    }
}

/** Each point stores x/y and an outward normal, computed once for the process. */
private object SelkieParticleContours {
    val largeCircle = circleContour(48)
    val smallCircle = circleContour(32)
    val roundedTriangle = roundedTriangleContour()
    val diamond = diamondContour()
}

private fun circleContour(count: Int): FloatArray {
    val contour = FloatArray(count * 4)
    var point = 0
    while (point < count) {
        val angle = point.toFloat() / count * PARTICLE_TURN
        val x = cos(angle)
        val y = sin(angle)
        putContourPoint(contour, point, x, y, x, y)
        point++
    }
    return contour
}

private fun roundedTriangleContour(): FloatArray {
    // Eight points per straight section and four per quadratic rounded corner.
    val vertices = floatArrayOf(0f, -1f, 0.8660254f, 0.5f, -0.8660254f, 0.5f)
    val contour = FloatArray(36 * 4)
    val trim = 0.16f
    var side = 0
    while (side < 3) {
        val start = side * 2
        val next = ((side + 1) % 3) * 2
        val after = ((side + 2) % 3) * 2
        val startX = vertices[start] + (vertices[next] - vertices[start]) * trim
        val startY = vertices[start + 1] + (vertices[next + 1] - vertices[start + 1]) * trim
        val endX = vertices[next] + (vertices[start] - vertices[next]) * trim
        val endY = vertices[next + 1] + (vertices[start + 1] - vertices[next + 1]) * trim
        val cornerEndX = vertices[next] + (vertices[after] - vertices[next]) * trim
        val cornerEndY = vertices[next + 1] + (vertices[after + 1] - vertices[next + 1]) * trim
        val tangentX = endX - startX
        val tangentY = endY - startY
        var point = 0
        while (point < 8) {
            val fraction = point / 8f
            putContourPointFromTangent(contour, side * 12 + point,
                startX + tangentX * fraction, startY + tangentY * fraction,
                tangentX, tangentY)
            point++
        }
        point = 0
        while (point < 4) {
            val fraction = point / 4f
            val remaining = 1f - fraction
            putContourPointFromTangent(contour, side * 12 + 8 + point,
                remaining * remaining * endX + 2f * remaining * fraction * vertices[next] +
                    fraction * fraction * cornerEndX,
                remaining * remaining * endY + 2f * remaining * fraction * vertices[next + 1] +
                    fraction * fraction * cornerEndY,
                remaining * (vertices[next] - endX) + fraction * (cornerEndX - vertices[next]),
                remaining * (vertices[next + 1] - endY) + fraction * (cornerEndY - vertices[next + 1]))
            point++
        }
        side++
    }
    return contour
}

private fun diamondContour(): FloatArray {
    val vertices = floatArrayOf(0f, -1f, 1f, 0f, 0f, 1f, -1f, 0f)
    val contour = FloatArray(28 * 4)
    var side = 0
    while (side < 4) {
        val start = side * 2
        val next = ((side + 1) % 4) * 2
        val tangentX = vertices[next] - vertices[start]
        val tangentY = vertices[next + 1] - vertices[start + 1]
        var point = 0
        while (point < 7) {
            val fraction = point / 7f
            putContourPointFromTangent(contour, side * 7 + point,
                vertices[start] + tangentX * fraction,
                vertices[start + 1] + tangentY * fraction,
                tangentX, tangentY)
            point++
        }
        side++
    }
    return contour
}

private fun putContourPointFromTangent(contour: FloatArray, point: Int,
    x: Float, y: Float, tangentX: Float, tangentY: Float) {
    val length = sqrt(tangentX * tangentX + tangentY * tangentY)
    putContourPoint(contour, point, x, y, tangentY / length, -tangentX / length)
}

private fun putContourPoint(contour: FloatArray, point: Int,
    x: Float, y: Float, normalX: Float, normalY: Float) {
    val offset = point * 4
    contour[offset] = x
    contour[offset + 1] = y
    contour[offset + 2] = normalX
    contour[offset + 3] = normalY
}
