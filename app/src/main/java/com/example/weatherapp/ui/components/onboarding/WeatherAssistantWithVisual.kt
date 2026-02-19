package com.example.weatherapp.ui.components.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapp.model.PageType
import com.example.weatherapp.ui.theme.RamadanGold


@Composable
fun WeatherAssistantWithVisual(type: PageType) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        // Primary Weather Icon (Larger)
        Box(modifier = Modifier.offset(y = (-40).dp)) {
            when (type) {
                PageType.RAMADAN_NIGHT -> RamadanVisual()
                PageType.WEATHER_DAY -> WeatherVisual()
                PageType.ALERTS -> AlertsVisual()
            }
        }



        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 40.dp)
                .size(100.dp, 4.dp)
                .background(RamadanGold.copy(alpha = 0.3f), CircleShape)
        )
    }
}
