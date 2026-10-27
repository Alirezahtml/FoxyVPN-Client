package com.vauth.foxyvpn.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.vauth.foxyvpn.data.FoxyColorTheme
import com.vauth.foxyvpn.data.FoxyUiDesignStyle
import com.vauth.foxyvpn.data.HeroWaveAnimationStyle
import com.vauth.foxyvpn.data.SettingsStore

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
}

class ThemeController(private val settingsStore: SettingsStore) {

    var mode: ThemeMode by mutableStateOf(settingsStore.themeMode)
        private set

    var colorTheme: FoxyColorTheme by mutableStateOf(settingsStore.appColorTheme)
        private set

    var uiDesignStyle: FoxyUiDesignStyle by mutableStateOf(settingsStore.appUiDesignStyle)
        private set

    var heroWaveStyle: HeroWaveAnimationStyle by mutableStateOf(settingsStore.heroWaveAnimationStyle)
        private set

    fun resolveDark(systemInDarkTheme: Boolean): Boolean = when (mode) {
        ThemeMode.SYSTEM -> systemInDarkTheme
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    fun set(next: ThemeMode) {
        if (next == mode) return
        mode = next
        settingsStore.themeMode = next
    }

    fun toggle(systemInDarkTheme: Boolean) {
        set(if (resolveDark(systemInDarkTheme)) ThemeMode.LIGHT else ThemeMode.DARK)
    }

    fun applyColorTheme(next: FoxyColorTheme) {
        if (next == colorTheme) return
        colorTheme = next
        settingsStore.appColorTheme = next
    }

    fun cycleColorTheme() {
        val entries = FoxyColorTheme.entries
        val nextIdx = (entries.indexOf(colorTheme) + 1) % entries.size
        applyColorTheme(entries[nextIdx])
    }

    fun applyUiDesignStyle(next: FoxyUiDesignStyle) {
        if (next == uiDesignStyle) return
        uiDesignStyle = next
        settingsStore.appUiDesignStyle = next
        if (next == FoxyUiDesignStyle.DARK_LUXURY && mode != ThemeMode.DARK) {
            set(ThemeMode.DARK)
        }
    }

    fun selectUiDesignStyle(next: FoxyUiDesignStyle) = applyUiDesignStyle(next)

    fun cycleUiDesignStyle() {
        val entries = FoxyUiDesignStyle.entries
        val nextIdx = (entries.indexOf(uiDesignStyle) + 1) % entries.size
        applyUiDesignStyle(entries[nextIdx])
    }

    fun applyHeroWaveStyle(next: HeroWaveAnimationStyle) {
        if (next == heroWaveStyle) return
        heroWaveStyle = next
        settingsStore.heroWaveAnimationStyle = next
    }

    fun cycleHeroWaveStyle() {
        val entries = HeroWaveAnimationStyle.entries
        val nextIdx = (entries.indexOf(heroWaveStyle) + 1) % entries.size
        applyHeroWaveStyle(entries[nextIdx])
    }
}

@Composable
fun rememberThemeController(settingsStore: SettingsStore): ThemeController =
    remember(settingsStore) { ThemeController(settingsStore) }
