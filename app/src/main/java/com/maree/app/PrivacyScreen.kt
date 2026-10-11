package com.maree.app

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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

/** Résumé accessible immédiatement ; politique complète disponible hors ligne sur demande. */
@Composable
fun PrivacyScreen(onBack: () -> Unit, onDeleteData: () -> Unit) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    val snackbars = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var fullPolicy by rememberSaveable { mutableStateOf(false) }
    Scaffold(containerColor = MareePalette.Background,
        snackbarHost = { SnackbarHost(snackbars) }) { inner ->
        LazyColumn(Modifier.fillMaxSize().padding(inner).statusBarsPadding()
            .padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    TextButton(onClick = onBack) { Text("‹ Retour") }
                    Text("Tes données, tes choix.", fontSize = 28.sp, fontWeight = FontWeight.Bold,
                        color = MareePalette.Ink)
                    Text("Voici l’essentiel sur cette version de Milo.", color = MareePalette.Muted,
                        fontSize = 15.sp, lineHeight = 22.sp)
                }
            }
            item {
                Card(shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MareePalette.Surface),
                    border = BorderStroke(1.dp, MareePalette.Outline)) {
                    Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        PrivacyFact("Sur ton téléphone", "Tes limites, tes points et tes défis sont stockés sur ton appareil. Aucun compte n’est nécessaire.")
                        PrivacyFact("Tu choisis les accès", "Le suivi utilise les durées des applications après ton autorisation. Milo ne lit ni tes messages, ni tes photos, ni le contenu de ton écran.")
                        PrivacyFact("Tu peux arrêter et effacer", "Mets le suivi en pause dans Plus ou supprime les données avec le bouton ci-dessous. Les autorisations Android se retirent dans les réglages du téléphone.")
                        PrivacyFact("Aucun message envoyé à une IA", "Le coach propose actuellement des conseils fixes hors ligne. Les achats et le dialogue avec une IA ne sont pas activés.")
                    }
                }
            }
            item {
                Card(shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MareePalette.Subtle)) {
                    Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Ton contact", fontWeight = FontWeight.SemiBold, color = MareePalette.Ink, fontSize = 17.sp)
                        Text(PrivacyIdentity.controllerName, color = MareePalette.Ink)
                        Text(PrivacyIdentity.contactEmail, color = MareePalette.Muted, fontSize = 14.sp)
                        OutlinedButton(onClick = {
                            try {
                                context.startActivity(Intent(Intent.ACTION_SENDTO,
                                    Uri.parse("mailto:${PrivacyIdentity.contactEmail}"))
                                    .putExtra(Intent.EXTRA_SUBJECT, "Milo · Confidentialité"))
                            } catch (_: ActivityNotFoundException) {
                                scope.launch { snackbars.showSnackbar("Écris à ${PrivacyIdentity.contactEmail}") }
                            }
                        }) { Text("Envoyer un e-mail") }
                    }
                }
            }
            item {
                OutlinedButton(onClick = { fullPolicy = !fullPolicy }, modifier = Modifier.fillMaxWidth()) {
                    Text(if (fullPolicy) "Masquer le texte complet" else "Lire la politique complète")
                }
            }
            if (fullPolicy) {
                item { Text("Politique complète · ${PrivacyContent.updatedOn}",
                    fontWeight = FontWeight.SemiBold, color = MareePalette.Teal) }
                items(PrivacyContent.sections, key = { it.title }) { section ->
                    Card(shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MareePalette.Surface)) {
                        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(section.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            section.paragraphs.forEach { Text(it, fontSize = 14.sp, lineHeight = 22.sp) }
                        }
                    }
                }
            }
            item {
                Column(Modifier.fillMaxWidth().padding(bottom = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(onClick = onDeleteData, modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer)) {
                        Text("Effacer les données de Milo")
                    }
                    Text("Une confirmation sera demandée. Cela n’efface pas les statistiques conservées par Android.",
                        fontSize = 12.sp, lineHeight = 18.sp, color = MareePalette.Muted)
                }
            }
        }
    }
}

@Composable
private fun PrivacyFact(title: String, description: String) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MareePalette.Teal)
        Text(description, fontSize = 14.sp, lineHeight = 21.sp, color = MareePalette.Muted)
    }
}
