package com.example.weatherapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weatherapp.data.local.dao.AlertDao
import com.example.weatherapp.data.local.dao.FavoriteDao
import com.example.weatherapp.data.local.dao.WeatherDao
import com.example.weatherapp.model.FavoriteLocation
import com.example.weatherapp.model.ForecastEntity
import com.example.weatherapp.model.WeatherEntity
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [WeatherEntity::class, ForecastEntity::class, FavoriteLocation::class, com.example.weatherapp.model.HourlyForecastEntity::class, com.example.weatherapp.model.Alert::class], version = 8, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun alertDao(): AlertDao
}
