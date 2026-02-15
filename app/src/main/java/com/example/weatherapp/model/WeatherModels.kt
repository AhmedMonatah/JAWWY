package com.example.weatherapp.model

data class WeatherState(
    val currentTemp: Int,
    val condition: String,
    val location: String,
    val description: String,
    val feelsLike: Int,
    val humidity: Int,
    val windSpeed: Int,
    val pressure: Int,
    val clouds: Int,
    val sunrise: String,
    val sunset: String
)

data class HourlyForecast(
    val time: String,
    val temp: Int,
    val icon: String
)

data class DailyForecast(
    val day: String,
    val tempHigh: Int,
    val tempLow: Int,
    val condition: String
)

data class Alert(
    val id: String,
    val type: String,
    val startTime: String,
    val endTime: String,
    val isNotification: Boolean
)

data class FavoriteLocation(
    val id: String,
    val name: String,
    val lat: Double,
    val lon: Double,
    val currentTemp: Int,
    val condition: String
)

object FakeData {
    val currentWeather = WeatherState(
        currentTemp = 20,
        condition = "Thunder",
        location = "Dhaka",
        description = "Softly coldy",
        feelsLike = 18,
        humidity = 60,
        windSpeed = 9,
        pressure = 29,
        clouds = 75,
        sunrise = "06:00 AM",
        sunset = "06:00 PM"
    )

    val hourlyForecast = listOf(
        HourlyForecast("10 AM", 21, "sunny"),
        HourlyForecast("11 AM", 22, "cloudy"),
        HourlyForecast("12 PM", 23, "rainy"),
        HourlyForecast("01 PM", 22, "cloudy"),
        HourlyForecast("02 PM", 21, "sunny")
    )

    val dailyForecast = listOf(
        DailyForecast("Mon", 25, 18, "Clear"),
        DailyForecast("Tue", 24, 17, "Cloudy"),
        DailyForecast("Wed", 22, 16, "Rain"),
        DailyForecast("Thu", 23, 17, "Thunder"),
        DailyForecast("Fri", 25, 18, "Clear")
    )

    val alerts = listOf(
        Alert("1", "Rain", "10:00 AM", "12:00 PM", true),
        Alert("2", "Wind", "02:00 PM", "04:00 PM", false)
    )

    val favorites = listOf(
        FavoriteLocation("1", "California", 36.77, -119.41, 6, "Clear"),
        FavoriteLocation("2", "Beijing", 39.90, 116.40, 5, "Mostly sunny"),
        FavoriteLocation("3", "Moscow", 55.75, 37.61, -4, "Cloudy")
    )
}
