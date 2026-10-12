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
                NavigationBar(containerColor = MareePalette.NavigationBackground, tonalElevation = 0.dp,
                    modifier = Modifier.heightIn(min = (80f + extraNavHeight).dp)) {
                    listOf(
                        Triple(tr("Accueil", "Home"), R.drawable.ic_nav_home, 0),
                        Triple(tr("Quotidien", "Daily"), R.drawable.ic_nav_daily, 1),
                        Triple(tr("Défis", "Challenges"), R.drawable.ic_nav_challenges, 2),
                        Triple(tr("Coach", "Coach"), R.drawable.ic_nav_coach, 3),
                        Triple(tr("Plus", "More"), R.drawable.ic_nav_more, 4)
                    ).forEach { (name, icon, index) ->
                        NavigationBarItem(
                            selected = tab == index, onClick = { tab = index },
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
                        onAccount = { infoMessage = tr("Cette version fonctionne sans compte ; ton suivi reste sur ce téléphone. Un compte Selkie est prévu pour retrouver tes droits d’abonnement, avec un seul téléphone connecté à la fois. Il n’est pas encore actif : il demande un serveur de connexion et une vérification des achats Google Play. Tes points et défis ne sont pas synchronisés.", "This version works without an account; your tracking stays on this phone. A Selkie account is planned to restore subscription access, with only one phone signed in at a time. Accounts are not active yet: they need an authentication server and Google Play purchase verification. Your points and challenges are not synced.") }
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
                store.removeApp(item.packageName); removeApp = null; refresh()
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
            text = { Text(tr("Tes applications suivies, limites, verrous, points, défis, choix de démonstration, d’accueil, de langue et d’apparence, ainsi que les questions du coach enregistrées sur ce téléphone seront effacés. Le suivi sera arrêté. Cette action est irréversible et ne supprime pas les statistiques Android.", "Your tracked apps, limits, locks, points, challenges, demo, onboarding, language and appearance choices, and coach questions saved on this phone will be deleted. Tracking will stop. This cannot be undone and does not delete Android usage statistics.")) },
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
                        val language = AppLanguage.reset(context)
                        val appearance = AppAppearance.reset(context)
                        local && coach && offlineCoach && language && appearance
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
        item {
            ScreenHeading(tr("TON ESPACE", "YOUR SPACE"), tr("Reprends ton temps.", "Take back your time."),
                tr("Des limites simples. Plus de place pour toi.", "Simple limits. More room for you."))
        }
        item {
            DisciplineCard(snapshot, onChallenges)
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                HomeMetric(
                    value = snapshot.apps.size.toString(),
                    label = tr("Applications suivies", "Tracked apps"),
                    modifier = Modifier.weight(1f)
                )
                HomeMetric(
                    value = snapshot.checkedChallenges.size.toString(),
                    label = tr("Défis terminés · série", "Completed challenges · series"),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        if (!active) item {
            AppCardSurface {
                StatusPill(tr("Suivi en pause", "Tracking paused"))
                Text(tr("Active le suivi pour mesurer tes limites et afficher les écrans de pause.", "Turn on tracking to measure your limits and show pause screens."),
                    color = Muted, fontSize = 14.sp, lineHeight = 21.sp)
                PrimaryAction(tr("Activer le suivi", "Turn on tracking"), onClick = onSetup, modifier = Modifier.fillMaxWidth())
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(tr("Tes applications", "Your apps"), color = Cream, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    Text(if (appLimit == null) tr("${snapshot.apps.size} suivies · sans plafond", "${snapshot.apps.size} tracked · no limit")
                        else tr("${snapshot.apps.size} sur $appLimit suivies", "${snapshot.apps.size} of $appLimit tracked"), color = Muted, fontSize = 12.sp)
                }
                PrimaryAction(tr("+ Ajouter", "+ Add"), onClick = onAdd,
                    enabled = snapshot.points > 0 && (appLimit == null || snapshot.apps.size < appLimit))
            }
        }
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
                OutlinedButton(onClick = onAdd, enabled = snapshot.points > 0,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp), shape = RoundedCornerShape(14.dp)) {
                    Text(tr("Choisir une application", "Choose an app"))
                }
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
private fun MoreTab(
    active: Boolean, notificationsGranted: Boolean, onSetup: () -> Unit, onPause: () -> Unit,
    onNotifications: () -> Unit, onPrivacy: () -> Unit, plan: PlanType,
    onDemoPlan: (PlanType) -> Unit, onAccount: () -> Unit
) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        ScreenHeading(tr("TES RÉGLAGES", "YOUR SETTINGS"), tr("À ta façon.", "Your way."), tr("Ton suivi, tes données, tes choix.", "Your tracking, your data, your choices."))
        LanguageSelector()
        AppearanceSelector()
        AppCardSurface {
            SectionLabel(tr("Le suivi", "Tracking"), if (active) tr("Actif", "Active") else tr("En pause", "Paused"))
            Text(if (active) tr("Les limites de tes applications sont surveillées.", "Your app limits are being monitored.")
                else tr("Aucune application n’est surveillée ou bloquée.", "No apps are being monitored or blocked."),
                color = Muted, fontSize = 14.sp, lineHeight = 21.sp)
            PrimaryAction(if (active) tr("Gérer les autorisations", "Manage permissions") else tr("Activer le suivi", "Turn on tracking"),
                onClick = onSetup, modifier = Modifier.fillMaxWidth())
            if (active) TextButton(onClick = onPause) { Text(tr("Mettre le suivi en pause", "Pause tracking")) }
            if (!notificationsGranted) {
                HorizontalDivider(color = MareePalette.Outline)
                TextButton(onClick = onNotifications) { Text(tr("Activer les alertes facultatives", "Turn on optional alerts")) }
            }
        }
        AppCardSurface {
            Text(tr("Tes données, sous ton contrôle", "Your data, under your control"), fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            Text(tr("Ton suivi reste sur ton téléphone. Consulte les informations de confidentialité ou efface tes données.", "Your tracking stays on your phone. Read the privacy information or delete your data."),
                color = Muted, fontSize = 14.sp, lineHeight = 21.sp)
            OutlinedButton(onClick = onPrivacy, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                shape = RoundedCornerShape(14.dp)) { Text(tr("Confidentialité et données", "Privacy and data")) }
            TextButton(onClick = onAccount) { Text(tr("Compte et changement de téléphone", "Account and switching phones"), fontSize = 13.sp) }
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
                        enabled = minutes > 5) { Text("−", fontSize = 20.sp) }
                    Text("$minutes min", fontSize = 27.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    OutlinedButton(onClick = { minutes = (minutes + 5).coerceAtMost(1440) },
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
    Card(shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MareePalette.HeroBackground)) {
        Column(Modifier.fillMaxWidth().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(tr("TON CAPITAL\nDISCIPLINE", "YOUR DISCIPLINE\nBALANCE"), color = MareePalette.HeroAccent,
                    style = MaterialTheme.typography.labelMedium, letterSpacing = 1.5.sp,
                    modifier = Modifier.weight(1f))
                Surface(color = MareePalette.HeroAccent, shape = CircleShape) {
                    Icon(painterResource(R.drawable.ic_nav_challenges), contentDescription = null,
                        tint = MareePalette.OnHeroAccent,
                        modifier = Modifier.padding(12.dp).size(24.dp))
                }
            }
            Column {
                Text(formatPoints(snapshot.points), color = MareePalette.OnHero,
                    fontSize = 64.sp, lineHeight = 70.sp, fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-2).sp)
                Text(tr("points pour avancer", "points to keep moving"), color = MareePalette.OnHeroMuted,
                    style = MaterialTheme.typography.bodyMedium)
            }
            HorizontalDivider(color = MareePalette.OnHero.copy(alpha = 0.18f))
            Text(tr("Chaque petit pas compte.", "Every small step counts."), color = MareePalette.OnHero,
                style = MaterialTheme.typography.titleMedium)
            Button(onClick = onChallenges,
                modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MareePalette.HeroAccent,
                    contentColor = MareePalette.OnHeroAccent)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(tr("Explorer les défis", "Explore challenges"), style = MaterialTheme.typography.labelLarge,
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
