package com.example.weatherapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weatherapp.data.local.dao.AlertDao
import com.example.weatherapp.data.local.dao.FavoriteDao
import com.example.weatherapp.data.local.dao.WeatherDao
import com.example.weatherapp.data.local.entity.FavoriteLocation
import com.example.weatherapp.data.local.entity.ForecastEntity
import com.example.weatherapp.data.local.entity.WeatherEntity

@Database(entities = [WeatherEntity::class, ForecastEntity::class, FavoriteLocation::class, com.example.weatherapp.data.local.entity.HourlyForecastEntity::class, com.example.weatherapp.data.local.entity.Alert::class], version = 5, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun alertDao(): AlertDao
}
