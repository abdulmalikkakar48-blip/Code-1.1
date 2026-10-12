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

private val coachSituations: List<LocalCoachTip> get() = listOf(
    LocalCoachTip(tr("Je scrolle sans réfléchir", "I scroll without thinking"), tr("Reprends la main en trois gestes", "Take back control in three steps"), listOf(
        tr("Pose ton téléphone dix minutes hors de portée.", "Put your phone out of reach for ten minutes."),
        tr("Avant de le reprendre, choisis une seule chose à y faire.", "Before picking it up again, choose just one thing to do on it."),
        tr("Une fois cette tâche terminée, referme l’application.", "When that task is done, close the app.")
    )),
    LocalCoachTip(tr("Je dépasse mes limites", "I exceed my limits"), tr("Commence par une seule habitude", "Start with one habit"), listOf(
        tr("Choisis une application et une limite réaliste pour commencer.", "Start by choosing one app and a realistic limit."),
        tr("Prépare une alternative courte : boire de l’eau, marcher ou lire une page.", "Plan a short alternative: drink some water, walk or read a page."),
        tr("Quand l’écran de pause apparaît, essaie cette alternative.", "When the pause screen appears, try that alternative.")
    )),
    LocalCoachTip(tr("J’ai du mal à tenir un défi", "I struggle to stick with a challenge"), tr("Fais de la place pour un petit pas", "Make room for a small step"), listOf(
        tr("Choisis un défi adapté à ce que tu peux faire aujourd’hui.", "Choose a challenge that fits what you can do today."),
        tr("Prévois un moment précis pour le pratiquer.", "Set a specific time for it."),
        tr("Les jours peuvent être espacés : une journée manquée ne supprime pas ta progression.", "The days can be spaced out: missing a day does not erase your progress.")
    ))
)

/** Le dialogue appelle le serveur ; les conseils fixes restent explicitement hors ligne. */
@Composable
internal fun OnlineCoachTab() {
    val context = LocalContext.current
    val language = AppLanguage.current
    val situations = remember(language) { coachSituations }
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
    val advice = situations[selected.coerceIn(situations.indices)]
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
            catch (_: Exception) { error = tr("Connexion impossible. Vérifie Internet puis réessaie.", "Cannot connect. Check your Internet connection and try again.") }
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
            title = tr("Parlons de tes habitudes.", "Let’s talk about your habits."),
            description = tr("Un échange pour trouver ton prochain petit pas.", "A conversation to find your next small step.")
        )
        if (!client.configured) {
            AppCardSurface {
                SectionLabel(tr("Activer le dialogue IA", "Activate AI chat"))
                Text(tr("Le serveur du coach doit être configuré par l’éditeur avant le premier échange.", "The publisher needs to configure the coach server before the first conversation."),
                    color = MareePalette.Muted, fontSize = 15.sp, lineHeight = 22.sp)
                Text(tr("Aucune réponse IA n’est simulée. Les conseils hors ligne restent disponibles ci-dessous.", "No AI answers are simulated. Offline advice is still available below."),
                    color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 19.sp)
            }
        } else {
            AppCardSurface {
                SectionLabel(tr("Coach IA", "AI coach"), tr("100 questions gratuites", "100 free questions"))
                Text(tr("Tes messages et les derniers échanges sont transmis au serveur Selkie (Supabase), puis à OpenAI. ", "Your messages and recent conversation are sent to the Selkie server (Supabase), then to OpenAI. ") +
                    tr("Tes applications, points et défis ne sont pas envoyés. Évite les informations sensibles. ", "Your apps, points and challenges are not sent. Avoid sensitive information. ") +
                    tr("L’IA peut se tromper et ne remplace pas un professionnel de santé.", "AI can make mistakes and does not replace a health professional."),
                    color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 19.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = consent, onCheckedChange = { if (!busy) consent = it }, enabled = !busy)
                    Text(tr("J’accepte l’envoi nécessaire à mes échanges avec le coach.", "I agree to the transmission needed for my conversations with the coach."),
                        color = MareePalette.Ink, fontSize = 14.sp, lineHeight = 20.sp)
                }
                if (session == null) {
                    Text(tr("Connecte-toi par e-mail pour conserver ton accès aux 100 questions. Le suivi du téléphone reste sans compte.", "Sign in by email to keep your access to the 100 questions. Phone tracking still works without an account."),
                        color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp)
                    OutlinedTextField(value = email, onValueChange = { email = it.take(254); codeSent = false; code = "" },
                        label = { Text(tr("Ton adresse e-mail", "Your email address")) }, singleLine = true, enabled = !busy,
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp))
                    PrimaryAction(if (codeSent) tr("Renvoyer un code", "Resend code") else tr("Recevoir un code", "Send me a code"), onClick = {
                        run {
                            withContext(Dispatchers.IO) { client.requestCode(email) }
                            codeSent = true
                        }
                    }, modifier = Modifier.fillMaxWidth(), enabled = !busy && consent && email.isNotBlank())
                    if (codeSent) {
                        OutlinedTextField(value = code, onValueChange = { code = it.filter(Char::isDigit).take(10) },
                            label = { Text(tr("Code reçu par e-mail", "Code received by email")) }, singleLine = true, enabled = !busy,
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp))
                        PrimaryAction(tr("Me connecter", "Sign in"), onClick = {
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
                    Text(tr("Connecté : ${session!!.email}", "Signed in: ${session!!.email}"), color = MareePalette.Muted, fontSize = 13.sp)
                    remaining?.let {
                        Text(if (it < 0) tr("Accès confirmé par le serveur", "Access confirmed by the server") else tr("$it questions restantes", "$it questions remaining"),
                            color = MareePalette.Teal, fontSize = 13.sp)
                    }
                    TextButton(enabled = !busy, onClick = {
                        run {
                            withContext(Dispatchers.IO) { sessions.clear() }
                            session = null; messages = emptyList(); remaining = null; consent = false
                        }
                    }) { Text(tr("Me déconnecter sur ce téléphone", "Sign out on this phone")) }
                }
            }
            messages.forEach { message ->
                AppCardSurface(containerColor = if (message.role == "user") MareePalette.Subtle else MareePalette.Surface) {
                    SectionLabel(if (message.role == "user") tr("Toi", "You") else tr("Coach IA", "AI coach"))
                    Text(message.text, color = MareePalette.Ink, fontSize = 16.sp, lineHeight = 24.sp)
                }
            }
            if (session != null) {
                AppCardSurface {
                    OutlinedTextField(value = question, onValueChange = { question = it.take(800) },
                        label = { Text(tr("Ta question", "Your question")) }, placeholder = { Text(tr("Comment arrêter de scroller le soir ?", "How can I stop scrolling in the evening?")) },
                        minLines = 2, maxLines = 5, enabled = !busy,
                        supportingText = { Text("${question.length}/800") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp))
                    PrimaryAction(if (busy) tr("Le coach répond…", "The coach is replying…") else tr("Envoyer", "Send"), onClick = {
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
                    Text(tr("La conversation reste en mémoire pendant l’ouverture de cet onglet. Elle n’est pas enregistrée sur le téléphone.", "The conversation stays in memory while this tab is open. It is not saved on the phone."),
                        color = MareePalette.Muted, fontSize = 12.sp, lineHeight = 18.sp)
                    TextButton(enabled = !busy, onClick = { messages = emptyList() }) { Text(tr("Nouvelle conversation", "New conversation")) }
                }
            }
            if (busy) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MareePalette.Teal)
            error?.let { Text(it, color = MareePalette.Danger, fontSize = 14.sp, lineHeight = 21.sp) }
        }
        TextButton(onClick = { localTips = !localTips }) {
            Text(if (localTips) tr("Masquer les conseils hors ligne", "Hide offline advice") else tr("Voir les conseils hors ligne", "Show offline advice"))
        }
        if (localTips) {
        AppCardSurface {
            StatusPill(tr("Conseils hors ligne", "Offline advice"))
            Text(tr("Comment te sens-tu aujourd’hui ?", "How do you feel today?"), color = MareePalette.Ink,
                fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 27.sp)
            situations.forEachIndexed { index, situation ->
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
            SectionLabel(tr("À essayer maintenant", "Try this now"))
            Text(advice.title, color = MareePalette.Ink, fontWeight = FontWeight.Bold,
                fontSize = 22.sp, lineHeight = 29.sp)
            advice.steps.forEachIndexed { index, step ->
                if (index > 0) HorizontalDivider(color = MareePalette.Outline)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(tr("ÉTAPE ${index + 1}", "STEP ${index + 1}"), color = MareePalette.Teal,
                        fontWeight = FontWeight.SemiBold, fontSize = 12.sp, letterSpacing = .6.sp)
                    Text(step, color = MareePalette.Muted, fontSize = 16.sp, lineHeight = 24.sp)
                }
            }
            Text(tr("Conseil écrit à l’avance · aucun message envoyé", "Prepared advice · no messages sent"),
                color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 19.sp)
        }
        }
        Spacer(Modifier.height(4.dp))
    }
}
