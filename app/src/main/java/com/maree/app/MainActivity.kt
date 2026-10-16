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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.semantics.semantics
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
import java.time.LocalDate

private val Background get() = MareePalette.Background
private val CardColor get() = MareePalette.Surface
private val CardLighter get() = MareePalette.Subtle
private val Mint get() = MareePalette.Teal
private val Cream get() = MareePalette.Ink
private val Muted get() = MareePalette.Muted
private val Warning get() = MareePalette.Danger

data class InstalledApp(val pkg: String, val label: String)

fun installedLaunchableApps(context: android.content.Context): List<InstalledApp> {
    val launcher = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
    @Suppress("DEPRECATION")
    val matches: List<ResolveInfo> = context.packageManager.queryIntentActivities(launcher, 0)
    return matches.mapNotNull { info ->
        val pkg = info.activityInfo?.packageName ?: return@mapNotNull null
        if (pkg == context.packageName) return@mapNotNull null
        InstalledApp(pkg, info.loadLabel(context.packageManager)?.toString() ?: pkg)
    }.distinctBy { it.pkg }.sortedBy { it.label.lowercase(AppLanguage.locale) }
}

fun appLabel(context: android.content.Context, pkg: String): String = try {
    context.packageManager.getApplicationLabel(context.packageManager.getApplicationInfo(pkg, 0)).toString()
} catch (_: Exception) { pkg.substringAfterLast('.') }

class MainActivity : ComponentActivity() {
    private val widgetDestination = mutableStateOf<String?>(null)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retired online coach: remove any legacy session credentials on upgrade.
        deleteSharedPreferences("selkie_coach_session")
        widgetDestination.value = intent?.getStringExtra("selkie_destination")
        setContent { MareeTheme { SelkieMotion { MareeApp(widgetDestination.value) { widgetDestination.value = null; intent.removeExtra("selkie_destination") } } } }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        widgetDestination.value = intent.getStringExtra("selkie_destination")
    }

    override fun onResume() {
        super.onResume()
        MonitorService.start(this)
    }
}

@Composable
private fun MareeApp(widgetDestination: String? = null, onWidgetHandled: () -> Unit = {}) {
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
    var onboardingStep by rememberSaveable { mutableIntStateOf(if (setupComplete) 2 else 0) }
    var showPrivacy by rememberSaveable { mutableStateOf(false) }
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var dailySection by rememberSaveable { mutableStateOf("today") }
    var honorDicePendingId by rememberSaveable { mutableStateOf<String?>(null) }
    var settingsPage by rememberSaveable { mutableStateOf<SettingsPage?>(null) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    var notificationAsked by rememberSaveable { mutableStateOf(false) }
    var installed by remember { mutableStateOf<List<InstalledApp>?>(null) }
    var installedLoadFailed by remember { mutableStateOf(false) }
    var installedLoadKey by remember { mutableIntStateOf(0) }
    var refreshKey by remember { mutableIntStateOf(0) }
    var dailyHistory by remember(storeGeneration) { mutableStateOf<List<DailyHistoryEntry>>(emptyList()) }
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

    LaunchedEffect(Unit) { if (tab !in 0..3) tab = 0 }
    fun openMenuPage(page: SettingsPage) {
        scope.launch {
            drawerState.close()
            if (page == SettingsPage.PRIVACY) showPrivacy = true else settingsPage = page
        }
    }

    fun openSetup() {
        onboardingStep = if (setupComplete) 2 else 0
        showSetup = true
    }
    fun openGuideDestination(destination: GuideDestination) {
        when (destination) {
            GuideDestination.HOME -> { settingsPage = null; tab = 0 }
            GuideDestination.DAILY, GuideDestination.CALENDAR, GuideDestination.REVIEW,
            GuideDestination.READING, GuideDestination.DICE, GuideDestination.COLORING, GuideDestination.TOOLS -> {
                dailySection = when (destination) {
                    GuideDestination.CALENDAR -> "calendar"
                    GuideDestination.REVIEW -> "week"
                    GuideDestination.READING -> "reading"
                    GuideDestination.DICE -> "dice"
                    GuideDestination.COLORING -> "coloring"
                    GuideDestination.TOOLS -> "tools"
                    else -> "today"
                }
                settingsPage = null; tab = 1
            }
            GuideDestination.CHALLENGES -> { settingsPage = null; tab = 2 }
            GuideDestination.COACH -> { settingsPage = null; tab = 3 }
            GuideDestination.ACCESS -> openMenuPage(SettingsPage.ACCESS)
            GuideDestination.SUBSCRIPTIONS -> openMenuPage(SettingsPage.SUBSCRIPTIONS)
            GuideDestination.APPEARANCE -> openMenuPage(SettingsPage.APPEARANCE)
            GuideDestination.LANGUAGES -> openMenuPage(SettingsPage.LANGUAGES)
            GuideDestination.PRIVACY -> { showPrivacy = true }
        }
    }

    fun display(text: String) { scope.launch { snackbars.showSnackbar(text) } }
    fun refresh() { refreshKey++ }
    fun explainHistoryWriteFailure() {
        if (!store.lastValidationHistoryWriteSucceeded) infoMessage = tr(
            "Le défi est validé, mais sa journée n’a pas pu être enregistrée dans le calendrier.",
            "The challenge check-in is saved, but its day could not be recorded in the calendar.")
    }
    fun refreshPermissions() { permissions = PermissionStatus.read(context); refresh() }
    val settingsRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { refreshPermissions() }
    val notificationRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { refreshPermissions() }
    val exportDocument = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { destination ->
        if (destination != null) {
            val exportStore = store
            scope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        LocalDataExport.write(context.contentResolver, destination, LocalDataExport.json(exportStore))
                    }
                    snackbars.showSnackbar(tr("Ton export a été enregistré dans le fichier choisi.",
                        "Your export was saved to the file you chose."))
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    snackbars.showSnackbar(tr("L’export n’a pas pu être enregistré. Vérifie le fichier choisi et réessaie.",
                        "The export could not be saved. Check the chosen file and try again."))
                }
            }
        }
    }

    fun openSettings(intent: Intent) {
        try { settingsRequest.launch(intent) }
        catch (_: android.content.ActivityNotFoundException) {
            infoMessage = tr("Ouvre les paramètres Android, puis Applications → Selkie, pour gérer les autorisations.", "Open Android settings, then Apps → Selkie, to manage permissions.")
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
        else { pendingAdd = true; openSetup() }
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
        settingsPage = null
        if (enableMonitoring) MonitorService.start(context) else MonitorService.stop(context)
        if (pendingAdd && enableMonitoring) showPicker = true
        pendingAdd = false
        refresh()
    }

    BackHandler(enabled = showSetup && setupComplete && !showPrivacy) {
        showSetup = false
        pendingAdd = false
    }
    BackHandler(enabled = !showSetup && setupComplete && !showPrivacy &&
        settingsPage != null && !drawerState.isOpen) { settingsPage = null }
    BackHandler(enabled = !showSetup && setupComplete && !showPrivacy && drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }
    LaunchedEffect(widgetDestination, setupComplete, showSetup) {
        if (widgetDestination != null && setupComplete && !showSetup) {
            when (widgetDestination) {
                "daily" -> { settingsPage = null; tab = 1 }
                "challenges" -> { settingsPage = null; tab = 2 }
                "subscriptions" -> settingsPage = SettingsPage.SUBSCRIPTIONS
            }
            onWidgetHandled()
        }
    }
    LaunchedEffect(AppLanguage.current, snapshot.plan) {
        SelkieWidgetProvider.refresh(context, force = true)
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
            var lastPrunedDay: LocalDate? = null
            while (true) {
                val todayForCleanup = LocalDate.now()
                if (!deleting && setupComplete && lastPrunedDay != todayForCleanup) {
                    val cleaned = withContext(Dispatchers.IO) { store.pruneHistory() }
                    if (!cleaned && !deleting) infoMessage = tr(
                        "Le nettoyage de l’ancien historique n’a pas pu être confirmé. Réessaie en rouvrant Selkie.",
                        "Cleanup of older history could not be confirmed. Try opening Selkie again.")
                    lastPrunedDay = todayForCleanup
                }
                permissions = PermissionStatus.read(context)
                if (setupComplete && !showSetup && !deleting) {
                    try {
                        val observedGeneration = storeGeneration
                        val (reading, currentSnapshot, currentHistory) = withContext(Dispatchers.IO) {
                            val currentReading = if (setup.isMonitoringEnabled() && permissions.readyForMonitoring)
                                reader.read(store.packages()) else lastReading
                            Triple(currentReading, store.readSnapshot(currentReading), store.historyEntries())
                        }
                        if (deleting || observedGeneration != storeGeneration || !setup.isCompleted()) {
                            delay(2500L)
                            continue
                        }
                        lastReading = reading
                        snapshot = currentSnapshot
                        dailyHistory = currentHistory
                        SelkieWidgetProvider.refresh(context)
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
        showPrivacy -> Box(Modifier.fillMaxSize()) {
            PrivacyScreen(
                onBack = { showPrivacy = false },
                onDeleteData = { deleteConfirmation = true },
                onExportData = {
                    try {
                        exportDocument.launch("Selkie-donnees-${LocalDate.now()}.json")
                    } catch (_: Exception) {
                        display(tr("Le sélecteur de fichiers Android n’est pas disponible. Réessaie depuis un autre appareil.",
                            "The Android file picker is unavailable. Try again on another device."))
                    }
                }
            )
            SnackbarHost(snackbars, Modifier.align(Alignment.BottomCenter)
                .navigationBarsPadding().padding(16.dp))
        }
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
            onExplore = { finishSetup(false) },
            currentStep = onboardingStep, onStepChange = { onboardingStep = it }, permissionsOnly = setupComplete
        )
        else -> Box(Modifier.fillMaxSize().background(Background)) {
            SelkieGeometryBackground(Modifier.fillMaxSize())
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    SelkieMenuSheet(selectedPage = settingsPage,
                        onSelect = ::openMenuPage,
                        onClose = { scope.launch { drawerState.close() } })
                }
            ) {
                Scaffold(
                    containerColor = Color.Transparent,
                    snackbarHost = { SnackbarHost(snackbars) },
                    bottomBar = {
                        val extraNavHeight = (LocalDensity.current.fontScale - 1f).coerceAtLeast(0f) * 28f
                        NavigationBar(containerColor = MareePalette.NavigationBackground, tonalElevation = 0.dp,
                            modifier = Modifier.heightIn(min = (80f + extraNavHeight).dp)) {
                            listOf(
                                Triple(tr("Accueil", "Home"), R.drawable.ic_nav_home, 0),
                                Triple(tr("Quotidien", "Daily"), R.drawable.ic_nav_daily, 1),
                                Triple(tr("Défis", "Challenges"), R.drawable.ic_nav_challenges, 2),
                                Triple(tr("Coach", "Coach"), R.drawable.ic_nav_coach, 3)
                            ).forEach { (name, icon, index) ->
                                NavigationBarItem(
                                    selected = settingsPage == null && tab == index,
                                    onClick = { settingsPage = null; tab = index },
                                    icon = { Icon(painterResource(icon), contentDescription = null, modifier = Modifier.size(23.dp)) },
                                    label = { Text(name, fontSize = 11.sp, lineHeight = 13.sp,
                                        fontWeight = FontWeight.Medium, maxLines = 2,
                                        textAlign = TextAlign.Center, overflow = TextOverflow.Ellipsis) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MareePalette.OnHeroAccent, selectedTextColor = MareePalette.HeroAccent,
                                        indicatorColor = MareePalette.HeroAccent, unselectedIconColor = MareePalette.NavigationMuted,
                                        unselectedTextColor = MareePalette.NavigationMuted
                                    )
                                )
                            }
                        }
                    }
                ) { inner ->
                    Column(Modifier.fillMaxSize().padding(inner)) {
                        Row(
                            Modifier.fillMaxWidth().padding(start = 22.dp, end = 22.dp, top = 10.dp, bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                BrandIcon(36.dp)
                                Spacer(Modifier.width(12.dp))
                                Text(stringResource(R.string.app_name), color = Cream, fontSize = 23.sp,
                                    fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f))
                            }
                            TextButton(onClick = { openMenuPage(SettingsPage.SUBSCRIPTIONS) },
                                modifier = Modifier.semantics {
                                    contentDescription = tr(
                                        "Ouvrir les abonnements. Offre actuelle : ${snapshot.plan.displayName}",
                                        "Open subscriptions. Current plan: ${snapshot.plan.displayName}")
                                }) {
                                StatusPill(snapshot.plan.displayName)
                            }
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(painterResource(R.drawable.ic_menu),
                                    contentDescription = tr("Ouvrir le menu", "Open menu"), tint = Cream)
                            }
                        }
                        val currentPage = settingsPage
                        if (currentPage != null) {
                            TextButton(onClick = { settingsPage = null },
                                modifier = Modifier.padding(horizontal = 12.dp)) {
                                Text(tr("‹ Retour", "‹ Back"))
                            }
                            if (currentPage == SettingsPage.GUIDE) {
                                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                                    .padding(horizontal = 20.dp, vertical = 4.dp)) {
                                    FeatureGuideSection(onOpen = ::openGuideDestination)
                                }
                            } else key(currentPage) {
                                SettingsPageContent(page = currentPage, snapshot = snapshot,
                                    monitoring = monitoringEnabled && permissions.readyForMonitoring,
                                    notificationsGranted = permissions.notificationsGranted,
                                    onSetup = { openSetup() }, onStart = { openSetup() },
                                    onStop = {
                                        setup.setMonitoringEnabled(false)
                                        monitoringEnabled = false
                                        MonitorService.stop(context)
                                        refresh()
                                    },
                                    onNotifications = ::askNotifications,
                                    onPlan = { plan ->
                                        store.setDemoPlan(plan)
                                        snapshot = store.readSnapshot(lastReading)
                                        refresh()
                                    })
                            }
                        } else when (tab) {
                            0 -> HomeTab(
                                snapshot = snapshot,
                                active = monitoringEnabled && permissions.readyForMonitoring,
                                onSetup = { openSetup() },
                                onChallenges = { tab = 2 },
                                onGuide = { openMenuPage(SettingsPage.GUIDE) }, onOpen = ::openGuideDestination,
                                onAdd = ::addApplication, onEdit = { editApp = it.copy(limitMinutes = it.configuredLimitMinutes) },
                                onRemove = { removeApp = it },
                                onReset = {
                                    if (monitoringEnabled && permissions.readyForMonitoring) resetApp = it
                                    else { openSetup() }
                                },
                                getName = { appLabel(context, it) }
                            )
                            1 -> DailyTab(snapshot, onCoach = { tab = 3 }, getName = { appLabel(context, it) }, onAddApp = ::addApplication,
                                monitoringActive = monitoringEnabled && permissions.readyForMonitoring,
                                onActivateTracking = { openSetup() }, history = dailyHistory,
                                section = dailySection, onSectionChange = { dailySection = it },
                                honorPendingId = honorDicePendingId, onHonorPendingIdChange = { honorDicePendingId = it },
                                onOffers = { openMenuPage(SettingsPage.SUBSCRIPTIONS) }, onToolsChanged = ::refresh,
                                onProgramValidated = { id ->
                                    if (!store.recordProgramValidation(id)) display(tr(
                                        "Le programme est validé, mais sa journée n’a pas pu être enregistrée dans le calendrier.",
                                        "The program check-in is saved, but its day could not be recorded in the calendar."))
                                    refresh()
                                })
                            2 -> ChallengeTab(snapshot,
                                onComplete = { index ->
                                    if (store.completeChallenge(index)) {
                                        explainHistoryWriteFailure()
                                        snapshot = store.readSnapshot(lastReading)
                                        refresh()
                                    }
                                },
                                onOffers = { openMenuPage(SettingsPage.SUBSCRIPTIONS) },
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
                                        if (issue == null) { explainHistoryWriteFailure(); snapshot = store.readSnapshot(lastReading); refresh() }
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
                                getName = { appLabel(context, it) },
                                onOffers = { openMenuPage(SettingsPage.SUBSCRIPTIONS) })
                        }
                    }
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
        MinutesDialog(tr("Modifier la limite de ${appLabel(context, item.packageName)}", "Edit the limit for ${appLabel(context, item.packageName)}"), item.limitMinutes,
            onDismiss = { editApp = null }, onConfirm = { mins ->
                store.editLimit(item.packageName, mins)?.let(::display)
                editApp = null
                refresh()
            })
    }
    removeApp?.let { item ->
        AlertDialog(onDismissRequest = { removeApp = null },
            title = { Text(tr("Retirer ${appLabel(context, item.packageName)} ?", "Remove ${appLabel(context, item.packageName)}?")) },
            text = { Text(tr("Cette application et son verrou seront supprimés de Selkie.", "This app and its lock will be removed from Selkie.")) },
            confirmButton = { Button(onClick = {
                store.removeApp(item.packageName)
                MonitorService.clearAppAlert(context, item.packageName)
                removeApp = null
                refresh()
            }) { Text(tr("Retirer", "Remove")) } },
            dismissButton = { TextButton(onClick = { removeApp = null }) { Text(tr("Annuler", "Cancel")) } })
    }
    resetApp?.let { item ->
        AlertDialog(onDismissRequest = { resetApp = null },
            title = { Text(tr("Réinitialiser la limite ?", "Reset the limit?")) },
            text = { Text(tr("Le verrou sera levé et la limite complète sera rendue. Coût : 10 points.", "The lock will be removed and the full limit restored. Cost: 10 points.")) },
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
            }) { Text(tr("Réinitialiser (−10)", "Reset (−10)")) } },
            dismissButton = { TextButton(onClick = { resetApp = null }) { Text(tr("Annuler", "Cancel")) } })
    }
    infoMessage?.let { message ->
        AlertDialog(onDismissRequest = { infoMessage = null },
            title = { Text("Selkie") }, text = { Text(message) },
            confirmButton = { TextButton(onClick = { infoMessage = null }) { Text(tr("Compris", "Got it")) } })
    }
    if (deleteConfirmation) {
        AlertDialog(onDismissRequest = { if (!deleting) deleteConfirmation = false },
            title = { Text(tr("Effacer les données de Selkie ?", "Delete Selkie data?")) },
            text = { Text(tr("Tes applications suivies, limites, verrous, points, défis, choix de démonstration, d’accueil, de langue et d’apparence, ainsi que l’historique du calendrier, les tâches personnelles, les créneaux, les favoris, les programmes guidés, les coloriages, le dernier reçu du dé Points et les questions du coach enregistrées sur ce téléphone seront effacés. Le suivi sera arrêté. Cette action est irréversible et ne supprime pas les statistiques Android.", "Your tracked apps, limits, locks, points, challenges, demo, onboarding, language and appearance choices, calendar history, calendar tasks, time slots, favorites, guided programs, colouring pages, the last Points dice receipt and coach questions saved on this phone will be deleted. Tracking will stop. This cannot be undone and does not delete Android usage statistics.")) },
            confirmButton = { Button(enabled = !deleting, onClick = {
                scope.launch {
                    deleting = true
                    honorDicePendingId = null
                    setup.setMonitoringEnabled(false)
                    monitoringEnabled = false
                    MonitorService.stop(context)
                    val cleared = withContext(Dispatchers.IO) {
                        val onboarding = setup.reset()
                        val local = store.clearPersonalData()
                        val coach = context.deleteSharedPreferences("selkie_coach_session")
                        val offlineCoach = OfflineCoachHistory(context).clear()
                        val language = AppLanguage.reset(context)
                        val appearance = AppAppearance.reset(context)
                        val widget = SelkieWidgetProvider.refreshNow(context)
                        onboarding && local && coach && offlineCoach && language && appearance && widget
                    }
                    deleting = false
                    deleteConfirmation = false
                    if (cleared) {
                        storeGeneration++
                        honorDicePendingId = null
                        setupComplete = false
                        showPrivacy = false
                        settingsPage = null
                        openSetup()
                        showPicker = false
                        pendingAdd = false
                        editApp = null; removeApp = null; resetApp = null
                        tab = 0; dailySection = "today"; onboardingStep = 0
                        refresh()
                    } else {
                        setupComplete = setup.isCompleted()
                        refresh()
                        infoMessage = tr("L’effacement n’a pas pu être enregistré. Le suivi est arrêté. Réessaie depuis Confidentialité.", "Deletion could not be saved. Tracking has stopped. Try again from Privacy.")
                    }
                }
            }, colors = ButtonDefaults.buttonColors(containerColor = MareePalette.Danger,
                contentColor = MareePalette.OnDanger)) {
                Text(if (deleting) tr("Effacement…", "Deleting…") else tr("Tout effacer", "Delete everything"))
            } },
            dismissButton = { TextButton(enabled = !deleting, onClick = { deleteConfirmation = false }) { Text(tr("Annuler", "Cancel")) } })
    }
}

@Composable
private fun HomeTab(
    snapshot: MareeSnapshot, active: Boolean, onSetup: () -> Unit, onChallenges: () -> Unit,
    onGuide: () -> Unit, onOpen: (GuideDestination) -> Unit,
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
            else -> matching.sortedBy { getName(it.packageName).lowercase(AppLanguage.locale) }
        }
    } else snapshot.apps
    LazyColumn(Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { HomeWelcomeCard(onGuide) }
        item { HomeTrackingStrip(snapshot.points, active, onChallenges, onSetup) }
        item { HomeAppsHeader(snapshot.apps.size, appLimit,
            canAdd = snapshot.points > 0 && (appLimit == null || snapshot.apps.size < appLimit), onAdd = onAdd) }
        if (snapshot.points <= 0) item {
            Text(tr("À 0 point, gagne des points avec les défis pour ajouter ou modifier une limite.", "At 0 points, earn points through challenges to add or edit a limit."),
                color = Warning, fontSize = 13.sp, lineHeight = 20.sp)
        }
        if (appLimit != null && snapshot.apps.size >= appLimit) item {
            Text(if (snapshot.apps.size > appLimit)
                tr("Tes applications restent suivies. Retire-en pour passer sous le plafond et en ajouter une autre.", "Your apps are still tracked. Remove some to get below the limit and add another.")
                else tr("Plafond atteint. Tu peux retirer une application pour en choisir une autre.", "App limit reached. You can remove an app to choose another."),
                color = Muted, fontSize = 13.sp, lineHeight = 20.sp)
        }
        if (snapshot.apps.isEmpty()) item {
            AppCardSurface {
                Text(tr("Ta première limite", "Your first limit"), fontWeight = FontWeight.SemiBold, fontSize = 19.sp)
                Text(tr("Choisis l’application qui prend le plus de place dans ta journée. Fixe une limite qui te ressemble.", "Choose the app that takes up most of your day. Set a limit that works for you."),
                    color = Muted, fontSize = 14.sp, lineHeight = 21.sp)
                Text(tr("Utilise Ajouter juste au-dessus pour choisir ton application.",
                    "Use Add just above to choose your app."), color = MareePalette.Teal,
                    style = MaterialTheme.typography.bodySmall)
            }
        } else {
            if (snapshot.plan == PlanType.ULTRA) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = appQuery, onValueChange = { appQuery = it },
                            placeholder = { Text(tr("Rechercher une application", "Search for an app")) }, singleLine = true,
                            shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth())
                        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(tr("Nom", "Name"), tr("Temps passé", "Time spent"), tr("Limite proche", "Close to limit")).forEachIndexed { index, title ->
                                FilterChip(selected = appSort == index, onClick = { appSort = index },
                                    label = { Text(title, fontSize = 12.sp) })
                            }
                        }
                    }
                }
                if (displayedApps.isEmpty()) item {
                    Text(tr("Aucune application ne correspond à ta recherche.", "No apps match your search."), color = Muted, fontSize = 14.sp)
                }
            }
            items(displayedApps, key = { it.packageName }) { app ->
                AppCard(app, getName(app.packageName), snapshot.points > 0, snapshot.points >= 10, active,
                    onEdit = { onEdit(app) }, onRemove = { onRemove(app) }, onReset = { onReset(app) })
            }
        }
        item { HomeShortcuts(onCalendar = { onOpen(GuideDestination.CALENDAR) }, onCoach = { onOpen(GuideDestination.COACH) }) }
        item { DisciplineCard(snapshot, onChallenges) }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                HomeMetric(snapshot.apps.size.toString(), tr("Applications suivies", "Tracked apps"), Modifier.weight(1f))
                HomeMetric(snapshot.checkedChallenges.size.toString(), tr("Défis terminés · série", "Completed challenges · series"), Modifier.weight(1f))
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
                Text(if (!active) tr("Suivi en pause", "Tracking paused") else if (app.isLocked) tr("Limite atteinte", "Limit reached") else tr("Suivi actif", "Tracking active"),
                    fontSize = 12.sp, color = if (!active) Muted else if (app.isLocked) Warning else Mint)
            }
            Box {
                IconButton(onClick = { menuOpen = true }) {
                    Icon(painterResource(R.drawable.ic_app_options), contentDescription = tr("Options pour $name", "Options for $name"))
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    DropdownMenuItem(text = { Text(tr("Réinitialiser · −10 points", "Reset · −10 points")) }, enabled = canReset,
                        onClick = { menuOpen = false; onReset() })
                    DropdownMenuItem(text = { Text(tr("Retirer du suivi", "Stop tracking"), color = MareePalette.Danger) },
                        onClick = { menuOpen = false; onRemove() })
                    if (!canReset) Text(tr("10 points nécessaires", "10 points required"), color = Muted, fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                }
            }
        }
        if (active) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("${app.usedMinutes} min", fontSize = 20.sp, color = Cream, fontWeight = FontWeight.SemiBold)
                Text(tr("sur ${app.limitMinutes} min", "of ${app.limitMinutes} min"), color = Muted, fontSize = 13.sp)
            }
            LinearProgressIndicator(progress = { app.progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = if (app.isLocked || app.progress >= 1f) Warning else Mint, trackColor = CardLighter)
        } else {
            Text(tr("Limite choisie : ${app.limitMinutes} min", "Selected limit: ${app.limitMinutes} min"), color = Muted, fontSize = 14.sp)
        }
        if (app.isLocked && active) Text(tr("Écran de pause · encore ${remainingTime(app.lockedUntil)}", "Pause screen · ${remainingTime(app.lockedUntil)} remaining"),
            color = Warning, fontSize = 13.sp)
        if (app.limitMinutes != app.configuredLimitMinutes) Text(
            tr("Créneau actif · limite habituelle : ${app.configuredLimitMinutes} min",
                "Time slot active · usual limit: ${app.configuredLimitMinutes} min"),
            color = Muted, fontSize = 13.sp)
        TextButton(onClick = onEdit, enabled = canEdit) {
            Text(tr("Modifier la limite", "Edit limit"), fontSize = 13.sp)
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
        ScreenHeading(tr("LES DÉFIS", "CHALLENGES"), tr("Un jour à la fois.", "One day at a time."),
            if (personal) tr("Crée les habitudes qui te ressemblent.", "Build habits that suit you.") else tr("De petites actions pour de vrais progrès.", "Small actions for real progress."))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FilterChip(selected = !personal, onClick = { personal = false },
                modifier = Modifier.weight(1f), label = { Text(tr("Défis proposés", "Suggested challenges")) })
            FilterChip(selected = personal, onClick = { personal = true },
                modifier = Modifier.weight(1f), label = { Text(tr("Mes défis", "My challenges")) })
        }
        if (personal) {
            CustomChallengesSection(snapshot, onOffers, onCreateCustom, onToggleCustomStep,
                onValidateCustom, onDeleteCustom)
        } else {
            AppCardSurface {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(tr("Ta série", "Your series"), color = Cream, fontSize = 20.sp, fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f))
                    Text("$finished / 10", color = Muted, fontSize = 13.sp)
                    TextButton(onClick = { showRules = !showRules }) {
                        Text(if (showRules) tr("Fermer", "Close") else tr("Règles", "Rules"), fontSize = 12.sp)
                    }
                }
                LinearProgressIndicator(progress = { finished / 10f },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                    color = Mint, trackColor = CardLighter)
                Text(tr("+5 points pour 5 défis terminés", "+5 points for every 5 completed challenges"), color = Mint,
                    fontWeight = FontWeight.Medium, fontSize = 13.sp, lineHeight = 20.sp)
                Text(tr("Nouvelle série dans ${snapshot.daysUntilChallenges} ${if (snapshot.daysUntilChallenges == 1) "jour" else "jours"} · progression remise à zéro.", "New series in ${snapshot.daysUntilChallenges} ${if (snapshot.daysUntilChallenges == 1) "day" else "days"} · progress resets."),
                    color = Muted, fontSize = 12.sp, lineHeight = 18.sp)
                if (showRules) Text(tr("Chaque défi demande $CHALLENGE_REQUIRED_DAYS journées différentes, avec une validation par date locale. Les jours peuvent être espacés. Les jours intermédiaires ne donnent pas de points. Les défis entièrement terminés se cumulent entre les séries pour le bonus.", "Each challenge requires $CHALLENGE_REQUIRED_DAYS different days, with one check-in per local date. The days do not have to be consecutive. Individual days do not earn points. Fully completed challenges count towards the bonus across series."),
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
                                Text(tr("$completedDays / ${progress.requiredDays} journées validées", "$completedDays / ${progress.requiredDays} days checked in"),
                                    color = Muted, fontSize = 13.sp)
                            }
                        }
                        when {
                            done -> StatusPill(tr("✓ Défi terminé", "✓ Challenge complete"))
                            progress.validatedToday -> StatusPill(tr("✓ Validé aujourd’hui · à demain", "✓ Checked in today · see you tomorrow"))
                            else -> {
                                PrimaryAction(tr("Valider aujourd’hui", "Check in today"), onClick = { onComplete(index) },
                                    enabled = progress.canValidateToday, modifier = Modifier.fillMaxWidth())
                                if (!progress.canValidateToday) Text(tr("La date du téléphone précède une validation enregistrée. Vérifie les réglages de date.", "Your phone date is earlier than a saved check-in. Check your date settings."),
                                    color = Muted, fontSize = 12.sp, lineHeight = 18.sp)
                            }
                        }
                        TextButton(onClick = { expanded = !expanded }) {
                            Text(if (expanded) tr("Masquer les journées", "Hide days") else tr("Voir les ${progress.requiredDays} journées", "Show the ${progress.requiredDays} days"), fontSize = 13.sp)
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
private fun AppPickerDialog(
    apps: List<InstalledApp>?, loadFailed: Boolean, onRetry: () -> Unit,
    onDismiss: () -> Unit, onConfirm: (String, Int) -> Unit
) {
    var selected by remember { mutableStateOf<InstalledApp?>(null) }
    var query by remember { mutableStateOf("") }
    if (selected != null) {
        MinutesDialog(tr("Limite quotidienne de ${selected!!.label}", "Daily limit for ${selected!!.label}"), 30,
            onDismiss = onDismiss,
            onConfirm = { mins -> onConfirm(selected!!.pkg, mins) })
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(tr("Choisir une application", "Choose an app")) },
            text = {
                Column {
                    OutlinedTextField(
                        value = query, onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = apps != null && !loadFailed,
                        singleLine = true, label = { Text(tr("Rechercher", "Search")) }
                    )
                    Spacer(Modifier.height(12.dp))
                    if (loadFailed) {
                        Text(tr("Impossible de charger les applications.", "Could not load apps."), color = Muted)
                        TextButton(onClick = onRetry) { Text(tr("Réessayer", "Try again")) }
                    } else if (apps == null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(12.dp))
                            Text(tr("Chargement des applications…", "Loading apps…"), color = Muted)
                        }
                    } else {
                        val matching = apps.filter { it.label.contains(query, ignoreCase = true) }
                        if (matching.isEmpty()) {
                            Text(if (apps.isEmpty()) tr("Aucune application disponible.", "No apps available.")
                                else tr("Aucune application trouvée.", "No apps found."), color = Muted)
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
            confirmButton = { TextButton(onClick = onDismiss) { Text(tr("Fermer", "Close")) } }
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
                Text(tr("Choisis une limite par pas de 5 minutes.", "Choose a limit in 5-minute steps."), color = Muted,
                    textAlign = TextAlign.Center)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(onClick = { minutes = (minutes - 5).coerceAtLeast(5) },
                        modifier = Modifier.semantics {
                            contentDescription = tr("Diminuer la limite de 5 minutes", "Decrease the limit by 5 minutes")
                        },
                        enabled = minutes > 5) { Text("−", fontSize = 20.sp) }
                    Text("$minutes min", fontSize = 27.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    OutlinedButton(onClick = { minutes = (minutes + 5).coerceAtMost(1440) },
                        modifier = Modifier.semantics {
                            contentDescription = tr("Augmenter la limite de 5 minutes", "Increase the limit by 5 minutes")
                        },
                        enabled = minutes < 1440) { Text("+", fontSize = 20.sp) }
                }
            }
        },
        confirmButton = { Button(onClick = { onConfirm(minutes) }) { Text(tr("Confirmer", "Confirm")) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(tr("Annuler", "Cancel")) } }
    )
}

@Composable
private fun DisciplineCard(snapshot: MareeSnapshot, onChallenges: () -> Unit) {
    Card(shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MareePalette.HeroBackground)) {
        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(Modifier.weight(1f)) {
                    Text(tr("TES POINTS", "YOUR POINTS"), color = MareePalette.OnHeroMuted,
                        style = MaterialTheme.typography.labelMedium)
                    Text(formatPoints(snapshot.points), color = MareePalette.OnHero,
                        fontSize = 42.sp, lineHeight = 48.sp, fontWeight = FontWeight.Bold)
                }
                Icon(painterResource(R.drawable.ic_nav_challenges), contentDescription = null,
                    tint = MareePalette.HeroAccent, modifier = Modifier.size(30.dp))
            }
            Text(tr("Les défis réalisés sur plusieurs jours te permettent d’en gagner. À 0 point, l’ajout d’applications et la modification des limites sont bloqués.",
                "Challenges completed over several days help you earn points. At 0 points, adding apps and changing limits are blocked."),
                color = MareePalette.OnHeroMuted, style = MaterialTheme.typography.bodyMedium)
            Button(onClick = onChallenges, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(
                    containerColor = MareePalette.HeroAccent, contentColor = MareePalette.OnHeroAccent)) {
                Text(tr("Choisir un défi", "Choose a challenge"))
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
