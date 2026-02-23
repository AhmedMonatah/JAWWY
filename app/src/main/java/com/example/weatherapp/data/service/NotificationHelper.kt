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

    // ─── Public API ───────────────────────────────────────────────────────────

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

    // ─── Alarm (AlarmManager – fires exactly at startTime) ───────────────────

    private fun scheduleExactAlarm(context: Context, startTime: Long, alertId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alertId", alertId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alertId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val triggerAt = maxOf(startTime, System.currentTimeMillis() + 1000L)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
    }

    private fun cancelExactAlarm(context: Context, alertId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alertId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let { alarmManager.cancel(it) }
    }

    // ─── Notification (WorkManager – periodic from startTime until endTime) ──

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

    // ─── Show alarm notification ──────────────────────────────────────────────

    fun showAlarmNotification(context: Context, alertId: Int) {
        val channelId = "alarm_channel"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "CloudX Alarm", NotificationManager.IMPORTANCE_HIGH).apply {
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                setSound(sound, android.media.AudioAttributes.Builder()
                    .setUsage(android.media.AudioAttributes.USAGE_ALARM).build())
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 400, 200, 400)
            }
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, alertId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("🌙 ${context.getString(R.string.alarm_title)}")
            .setContentText(context.getString(R.string.alarm_text))
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("✨ ${context.getString(R.string.alarm_text)}")
                .setSummaryText("CloudX · Ramadan Alert"))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setSound(sound)
            .setVibrate(longArrayOf(0, 400, 200, 400))
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setColor(0xFFD4A843.toInt())   // RamadanGold as accent
            .build()

        manager.notify(alertId, notification)
    }

    // ─── Weather notification ─────────────────────────────────────────────────

    fun createWeatherNotification(context: Context, temp: Int, desc: String, city: String) {
        val channelId = "weather_channel"
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? NotificationManager // Fallback
            ?: context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "CloudX Weather", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        val title = if (city.isNotEmpty()) "🌙 Weather in $city" else "🌙 CloudX Weather"
        val intent = Intent(context, MainActivity::class.java)
        val pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText("✨ $temp°C · $desc")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("✨ Current weather is $temp°C with $desc.")
                .setSummaryText("CloudX · Daily Blessing"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .setColor(0xFFD4A843.toInt()) // RamadanGold
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
