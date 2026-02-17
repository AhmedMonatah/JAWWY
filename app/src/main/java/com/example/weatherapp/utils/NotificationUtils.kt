package com.example.weatherapp.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.weatherapp.R
import com.example.weatherapp.receiver.AlertReceiver
import com.example.weatherapp.worker.NotificationWorker
import java.util.concurrent.TimeUnit

object NotificationUtils {

    private const val TAG = "NotificationUtils"

    fun showAlarmNotification(context: Context, alertId: Int) {
        val channelId = "alarm_channel"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Alarm Channel", NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(alarmSound, android.media.AudioAttributes.Builder()
                    .setUsage(android.media.AudioAttributes.USAGE_ALARM)
                    .build())
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 800, 400, 800)
            }
            manager.createNotificationChannel(channel)
        }

        val tapIntent = Intent(context, com.example.weatherapp.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val tapPending = PendingIntent.getActivity(
            context, alertId, tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("⏰ Alarm!")
            .setContentText("Your alarm time has arrived!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(alarmSound)
            .setVibrate(longArrayOf(0, 800, 400, 800))
            .setContentIntent(tapPending)
            .build()

        manager.notify(alertId, notification)
        Log.d(TAG, "Alarm notification shown for alertId=$alertId")
    }

    // Show weather notification with default notification sound
    fun createWeatherNotification(
        context: Context,
        temperature: Int,
        description: String,
        cityName: String = ""
    ) {
        val channelId = "weather_channel"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notifSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Weather Updates", NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                setSound(notifSound, android.media.AudioAttributes.Builder()
                    .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
                    .build())
            }
            manager.createNotificationChannel(channel)
        }

        val suggestion = when {
            description.contains("clear", true) || description.contains("sun", true) ->
                "Beautiful sunny day! Don't forget your sunglasses 😎"
            description.contains("rain", true) || description.contains("drizzle", true) ->
                "Rainy weather. Take an umbrella! ☔"
            description.contains("snow", true) ->
                "Snowy weather! Stay warm ❄️"
            description.contains("cloud", true) ->
                "Cloudy skies. Perfect for a walk! ☁️"
            description.contains("thunder", true) ->
                "Thunderstorm warning! Stay indoors ⚡"
            description.contains("mist", true) || description.contains("fog", true) ->
                "Low visibility. Drive carefully! 🌫️"
            else -> "Have a great day! ✨"
        }

        val title = if (cityName.isNotEmpty()) "🌤 Weather in $cityName" else "🌤 Weather Update"
        val content = "$temperature°C · $description\n$suggestion"

        val tapIntent = Intent(context, com.example.weatherapp.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val tapPending = PendingIntent.getActivity(
            context, 0, tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(tapPending)
            .setAutoCancel(true)
            .setSound(notifSound)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
        Log.d(TAG, "Weather notification shown: $title")
    }

    fun scheduleAlert(
        context: Context,
        startTime: Long,
        endTime: Long,
        type: String,
        alertId: Int
    ) {
        Log.d(TAG, "scheduleAlert: type=$type, alertId=$alertId")

        if (type == "alarm") {
            scheduleExactAlarm(context, startTime, alertId)
        } else {
            schedulePeriodicNotification(context, startTime, endTime, alertId)
        }
    }

    fun cancelAlert(context: Context, alertId: Int, type: String, startTime: Long) {
        if (type == "alarm") {
            val intent = Intent(context, AlertReceiver::class.java)
            val pi = PendingIntent.getBroadcast(
                context, alertId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(pi)
        } else {
            WorkManager.getInstance(context).cancelUniqueWork("notif_$alertId")
        }
        Log.d(TAG, "cancelAlert: type=$type, alertId=$alertId")
    }

    // Schedule exact alarm via AlarmManager
    private fun scheduleExactAlarm(context: Context, startTime: Long, alertId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlertReceiver::class.java).apply {
            putExtra("ALERT_ID", alertId)
            putExtra("ALERT_TYPE", "alarm")
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, alertId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        var triggerTime = startTime
        if (triggerTime <= System.currentTimeMillis()) {
            triggerTime = System.currentTimeMillis() + 3000
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent
                )
            }
            Log.d(TAG, "Exact alarm scheduled at triggerTime=$triggerTime, alertId=$alertId")
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException scheduling exact alarm", e)
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }

    private fun schedulePeriodicNotification(
        context: Context,
        startTime: Long,
        endTime: Long,
        alertId: Int
    ) {
        val delay = maxOf(0L, startTime - System.currentTimeMillis())

        val data = workDataOf(
            "endTime" to endTime,
            "alertId" to alertId
        )

        val request = PeriodicWorkRequestBuilder<NotificationWorker>(
            15, TimeUnit.MINUTES
        )
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("notif_tag_$alertId")
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "notif_$alertId",
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )

        Log.d(TAG, "Periodic notification scheduled: delay=${delay}ms, alertId=$alertId")
    }
}
