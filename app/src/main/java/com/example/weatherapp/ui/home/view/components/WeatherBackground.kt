package com.example.weatherapp.ui.home.view.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import kotlin.random.Random
import com.example.weatherapp.model.Particle
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import com.example.weatherapp.ui.theme.RamadanGold
import com.example.weatherapp.ui.theme.RamadanLanternOrange
import com.example.weatherapp.ui.theme.RamadanMoonGlow
import com.example.weatherapp.ui.theme.RamadanStarLight
import kotlin.math.sin

@Composable
fun WeatherBackground(
    weatherType: String = "clear",
    isCold: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "weather")
    
    val timeSource = remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) {
        val startTime = withFrameMillis { it }
        while (true) {
            withFrameMillis { frameTime ->
                timeSource.longValue = frameTime - startTime
            }
        }
    }
    val time = timeSource.longValue / 1000f // time in seconds

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val moonGlow by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "moonGlow"
    )

    // Stars and golden particles
    val particles = remember(weatherType, isCold) {
        val count = when {
            weatherType == "snow" || (weatherType == "clear" && isCold) -> 120
            weatherType == "clear" -> 40
            else -> 60
        }
        List(count) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 2.5f + 1f,
                speed = Random.nextFloat() * 0.05f + 0.02f,
                alpha = Random.nextFloat() * 0.6f + 0.3f
            )
        }
    }

    // Lantern-like floating particles (warm golden)
    val lanternParticles = remember {
        List(10) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 3f + 1.5f,
                speed = Random.nextFloat() * 0.02f + 0.01f,
                alpha = Random.nextFloat() * 0.4f + 0.2f
            )
        }
    }

    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        val moonX = if (isRtl) 0.18f * width else 0.82f * width
        val moonCenter = Offset(moonX, 90.dp.toPx())

        // Stars / falling particles
        particles.forEachIndexed { index, p ->
            val individualOffset = (time * p.speed + index * 0.07f)
            val shiftedY = (p.y + individualOffset) % 1f
            val yPos = shiftedY * height
            
            // Subtle horizontal sway
            val xOffset = sin(time * 0.5f + p.x * 10f) * 10f
            val xPos = (p.x * width + xOffset + width) % width

            drawCircle(
                color = RamadanStarLight.copy(alpha = p.alpha * pulse),
                radius = p.size,
                center = Offset(xPos, yPos)
            )

            if (p.size > 2.2f) {
                drawLine(
                    color = RamadanMoonGlow.copy(alpha = p.alpha * 0.3f * pulse),
                    start = Offset(xPos - p.size * 2f, yPos),
                    end = Offset(xPos + p.size * 2f, yPos),
                    strokeWidth = 0.5.dp.toPx()
                )
                drawLine(
                    color = RamadanMoonGlow.copy(alpha = p.alpha * 0.3f * pulse),
                    start = Offset(xPos, yPos - p.size * 2f),
                    end = Offset(xPos, yPos + p.size * 2f),
                    strokeWidth = 0.5.dp.toPx()
                )
            }
        }

        // Floating golden lantern particles
        lanternParticles.forEachIndexed { index, p ->
            val floatTime = time * 0.3f + index * 0.5f
            val xOffset = sin(floatTime) * 40f
            val xPos = (p.x * width + xOffset + width) % width
            
            val yShift = (p.y - time * p.speed + 1000f) % 1f
            val yPos = yShift * height

            // Warm glow circle
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        RamadanLanternOrange.copy(alpha = p.alpha * pulse),
                        RamadanGold.copy(alpha = p.alpha * 0.3f * pulse),
                        Color.Transparent
                    ),
                    center = Offset(xPos, yPos),
                    radius = p.size * 10f
                ),
                radius = p.size * 10f,
                center = Offset(xPos, yPos)
            )
            drawCircle(
                color = RamadanMoonGlow.copy(alpha = p.alpha * pulse * 0.8f),
                radius = p.size * 0.8f,
                center = Offset(xPos, yPos)
            )
        }

        // Crescent moon (always visible for Ramadan)
        if (isRtl) {
            withTransform({
                scale(-1f, 1f, pivot = moonCenter)
            }) {
                drawCrescentMoon(
                    center = moonCenter,
                    outerRadius = 40.dp.toPx(),
                    glowAlpha = moonGlow,
                    isRtl = isRtl
                )
            }
        } else {
            drawCrescentMoon(
                center = moonCenter,
                outerRadius = 40.dp.toPx(),
                glowAlpha = moonGlow,
                isRtl = isRtl
            )
        }

        // Clear weather: golden sun glow behind crescent
        if (weatherType.lowercase() == "clear") {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        RamadanGold.copy(alpha = 0.12f * pulse),
                        Color.Transparent
                    ),
                    center = moonCenter,
                    radius = 200.dp.toPx()
                ),
                radius = 200.dp.toPx(),
                center = moonCenter
            )
        }
    }
}

/**
 * Draws a crescent moon shape with glow effect.
 */
private fun DrawScope.drawCrescentMoon(
    center: Offset,
    outerRadius: Float,
    glowAlpha: Float,
    isRtl: Boolean
) {
    // Outer glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                RamadanMoonGlow.copy(alpha = glowAlpha),
                RamadanGold.copy(alpha = glowAlpha * 0.3f),
                Color.Transparent
            ),
            center = center,
            radius = outerRadius * 3f
        ),
        radius = outerRadius * 3f,
        center = center
    )

    // Moon body (crescent via two overlapping circles)
    drawCircle(
        color = RamadanMoonGlow.copy(alpha = 0.9f),
        radius = outerRadius,
        center = center
    )
    // Inner circle to create crescent shape
    val xOffset = if (isRtl) -outerRadius * 0.35f else outerRadius * 0.35f
    drawCircle(
        color = Color(0xFF0A1628), // Match background
        radius = outerRadius * 0.78f,
        center = Offset(center.x + xOffset, center.y - outerRadius * 0.1f)
    )
}

// Particle model moved to WeatherEffectModels.kt
