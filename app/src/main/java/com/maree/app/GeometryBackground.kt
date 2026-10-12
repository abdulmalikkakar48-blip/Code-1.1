package com.maree.app

import android.animation.ValueAnimator
import android.database.ContentObserver
import android.graphics.Paint
import android.graphics.Path
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

/** Decorative drawing only: no input, layout changes or per-frame app recomposition. */
@Composable
internal fun SelkieGeometryBackground(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val phase = remember { mutableFloatStateOf(0f) }
    val water = remember { SelkieWaterParticleField() }
    val constellation = remember { SelkieLightConstellation() }
    var motionAllowed by remember { mutableStateOf(ValueAnimator.areAnimatorsEnabled()) }

    DisposableEffect(context, lifecycle) {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                // WindowManager may deliver its effective scale after the provider notification.
                Handler(Looper.getMainLooper()).post {
                    motionAllowed = ValueAnimator.areAnimatorsEnabled()
                }
            }
        }
        val removeNativeListener = if (Build.VERSION.SDK_INT >= 33)
            NativeAnimatorScale.observe { enabled -> motionAllowed = enabled } else null
        val registered = runCatching {
            context.contentResolver.registerContentObserver(
                Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE), false, observer)
        }.isSuccess
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) motionAllowed = ValueAnimator.areAnimatorsEnabled()
        }
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
            removeNativeListener?.invoke()
            if (registered) context.contentResolver.unregisterContentObserver(observer)
        }
    }
    LaunchedEffect(lifecycle, motionAllowed) {
        if (!motionAllowed) return@LaunchedEffect
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            val initial = phase.floatValue
            val started = withFrameNanos { it }
            while (isActive) {
                withFrameNanos { now ->
                    phase.floatValue = ((initial + (now - started) / 12_000_000_000.0) % 1.0).toFloat()
                }
                // Slow decoration needs fewer redraws than interactive UI animations.
                delay(50L)
            }
        }
    }
    Canvas(modifier.clipToBounds().clearAndSetSemantics { }) {
        // Reading phase here invalidates drawing, rather than the screens or their layout.
        if (AppAppearance.isNight) {
            // Preserve the existing night water, including its colour and opacity.
            val waterColor = MareePalette.Teal.copy(alpha = 0.32f)
            water.draw(this, phase.floatValue, waterColor)
        } else {
            constellation.draw(this, phase.floatValue, MareePalette.Teal.toArgb())
        }
    }
}

private const val LIGHT_SHAPE_COUNT = 48
private const val LIGHT_TURN = 6.283185307179586
// 30/255 < 0.12: even the darkest outline keeps small muted text above 4.5:1.
private const val LIGHT_MAX_ALPHA = 30

/**
 * A sparse, gently twinkling constellation for the light appearance.
 * Each outline stays inside its own cell, so overlapping shapes cannot darken text backgrounds.
 * Buffers, native paints and the path are reused; only the viewport/density changes the layout.
 */
private class SelkieLightConstellation {
    private val paints = arrayOf(
        lightOutlinePaint(0xFF174D38.toInt()),
        lightOutlinePaint(0xFF2A6688.toInt()),
        lightOutlinePaint(0xFF477B61.toInt())
    )
    private val path = Path()
    private val star = floatArrayOf(
        0f, -1f, 0.24f, -0.24f, 1f, 0f, 0.24f, 0.24f,
        0f, 1f, -0.24f, 0.24f, -1f, 0f, -0.24f, -0.24f
    )
    private val diamond = floatArrayOf(0f, -1f, 0.72f, 0f, 0f, 1f, -0.72f, 0f)
    private val centers = FloatArray(LIGHT_SHAPE_COUNT * 2)
    private val radii = FloatArray(LIGHT_SHAPE_COUNT)
    private val horizontalTravel = FloatArray(LIGHT_SHAPE_COUNT)
    private val verticalTravel = FloatArray(LIGHT_SHAPE_COUNT)
    private val offsets = DoubleArray(LIGHT_SHAPE_COUNT) { index ->
        unit(index, 7).toDouble() * LIGHT_TURN
    }
    private var cachedWidth = -1f
    private var cachedHeight = -1f
    private var cachedDensity = -1f
    private var primaryArgb = 0xFF174D38.toInt()

    fun draw(scope: DrawScope, phase: Float, primary: Int) {
        val width = scope.size.width
        val height = scope.size.height
        val density = scope.density
        if (!width.isFinite() || !height.isFinite() || !density.isFinite() || !phase.isFinite() ||
            width <= 0f || height <= 0f || density <= 0f) return
        // No compressed, overlapping pattern if this decorative Canvas is ever very small.
        if (min(width, height) < 120f * density) return
        if (width != cachedWidth || height != cachedHeight || density != cachedDensity) {
            prepareLayout(width, height, density)
        }
        if (primary != primaryArgb) {
            paints[0].color = primary
            primaryArgb = primary
        }

        val time = phase.toDouble() * LIGHT_TURN
        scope.drawIntoCanvas { canvas ->
            val nativeCanvas = canvas.nativeCanvas
            var index = 0
            while (index < LIGHT_SHAPE_COUNT) {
                val localTime = time + offsets[index]
                val x = centers[index * 2] +
                    (sin(localTime) * horizontalTravel[index]).toFloat()
                val y = centers[index * 2 + 1] +
                    (cos(localTime) * verticalTravel[index]).toFloat()
                // Two smooth brightening cycles per 12 seconds; never blink or disappear.
                val shimmer = (0.5 + 0.5 * sin(time * 2.0 + offsets[index])).toFloat()
                val paint = paints[index % paints.size]
                paint.alpha = (16f + (LIGHT_MAX_ALPHA - 16f) * shimmer).roundToInt()
                    .coerceIn(16, LIGHT_MAX_ALPHA)
                val radius = radii[index] * (0.95f + 0.05f * shimmer)
                when (index % 6) {
                    2 -> nativeCanvas.drawCircle(x, y, radius * 0.8f, paint)
                    else -> {
                        val coordinates = if (index % 6 == 1 || index % 6 == 4) diamond else star
                        // A small rocking angle adds motion without spinning the whole screen.
                        val angle = sin(localTime) * 0.32
                        val rotationCos = cos(angle).toFloat()
                        val rotationSin = sin(angle).toFloat()
                        path.rewind()
                        var vertex = 0
                        while (vertex < coordinates.size) {
                            val localX = coordinates[vertex] * radius
                            val localY = coordinates[vertex + 1] * radius
                            val pointX = x + localX * rotationCos - localY * rotationSin
                            val pointY = y + localX * rotationSin + localY * rotationCos
                            if (vertex == 0) path.moveTo(pointX, pointY) else path.lineTo(pointX, pointY)
                            vertex += 2
                        }
                        path.close()
                        nativeCanvas.drawPath(path, paint)
                    }
                }
                index++
            }
        }
    }

    private fun prepareLayout(width: Float, height: Float, density: Float) {
        val columns = if (width > height) 8 else 6
        val rows = LIGHT_SHAPE_COUNT / columns
        val cellWidth = width / columns
        val cellHeight = height / rows
        val strokeWidth = 0.95f * density
        paints.forEach { it.strokeWidth = strokeWidth }
        var index = 0
        while (index < LIGHT_SHAPE_COUNT) {
            val jitterX = (unit(index, 1) - 0.5f) * cellWidth * 0.05f
            val jitterY = (unit(index, 2) - 0.5f) * cellHeight * 0.08f
            val preferredRadius = (3f + 4f * unit(index, 3)) * density
            val availableRadius = min(
                cellWidth * 0.5f - kotlin.math.abs(jitterX),
                cellHeight * 0.5f - kotlin.math.abs(jitterY)
            ) - strokeWidth - 2f * density
            val radius = min(preferredRadius, max(0f, availableRadius))
            centers[index * 2] = (index % columns + 0.5f) * cellWidth + jitterX
            centers[index * 2 + 1] = (index / columns + 0.5f) * cellHeight + jitterY
            radii[index] = radius
            // Reserve the full rotated radius, stroke and a gutter inside each cell.
            val gutter = radius + strokeWidth + 2f * density
            val freeX = max(0f, cellWidth * 0.5f - kotlin.math.abs(jitterX) - gutter)
            val freeY = max(0f, cellHeight * 0.5f - kotlin.math.abs(jitterY) - gutter)
            horizontalTravel[index] = min((18f + 10f * unit(index, 4)) * density, freeX)
            verticalTravel[index] = min((15f + 12f * unit(index, 5)) * density, freeY)
            index++
        }
        cachedWidth = width
        cachedHeight = height
        cachedDensity = density
    }

    private fun unit(index: Int, salt: Int): Float {
        // Deterministic placement: a recomposition or language change never reshuffles the sky.
        val value = (index + 1) * 1103515245 + salt * 12345
        return ((value xor (value ushr 16)) and 0xffff).toFloat() / 65535f
    }
}

private fun lightOutlinePaint(argb: Int): Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = argb
    style = Paint.Style.STROKE
    strokeCap = Paint.Cap.ROUND
    strokeJoin = Paint.Join.ROUND
}

/** Observe the framework's effective value, including accessibility and battery-saving overrides. */
@RequiresApi(33)
private object NativeAnimatorScale {
    fun observe(onChange: (Boolean) -> Unit): () -> Unit {
        val listener = object : ValueAnimator.DurationScaleChangeListener {
            override fun onChanged(durationScale: Float) { onChange(durationScale > 0f) }
        }
        ValueAnimator.registerDurationScaleChangeListener(listener)
        return { ValueAnimator.unregisterDurationScaleChangeListener(listener) }
    }
}
