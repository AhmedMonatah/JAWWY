package com.example.weatherapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.data.local.entity.ForecastEntity
import com.example.weatherapp.data.local.entity.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Query("SELECT * FROM current_weather LIMIT 1")
    fun getCurrentWeather(): Flow<WeatherEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(weather: WeatherEntity)

    @Query("SELECT * FROM forecast ORDER BY dt ASC")
    fun getForecast(): Flow<List<ForecastEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(forecast: List<ForecastEntity>)

    @Query("DELETE FROM forecast")
    suspend fun clearForecast()
}
