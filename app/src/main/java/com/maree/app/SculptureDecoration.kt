package com.maree.app

import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.SweepGradient
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.semantics.clearAndSetSemantics
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/** Actual drawing limits of the background; ribbon sums conservatively bound nested passes. */
internal object AmbientStyle {
    const val LILAC_COLOR: Long = 0xFFB8A0ECL
    const val LILAC_MAX_ALPHA = 24
    const val PEACH_COLOR: Long = 0xFFF2AA8DL
    const val PEACH_MAX_ALPHA = 14
    val LILAC_RIBBON_ALPHAS = intArrayOf(4, 6, 10, 14, 19)
    val PEACH_RIBBON_ALPHAS = intArrayOf(2, 4, 6, 8, 11)
    const val LILAC_RIBBON_MAX_ALPHA = 53
    const val PEACH_RIBBON_MAX_ALPHA = 31
}

/** Slow satin waves: broad, feathered filled bands, no particles, narrow lines or flashing. */
internal class SelkieAmbientField {
    private fun wash(color: Long, alpha: Int) = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        val rgb = color.toInt() and 0xFFFFFF
        shader = RadialGradient(0f, 0f, 1f, intArrayOf(rgb or (alpha shl 24), rgb),
            floatArrayOf(0f, 1f), Shader.TileMode.CLAMP)
    }
    private val lilac = wash(AmbientStyle.LILAC_COLOR, AmbientStyle.LILAC_MAX_ALPHA)
    private val peach = wash(AmbientStyle.PEACH_COLOR, AmbientStyle.PEACH_MAX_ALPHA)
    private val ribbon = Path()
    private val featherWidths = floatArrayOf(1f, 0.82f, 0.66f, 0.5f, 0.34f)
    private fun silk(color: Long) = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        val rgb = color.toInt() and 0xFFFFFF
        shader = LinearGradient(-0.25f, 0f, 1.25f, 0f,
            intArrayOf(rgb, rgb or 0xFF000000.toInt(), rgb or 0xFF000000.toInt(), rgb),
            floatArrayOf(0f, 0.25f, 0.75f, 1f), Shader.TileMode.CLAMP)
    }
    private val lilacSilk = silk(AmbientStyle.LILAC_COLOR)
    private val peachSilk = silk(AmbientStyle.PEACH_COLOR)

    fun draw(scope: DrawScope, phase: Float) {
        if (scope.size.width <= 0 || scope.size.height <= 0 || !phase.isFinite()) return
        val t = (phase * PI * 2).toFloat()
        scope.drawIntoCanvas { canvas ->
            val native = canvas.nativeCanvas
            native.save()
            native.translate(scope.size.width * (0.9f + sin(t) * 0.10f), scope.size.height * 0.16f)
            native.scale(scope.size.width * 1.1f, scope.size.height * 0.65f)
            native.drawCircle(0f, 0f, 1f, lilac)
            native.restore()
            native.save()
            native.translate(scope.size.width * 0.05f, scope.size.height * (0.74f + sin(t) * 0.075f))
            native.scale(scope.size.width * 1.2f, scope.size.height * 0.55f)
            native.drawCircle(0f, 0f, 1f, peach)
            native.restore()
            native.save()
            native.scale(scope.size.width, scope.size.height)
            drawSilk(native, lilacSilk, AmbientStyle.LILAC_RIBBON_ALPHAS,
                center = 0.17f + 0.10f * sin(t),
                bendA = -0.20f + 0.025f * cos(2f * t),
                bendB = 0.18f + 0.06f * sin(t + 1.1f),
                slope = 0.08f * cos(t + 0.4f), width = 0.25f + 0.025f * cos(2f * t))
            drawSilk(native, peachSilk, AmbientStyle.PEACH_RIBBON_ALPHAS,
                center = 0.69f + 0.11f * cos(t + 0.7f),
                bendA = 0.19f + 0.025f * sin(2f * t),
                bendB = -0.19f + 0.05f * cos(t + 1.8f),
                slope = -0.05f * sin(t), width = 0.31f + 0.02f * sin(2f * t + 0.6f))
            native.restore()
        }
    }

    private fun drawSilk(canvas: android.graphics.Canvas, paint: Paint, alphas: IntArray,
                         center: Float, bendA: Float, bendB: Float, slope: Float, width: Float) {
        // Nested filled bands approximate a soft feather without blur layers or per-frame allocation.
        for (index in featherWidths.indices) {
            val half = width * featherWidths[index] / 2f
            ribbon.rewind()
            ribbon.moveTo(-0.25f, center - half)
            ribbon.cubicTo(0.10f, center + bendA - half, 0.74f, center + bendB - half,
                1.25f, center + slope - half)
            ribbon.lineTo(1.25f, center + slope + half)
            ribbon.cubicTo(0.74f, center + bendB + half, 0.10f, center + bendA + half,
                -0.25f, center + half)
            ribbon.close()
            paint.alpha = alphas[index]
            canvas.drawPath(ribbon, paint)
        }
    }
}

/** Original illustration: shaded ceramic ring, peach sphere and mint beads. No image or network. */
@Composable
internal fun SelkieSculpture(modifier: Modifier = Modifier) {
    val phase = LocalDecorationPhase.current
    val sculpture = remember { SelkieSculptureRenderer() }
    Canvas(modifier.clearAndSetSemantics { }) { sculpture.draw(this, phase.value) }
}

private class SelkieSculptureRenderer {
    private val ringBounds = RectF(-82f, -57f, 82f, 57f)
    private val capsuleBounds = RectF(-12f, -25f, 12f, 25f)
    private val shadow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        shader = RadialGradient(0f, 0f, 1f,
            intArrayOf(0x302B194F, 0x002B194F), null, Shader.TileMode.CLAMP)
    }
    private val ringOuter = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 36f
        shader = SweepGradient(0f, 0f, intArrayOf(0xFF8756BF.toInt(), 0xFF69439D.toInt(),
            0xFFBD99E8.toInt(), 0xFFF4E7FF.toInt(), 0xFF8756BF.toInt()),
            floatArrayOf(0f, 0.25f, 0.55f, 0.77f, 1f))
    }
    private val ringInner = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 23f
        shader = LinearGradient(-65f, -60f, 65f, 62f,
            intArrayOf(0xFFF7EEFF.toInt(), 0xFFC6A6F1.toInt(), 0xFF9568CA.toInt()),
            floatArrayOf(0f, 0.45f, 1f), Shader.TileMode.CLAMP)
    }
    private val ringHighlight = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xBCFFFFFF.toInt()
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 2f
    }
    private val peach = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        shader = RadialGradient(-14f, -17f, 78f,
            intArrayOf(0xFFFFF4DA.toInt(), 0xFFF2B684.toInt(), 0xFFB96B58.toInt()),
            floatArrayOf(0f, 0.55f, 1f), Shader.TileMode.CLAMP)
    }
    private val mint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        shader = RadialGradient(-9f, -11f, 46f,
            intArrayOf(0xFFF4FFF2.toInt(), 0xFFA7D6BC.toInt(), 0xFF4D9589.toInt()),
            floatArrayOf(0f, 0.55f, 1f), Shader.TileMode.CLAMP)
    }
    private val lilac = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        shader = RadialGradient(-5f, -6f, 25f,
            intArrayOf(0xFFFAF5FF.toInt(), 0xFFAC91DA.toInt(), 0xFF7553AC.toInt()),
            floatArrayOf(0f, 0.55f, 1f), Shader.TileMode.CLAMP)
    }
    private val capsule = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        shader = LinearGradient(-12f, -20f, 14f, 24f,
            intArrayOf(0xFFEEFFF1.toInt(), 0xFFB8DFC2.toInt(), 0xFF79B19B.toInt()),
            floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)
    }

    fun draw(scope: DrawScope, phase: Float) {
        if (scope.size.width <= 0f || scope.size.height <= 0f || !phase.isFinite()) return
        val scale = min(scope.size.width / 300f, scope.size.height / 240f)
        val wave = sin(phase * (2 * PI).toFloat())
        scope.drawIntoCanvas { canvas ->
            val n = canvas.nativeCanvas
            n.save()
            n.translate((scope.size.width - 300f * scale) / 2, (scope.size.height - 240f * scale) / 2)
            n.scale(scale, scale)
            n.save()
            n.translate(159f, 214f)
            n.scale(110f, 15f)
            n.drawCircle(0f, 0f, 1f, shadow)
            n.restore()
            n.save()
            n.translate(149f, 125f + wave * 5f)
            n.rotate(-31f + wave * 3f)
            n.drawOval(ringBounds, ringOuter)
            n.drawOval(ringBounds, ringInner)
            n.drawArc(ringBounds, 205f, 61f, false, ringHighlight)
            n.restore()
            n.save()
            n.translate(226f, 68f - wave * 4f)
            n.drawCircle(0f, 0f, 41f, peach)
            n.restore()
            n.save()
            n.translate(70f, 177f - wave * 3f)
            n.drawCircle(0f, 0f, 27f, mint)
            n.restore()
            n.save()
            n.translate(246f, 183f + wave * 4f)
            n.drawCircle(0f, 0f, 14f, lilac)
            n.restore()
            n.save()
            n.translate(58f, 58f + wave * 4f)
            n.rotate(35f - wave * 5f)
            n.drawRoundRect(capsuleBounds, 12f, 12f, capsule)
            n.restore()
            n.restore()
        }
    }
}
