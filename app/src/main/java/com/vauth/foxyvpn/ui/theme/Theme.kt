package com.vauth.foxyvpn.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.view.WindowCompat
import com.vauth.foxyvpn.data.FoxyColorTheme
import com.vauth.foxyvpn.data.FoxyUiDesignStyle

private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    inversePrimary = md_theme_light_inversePrimary,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    onError = md_theme_light_onError,
    errorContainer = md_theme_light_errorContainer,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    surfaceTint = md_theme_light_surfaceTint,
    surfaceBright = md_theme_light_surfaceBright,
    surfaceDim = md_theme_light_surfaceDim,
    surfaceContainer = md_theme_light_surfaceContainer,
    surfaceContainerHigh = md_theme_light_surfaceContainerHigh,
    surfaceContainerHighest = md_theme_light_surfaceContainerHighest,
    surfaceContainerLow = md_theme_light_surfaceContainerLow,
    surfaceContainerLowest = md_theme_light_surfaceContainerLowest,
    outline = md_theme_light_outline,
    outlineVariant = md_theme_light_outlineVariant,
    inverseSurface = md_theme_light_inverseSurface,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    scrim = md_theme_light_scrim,
)

private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    inversePrimary = md_theme_dark_inversePrimary,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    onError = md_theme_dark_onError,
    errorContainer = md_theme_dark_errorContainer,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    surfaceTint = md_theme_dark_surfaceTint,
    surfaceBright = md_theme_dark_surfaceBright,
    surfaceDim = md_theme_dark_surfaceDim,
    surfaceContainer = md_theme_dark_surfaceContainer,
    surfaceContainerHigh = md_theme_dark_surfaceContainerHigh,
    surfaceContainerHighest = md_theme_dark_surfaceContainerHighest,
    surfaceContainerLow = md_theme_dark_surfaceContainerLow,
    surfaceContainerLowest = md_theme_dark_surfaceContainerLowest,
    outline = md_theme_dark_outline,
    outlineVariant = md_theme_dark_outlineVariant,
    inverseSurface = md_theme_dark_inverseSurface,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    scrim = md_theme_dark_scrim,
)

@Suppress("UNUSED_PARAMETER")
@Composable
fun FoxyVpnTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    appLanguage: AppLanguage = AppLanguage.EN,
    colorTheme: FoxyColorTheme = FoxyColorTheme.CYBER_FOX,
    uiDesignStyle: FoxyUiDesignStyle = FoxyUiDesignStyle.NEUMORPHISM,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val systemInDarkTheme = isSystemInDarkTheme()
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> systemInDarkTheme
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val statusColors = statusColorsForTheme(darkTheme, colorTheme)
    val rawPrimary = if (darkTheme) md_theme_dark_primary else md_theme_light_primary
    val targetSecondary = if (darkTheme) md_theme_dark_secondary else md_theme_light_secondary

    val animatedPrimary by animateColorAsState(
        targetValue = rawPrimary,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "themePrimary",
    )
    val animatedSecondary by animateColorAsState(
        targetValue = targetSecondary,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "themeSecondary",
    )
    val animatedContainer by animateColorAsState(
        targetValue = if (darkTheme) {
            rawPrimary.copy(alpha = 0.24f)
        } else {
            rawPrimary.copy(alpha = 0.16f)
        },
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "themeContainer",
    )

    val baseScheme = if (darkTheme) DarkColors else LightColors

    val colorScheme = baseScheme.copy(
        primary = animatedPrimary,
        onPrimary = Color.White,
        primaryContainer = animatedContainer,
        onPrimaryContainer = if (darkTheme) Color(0xFFF8FAFC) else Color(0xFF0F172A),
        secondary = animatedSecondary,
        surfaceTint = animatedPrimary,
    )

    val neuColors = if (darkTheme) {
        NeumorphicColors(
            background = NeuDarkBg,
            surface = NeuDarkSurface,
            surfaceElevated = NeuDarkSurfaceElevated,
            shadowLight = NeuDarkShadowLight,
            shadowDark = NeuDarkShadowDark,
            accent = animatedPrimary,
            textPrimary = md_theme_dark_onBackground,
            textSecondary = md_theme_dark_onSurfaceVariant,
            isDark = true,
            designStyle = FoxyUiDesignStyle.NEUMORPHISM,
        )
    } else {
        NeumorphicColors(
            background = NeuLightBg,
            surface = NeuLightSurface,
            surfaceElevated = NeuLightSurfaceElevated,
            shadowLight = NeuLightShadowLight,
            shadowDark = NeuLightShadowDark,
            accent = animatedPrimary,
            textPrimary = md_theme_light_onBackground,
            textSecondary = md_theme_light_onSurfaceVariant,
            isDark = false,
            designStyle = FoxyUiDesignStyle.NEUMORPHISM,
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    val strings = foxyStringsFor(appLanguage)
    val fontHierarchy = fontHierarchyFor(appLanguage)
    val typography = foxyTypographyFor(appLanguage)
    val layoutDirection = if (appLanguage.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(
        LocalFoxyStatusColors provides statusColors,
        LocalNeumorphicColors provides neuColors,
        LocalFoxyStrings provides strings,
        LocalFoxyFonts provides fontHierarchy,
        LocalLayoutDirection provides layoutDirection,
    ) {
        MaterialExpressiveTheme(
            colorScheme = colorScheme,
            typography = typography,
            motionScheme = MotionScheme.expressive(),
            content = content,
        )
    }
}

