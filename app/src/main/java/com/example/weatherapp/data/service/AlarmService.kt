package com.example.weatherapp.data.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.weatherapp.R
import com.example.weatherapp.WeatherApplication
import com.example.weatherapp.model.WeatherEntity
import com.example.weatherapp.utils.state.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class AlarmService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        const val ACTION_STOP_ALARM = "ACTION_STOP_ALARM"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_ALARM) {
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
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WeatherApp:AlarmWakeLock")
        wakeLock?.acquire(10 * 60 * 1000L) // 10 minutes max

        val notification = NotificationHelper.getAlarmNotification(this, alertId, null)
        startForeground(alertId, notification)

        serviceScope.launch {
            val weather = fetchWeather()
            val updatedNotification = NotificationHelper.getAlarmNotification(this@AlarmService, alertId, weather)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(alertId, updatedNotification)
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

        return when (val res = repository.fetchWeather(lat, lon, units, lang)) {
            is Resource.Success -> res.data
            else -> repository.getCurrentWeather().firstOrNull()
        }
    }

    private fun playAlarm() {
        if (mediaPlayer != null) return
        
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.alarm).apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                isLooping = true
                setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
                setOnCompletionListener { it.start() } // Explicitly restart for short files
                setVolume(1.0f, 1.0f)
                start()
            }
            setupVibrator()
        } catch (e: Exception) {
            e.printStackTrace()
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
