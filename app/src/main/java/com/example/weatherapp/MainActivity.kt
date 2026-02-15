package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.weatherapp.ui.main.view.MainScreen
import com.example.weatherapp.ui.theme.ProvideThemeController
import com.example.weatherapp.ui.theme.WeatherAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var repository: com.example.weatherapp.data.repository.AppRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val darkMode by repository.darkModeFlow.collectAsState(initial = "system")
            val isDark = when (darkMode) {
                "dark" -> true
                "light" -> false
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }

            ProvideThemeController(
                isDark = isDark,
                onToggle = { dark -> 
                    kotlinx.coroutines.MainScope().launch {
                        repository.setDarkMode(if (dark) "dark" else "light")
                    }
                }
            ) {
                WeatherAppTheme(darkTheme = isDark) {
                    val navController = rememberNavController()
                    MainScreen(navController = navController)
                }
            }
        }
    }
}
