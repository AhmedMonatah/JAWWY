package com.example.weatherapp.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "current_weather")
data class WeatherEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 1,
    val cityName: String,
    val temp: Double,
    val description: String,
    val icon: String,
    val lat: Double,
    val lon: Double,
    val timestamp: Long,
    val humidity: Int,
    val pressure: Int,
    val windSpeed: Double,
    val clouds: Int
) : Parcelable
