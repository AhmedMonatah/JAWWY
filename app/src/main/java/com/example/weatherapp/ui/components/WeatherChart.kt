package com.example.weatherapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.weatherapp.ui.theme.AccentBlue
import com.example.weatherapp.ui.theme.AccentPurple

@Composable
fun WeatherChart(data: List<Int>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        if (data.isEmpty()) return@Canvas

        val spacing = size.width / (data.size - 1)
        val maxTemp = data.maxOrNull() ?: 1
        val minTemp = data.minOrNull() ?: 0
        val tempRange = (maxTemp - minTemp).coerceAtLeast(1)
        
        val points = data.mapIndexed { index, temp ->
            Offset(
                x = index * spacing,
                y = size.height - ((temp - minTemp).toFloat() / tempRange * size.height)
            )
        }

        val path = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                val p1 = points[i - 1]
                val p2 = points[i]
                val controlPoint1 = Offset(p1.x + (p2.x - p1.x) / 2, p1.y)
                val controlPoint2 = Offset(p1.x + (p2.x - p1.x) / 2, p2.y)
                cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p2.x, p2.y)
            }
        }

        drawPath(
            path = path,
            brush = Brush.horizontalGradient(listOf(AccentPurple, AccentBlue)),
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        val fillPath = path.apply {
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(AccentPurple.copy(alpha = 0.3f), Color.Transparent)
            )
        )
    }
}
