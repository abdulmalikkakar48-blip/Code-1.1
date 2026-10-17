package com.maree.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Original numbered colors are shared by the editor and gallery; 0 is unpainted paper. */
internal val illustrationPalette = listOf(
    Color.White, Color(0xFFF9CEDC), Color(0xFFE979AA), Color(0xFFED6B66),
    Color(0xFFA82E54), Color(0xFFF4AF8C), Color(0xFFE8BC50), Color(0xFFD9C3EC),
    Color(0xFF9468BD), Color(0xFFB3D9ED), Color(0xFF538AB5), Color(0xFFB8D993),
    Color(0xFF6CAE79), Color(0xFF367660), Color(0xFF74C5B2), Color(0xFFAA805D),
    Color(0xFFF9E9BC)
)

private fun illustrationColorName(index: Int): String = listOf(
    tr("Gomme", "Eraser"), tr("Rose pâle", "Pale pink"), tr("Rose", "Pink"),
    tr("Corail", "Coral"), tr("Framboise", "Raspberry"), tr("Pêche", "Peach"),
    tr("Or", "Gold"), tr("Lilas", "Lilac"), tr("Violet", "Purple"),
    tr("Bleu ciel", "Sky blue"), tr("Bleu", "Blue"), tr("Vert tendre", "Light green"),
    tr("Vert feuille", "Leaf green"), tr("Vert forêt", "Forest green"),
    tr("Turquoise", "Turquoise"), tr("Terre", "Earth"), tr("Crème", "Cream")
)[index.coerceIn(0, 16)]

@Composable
internal fun ColoringSection() {
    var illustrations by rememberSaveable { mutableStateOf(true) }
    var busy by remember { mutableStateOf(false) }
    AppCardSurface {
        SectionLabel(tr("Atelier coloriage", "Coloring studio"), tr("Gratuit", "Free"))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = illustrations, enabled = !busy,
                onClick = { illustrations = true }, modifier = Modifier.heightIn(min = 48.dp),
                label = { Text(tr("Illustrations", "Illustrations")) })
            FilterChip(selected = !illustrations, enabled = !busy,
                onClick = { illustrations = false }, modifier = Modifier.heightIn(min = 48.dp),
                label = { Text(tr("À carreaux", "Grid pages")) })
        }
        Text(if (illustrations) tr("Fleurs, paysages et motifs à remplir, une zone à la fois.",
            "Flowers, landscapes and patterns to fill, one area at a time.") else tr(
            "Tes 12 pages à carreaux et leurs couleurs sont conservées ici.",
            "Your 12 grid pages and their saved colors are kept here."), color = MareePalette.Muted)
    }
    if (illustrations) IllustrationSection { busy = it }
    else GridColoringSection { busy = it }
}

@Composable
private fun IllustrationSection(onBusyChange: (Boolean) -> Unit) {
    val context = LocalContext.current
    val store = remember(context) { IllustrationStore(context) }
    val scope = rememberCoroutineScope()
    var pageId by rememberSaveable { mutableStateOf(illustrationPages.first().id) }
    var category by rememberSaveable { mutableStateOf<IllustrationCategory?>(null) }
    val visiblePages = illustrationPagesForCategory(category)
    val page = selectedIllustrationPage(visiblePages, pageId)
    var color by rememberSaveable { mutableIntStateOf(2) }
    var numbers by rememberSaveable { mutableStateOf(false) }
    var zoom by rememberSaveable(page.id) { mutableFloatStateOf(1f) }
    var regionIndex by rememberSaveable(page.id) { mutableIntStateOf(0) }
    var history by remember(page.id) { mutableStateOf(IllustrationHistory(List(page.regions.size) { 0 })) }
    var loading by remember(page.id) { mutableStateOf(true) }
    var saving by remember(page.id) { mutableStateOf(false) }
    var failed by remember(page.id) { mutableStateOf(false) }
    var pendingReset by remember(page.id) { mutableStateOf(false) }
    var hasSaved by remember(page.id) { mutableStateOf(false) }
    var showReset by remember { mutableStateOf(false) }
    val editable = !loading && !saving && !failed && !pendingReset

    LaunchedEffect(loading, saving, failed, pendingReset) {
        onBusyChange(loading || saving || failed || pendingReset)
    }
    LaunchedEffect(page.id, store) {
        history = IllustrationHistory(withContext(Dispatchers.IO) { store.page(page.id) })
        loading = false
    }
    fun save(next: IllustrationHistory) {
        if (saving || loading || pendingReset) return
        history = next
        saving = true
        failed = false
        scope.launch(start = CoroutineStart.UNDISPATCHED) {
            try {
                val confirmed = withContext(NonCancellable + Dispatchers.IO) { store.savePage(page.id, next.cells) }
                failed = !confirmed
                hasSaved = confirmed
            } catch (cancelled: CancellationException) { throw cancelled
            } catch (_: Exception) { failed = true
            } finally { saving = false }
        }
    }
    fun reset() {
        if (loading || saving) return
        pendingReset = true
        saving = true
        failed = false
        scope.launch(start = CoroutineStart.UNDISPATCHED) {
            try {
                val confirmed = withContext(NonCancellable + Dispatchers.IO) { store.resetPage(page.id) }
                if (confirmed) {
                    history = IllustrationHistory(List(page.regions.size) { 0 })
                    pendingReset = false
                }
                failed = !confirmed
                hasSaved = confirmed
            } catch (cancelled: CancellationException) { throw cancelled
            } catch (_: Exception) { failed = true
            } finally { saving = false }
        }
    }
    fun paint(index: Int) {
        // Read the state delegates here: a remembered gesture callback must not capture
        // the initial derived editable=false value from the loading composition.
        if (loading || saving || failed || pendingReset || index !in page.regions.indices) return
        regionIndex = index
        val next = history.paint(history.cells.toMutableList().also { it[index] = color })
        if (next != history) save(next)
    }
    fun chooseCategory(next: IllustrationCategory?) {
        if (loading || saving || failed || pendingReset || showReset) return
        val nextPage = selectedIllustrationPage(illustrationPagesForCategory(next), page.id)
        pageId = nextPage.id
        category = next
    }

    AppCardSurface {
        Text(tr("1 · Choisis ton illustration", "1 · Choose your illustration"),
            color = MareePalette.Ink, fontWeight = FontWeight.SemiBold)
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = category == null, enabled = editable,
                onClick = { chooseCategory(null) }, modifier = Modifier.heightIn(min = 48.dp),
                label = { Text(tr("Toutes", "All")) })
            IllustrationCategory.entries.forEach { item ->
                FilterChip(selected = category == item, enabled = editable,
                    onClick = { chooseCategory(item) }, modifier = Modifier.heightIn(min = 48.dp),
                    label = { Text(item.label()) })
            }
        }
        val galleryScrollState = key(category) { rememberLazyListState() }
        LazyRow(Modifier.fillMaxWidth(), state = galleryScrollState,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(visiblePages, key = { it.id }) { item ->
                val selectedPage = page.id == item.id
                Column(Modifier.width(136.dp).clip(RoundedCornerShape(16.dp))
                    .border(if (selectedPage) 2.dp else 1.dp,
                        if (selectedPage) MareePalette.Lavender else MareePalette.Outline,
                        RoundedCornerShape(16.dp))
                    .clickable(enabled = editable, role = Role.RadioButton) { pageId = item.id }
                    .semantics { selected = selectedPage; contentDescription = item.name() }
                    .padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    IllustrationCanvas(item, emptyList(), enabled = false, showNumbers = false,
                        zoom = 1f, onRegionTapped = {}, preview = true)
                    Text(item.name(), color = MareePalette.Ink, minLines = 2,
                        style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center,
                        fontWeight = if (selectedPage) FontWeight.Bold else FontWeight.Medium)
                }
            }
        }
        val drawingCount = if (visiblePages.size == 1) tr("1 dessin", "1 drawing")
            else tr("${visiblePages.size} dessins", "${visiblePages.size} drawings")
        Text(tr("$drawingCount · glisse les miniatures pour choisir. Les aperçus colorés sont des exemples : ta feuille commence blanche.",
            "$drawingCount · swipe the thumbnails to choose. Colored previews are examples: your own page starts blank."),
            color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall)
    }
    AppCardSurface {
        SectionLabel(page.name(), "${history.cells.count { it != 0 }} / ${page.regions.size}")
        Text(tr("2 · Choisis une couleur", "2 · Choose a color"),
            color = MareePalette.Ink, fontWeight = FontWeight.SemiBold)
        Column(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)) {
            illustrationPalette.indices.toList().chunked(9).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    row.forEach { index -> IllustrationSwatch(index, color == index, editable) { color = index } }
                }
            }
        }
        Text(tr("${if (color == 0) "" else "$color · "}${illustrationColorName(color)} · glisse la palette pour les autres couleurs.",
            "${if (color == 0) "" else "$color · "}${illustrationColorName(color)} · swipe the palette for more colors."),
            color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text(tr("Repères numérotés", "Number guides"), color = MareePalette.Ink,
                modifier = Modifier.weight(1f))
            Switch(checked = numbers, onCheckedChange = { numbers = it },
                modifier = Modifier.semantics { contentDescription = tr("Afficher les numéros de couleur suggérés", "Show suggested color numbers") })
        }
        if (numbers) Text(tr("Les numéros suggèrent les couleurs de la palette. Tu peux choisir librement une autre couleur.",
            "Numbers suggest colors from the palette. You are free to choose another color."),
            color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall)
        Text(tr("3 · Touche une zone pour la remplir", "3 · Tap an area to fill it"),
            color = MareePalette.Ink, fontWeight = FontWeight.SemiBold)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = { zoom = (zoom - 0.5f).coerceAtLeast(1f) }, enabled = zoom > 1f,
                modifier = Modifier.heightIn(min = 48.dp).semantics { contentDescription = tr("Réduire le zoom", "Zoom out") }) { Text("−") }
            Text("${(zoom * 100).toInt()} %", color = MareePalette.Ink)
            TextButton(onClick = { zoom = (zoom + 0.5f).coerceAtMost(3f) }, enabled = zoom < 3f,
                modifier = Modifier.heightIn(min = 48.dp).semantics { contentDescription = tr("Agrandir le dessin", "Zoom in") }) { Text("+") }
            TextButton(onClick = { zoom = 1f }, modifier = Modifier.heightIn(min = 48.dp)) { Text(tr("Centrer", "Reset")) }
        }
        if (loading) CircularProgressIndicator(Modifier.size(28.dp))
        IllustrationCanvas(page, history.cells, editable, numbers, zoom, { index -> paint(index) },
            Modifier.clip(RoundedCornerShape(12.dp)).border(1.dp, MareePalette.Outline, RoundedCornerShape(12.dp)))
        Text(tr("Au zoom normal, glisse sur la feuille pour faire défiler l’écran. Après agrandissement, glisse pour déplacer le dessin.",
            "At normal zoom, swipe the page to scroll the screen. When zoomed in, drag to move around the drawing."),
            color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = { save(history.undo()) }, enabled = editable && history.previous.isNotEmpty(),
                modifier = Modifier.heightIn(min = 48.dp)) { Text(tr("Annuler", "Undo")) }
            TextButton(onClick = { save(history.redo()) }, enabled = editable && history.following.isNotEmpty(),
                modifier = Modifier.heightIn(min = 48.dp)) { Text(tr("Rétablir", "Redo")) }
        }
        Text(when {
            saving && pendingReset -> tr("Effacement…", "Clearing…")
            saving -> tr("Enregistrement…", "Saving…")
            failed && pendingReset -> tr("L’effacement n’a pas pu être confirmé. Réessaie pour terminer l’effacement.", "Clearing could not be confirmed. Try again to finish clearing.")
            failed -> tr("La couleur n’a pas pu être enregistrée. Réessaie avant de quitter cette page.", "Your color could not be saved. Try again before leaving this page.")
            hasSaved -> tr("Enregistré sur ce téléphone.", "Saved on this phone.")
            else -> tr("Sauvegarde automatique après chaque zone. Annuler/Rétablir garde les 50 derniers gestes de cette page ouverte.", "Each area is saved automatically. Undo/Redo keeps the last 50 actions while this page is open.")
        }, color = if (failed) MareePalette.Danger else MareePalette.Muted,
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
        if (failed) PrimaryAction(if (pendingReset) tr("Réessayer l’effacement", "Retry clearing") else tr("Réessayer l’enregistrement", "Retry saving"),
            onClick = { if (pendingReset) reset() else save(history) }, enabled = !saving)
        TextButton(onClick = { showReset = true }, enabled = editable,
            modifier = Modifier.heightIn(min = 48.dp)) { Text(tr("Effacer cette illustration", "Clear this illustration"), color = MareePalette.Danger) }
    }
    AppCardSurface {
        SectionLabel(tr("Colorier avec les commandes", "Color with controls"))
        Text(tr("Tu peux aussi choisir une zone puis appliquer ta couleur, sans viser ses contours.",
            "You can also choose an area and apply your color without targeting its outline."), color = MareePalette.Muted)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { regionIndex-- }, enabled = editable && regionIndex > 0,
                modifier = Modifier.heightIn(min = 48.dp).semantics { contentDescription = tr("Zone précédente", "Previous area") }) { Text("−") }
            Text(tr("Zone ${regionIndex + 1} / ${page.regions.size}", "Area ${regionIndex + 1} / ${page.regions.size}"),
                color = MareePalette.Ink, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            TextButton(onClick = { regionIndex++ }, enabled = editable && regionIndex < page.regions.lastIndex,
                modifier = Modifier.heightIn(min = 48.dp).semantics { contentDescription = tr("Zone suivante", "Next area") }) { Text("+") }
        }
        PrimaryAction(tr("Appliquer : ${illustrationColorName(color)}", "Apply: ${illustrationColorName(color)}"),
            onClick = { paint(regionIndex) }, enabled = editable)
        Text(tr("Une pause créative gratuite, sans points ni téléchargement. Tes illustrations et tes pages à carreaux se gardent séparément sur ce téléphone.",
            "A free creative break with no points or downloads. Your illustrations and grid pages are stored separately on this phone."),
            color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall)
    }
    if (showReset) AlertDialog(onDismissRequest = { showReset = false },
        title = { Text(tr("Effacer ${page.name()} ?", "Clear ${page.name()}?")) },
        text = { Text(tr("Seule cette illustration sera effacée. Les autres dessins et les pages à carreaux sont conservés.",
            "Only this illustration will be cleared. Other drawings and grid pages are kept.")) },
        confirmButton = { TextButton(onClick = { showReset = false; reset() }) { Text(tr("Effacer", "Clear")) } },
        dismissButton = { TextButton(onClick = { showReset = false }) { Text(tr("Annuler", "Cancel")) } })
}

@Composable
private fun IllustrationSwatch(index: Int, isSelected: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Box(Modifier.size(48.dp).clip(CircleShape)
        .clickable(enabled = enabled, role = Role.RadioButton, onClick = onClick)
        .semantics { selected = isSelected; contentDescription = "${if (index == 0) "" else "$index · "}${illustrationColorName(index)}" }
        .border(if (isSelected) 3.dp else 1.dp, if (isSelected) MareePalette.Ink else MareePalette.Outline, CircleShape),
        contentAlignment = Alignment.Center) {
        Box(Modifier.size(37.dp).background(illustrationPalette[index], CircleShape), contentAlignment = Alignment.Center) {
            Text(if (index == 0) "×" else "$index", color = if (illustrationPalette[index].luminance() > 0.179f) Color.Black else Color.White,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium)
        }
    }
}
