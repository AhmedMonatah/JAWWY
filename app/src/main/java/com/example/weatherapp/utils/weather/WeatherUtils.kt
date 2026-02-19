package com.example.weatherapp.utils.weather

import androidx.compose.ui.graphics.Color
import com.example.weatherapp.R

fun getWeatherIconRes(icon: String, description: String = ""): Int {
    val lowerDesc = description.lowercase()
    return when {
        lowerDesc.contains("snow") || icon.startsWith("13") -> R.drawable.ic_snowy
        lowerDesc.contains("rain") || lowerDesc.contains("drizzle") || icon.startsWith("09") || icon.startsWith("10") -> R.drawable.ic_rainy
        lowerDesc.contains("thunder") || icon.startsWith("11") -> R.drawable.ic_rainy 
        lowerDesc.contains("cloud") || icon.startsWith("02") || icon.startsWith("03") || icon.startsWith("04") -> R.drawable.ic_cloud
        else -> R.drawable.ic_sunny
    }
}

fun getWeatherIconTint(icon: String, description: String = ""): Color {
    val lowerDesc = description.lowercase()
    return when {
        lowerDesc.contains("snow") || icon.startsWith("13") -> Color(0xFFB0C4DE)
        lowerDesc.contains("rain") || lowerDesc.contains("drizzle") || icon.startsWith("09") || icon.startsWith("10") -> Color(0xFF4682B4)
        lowerDesc.contains("thunder") || icon.startsWith("11") -> Color(0xFF483D8B)
        lowerDesc.contains("cloud") || icon.startsWith("02") || icon.startsWith("03") || icon.startsWith("04") -> Color(0xFF87CEEB)
        else -> Color(0xFFFFD700)
    }
}
