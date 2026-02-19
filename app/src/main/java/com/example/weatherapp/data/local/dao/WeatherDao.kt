package com.example.weatherapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.model.ForecastEntity
import com.example.weatherapp.model.HourlyForecastEntity
import com.example.weatherapp.model.WeatherEntity
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

    @Query("SELECT * FROM hourly_forecast ORDER BY dt ASC")
    fun getHourlyForecast(): Flow<List<com.example.weatherapp.model.HourlyForecastEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHourlyForecast(hourly: List<com.example.weatherapp.model.HourlyForecastEntity>)

    @Query("DELETE FROM hourly_forecast")
    suspend fun clearHourlyForecast()
}
