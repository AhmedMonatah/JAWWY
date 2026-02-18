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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.data.repository.AppRepository
import com.example.weatherapp.ui.main.view.MainScreen
import com.example.weatherapp.ui.navigation.Screen
import com.example.weatherapp.ui.theme.AccentPurple
import com.example.weatherapp.ui.theme.DashboardBackground
import com.example.weatherapp.ui.theme.WeatherAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject lateinit var repository: AppRepository

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermission()

        setContent {
            val isDark = androidx.compose.foundation.isSystemInDarkTheme()

            val currentLang by repository.languageFlow.collectAsState(initial = "en")
            LaunchedEffect(currentLang) {
                val locales = androidx.core.os.LocaleListCompat.forLanguageTags(currentLang)
                AppCompatDelegate.setApplicationLocales(locales)
            }

            WeatherAppTheme(darkTheme = isDark) {
                val onboardingShown by repository.onboardingShownFlow.collectAsState(initial = null)

                if (onboardingShown != null) {
                    if (onboardingShown == true) {
                        val navController = rememberNavController()
                        MainScreen(
                            navController = navController,
                            startDestination = Screen.Dashboard.route
                        )
                    } else {
                        com.example.weatherapp.ui.onboarding.OnboardingScreen(
                            onFinish = {
                                // The flow will automatically update when repository.setOnboardingShown() is called
                            }
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize().background(DashboardBackground),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AccentPurple)
                    }
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
