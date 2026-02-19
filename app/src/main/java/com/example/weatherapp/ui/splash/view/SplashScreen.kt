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

@Composable
fun SplashScreen() {

    val infiniteTransition = rememberInfiniteTransition(label = "splash")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val title = stringResource(R.string.app_name)

    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(400)
        textAlpha.animateTo(1f, tween(1200))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RamadanDeepNavy),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Default.WbSunny,
                contentDescription = null,
                tint = RamadanGold,
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale)
                    .alpha(alpha)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White.copy(alpha = textAlpha.value),
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )
        }
    }
}

@Preview(showBackground = true, locale = "en")
@Composable
fun SplashPreviewEnglish() {
    SplashScreen()
}
