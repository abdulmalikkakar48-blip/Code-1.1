package com.maree.app

import android.Manifest
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import android.widget.ImageView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

private val Background = MareePalette.Background
private val CardColor = MareePalette.Surface
private val CardLighter = MareePalette.Subtle
private val Mint = MareePalette.Teal
private val Cream = MareePalette.Ink
private val Muted = MareePalette.Muted
private val Warning = MareePalette.Coral

data class InstalledApp(val pkg: String, val label: String)

fun installedLaunchableApps(context: android.content.Context): List<InstalledApp> {
    val launcher = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
    @Suppress("DEPRECATION")
    val matches: List<ResolveInfo> = context.packageManager.queryIntentActivities(launcher, 0)
    return matches.mapNotNull { info ->
        val pkg = info.activityInfo?.packageName ?: return@mapNotNull null
        if (pkg == context.packageName) return@mapNotNull null
        InstalledApp(pkg, info.loadLabel(context.packageManager)?.toString() ?: pkg)
    }.distinctBy { it.pkg }.sortedBy { it.label.lowercase(Locale.getDefault()) }
}

fun appLabel(context: android.content.Context, pkg: String): String = try {
    context.packageManager.getApplicationLabel(context.packageManager.getApplicationInfo(pkg, 0)).toString()
} catch (_: Exception) { pkg.substringAfterLast('.') }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MareeTheme { MareeApp() } }
    }

    override fun onResume() {
        super.onResume()
        MonitorService.start(this)
    }
}

@Composable
private fun MareeApp() {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var storeGeneration by remember { mutableIntStateOf(0) }
    val store = remember(storeGeneration) { Store(context) }
    val reader = remember { UsageReader(context) }
    val setup = remember { SetupPreferences(context) }
    val scope = rememberCoroutineScope()
    val snackbars = remember { SnackbarHostState() }
    var setupComplete by remember { mutableStateOf(setup.isCompleted()) }
    var monitoringEnabled by remember { mutableStateOf(setup.isMonitoringEnabled()) }
    var permissions by remember { mutableStateOf(PermissionStatus.read(context)) }
    var showSetup by rememberSaveable { mutableStateOf(!setupComplete) }
    var showPrivacy by rememberSaveable { mutableStateOf(false) }
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var notificationAsked by rememberSaveable { mutableStateOf(false) }
    var installed by remember { mutableStateOf<List<InstalledApp>?>(null) }
    var installedLoadFailed by remember { mutableStateOf(false) }
    var installedLoadKey by remember { mutableIntStateOf(0) }
    var refreshKey by remember { mutableIntStateOf(0) }
    var lastReading by remember(storeGeneration) { mutableStateOf(UsageReading(emptyMap(), null)) }
    var snapshot by remember(storeGeneration) {
        mutableStateOf(store.readSnapshot(UsageReading(emptyMap(), null)))
    }
    var showPicker by remember { mutableStateOf(false) }
    var pendingAdd by remember { mutableStateOf(false) }
    var editApp by remember { mutableStateOf<AppSnapshot?>(null) }
    var removeApp by remember { mutableStateOf<AppSnapshot?>(null) }
    var resetApp by remember { mutableStateOf<AppSnapshot?>(null) }
    var infoMessage by remember { mutableStateOf<String?>(null) }
    var deleteConfirmation by remember { mutableStateOf(false) }
    var deleting by remember { mutableStateOf(false) }

    fun display(text: String) { scope.launch { snackbars.showSnackbar(text) } }
    fun refresh() { refreshKey++ }
    fun refreshPermissions() { permissions = PermissionStatus.read(context); refresh() }
    val settingsRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { refreshPermissions() }
    val notificationRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { refreshPermissions() }

    fun openSettings(intent: Intent) {
        try { settingsRequest.launch(intent) }
        catch (_: android.content.ActivityNotFoundException) {
            infoMessage = "Ouvre les paramètres Android, puis Applications → Marée, pour gérer les autorisations."
        }
    }
    fun askNotifications() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (notificationAsked) {
                openSettings(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName))
            } else {
                notificationAsked = true
                notificationRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    fun addApplication() {
        if (setup.isMonitoringEnabled() && permissions.readyForMonitoring) showPicker = true
        else { pendingAdd = true; showSetup = true }
    }
    fun finishSetup(enableMonitoring: Boolean) {
        val current = PermissionStatus.read(context)
        permissions = current
        if (enableMonitoring && !current.readyForMonitoring) return
        setup.complete(enableMonitoring)
        setupComplete = true
        monitoringEnabled = enableMonitoring
        showSetup = false
        tab = 0
        if (enableMonitoring) MonitorService.start(context) else MonitorService.stop(context)
        if (pendingAdd && enableMonitoring) showPicker = true
        pendingAdd = false
        refresh()
    }

    BackHandler(enabled = showSetup && setupComplete && !showPrivacy) {
        showSetup = false
        pendingAdd = false
    }
    LaunchedEffect(showPicker, installedLoadKey) {
        if (!showPicker) return@LaunchedEffect
        installed = null
        installedLoadFailed = false
        try {
            installed = withContext(Dispatchers.IO) { installedLaunchableApps(context) }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) { installedLoadFailed = true }
    }
    LaunchedEffect(lifecycle, refreshKey, setupComplete, monitoringEnabled, showSetup, storeGeneration) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {
                permissions = PermissionStatus.read(context)
                if (setupComplete && !showSetup) {
                    try {
                        val (reading, currentSnapshot) = withContext(Dispatchers.IO) {
                            val currentReading = if (setup.isMonitoringEnabled() && permissions.readyForMonitoring)
                                reader.read(store.packages()) else lastReading
                            currentReading to store.readSnapshot(currentReading)
                        }
                        lastReading = reading
                        snapshot = currentSnapshot
                        if (setup.isMonitoringEnabled() && permissions.readyForMonitoring)
                            MonitorService.start(context)
                    } catch (cancelled: CancellationException) {
                        throw cancelled
                    } catch (_: Exception) { }
                }
                delay(2500L)
            }
        }
    }

    when {
        showPrivacy -> PrivacyScreen(
            onBack = { showPrivacy = false },
            onDeleteData = { deleteConfirmation = true }
        )
        showSetup || !setupComplete -> OnboardingScreen(
            status = permissions,
            onUsagePermission = { openSettings(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)) },
            onOverlayPermission = {
                openSettings(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${context.packageName}")))
            },
            onNotificationPermission = ::askNotifications,
            onPrivacy = { showPrivacy = true },
            onComplete = { finishSetup(true) },
            onExplore = { finishSetup(false) }
        )
        else -> Scaffold(
            containerColor = Background,
            snackbarHost = { SnackbarHost(snackbars) },
            bottomBar = {
                NavigationBar(containerColor = CardColor, tonalElevation = 0.dp) {
                    listOf(
                        Triple("Accueil", R.drawable.ic_nav_home, 0),
                        Triple("Quotidien", R.drawable.ic_nav_daily, 1),
                        Triple("Défis", R.drawable.ic_nav_challenges, 2),
                        Triple("Plus", R.drawable.ic_nav_more, 3)
                    ).forEach { (name, icon, index) ->
                        NavigationBarItem(
                            selected = tab == index, onClick = { tab = index },
                            icon = { Icon(painterResource(icon), contentDescription = null, modifier = Modifier.size(23.dp)) },
                            label = { Text(name, fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Mint, selectedTextColor = Mint,
                                indicatorColor = CardLighter, unselectedIconColor = Muted,
                                unselectedTextColor = Muted
                            )
                        )
                    }
                }
            }
        ) { inner ->
            Column(Modifier.fillMaxSize().padding(inner).statusBarsPadding()) {
                Row(
                    Modifier.fillMaxWidth().padding(start = 22.dp, end = 22.dp, top = 16.dp, bottom = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("≋", color = Mint, fontSize = 38.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(9.dp))
                        Text("marée", color = Cream, fontSize = 29.sp, fontWeight = FontWeight.Bold)
                    }
                    Surface(color = MareePalette.Sand, shape = CircleShape) {
                        Text("✧ ${snapshot.points}", color = MareePalette.Amber, fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 15.dp, vertical = 9.dp))
                    }
                }
                when (tab) {
                    0 -> HomeTab(
                        snapshot = snapshot,
                        active = monitoringEnabled && permissions.readyForMonitoring,
                        onSetup = { showSetup = true },
                        onAdd = ::addApplication, onEdit = { editApp = it },
                        onRemove = { removeApp = it },
                        onReset = {
                            if (monitoringEnabled && permissions.readyForMonitoring) resetApp = it
                            else { showSetup = true }
                        },
                        getName = { appLabel(context, it) }
                    )
                    1 -> DailyTab(snapshot, onCoach = {
                        infoMessage = "Le coach est en préparation. En attendant, retrouve chaque jour un conseil dans Quotidien. Aucun message n’est envoyé à une IA dans cette version."
                    }, getName = { appLabel(context, it) }, onAddApp = ::addApplication,
                        monitoringActive = monitoringEnabled && permissions.readyForMonitoring,
                        onActivateTracking = { showSetup = true })
                    2 -> ChallengeTab(snapshot) { index ->
                        if (store.completeChallenge(index)) refresh()
                    }
                    else -> MoreTab(
                        active = monitoringEnabled && permissions.readyForMonitoring,
                        notificationsGranted = permissions.notificationsGranted,
                        onSetup = { showSetup = true },
                        onPause = {
                            setup.setMonitoringEnabled(false)
                            monitoringEnabled = false
                            MonitorService.stop(context)
                            refresh()
                        },
                        onNotifications = ::askNotifications,
                        onPrivacy = { showPrivacy = true },
                        onPremium = { infoMessage = "Une offre Premium est à l’étude. Aucun achat ni abonnement n’est disponible dans cette version." },
                        onAccount = { infoMessage = "Marée fonctionne sans compte : pas d’inscription et tes données restent sur ton téléphone. Un compte pourra être proposé plus tard, uniquement pour une sauvegarde facultative." }
                    )
                }
            }
        }
    }

    if (showPicker && !showSetup && !showPrivacy) {
        AppPickerDialog(
            apps = installed?.filter { item -> snapshot.apps.none { it.packageName == item.pkg } },
            loadFailed = installedLoadFailed, onRetry = { installedLoadKey++ },
            onDismiss = { showPicker = false },
            onConfirm = { pkg, minutes ->
                val operationGeneration = storeGeneration
                val operationStore = store
                scope.launch {
                    val time = withContext(Dispatchers.IO) {
                        reader.read(setOf(pkg)).elapsedMillis[pkg] ?: 0L
                    }
                    // Une suppression ou une pause annule les actions encore en lecture.
                    if (operationGeneration != storeGeneration || deleting ||
                        !setup.isMonitoringEnabled() || !PermissionStatus.read(context).readyForMonitoring)
                        return@launch
                    val issue = operationStore.addApp(pkg, minutes, time)
                    showPicker = false
                    if (issue != null) display(issue)
                    refresh()
                }
            }
        )
    }
    editApp?.let { item ->
        MinutesDialog("Modifier la limite de ${appLabel(context, item.packageName)}", item.limitMinutes,
            onDismiss = { editApp = null }, onConfirm = { mins ->
                store.editLimit(item.packageName, mins)?.let(::display)
                editApp = null
                refresh()
            })
    }
    removeApp?.let { item ->
        AlertDialog(onDismissRequest = { removeApp = null },
            title = { Text("Retirer ${appLabel(context, item.packageName)} ?") },
            text = { Text("Cette application et son verrou seront supprimés de Marée.") },
            confirmButton = { Button(onClick = {
                store.removeApp(item.packageName); removeApp = null; refresh()
            }) { Text("Retirer") } },
            dismissButton = { TextButton(onClick = { removeApp = null }) { Text("Annuler") } })
    }
    resetApp?.let { item ->
        AlertDialog(onDismissRequest = { resetApp = null },
            title = { Text("Réinitialiser la limite ?") },
            text = { Text("Le verrou sera levé et la limite complète sera rendue. Coût : 10 points.") },
            confirmButton = { Button(onClick = {
                val operationGeneration = storeGeneration
                val operationStore = store
                scope.launch {
                    val nowUsed = withContext(Dispatchers.IO) {
                        reader.read(setOf(item.packageName)).elapsedMillis[item.packageName] ?: 0L
                    }
                    if (operationGeneration != storeGeneration || deleting ||
                        !setup.isMonitoringEnabled() || !PermissionStatus.read(context).readyForMonitoring)
                        return@launch
                    val issue = operationStore.resetApp(item.packageName, nowUsed)
                    resetApp = null
                    if (issue != null) display(issue)
                    refresh()
                }
            }) { Text("Réinitialiser (−10)") } },
            dismissButton = { TextButton(onClick = { resetApp = null }) { Text("Annuler") } })
    }
    infoMessage?.let { message ->
        AlertDialog(onDismissRequest = { infoMessage = null },
            title = { Text("Marée") }, text = { Text(message) },
            confirmButton = { TextButton(onClick = { infoMessage = null }) { Text("Compris") } })
    }
    if (deleteConfirmation) {
        AlertDialog(onDismissRequest = { if (!deleting) deleteConfirmation = false },
            title = { Text("Effacer les données de Marée ?") },
            text = { Text("Tes applications suivies, limites, verrous, points, défis et choix d’accueil seront effacés. Le suivi sera arrêté. Cette action est irréversible et ne supprime pas les statistiques Android.") },
            confirmButton = { Button(enabled = !deleting, onClick = {
                scope.launch {
                    deleting = true
                    setup.reset()
                    monitoringEnabled = false
                    MonitorService.stop(context)
                    val cleared = withContext(Dispatchers.IO) { store.clearPersonalData() }
                    deleting = false
                    deleteConfirmation = false
                    if (cleared) {
                        storeGeneration++
                        setupComplete = false
                        showPrivacy = false
                        showSetup = true
                        showPicker = false
                        pendingAdd = false
                        editApp = null; removeApp = null; resetApp = null
                        tab = 0
                        refresh()
                    } else {
                        infoMessage = "L’effacement n’a pas pu être enregistré. Le suivi est arrêté. Réessaie depuis Confidentialité."
                    }
                }
            }, colors = ButtonDefaults.buttonColors(containerColor = MareePalette.Danger)) {
                Text(if (deleting) "Effacement…" else "Tout effacer")
            } },
            dismissButton = { TextButton(enabled = !deleting, onClick = { deleteConfirmation = false }) { Text("Annuler") } })
    }
}

@Composable
private fun HomeTab(
    snapshot: MareeSnapshot, active: Boolean, onSetup: () -> Unit,
    onAdd: () -> Unit, onEdit: (AppSnapshot) -> Unit, onRemove: (AppSnapshot) -> Unit,
    onReset: (AppSnapshot) -> Unit, getName: (String) -> String
) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(MareePalette.DeepTeal, Mint)))
            .padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("DU TEMPS POUR TOI", color = MareePalette.Mint, fontSize = 11.sp,
                fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            Text("Moins d’écran.\nPlus de vie.", fontSize = 32.sp, lineHeight = 38.sp,
                fontWeight = FontWeight.Bold, color = Color.White)
            Text("Chaque minute retrouvée compte.", color = Color(0xFFE1F5EA),
                fontSize = 14.sp, lineHeight = 21.sp)
        }
        Card(colors = CardDefaults.cardColors(containerColor = MareePalette.Peach),
            shape = RoundedCornerShape(24.dp)) {
            Row(Modifier.fillMaxWidth().padding(22.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("TON CAPITAL DISCIPLINE", color = MareePalette.Coral, fontSize = 10.sp,
                        fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("${snapshot.points}", color = MareePalette.Coral, fontSize = 43.sp,
                            fontWeight = FontWeight.Bold)
                        Text(" points", color = Cream, modifier = Modifier.padding(bottom = 9.dp))
                    }
                    Text("Un pas à la fois, à ton rythme.", color = Muted, fontSize = 13.sp)
                }
                Text("✧", fontSize = 45.sp, color = MareePalette.Coral)
            }
        }
        if (!active) {
            SectionCard {
                Text("Le suivi est en pause", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text("Active les autorisations pour mesurer tes limites et afficher les écrans de pause. Les compteurs ne sont pas actualisés pendant la pause.",
                    color = Muted, fontSize = 13.sp, lineHeight = 19.sp)
                Button(onClick = onSetup) { Text("Configurer le suivi") }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Mes applications", color = Cream, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("${snapshot.apps.size} / ${if (snapshot.premium) "∞" else "5"} suivies", color = Muted, fontSize = 13.sp)
            }
            Button(onClick = onAdd, shape = RoundedCornerShape(16.dp),
                enabled = snapshot.premium || (snapshot.points > 0 && snapshot.apps.size < 5)) {
                Text("＋ Ajouter", fontWeight = FontWeight.Bold)
            }
        }
        if (snapshot.points == 0 && !snapshot.premium) {
            Text("Tes défis te permettent de regagner des points pour modifier tes limites.", color = Warning, fontSize = 13.sp)
        }
        if (snapshot.apps.isEmpty()) {
            SectionCard {
                Text("Ta première limite", fontWeight = FontWeight.Bold, fontSize = 19.sp)
                Text("Choisis une application et le temps que tu souhaites lui consacrer. Tu gardes la main sur tes réglages.",
                    color = Muted, fontSize = 14.sp, lineHeight = 21.sp)
                OutlinedButton(onClick = onAdd, enabled = snapshot.points > 0 || snapshot.premium) {
                    Text("Choisir une application")
                }
            }
        } else snapshot.apps.forEach { app ->
            AppCard(app, getName(app.packageName), snapshot.premium || snapshot.points > 0, active,
                onEdit = { onEdit(app) }, onRemove = { onRemove(app) }, onReset = { onReset(app) })
        }
        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun SectionCard(content: @Composable ColumnScope.() -> Unit) {
    Card(shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardColor)) {
        Column(Modifier.fillMaxWidth().padding(19.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp), content = content)
    }
}

@Composable
private fun AppCard(
    app: AppSnapshot, name: String, canEdit: Boolean, active: Boolean,
    onEdit: () -> Unit, onRemove: () -> Unit, onReset: () -> Unit
) {
    val context = LocalContext.current
    SectionCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AndroidView(factory = { ImageView(context).apply {
                try { setImageDrawable(context.packageManager.getApplicationIcon(app.packageName)) }
                catch (_: Exception) { setImageResource(R.drawable.ic_maree) }
            } }, modifier = Modifier.size(45.dp).clip(RoundedCornerShape(12.dp)))
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.SemiBold, fontSize = 17.sp,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(if (!active) "Suivi en pause" else if (app.isLocked) "Écran de pause actif" else "Suivi en cours",
                    fontSize = 12.sp, color = if (app.isLocked) Warning else Mint)
            }
            TextButton(onClick = onEdit, enabled = canEdit) { Text("Modifier") }
        }
        if (active) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${app.usedMinutes} / ${app.limitMinutes} min", color = Cream, fontWeight = FontWeight.Medium)
                Text("${(app.progress * 100).toInt()} %", color = Muted)
            }
            LinearProgressIndicator(
                progress = { app.progress }, modifier = Modifier.fillMaxWidth().height(9.dp)
                    .clip(RoundedCornerShape(10.dp)),
                color = if (app.isLocked || app.progress >= 1f) Warning else Mint,
                trackColor = CardLighter
            )
        } else {
            Text("Limite choisie : ${app.limitMinutes} min", color = Muted, fontSize = 13.sp)
        }
        if (app.isLocked) {
            Text("Déblocage dans ${remainingTime(app.lockedUntil)}", color = Warning, fontSize = 13.sp)
        }
        HorizontalDivider(color = MareePalette.Outline)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onRemove) { Text("Retirer", color = Muted) }
            TextButton(onClick = onReset, enabled = canEdit) {
                Text("Réinitialiser (−10)", color = if (canEdit) Mint else Muted)
            }
        }
    }
}

@Composable
private fun ChallengeTab(snapshot: MareeSnapshot, onComplete: (Int) -> Unit) {
    val indices = if (snapshot.challengeSeries == 0) 0..9 else 10..19
    val finished = indices.count { it in snapshot.checkedChallenges }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
        Text("Petits défis,\ngrands changements.", fontSize = 31.sp,
            lineHeight = 37.sp, fontWeight = FontWeight.Bold)
        Text("10 défis pour reprendre tes habitudes, sans pression.", color = Muted)
        SectionCard {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Série en cours", fontWeight = FontWeight.Bold)
                Text("$finished / 10", color = Mint)
            }
            LinearProgressIndicator(progress = { finished / 10f },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = Mint, trackColor = CardLighter)
            Text("Nouvelle série dans ${snapshot.daysUntilChallenges} jours", color = Muted, fontSize = 13.sp)
            Text("+5 points tous les 5 défis réussis (cumulés)", color = Mint, fontSize = 13.sp)
        }
        indices.forEach { index ->
            val done = index in snapshot.checkedChallenges
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (done) CardLighter else CardColor),
                modifier = Modifier.fillMaxWidth().clickable(enabled = !done) { onComplete(index) }
            ) {
                Row(Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = done, onCheckedChange = { if (!done) onComplete(index) },
                        enabled = !done)
                    Spacer(Modifier.width(10.dp))
                    Text(CHALLENGES[index], color = if (done) Muted else Cream,
                        modifier = Modifier.weight(1f), fontSize = 14.sp)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun MoreTab(
    active: Boolean, notificationsGranted: Boolean, onSetup: () -> Unit, onPause: () -> Unit,
    onNotifications: () -> Unit, onPrivacy: () -> Unit, onPremium: () -> Unit, onAccount: () -> Unit
) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("À ta façon.", fontWeight = FontWeight.Bold, fontSize = 32.sp)
        Text("Tes réglages, tes données, tes choix.", color = Muted, fontSize = 14.sp)
        SectionCard {
            Text("Le suivi", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(if (active) "Actif sur ce téléphone" else "En pause · aucun écran de blocage actif",
                color = if (active) Mint else MareePalette.Coral)
            Text("Les autorisations se modifient dans les réglages Android. Tu peux mettre le suivi en pause à tout moment.",
                color = Muted, fontSize = 14.sp)
            Button(onClick = onSetup) { Text(if (active) "Revoir les autorisations" else "Activer le suivi") }
            if (active) TextButton(onClick = onPause) { Text("Mettre en pause") }
            if (!notificationsGranted) OutlinedButton(onClick = onNotifications) { Text("Activer les alertes facultatives") }
        }
        Card(shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MareePalette.Subtle)) {
            Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Ta confidentialité", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MareePalette.DeepTeal)
                Text("Ton suivi reste sur ton téléphone. Retrouve les données utilisées, ton contact et les moyens de tout effacer.",
                    color = Muted, fontSize = 14.sp, lineHeight = 21.sp)
                OutlinedButton(onClick = onPrivacy) { Text("Lire la politique de confidentialité") }
            }
        }
        SectionCard {
            Text("Libre de commencer", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text("Marée fonctionne sans compte. Tu n’as pas besoin de t’inscrire pour reprendre ton temps.",
                color = Muted, lineHeight = 22.sp)
            TextButton(onClick = onAccount) { Text("Et la sauvegarde ?") }
        }
        Card(shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MareePalette.Lilac)) {
            Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Marée Premium · à venir", color = MareePalette.Lavender,
                    fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Une offre pour aller plus loin est à l’étude. Aucun paiement n’est disponible pour le moment.",
                    color = Muted, fontSize = 14.sp, lineHeight = 21.sp)
                OutlinedButton(onClick = onPremium) { Text("En savoir plus") }
            }
        }
        Text("Marée · Version ${BuildConfig.VERSION_NAME} · Conçue pour te rendre du temps.",
            color = Muted, fontSize = 12.sp)
        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun AppPickerDialog(
    apps: List<InstalledApp>?, loadFailed: Boolean, onRetry: () -> Unit,
    onDismiss: () -> Unit, onConfirm: (String, Int) -> Unit
) {
    var selected by remember { mutableStateOf<InstalledApp?>(null) }
    var query by remember { mutableStateOf("") }
    if (selected != null) {
        MinutesDialog("Limite quotidienne de ${selected!!.label}", 30,
            onDismiss = onDismiss,
            onConfirm = { mins -> onConfirm(selected!!.pkg, mins) })
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Choisir une application") },
            text = {
                Column {
                    OutlinedTextField(
                        value = query, onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = apps != null && !loadFailed,
                        singleLine = true, label = { Text("Rechercher") }
                    )
                    Spacer(Modifier.height(12.dp))
                    if (loadFailed) {
                        Text("Impossible de charger les applications.", color = Muted)
                        TextButton(onClick = onRetry) { Text("Réessayer") }
                    } else if (apps == null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(12.dp))
                            Text("Chargement des applications…", color = Muted)
                        }
                    } else {
                        val matching = apps.filter { it.label.contains(query, ignoreCase = true) }
                        if (matching.isEmpty()) {
                            Text(if (apps.isEmpty()) "Aucune application disponible."
                                else "Aucune application trouvée.", color = Muted)
                        }
                        LazyColumn(Modifier.heightIn(max = 340.dp)) {
                            items(matching, key = { it.pkg }) { app ->
                                Row(
                                    Modifier.fillMaxWidth().clickable { selected = app }
                                        .padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(app.label, modifier = Modifier.weight(1f))
                                    Text("＋", color = Mint)
                                }
                                HorizontalDivider(color = MareePalette.Outline)
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = onDismiss) { Text("Fermer") } }
        )
    }
}

@Composable
private fun MinutesDialog(
    title: String, initialMinutes: Int, onDismiss: () -> Unit, onConfirm: (Int) -> Unit
) {
    var minutes by remember(initialMinutes, title) { mutableIntStateOf(initialMinutes) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Choisis une limite par pas de 5 minutes.", color = Muted,
                    textAlign = TextAlign.Center)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(onClick = { minutes = (minutes - 5).coerceAtLeast(5) },
                        enabled = minutes > 5) { Text("−", fontSize = 20.sp) }
                    Text("$minutes min", fontSize = 27.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    OutlinedButton(onClick = { minutes = (minutes + 5).coerceAtMost(1440) },
                        enabled = minutes < 1440) { Text("+", fontSize = 20.sp) }
                }
            }
        },
        confirmButton = { Button(onClick = { onConfirm(minutes) }) { Text("Confirmer") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}
