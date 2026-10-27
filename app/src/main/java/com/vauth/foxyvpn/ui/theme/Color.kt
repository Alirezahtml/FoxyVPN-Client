package com.vauth.foxyvpn.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.vauth.foxyvpn.data.FoxyColorTheme

val FoxSeed = Color(0xFF00D8F8)

// Soft Neumorphism Light Palette (Warm-Cool Porcelain with tactile dual shadows)
val NeuLightBg = Color(0xFFE9EDF4)
val NeuLightSurface = Color(0xFFF1F4F9)
val NeuLightSurfaceElevated = Color(0xFFE2E7F0)
val NeuLightShadowLight = Color(0xFFFFFFFF)
val NeuLightShadowDark = Color(0xFFC0C7D6)

val md_theme_light_primary = Color(0xFF0088F8)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFE0F2FE)
val md_theme_light_onPrimaryContainer = Color(0xFF082F49)
val md_theme_light_inversePrimary = Color(0xFF66DDF8)

val md_theme_light_secondary = Color(0xFF0058F0)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFDBEAFE)
val md_theme_light_onSecondaryContainer = Color(0xFF0E1D2E)

val md_theme_light_tertiary = Color(0xFF10B981)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFD1FAE5)
val md_theme_light_onTertiaryContainer = Color(0xFF042F2E)

val md_theme_light_error = Color(0xFFE11D48)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_errorContainer = Color(0xFFFFE4E6)
val md_theme_light_onErrorContainer = Color(0xFF4C0519)

val md_theme_light_background = NeuLightBg
val md_theme_light_onBackground = Color(0xFF0F172A)
val md_theme_light_surface = NeuLightSurface
val md_theme_light_onSurface = Color(0xFF0F172A)
val md_theme_light_surfaceVariant = Color(0xFFDDE3ED)
val md_theme_light_onSurfaceVariant = Color(0xFF475569)
val md_theme_light_surfaceTint = md_theme_light_primary

val md_theme_light_surfaceContainerLowest = Color(0xFFFFFFFF)
val md_theme_light_surfaceContainerLow = Color(0xFFF6F8FC)
val md_theme_light_surfaceContainer = NeuLightSurface
val md_theme_light_surfaceContainerHigh = Color(0xFFE5EAF2)
val md_theme_light_surfaceContainerHighest = Color(0xFFDCE2EC)
val md_theme_light_surfaceBright = Color(0xFFF8FAFC)
val md_theme_light_surfaceDim = Color(0xFFD5DCE8)

val md_theme_light_outline = Color(0xFF94A3B8)
val md_theme_light_outlineVariant = Color(0xFFCBD5E1)
val md_theme_light_inverseSurface = Color(0xFF0F172A)
val md_theme_light_inverseOnSurface = Color(0xFFF8FAFC)
val md_theme_light_scrim = Color(0xFF000000)

// Modern 2026 OLED Black + Blue Spectrum Palette
// Palette sampled from the official blue/cyan FOX VPN icon + deep OLED black + semantic green for connected state
val NeuDarkBg = Color(0xFF05080E)
val NeuDarkSurface = Color(0xFF0A0F19)
val NeuDarkSurfaceElevated = Color(0xFF101726)
val NeuDarkShadowLight = Color(0xFF1A2338)
val NeuDarkShadowDark = Color(0xFF020306)

val md_theme_dark_primary = Color(0xFF00D8F8)
val md_theme_dark_onPrimary = Color(0xFF020813)
val md_theme_dark_primaryContainer = Color(0xFF072746)
val md_theme_dark_onPrimaryContainer = Color(0xFFE0F7FE)
val md_theme_dark_inversePrimary = Color(0xFF0088F8)

val md_theme_dark_secondary = Color(0xFF0058F0)
val md_theme_dark_onSecondary = Color(0xFFFFFFFF)
val md_theme_dark_secondaryContainer = Color(0xFF132A6B)
val md_theme_dark_onSecondaryContainer = Color(0xFFDBEAFE)

val md_theme_dark_tertiary = Color(0xFF10B981)
val md_theme_dark_onTertiary = Color(0xFF022C22)
val md_theme_dark_tertiaryContainer = Color(0xFF064E3B)
val md_theme_dark_onTertiaryContainer = Color(0xFFD1FAE5)

val md_theme_dark_error = Color(0xFFF43F5E)
val md_theme_dark_onError = Color(0xFF4C0519)
val md_theme_dark_errorContainer = Color(0xFF881337)
val md_theme_dark_onErrorContainer = Color(0xFFFFE4E6)

val md_theme_dark_background = NeuDarkBg
val md_theme_dark_onBackground = Color(0xFFF1F5F9)
val md_theme_dark_surface = NeuDarkSurface
val md_theme_dark_onSurface = Color(0xFFF1F5F9)
val md_theme_dark_surfaceVariant = Color(0xFF182030)
val md_theme_dark_onSurfaceVariant = Color(0xFF94A3B8)
val md_theme_dark_surfaceTint = md_theme_dark_primary

val md_theme_dark_surfaceContainerLowest = Color(0xFF030509)
val md_theme_dark_surfaceContainerLow = Color(0xFF070B13)
val md_theme_dark_surfaceContainer = NeuDarkSurface
val md_theme_dark_surfaceContainerHigh = Color(0xFF121B2B)
val md_theme_dark_surfaceContainerHighest = Color(0xFF172338)
val md_theme_dark_surfaceBright = Color(0xFF22324F)
val md_theme_dark_surfaceDim = Color(0xFF070C15)

val md_theme_dark_outline = Color(0xFF2A3952)
val md_theme_dark_outlineVariant = Color(0xFF192437)
val md_theme_dark_inverseSurface = Color(0xFFF8FAFC)
val md_theme_dark_inverseOnSurface = Color(0xFF05080E)
val md_theme_dark_scrim = Color(0xFF000000)

data class FoxyStatusColors(
    val connected: Color,
    val connecting: Color,
    val disconnected: Color,
)

val LightStatusColors = FoxyStatusColors(
    connected = Color(0xFF059669),
    connecting = Color(0xFF0284C7),
    disconnected = Color(0xFF64748B),
)

val DarkStatusColors = FoxyStatusColors(
    connected = Color(0xFF10B981),
    connecting = Color(0xFF00D8F8),
    disconnected = Color(0xFF64748B),
)

@Suppress("UNUSED_PARAMETER")
fun statusColorsForTheme(darkTheme: Boolean, colorTheme: FoxyColorTheme): FoxyStatusColors {
    return if (darkTheme) DarkStatusColors else LightStatusColors
}

val LocalFoxyStatusColors = staticCompositionLocalOf { DarkStatusColors }
