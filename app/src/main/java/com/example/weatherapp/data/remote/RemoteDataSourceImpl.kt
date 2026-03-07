package com.example.weatherapp.data.remote

import com.example.weatherapp.model.CurrentWeatherResponse
import com.example.weatherapp.model.DailyForecastResponse
import com.example.weatherapp.model.HourlyForecastResponse

class RemoteDataSourceImpl(
    private val api: WeatherApi
) : RemoteDataSource {
    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): CurrentWeatherResponse = api.getCurrentWeather(lat, lon, units, lang)

    override suspend fun getDailyForecast(
        lat: Double,
        lon: Double,
        units: String,
        lang: String,
        cnt: Int
    ): DailyForecastResponse = api.getDailyForecast(lat, lon, units, lang, cnt)

    override suspend fun getHourlyForecast(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): HourlyForecastResponse = api.getHourlyForecast(lat, lon, units, lang)
}
