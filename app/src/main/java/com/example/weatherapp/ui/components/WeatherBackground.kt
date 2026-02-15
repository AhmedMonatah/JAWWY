package com.example.weatherapp.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import java.util.*
import kotlin.random.Random

@Composable
fun WeatherBackground(weatherType: String = "snow") {
    val infiniteTransition = rememberInfiniteTransition(label = "weather")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    val particles = remember {
        List(100) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                speed = 0.1f + Random.nextFloat() * 0.3f,
                size = 1f + Random.nextFloat() * 3f,
                alpha = 0.1f + Random.nextFloat() * 0.4f
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        particles.forEach { particle ->
            val shiftedY = (particle.y + progress * particle.speed) % 1f
            val yPos = shiftedY * height
            
            drawCircle(
                color = Color.White.copy(alpha = particle.alpha),
                radius = particle.size,
                center = Offset(particle.x * width, yPos)
            )
        }
    }
}

data class Particle(
    val x: Float,
    val y: Float,
    val speed: Float,
    val size: Float,
    val alpha: Float
)
