package com.example.weatherapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.*
import com.example.weatherapp.di.LocalAppContainer
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.ui.main.view.MainScreen
import com.example.weatherapp.ui.navigation.Screen
import com.example.weatherapp.ui.onboarding.view.OnboardingScreen
import com.example.weatherapp.ui.splash.view.SplashScreen
import com.example.weatherapp.ui.theme.WeatherAppTheme
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {
    private lateinit var repository: WeatherRepository

    companion object {
        private var splashShown = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as WeatherApplication
        repository = app.container.weatherRepository
        enableEdgeToEdge()

        setContent {
            val isDark = true

            val currentLang by repository.languageFlow.collectAsState(initial = "en")
            LaunchedEffect(currentLang) {
                val locales = androidx.core.os.LocaleListCompat.forLanguageTags(currentLang)
                AppCompatDelegate.setApplicationLocales(locales)
            }

            CompositionLocalProvider(LocalAppContainer provides app.container) {
                WeatherAppTheme(darkTheme = isDark) {
                    val onboardingShown by repository.onboardingShownFlow.collectAsState(initial = null)
                    var showSplash by androidx.compose.runtime.saveable.rememberSaveable {
                        mutableStateOf(
                            !splashShown
                        )
                    }

                    LaunchedEffect(onboardingShown) {
                        if (showSplash) {
                            delay(5000)
                            showSplash = false
                            splashShown = true
                        }
                    }

                    if (showSplash || onboardingShown == null) {
                        SplashScreen()
                    } else {
                        var currentOnboardingState by remember { mutableStateOf(onboardingShown) }
                        
                        LaunchedEffect(onboardingShown) {
                            currentOnboardingState = onboardingShown
                        }

                        if (currentOnboardingState == true) {
                            val navController = rememberNavController()
                            MainScreen(
                                navController = navController,
                                startDestination = Screen.Dashboard.route
                            )
                        } else {
                            OnboardingScreen(
                                onFinish = {
                                    currentOnboardingState = true
                                }
                            )
                        }
                    }
                }
            }
        }

    }
}
