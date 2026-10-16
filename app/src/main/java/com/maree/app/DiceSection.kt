package com.maree.app

import android.animation.ValueAnimator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
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

    AppCardSurface {
        SectionLabel(tr("Le Dé Selkie", "Selkie Dice"))
        StatusPill(tr("Gratuit · hors ligne", "Free · offline"))
        Text(tr("Une pause au hasard", "A pause at random"), style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold, color = MareePalette.Ink)
        Text(tr("Choisis un mode, lance le dé, puis décide si tu veux garder son idée.",
            "Choose a mode, roll the dice, then decide whether to keep the idea."), color = MareePalette.Muted)
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(DiceMode.ACTIVITY to tr("Activité", "Activity"), DiceMode.APPLICATION to tr("Application", "App"),
                DiceMode.WEEK to tr("Semaine", "Week")).forEach { (choice, title) ->
                FilterChip(selected = mode == choice, enabled = !rolling && !busy, onClick = {
                    mode = choice; invalidate(); showFaces = false
                }, label = { Text(title) })
            }
        }
        when (mode) {
            DiceMode.ACTIVITY -> {
                Text(tr("Six petites activités à faire aujourd’hui, loin du défilement.",
                    "Six small things to do today, away from scrolling."), color = MareePalette.Muted)
                TextButton(onClick = { showFaces = !showFaces }) {
                    Text(if (showFaces) tr("Masquer les six faces", "Hide the six faces") else tr("Voir les six faces", "See the six faces"))
                }
                if (showFaces) diceActivities.forEachIndexed { index, activity ->
                    Text("${index + 1}. ${if (language == AppLocale.ENGLISH) activity.english else activity.french}", color = MareePalette.Ink)
                }
            }
            DiceMode.APPLICATION, DiceMode.WEEK -> {
                Text(if (mode == DiceMode.APPLICATION)
                    tr("Choisis de 1 à 6 applications suivies : le résultat propose une pause de 20 minutes aujourd’hui.",
                        "Choose 1 to 6 tracked apps: the result suggests a 20-minute break today.")
                    else tr("Choisis une application. Faces 1–2 : un jour ; 3–4 : deux jours ; 5–6 : trois jours, dans les sept prochains jours, aujourd’hui compris.",
                        "Choose one app. Faces 1–2: one day; 3–4: two days; 5–6: three days, within the next seven days including today."),
                    color = MareePalette.Muted)
                if (packages.isEmpty()) Text(tr("Ajoute d’abord une application suivie depuis l’accueil. Le mode Activité reste disponible.",
                    "Add a tracked app from Home first. Activity mode is still available."), color = MareePalette.Muted)
                packages.take(visibleApps).forEach { pkg ->
                    val selected = if (mode == DiceMode.APPLICATION) pkg in selectedApps else pkg == weekApp
                    FilterChip(selected = selected,
                        enabled = !rolling && !busy && (mode == DiceMode.WEEK || selected || selectedApps.size < 6),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            if (mode == DiceMode.APPLICATION) selectedApps = if (selected) selectedApps - pkg else selectedApps + pkg
                            else weekApp = if (selected) null else pkg
                            invalidate()
                        }, label = { Text(getName(pkg), maxLines = 2) })
                }
                if (packages.size > visibleApps) TextButton(enabled = !rolling && !busy, onClick = { visibleApps += 8 }) {
                    Text(tr("Voir plus d’applications", "Show more apps"))
                }
                if (mode == DiceMode.APPLICATION && selectedApps.isNotEmpty()) Text(
                    tr("${selectedApps.size}/6 sélectionnées. Chaque application choisie a la même chance ; le numéro du dé est visuel.",
                        "${selectedApps.size}/6 selected. Each selected app has the same chance; the dice number is visual."),
                    color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall)
            }
        }
        DiceFace(face, rolling, rotation.value, Modifier.align(Alignment.CenterHorizontally))
        PrimaryAction(if (rolling) tr("Le dé tourne…", "Rolling…") else if (result == null) tr("Lancer le dé", "Roll the dice")
            else tr("Relancer le dé", "Roll again"), onClick = ::roll, modifier = Modifier.fillMaxWidth(),
            enabled = !rolling && !busy && (mode == DiceMode.ACTIVITY || mode == DiceMode.APPLICATION && selectedApps.isNotEmpty() || mode == DiceMode.WEEK && weekApp != null))
        result?.let { target ->
            val appLabel = target.appPackage?.let(getName).orEmpty()
            val activity = diceActivities[target.face - 1]
            val count = diceWeekDayCount(target.face)
            Text(when (target.mode) {
                DiceMode.ACTIVITY -> if (language == AppLocale.ENGLISH) activity.english else activity.french
                DiceMode.APPLICATION -> tr("20 minutes sans $appLabel", "20 minutes without $appLabel")
                DiceMode.WEEK -> tr("$count journée${if (count > 1) "s" else ""} sans $appLabel", "$count day${if (count > 1) "s" else ""} without $appLabel")
            }, color = MareePalette.Ink, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
            if (target.mode == DiceMode.WEEK && !saved) {
                Text(tr("Choisis exactement $count date${if (count > 1) "s" else ""} : ${dates.size}/$count.",
                    "Choose exactly $count date${if (count > 1) "s" else ""}: ${dates.size}/$count."), color = MareePalette.Muted)
                diceWeekDates(target.date).forEach { date ->
                    val checked = date in dates
                    val enabled = !busy && (checked || dates.size < count)
                    Row(Modifier.fillMaxWidth().heightIn(min = 48.dp).toggleable(value = checked, enabled = enabled, role = Role.Checkbox,
                        onValueChange = { dates = if (checked) dates - date else dates + date; issue = false }),
                        verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = checked, onCheckedChange = null, enabled = enabled)
                        Text(date.format(DateTimeFormatter.ofPattern(tr("EEEE d MMMM", "EEEE, MMMM d"), AppLanguage.locale)), color = MareePalette.Ink)
                    }
                }
            }
            if (saved) {
                Text(tr("Enregistré dans ton calendrier. Coche les tâches toi-même quand tu les as réalisées.",
                    "Saved in your calendar. Check off the tasks yourself when you complete them."), color = MareePalette.Teal)
                TextButton(onClick = onCalendar) { Text(tr("Ouvrir le calendrier", "Open calendar")) }
            } else {
                Text(tr("La confirmation ajoute ${if (target.mode == DiceMode.WEEK) count else 1} tâche${if (target.mode == DiceMode.WEEK && count > 1) "s" else ""} au calendrier, à valider toi-même.",
                    "Confirming adds ${if (target.mode == DiceMode.WEEK) count else 1} personal calendar task${if (target.mode == DiceMode.WEEK && count > 1) "s" else ""}, for you to check off."), color = MareePalette.Muted)
                PrimaryAction(if (busy) tr("Enregistrement…", "Saving…") else tr("Confirmer cet objectif", "Confirm this goal"),
                    onClick = ::confirm, enabled = !busy && !rolling && (target.mode != DiceMode.WEEK || dates.size == count), modifier = Modifier.fillMaxWidth())
            }
        }
        if (issue) Text(tr("L’objectif n’a pas pu être enregistré. Vérifie les dates, les applications suivies et la place dans le calendrier (500 tâches maximum), puis réessaie.",
            "The goal could not be saved. Check the dates, tracked apps and calendar space (500 tasks maximum), then try again."), color = MareePalette.Danger)
        Text(tr("Ces objectifs sont volontaires : aucun point, aucun blocage supplémentaire ni rappel automatique. Tes limites et les verrouillages habituels restent actifs.",
            "These are voluntary goals: no points, extra blocking or automatic reminders. Your normal limits and locks remain active."),
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
    Canvas(modifier.size(92.dp).graphicsLayer { rotationZ = rotation }
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
