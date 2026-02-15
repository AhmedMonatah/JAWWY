package com.example.weatherapp.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "forecast")
data class ForecastEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val dt: Long,
    val tempDay: Double,
    val tempMin: Double,
    val tempMax: Double,
    val description: String,
    val icon: String,
    val timestamp: Long
) : Parcelable
