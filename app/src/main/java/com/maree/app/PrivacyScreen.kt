package com.maree.app

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

/** Résumé lisible dès le premier lancement ; toutes les sections sont consultables hors ligne. */
@Composable
fun PrivacyScreen(onBack: () -> Unit, onDeleteData: () -> Unit, onExportData: () -> Unit) {
    val context = LocalContext.current
    val snackbars = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var fullPolicy by rememberSaveable { mutableStateOf(false) }
    var legalDocument by rememberSaveable { mutableStateOf<LegalDocument?>(null) }
    val listState = rememberLazyListState()
    val currentDocument = legalDocument
    if (currentDocument != null) {
        LegalInformationScreen(currentDocument, onBack = { legalDocument = null })
        return
    }
    BackHandler(onBack = onBack)
    Scaffold(containerColor = MareePalette.Background,
        snackbarHost = { SnackbarHost(snackbars) }) { inner ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(inner)
                .statusBarsPadding().navigationBarsPadding(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextButton(onClick = onBack) { Text(tr("‹ Retour", "‹ Back")) }
                    ScreenHeading(
                        eyebrow = tr("CONFIDENTIALITÉ", "PRIVACY"),
                        title = tr("Tes données, tes choix.", "Your data, your choices."),
                        description = tr("Version 0.12.0 · 13 octobre 2026. Le texte complet est disponible ci-dessous.", "Version 0.12.0 · 13 October 2026. The full policy is available below.")
                    )
                }
            }
            item {
                AppCardSurface {
                    StatusPill(tr("Suivi local · coach facultatif", "Local tracking · optional coach"))
                    PrivacyFact(tr("Ce qui est enregistré", "What is saved"),
                        tr("Tes applications, limites, points, défis, plages horaires, favoris et programmes restent dans le stockage privé de ce téléphone. L’historique du calendrier et les bilans gardent au maximum 90 jours locaux d’observations et de validations. Tes tâches personnelles restent jusqu’à leur suppression et sont incluses dans ton export.", "Your apps, limits, points, challenges, time slots, favourites and programmes stay in this phone’s private storage. Calendar history and summaries keep at most 90 local days of observations and check-ins. Your personal tasks remain until deleted and are included in your export."))
                    PrivacyFact(tr("Ce qui reste privé", "What stays private"),
                        tr("Selkie ne lit ni les messages de tes autres applications, ni tes photos, ni le contenu de ton écran. Les durées sont consultées uniquement après ton autorisation.", "Selkie does not read messages from your other apps, photos or screen content. Usage times are accessed only after you give permission."))
                    PrivacyFact(tr("Tu gardes le contrôle", "You stay in control"),
                        tr("Arrête le suivi dans le menu ☰ → Accès Android ou efface tes données ci-dessous. Pour retirer un accès, utilise les réglages Android.", "Stop tracking in the ☰ menu → Android access or erase your data below. Use Android settings to revoke access."))
                    PrivacyFact(tr("Le Coach, aujourd’hui", "The coach today"),
                        tr("Le coach à conseils préparés fonctionne sans IA, compte ni envoi. Ses 50 favoris maximum gardent des références de conseils préparés ou des réponses rédigées sélectionnées, avec leur langue et leur offre minimale. Les réponses payantes préparées ne sont pas recopiées. La conservation facultative des questions non comprises est distincte, désactivée par défaut. Les paiements restent inactifs.", "The coach uses prepared advice, without AI, an account or sending messages anywhere. Its maximum of 50 favourites keeps prepared tip references or selected authored replies, with their language and minimum plan. Paid prepared answers are not copied. Optional saving of unrecognised questions is separate, off by default. Payments remain inactive."))
                    PrivacyFact(tr("Historique observé et widget", "Observed history and widget"),
                        tr("L’historique exclut les pauses et les intervalles non observés ; il n’invente pas les journées passées. Le calendrier de Selkie n’accède pas à celui du téléphone. Si tu ajoutes le widget Premium ou Ultra, tes points et un défi proposé sont visibles sur l’écran d’accueil Android.", "History excludes pauses and unobserved gaps; it never invents past days. Selkie’s calendar does not access your phone’s calendar. If you add the Premium or Ultra widget, your points and a suggested challenge are visible on the Android home screen."))
                }
            }
            item {
                AppCardSurface {
                    SectionLabel(tr("Conditions et informations légales", "Terms and legal information"))
                    Text(tr("Cette version est un test gratuit. Consulte ses conditions, les informations sur les offres et le contact actuel.",
                        "This is a free test version. Read its terms, plan information and current contact details."),
                        color = MareePalette.Muted, style = MaterialTheme.typography.bodyMedium)
                    LegalDocument.entries.forEach { document ->
                        OutlinedButton(onClick = { legalDocument = document },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                            shape = RoundedCornerShape(14.dp)) { Text(document.title) }
                    }
                }
            }
            item {
                AppCardSurface {
                    SectionLabel(tr("Une copie de tes données", "A copy of your data"))
                    Text(tr("Enregistre un fichier JSON de tes réglages, de ta progression et des données structurées de l’historique, des plages horaires, des favoris préparés et des programmes. Il peut contenir des informations privées. Les saisies brutes du Coach, jetons et adresses e-mail sont exclus. Une copie exportée doit être supprimée séparément.",
                        "Save a JSON file of your settings, progress and structured history, time-slot, prepared-favourite and programme data. It may contain private information. Raw Coach input, tokens and email addresses are excluded. Delete any exported copy separately."),
                        color = MareePalette.Muted, style = MaterialTheme.typography.bodyMedium)
                    OutlinedButton(onClick = onExportData,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                        shape = RoundedCornerShape(14.dp)) {
                        Text(tr("Exporter mes données", "Export my data"))
                    }
                }
            }
            item {
                SectionLabel(tr("Une question sur tes données ?", "A question about your data?"))
            }
            item {
                AppCardSurface {
                    Text(tr("Responsable de l’application", "Data controller"), fontSize = 12.sp, color = MareePalette.Muted)
                    Text(PrivacyIdentity.controllerName, color = MareePalette.Ink,
                        fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    Text(PrivacyIdentity.contactEmail, color = MareePalette.Muted, fontSize = 14.sp)
                    OutlinedButton(onClick = {
                        try {
                            context.startActivity(Intent(Intent.ACTION_SENDTO,
                                Uri.parse("mailto:${PrivacyIdentity.contactEmail}"))
                                .putExtra(Intent.EXTRA_SUBJECT, tr("Selkie · Confidentialité", "Selkie · Privacy")))
                        } catch (_: ActivityNotFoundException) {
                            scope.launch { snackbars.showSnackbar(tr("Écris à ${PrivacyIdentity.contactEmail}", "Email ${PrivacyIdentity.contactEmail}")) }
                        }
                    }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                        Text(tr("Contacter par e-mail", "Contact by email"))
                    }
                }
            }
            item {
                OutlinedButton(onClick = { fullPolicy = !fullPolicy },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                    Text(if (fullPolicy) tr("Masquer la politique complète", "Hide full policy") else tr("Lire la politique complète", "Read full policy"))
                }
            }
            if (fullPolicy) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        SectionLabel(tr("Politique complète", "Full policy"))
                        Text(tr("Mise à jour : ${PrivacyContent.updatedOn}", "Updated: ${PrivacyContent.updatedOn}"),
                            color = MareePalette.Muted, fontSize = 12.sp)
                    }
                }
                items(PrivacyContent.sections, key = { it.title }) { section ->
                    AppCardSurface {
                        Text(section.title, fontSize = 17.sp, lineHeight = 23.sp,
                            fontWeight = FontWeight.SemiBold, color = MareePalette.Ink)
                        section.paragraphs.forEach {
                            Text(it, fontSize = 14.sp, lineHeight = 22.sp, color = MareePalette.Muted)
                        }
                    }
                }
            }
            item {
                AppCardSurface {
                    Text(tr("Repartir de zéro", "Start fresh"), fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold, color = MareePalette.Ink)
                    Text(tr("Cela supprime toutes les données locales de Selkie : réglages, points, défis, historique, tâches du calendrier, plages horaires, favoris, programmes et questions du Coach enregistrées.", "This erases all Selkie’s local data: settings, points, challenges, history, calendar tasks, time slots, favourites, programmes and saved Coach questions."),
                        fontSize = 14.sp, lineHeight = 21.sp, color = MareePalette.Muted)
                    Button(onClick = onDeleteData, modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer)) {
                        Text(tr("Effacer mes données", "Erase my data"))
                    }
                    Text(tr("Une confirmation sera demandée. Les statistiques d’Android et les autorisations du téléphone restent inchangées.", "You will be asked to confirm. Android usage statistics and your phone’s permissions stay unchanged."),
                        fontSize = 12.sp, lineHeight = 18.sp, color = MareePalette.Muted)
                }
            }
        }
    }
}

@Composable
private fun PrivacyFact(title: String, description: String) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MareePalette.Ink)
        Text(description, fontSize = 14.sp, lineHeight = 21.sp, color = MareePalette.Muted)
    }
}
