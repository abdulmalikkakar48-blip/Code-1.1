package com.maree.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal object MareePalette {
    val Background = Color(0xFFF6F7F3)
    val Surface = Color.White
    val Subtle = Color(0xFFEEF4EE)
    val Ink = Color(0xFF1D352C)
    val Muted = Color(0xFF5E7065)
    val Teal = Color(0xFF245A43)
    val DeepTeal = Color(0xFF183D2D)
    val Mint = Color(0xFFD6ECD9)
    // Les anciens rôles partagent désormais une seule palette pour garder les écrans cohérents.
    val Coral = Teal
    val Peach = Subtle
    val Lavender = Teal
    val Lilac = Subtle
    val Amber = Teal
    val Sand = Subtle
    val Outline = Color(0xFFE0E7DE)
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

private val MareeTypography = Typography(
    headlineLarge = TextStyle(fontSize = 32.sp, lineHeight = 39.sp, fontWeight = FontWeight.Bold),
    headlineMedium = TextStyle(fontSize = 28.sp, lineHeight = 34.sp, fontWeight = FontWeight.Bold),
    titleLarge = TextStyle(fontSize = 20.sp, lineHeight = 27.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontSize = 16.sp, lineHeight = 23.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 21.sp),
    bodySmall = TextStyle(fontSize = 12.sp, lineHeight = 18.sp),
    labelLarge = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold),
    labelMedium = TextStyle(fontSize = 12.sp, lineHeight = 17.sp, fontWeight = FontWeight.Medium)
)

@Composable
internal fun MareeTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = MareeScheme, typography = MareeTypography,
        shapes = Shapes(
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(16.dp),
            large = RoundedCornerShape(20.dp),
            extraLarge = RoundedCornerShape(24.dp)
        ), content = content)
}
