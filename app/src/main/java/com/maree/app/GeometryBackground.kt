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
import kotlin.math.sin

/** Decorative drawing only: no input, layout changes or per-frame app recomposition. */
@Composable
internal fun SelkieGeometryBackground(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val phase = remember { mutableFloatStateOf(0f) }
    val water = remember { SelkieWaterParticleField() }
    val calmLines = remember { SelkieLightContours() }
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
            calmLines.draw(this, phase.floatValue, MareePalette.Teal.toArgb())
        }
    }
}

/** Six slow contour lines: one colour, no sparkling stars or scattered shapes. */
private class SelkieLightContours {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val path = Path()
    fun draw(scope: DrawScope, phase: Float, primary: Int) {
        if (scope.size.width <= 0f || scope.size.height <= 0f || !phase.isFinite()) return
        paint.color = primary
        paint.alpha = 22
        paint.strokeWidth = 1f * scope.density
        val time = phase * 6.2831853f
        scope.drawIntoCanvas { canvas ->
            repeat(6) { line ->
                path.rewind()
                val base = scope.size.height * (0.08f + line * 0.18f)
                for (step in 0..40) {
                    val x = scope.size.width * step / 40f
                    val y = base + sin(step / 40f * 4f + time + line * 0.7f) * 18f * scope.density
                    if (step == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                canvas.nativeCanvas.drawPath(path, paint)
            }
        }
    }
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
