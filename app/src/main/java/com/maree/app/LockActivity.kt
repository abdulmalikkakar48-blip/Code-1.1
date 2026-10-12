package com.maree.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color as AndroidColor
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button as ComposeButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

fun goToHome(context: Context) {
    val intent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_HOME)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}

fun remainingTime(until: Long): String {
    val total = ((until - System.currentTimeMillis()).coerceAtLeast(0) + 999) / 1000
    val hours = total / 3600
    val minutes = (total % 3600) / 60
    val seconds = total % 60
    return "%02d h %02d min %02d s".format(AppLanguage.locale, hours, minutes, seconds)
}

/** Écran de secours pour une éventuelle ouverture via activité. Le service utilise une surcouche. */
class LockActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val name = intent.getStringExtra("name") ?: tr("Cette application", "This app")
        val until = intent.getLongExtra("until", System.currentTimeMillis())
        @Suppress("DEPRECATION")
        val points = (intent.extras?.get("points") as? Number)?.toDouble() ?: 100.0
        setContent {
            LockScreen(name, until, points) { goToHome(this); finish() }
        }
    }
}

@Composable
private fun LockScreen(name: String, until: Long, points: Double, leave: () -> Unit) {
    BackHandler(onBack = leave)
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFAD2639)).padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("SELKIE", color = Color.White.copy(alpha = 0.8f), fontSize = 18.sp)
        Spacer(Modifier.height(36.dp))
        Text(tr("Le moment de\ndéconnecter.", "Time to\ndisconnect."), color = Color.White,
            fontWeight = FontWeight.Bold, fontSize = 35.sp, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        Text(tr("$name est en pause pendant 24 h.", "$name is paused for 24 hours."), color = Color.White,
            textAlign = TextAlign.Center, fontSize = 18.sp)
        Spacer(Modifier.height(32.dp))
        Text(tr("Déblocage dans ${remainingTime(until)}", "Unlocks in ${remainingTime(until)}"), color = Color.White, fontSize = 17.sp)
        Spacer(Modifier.height(12.dp))
        Text(tr("${formatPoints(points)} points d’honneur", "${formatPoints(points)} discipline points"), color = Color.White, fontSize = 16.sp)
        Spacer(Modifier.height(36.dp))
        ComposeButton(onClick = leave) { Text(tr("Retour à l’accueil", "Back to home")) }
    }
}

/** Fenêtre TYPE_APPLICATION_OVERLAY, car Android limite l'ouverture d'Activity en arrière-plan. */
class LockOverlay(private val context: Context) {
    private val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val handler = Handler(Looper.getMainLooper())
    private var view: FrameLayout? = null
    private var caption: TextView? = null
    private var headline: TextView? = null
    private var exitButton: Button? = null
    private var countdown: TextView? = null
    private var honor: TextView? = null
    private var appName = ""
    private var lockUntil = 0L
    private var points = 0.0

    private val refresh = object : Runnable {
        override fun run() {
            updateText()
            handler.postDelayed(this, 1000)
        }
    }

    fun show(name: String, until: Long, currentPoints: Double) {
        appName = name
        lockUntil = until
        points = currentPoints
        if (view == null) create()
        updateText()
    }

    private fun updateText() {
        headline?.text = tr("Le moment de\ndéconnecter.", "Time to\ndisconnect.")
        exitButton?.text = tr("Retour à l’accueil", "Back to home")
        caption?.text = tr("$appName est en pause pendant 24 h.", "$appName is paused for 24 hours.")
        countdown?.text = tr("Déblocage dans ${remainingTime(lockUntil)}", "Unlocks in ${remainingTime(lockUntil)}")
        honor?.text = tr("${formatPoints(points)} points d’honneur", "${formatPoints(points)} discipline points")
    }

    private fun makeText(text: String, size: Float, bold: Boolean = false): TextView =
        TextView(context).apply {
            this.text = text
            setTextColor(AndroidColor.WHITE)
            textSize = size
            gravity = Gravity.CENTER
            if (bold) typeface = Typeface.DEFAULT_BOLD
            setPadding(14, 14, 14, 14)
        }

    private fun create() {
        val root = object : FrameLayout(context) {
            override fun dispatchKeyEvent(event: KeyEvent): Boolean {
                if (event.keyCode == KeyEvent.KEYCODE_BACK) {
                    if (event.action == KeyEvent.ACTION_UP) exitToHome()
                    return true
                }
                return super.dispatchKeyEvent(event)
            }
        }.apply {
            setBackgroundColor(AndroidColor.rgb(173, 38, 57))
            isFocusable = true
            isFocusableInTouchMode = true
        }
        val column = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 50, 28, 50)
        }
        column.addView(makeText("SELKIE", 19f))
        headline = makeText(tr("Le moment de\ndéconnecter.", "Time to\ndisconnect."), 34f, true)
            .also { column.addView(it) }
        caption = makeText("", 18f).also { column.addView(it) }
        countdown = makeText("", 18f, true).also { column.addView(it) }
        honor = makeText("", 17f).also { column.addView(it) }
        val button = Button(context).apply {
            text = tr("Retour à l’accueil", "Back to home")
            setTextColor(AndroidColor.rgb(30, 45, 50))
            setBackgroundColor(AndroidColor.WHITE)
            isAllCaps = false
            setOnClickListener { exitToHome() }
        }.also { exitButton = it }
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(26, 44, 26, 0) }
        column.addView(button, params)
        root.addView(column, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        ))
        val overlayParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.START }
        try {
            manager.addView(root, overlayParams)
            view = root
            root.requestFocus()
            handler.removeCallbacks(refresh)
            handler.post(refresh)
        } catch (_: Exception) {
            // Permission potentiellement révoquée pendant le suivi.
            view = null
            caption = null
            headline = null
            exitButton = null
            countdown = null
            honor = null
        }
    }

    private fun exitToHome() {
        try { goToHome(context) } catch (_: Exception) { }
        hide()
    }

    fun hide() {
        handler.removeCallbacks(refresh)
        view?.let { try { manager.removeViewImmediate(it) } catch (_: Exception) { } }
        view = null
        caption = null
        headline = null
        exitButton = null
        countdown = null
        honor = null
    }
}
