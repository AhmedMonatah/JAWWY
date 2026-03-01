package com.example.weatherapp.ui.splash.view.components

import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashContent(
    title: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        JawwyAnimatedLogo()

        Spacer(modifier = Modifier.height(48.dp))

        JawwyAnimatedName(title)
    }
}

@Composable
fun JawwyAnimatedLogo() {
    val infiniteTransition = rememberInfiniteTransition(label = "logo_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .size(160.dp)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f

            // 1. Draw Sun (Rotating)
            val sunRadius = 40.dp.toPx()
            val sunCenter = Offset(cx + 20.dp.toPx(), cy - 20.dp.toPx())
            
            rotate(rotation, pivot = sunCenter) {
                // Sun Core
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFFEE58), Color(0xFFF9A825)),
                        center = sunCenter,
                        radius = sunRadius
                    ),
                    radius = sunRadius,
                    center = sunCenter
                )

                // Sun Rays
                for (i in 0 until 8) {
                    val angle = i * 45f
                    rotate(angle, pivot = sunCenter) {
                        drawRoundRect(
                            color = Color(0xFFFFD54F),
                            topLeft = Offset(sunCenter.x - 3.dp.toPx(), sunCenter.y - sunRadius - 16.dp.toPx()),
                            size = Size(6.dp.toPx(), 16.dp.toPx()),
                            cornerRadius = CornerRadius(3.dp.toPx())
                        )
                    }
                }
            }

            // 2. Draw Sleek Cloud (Overlapping the sun)
            val cloudY = cy + 10.dp.toPx()
            
            // Cloud Base
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFE3F2FD), Color(0xFF90CAF9))
                ),
                topLeft = Offset(cx - 60.dp.toPx(), cloudY),
                size = Size(120.dp.toPx(), 40.dp.toPx()),
                cornerRadius = CornerRadius(20.dp.toPx())
            )

            // Cloud Puffs
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB)),
                    center = Offset(cx - 20.dp.toPx(), cloudY + 5.dp.toPx()),
                    radius = 30.dp.toPx()
                ),
                radius = 30.dp.toPx(),
                center = Offset(cx - 20.dp.toPx(), cloudY + 5.dp.toPx())
            )
            
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFBBDEFB), Color(0xFF90CAF9)),
                    center = Offset(cx + 25.dp.toPx(), cloudY + 15.dp.toPx()),
                    radius = 22.dp.toPx()
                ),
                radius = 22.dp.toPx(),
                center = Offset(cx + 25.dp.toPx(), cloudY + 15.dp.toPx())
            )
        }
    }
}

@Composable
fun JawwyAnimatedName(title: String) {
    // Check if the title is Arabic to avoid splitting letters
    val isArabic = title.any { it in '\u0600'..'\u06FF' }

    if (isArabic) {
        var visible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            delay(300)
            visible = true
        }

        val scale by animateFloatAsState(
            targetValue = if (visible) 1f else 0.5f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ), label = "arabic_scale"
        )
        val alpha by animateFloatAsState(
            targetValue = if (visible) 1f else 0f,
            animationSpec = tween(800), label = "arabic_alpha"
        )

        Text(
            text = title,
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .scale(scale)
                .alpha(alpha)
        )
    } else {
        Row(verticalAlignment = Alignment.CenterVertically) {
            title.forEachIndexed { index, char ->
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(key1 = index) {
                    delay(index * 150L) // Slightly longer delay for impact
                    visible = true
                }
                
                val scale by animateFloatAsState(
                    targetValue = if (visible) 1f else 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ), label = "char_scale"
                )

                val alpha by animateFloatAsState(
                    targetValue = if (visible) 1f else 0f,
                    animationSpec = tween(400), label = "char_alpha"
                )

                Text(
                    text = char.toString(),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .scale(scale)
                        .alpha(alpha)
                )
            }
        }
    }
}
