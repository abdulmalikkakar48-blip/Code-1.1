package com.maree.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private data class LocalCoachTip(val situation: String, val title: String, val steps: List<String>)

private val coachSituations = listOf(
    LocalCoachTip("Je scrolle sans réfléchir", "Reprends la main en trois gestes", listOf(
        "Pose ton téléphone dix minutes hors de portée.",
        "Avant de le reprendre, choisis une seule chose à y faire.",
        "Une fois cette tâche terminée, referme l’application."
    )),
    LocalCoachTip("Je dépasse mes limites", "Commence par une seule habitude", listOf(
        "Choisis une application et une limite réaliste pour commencer.",
        "Prépare une alternative courte : boire de l’eau, marcher ou lire une page.",
        "Quand l’écran de pause apparaît, essaie cette alternative."
    )),
    LocalCoachTip("J’ai du mal à tenir un défi", "Fais de la place pour un petit pas", listOf(
        "Choisis un défi adapté à ce que tu peux faire aujourd’hui.",
        "Prévois un moment précis pour le pratiquer.",
        "Les jours peuvent être espacés : une journée manquée ne supprime pas ta progression."
    ))
)

/** Le dialogue appelle le serveur ; les conseils fixes restent explicitement hors ligne. */
@Composable
internal fun CoachTab() {
    val context = LocalContext.current
    val client = remember { CoachClient(BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_PUBLIC_KEY) }
    val sessions = remember { CoachSessionStore(context) }
    val scope = rememberCoroutineScope()
    val scroll = rememberScrollState()
    var session by remember { mutableStateOf(sessions.load()) }
    var email by rememberSaveable { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var codeSent by remember { mutableStateOf(false) }
    var consent by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var question by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf<List<CoachMessage>>(emptyList()) }
    var remaining by remember { mutableStateOf<Int?>(null) }
    var localTips by rememberSaveable { mutableStateOf(false) }
    var selected by rememberSaveable { mutableIntStateOf(0) }
    val advice = coachSituations[selected.coerceIn(coachSituations.indices)]
    LaunchedEffect(messages.size, busy) {
        if (messages.isNotEmpty()) scroll.animateScrollTo(scroll.maxValue)
    }

    fun run(action: suspend () -> Unit) {
        if (busy) return
        busy = true
        error = null
        scope.launch {
            try { action() }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (failure: CoachError) { error = failure.message }
            catch (_: Exception) { error = "Connexion impossible. Vérifie Internet puis réessaie." }
            finally { busy = false }
        }
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(scroll)
            .padding(horizontal = 20.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ScreenHeading(
            eyebrow = "COACH",
            title = "Parlons de tes habitudes.",
            description = "Un échange pour trouver ton prochain petit pas."
        )
        if (!client.configured) {
            AppCardSurface {
                SectionLabel("Activer le dialogue IA")
                Text("Le serveur du coach doit être configuré par l’éditeur avant le premier échange.",
                    color = MareePalette.Muted, fontSize = 15.sp, lineHeight = 22.sp)
                Text("Aucune réponse IA n’est simulée. Les conseils hors ligne restent disponibles ci-dessous.",
                    color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 19.sp)
            }
        } else {
            AppCardSurface {
                SectionLabel("Coach IA", "100 questions gratuites")
                Text("Tes messages et les derniers échanges sont transmis au serveur Selkie (Supabase), puis à OpenAI. " +
                    "Tes applications, points et défis ne sont pas envoyés. Évite les informations sensibles. " +
                    "L’IA peut se tromper et ne remplace pas un professionnel de santé.",
                    color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 19.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = consent, onCheckedChange = { if (!busy) consent = it }, enabled = !busy)
                    Text("J’accepte l’envoi nécessaire à mes échanges avec le coach.",
                        color = MareePalette.Ink, fontSize = 14.sp, lineHeight = 20.sp)
                }
                if (session == null) {
                    Text("Connecte-toi par e-mail pour conserver ton accès aux 100 questions. Le suivi du téléphone reste sans compte.",
                        color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp)
                    OutlinedTextField(value = email, onValueChange = { email = it.take(254); codeSent = false; code = "" },
                        label = { Text("Ton adresse e-mail") }, singleLine = true, enabled = !busy,
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp))
                    PrimaryAction(if (codeSent) "Renvoyer un code" else "Recevoir un code", onClick = {
                        run {
                            withContext(Dispatchers.IO) { client.requestCode(email) }
                            codeSent = true
                        }
                    }, modifier = Modifier.fillMaxWidth(), enabled = !busy && consent && email.isNotBlank())
                    if (codeSent) {
                        OutlinedTextField(value = code, onValueChange = { code = it.filter(Char::isDigit).take(10) },
                            label = { Text("Code reçu par e-mail") }, singleLine = true, enabled = !busy,
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp))
                        PrimaryAction("Me connecter", onClick = {
                            val generation = sessions.generation()
                            run {
                                val verified = withContext(Dispatchers.IO) {
                                    client.verifyCode(email, code).also { sessions.save(it, generation) }
                                }
                                session = verified
                                code = ""
                            }
                        }, modifier = Modifier.fillMaxWidth(), enabled = !busy && consent && code.length >= 4)
                    }
                } else {
                    Text("Connecté : ${session!!.email}", color = MareePalette.Muted, fontSize = 13.sp)
                    remaining?.let {
                        Text(if (it < 0) "Accès confirmé par le serveur" else "$it questions restantes",
                            color = MareePalette.Teal, fontSize = 13.sp)
                    }
                    TextButton(enabled = !busy, onClick = {
                        run {
                            withContext(Dispatchers.IO) { sessions.clear() }
                            session = null; messages = emptyList(); remaining = null; consent = false
                        }
                    }) { Text("Me déconnecter sur ce téléphone") }
                }
            }
            messages.forEach { message ->
                AppCardSurface(containerColor = if (message.role == "user") MareePalette.Subtle else MareePalette.Surface) {
                    SectionLabel(if (message.role == "user") "Toi" else "Coach IA")
                    Text(message.text, color = MareePalette.Ink, fontSize = 16.sp, lineHeight = 24.sp)
                }
            }
            if (session != null) {
                AppCardSurface {
                    OutlinedTextField(value = question, onValueChange = { question = it.take(800) },
                        label = { Text("Ta question") }, placeholder = { Text("Comment arrêter de scroller le soir ?") },
                        minLines = 2, maxLines = 5, enabled = !busy,
                        supportingText = { Text("${question.length}/800") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp))
                    PrimaryAction(if (busy) "Le coach répond…" else "Envoyer", onClick = {
                        val outgoing = question.trim()
                        val previous = messages.toList()
                        val generation = sessions.generation()
                        run {
                            var active = session ?: return@run
                            val reply = try {
                                withContext(Dispatchers.IO) { client.ask(active, outgoing, previous) }
                            } catch (failure: CoachError) {
                                if (failure.code != "UNAUTHORIZED") throw failure
                                active = withContext(Dispatchers.IO) {
                                    client.refresh(active).also { sessions.save(it, generation) }
                                }
                                session = active
                                withContext(Dispatchers.IO) { client.ask(active, outgoing, previous) }
                            }
                            session = active
                            messages = (previous + CoachMessage("user", outgoing) +
                                CoachMessage("assistant", reply.answer)).takeLast(40)
                            remaining = reply.remaining
                            question = ""
                        }
                    }, modifier = Modifier.fillMaxWidth(),
                        enabled = !busy && consent && question.isNotBlank() && remaining != 0)
                    Text("La conversation reste en mémoire pendant l’ouverture de cet onglet. Elle n’est pas enregistrée sur le téléphone.",
                        color = MareePalette.Muted, fontSize = 12.sp, lineHeight = 18.sp)
                    TextButton(enabled = !busy, onClick = { messages = emptyList() }) { Text("Nouvelle conversation") }
                }
            }
            if (busy) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MareePalette.Teal)
            error?.let { Text(it, color = MareePalette.Danger, fontSize = 14.sp, lineHeight = 21.sp) }
        }
        TextButton(onClick = { localTips = !localTips }) {
            Text(if (localTips) "Masquer les conseils hors ligne" else "Voir les conseils hors ligne")
        }
        if (localTips) {
        AppCardSurface {
            StatusPill("Conseils hors ligne")
            Text("Comment te sens-tu aujourd’hui ?", color = MareePalette.Ink,
                fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 27.sp)
            coachSituations.forEachIndexed { index, situation ->
                val isSelected = selected == index
                OutlinedButton(
                    onClick = { selected = index },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSelected) MareePalette.Subtle else MareePalette.Surface,
                        contentColor = if (isSelected) MareePalette.Teal else MareePalette.Ink
                    ),
                    border = BorderStroke(1.dp,
                        if (isSelected) MareePalette.Teal else MareePalette.Outline)
                ) {
                    Text(situation.situation, modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = 15.sp, lineHeight = 22.sp)
                }
            }
        }
        AppCardSurface {
            SectionLabel("À essayer maintenant")
            Text(advice.title, color = MareePalette.Ink, fontWeight = FontWeight.Bold,
                fontSize = 22.sp, lineHeight = 29.sp)
            advice.steps.forEachIndexed { index, step ->
                if (index > 0) HorizontalDivider(color = MareePalette.Outline)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("ÉTAPE ${index + 1}", color = MareePalette.Teal,
                        fontWeight = FontWeight.SemiBold, fontSize = 12.sp, letterSpacing = .6.sp)
                    Text(step, color = MareePalette.Muted, fontSize = 16.sp, lineHeight = 24.sp)
                }
            }
            Text("Conseil écrit à l’avance · aucun message envoyé",
                color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 19.sp)
        }
        }
        Spacer(Modifier.height(4.dp))
    }
}
