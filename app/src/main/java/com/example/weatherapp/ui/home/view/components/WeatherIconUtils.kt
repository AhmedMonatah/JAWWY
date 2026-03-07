package com.example.weatherapp.ui.home.view.components

import androidx.compose.ui.graphics.Color
import com.example.weatherapp.R

fun getWeatherIconRes(icon: String, description: String = ""): Int {
    val lowerIcon = icon.lowercase()
    return when {
        lowerIcon.startsWith("13") -> R.drawable.ic_snowy
        lowerIcon.startsWith("11") -> R.drawable.ic_rainy
        lowerIcon.startsWith("09") || lowerIcon.startsWith("10") -> R.drawable.ic_rainy
        lowerIcon.startsWith("02") || lowerIcon.startsWith("03") || lowerIcon.startsWith("04") -> R.drawable.ic_cloud
        lowerIcon.startsWith("50") -> R.drawable.ic_cloud
        // Fallback to description ONLY if English (though less reliable)
        description.lowercase().contains("rain") -> R.drawable.ic_rainy
        description.lowercase().contains("cloud") -> R.drawable.ic_cloud
        else -> R.drawable.ic_sunny
    }
}

fun getWeatherIconTint(icon: String, description: String = ""): Color {
    val lowerIcon = icon.lowercase()
    return when {
        lowerIcon.startsWith("13") -> Color(0xFFB0C4DE)
        lowerIcon.startsWith("11") -> Color(0xFF483D8B)
        lowerIcon.startsWith("09") || lowerIcon.startsWith("10") -> Color(0xFF4682B4)
        lowerIcon.startsWith("02") || lowerIcon.startsWith("03") || lowerIcon.startsWith("04") -> Color(0xFF87CEEB)
        lowerIcon.startsWith("50") -> Color(0xFF90A4AE)
        else -> Color(0xFFFFB300)
    }
}
