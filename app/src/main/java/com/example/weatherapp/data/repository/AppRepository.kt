package com.example.weatherapp.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.local.entity.FavoriteLocation
import com.example.weatherapp.data.local.entity.ForecastEntity
import com.example.weatherapp.data.local.entity.HourlyForecastEntity
import com.example.weatherapp.data.local.entity.WeatherEntity
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.utils.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class AppRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: WeatherApi,
    private val db: WeatherDatabase
) {
    private val dao = db.weatherDao()
    private val favoriteDao = db.favoriteDao()

    private val UNITS_KEY = stringPreferencesKey("units")
    private val LANG_KEY = stringPreferencesKey("lang")
    private val LOCATION_MODE_KEY = stringPreferencesKey("location_mode") // "gps" or "map"
    private val MANUAL_LAT_KEY = stringPreferencesKey("manual_lat")
    private val MANUAL_LON_KEY = stringPreferencesKey("manual_lon")
    private val DARK_MODE_KEY = stringPreferencesKey("dark_mode") // "dark", "light", or "system"

    // --- Settings Logic ---
    val darkModeFlow: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[DARK_MODE_KEY] ?: "system" }

    suspend fun setDarkMode(mode: String) {
        context.dataStore.edit { preferences -> preferences[DARK_MODE_KEY] = mode }
    }
    val unitsFlow: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[UNITS_KEY] ?: "metric" }

    val languageFlow: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[LANG_KEY] ?: "en" }

    val locationModeFlow: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[LOCATION_MODE_KEY] ?: "gps" }

    val manualLocationFlow: Flow<Pair<Double, Double>?> = context.dataStore.data
        .map { preferences -> 
            val lat = preferences[MANUAL_LAT_KEY]?.toDoubleOrNull()
            val lon = preferences[MANUAL_LON_KEY]?.toDoubleOrNull()
            if (lat != null && lon != null) lat to lon else null
        }

    suspend fun setUnits(units: String) {
        context.dataStore.edit { preferences -> preferences[UNITS_KEY] = units }
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { preferences -> preferences[LANG_KEY] = lang }
    }

    suspend fun setLocationMode(mode: String) {
        context.dataStore.edit { preferences -> preferences[LOCATION_MODE_KEY] = mode }
    }

    suspend fun setManualLocation(lat: Double, lon: Double) {
        context.dataStore.edit { preferences -> 
            preferences[MANUAL_LAT_KEY] = lat.toString()
            preferences[MANUAL_LON_KEY] = lon.toString()
        }
    }

    // --- Weather Logic ---

    // Single Source of Truth: Database
    fun getCurrentWeather(): Flow<WeatherEntity?> = dao.getCurrentWeather()

    fun getForecast(): Flow<List<ForecastEntity>> = dao.getForecast()

    suspend fun fetchWeather(lat: Double, lon: Double, apiKey: String, units: String, lang: String): Resource<WeatherEntity> {
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
                windSpeed = response.wind.speed,
                clouds = response.clouds?.all ?: 0
            )
            Resource.Success(entity)
        } catch (e: Exception) {
            Log.e("AppRepo", "Error fetching weather", e)
            Resource.Error(e.message ?: "Unknown Error")
        }
    }

    suspend fun refreshCurrentWeather(lat: Double, lon: Double, apiKey: String, units: String, lang: String): Resource<WeatherEntity> {
        val result = fetchWeather(lat, lon, apiKey, units, lang)
        if (result is Resource.Success && result.data != null) {
            dao.insertCurrentWeather(result.data)
        }
        return result
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

    suspend fun refreshHourlyForecast(lat: Double, lon: Double, apiKey: String, units: String, lang: String): Resource<Unit> {
        return try {
            val response = api.getHourlyForecast(lat, lon, apiKey, units, lang)
            val entities = response.list.map { item ->
                HourlyForecastEntity(
                    dt = item.dt,
                    temp = item.main.temp,
                    description = item.weather.firstOrNull()?.description ?: "",
                    icon = item.weather.firstOrNull()?.icon ?: "",
                    timestamp = System.currentTimeMillis()
                )
            }
            dao.clearHourlyForecast()
            dao.insertHourlyForecast(entities)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e("AppRepo", "Error fetching hourly", e)
            Resource.Error(e.message ?: "Unknown Error")
        }
    }

    fun getFavorites(): Flow<List<FavoriteLocation>> = favoriteDao.getAllFavorites()

    suspend fun addFavorite(favorite: FavoriteLocation) {
        favoriteDao.insertFavorite(favorite)
    }

    suspend fun removeFavorite(favorite: FavoriteLocation) {
        favoriteDao.deleteFavorite(favorite)
    }
    fun getHourlyForecast(): Flow<List<HourlyForecastEntity>> {


    return  dao.getHourlyForecast()
    }
    suspend fun isFavorite(lat: Double, lon: Double): Boolean {
        return favoriteDao.getFavoriteByCoords(lat, lon) != null
    }
}
