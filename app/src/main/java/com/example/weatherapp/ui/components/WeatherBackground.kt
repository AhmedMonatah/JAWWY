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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.withTransform

@Composable
fun WeatherBackground(
    weatherType: String = "clear",
    isCold: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "weather")
    
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Ray rotation
    val rayRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rays"
    )

    // Dynamic Particle System (Falling Stars)
    val particles = remember(weatherType, isCold) {
        val count = when {
            weatherType == "snow" || (weatherType == "clear" && isCold) -> 180
            weatherType == "clear" -> 40
            else -> 80
        }
        List(count) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 2.5f + 1f,
                speed = Random.nextFloat() * 0.04f + 0.02f, // Slower falling
                alpha = Random.nextFloat() * 0.5f + 0.2f
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Always draw falling stars
        particles.forEach { p ->
            // Falling effect: y increases by progress * speed, looping 0 to 1
            val shiftedY = (p.y + progress * (p.speed * 20)) % 1f 
            val yPos = shiftedY * height
            // Subtle horizontal sway
            val xOffset = (kotlin.math.sin(progress * 2 * Math.PI.toFloat() + p.x * 10) * 15)
            val xPos = (p.x * width + xOffset) % width
            
            drawCircle(
                color = Color.White.copy(alpha = p.alpha * pulse),
                radius = p.size,
                center = Offset(xPos, yPos)
            )
            
            // Star twinkle effect for larger ones
            if (p.size > 2.2f) {
                drawLine(
                    color = Color.White.copy(alpha = p.alpha * 0.3f * pulse),
                    start = Offset(xPos - p.size * 2f, yPos),
                    end = Offset(xPos + p.size * 2f, yPos),
                    strokeWidth = 0.5.dp.toPx()
                )
                drawLine(
                    color = Color.White.copy(alpha = p.alpha * 0.3f * pulse),
                    start = Offset(xPos, yPos - p.size * 2f),
                    end = Offset(xPos, yPos + p.size * 2f),
                    strokeWidth = 0.5.dp.toPx()
                )
            }
        }

        // Fixed Sun effect for clear weather
        if (weatherType.lowercase() == "clear") {
            // Sun is fixed at top-right of the screen
            val sunCenter = Offset(width * 0.85f, 100.dp.toPx())
            
            // Core glows
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Yellow.copy(alpha = 0.25f * pulse), Color.Transparent),
                    center = sunCenter,
                    radius = 250.dp.toPx()
                ),
                radius = 250.dp.toPx(),
                center = sunCenter
            )

            // Dynamic Rays (Fixed position, only rotation)
            withTransform({
                rotate(rayRotation, sunCenter)
            }) {
                for (i in 0 until 8) {
                    val angle = i * 45f
                    val rad = Math.toRadians(angle.toDouble())
                    val start = Offset(
                        (sunCenter.x + Math.cos(rad) * 45.dp.toPx()).toFloat(),
                        (sunCenter.y + Math.sin(rad) * 45.dp.toPx()).toFloat()
                    )
                    val end = Offset(
                        (sunCenter.x + Math.cos(rad) * 90.dp.toPx()).toFloat(),
                        (sunCenter.y + Math.sin(rad) * 90.dp.toPx()).toFloat()
                    )
                    drawLine(
                        color = Color.Yellow.copy(alpha = 0.2f * pulse),
                        start = start,
                        end = end,
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawParticles(
    particles: List<Particle>,
    progress: Float,
    canvasWidth: Float,
    canvasHeight: Float,
    pulse: Float,
    isStar: Boolean
) {
    particles.forEach { p ->
        val shiftedY = (p.y + progress * p.speed) % 1f
        val yPos = shiftedY * canvasHeight
        val xPos = (p.x * canvasWidth + (kotlin.math.sin(progress * 2 * Math.PI.toFloat() + p.x * 10) * 20)) % canvasWidth
        
        drawCircle(
            color = Color.White.copy(alpha = p.alpha * pulse),
            radius = p.size,
            center = Offset(xPos, yPos)
        )
        if (isStar && p.size > 2.5f) {
            drawLine(
                color = Color.White.copy(alpha = p.alpha * 0.4f * pulse),
                start = Offset(xPos - p.size * 1.8f, yPos),
                end = Offset(xPos + p.size * 1.8f, yPos),
                strokeWidth = 0.8.dp.toPx()
            )
            drawLine(
                color = Color.White.copy(alpha = p.alpha * 0.4f * pulse),
                start = Offset(xPos, yPos - p.size * 1.8f),
                end = Offset(xPos, yPos + p.size * 1.8f),
                strokeWidth = 0.8.dp.toPx()
            )
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
