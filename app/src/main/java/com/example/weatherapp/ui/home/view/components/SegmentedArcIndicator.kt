package com.example.weatherapp.ui.home.view.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

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
        val activeArcColor = MaterialTheme.colorScheme.primary
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 8f
            val dashCount = 40
            val startAngle = -90f
            val sweepAngle = 360f

            val radius = size.minDimension / 2
            val innerRadius = radius * 0.70f // Thinner ring
            val outerRadius = radius * 0.85f

            val center = this.center
            val angleStep = sweepAngle / dashCount

            repeat(dashCount) { index ->
                val angleInDegrees = startAngle + (index * angleStep)
                val angleInRad = Math.toRadians(angleInDegrees.toDouble())

                // Scale index to match progress since we're using 360 degrees now
                val isActive = (index.toFloat() / dashCount) <= animatedProgress

                val startX = center.x + (innerRadius * cos(angleInRad)).toFloat()
                val startY = center.y + (innerRadius * sin(angleInRad)).toFloat()

                val endX = center.x + (outerRadius * cos(angleInRad)).toFloat()
                val endY = center.y + (outerRadius * sin(angleInRad)).toFloat()

                drawLine(
                    color = if (isActive) activeArcColor else activeArcColor.copy(alpha = 0.08f), // Even more subtle inactive state
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }
        }
        val labelColor = MaterialTheme.colorScheme.onSurface
        val unitColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

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