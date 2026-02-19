package com.example.weatherapp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.weatherapp.ui.theme.AccentPurple
import com.example.weatherapp.ui.theme.TextSecondary

@Composable
fun SegmentedArcIndicator(
    progress: Float,
    label: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(1000),
        label = ""
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 8f
            val dashCount = 20
            val startAngle = 135f
            val sweepAngle = 270f

            val radius = size.minDimension / 2
            val innerRadius = radius * 0.65f
            val outerRadius = radius * 0.85f

            val center = this.center
            val angleStep = sweepAngle / dashCount

            repeat(dashCount) { index ->
                val angleInDegrees = startAngle + (index * angleStep)
                val angleInRad = Math.toRadians(angleInDegrees.toDouble())

                val isActive = index < (dashCount * animatedProgress)

                val startX = center.x + (innerRadius * kotlin.math.cos(angleInRad)).toFloat()
                val startY = center.y + (innerRadius * kotlin.math.sin(angleInRad)).toFloat()

                val endX = center.x + (outerRadius * kotlin.math.cos(angleInRad)).toFloat()
                val endY = center.y + (outerRadius * kotlin.math.sin(angleInRad)).toFloat()

                drawLine(
                    color = if (isActive) AccentPurple else AccentPurple.copy(alpha = 0.2f),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }
        }
        val isDark = isSystemInDarkTheme()
        val labelColor = if (isDark) Color.White else Color.Black
        val unitColor = if (isDark) TextSecondary else Color.Gray

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = labelColor
            )
            Text(
                text = unit,
                fontSize = 14.sp,
                color = unitColor
            )
        }
    }
}