package com.maree.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp

@Composable
internal fun BrandIcon(size: Dp = 36.dp) {
    Surface(modifier = Modifier.size(size), color = MareePalette.BrandBackground,
        shape = RoundedCornerShape((size.value * 0.32f).dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.72f)),
        shadowElevation = 2.dp) {
        Image(painterResource(R.drawable.brand_mark), contentDescription = null,
            modifier = Modifier.padding((size.value * 0.15f).dp))
    }
}

@Composable
internal fun ScreenHeading(eyebrow: String, title: String, description: String? = null) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(color = MareePalette.Lilac, shape = RoundedCornerShape(50)) {
            Text(eyebrow, color = MareePalette.Lavender, style = MaterialTheme.typography.labelMedium,
                letterSpacing = 1.2.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
        }
        Text(title, color = MareePalette.Ink, style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.semantics { heading() })
        if (description != null) Text(description, color = MareePalette.Muted,
            style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
internal fun AppCardSurface(modifier: Modifier = Modifier, containerColor: Color = MareePalette.Surface, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (AppAppearance.isNight) 1.dp else 2.dp),
        border = BorderStroke(1.dp, MareePalette.Outline)) {
        Column(Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
    }
}

@Composable
internal fun SectionLabel(title: String, trailing: String? = null) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = MareePalette.Ink, style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f).semantics { heading() })
        if (trailing != null) Text(trailing, color = MareePalette.Muted,
            style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
internal fun StatusPill(label: String, color: Color = MareePalette.Teal,
                        background: Color = MareePalette.Subtle) {
    Surface(color = background, shape = RoundedCornerShape(50)) {
        Text(label, color = color, fontSize = 12.sp, lineHeight = 17.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
    }
}

@Composable
internal fun PrimaryAction(label: String, onClick: () -> Unit,
                           modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(onClick = onClick, enabled = enabled, shape = RoundedCornerShape(50),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 0.dp,
            focusedElevation = 3.dp, hoveredElevation = 3.dp),
        modifier = modifier.heightIn(min = 52.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center)
    }
}

@Composable
internal fun LanguageSelector() {
    val context = LocalContext.current
    var saveFailed by remember { mutableStateOf(false) }
    AppCardSurface {
        Text("Langue / Language", style = MaterialTheme.typography.titleMedium,
            color = MareePalette.Ink)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf(AppLocale.FRENCH to "Français", AppLocale.ENGLISH to "English").forEach { (locale, label) ->
                FilterChip(
                    selected = AppLanguage.current == locale,
                    onClick = { saveFailed = !AppLanguage.set(context, locale) },
                    modifier = Modifier.weight(1f),
                    label = { Text(label) }
                )
            }
        }
        if (saveFailed) Text(
            tr("La langue n’a pas pu être enregistrée. Réessaie.", "The language could not be saved. Try again."),
            color = MareePalette.Danger, style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
internal fun AppearanceSelector() {
    val context = LocalContext.current
    var saveFailed by remember { mutableStateOf(false) }
    AppCardSurface {
        Text(tr("Apparence", "Appearance"), style = MaterialTheme.typography.titleMedium,
            color = MareePalette.Ink)
        Text(tr("Choisis le style de Selkie. Ton choix reste sur ce téléphone.",
            "Choose Selkie’s look. Your choice stays on this phone."),
            color = MareePalette.Muted, style = MaterialTheme.typography.bodyMedium)
        listOf(
            Triple(ThemeMode.LIGHT, tr("Clair", "Light"),
                tr("Blanc doux, lavande et pêche", "Soft white, lavender and peach")),
            Triple(ThemeMode.DARK, tr("Nuit", "Night"),
                tr("Bleu nuit, prune et reflets lilas", "Midnight blue, plum and lilac highlights")),
            Triple(ThemeMode.SYSTEM, tr("Comme le téléphone", "Follow phone settings"),
                tr("Suit le mode clair ou sombre d’Android", "Follows Android’s light or dark mode"))
        ).forEach { (mode, label, description) ->
            FilterChip(
                selected = AppAppearance.current == mode,
                onClick = { saveFailed = !AppAppearance.set(context, mode) },
                modifier = Modifier.fillMaxWidth().heightIn(min = 58.dp),
                label = {
                    Column(Modifier.padding(vertical = 6.dp)) {
                        Text(label, style = MaterialTheme.typography.labelLarge)
                        Text(description, style = MaterialTheme.typography.bodySmall)
                    }
                }
            )
        }
        if (saveFailed) Text(
            tr("L’apparence n’a pas pu être enregistrée. Réessaie.",
                "The appearance could not be saved. Try again."),
            color = MareePalette.Danger, style = MaterialTheme.typography.bodySmall
        )
    }
}
