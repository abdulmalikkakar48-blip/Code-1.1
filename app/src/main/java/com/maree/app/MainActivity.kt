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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.key
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
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
private val Warning = MareePalette.Danger

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
            infoMessage = "Ouvre les paramètres Android, puis Applications → Selkie, pour gérer les autorisations."
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
                val extraNavHeight = (LocalDensity.current.fontScale - 1f).coerceAtLeast(0f) * 28f
                NavigationBar(containerColor = MareePalette.DeepTeal, tonalElevation = 0.dp,
                    modifier = Modifier.heightIn(min = (80f + extraNavHeight).dp)) {
                    listOf(
                        Triple("Accueil", R.drawable.ic_nav_home, 0),
                        Triple("Quotidien", R.drawable.ic_nav_daily, 1),
                        Triple("Défis", R.drawable.ic_nav_challenges, 2),
                        Triple("Coach", R.drawable.ic_nav_coach, 3),
                        Triple("Plus", R.drawable.ic_nav_more, 4)
                    ).forEach { (name, icon, index) ->
                        NavigationBarItem(
                            selected = tab == index, onClick = { tab = index },
                            icon = { Icon(painterResource(icon), contentDescription = null, modifier = Modifier.size(23.dp)) },
                            label = { Text(name, fontSize = 11.sp, lineHeight = 13.sp,
                                fontWeight = FontWeight.Medium, maxLines = 2,
                                textAlign = TextAlign.Center, overflow = TextOverflow.Ellipsis) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MareePalette.DeepTeal, selectedTextColor = MareePalette.Mint,
                                indicatorColor = MareePalette.Mint, unselectedIconColor = Color(0xFFAFBEB5),
                                unselectedTextColor = Color(0xFFAFBEB5)
                            )
                        )
                    }
                }
            }
        ) { inner ->
            Column(Modifier.fillMaxSize().padding(inner)) {
                Row(
                    Modifier.fillMaxWidth().padding(start = 22.dp, end = 22.dp, top = 16.dp, bottom = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        BrandIcon(40.dp)
                        Spacer(Modifier.width(12.dp))
                        Text(stringResource(R.string.app_name), color = Cream, fontSize = 23.sp,
                            fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f))
                    }
                    TextButton(onClick = { tab = 4 }) {
                        StatusPill(snapshot.plan.displayName)
                    }
                }
                when (tab) {
                    0 -> HomeTab(
                        snapshot = snapshot,
                        active = monitoringEnabled && permissions.readyForMonitoring,
                        onSetup = { showSetup = true },
                        onChallenges = { tab = 2 },
                        onAdd = ::addApplication, onEdit = { editApp = it },
                        onRemove = { removeApp = it },
                        onReset = {
                            if (monitoringEnabled && permissions.readyForMonitoring) resetApp = it
                            else { showSetup = true }
                        },
                        getName = { appLabel(context, it) }
                    )
                    1 -> DailyTab(snapshot, onCoach = { tab = 3 }, getName = { appLabel(context, it) }, onAddApp = ::addApplication,
                        monitoringActive = monitoringEnabled && permissions.readyForMonitoring,
                        onActivateTracking = { showSetup = true })
                    2 -> ChallengeTab(snapshot,
                        onComplete = { index ->
                            if (store.completeChallenge(index)) {
                                snapshot = store.readSnapshot(lastReading)
                                refresh()
                            }
                        },
                        onOffers = { tab = 4 },
                        onCreateCustom = { title, steps, duration ->
                            store.createCustomChallenge(title, steps, duration).also { issue ->
                                if (issue == null) { snapshot = store.readSnapshot(lastReading); refresh() }
                            }
                        },
                        onToggleCustomStep = { id, index, checked ->
                            store.setCustomChallengeStep(id, index, checked).also { issue ->
                                if (issue == null) { snapshot = store.readSnapshot(lastReading); refresh() }
                            }
                        },
                        onValidateCustom = { id ->
                            store.validateCustomChallengeToday(id).also { issue ->
                                if (issue == null) { snapshot = store.readSnapshot(lastReading); refresh() }
                            }
                        },
                        onDeleteCustom = { id ->
                            store.deleteCustomChallenge(id).also { issue ->
                                if (issue == null) { snapshot = store.readSnapshot(lastReading); refresh() }
                            }
                        }
                    )
                    3 -> CoachTab(snapshot,
                        monitoringActive = monitoringEnabled && permissions.readyForMonitoring,
                        getName = { appLabel(context, it) })
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
                        plan = snapshot.plan,
                        onDemoPlan = { plan ->
                            store.setDemoPlan(plan)
                            snapshot = store.readSnapshot(lastReading)
                            refresh()
                        },
                        onAccount = { infoMessage = "Cette version fonctionne sans compte ; ton suivi reste sur ce téléphone. Un compte Selkie est prévu pour retrouver tes droits d’abonnement, avec un seul téléphone connecté à la fois. Il n’est pas encore actif : il demande un serveur de connexion et une vérification des achats Google Play. Tes points et défis ne sont pas synchronisés." }
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
            text = { Text("Cette application et son verrou seront supprimés de Selkie.") },
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
            title = { Text("Selkie") }, text = { Text(message) },
            confirmButton = { TextButton(onClick = { infoMessage = null }) { Text("Compris") } })
    }
    if (deleteConfirmation) {
        AlertDialog(onDismissRequest = { if (!deleting) deleteConfirmation = false },
            title = { Text("Effacer les données de Selkie ?") },
            text = { Text("Tes applications suivies, limites, verrous, points, défis, choix de démonstration et d’accueil, ainsi que les questions du coach enregistrées sur ce téléphone seront effacés. Le suivi sera arrêté. Cette action est irréversible et ne supprime pas les statistiques Android.") },
            confirmButton = { Button(enabled = !deleting, onClick = {
                scope.launch {
                    deleting = true
                    setup.reset()
                    monitoringEnabled = false
                    MonitorService.stop(context)
                    val cleared = withContext(Dispatchers.IO) {
                        val local = store.clearPersonalData()
                        val coach = try { CoachSessionStore(context).clear(); true } catch (_: CoachError) { false }
                        val offlineCoach = OfflineCoachHistory(context).clear()
                        local && coach && offlineCoach
                    }
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
    snapshot: MareeSnapshot, active: Boolean, onSetup: () -> Unit, onChallenges: () -> Unit,
    onAdd: () -> Unit, onEdit: (AppSnapshot) -> Unit, onRemove: (AppSnapshot) -> Unit,
    onReset: (AppSnapshot) -> Unit, getName: (String) -> String
) {
    var appQuery by rememberSaveable { mutableStateOf("") }
    var appSort by rememberSaveable { mutableIntStateOf(0) }
    val appLimit = snapshot.plan.maxTrackedApps
    val displayedApps = if (snapshot.plan == PlanType.ULTRA) {
        val matching = snapshot.apps.filter { getName(it.packageName).contains(appQuery, ignoreCase = true) }
        when (appSort) {
            1 -> matching.sortedByDescending { it.usedMillis }
            2 -> matching.sortedByDescending { it.progress }
            else -> matching.sortedBy { getName(it.packageName).lowercase(Locale.FRENCH) }
        }
    } else snapshot.apps
    LazyColumn(Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            ScreenHeading("TON ESPACE", "Reprends ton temps.",
                "Des limites simples. Plus de place pour toi.")
        }
        item {
            DisciplineCard(snapshot, onChallenges)
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                HomeMetric(
                    value = snapshot.apps.size.toString(),
                    label = "Applications suivies",
                    modifier = Modifier.weight(1f)
                )
                HomeMetric(
                    value = snapshot.checkedChallenges.size.toString(),
                    label = "Défis terminés · série",
                    modifier = Modifier.weight(1f)
                )
            }
        }
        if (!active) item {
            AppCardSurface {
                StatusPill("Suivi en pause")
                Text("Active le suivi pour mesurer tes limites et afficher les écrans de pause.",
                    color = Muted, fontSize = 14.sp, lineHeight = 21.sp)
                PrimaryAction("Activer le suivi", onClick = onSetup, modifier = Modifier.fillMaxWidth())
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Tes applications", color = Cream, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    Text(if (appLimit == null) "${snapshot.apps.size} suivies · sans plafond"
                        else "${snapshot.apps.size} sur $appLimit suivies", color = Muted, fontSize = 12.sp)
                }
                PrimaryAction("+ Ajouter", onClick = onAdd,
                    enabled = snapshot.points > 0 && (appLimit == null || snapshot.apps.size < appLimit))
            }
        }
        if (snapshot.points <= 0) item {
            Text("À 0 point, gagne des points avec les défis pour ajouter ou modifier une limite.",
                color = Warning, fontSize = 13.sp, lineHeight = 20.sp)
        }
        if (appLimit != null && snapshot.apps.size >= appLimit) item {
            Text(if (snapshot.apps.size > appLimit)
                "Tes applications restent suivies. Retire-en pour passer sous le plafond et en ajouter une autre."
                else "Plafond atteint. Tu peux retirer une application pour en choisir une autre.",
                color = Muted, fontSize = 13.sp, lineHeight = 20.sp)
        }
        if (snapshot.apps.isEmpty()) item {
            AppCardSurface {
                Text("Ta première limite", fontWeight = FontWeight.SemiBold, fontSize = 19.sp)
                Text("Choisis l’application qui prend le plus de place dans ta journée. Fixe une limite qui te ressemble.",
                    color = Muted, fontSize = 14.sp, lineHeight = 21.sp)
                OutlinedButton(onClick = onAdd, enabled = snapshot.points > 0,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp), shape = RoundedCornerShape(14.dp)) {
                    Text("Choisir une application")
                }
            }
        } else {
            if (snapshot.plan == PlanType.ULTRA) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = appQuery, onValueChange = { appQuery = it },
                            placeholder = { Text("Rechercher une application") }, singleLine = true,
                            shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth())
                        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Nom", "Temps passé", "Limite proche").forEachIndexed { index, title ->
                                FilterChip(selected = appSort == index, onClick = { appSort = index },
                                    label = { Text(title, fontSize = 12.sp) })
                            }
                        }
                    }
                }
                if (displayedApps.isEmpty()) item {
                    Text("Aucune application ne correspond à ta recherche.", color = Muted, fontSize = 14.sp)
                }
            }
            items(displayedApps, key = { it.packageName }) { app ->
                AppCard(app, getName(app.packageName), snapshot.points > 0, snapshot.points >= 10, active,
                    onEdit = { onEdit(app) }, onRemove = { onRemove(app) }, onReset = { onReset(app) })
            }
        }
    }
}

@Composable
private fun SectionCard(content: @Composable ColumnScope.() -> Unit) {
    AppCardSurface(content = content)
}

@Composable
private fun AppCard(
    app: AppSnapshot, name: String, canEdit: Boolean, canReset: Boolean, active: Boolean,
    onEdit: () -> Unit, onRemove: () -> Unit, onReset: () -> Unit
) {
    val context = LocalContext.current
    var menuOpen by remember { mutableStateOf(false) }
    AppCardSurface {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AndroidView(factory = { ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                try { setImageDrawable(context.packageManager.getApplicationIcon(app.packageName)) }
                catch (_: Exception) { setImageResource(R.drawable.brand_mark) }
            } }, modifier = Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(name, fontWeight = FontWeight.SemiBold, fontSize = 17.sp,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(if (!active) "Suivi en pause" else if (app.isLocked) "Limite atteinte" else "Suivi actif",
                    fontSize = 12.sp, color = if (!active) Muted else if (app.isLocked) Warning else Mint)
            }
            Box {
                IconButton(onClick = { menuOpen = true }) {
                    Icon(painterResource(R.drawable.ic_app_options), contentDescription = "Options pour $name")
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    DropdownMenuItem(text = { Text("Réinitialiser · −10 points") }, enabled = canReset,
                        onClick = { menuOpen = false; onReset() })
                    DropdownMenuItem(text = { Text("Retirer du suivi", color = MareePalette.Danger) },
                        onClick = { menuOpen = false; onRemove() })
                    if (!canReset) Text("10 points nécessaires", color = Muted, fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                }
            }
        }
        if (active) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("${app.usedMinutes} min", fontSize = 20.sp, color = Cream, fontWeight = FontWeight.SemiBold)
                Text("sur ${app.limitMinutes} min", color = Muted, fontSize = 13.sp)
            }
            LinearProgressIndicator(progress = { app.progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = if (app.isLocked || app.progress >= 1f) Warning else Mint, trackColor = CardLighter)
        } else {
            Text("Limite choisie : ${app.limitMinutes} min", color = Muted, fontSize = 14.sp)
        }
        if (app.isLocked && active) Text("Écran de pause · encore ${remainingTime(app.lockedUntil)}",
            color = Warning, fontSize = 13.sp)
        TextButton(onClick = onEdit, enabled = canEdit) {
            Text("Modifier la limite", fontSize = 13.sp)
        }
    }
}

@Composable
private fun ChallengeTab(
    snapshot: MareeSnapshot, onComplete: (Int) -> Unit, onOffers: () -> Unit,
    onCreateCustom: (String, List<String>, Int) -> String?,
    onToggleCustomStep: (String, Int, Boolean) -> String?,
    onValidateCustom: (String) -> String?, onDeleteCustom: (String) -> String?
) {
    val indices = if (snapshot.challengeSeries == 0) 0..9 else 10..19
    val finished = indices.count { it in snapshot.checkedChallenges }
    val firstPending = indices.firstOrNull { it !in snapshot.checkedChallenges }
    var personal by rememberSaveable { mutableStateOf(false) }
    var showRules by rememberSaveable { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ScreenHeading("LES DÉFIS", "Un jour à la fois.",
            if (personal) "Crée les habitudes qui te ressemblent." else "De petites actions pour de vrais progrès.")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FilterChip(selected = !personal, onClick = { personal = false },
                modifier = Modifier.weight(1f), label = { Text("Défis proposés") })
            FilterChip(selected = personal, onClick = { personal = true },
                modifier = Modifier.weight(1f), label = { Text("Mes défis") })
        }
        if (personal) {
            CustomChallengesSection(snapshot, onOffers, onCreateCustom, onToggleCustomStep,
                onValidateCustom, onDeleteCustom)
        } else {
            AppCardSurface {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ta série", color = Cream, fontSize = 20.sp, fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f))
                    Text("$finished / 10", color = Muted, fontSize = 13.sp)
                    TextButton(onClick = { showRules = !showRules }) {
                        Text(if (showRules) "Fermer" else "Règles", fontSize = 12.sp)
                    }
                }
                LinearProgressIndicator(progress = { finished / 10f },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                    color = Mint, trackColor = CardLighter)
                Text("+5 points pour 5 défis terminés", color = Mint,
                    fontWeight = FontWeight.Medium, fontSize = 13.sp, lineHeight = 20.sp)
                Text("Nouvelle série dans ${snapshot.daysUntilChallenges} jours · progression remise à zéro.",
                    color = Muted, fontSize = 12.sp, lineHeight = 18.sp)
                if (showRules) Text("Chaque défi demande $CHALLENGE_REQUIRED_DAYS journées différentes, avec une validation par date locale. Les jours peuvent être espacés. Les jours intermédiaires ne donnent pas de points. Les défis entièrement terminés se cumulent entre les séries pour le bonus.",
                    color = Muted, fontSize = 13.sp, lineHeight = 20.sp)
            }
            indices.forEach { index ->
                key(index) {
                    val done = index in snapshot.checkedChallenges
                    val progress = snapshot.challengeProgress[index] ?: ChallengeProgress(
                        completedDays = if (done) CHALLENGE_REQUIRED_DAYS else 0)
                    var expanded by rememberSaveable(snapshot.challengeSeries, index) {
                        mutableStateOf(index == firstPending)
                    }
                    val completedDays = progress.completedDays.coerceIn(0, progress.requiredDays)
                    AppCardSurface {
                        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Surface(color = CardLighter, shape = RoundedCornerShape(10.dp)) {
                                Text("${index - indices.first + 1}", color = Mint, fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp))
                            }
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                Text(CHALLENGES[index], color = Cream, fontWeight = FontWeight.SemiBold,
                                    fontSize = 17.sp, lineHeight = 24.sp)
                                Text("$completedDays / ${progress.requiredDays} journées validées",
                                    color = Muted, fontSize = 13.sp)
                            }
                        }
                        when {
                            done -> StatusPill("✓ Défi terminé")
                            progress.validatedToday -> StatusPill("✓ Validé aujourd’hui · à demain")
                            else -> {
                                PrimaryAction("Valider aujourd’hui", onClick = { onComplete(index) },
                                    enabled = progress.canValidateToday, modifier = Modifier.fillMaxWidth())
                                if (!progress.canValidateToday) Text("La date du téléphone précède une validation enregistrée. Vérifie les réglages de date.",
                                    color = Muted, fontSize = 12.sp, lineHeight = 18.sp)
                            }
                        }
                        TextButton(onClick = { expanded = !expanded }) {
                            Text(if (expanded) "Masquer les journées" else "Voir les ${progress.requiredDays} journées", fontSize = 13.sp)
                        }
                        if (expanded) ChallengeDays(progress.requiredDays, completedDays, progress.validatedToday)
                    }
                }
            }
        }
        Spacer(Modifier.height(18.dp))
    }
}

@Composable
private fun MoreTab(
    active: Boolean, notificationsGranted: Boolean, onSetup: () -> Unit, onPause: () -> Unit,
    onNotifications: () -> Unit, onPrivacy: () -> Unit, plan: PlanType,
    onDemoPlan: (PlanType) -> Unit, onAccount: () -> Unit
) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        ScreenHeading("TES RÉGLAGES", "À ta façon.", "Ton suivi, tes données, tes choix.")
        AppCardSurface {
            SectionLabel("Le suivi", if (active) "Actif" else "En pause")
            Text(if (active) "Les limites de tes applications sont surveillées."
                else "Aucune application n’est surveillée ou bloquée.",
                color = Muted, fontSize = 14.sp, lineHeight = 21.sp)
            PrimaryAction(if (active) "Gérer les autorisations" else "Activer le suivi",
                onClick = onSetup, modifier = Modifier.fillMaxWidth())
            if (active) TextButton(onClick = onPause) { Text("Mettre le suivi en pause") }
            if (!notificationsGranted) {
                HorizontalDivider(color = MareePalette.Outline)
                TextButton(onClick = onNotifications) { Text("Activer les alertes facultatives") }
            }
        }
        AppCardSurface {
            Text("Tes données, sous ton contrôle", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            Text("Ton suivi reste sur ton téléphone. Consulte les informations de confidentialité ou efface tes données.",
                color = Muted, fontSize = 14.sp, lineHeight = 21.sp)
            OutlinedButton(onClick = onPrivacy, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                shape = RoundedCornerShape(14.dp)) { Text("Confidentialité et données") }
            TextButton(onClick = onAccount) { Text("Compte et changement de téléphone", fontSize = 13.sp) }
        }
        Spacer(Modifier.height(4.dp))
        MembershipSection(plan, onDemoPlan)
        Text("Selkie · ${BuildConfig.VERSION_NAME}", color = Muted, fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        Spacer(Modifier.height(18.dp))
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

@Composable
private fun DisciplineCard(snapshot: MareeSnapshot, onChallenges: () -> Unit) {
    Card(shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MareePalette.DeepTeal)) {
        Column(Modifier.fillMaxWidth().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("TON CAPITAL\nDISCIPLINE", color = MareePalette.Mint,
                    style = MaterialTheme.typography.labelMedium, letterSpacing = 1.5.sp,
                    modifier = Modifier.weight(1f))
                Surface(color = MareePalette.Mint, shape = CircleShape) {
                    Icon(painterResource(R.drawable.ic_nav_challenges), contentDescription = null,
                        tint = MareePalette.DeepTeal,
                        modifier = Modifier.padding(12.dp).size(24.dp))
                }
            }
            Column {
                Text(formatPoints(snapshot.points), color = Color.White,
                    fontSize = 64.sp, lineHeight = 70.sp, fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-2).sp)
                Text("points pour avancer", color = Color(0xFFCBDBD0),
                    style = MaterialTheme.typography.bodyMedium)
            }
            HorizontalDivider(color = Color.White.copy(alpha = 0.18f))
            Text("Chaque petit pas compte.", color = Color.White,
                style = MaterialTheme.typography.titleMedium)
            Button(onClick = onChallenges,
                modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MareePalette.Mint,
                    contentColor = MareePalette.DeepTeal)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Explorer les défis", style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.weight(1f))
                    Text("→", fontSize = 22.sp)
                }
            }
        }
    }
}

@Composable
private fun HomeMetric(value: String, label: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, color = MareePalette.Surface,
        shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, MareePalette.Outline)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(value, color = MareePalette.Teal, fontSize = 28.sp,
                lineHeight = 32.sp, fontWeight = FontWeight.Bold)
            Text(label, color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall)
        }
    }
}
