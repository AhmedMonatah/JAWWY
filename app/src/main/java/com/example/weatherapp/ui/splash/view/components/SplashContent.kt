package com.example.weatherapp.ui.splash.view.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.ui.theme.RamadanGold

@Composable
fun SplashContent(
    scale: Float,
    alpha: Float,
    textAlpha: Animatable<Float, *>,
    textOffset: Animatable<Float, *>,
    title: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.WbSunny,
            contentDescription = null,
            tint = RamadanGold,
            modifier = Modifier
                .size(125.dp)
                .scale(scale)
                .alpha(alpha)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            ),
            color = Color.White.copy(alpha = textAlpha.value),
            modifier = Modifier.offset(y = textOffset.value.dp)
        )
    }
}
