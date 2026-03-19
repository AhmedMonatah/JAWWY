package com.example.weatherapp.ui.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.weatherapp.R
import com.example.weatherapp.WeatherApplication
import com.example.weatherapp.data.model.WeatherEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AlarmService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val TAG = "AlarmService"

    companion object {
        const val ACTION_STOP_ALARM = "ACTION_STOP_ALARM"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        android.util.Log.d(TAG, "onStartCommand: action=${intent?.action} alertId=${intent?.getIntExtra("alertId", -1)}")
        if (intent?.action == ACTION_STOP_ALARM) {
            android.util.Log.d(TAG, "Stopping alarm via action")
            stopAlarm()
            stopSelf()
            return START_NOT_STICKY
        }

        val alertId = intent?.getIntExtra("alertId", -1) ?: -1
        
        startAlarmForeground(alertId)
        playAlarm()
        
        return START_STICKY
    }

    private fun startAlarmForeground(alertId: Int) {
        android.util.Log.d(TAG, "startAlarmForeground: alertId=$alertId")
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WeatherApp:AlarmWakeLock")
        wakeLock?.acquire(10 * 60 * 1000L) // 10 minutes max

        val repository = (applicationContext as WeatherApplication).container.weatherRepository
        val cachedWeather = runBlocking(Dispatchers.IO) {
            repository.getCurrentWeather().firstOrNull()
        }
        
        val notification = NotificationHelper.getAlarmNotification(this, alertId, cachedWeather)
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(alertId, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
            } else {
                startForeground(alertId, notification)
            }
            android.util.Log.d(TAG, "startForeground called successfully")
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error in startForeground", e)
        }

        serviceScope.launch {
            try {
                android.util.Log.d(TAG, "Weather fetch started in coroutine")
                val weather = fetchWeather()
                android.util.Log.d(TAG, "Weather fetch completed: ${weather?.cityName}")
                val updatedNotification = NotificationHelper.getAlarmNotification(this@AlarmService, alertId, weather)
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(alertId, updatedNotification)
                android.util.Log.d(TAG, "Notification updated with weather")
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error in weather fetch coroutine", e)
            }
        }
    }

    private suspend fun fetchWeather(): WeatherEntity? {
        val repository = (applicationContext as WeatherApplication).container.weatherRepository
        val mode = repository.locationModeFlow.firstOrNull() ?: "gps"
        val (lat, lon) = if (mode == "map") {
            repository.manualLocationFlow.firstOrNull() ?: (null to null)
        } else {
            repository.getCurrentWeather().firstOrNull()?.let { it.lat to it.lon } ?: (null to null)
        }

        if (lat == null || lon == null) return repository.getCurrentWeather().firstOrNull()

        val units = repository.unitsFlow.firstOrNull() ?: "metric"
        val lang = repository.languageFlow.firstOrNull() ?: "en"

        return try {
            repository.fetchWeather(lat, lon, units, lang)
        } catch (e: Exception) {
            repository.getCurrentWeather().firstOrNull()
        }
    }

    private fun playAlarm() {
        android.util.Log.d(TAG, "playAlarm called")
        if (mediaPlayer != null) {
            android.util.Log.d(TAG, "mediaPlayer already exists, skipping")
            return
        }
        
        try {
            android.util.Log.d(TAG, "Initializing MediaPlayer")
            mediaPlayer = MediaPlayer().apply {
                val attributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                setAudioAttributes(attributes)
                
                // Use setDataSource with raw resource URI for better stability
                val assetFileDescriptor = resources.openRawResourceFd(R.raw.alarm)
                setDataSource(assetFileDescriptor.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.length)
                assetFileDescriptor.close()
                
                isLooping = true
                setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
                setVolume(1.0f, 1.0f)
                
                prepare()
                start()
                android.util.Log.d(TAG, "MediaPlayer started successfully")
            }
            setupVibrator()
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Failed to play alarm", e)
            // Fallback to system alarm sound if raw resource fails
            try {
                val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(applicationContext, alarmUri)
                    setAudioAttributes(AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build())
                    isLooping = true
                    prepare()
                    start()
                }
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }
    }

    private fun setupVibrator() {
        try {
            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(longArrayOf(0, 500, 500), 1)
                vibrator?.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 500, 500), 1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopAlarm() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        vibrator?.cancel()
        vibrator = null
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
        }
        wakeLock = null
    }

    override fun onDestroy() {
        stopAlarm()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
