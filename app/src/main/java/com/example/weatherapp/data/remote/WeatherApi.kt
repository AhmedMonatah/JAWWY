package com.example.weatherapp.data.remote

import com.example.weatherapp.model.CurrentWeatherResponse
import com.example.weatherapp.model.DailyForecastResponse
import com.example.weatherapp.model.HourlyForecastResponse
import com.example.weatherapp.model.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("lang") lang: String
    ): CurrentWeatherResponse

    @GET("data/2.5/forecast/daily")
    suspend fun getDailyForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("lang") lang: String,
        @Query("cnt") cnt: Int = 7
    ): DailyForecastResponse

    @GET("data/2.5/forecast")
    suspend fun getHourlyForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("lang") lang: String
    ): HourlyForecastResponse
}
