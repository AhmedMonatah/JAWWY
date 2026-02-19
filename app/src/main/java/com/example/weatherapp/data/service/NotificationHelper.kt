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
import com.example.weatherapp.receiver.AlertReceiver
import java.util.concurrent.TimeUnit

object NotificationHelper {

    fun scheduleAlert(context: Context, startTime: Long, endTime: Long, type: String, alertId: Int) {
        if (type == "alarm") {
            scheduleExactAlarm(context, startTime, alertId)
        } else {
            schedulePeriodic(context, startTime, endTime, alertId)
        }
    }

    fun schedulePeriodic(context: Context, startTime: Long, endTime: Long, alertId: Int) {
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

    fun scheduleExactAlarm(context: Context, startTime: Long, alertId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlertReceiver::class.java).apply {
            putExtra("ALERT_ID", alertId)
            putExtra("ALERT_TYPE", "alarm")
        }
        val pi = PendingIntent.getBroadcast(
            context, alertId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val trigger = if (startTime <= System.currentTimeMillis()) System.currentTimeMillis() + 3000 else startTime

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger, pi)
        } else {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger, pi)
        }
    }

    fun cancelAlert(context: Context, alertId: Int, type: String) {
        if (type == "alarm") {
            val intent = Intent(context, AlertReceiver::class.java)
            val pi = PendingIntent.getBroadcast(
                context, alertId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(pi)
        } else {
            cancelUniqueWork(context, alertId)
        }
    }

    fun cancelUniqueWork(context: Context, alertId: Int) {
        WorkManager.getInstance(context).cancelUniqueWork("notif_$alertId")
    }

    fun showAlarmNotification(context: Context, alertId: Int) {
        val channelId = "alarm_channel"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Alarm Channel", NotificationManager.IMPORTANCE_HIGH).apply {
                setSound(sound, android.media.AudioAttributes.Builder().setUsage(android.media.AudioAttributes.USAGE_ALARM).build())
            }
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java)
        val pi = PendingIntent.getActivity(context, alertId, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("⏰ Alarm!")
            .setContentText("Your alarm time has arrived!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(sound)
            .setContentIntent(pi)
            .build()

        manager.notify(alertId, notification)
    }

    fun createWeatherNotification(context: Context, temp: Int, desc: String, city: String) {
        val channelId = "weather_channel"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Weather Updates", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        val title = if (city.isNotEmpty()) "🌤 Weather in $city" else "🌤 Weather Update"
        val content = "$temp°C · $desc"

        val intent = Intent(context, MainActivity::class.java)
        val pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
