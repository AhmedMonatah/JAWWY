package com.example.weatherapp.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weatherapp.R
import com.example.weatherapp.data.repository.AlertRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

@HiltWorker
class AlertWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: com.example.weatherapp.data.repository.AppRepository, // Use AppRepository for weather
    private val alertRepository: AlertRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val alertId = inputData.getInt("ALERT_ID", -1)
            if (alertId == -1) return@withContext Result.failure()

            // Fetch current weather
            // Note: In a real app we might want to fetch OneCall or specific Alert endpoint
            // For now, we fetch current weather to tell user "It is Clear" etc.
            val weather = repository.getCurrentWeather().firstOrNull()
            val description = weather?.description ?: "Weather update"

            val alert = alertRepository.getAlertById(alertId) ?: return@withContext Result.failure()
            
            triggerAlert(alert, description)
            
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun triggerAlert(alert: com.example.weatherapp.data.local.entity.Alert, weatherDesc: String) {
        val context = applicationContext
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "weather_alerts_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Weather Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for weather alerts and alarms"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Customize message based on user request ("tell user only weather is clear or cloud")
        val contentText = "Current conditions: $weatherDesc. Take care!"
        val title = if (alert.type == "alarm") "Weather Alarm" else "Weather Notification"

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        if (alert.type.lowercase() == "alarm") {
             val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
             notificationBuilder.setSound(alarmSound)
             // For a full screen alarm activity, we would use setFullScreenIntent here
        }

        notificationManager.notify(alert.id, notificationBuilder.build())
    }
}
