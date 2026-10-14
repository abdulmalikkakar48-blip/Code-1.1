package com.maree.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private data class ReadingTextLoad(val resourceId: String, val language: AppLocale, val body: String?, val failed: Boolean)

/** Offline excerpts; opening a source or creating a document always requires a user action. */
@Composable
internal fun ReadingSection() {
    val context = LocalContext.current
    val language = AppLanguage.current
    val scope = rememberCoroutineScope()
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingExportId by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingExportLanguage by rememberSaveable { mutableStateOf<String?>(null) }
    var exporting by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var messageIsError by remember { mutableStateOf(false) }
    var loaded by remember { mutableStateOf<ReadingTextLoad?>(null) }
    var loadRevision by remember { mutableIntStateOf(0) }
    val selected = readingResources.firstOrNull { it.id == selectedId }
    val currentLoad = loaded?.takeIf { it.resourceId == selected?.id && it.language == language }

    val exportDocument = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { destination ->
        // Retain only catalogue identifiers while Android's picker is open, never the excerpt text.
        val resource = readingResources.firstOrNull { it.id == pendingExportId }
        val exportLanguage = AppLocale.entries.firstOrNull { it.tag == pendingExportLanguage }
        pendingExportId = null
        pendingExportLanguage = null
        if (destination == null) {
            exporting = false
            messageIsError = false
            message = tr("Enregistrement annulé.", "Saving cancelled.")
        } else if (resource == null || exportLanguage == null) {
            exporting = false
            messageIsError = true
            message = tr("La sélection a changé. Choisis à nouveau l’extrait à enregistrer.",
                "The selection changed. Choose the excerpt to save again.")
        } else {
            exporting = true
            scope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        val text = readingExportText(context, resource, exportLanguage)
                        val output = context.contentResolver.openOutputStream(destination, "wt")
                            ?: throw java.io.IOException("Document provider returned no output stream")
                        output.use { it.write(text.toByteArray(Charsets.UTF_8)) }
                    }
                    messageIsError = false
                    message = tr("L’extrait a été enregistré dans le fichier choisi.",
                        "The excerpt was saved to the file you chose.")
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    messageIsError = true
                    message = tr("L’extrait n’a pas pu être enregistré. Le fichier choisi peut être incomplet : vérifie-le et réessaie.",
                        "The excerpt could not be saved. The chosen file may be incomplete: check it and try again.")
                } finally {
                    exporting = false
                }
            }
        }
    }

    LaunchedEffect(selected?.id, language, loadRevision) {
        loaded = null
        if (selected != null) {
            try {
                val body = withContext(Dispatchers.IO) { readReadingText(context, selected, language) }
                loaded = ReadingTextLoad(selected.id, language, body, failed = false)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                loaded = ReadingTextLoad(selected.id, language, null, failed = true)
            }
        }
    }

    BackHandler(enabled = selected != null) {
        selectedId = null
        message = null
    }

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (selected == null) {
            ScreenHeading(tr("LECTURES", "READING"), tr("Quelques pages pour réfléchir.", "A few pages to reflect."),
                tr("Une sélection gratuite autour de l’apprentissage, de l’attention et des habitudes.",
                    "A free selection about learning, attention and habits."))
            AppCardSurface {
                StatusPill(tr("Gratuit · Hors ligne", "Free · Offline"))
                Text(tr("Tu lis ici des extraits sélectionnés, pas les livres complets. Chaque fiche précise l’auteur, la source et l’édition utilisée.",
                    "You read selected excerpts here, not complete books. Each card identifies the author, source and edition used."),
                    color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp)
                Text(tr("Les liens ouvrent le navigateur. Les pages externes peuvent contenir d’autres textes qui ne font pas partie de cette sélection.",
                    "Links open your browser. External pages may contain other texts that are not part of this selection."),
                    color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)
            }
            readingResources.forEach { resource ->
                AppCardSurface {
                    SectionLabel(if (language == AppLocale.ENGLISH) resource.titleEn else resource.titleFr)
                    Text(resource.author, color = MareePalette.Teal, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    Text(if (language == AppLocale.ENGLISH) resource.descriptionEn else resource.descriptionFr,
                        color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp)
                    Text(tr("Source : ${resource.sourceLabel}", "Source: ${resource.sourceLabel}"),
                        color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)
                    Text(if (language == AppLocale.ENGLISH) resource.editionNoteEn else resource.editionNoteFr,
                        color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)
                    PrimaryAction(tr("Lire l’extrait", "Read excerpt"), onClick = {
                        selectedId = resource.id
                        message = null
                    }, modifier = Modifier.fillMaxWidth())
                }
            }
        } else {
            TextButton(onClick = { selectedId = null; message = null }, modifier = Modifier.heightIn(min = 48.dp)) {
                Text(tr("Retour aux lectures", "Back to reading list"))
            }
            AppCardSurface {
                SectionLabel(if (language == AppLocale.ENGLISH) selected.titleEn else selected.titleFr)
                Text(selected.author, color = MareePalette.Teal, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                StatusPill(tr("Extrait sélectionné · livre non intégral", "Selected excerpt · not the whole book"))
                Text(tr("Source : ${selected.sourceLabel}", "Source: ${selected.sourceLabel}"),
                    color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)
                Text(if (language == AppLocale.ENGLISH) selected.editionNoteEn else selected.editionNoteFr,
                    color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)
                HorizontalDivider(color = MareePalette.Outline)
                when {
                    currentLoad == null -> Text(tr("Chargement de l’extrait…", "Loading excerpt…"), color = MareePalette.Muted)
                    currentLoad.failed -> {
                        Text(tr("Cet extrait n’a pas pu être ouvert. Réessaie.", "This excerpt could not be opened. Try again."),
                            color = MareePalette.Danger)
                        OutlinedButton(onClick = { loadRevision += 1 }) { Text(tr("Réessayer", "Try again")) }
                    }
                    else -> {
                        // A bounded inner scroll keeps large excerpts readable and the controls reachable.
                        androidx.compose.runtime.key(selected.id, language) {
                            SelectionContainer {
                                Text(currentLoad.body.orEmpty(), color = MareePalette.Ink, fontSize = 17.sp, lineHeight = 27.sp,
                                    modifier = Modifier.fillMaxWidth().heightIn(max = 520.dp)
                                        .verticalScroll(rememberScrollState()))
                            }
                        }
                    }
                }
                HorizontalDivider(color = MareePalette.Outline)
                PrimaryAction(if (exporting || pendingExportId != null) tr("Enregistrement…", "Saving…") else tr("Enregistrer cet extrait (.txt)", "Save this excerpt (.txt)"),
                    enabled = currentLoad?.body != null && !exporting && pendingExportId == null, modifier = Modifier.fillMaxWidth(), onClick = {
                        pendingExportId = selected.id
                        pendingExportLanguage = language.tag
                        exporting = true
                        message = null
                        try {
                            val safeName = selected.assetName.replace(Regex("[^A-Za-z0-9_-]"), "_").take(80)
                            exportDocument.launch("Selkie-$safeName-${language.tag}.txt")
                        } catch (_: Exception) {
                            pendingExportId = null
                            pendingExportLanguage = null
                            exporting = false
                            messageIsError = true
                            message = tr("Le sélecteur de fichiers n’a pas pu être ouvert. Réessaie depuis cette lecture.",
                                "The file picker could not be opened. Try again from this reading.")
                        }
                    })
                OutlinedButton(modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp), onClick = {
                    message = null
                    try {
                        val uri = Uri.parse(selected.sourceUrl)
                        require(uri.scheme == "https" && !uri.host.isNullOrBlank())
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri).addCategory(Intent.CATEGORY_BROWSABLE))
                    } catch (_: Exception) {
                        messageIsError = true
                        message = tr("La source n’a pas pu être ouverte. Vérifie qu’un navigateur est disponible et réessaie.",
                            "The source could not be opened. Check that a browser is available and try again.")
                    }
                }) { Text(tr("Ouvrir la source dans le navigateur", "Open source in browser")) }
                Text(tr("La source externe contient un ouvrage plus large. Seul le passage affiché ici a été sélectionné pour cette rubrique.",
                    "The external source contains a larger work. Only the passage shown here was selected for this section."), color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)
                Text(tr("Le fichier contient seulement cet extrait avec ses références. Aucun compte ni historique de lecture n’est enregistré. Une connexion est nécessaire uniquement pour consulter la source externe.",
                    "The file contains only this excerpt and its references. No account or reading history is saved. An internet connection is needed only to visit the external source."),
                    color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 20.sp)
                TextButton(onClick = { selectedId = null; message = null }, modifier = Modifier.heightIn(min = 48.dp)) {
                    Text(tr("Choisir une autre lecture", "Choose another reading"))
                }
            }
        }
        if (message != null) Text(message!!, color = if (messageIsError) MareePalette.Danger else MareePalette.Teal,
            fontSize = 14.sp, lineHeight = 21.sp)
    }
}

private fun readReadingText(context: Context, resource: ReadingResource, language: AppLocale): String =
    context.assets.open("reading/${resource.assetName}-${language.tag}.txt").bufferedReader(Charsets.UTF_8).use {
        it.readText().takeIf(String::isNotBlank) ?: throw java.io.IOException("Reading asset is empty")
    }

private fun readingExportText(context: Context, resource: ReadingResource, language: AppLocale): String =
    readingExportContent(resource, language, readReadingText(context, resource, language))
