package com.example.weatherapp.data.local


import com.example.weatherapp.data.model.Alert
import com.example.weatherapp.data.model.FavoriteLocation
import com.example.weatherapp.data.model.ForecastEntity
import com.example.weatherapp.data.model.HourlyForecastEntity
import com.example.weatherapp.data.model.WeatherEntity
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    fun getCurrentWeather(): Flow<WeatherEntity?>
    suspend fun insertCurrentWeather(weather: WeatherEntity)
    fun getForecast(): Flow<List<ForecastEntity>>
    suspend fun insertForecast(forecast: List<ForecastEntity>)
    suspend fun clearForecast()
    fun getHourlyForecast(): Flow<List<HourlyForecastEntity>>
    suspend fun insertHourlyForecast(hourly: List<HourlyForecastEntity>)
    suspend fun clearHourlyForecast()
    fun getFavorites(): Flow<List<FavoriteLocation>>
    suspend fun insertFavorite(favorite: FavoriteLocation)
    suspend fun deleteFavorite(favorite: FavoriteLocation)
    suspend fun getFavoriteByCoords(lat: Double, lon: Double): FavoriteLocation?
    fun getAllAlerts(): Flow<List<Alert>>
    suspend fun insertAlert(alert: Alert): Long
    suspend fun deleteAlert(alert: Alert)
    suspend fun getActiveAlerts(): List<Alert>
    suspend fun getAlertById(id: Int): Alert?
    suspend fun deleteAllAlerts()
    fun getUnits(): Flow<String>
    suspend fun setUnits(units: String)
    fun getLanguage(): Flow<String>
    suspend fun setLanguage(lang: String)
    fun getLocationMode(): Flow<String>
    suspend fun setLocationMode(mode: String)
    fun getThemeMode(): Flow<String>
    suspend fun setThemeMode(mode: String)
    fun getManualLocation(): Flow<Pair<Double, Double>?>
    suspend fun setManualLocation(lat: Double, lon: Double)
    fun getOnboardingShown(): Flow<Boolean>
    suspend fun setOnboardingShown()
}
