package com.example.weatherapp.ui.splash.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.weatherapp.R
import com.example.weatherapp.ui.splash.view.components.SplashContent
import com.example.weatherapp.ui.theme.RamadanDarkBlue
import com.example.weatherapp.ui.theme.RamadanDeepNavy
import com.example.weatherapp.ui.theme.RamadanMidnight

@Composable
fun SplashScreen() {
    val title = stringResource(R.string.app_name)

    val isDark = androidx.compose.material3.MaterialTheme.colorScheme.background != Color.White
    val bgColors = if (isDark) listOf(RamadanMidnight, RamadanDeepNavy, RamadanDarkBlue) else listOf(Color(0xFFE8EDF5), Color.White, Color.White)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(bgColors)
            ),
        contentAlignment = Alignment.Center
    ) {
        SplashContent(
            title = title
        )
    }
}
