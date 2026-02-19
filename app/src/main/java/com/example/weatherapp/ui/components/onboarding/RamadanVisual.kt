package com.example.weatherapp.ui.components.onboarding

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun RamadanVisual() {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val floatY by infiniteTransition.animateFloat(
        initialValue = -15f, targetValue = 15f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = ""
    )

    Box(modifier = Modifier.graphicsLayer { translationY = floatY }) {
        Text(text = "🌙", fontSize = 120.sp)
        Text(text = "🏮", fontSize = 60.sp, modifier = Modifier.align(Alignment.BottomEnd).offset(x = (-20).dp))
    }
}

