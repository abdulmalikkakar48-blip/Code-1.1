package com.maree.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

internal object MareePalette {
    val Background = Color(0xFFF7F5EF)
    val Surface = Color.White
    val Subtle = Color(0xFFEDF4F0)
    val Ink = Color(0xFF172D2B)
    val Muted = Color(0xFF536966)
    val Teal = Color(0xFF176C61)
    val DeepTeal = Color(0xFF124E47)
    val Mint = Color(0xFF83E5C6)
    val Coral = Color(0xFFAD5037)
    val Peach = Color(0xFFFFEFE6)
    val Lavender = Color(0xFF695593)
    val Lilac = Color(0xFFF1EDF8)
    val Amber = Color(0xFF866014)
    val Sand = Color(0xFFFBF1DA)
    val Outline = Color(0xFFD9E2DA)
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
