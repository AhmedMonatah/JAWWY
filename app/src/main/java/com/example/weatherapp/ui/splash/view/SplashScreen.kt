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
import com.example.weatherapp.ui.theme.LocalIsDark
import com.example.weatherapp.ui.theme.DarkBlue
import com.example.weatherapp.ui.theme.DeepNavy
import com.example.weatherapp.ui.theme.Midnight

@Composable
fun SplashScreen() {
    val title = stringResource(R.string.app_name)

    val isDark = LocalIsDark.current
    val bgColors = if (isDark) {
        listOf(Midnight, DeepNavy, DarkBlue)
    } else {
        listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB), Color.White)
    }

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
