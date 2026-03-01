package com.example.weatherapp.ui.onboarding.view.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R

@Composable
fun GlobalWeatherVisual() {
    val infiniteTransition = rememberInfiniteTransition(label = "global_pulse")
    val primaryColor = MaterialTheme.colorScheme.primary
    
    Box(
        modifier = Modifier.size(240.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.raw.earth),
            contentDescription = "Earth",
            modifier = Modifier.size(200.dp)
        )

        val pulseScale by infiniteTransition.animateFloat(
            initialValue = 0.8f,
            targetValue = 1.3f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pin_pulse"
        )
        val pulseAlpha by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "pin_alpha"
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = 20.dp, y = (-30).dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(60.dp)) {
                drawCircle(
                    color = primaryColor.copy(alpha = pulseAlpha),
                    radius = (size.minDimension / 2f) * pulseScale * 1.2f,
                    style = Stroke(width = 4.dp.toPx())
                )
            }
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = "Favorite Location Pin",
                tint = primaryColor,
                modifier = Modifier.size(46.dp)
            )
        }
    }
}
