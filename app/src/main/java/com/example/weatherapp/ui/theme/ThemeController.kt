package com.example.weatherapp.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

val LocalThemeController = compositionLocalOf<ThemeController> {
    error("No ThemeController provided")
}

class ThemeController(
    initialIsDark: Boolean,
    private val onToggle: (Boolean) -> Unit
) {
    var isDarkTheme by mutableStateOf(initialIsDark)
        private set

    fun toggleTheme() {
        isDarkTheme = !isDarkTheme
        onToggle(isDarkTheme)
    }
    
    fun updateTheme(isDark: Boolean) {
        isDarkTheme = isDark
    }
}

@Composable
fun ProvideThemeController(
    isDark: Boolean,
    onToggle: (Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    val themeController = remember(isDark) { ThemeController(isDark, onToggle) }
    CompositionLocalProvider(LocalThemeController provides themeController) {
        content()
    }
}
