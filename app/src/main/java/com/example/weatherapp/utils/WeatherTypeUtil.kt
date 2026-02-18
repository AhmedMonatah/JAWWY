package com.example.weatherapp.utils

/**
 * Utility to determine weather type from description and icon code.
 * Shared across HomeScreen and MainScreen to avoid duplication.
 */
object WeatherTypeUtil {

    fun determineWeatherType(description: String?, icon: String?): String {
        val desc = (description ?: "").lowercase()
        val ic = icon ?: ""
        return when {
            desc.contains("snow") || ic.startsWith("13") -> "snow"
            desc.contains("rain") || desc.contains("drizzle") ||
                    ic.startsWith("09") || ic.startsWith("10") -> "rain"
            desc.contains("cloud") || ic.startsWith("02") ||
                    ic.startsWith("03") || ic.startsWith("04") -> "clouds"
            else -> "clear"
        }
    }
}
