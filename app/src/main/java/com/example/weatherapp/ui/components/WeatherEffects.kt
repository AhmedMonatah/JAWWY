package com.example.weatherapp.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

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
            
            val x = flake.x * canvasWidth + kotlin.math.sin(time * 2 + flake.wobble) * 20
            
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

data class Snowflake(
    val x: Float = Random.nextFloat(),
    val y: Float = Random.nextFloat(),
    val radius: Float = Random.nextFloat() * 5f + 2f,
    val speed: Float = Random.nextFloat() * 0.5f + 0.1f,
    val wobble: Float = Random.nextFloat() * 10f
)

data class Raindrop(
    val x: Float = Random.nextFloat(),
    val y: Float = Random.nextFloat(),
    val length: Float = Random.nextFloat() * 20f + 10f,
    val speed: Float = Random.nextFloat() * 1.5f + 1.0f
)
