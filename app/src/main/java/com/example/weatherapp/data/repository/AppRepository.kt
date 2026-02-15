package com.example.weatherapp.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.weatherapp.data.local.dao.WeatherDao
import com.example.weatherapp.data.local.entity.ForecastEntity
import com.example.weatherapp.data.local.entity.WeatherEntity
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.utils.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Centralized DataStore extension
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class AppRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: WeatherApi,
    private val dao: WeatherDao
) {

    private val UNITS_KEY = stringPreferencesKey("units")
    private val LANG_KEY = stringPreferencesKey("lang")

    // --- Settings Logic ---
    val unitsFlow: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[UNITS_KEY] ?: "metric" }

    val languageFlow: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[LANG_KEY] ?: "en" }

    suspend fun setUnits(units: String) {
        context.dataStore.edit { preferences -> preferences[UNITS_KEY] = units }
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { preferences -> preferences[LANG_KEY] = lang }
    }

    // --- Weather Logic ---

    // Single Source of Truth: Database
    fun getCurrentWeather(): Flow<WeatherEntity?> = dao.getCurrentWeather()

    fun getForecast(): Flow<List<ForecastEntity>> = dao.getForecast()

    suspend fun refreshCurrentWeather(lat: Double, lon: Double, apiKey: String, units: String, lang: String): Resource<WeatherEntity> {
        return try {
            val response = api.getCurrentWeather(lat, lon, apiKey, units, lang)
            val entity = WeatherEntity(
                cityName = response.name,
                temp = response.main.temp,
                description = response.weather.firstOrNull()?.description ?: "",
                icon = response.weather.firstOrNull()?.icon ?: "",
                lat = response.coord.lat,
                lon = response.coord.lon,
                timestamp = System.currentTimeMillis(),
                humidity = response.main.humidity,
                pressure = response.main.pressure,
                windSpeed = response.wind.speed
            )
            dao.insertCurrentWeather(entity)
            Resource.Success(entity)
        } catch (e: Exception) {
            Log.e("AppRepo", "Error fetching current weather", e)
            Resource.Error(e.message ?: "Unknown Error")
        }
    }

    suspend fun refreshForecast(city: String, apiKey: String, units: String, lang: String): Resource<Unit> {
        return try {
            val response = api.getDailyForecast(city, apiKey, units, lang, 7)
            val entities = response.list.map { item ->
                ForecastEntity(
                    dt = item.dt,
                    tempDay = item.temp.day,
                    tempMin = item.temp.min,
                    tempMax = item.temp.max,
                    description = item.weather.firstOrNull()?.description ?: "",
                    icon = item.weather.firstOrNull()?.icon ?: "",
                    timestamp = System.currentTimeMillis()
                )
            }
            dao.clearForecast()
            dao.insertForecast(entities)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e("AppRepo", "Error fetching forecast", e)
            Resource.Error(e.message ?: "Unknown Error")
        }
    }
}
