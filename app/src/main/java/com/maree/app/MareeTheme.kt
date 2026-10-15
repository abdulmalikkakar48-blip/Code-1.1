package com.maree.app

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

/** Explicit roles keep bright text colours separate from dark navigation/card backgrounds. */
internal data class SelkieThemeColors(
    val background: Long,
    val surface: Long,
    val subtle: Long,
    val ink: Long,
    val muted: Long,
    val primary: Long,
    val onPrimary: Long,
    val strongInk: Long,
    val softAccent: Long,
    val outline: Long,
    val danger: Long,
    val onDanger: Long,
    val heroBackground: Long,
    val onHero: Long,
    val onHeroMuted: Long,
    val heroAccent: Long,
    val onHeroAccent: Long,
    val navigationBackground: Long,
    val navigationMuted: Long
)

internal val SelkieLightColors = SelkieThemeColors(
    background = 0xFFF7F6FB, surface = 0xFFFFFFFF, subtle = 0xFFF0EDF8,
    ink = 0xFF242035, muted = 0xFF484255, primary = 0xFF5143A8,
    onPrimary = 0xFFFFFFFF, strongInk = 0xFF342657, softAccent = 0xFFE2F2EB,
    outline = 0xFFE2DDEB, danger = 0xFF902D45, onDanger = 0xFFFFFFFF,
    heroBackground = 0xFF26213B, onHero = 0xFFFFFFFF, onHeroMuted = 0xFFD9D2ED,
    heroAccent = 0xFFD8C8FF, onHeroAccent = 0xFF2D2247,
    navigationBackground = 0xFF252137, navigationMuted = 0xFFC3BCD6
)

internal val SelkieNightColors = SelkieThemeColors(
    background = 0xFF0B0C16, surface = 0xFF17182A, subtle = 0xFF212139,
    ink = 0xFFF5F0FF, muted = 0xFFBDB4D4, primary = 0xFFB4B7FF,
    onPrimary = 0xFF222044, strongInk = 0xFFDDD3FF, softAccent = 0xFF203A39,
    outline = 0xFF38334F, danger = 0xFFFFAFBD, onDanger = 0xFF461328,
    heroBackground = 0xFF1D1B33, onHero = 0xFFF5F0FF, onHeroMuted = 0xFFCEC4E5,
    heroAccent = 0xFFCEC0FF, onHeroAccent = 0xFF2E224A,
    navigationBackground = 0xFF0E0F1C, navigationMuted = 0xFFBFB5D3
)

internal object MareePalette {
    private val colors: SelkieThemeColors get() =
        if (AppAppearance.isNight) SelkieNightColors else SelkieLightColors
    val Background: Color get() = Color(colors.background)
    val Surface: Color get() = Color(colors.surface)
    val Subtle: Color get() = Color(colors.subtle)
    val Ink: Color get() = Color(colors.ink)
    val Muted: Color get() = Color(colors.muted)
    val Teal: Color get() = Color(colors.primary)
    val OnPrimary: Color get() = Color(colors.onPrimary)
    val DeepTeal: Color get() = Color(colors.strongInk)
    val Mint: Color get() = Color(colors.softAccent)
    // Keep a light shell around the original seal in both appearances.
    val BrandBackground = Color(0xFFE8DDF8)
    // Saturated colours are for text/icons; their paired pastels are opaque containers.
    val Coral: Color get() = if (AppAppearance.isNight) Color(0xFFFFB39D) else Color(0xFF9D4137)
    val Peach: Color get() = if (AppAppearance.isNight) Color(0xFF3B272B) else Color(0xFFFFF0E8)
    val Lavender: Color get() = if (AppAppearance.isNight) Color(0xFFC7B0FF) else Color(0xFF6350A7)
    val Lilac: Color get() = if (AppAppearance.isNight) Color(0xFF2B2543) else Color(0xFFF0EAFE)
    val Amber: Color get() = if (AppAppearance.isNight) Color(0xFFF4CE83) else Color(0xFF805300)
    val Sand: Color get() = if (AppAppearance.isNight) Color(0xFF352D20) else Color(0xFFF7EEDC)
    val Outline: Color get() = Color(colors.outline)
    // Control borders need more contrast than decorative card separators.
    val ControlOutline: Color get() = Color(colors.muted)
    val Danger: Color get() = Color(colors.danger)
    val OnDanger: Color get() = Color(colors.onDanger)
    val HeroBackground: Color get() = Color(colors.heroBackground)
    val OnHero: Color get() = Color(colors.onHero)
    val OnHeroMuted: Color get() = Color(colors.onHeroMuted)
    val HeroAccent: Color get() = Color(colors.heroAccent)
    val OnHeroAccent: Color get() = Color(colors.onHeroAccent)
    val NavigationBackground: Color get() = Color(colors.navigationBackground)
    val NavigationMuted: Color get() = Color(colors.navigationMuted)
}

private fun mareeScheme(night: Boolean) =
    if (night) darkColorScheme(
        primary = MareePalette.Teal, onPrimary = MareePalette.OnPrimary,
        primaryContainer = MareePalette.Subtle, onPrimaryContainer = MareePalette.DeepTeal,
        secondary = MareePalette.Lavender, onSecondary = MareePalette.OnPrimary,
        secondaryContainer = MareePalette.Lilac, onSecondaryContainer = MareePalette.Lavender,
        tertiary = MareePalette.Coral, onTertiary = MareePalette.OnPrimary,
        tertiaryContainer = MareePalette.Peach, onTertiaryContainer = MareePalette.Coral,
        background = MareePalette.Background, onBackground = MareePalette.Ink,
        surface = MareePalette.Surface, onSurface = MareePalette.Ink,
        surfaceVariant = MareePalette.Subtle, onSurfaceVariant = MareePalette.Muted,
        outline = MareePalette.ControlOutline, error = MareePalette.Danger, onError = MareePalette.OnDanger,
        inverseSurface = Color(0xFFEDE7F8), inverseOnSurface = Color(0xFF242035),
        inversePrimary = Color(0xFF5143A8), surfaceTint = MareePalette.Teal
    ) else lightColorScheme(
        primary = MareePalette.Teal, onPrimary = MareePalette.OnPrimary,
        primaryContainer = MareePalette.Subtle, onPrimaryContainer = MareePalette.DeepTeal,
        secondary = MareePalette.Lavender, onSecondary = MareePalette.OnPrimary,
        secondaryContainer = MareePalette.Lilac, onSecondaryContainer = MareePalette.Lavender,
        tertiary = MareePalette.Coral, onTertiary = MareePalette.OnPrimary,
        tertiaryContainer = MareePalette.Peach, onTertiaryContainer = MareePalette.Coral,
        background = MareePalette.Background, onBackground = MareePalette.Ink,
        surface = MareePalette.Surface, onSurface = MareePalette.Ink,
        surfaceVariant = MareePalette.Subtle, onSurfaceVariant = MareePalette.Muted,
        outline = MareePalette.ControlOutline, error = MareePalette.Danger, onError = MareePalette.OnDanger
    )

private val MareeTypography = Typography(
    headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 36.sp, lineHeight = 40.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-1).sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 32.sp, lineHeight = 36.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.8).sp),
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
    val night = AppAppearance.isNight
    val systemNight = isSystemInDarkTheme()
    val activity = LocalContext.current as? Activity
    val statusBarColor = MareePalette.Background.toArgb()
    val navigationBarColor = MareePalette.NavigationBackground.toArgb()
    SideEffect {
        AppAppearance.updateSystemAppearance(systemNight)
        activity?.window?.let { window ->
            window.statusBarColor = statusBarColor
            window.navigationBarColor = navigationBarColor
            WindowCompat.getInsetsController(window, window.decorView).apply {
                isAppearanceLightStatusBars = !night
                isAppearanceLightNavigationBars = false
            }
        }
    }
    val scheme = remember(night) { mareeScheme(night) }
    MaterialTheme(colorScheme = scheme, typography = MareeTypography,
        shapes = Shapes(
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(18.dp),
            large = RoundedCornerShape(24.dp),
            extraLarge = RoundedCornerShape(28.dp)
        ), content = content)
}
