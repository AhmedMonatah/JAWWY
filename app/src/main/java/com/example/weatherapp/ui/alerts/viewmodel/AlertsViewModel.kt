package com.example.weatherapp.ui.alerts.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.local.entity.Alert
import com.example.weatherapp.data.repository.AlertRepository
import com.example.weatherapp.receiver.AlertReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val repository: AlertRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _alerts = MutableStateFlow<List<Alert>>(emptyList())
    val alerts: StateFlow<List<Alert>> = _alerts.asStateFlow()

    init {
        fetchAlerts()
    }

    private fun fetchAlerts() {
        viewModelScope.launch {
            repository.getAllAlerts().collect {
                _alerts.value = it
            }
        }
    }

    fun addAlert(startTime: Long, endTime: Long, type: String) {
        viewModelScope.launch {
            val alert = Alert(startTime = startTime, endTime = endTime, type = type)
            val id = repository.insertAlert(alert)
            scheduleAlarm(alert.copy(id = id.toInt()))
        }
    }

    fun deleteAlert(alert: Alert) {
        viewModelScope.launch {
            repository.deleteAlert(alert)
            cancelAlarm(alert)
        }
    }

    fun deleteAllAlerts() {
        viewModelScope.launch {
            _alerts.value.forEach { cancelAlarm(it) }
            repository.deleteAllAlerts()
        }
    }
    
    private fun scheduleAlarm(alert: Alert) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlertReceiver::class.java).apply {
            putExtra("ALERT_ID", alert.id)
            putExtra("ALERT_TYPE", alert.type)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alert.id, // Unique Request Code
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // For Android 12+ (S) and 14 (U), Exact Alarms require permission.
        // We use setExactAndAllowWhileIdle for critical alerts if permitted, 
        // else standard set.
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        alert.startTime,
                        pendingIntent
                    )
                } else {
                    // Fallback or ask for permission (already handled in UI ideally)
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        alert.startTime,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    alert.startTime,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun cancelAlarm(alert: Alert) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlertReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alert.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
