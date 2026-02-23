package com.example.weatherapp.data.service

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.utils.state.Resource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: WeatherRepository
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "NotificationWorker"
    }

    override suspend fun doWork(): Result {
        val endTime = inputData.getLong("endTime", 0L)
        val alertId = inputData.getInt("alertId", -1)

        if (endTime != 0L && System.currentTimeMillis() > endTime) {
            if (alertId != -1) WorkManager.getInstance(appContext).cancelUniqueWork("notif_$alertId")
            return Result.success()
        }

        try {
            val weather = fetchWeather()
            if (weather != null) {
                NotificationHelper.createWeatherNotification(appContext, weather.temp.toInt(), weather.description, weather.cityName)
            } else {
                NotificationHelper.createWeatherNotification(appContext, 0, "Weather data unavailable", "")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Work failed", e)
            NotificationHelper.createWeatherNotification(appContext, 0, "Weather update error", "")
        }

        return Result.success()
    }

    private suspend fun fetchWeather(): com.example.weatherapp.model.WeatherEntity? {
        val mode = repository.locationModeFlow.firstOrNull() ?: "gps"
        val (lat, lon) = if (mode == "map") {
            repository.manualLocationFlow.firstOrNull() ?: (null to null)
        } else {
            repository.getCurrentWeather().firstOrNull()?.let { it.lat to it.lon } ?: (null to null)
        }

        if (lat == null || lon == null) return null

        val units = repository.unitsFlow.firstOrNull() ?: "metric"
        val lang = repository.languageFlow.firstOrNull() ?: "en"

        return when (val res = repository.fetchWeather(lat, lon, units, lang)) {
            is Resource.Success -> res.data
            else -> repository.getCurrentWeather().firstOrNull()
        }
    }
}
