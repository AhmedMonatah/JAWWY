package com.example.weatherapp.data.service

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.receiver.AlarmReceiver
import java.util.concurrent.TimeUnit

object NotificationHelper {


    fun scheduleAlert(context: Context, startTime: Long, endTime: Long, type: String, alertId: Int) {
        if (type == "alarm") {
            scheduleExactAlarm(context, startTime, alertId)
        } else {
            scheduleNotificationWindow(context, startTime, endTime, alertId)
        }
    }

    fun cancelAlert(context: Context, alertId: Int, type: String) {
        if (type == "alarm") {
            cancelExactAlarm(context, alertId)
        } else {
            WorkManager.getInstance(context).cancelUniqueWork("notif_$alertId")
        }
    }


    private fun scheduleExactAlarm(context: Context, startTime: Long, alertId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alertId", alertId)
        }
        
        val operation = PendingIntent.getBroadcast(
            context,
            alertId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // For AlarmClockInfo, we need a pending intent that opens the main UI if clicked from the system alarm clock
        val mainIntent = Intent(context, MainActivity::class.java)
        val mainPi = PendingIntent.getActivity(
            context,
            alertId,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAt = maxOf(startTime, System.currentTimeMillis() + 1000L)
        val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerAt, mainPi)
        
        try {
            alarmManager.setAlarmClock(alarmClockInfo, operation)
            android.util.Log.d("NotificationHelper", "Scheduled AlarmClock trigger for $alertId at $triggerAt")
        } catch (e: Exception) {
            android.util.Log.e("NotificationHelper", "Failed to schedule alarm clock", e)
        }
    }

    private fun cancelExactAlarm(context: Context, alertId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pi = PendingIntent.getBroadcast(
            context,
            alertId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pi?.let { alarmManager.cancel(it) }
    }


    private fun scheduleNotificationWindow(context: Context, startTime: Long, endTime: Long, alertId: Int) {
        val delay = maxOf(0L, startTime - System.currentTimeMillis())
        val data = workDataOf("endTime" to endTime, "alertId" to alertId)

        val request = PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("notif_tag_$alertId")
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "notif_$alertId",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }


    fun showAlarmNotification(context: Context, alertId: Int) {
        android.util.Log.d("NotificationHelper", "showAlarmNotification: alertId=$alertId")
        val intent = Intent(context, AlarmService::class.java).apply {
            putExtra("alertId", alertId)
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            android.util.Log.d("NotificationHelper", "Service start command sent")
        } catch (e: Exception) {
            android.util.Log.e("NotificationHelper", "Failed to start AlarmService", e)
        }
    }

    fun getAlarmNotification(context: Context, alertId: Int, weather: com.example.weatherapp.model.WeatherEntity? = null): android.app.Notification {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "alarm_notification_channel_v2"

        val stopIntent = Intent(context, AlarmService::class.java).apply {
            action = AlarmService.ACTION_STOP_ALARM
        }
        val stopPi = PendingIntent.getService(
            context, alertId, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val clickIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val clickPi = PendingIntent.getActivity(
            context, alertId, clickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelName = context.getString(R.string.alarm_channel_name)
        val title = if (weather != null && weather.cityName.isNotEmpty()) {
            "🌙 ${context.getString(R.string.weather_title_city, weather.cityName)}"
        } else {
            "🌙 ${context.getString(R.string.weather_title_default)}"
        }
        
        val text = if (weather != null) {
            val description = weather.description.lowercase()
            val temp = weather.temp
            
            // Logic for personal suggestion
            val suggestion = when {
                (description.contains("clear") || description.contains("clouds")) && temp > 15 -> 
                    context.getString(R.string.suggestion_wonderful_walk)
                description.contains("rain") || description.contains("snow") || description.contains("storm") || temp < 5 -> 
                    context.getString(R.string.suggestion_stay_home)
                else -> context.getString(R.string.suggestion_normal)
            }
            
            "✨ ${context.getString(R.string.weather_personal_subtitle, temp, suggestion)}"
        } else {
            "✨ ${context.getString(R.string.alarm_text)}"
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                setSound(null, null) 
                enableVibration(true)
            }
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(android.graphics.BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(clickPi)
            .setOngoing(true)
            .setAutoCancel(false)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(R.drawable.ic_notification, context.getString(R.string.stop_alarm), stopPi)
            .setColor(0xFFD4A843.toInt())
            .build()
    }

    fun createWeatherNotification(context: Context, temp: Int, desc: String, city: String) {
        val channelId = "weather_channel"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val intent = Intent(context, MainActivity::class.java)
        val pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val channelName = context.getString(R.string.weather_channel_name)
        val title = if (city.isNotEmpty()) {
            "🌙 ${context.getString(R.string.weather_title_city, city)}"
        } else {
            "🌙 ${context.getString(R.string.weather_title_default)}"
        }
        val text = context.getString(R.string.weather_text, temp, desc)
        val summary = context.getString(R.string.weather_summary)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText("✨ $text")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("✨ $text")
                .setSummaryText(summary))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .setColor(0xFFD4A843.toInt())
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
