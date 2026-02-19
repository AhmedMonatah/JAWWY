package com.example.weatherapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.weatherapp.model.Alert
import com.example.weatherapp.model.FavoriteLocation
import com.example.weatherapp.model.ForecastEntity
import com.example.weatherapp.model.HourlyForecastEntity
import com.example.weatherapp.model.WeatherEntity
import com.example.weatherapp.data.repository.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val db: WeatherDatabase
) : LocalDataSource {
    private val weatherDao = db.weatherDao()
    private val favoriteDao = db.favoriteDao()
    private val alertDao = db.alertDao()

    private val UNITS_KEY = stringPreferencesKey("units")
    private val LANG_KEY = stringPreferencesKey("lang")
    private val LOCATION_MODE_KEY = stringPreferencesKey("location_mode")
    private val MANUAL_LAT_KEY = stringPreferencesKey("manual_lat")
    private val MANUAL_LON_KEY = stringPreferencesKey("manual_lon")
    private val ONBOARDING_KEY = booleanPreferencesKey("onboarding_shown")

    // Weather
    override fun getCurrentWeather(): Flow<WeatherEntity?> = weatherDao.getCurrentWeather()
    override suspend fun insertCurrentWeather(weather: WeatherEntity) = weatherDao.insertCurrentWeather(weather)
    override fun getForecast(): Flow<List<ForecastEntity>> = weatherDao.getForecast()
    override suspend fun insertForecast(forecast: List<ForecastEntity>) = weatherDao.insertForecast(forecast)
    override suspend fun clearForecast() = weatherDao.clearForecast()
    override fun getHourlyForecast(): Flow<List<HourlyForecastEntity>> = weatherDao.getHourlyForecast()
    override suspend fun insertHourlyForecast(hourly: List<HourlyForecastEntity>) = weatherDao.insertHourlyForecast(hourly)
    override suspend fun clearHourlyForecast() = weatherDao.clearHourlyForecast()

    // Favorites
    override fun getFavorites(): Flow<List<FavoriteLocation>> = favoriteDao.getAllFavorites()
    override suspend fun insertFavorite(favorite: FavoriteLocation) = favoriteDao.insertFavorite(favorite)
    override suspend fun deleteFavorite(favorite: FavoriteLocation) = favoriteDao.deleteFavorite(favorite)
    override suspend fun getFavoriteByCoords(lat: Double, lon: Double): FavoriteLocation? = favoriteDao.getFavoriteByCoords(lat, lon)

    // Alerts
    override fun getAllAlerts(): Flow<List<Alert>> = alertDao.getAllAlerts()
    override suspend fun insertAlert(alert: Alert): Long = alertDao.insertAlert(alert)
    override suspend fun deleteAlert(alert: Alert) = alertDao.deleteAlert(alert)
    override suspend fun getActiveAlerts(): List<Alert> = alertDao.getActiveAlerts()
    override suspend fun getAlertById(id: Int): Alert? = alertDao.getAlertById(id)
    override suspend fun deleteAllAlerts() = alertDao.deleteAllAlerts()

    // Settings (DataStore)
    override fun getUnits(): Flow<String> = context.dataStore.data.map { it[UNITS_KEY] ?: "metric" }
    override suspend fun setUnits(units: String) { context.dataStore.edit { it[UNITS_KEY] = units } }
    override fun getLanguage(): Flow<String> = context.dataStore.data.map { it[LANG_KEY] ?: "en" }
    override suspend fun setLanguage(lang: String) { context.dataStore.edit { it[LANG_KEY] = lang } }
    override fun getLocationMode(): Flow<String> = context.dataStore.data.map { it[LOCATION_MODE_KEY] ?: "gps" }
    override suspend fun setLocationMode(mode: String) { context.dataStore.edit { it[LOCATION_MODE_KEY] = mode } }

    override fun getManualLocation(): Flow<Pair<Double, Double>?> = context.dataStore.data.map { preferences ->
        val lat = preferences[MANUAL_LAT_KEY]?.toDoubleOrNull()
        val lon = preferences[MANUAL_LON_KEY]?.toDoubleOrNull()
        if (lat != null && lon != null) lat to lon else null
    }

    override suspend fun setManualLocation(lat: Double, lon: Double) {
        context.dataStore.edit { preferences ->
            preferences[MANUAL_LAT_KEY] = lat.toString()
            preferences[MANUAL_LON_KEY] = lon.toString()
        }
    }

    override fun getOnboardingShown(): Flow<Boolean> = context.dataStore.data.map { it[ONBOARDING_KEY] ?: false }
    override suspend fun setOnboardingShown() { context.dataStore.edit { it[ONBOARDING_KEY] = true } }
}
