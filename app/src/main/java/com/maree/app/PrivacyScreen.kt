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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
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
fun PrivacyScreen(onBack: () -> Unit, onDeleteData: () -> Unit) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    val snackbars = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var fullPolicy by rememberSaveable { mutableStateOf(false) }
    Scaffold(containerColor = MareePalette.Background,
        snackbarHost = { SnackbarHost(snackbars) }) { inner ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(inner)
                .statusBarsPadding().navigationBarsPadding(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextButton(onClick = onBack) { Text("‹ Retour") }
                    ScreenHeading(
                        eyebrow = "CONFIDENTIALITÉ",
                        title = "Tes données, tes choix.",
                        description = "L’essentiel sur cette version de Selkie. Le texte complet est disponible ci-dessous."
                    )
                }
            }
            item {
                AppCardSurface {
                    StatusPill("Suivi local · coach facultatif")
                    PrivacyFact("Ce qui est enregistré",
                        "Tes applications suivies, tes limites, tes points et tes défis restent sur ton appareil.")
                    PrivacyFact("Ce qui reste privé",
                        "Selkie ne lit ni tes messages, ni tes photos, ni le contenu de ton écran. Les durées sont consultées uniquement après ton autorisation.")
                    PrivacyFact("Tu gardes le contrôle",
                        "Arrête le suivi dans Plus ou efface tes données ci-dessous. Pour retirer un accès, utilise les réglages Android.")
                    PrivacyFact("Le Coach, aujourd’hui",
                        "Le coach à conseils préparés fonctionne sans compte ni envoi. Les questions non comprises restent sur ce téléphone uniquement si tu actives leur enregistrement ; tu peux les consulter et les effacer dans Coach. Un futur dialogue IA en ligne demanderait ton accord pour transmettre les échanges. Les paiements restent inactifs.")
                }
            }
            item {
                SectionLabel("Une question sur tes données ?")
            }
            item {
                AppCardSurface {
                    Text("Responsable de l’application", fontSize = 12.sp, color = MareePalette.Muted)
                    Text(PrivacyIdentity.controllerName, color = MareePalette.Ink,
                        fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    Text(PrivacyIdentity.contactEmail, color = MareePalette.Muted, fontSize = 14.sp)
                    OutlinedButton(onClick = {
                        try {
                            context.startActivity(Intent(Intent.ACTION_SENDTO,
                                Uri.parse("mailto:${PrivacyIdentity.contactEmail}"))
                                .putExtra(Intent.EXTRA_SUBJECT, "Selkie · Confidentialité"))
                        } catch (_: ActivityNotFoundException) {
                            scope.launch { snackbars.showSnackbar("Écris à ${PrivacyIdentity.contactEmail}") }
                        }
                    }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                        Text("Contacter par e-mail")
                    }
                }
            }
            item {
                OutlinedButton(onClick = { fullPolicy = !fullPolicy },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                    Text(if (fullPolicy) "Masquer la politique complète" else "Lire la politique complète")
                }
            }
            if (fullPolicy) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        SectionLabel("Politique complète")
                        Text("Mise à jour : ${PrivacyContent.updatedOn}",
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
                    Text("Repartir de zéro", fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold, color = MareePalette.Ink)
                    Text("Cela supprime tes réglages, tes points et tes défis de Selkie.",
                        fontSize = 14.sp, lineHeight = 21.sp, color = MareePalette.Muted)
                    Button(onClick = onDeleteData, modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer)) {
                        Text("Effacer mes données")
                    }
                    Text("Une confirmation sera demandée. Les statistiques d’Android et les autorisations du téléphone restent inchangées.",
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
