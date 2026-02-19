package com.example.weatherapp.data.remote

import com.example.weatherapp.model.CurrentWeatherResponse
import com.example.weatherapp.model.DailyForecastResponse
import com.example.weatherapp.model.HourlyForecastResponse

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
