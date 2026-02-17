package com.example.weatherapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.weatherapp.ui.main.view.MainScreen
import com.example.weatherapp.ui.navigation.Screen
import com.example.weatherapp.ui.theme.WeatherAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject lateinit var repository: com.example.weatherapp.data.repository.AppRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDark = androidx.compose.foundation.isSystemInDarkTheme()

            val currentLang by repository.languageFlow.collectAsState(initial = "en")
            LaunchedEffect(currentLang) {
                val locales = androidx.core.os.LocaleListCompat.forLanguageTags(currentLang)
                AppCompatDelegate.setApplicationLocales(locales)
            }

            WeatherAppTheme(darkTheme = isDark) {
                val navController = rememberNavController()
                val onboardingShown by repository.onboardingShownFlow.collectAsState(initial = true)

                MainScreen(
                    navController = navController,
                    startDestination = if (onboardingShown) Screen.Dashboard.route else Screen.Onboarding.route
                )
            }
        }
    }
}
