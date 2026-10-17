package com.maree.app

import android.animation.ValueAnimator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

/** A separate, explicitly chosen mode. Composition and restoring a screen never charge points. */
@Composable
internal fun HonorDiceSection(
    snapshot: MareeSnapshot,
    pendingId: String?,
    onPendingIdChange: (String?) -> Unit,
    onBalanceChanged: () -> Unit
) {
    val context = LocalContext.current
    val store = remember(context) { Store(context) }
    val scope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) }
    var receipt by remember { mutableStateOf<HonorDiceResult?>(null) }
    var balance by remember { mutableStateOf(snapshot.pointUnits.coerceIn(0, MAX_POINT_UNITS)) }
    var ready by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    var issue by remember { mutableStateOf(false) }

    LaunchedEffect(store, snapshot.pointUnits) {
        val saved = withContext(Dispatchers.IO) { store.lastHonorDiceResult() to store.pointUnits() }
        receipt = saved.first
        balance = saved.second
        ready = true
    }

    fun roll() {
        if (!ready || busy || pendingId == null && balance == 0) return
        // Hoisted and saveable in MainActivity: leaving the tab or rotating retains the same attempt.
        val operation = pendingId ?: UUID.randomUUID().toString()
        onPendingIdChange(operation)
        busy = true
        issue = false
        scope.launch {
            try {
                val result = withContext(Dispatchers.IO) { store.rollHonorDie(operation) }
                balance = withContext(Dispatchers.IO) { store.pointUnits() }
                if (result == null) {
                    // Android can update memory before reporting a disk failure. Keep the token;
                    // the retry confirms that same receipt instead of drawing or charging again.
                    val saved = withContext(Dispatchers.IO) { store.lastHonorDiceResult() }
                    if (balance == 0 && saved?.operationId != operation) onPendingIdChange(null)
                    else issue = true
                } else {
                    receipt = result
                    onBalanceChanged()
                    rotation.snapTo(0f)
                    if (ValueAnimator.areAnimatorsEnabled()) {
                        rotation.animateTo(720f, tween(650, easing = FastOutSlowInEasing))
                    }
                    onPendingIdChange(null)
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                issue = true
            } finally {
                busy = false
            }
        }
    }

    AppCardSurface(containerColor = MareePalette.Lilac) {
        SectionLabel(tr("POINTS D’HONNEUR", "HONOR POINTS"), "${formatPointUnits(balance)} / 100")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically) {
            DiceFace(receipt?.face ?: 1, busy, rotation.value)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(tr("Un lancer, une perte", "One roll, a deduction"),
                    color = MareePalette.Ink, style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold)
                Text(tr("1 → −10 · 2 → −20 · 3 → −30\n4 → −40 · 5 → −50 · 6 → −60 points.",
                    "1 → −10 · 2 → −20 · 3 → −30\n4 → −40 · 5 → −50 · 6 → −60 points."),
                    color = MareePalette.Ink, style = MaterialTheme.typography.bodyMedium)
            }
        }
        Text(tr("Attention : lancer ce dé retire immédiatement 10 à 60 points. Si ton solde est plus bas, tu perds seulement ce qu’il te reste. Aucun gain ni argent en jeu.",
            "Warning: rolling this die immediately deducts 10 to 60 points. If your balance is lower, only your remaining points are deducted. There are no winnings or money involved."),
            color = MareePalette.Muted, style = MaterialTheme.typography.bodyMedium)
        if (balance == 0 && pendingId == null) Text(tr(
            "À 0 point, aucun nouveau lancer. Termine un défi ou attends un bonus pour en regagner.",
            "At 0 points, you cannot start a new roll. Complete a challenge or wait for a bonus to earn points back."),
            color = MareePalette.Ink, style = MaterialTheme.typography.bodyMedium)
        PrimaryAction(when {
            busy -> tr("Lancer en cours…", "Rolling…")
            pendingId != null -> tr("Vérifier ce lancer", "Check this roll")
            else -> tr("Lancer et retirer 10 à 60 points", "Roll and deduct 10 to 60 points")
        }, onClick = ::roll, modifier = Modifier.fillMaxWidth(),
            enabled = ready && !busy && (pendingId != null || balance > 0))
        if (pendingId != null && !busy) Text(tr(
            "Un lancer reste à confirmer. Vérifie-le avec ce bouton : il reprend la même tentative sans nouveau retrait.",
            "A roll still needs confirmation. Use this button to check the same attempt without another deduction."),
            color = if (issue) MareePalette.Danger else MareePalette.Muted,
            style = MaterialTheme.typography.bodyMedium)
        receipt?.let { last ->
            Text(tr("Dernier lancer : face ${last.face} · −${formatPointUnits(last.spentUnits)} point(s). Solde après ce lancer : ${formatPointUnits(last.balanceAfterUnits)} / 100.",
                "Last roll: face ${last.face} · −${formatPointUnits(last.spentUnits)} point(s). Balance after that roll: ${formatPointUnits(last.balanceAfterUnits)} / 100."),
                color = MareePalette.Ink, style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
            if (last.pointsPerFace == 1) Text(tr(
                "Ce reçu provient de l’ancien barème. Les nouveaux lancers retirent 10 points par face.",
                "This receipt uses the previous rule. New rolls deduct 10 points per pip."),
                color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall)
        }
        Text(tr("Mode facultatif et gratuit. Activité, Appli et Semaine restent sans perte de points. Les limites et verrouillages habituels restent actifs.",
            "Optional and free. Activity, App and Week still deduct no points. Your normal limits and locks remain active."),
            color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall)
    }
}
