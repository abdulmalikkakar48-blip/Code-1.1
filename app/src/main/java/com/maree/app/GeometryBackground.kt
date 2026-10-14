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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/** Decorative drawing only: no input, layout changes or per-frame app recomposition. */
@Composable
internal fun SelkieGeometryBackground(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val phase = remember { mutableFloatStateOf(0f) }
    val water = remember { SelkieWaterParticleField() }
    val octagons = remember { SelkieOctagonField() }
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
            octagons.draw(this, phase.floatValue)
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
