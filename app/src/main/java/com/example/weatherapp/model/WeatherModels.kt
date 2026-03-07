package com.example.weatherapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class CurrentWeatherResponse(
    val coord: Coord,
    val weather: List<Weather>,
    val main: Main,
    val wind: Wind,
    val dt: Long,
    val sys: Sys,
    val clouds: Clouds?,
    val name: String,
    val id: Int,
    val timezone: Int = 0
) : Parcelable

@Parcelize
data class HourlyForecastResponse(
    val list: List<HourlyItem>,
    val city: City
) : Parcelable

@Parcelize
data class DailyForecastResponse(
    val list: List<DailyItem>,
    val city: City
) : Parcelable

@Parcelize
data class GeocodingResponse(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String
) : Parcelable

@Parcelize
data class Coord(val lon: Double, val lat: Double) : Parcelable
@Parcelize
data class Weather(val id: Int, val main: String, val description: String, val icon: String) : Parcelable
@Parcelize
data class Main(val temp: Double, val feels_like: Double, val temp_min: Double, val temp_max: Double, val pressure: Int, val humidity: Int) : Parcelable
@Parcelize
data class Wind(val speed: Double, val deg: Int) : Parcelable
@Parcelize
data class Sys(val type: Int, val id: Int, val country: String, val sunrise: Long, val sunset: Long) : Parcelable
@Parcelize
data class City(val id: Int, val name: String, val coord: Coord, val country: String) : Parcelable

@Parcelize
data class Clouds(val all: Int) : Parcelable

@Parcelize
data class HourlyItem(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind,
    val clouds: Clouds?,
    val dt_txt: String
) : Parcelable

@Parcelize
data class DailyItem(
    val dt: Long,
    val temp: Temp,
    val pressure: Int,
    val humidity: Int,
    val speed: Double,
    val clouds: Int,
    val weather: List<Weather>
) : Parcelable

@Parcelize
data class Temp(val day: Double, val min: Double, val max: Double, val night: Double, val eve: Double, val morn: Double) : Parcelable
