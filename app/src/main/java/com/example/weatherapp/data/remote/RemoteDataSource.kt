package com.example.weatherapp.data.remote


import com.example.weatherapp.data.model.CurrentWeatherResponse
import com.example.weatherapp.data.model.DailyForecastResponse
import com.example.weatherapp.data.model.HourlyForecastResponse

interface RemoteDataSource {
    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): CurrentWeatherResponse

    suspend fun getDailyForecast(
        lat: Double,
        lon: Double,
        units: String,
        lang: String,
        cnt: Int = 7
    ): DailyForecastResponse

    suspend fun getHourlyForecast(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): HourlyForecastResponse
}
