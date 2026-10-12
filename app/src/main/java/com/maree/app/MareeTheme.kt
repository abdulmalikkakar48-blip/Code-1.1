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
    background = 0xFFF2F4F0, surface = 0xFFFFFFFF, subtle = 0xFFEEF4EE,
    ink = 0xFF10251E, muted = 0xFF52645A, primary = 0xFF174D38,
    onPrimary = 0xFFFFFFFF, strongInk = 0xFF102B22, softAccent = 0xFFC9F79B,
    outline = 0xFFE4E9E1, danger = 0xFFA33939, onDanger = 0xFFFFFFFF,
    heroBackground = 0xFF102B22, onHero = 0xFFFFFFFF, onHeroMuted = 0xFFCBDBD0,
    heroAccent = 0xFFC9F79B, onHeroAccent = 0xFF102B22,
    navigationBackground = 0xFF102B22, navigationMuted = 0xFFAFBEB5
)

internal val SelkieNightColors = SelkieThemeColors(
    background = 0xFF070C14, surface = 0xFF101B2B, subtle = 0xFF17263B,
    ink = 0xFFEDF3FF, muted = 0xFFA6B8D0, primary = 0xFF8DBEFF,
    onPrimary = 0xFF081B33, strongInk = 0xFFCBDFFF, softAccent = 0xFF152D4B,
    outline = 0xFF354A66, danger = 0xFFFF9EAA, onDanger = 0xFF3E0C18,
    heroBackground = 0xFF10213B, onHero = 0xFFEDF3FF, onHeroMuted = 0xFFBBCBE2,
    heroAccent = 0xFF8DBEFF, onHeroAccent = 0xFF081B33,
    navigationBackground = 0xFF070D17, navigationMuted = 0xFFB3C2D8
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
    // The seal and its existing light background remain readable in either appearance.
    val BrandBackground = Color(0xFFC9F79B)
    val Coral: Color get() = Teal
    val Peach: Color get() = Subtle
    val Lavender: Color get() = Teal
    val Lilac: Color get() = Subtle
    val Amber: Color get() = Teal
    val Sand: Color get() = Subtle
    val Outline: Color get() = Color(colors.outline)
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
        outline = MareePalette.Outline, error = MareePalette.Danger, onError = MareePalette.OnDanger,
        inverseSurface = Color(0xFFDDE6F4), inverseOnSurface = Color(0xFF142237),
        inversePrimary = Color(0xFF174D88), surfaceTint = MareePalette.Teal
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
        outline = MareePalette.Outline, error = MareePalette.Danger, onError = MareePalette.OnDanger
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
            medium = RoundedCornerShape(16.dp),
            large = RoundedCornerShape(20.dp),
            extraLarge = RoundedCornerShape(24.dp)
        ), content = content)
}
