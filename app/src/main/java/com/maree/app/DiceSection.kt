package com.maree.app

import android.animation.ValueAnimator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

/** A free, optional offline prompt. It writes ordinary personal calendar tasks only after confirmation. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun DiceSection(
    snapshot: MareeSnapshot,
    getName: (String) -> String,
    onCalendar: () -> Unit,
    onTasksChanged: () -> Unit
) {
    val context = LocalContext.current
    val store = remember(context) { CalendarTaskStore(context) }
    val scope = rememberCoroutineScope()
    val packages = snapshot.apps.map { it.packageName }.distinct()
    val currentPackages by rememberUpdatedState(packages)
    var mode by remember { mutableStateOf(DiceMode.ACTIVITY) }
    var selectedApps by remember { mutableStateOf<List<String>>(emptyList()) }
    var weekApp by remember { mutableStateOf<String?>(null) }
    var visibleApps by remember { mutableIntStateOf(8) }
    var result by remember { mutableStateOf<DiceResult?>(null) }
    var dates by remember { mutableStateOf<Set<LocalDate>>(emptySet()) }
    var rolling by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    var saved by remember { mutableStateOf(false) }
    var issue by remember { mutableStateOf(false) }
    var showFaces by remember { mutableStateOf(false) }
    var choosingApps by remember { mutableStateOf(true) }
    val resultAnchor = remember { BringIntoViewRequester() }
    var face by remember { mutableIntStateOf(1) }
    var rollGeneration by remember { mutableIntStateOf(0) }
    val rotation = remember { Animatable(0f) }
    val language = AppLanguage.current

    fun invalidate() {
        rollGeneration++; result = null; dates = emptySet(); saved = false; issue = false
    }
    LaunchedEffect(packages) {
        selectedApps = selectedApps.filter { it in packages }
        if (weekApp !in packages) weekApp = null
        invalidate(); rolling = false
    }
    fun roll() {
        if (busy || rolling) return
        val chosen = selectedApps.toList()
        val chosenWeekApp = weekApp
        if (mode == DiceMode.APPLICATION && chosen.isEmpty() || mode == DiceMode.WEEK && chosenWeekApp == null) return
        invalidate(); rolling = true
        val token = rollGeneration
        val chosenMode = mode
        val finalFace = Random.nextInt(1, 7)
        val chosenApp = when (chosenMode) {
            DiceMode.ACTIVITY -> null
            DiceMode.APPLICATION -> diceSelectedApplication(chosen, Random.nextInt(chosen.size))
            DiceMode.WEEK -> chosenWeekApp
        }
        scope.launch {
            try {
                rotation.snapTo(0f)
                if (ValueAnimator.areAnimatorsEnabled()) coroutineScope {
                    val movement = launch { rotation.animateTo(720f, tween(750, easing = FastOutSlowInEasing)) }
                    repeat(6) { index -> face = index + 1; delay(75L) }
                    movement.join()
                }
                if (token == rollGeneration) {
                    face = finalFace
                    if (chosenApp == null && chosenMode != DiceMode.ACTIVITY ||
                        chosenApp != null && chosenApp !in currentPackages) issue = true
                    else result = DiceResult(chosenMode, finalFace, LocalDate.now(), chosenApp)
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                if (token == rollGeneration) issue = true
            } finally {
                if (token == rollGeneration) rolling = false
            }
        }
    }
    fun confirm() {
        val target = result ?: return
        if (busy || rolling || saved) return
        if (target.appPackage != null && target.appPackage !in currentPackages) { issue = true; return }
        val tasks = diceTaskBatch(target, dates.toList(), target.appPackage?.let(getName).orEmpty(), language, LocalDate.now())
        if (tasks == null) { issue = true; return }
        busy = true; issue = false
        scope.launch {
            try {
                val success = withContext(Dispatchers.IO) { store.saveBatch(tasks, target.id) }
                if (success) { saved = true; onTasksChanged() } else issue = true
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) { issue = true
            } finally { busy = false }
        }
    }

    val canRoll = mode == DiceMode.ACTIVITY ||
        mode == DiceMode.APPLICATION && selectedApps.isNotEmpty() ||
        mode == DiceMode.WEEK && weekApp != null

    // Bring the proposal, rather than the whole long week card, into the viewport.
    // This also works with system animations disabled; no decorative animation is required.
    LaunchedEffect(result?.id) {
        if (result != null) resultAnchor.bringIntoView()
    }

    AppCardSurface {
        SectionLabel(tr("Dé Selkie", "Selkie Dice"), tr("Gratuit", "Free"))
        Text(tr("Une idée de pause, puis tu choisis de la garder ou non.",
            "Get a break idea, then choose whether to keep it."),
            color = MareePalette.Muted, style = MaterialTheme.typography.bodyMedium)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(DiceMode.ACTIVITY to tr("Activité", "Activity"), DiceMode.APPLICATION to tr("Appli", "App"),
                DiceMode.WEEK to tr("Semaine", "Week")).forEach { (choice, title) ->
                FilterChip(selected = mode == choice, enabled = !rolling && !busy, onClick = {
                    mode = choice; invalidate(); showFaces = false; choosingApps = true
                }, modifier = Modifier.weight(1f).heightIn(min = 48.dp), label = { Text(title) })
            }
        }
        Text(when (mode) {
            DiceMode.ACTIVITY -> tr("Une petite activité sans écran, à faire aujourd’hui.",
                "A small screen-free activity to do today.")
            DiceMode.APPLICATION -> tr("Une pause de 20 minutes sans l’une des applications choisies.",
                "A 20-minute break from one of the apps you choose.")
            DiceMode.WEEK -> tr("De 1 à 3 journées sans une application. Tu choisiras les dates après le lancer.",
                "From 1 to 3 days without one app. Choose the dates after rolling.")
        }, color = MareePalette.Ink, style = MaterialTheme.typography.bodyMedium)
        if (mode != DiceMode.ACTIVITY) {
            if (packages.isEmpty()) {
                Text(tr("Ajoute une application suivie depuis l’accueil, ou choisis le mode Activité.",
                    "Add a tracked app from Home, or choose Activity mode."), color = MareePalette.Muted)
            } else {
                val selectionLabel = if (mode == DiceMode.APPLICATION)
                    tr("${selectedApps.size}/6 applications choisies", "${selectedApps.size}/6 apps chosen")
                    else weekApp?.let(getName) ?: tr("Aucune application choisie", "No app chosen")
                TextButton(enabled = !rolling && !busy, onClick = { choosingApps = !choosingApps },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                    Text(if (choosingApps) tr("Choisis ${if (mode == DiceMode.APPLICATION) "1 à 6 applications" else "une application"}",
                        "Choose ${if (mode == DiceMode.APPLICATION) "1 to 6 apps" else "one app"}")
                    else tr("$selectionLabel · Modifier", "$selectionLabel · Edit"))
                }
                if (choosingApps) {
                    packages.take(visibleApps).forEach { pkg ->
                        val selected = if (mode == DiceMode.APPLICATION) pkg in selectedApps else pkg == weekApp
                        FilterChip(selected = selected,
                            enabled = !rolling && !busy && (mode == DiceMode.WEEK || selected || selectedApps.size < 6),
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                            onClick = {
                                if (mode == DiceMode.APPLICATION) selectedApps = if (selected) selectedApps - pkg else selectedApps + pkg
                                else weekApp = if (selected) null else pkg
                                invalidate()
                            }, label = { Text(getName(pkg), maxLines = 2) })
                    }
                    if (packages.size > visibleApps) TextButton(enabled = !rolling && !busy,
                        onClick = { visibleApps += 8 }, modifier = Modifier.heightIn(min = 48.dp)) {
                        Text(tr("Voir plus d’applications", "Show more apps"))
                    }
                    if (canRoll) TextButton(enabled = !rolling && !busy,
                        onClick = { choosingApps = false }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                        Text(tr("Garder cette sélection", "Keep this selection"))
                    }
                }
            }
        }
    }

    AppCardSurface(containerColor = if (saved) MareePalette.Mint else MareePalette.Lilac) {
        val target = result
        val appLabel = target?.appPackage?.let(getName).orEmpty()
        val count = target?.let { diceWeekDayCount(it.face) } ?: 0
        Row(Modifier.fillMaxWidth().bringIntoViewRequester(resultAnchor),
            horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            DiceFace(face, rolling, rotation.value)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(if (saved) tr("OBJECTIF ENREGISTRÉ", "GOAL SAVED")
                    else if (target != null) tr("TA PROPOSITION", "YOUR IDEA")
                    else tr("À TOI DE LANCER", "YOUR TURN TO ROLL"),
                    style = MaterialTheme.typography.labelSmall, color = MareePalette.Lavender)
                Text(when {
                    rolling -> tr("Une idée arrive…", "An idea is coming…")
                    target == null -> tr("Quelle sera ta pause ?", "What will your break be?")
                    target.mode == DiceMode.ACTIVITY -> diceActivities[target.face - 1].let {
                        if (language == AppLocale.ENGLISH) it.english else it.french
                    }
                    target.mode == DiceMode.APPLICATION -> tr("20 minutes sans $appLabel", "20 minutes without $appLabel")
                    else -> tr("$count journée${if (count > 1) "s" else ""} sans $appLabel",
                        "$count day${if (count > 1) "s" else ""} without $appLabel")
                }, color = MareePalette.Ink, fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
            }
        }
        if (target == null) {
            Text(tr("Rien n’est ajouté au calendrier avant ta confirmation.",
                "Nothing is added to your calendar until you confirm."),
                color = MareePalette.Muted, style = MaterialTheme.typography.bodyMedium)
            PrimaryAction(if (rolling) tr("Le dé tourne…", "Rolling…") else tr("Lancer le dé", "Roll the dice"),
                onClick = { choosingApps = false; roll() }, modifier = Modifier.fillMaxWidth(),
                enabled = !rolling && !busy && canRoll)
        } else if (saved) {
            Text(tr("Retrouve cet objectif dans le calendrier et coche-le quand tu l’as réalisé.",
                "Find this goal in your calendar and check it off when you have completed it."),
                color = MareePalette.Ink, style = MaterialTheme.typography.bodyMedium)
            PrimaryAction(tr("Voir dans le calendrier", "View in calendar"), onCalendar, Modifier.fillMaxWidth())
            TextButton(onClick = ::roll, enabled = !rolling && !busy && canRoll,
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Text(tr("Trouver une autre idée", "Find another idea"))
            }
        } else {
            if (target.mode == DiceMode.WEEK) {
                Text(tr("Choisis $count date${if (count > 1) "s" else ""} · ${dates.size}/$count sélectionnée${if (dates.size > 1) "s" else ""}",
                    "Choose $count date${if (count > 1) "s" else ""} · ${dates.size}/$count selected"),
                    color = MareePalette.Ink, fontWeight = FontWeight.SemiBold)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    diceWeekDates(target.date).forEach { date ->
                        val checked = date in dates
                        val enabled = !busy && (checked || dates.size < count)
                        Row(Modifier.fillMaxWidth().heightIn(min = 48.dp)
                            .background(if (checked) MareePalette.Surface else MareePalette.Surface.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .toggleable(value = checked, enabled = enabled, role = Role.Checkbox,
                                onValueChange = { dates = if (checked) dates - date else dates + date; issue = false })
                            .padding(end = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = checked, onCheckedChange = null, enabled = enabled)
                            Text(date.format(DateTimeFormatter.ofPattern(tr("EEEE d MMMM", "EEEE, MMMM d"), AppLanguage.locale)),
                                color = MareePalette.Ink, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            } else {
                Text(tr("Pour aujourd’hui · ${target.date.format(DateTimeFormatter.ofPattern(tr("d MMMM", "MMMM d"), AppLanguage.locale))}",
                    "For today · ${target.date.format(DateTimeFormatter.ofPattern("MMMM d", AppLanguage.locale))}"),
                    color = MareePalette.Muted, style = MaterialTheme.typography.bodyMedium)
            }
            PrimaryAction(if (busy) tr("Enregistrement…", "Saving…")
                else if (target.mode == DiceMode.WEEK) tr("Ajouter ces $count dates au calendrier", "Add these $count dates to calendar")
                else tr("Ajouter à mon calendrier", "Add to my calendar"),
                onClick = ::confirm, enabled = !busy && !rolling && (target.mode != DiceMode.WEEK || dates.size == count),
                modifier = Modifier.fillMaxWidth())
            TextButton(onClick = ::roll, enabled = !rolling && !busy && canRoll,
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Text(tr("Cette idée ne me plaît pas · Relancer", "Not this idea · Roll again"))
            }
            Text(tr("Une tâche personnelle à cocher toi-même. Le dé ne change pas tes limites.",
                "A personal task for you to check off. The dice does not change your limits."),
                color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall)
        }
        if (issue) Text(tr("L’objectif n’a pas pu être enregistré. Vérifie les dates, les applications suivies et la place dans le calendrier (500 tâches maximum), puis réessaie.",
            "The goal could not be saved. Check the dates, tracked apps and calendar space (500 tasks maximum), then try again."), color = MareePalette.Danger)
    }

    AppCardSurface {
        TextButton(onClick = { showFaces = !showFaces }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
            Text(if (showFaces) tr("Masquer les détails", "Hide details") else when (mode) {
                DiceMode.ACTIVITY -> tr("Les 6 idées possibles", "The 6 possible ideas")
                DiceMode.APPLICATION -> tr("Comment l’application est choisie", "How the app is chosen")
                DiceMode.WEEK -> tr("Comment les jours sont choisis", "How the days are chosen")
            })
        }
        if (showFaces) when (mode) {
            DiceMode.ACTIVITY -> diceActivities.forEachIndexed { index, activity ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    StatusPill("${index + 1}", color = MareePalette.Lavender, background = MareePalette.Lilac)
                    Text(if (language == AppLocale.ENGLISH) activity.english else activity.french,
                        color = MareePalette.Ink, style = MaterialTheme.typography.bodyMedium)
                }
            }
            DiceMode.APPLICATION -> Text(tr("Chaque application de ta sélection a la même chance. Le numéro du dé est décoratif : la proposition indique l’application choisie.",
                "Each app in your selection has the same chance. The dice number is decorative: the idea names the chosen app."), color = MareePalette.Ink)
            DiceMode.WEEK -> Text(tr("Faces 1 ou 2 : un jour. Faces 3 ou 4 : deux jours. Faces 5 ou 6 : trois jours. Tu choisis les dates parmi les sept prochains jours, aujourd’hui compris.",
                "Faces 1 or 2: one day. Faces 3 or 4: two days. Faces 5 or 6: three days. Choose dates within the next seven days, including today."), color = MareePalette.Ink)
        }
        Text(tr("Hors ligne · Objectifs volontaires, sans point, blocage supplémentaire ni rappel automatique. Tes limites et verrouillages habituels restent actifs.",
            "Offline · Voluntary goals, with no points, extra blocking or automatic reminders. Your normal limits and locks remain active."),
            color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall)
    }
}

/** Native dots and a soft raised face: one short animation, with no image or network dependency. */
@Composable
private fun DiceFace(face: Int, rolling: Boolean, rotation: Float, modifier: Modifier = Modifier) {
    val description = if (rolling) tr("Dé en mouvement", "Dice rolling") else tr("Dé : face $face", "Dice: face $face")
    val ink = MareePalette.Ink
    val surface = MareePalette.Surface
    val lilac = MareePalette.Lilac
    Canvas(modifier.size(72.dp).graphicsLayer { rotationZ = rotation }
        .semantics { contentDescription = description }) {
        drawRoundRect(ink.copy(alpha = 0.10f), topLeft = Offset(2.dp.toPx(), 6.dp.toPx()),
            size = size.copy(width = size.width - 4.dp.toPx(), height = size.height - 8.dp.toPx()), cornerRadius = CornerRadius(22.dp.toPx()))
        drawRoundRect(Brush.linearGradient(listOf(surface, lilac)), size = size.copy(height = size.height - 6.dp.toPx()), cornerRadius = CornerRadius(22.dp.toPx()))
        val coordinates = when (face) {
            1 -> listOf(1 to 1)
            2 -> listOf(0 to 0, 2 to 2)
            3 -> listOf(0 to 0, 1 to 1, 2 to 2)
            4 -> listOf(0 to 0, 2 to 0, 0 to 2, 2 to 2)
            5 -> listOf(0 to 0, 2 to 0, 1 to 1, 0 to 2, 2 to 2)
            else -> listOf(0 to 0, 2 to 0, 0 to 1, 2 to 1, 0 to 2, 2 to 2)
        }
        coordinates.forEach { (x, y) -> drawCircle(ink, radius = 4.2.dp.toPx(),
            center = Offset(size.width * (0.29f + 0.21f * x), size.height * (0.26f + 0.21f * y))) }
    }
}
