package com.example.weatherapp.data.remote

import com.example.weatherapp.data.model.CurrentWeatherResponse
import com.example.weatherapp.data.model.DailyForecastResponse
import com.example.weatherapp.data.model.HourlyForecastResponse
import com.example.weatherapp.data.model.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String,
        @Query("lang") lang: String
    ): CurrentWeatherResponse

    @GET("data/2.5/forecast/daily")
    suspend fun getDailyForecast(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String,
        @Query("lang") lang: String,
        @Query("cnt") cnt: Int = 7 // Default to 7 days
    ): DailyForecastResponse
}
