package com.example.weatherapp.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "hourly_forecast")
data class HourlyForecastEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val dt: Long,
    val temp: Double,
    val description: String,
    val icon: String,
    val timestamp: Long
) : Parcelable
