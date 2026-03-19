package com.example.weatherapp.ui.home.view.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.weatherapp.data.model.Snowflake
import com.example.weatherapp.data.model.Raindrop
import kotlin.math.sin

@Composable
fun WeatherEffects(
    weatherType: String,
    modifier: Modifier = Modifier
) {
    if (weatherType.contains("snow")) {
        SnowEffect(modifier)
    } else if (weatherType.contains("rain") || weatherType.contains("drizzle") || weatherType.contains("thunder")) {
        RainEffect(modifier)
    }
}

@Composable
fun SnowEffect(modifier: Modifier = Modifier) {
    val snowflakes = remember { List(100) { Snowflake() } }

    var time by remember { mutableStateOf(0f) }
    
    LaunchedEffect(Unit) {
        val startTime = withFrameNanos { it }
        while (true) {
            withFrameNanos { frameTime ->
                time = (frameTime - startTime) / 1_000_000_000f
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        snowflakes.forEach { flake ->

            val y = (flake.y * canvasHeight + time * flake.speed * canvasHeight) % canvasHeight
            
            val x = flake.x * canvasWidth + sin(time * 2 + flake.wobble) * 20
            
            drawCircle(
                color = Color.White.copy(alpha = 0.8f),
                radius = flake.radius,
                center = Offset(x, y)
            )
        }
    }
}

@Composable
fun RainEffect(modifier: Modifier = Modifier) {
    val raindrops = remember { List(150) { Raindrop() } }
    val transition = rememberInfiniteTransition()
    val time by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing) // Faster for rain
        )
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        raindrops.forEach { drop ->
            val y = (drop.y + time * drop.speed * canvasHeight) % canvasHeight
            val x = drop.x * canvasWidth
            
            drawLine(
                color = Color.LightGray.copy(alpha = 0.6f),
                start = Offset(x, y),
                end = Offset(x, y + drop.length),
                strokeWidth = 2f
            )
        }
    }
}

// Models moved to WeatherEffectModels.kt
