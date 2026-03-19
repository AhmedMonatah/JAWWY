package com.example.weatherapp.data.repository

import com.example.weatherapp.data.model.Alert
import com.example.weatherapp.data.model.FavoriteLocation
import com.example.weatherapp.data.model.ForecastEntity
import com.example.weatherapp.data.model.HourlyForecastEntity
import com.example.weatherapp.data.model.WeatherEntity
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun isOnline(): Boolean
    val connectivityFlow: Flow<Boolean>
    val onboardingShownFlow: Flow<Boolean>
    suspend fun setOnboardingShown()
    
    fun getAllAlerts(): Flow<List<Alert>>
    suspend fun insertAlert(alert: Alert): Long
    suspend fun deleteAlert(alert: Alert)
    suspend fun deleteAllAlerts()
    
    val unitsFlow: Flow<String>
    val languageFlow: Flow<String>
    val locationModeFlow: Flow<String>
    val themeModeFlow: Flow<String>
    val manualLocationFlow: Flow<Pair<Double, Double>?>
    
    suspend fun setUnits(units: String)
    suspend fun setLanguage(lang: String)
    suspend fun setLocationMode(mode: String)
    suspend fun setThemeMode(mode: String)
    suspend fun setManualLocation(lat: Double, lon: Double)
    
    fun getCurrentWeather(): Flow<WeatherEntity?>
    fun getForecast(): Flow<List<ForecastEntity>>
    fun getHourlyForecast(): Flow<List<HourlyForecastEntity>>
    
    suspend fun fetchWeather(lat: Double, lon: Double, units: String, lang: String): WeatherEntity
    suspend fun refreshCurrentWeather(lat: Double, lon: Double, units: String, lang: String): WeatherEntity
    suspend fun refreshForecast(lat: Double, lon: Double, units: String, lang: String)
    suspend fun refreshHourlyForecast(lat: Double, lon: Double, units: String, lang: String)
    
    fun getFavorites(): Flow<List<FavoriteLocation>>
    suspend fun addFavorite(favorite: FavoriteLocation)
    suspend fun removeFavorite(favorite: FavoriteLocation)
    suspend fun isFavorite(lat: Double, lon: Double): Boolean
}
