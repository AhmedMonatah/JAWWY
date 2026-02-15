package com.example.weatherapp.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.util.*
import kotlin.random.Random

@Composable
fun WeatherBackground(weatherType: String = "clear") {
    val infiniteTransition = rememberInfiniteTransition(label = "weather")
    
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    // Pulse for "glow" effects
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val particles = remember(weatherType) {
        val count = when (weatherType.lowercase()) {
            "snow" -> 80
            "rain" -> 150
            else -> 40
        }
        List(count) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                speed = 0.05f + Random.nextFloat() * 0.2f,
                size = 1f + Random.nextFloat() * 4f,
                alpha = 0.2f + Random.nextFloat() * 0.5f,
                rotation = Random.nextFloat() * 360f
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        when (weatherType.lowercase()) {
            "clear" -> {
                // Sun Glow
                drawCircle(
                    color = Color.Yellow.copy(alpha = 0.08f * pulse),
                    radius = 400.dp.toPx(),
                    center = Offset(canvasWidth * 0.8f, 150.dp.toPx())
                )
                drawCircle(
                    color = Color.Yellow.copy(alpha = 0.15f * pulse),
                    radius = 200.dp.toPx(),
                    center = Offset(canvasWidth * 0.8f, 150.dp.toPx())
                )
            }
            "snow" -> {
                particles.forEach { p ->
                    val shiftedY = (p.y + progress * p.speed) % 1f
                    val yPos = shiftedY * canvasHeight
                    val xPos = p.x * canvasWidth + (kotlin.math.sin(progress * 2 * Math.PI.toFloat() + p.x * 10) * 20)
                    
                    // Draw "Star" snowflake (simple spark)
                    drawCircle(
                        color = Color.White.copy(alpha = p.alpha * pulse),
                        radius = p.size,
                        center = Offset(xPos, yPos)
                    )
                    drawLine(
                        color = Color.White.copy(alpha = p.alpha * 0.5f),
                        start = Offset(xPos - p.size * 2, yPos),
                        end = Offset(xPos + p.size * 2, yPos),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawLine(
                        color = Color.White.copy(alpha = p.alpha * 0.5f),
                        start = Offset(xPos, yPos - p.size * 2),
                        end = Offset(xPos, yPos + p.size * 2),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }
            "rain" -> {
                particles.forEach { p ->
                    val shiftedY = (p.y + progress * p.speed * 3) % 1f
                    val yPos = shiftedY * canvasHeight
                    val xPos = p.x * canvasWidth
                    
                    drawLine(
                        color = Color.White.copy(alpha = 0.3f),
                        start = Offset(xPos, yPos),
                        end = Offset(xPos, yPos + 15.dp.toPx()),
                        strokeWidth = 1.5.dp.toPx()
                    )
                }
            }
            "clouds" -> {
                particles.forEach { p ->
                    val shiftedX = (p.x + progress * p.speed * 0.2f) % 1f
                    val xPos = shiftedX * canvasWidth
                    val yPos = p.y * canvasHeight
                    
                    drawCircle(
                        color = Color.White.copy(alpha = 0.1f),
                        radius = p.size * 20,
                        center = Offset(xPos, yPos)
                    )
                }
            }
        }
    }
}

data class Particle(
    val x: Float,
    val y: Float,
    val speed: Float,
    val size: Float,
    val alpha: Float,
    val rotation: Float = 0f
)
