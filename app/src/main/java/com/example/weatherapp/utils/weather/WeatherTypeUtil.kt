package com.example.weatherapp.utils.weather


object WeatherTypeUtil {

    fun determineWeatherType(description: String?, icon: String?): String {
        val desc = (description ?: "").lowercase()
        val ic = icon ?: ""
        return when {
            desc.contains("snow") || ic.startsWith("13") -> "snow"
            desc.contains("thunder") || ic.startsWith("11") -> "thunder"
            desc.contains("rain") || desc.contains("drizzle") ||
                    ic.startsWith("09") || ic.startsWith("10") -> "rain"
            desc.contains("cloud") || ic.startsWith("02") ||
                    ic.startsWith("03") || ic.startsWith("04") -> "clouds"
            desc.contains("mist") || desc.contains("fog") || desc.contains("haze") ||
                    ic.startsWith("50") -> "atmosphere"
            else -> "clear"
        }
    }
}
