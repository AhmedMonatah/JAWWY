package com.example.weatherapp.ui.splash.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.weatherapp.ui.splash.view.components.SplashContent
import com.example.weatherapp.ui.theme.LocalIsDark
import com.example.weatherapp.ui.theme.Midnight
import com.example.weatherapp.ui.theme.DeepNavy
import com.example.weatherapp.ui.theme.DarkBlue

@Composable
fun SplashScreen() {
    val isDark = LocalIsDark.current

    val bgColors = if (isDark) {
        listOf(
            Midnight,
            DeepNavy,
            DarkBlue
        )
    } else {
        listOf(
            Color(0xFFFFFFFF),
            Color(0xFFF0F6FF),
            Color(0xFFDEECFC)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(bgColors)),
        contentAlignment = Alignment.Center
    ) {
        SplashContent(
            isDark = isDark
        )
    }
}
