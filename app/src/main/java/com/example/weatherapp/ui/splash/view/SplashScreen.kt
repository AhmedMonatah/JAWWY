package com.example.weatherapp.ui.splash.view

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R
import com.example.weatherapp.ui.theme.RamadanDeepNavy
import com.example.weatherapp.ui.theme.RamadanGold
import kotlinx.coroutines.delay
import com.example.weatherapp.ui.splash.view.components.SplashContent

@Composable
fun SplashScreen() {

    val infiniteTransition = rememberInfiniteTransition(label = "splash")

    // Sun pulse animation
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val title = stringResource(R.string.app_name)

    val textAlpha = remember { Animatable(0f) }
    val textOffset = remember { Animatable(30f) }

    LaunchedEffect(Unit) {
        delay(300)
        textAlpha.animateTo(1f, tween(1000))
        textOffset.animateTo(0f, tween(1000, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RamadanDeepNavy),
        contentAlignment = Alignment.Center
    ) {
        SplashContent(
            scale = scale,
            alpha = alpha,
            textAlpha = textAlpha,
            textOffset = textOffset,
            title = title
        )
    }
}
