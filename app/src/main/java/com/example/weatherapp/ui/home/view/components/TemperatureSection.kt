package com.example.weatherapp.ui.home.view.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.ui.theme.TextSecondary
import com.example.weatherapp.utils.getWeatherIconRes
import com.example.weatherapp.utils.getWeatherIconTint

@Composable
fun TemperatureSection(
    temp: Int,
    condition: String,
    date: String,
    time: String,
    textColor: Color = Color.White,
    weatherType: String = "clear"
) {
    val isDark = isSystemInDarkTheme()
    val iconRes = getWeatherIconRes(weatherType, condition)
    val iconTint = getWeatherIconTint(weatherType, condition)

    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = RoundedCornerShape(30.dp),
            color = if (isDark) Color.White.copy(alpha = 0.05f) else iconTint.copy(alpha = 0.1f)
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.padding(20.dp),
                tint = iconTint
            )
        }
        Spacer(Modifier.height(16.dp))
        Text("$temp°", fontSize = 100.sp, fontWeight = FontWeight.Light, color = textColor)
        Text(condition, style = MaterialTheme.typography.headlineSmall, color = textColor)
        Text("$date | $time", style = MaterialTheme.typography.bodyLarge, color = if (isDark) TextSecondary else Color.Gray)
    }
}
