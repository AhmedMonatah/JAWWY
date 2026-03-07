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
import com.example.weatherapp.ui.theme.LocalIsDark
import com.example.weatherapp.ui.theme.DarkBlue
import com.example.weatherapp.ui.theme.DeepNavy
import com.example.weatherapp.ui.theme.Gold
import com.example.weatherapp.ui.theme.LanternOrange
import com.example.weatherapp.ui.theme.Midnight
import com.example.weatherapp.ui.theme.MoonGlow
import com.example.weatherapp.ui.theme.StarLight
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
    val time = timeSource.longValue / 1000f

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

    val isDark = LocalIsDark.current
    
    val mainParticleColor = if (isDark) StarLight else Color(0xFFFFE59E) // Soft Gold for Day
    val lanternGlowColor = if (isDark) LanternOrange else Color(0xFFFFD54F) // Sunny Amber
    val crescentColor = if (isDark) MoonGlow else Color(0xFF6151C3) // Primary Purple in Light
    val glowColor = if (isDark) Gold else Color(0xFFFFC107) // Sun glow

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

    val skyGradientColors = remember(weatherType, isDark) {
        if (isDark) {
            listOf(Midnight, DeepNavy, DarkBlue)
        } else {
            when (weatherType.lowercase()) {
                "clear" -> listOf(Color(0xFF4FACFE), Color(0xFF00F2FE))
                "clouds" -> listOf(Color(0xFFBDC3C7), Color(0xFFEFF3F6))
                "rain", "thunderstorm", "drizzle", "thunder" -> listOf(Color(0xFF4B6CB7), Color(0xFF182848))
                "snow" -> listOf(Color(0xFFE6E9F0), Color(0xFFEEF1F5))
                "atmosphere" -> listOf(Color(0xFF90A4AE), Color(0xFFCFD8DC))
                else -> listOf(Color(0xFF4FACFE), Color(0xFF00F2FE))
            }
        }
    }
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        drawRect(
            brush = Brush.verticalGradient(
                colors = skyGradientColors
            ),
            size = size
        )

        val moonX = if (isRtl) 0.18f * width else 0.82f * width
        val moonCenter = Offset(moonX, 90.dp.toPx())

        particles.forEachIndexed { index, p ->
            val individualOffset = (time * p.speed + index * 0.07f)
            val shiftedY = (p.y + individualOffset) % 1f
            val yPos = shiftedY * height
            
            val xOffset = sin(time * 0.5f + p.x * 10f) * 10f
            val xPos = (p.x * width + xOffset + width) % width

            drawCircle(
                color = mainParticleColor.copy(alpha = p.alpha * pulse * (if (isDark) 1f else 0.6f)),
                radius = p.size,
                center = Offset(xPos, yPos)
            )

            if (p.size > 2.2f && isDark) {
                drawLine(
                    color = MoonGlow.copy(alpha = p.alpha * 0.3f * pulse),
                    start = Offset(xPos - p.size * 2f, yPos),
                    end = Offset(xPos + p.size * 2f, yPos),
                    strokeWidth = 0.5.dp.toPx()
                )
                drawLine(
                    color = MoonGlow.copy(alpha = p.alpha * 0.3f * pulse),
                    start = Offset(xPos, yPos - p.size * 2f),
                    end = Offset(xPos, yPos + p.size * 2f),
                    strokeWidth = 0.5.dp.toPx()
                )
            }
        }

        lanternParticles.forEachIndexed { index, p ->
            val floatTime = time * 0.3f + index * 0.5f
            val xOffset = sin(floatTime) * 40f
            val xPos = (p.x * width + xOffset + width) % width
            
            val yShift = (p.y - time * p.speed + 1000f) % 1f
            val yPos = yShift * height

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        lanternGlowColor.copy(alpha = p.alpha * pulse),
                        glowColor.copy(alpha = p.alpha * 0.3f * pulse),
                        Color.Transparent
                    ),
                    center = Offset(xPos, yPos),
                    radius = p.size * 10f
                ),
                radius = p.size * 10f,
                center = Offset(xPos, yPos)
            )
            drawCircle(
                color = (if (isDark) MoonGlow else Color.White).copy(alpha = p.alpha * pulse * 0.8f),
                radius = p.size * 0.8f,
                center = Offset(xPos, yPos)
            )
        }

        if (isDark) {
            if (isRtl) {
                withTransform({
                    scale(-1f, 1f, pivot = moonCenter)
                }) {
                    drawCrescentMoon(
                        center = moonCenter,
                        outerRadius = 40.dp.toPx(),
                        glowAlpha = moonGlow,
                        isRtl = isRtl,
                        isDark = isDark,
                        moonColor = crescentColor,
                        glowBaseColor = glowColor
                    )
                }
            } else {
                drawCrescentMoon(
                    center = moonCenter,
                    outerRadius = 40.dp.toPx(),
                    glowAlpha = moonGlow,
                    isRtl = isRtl,
                    isDark = isDark,
                    moonColor = crescentColor,
                    glowBaseColor = glowColor
                )
            }
        } else {
            drawSun(
                center = moonCenter,
                radius = 42.dp.toPx(),
                glowAlpha = pulse
            )
        }

        if (weatherType.lowercase() == "clear") {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        glowColor.copy(alpha = (if (isDark) 0.12f else 0.35f) * pulse),
                        Color.Transparent
                    ),
                    center = moonCenter,
                    radius = if (isDark) 200.dp.toPx() else 250.dp.toPx()
                ),
                radius = if (isDark) 200.dp.toPx() else 250.dp.toPx(),
                center = moonCenter
            )
        }
    }
}

private fun DrawScope.drawSun(
    center: Offset,
    radius: Float,
    glowAlpha: Float
) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFD54F).copy(alpha = 0.5f * glowAlpha),
                Color(0xFFFFC107).copy(alpha = 0.2f * glowAlpha),
                Color.Transparent
            ),
            center = center,
            radius = radius * 4f
        ),
        radius = radius * 4f,
        center = center
    )

    drawCircle(
        color = Color(0xFFFFC107),
        radius = radius,
        center = center
    )

    drawCircle(
        color = Color.White.copy(alpha = 0.4f),
        radius = radius * 0.7f,
        center = center
    )
}

private fun DrawScope.drawCrescentMoon(
    center: Offset,
    outerRadius: Float,
    glowAlpha: Float,
    isRtl: Boolean,
    isDark: Boolean,
    moonColor: Color,
    glowBaseColor: Color
) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                moonColor.copy(alpha = glowAlpha * (if (isDark) 1f else 0.4f)),
                glowBaseColor.copy(alpha = glowAlpha * 0.3f),
                Color.Transparent
            ),
            center = center,
            radius = outerRadius * 3f
        ),
        radius = outerRadius * 3f,
        center = center
    )

    drawCircle(
        color = moonColor.copy(alpha = if (isDark) 0.9f else 0.7f),
        radius = outerRadius,
        center = center
    )
    val xOffset = if (isRtl) -outerRadius * 0.35f else outerRadius * 0.35f
    val actualCutoutColor = if (isDark) DeepNavy else Color(0xFF4FACFE)
    drawCircle(
        color = actualCutoutColor, 
        radius = outerRadius * 0.78f,
        center = Offset(center.x + xOffset, center.y - outerRadius * 0.1f)
    )
}

