package com.maree.app

import android.animation.ValueAnimator
import android.database.ContentObserver
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** Decorative drawing only: no input, layout changes or per-frame app recomposition. */
@Composable
internal fun SelkieGeometryBackground(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val phase = remember { mutableFloatStateOf(0f) }
    val polygon = remember { Path() }
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
                    phase.floatValue = ((initial + (now - started) / 24_000_000_000.0) % 1.0).toFloat()
                }
                // Slow decoration needs fewer redraws than interactive UI animations.
                delay(50L)
            }
        }
    }
    Canvas(modifier.clipToBounds().clearAndSetSemantics { }) {
        // Reading phase here invalidates drawing, rather than the screens or their layout.
        val angle = phase.floatValue * (2f * PI.toFloat())
        val drift = 12.dp.toPx()
        val blueOrGreen = MareePalette.Teal.copy(alpha = if (AppAppearance.isNight) 0.11f else 0.06f)
        val secondary = MareePalette.Muted.copy(alpha = if (AppAppearance.isNight) 0.08f else 0.05f)
        val stroke = Stroke(width = 1.dp.toPx())
        drawCircle(blueOrGreen, radius = size.width * 0.25f,
            center = Offset(size.width * 1.03f + sin(angle) * drift,
                size.height * 0.18f + cos(angle) * drift), style = stroke)
        drawCircle(secondary, radius = size.width * 0.14f,
            center = Offset(-size.width * 0.04f + cos(angle + 1f) * drift,
                size.height * 0.76f + sin(angle + 1f) * drift), style = stroke)

        val triangle = Offset(size.width * 0.03f + sin(angle + 2f) * drift,
            size.height * 0.44f + cos(angle + 2f) * drift)
        val side = 26.dp.toPx()
        polygon.reset()
        polygon.moveTo(triangle.x, triangle.y - side)
        polygon.lineTo(triangle.x + side, triangle.y + side * 0.7f)
        polygon.lineTo(triangle.x - side, triangle.y + side * 0.7f)
        polygon.close()
        rotate(sin(angle) * 7f, triangle) { drawPath(polygon, blueOrGreen, style = stroke) }

        val diamond = Offset(size.width * 0.97f + cos(angle - 1f) * drift,
            size.height * 0.84f + sin(angle - 1f) * drift)
        val radius = 22.dp.toPx()
        polygon.reset()
        polygon.moveTo(diamond.x, diamond.y - radius)
        polygon.lineTo(diamond.x + radius, diamond.y)
        polygon.lineTo(diamond.x, diamond.y + radius)
        polygon.lineTo(diamond.x - radius, diamond.y)
        polygon.close()
        rotate(cos(angle) * 10f, diamond) { drawPath(polygon, secondary, style = stroke) }
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
