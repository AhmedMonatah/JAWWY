package com.example.weatherapp.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.weatherapp.data.repository.AppRepository
import com.example.weatherapp.utils.Config
import com.example.weatherapp.utils.NotificationUtils
import com.example.weatherapp.utils.Resource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: AppRepository
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "NotificationWorker"
    }

    override suspend fun doWork(): Result {
        val endTime = inputData.getLong("endTime", 0L)
        val alertId = inputData.getInt("alertId", -1)

        Log.d(TAG, "doWork: alertId=$alertId, endTime=$endTime, now=${System.currentTimeMillis()}")

        if (endTime != 0L && System.currentTimeMillis() > endTime) {
            Log.d(TAG, "End time passed, canceling work for alertId=$alertId")
            if (alertId != -1) {
                WorkManager.getInstance(applicationContext).cancelUniqueWork("notif_$alertId")
            }
            return Result.success()
        }

        try {
            val weather = fetchWeather()
            if (weather != null) {
                NotificationUtils.createWeatherNotification(
                    applicationContext,
                    weather.temp.toInt(),
                    weather.description,
                    weather.cityName
                )
                Log.d(TAG, "Notification shown: ${weather.cityName} ${weather.temp}°")
            } else {
                NotificationUtils.createWeatherNotification(
                    applicationContext, 0, "Weather data unavailable", ""
                )
                Log.w(TAG, "No weather data, showed fallback")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching weather", e)
            NotificationUtils.createWeatherNotification(
                applicationContext, 0, "Weather update", ""
            )
        }

        return Result.success()
    }

    private suspend fun fetchWeather(): com.example.weatherapp.data.local.entity.WeatherEntity? {
        val locationMode = repository.locationModeFlow.firstOrNull() ?: "gps"
        var lat: Double? = null
        var lon: Double? = null

        if (locationMode == "map") {
            val coords = repository.manualLocationFlow.firstOrNull()
            lat = coords?.first
            lon = coords?.second
        }

        if (lat == null || lon == null) {
            val cached = repository.getCurrentWeather().firstOrNull()
            lat = cached?.lat
            lon = cached?.lon
        }

        if (lat == null || lon == null) return null

        val units = repository.unitsFlow.firstOrNull() ?: "metric"
        val lang = repository.languageFlow.firstOrNull() ?: "en"

        return when (val result = repository.fetchWeather(lat, lon, Config.API_KEY, units, lang)) {
            is Resource.Success -> result.data
            else -> repository.getCurrentWeather().firstOrNull()
        }
    }
}
