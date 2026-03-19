package com.example.weatherapp.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.weatherapp.data.local.LocalDataSource

import com.example.weatherapp.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.weatherapp.data.model.Alert
import com.example.weatherapp.data.model.FavoriteLocation
import com.example.weatherapp.data.model.WeatherEntity
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import com.example.weatherapp.data.model.ForecastEntity
import com.example.weatherapp.data.model.HourlyForecastEntity
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AppRepository(
    private val context: Context,
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : WeatherRepository {
    override fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override val connectivityFlow: Flow<Boolean> = callbackFlow {
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
    }

    override val onboardingShownFlow: Flow<Boolean> = localDataSource.getOnboardingShown()

    override suspend fun setOnboardingShown() = localDataSource.setOnboardingShown()

    override fun getAllAlerts(): Flow<List<Alert>> = localDataSource.getAllAlerts()
    override suspend fun insertAlert(alert: Alert): Long = localDataSource.insertAlert(alert)
    override suspend fun deleteAlert(alert: Alert) = localDataSource.deleteAlert(alert)

    override suspend fun deleteAllAlerts() = localDataSource.deleteAllAlerts()

    override val unitsFlow: Flow<String> = localDataSource.getUnits()
    override val languageFlow: Flow<String> = localDataSource.getLanguage()
    override val locationModeFlow: Flow<String> = localDataSource.getLocationMode()
    override val themeModeFlow: Flow<String> = localDataSource.getThemeMode()
    override val manualLocationFlow: Flow<Pair<Double, Double>?> = localDataSource.getManualLocation()

    override suspend fun setUnits(units: String) = localDataSource.setUnits(units)
    override suspend fun setLanguage(lang: String) = localDataSource.setLanguage(lang)
    override suspend fun setLocationMode(mode: String) = localDataSource.setLocationMode(mode)
    override suspend fun setThemeMode(mode: String) = localDataSource.setThemeMode(mode)
    override suspend fun setManualLocation(lat: Double, lon: Double) = localDataSource.setManualLocation(lat, lon)

    override fun getCurrentWeather(): Flow<WeatherEntity?> = localDataSource.getCurrentWeather()
    override fun getForecast(): Flow<List<ForecastEntity>> = localDataSource.getForecast()
    override fun getHourlyForecast(): Flow<List<HourlyForecastEntity>> = localDataSource.getHourlyForecast()

    override suspend fun fetchWeather(lat: Double, lon: Double, units: String, lang: String): WeatherEntity {
        val response = remoteDataSource.getCurrentWeather(lat, lon, units, lang)
        return WeatherEntity(
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
            clouds = response.clouds?.all ?: 0,
            countryCode = response.sys.country,
            timezoneOffset = response.timezone
        )
    }

    override suspend fun refreshCurrentWeather(lat: Double, lon: Double, units: String, lang: String): WeatherEntity {
        val weather = fetchWeather(lat, lon, units, lang)
        localDataSource.insertCurrentWeather(weather)
        return weather
    }

    override suspend fun refreshForecast(lat: Double, lon: Double, units: String, lang: String) {
        val response = remoteDataSource.getDailyForecast(lat, lon, units, lang, 7)
        val entities = response.list.map { item ->
            ForecastEntity(
                dt = item.dt,
                tempDay = item.temp.day,
                tempMin = item.temp.min,
                tempMax = item.temp.max,
                description = item.weather.firstOrNull()?.description ?: "",
                icon = item.weather.firstOrNull()?.icon ?: "",
                humidity = item.humidity,
                pressure = item.pressure,
                windSpeed = item.speed,
                clouds = item.clouds,
                timestamp = System.currentTimeMillis()
            )
        }
        localDataSource.clearForecast()
        localDataSource.insertForecast(entities)
    }

    override suspend fun refreshHourlyForecast(lat: Double, lon: Double, units: String, lang: String) {
        val response = remoteDataSource.getHourlyForecast(lat, lon, units, lang)
        val entities = response.list.map { item ->
            HourlyForecastEntity(
                dt = item.dt,
                temp = item.main.temp,
                description = item.weather.firstOrNull()?.description ?: "",
                icon = item.weather.firstOrNull()?.icon ?: "",
                timestamp = System.currentTimeMillis()
            )
        }
        localDataSource.clearHourlyForecast()
        localDataSource.insertHourlyForecast(entities)
    }

    override fun getFavorites(): Flow<List<FavoriteLocation>> = localDataSource.getFavorites()
    override suspend fun addFavorite(favorite: FavoriteLocation) = localDataSource.insertFavorite(favorite)
    override suspend fun removeFavorite(favorite: FavoriteLocation) = localDataSource.deleteFavorite(favorite)
    override suspend fun isFavorite(lat: Double, lon: Double): Boolean = localDataSource.getFavoriteByCoords(lat, lon) != null
}
