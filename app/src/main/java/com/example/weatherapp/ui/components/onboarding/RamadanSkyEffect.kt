package com.example.weatherapp.ui.components.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.random.Random


@Composable
fun RamadanSkyEffect() {
    val particles = remember { List(20) { Random.nextFloat() to Random.nextFloat() } }
    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { (x, y) ->
            drawCircle(
                color = Color.White.copy(alpha = 0.6f),
                radius = Random.nextFloat() * 3f + 1f,
                center = Offset(x * size.width, y * size.height)
            )
        }
    }
}
