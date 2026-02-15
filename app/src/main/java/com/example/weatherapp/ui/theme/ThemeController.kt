package com.example.weatherapp.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

// Local Composition to provide the theme controller
val LocalThemeController = compositionLocalOf<ThemeController> {
    error("No ThemeController provided")
}

class ThemeController {
    var isDarkTheme by mutableStateOf(false)
        private set

    fun toggleTheme() {
        isDarkTheme = !isDarkTheme
    }
    
    fun setTheme(isDark: Boolean) {
        isDarkTheme = isDark
    }
}

@Composable
fun ProvideThemeController(content: @Composable () -> Unit) {
    val themeController = remember { ThemeController() }
    CompositionLocalProvider(LocalThemeController provides themeController) {
        content()
    }
}
