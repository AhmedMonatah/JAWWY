package com.example.weatherapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weatherapp.data.local.dao.AlertDao
import com.example.weatherapp.data.local.dao.FavoriteDao
import com.example.weatherapp.data.local.dao.WeatherDao
import com.example.weatherapp.data.model.Alert
import com.example.weatherapp.data.model.FavoriteLocation
import com.example.weatherapp.data.model.ForecastEntity
import com.example.weatherapp.data.model.HourlyForecastEntity
import com.example.weatherapp.data.model.WeatherEntity

@Database(entities = [WeatherEntity::class, ForecastEntity::class, FavoriteLocation::class, HourlyForecastEntity::class, Alert::class], version = 8, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun alertDao(): AlertDao
}
