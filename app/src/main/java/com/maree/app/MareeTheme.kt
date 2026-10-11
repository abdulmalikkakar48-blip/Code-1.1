package com.maree.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

internal object MareePalette {
    val Background = Color(0xFFF5F8F6)
    val Surface = Color.White
    val Subtle = Color(0xFFEAF3EF)
    val Ink = Color(0xFF193B32)
    val Muted = Color(0xFF5B6F65)
    val Teal = Color(0xFF216852)
    val DeepTeal = Color(0xFF153F34)
    val Mint = Color(0xFFC4E8D9)
    // Les anciens rôles partagent désormais une seule palette pour garder les écrans cohérents.
    val Coral = Teal
    val Peach = Subtle
    val Lavender = Teal
    val Lilac = Subtle
    val Amber = Teal
    val Sand = Subtle
    val Outline = Color(0xFFDCE5DF)
    val Danger = Color(0xFFA33939)
}

private val MareeScheme = lightColorScheme(
    primary = MareePalette.Teal,
    onPrimary = Color.White,
    primaryContainer = MareePalette.Subtle,
    onPrimaryContainer = MareePalette.DeepTeal,
    secondary = MareePalette.Lavender,
    onSecondary = Color.White,
    secondaryContainer = MareePalette.Lilac,
    onSecondaryContainer = MareePalette.Lavender,
    tertiary = MareePalette.Coral,
    onTertiary = Color.White,
    tertiaryContainer = MareePalette.Peach,
    onTertiaryContainer = MareePalette.Coral,
    background = MareePalette.Background,
    onBackground = MareePalette.Ink,
    surface = MareePalette.Surface,
    onSurface = MareePalette.Ink,
    surfaceVariant = MareePalette.Subtle,
    onSurfaceVariant = MareePalette.Muted,
    outline = MareePalette.Outline,
    error = MareePalette.Danger,
    onError = Color.White
)

@Composable
internal fun MareeTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = MareeScheme, content = content)
}
