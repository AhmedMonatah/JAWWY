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
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.weatherapp.data.local.entity.Alert
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class AppRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: WeatherApi,
    private val db: WeatherDatabase
) {
    private val dao = db.weatherDao()
    private val favoriteDao = db.favoriteDao()
    private val alertDao = db.alertDao()

    fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    val connectivityFlow: Flow<Boolean> = kotlinx.coroutines.flow.callbackFlow {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: android.net.Network) { trySend(true) }
            override fun onLost(network: android.net.Network) { trySend(false) }
        }
        val request = android.net.NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, callback)
        trySend(isOnline())
        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }.stateIn(
        scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO),
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
        initialValue = isOnline()
    )

    private val UNITS_KEY = stringPreferencesKey("units")
    private val LANG_KEY = stringPreferencesKey("lang")
    private val LOCATION_MODE_KEY = stringPreferencesKey("location_mode")
    private val MANUAL_LAT_KEY = stringPreferencesKey("manual_lat")
    private val MANUAL_LON_KEY = stringPreferencesKey("manual_lon")
    private val ONBOARDING_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("onboarding_shown")
    
    val onboardingShownFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[ONBOARDING_KEY] ?: false }

    suspend fun setOnboardingShown() {
        context.dataStore.edit { preferences -> preferences[ONBOARDING_KEY] = true }
    }

    fun getAllAlerts(): Flow<List<Alert>> = alertDao.getAllAlerts()
    suspend fun insertAlert(alert: Alert): Long = alertDao.insertAlert(alert)
    suspend fun deleteAlert(alert: Alert) = alertDao.deleteAlert(alert)
    suspend fun getActiveAlerts(): List<Alert> = alertDao.getActiveAlerts()
    suspend fun getAlertById(id: Int): Alert? = alertDao.getAlertById(id)
    suspend fun deleteAllAlerts() = alertDao.deleteAllAlerts()

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

    suspend fun refreshForecast(lat: Double, lon: Double, apiKey: String, units: String, lang: String): Resource<Unit> {
        return try {
            val response = api.getDailyForecast(lat, lon, apiKey, units, lang, 7)
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
