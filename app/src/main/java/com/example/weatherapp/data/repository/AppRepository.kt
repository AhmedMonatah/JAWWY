package com.example.weatherapp.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.weatherapp.data.local.LocalDataSource
import com.example.weatherapp.model.FavoriteLocation
import com.example.weatherapp.model.ForecastEntity
import com.example.weatherapp.model.HourlyForecastEntity
import com.example.weatherapp.model.WeatherEntity
import com.example.weatherapp.data.remote.RemoteDataSource
import com.example.weatherapp.utils.state.Resource
import kotlinx.coroutines.flow.Flow
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.weatherapp.model.Alert
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import com.example.weatherapp.BuildConfig

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
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
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
    }.stateIn(
        scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO),
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
        initialValue = isOnline()
    )

    override val onboardingShownFlow: Flow<Boolean> = localDataSource.getOnboardingShown()

    override suspend fun setOnboardingShown() = localDataSource.setOnboardingShown()

    override fun getAllAlerts(): Flow<List<Alert>> = localDataSource.getAllAlerts()
    override suspend fun insertAlert(alert: Alert): Long = localDataSource.insertAlert(alert)
    override suspend fun deleteAlert(alert: Alert) = localDataSource.deleteAlert(alert)

    override suspend fun deleteAllAlerts() = localDataSource.deleteAllAlerts()

    override val unitsFlow: Flow<String> = localDataSource.getUnits()
    override val languageFlow: Flow<String> = localDataSource.getLanguage()
    override val locationModeFlow: Flow<String> = localDataSource.getLocationMode()
    override val manualLocationFlow: Flow<Pair<Double, Double>?> = localDataSource.getManualLocation()

    override suspend fun setUnits(units: String) = localDataSource.setUnits(units)
    override suspend fun setLanguage(lang: String) = localDataSource.setLanguage(lang)
    override suspend fun setLocationMode(mode: String) = localDataSource.setLocationMode(mode)
    override suspend fun setManualLocation(lat: Double, lon: Double) = localDataSource.setManualLocation(lat, lon)

    override fun getCurrentWeather(): Flow<WeatherEntity?> = localDataSource.getCurrentWeather()
    override fun getForecast(): Flow<List<ForecastEntity>> = localDataSource.getForecast()
    override fun getHourlyForecast(): Flow<List<HourlyForecastEntity>> = localDataSource.getHourlyForecast()

    override suspend fun fetchWeather(lat: Double, lon: Double, units: String, lang: String): Resource<WeatherEntity> {
        return try {
            val response = remoteDataSource.getCurrentWeather(lat, lon, units, lang, BuildConfig.API_KEY)
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
                clouds = response.clouds?.all ?: 0,
                countryCode = response.sys.country,
                timezoneOffset = response.timezone
            )
            Resource.Success(entity)
        } catch (e: Exception) {
            Log.e("AppRepo", "Error fetching weather", e)
            Resource.Error(e.message ?: "Unknown Error")
        }
    }

    override suspend fun refreshCurrentWeather(lat: Double, lon: Double, units: String, lang: String): Resource<WeatherEntity> {
        val result = fetchWeather(lat, lon, units, lang)
        if (result is Resource.Success && result.data != null) {
            localDataSource.insertCurrentWeather(result.data)
        }
        return result
    }

    override suspend fun refreshForecast(lat: Double, lon: Double, units: String, lang: String): Resource<Unit> {
        return try {
            val response = remoteDataSource.getDailyForecast(lat, lon, units, lang, BuildConfig.API_KEY, 7)
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
            localDataSource.clearForecast()
            localDataSource.insertForecast(entities)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e("AppRepo", "Error fetching forecast", e)
            Resource.Error(e.message ?: "Unknown Error")
        }
    }

    override suspend fun refreshHourlyForecast(lat: Double, lon: Double, units: String, lang: String): Resource<Unit> {
        return try {
            val response = remoteDataSource.getHourlyForecast(lat, lon, units, lang, BuildConfig.API_KEY)
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
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e("AppRepo", "Error fetching hourly", e)
            Resource.Error(e.message ?: "Unknown Error")
        }
    }

    override fun getFavorites(): Flow<List<FavoriteLocation>> = localDataSource.getFavorites()
    override suspend fun addFavorite(favorite: FavoriteLocation) = localDataSource.insertFavorite(favorite)
    override suspend fun removeFavorite(favorite: FavoriteLocation) = localDataSource.deleteFavorite(favorite)
    override suspend fun isFavorite(lat: Double, lon: Double): Boolean = localDataSource.getFavoriteByCoords(lat, lon) != null
}
