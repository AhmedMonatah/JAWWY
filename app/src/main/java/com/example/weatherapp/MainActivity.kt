package com.example.weatherapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.data.repository.AppRepository
import com.example.weatherapp.ui.main.view.MainScreen
import com.example.weatherapp.ui.navigation.Screen
import com.example.weatherapp.ui.splash.view.SplashScreen
import com.example.weatherapp.ui.theme.AccentPurple
import com.example.weatherapp.ui.theme.DashboardBackground
import com.example.weatherapp.ui.theme.WeatherAppTheme
import kotlinx.coroutines.delay
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject lateinit var repository: AppRepository

    companion object {
        private var splashShown = false
    }

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
                val onboardingShown by repository.onboardingShownFlow.collectAsState(initial = null)
                var showSplash by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(!splashShown) }

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
                    if (onboardingShown == true) {
                        val navController = rememberNavController()
                        MainScreen(
                            navController = navController,
                            startDestination = Screen.Dashboard.route
                        )
                    } else {
                        com.example.weatherapp.ui.onboarding.view.OnboardingScreen(
                            onFinish = {
                                // The flow will automatically update when repository.setOnboardingShown() is called
                            }
                        )
                    }
                }
            }
        }
    }

}
