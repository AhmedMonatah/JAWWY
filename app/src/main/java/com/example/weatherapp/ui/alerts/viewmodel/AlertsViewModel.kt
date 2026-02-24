package com.example.weatherapp.ui.alerts.viewmodel

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.Alert
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.data.service.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlertsViewModel(
    private val repository: WeatherRepository,
    private val context: Context
) : ViewModel() {

    private val _alerts = MutableStateFlow<List<Alert>>(emptyList())
    val alerts: StateFlow<List<Alert>> = _alerts.asStateFlow()

    val language = repository.languageFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    init {
        viewModelScope.launch {
            repository.getAllAlerts().collect { _alerts.value = it }
        }
    }

    /** Returns true if the app can schedule exact alarms (required for alarm type). */
    fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).canScheduleExactAlarms()
        } else true
    }

    /**
     * Persists and schedules an alert.
     * [endTime] is 0L for alarm type (ignored); used only for notification windows.
     */
    fun addAlert(startTime: Long, endTime: Long, type: String) {
        viewModelScope.launch {
            val alert = Alert(startTime = startTime, endTime = endTime, type = type)
            val id = repository.insertAlert(alert)
            val savedAlert = alert.copy(id = id.toInt())
            Log.d("AlertsVM", "Alert saved id=${savedAlert.id} type=$type")
            NotificationHelper.scheduleAlert(
                context = context,
                startTime = savedAlert.startTime,
                endTime = savedAlert.endTime,
                type = savedAlert.type,
                alertId = savedAlert.id
            )
        }
    }

    fun deleteAlert(alert: Alert) {
        viewModelScope.launch {
            repository.deleteAlert(alert)
            NotificationHelper.cancelAlert(context, alert.id, alert.type)
        }
    }

    /** Toggle enable/disable — reschedules or cancels without removing from DB. */
    fun toggleAlert(alert: Alert) {
        viewModelScope.launch {
            val updated = alert.copy(isEnabled = !alert.isEnabled)
            repository.insertAlert(updated) // REPLACE strategy updates in place
            if (updated.isEnabled) {
                NotificationHelper.scheduleAlert(context, updated.startTime, updated.endTime, updated.type, updated.id)
            } else {
                NotificationHelper.cancelAlert(context, updated.id, updated.type)
            }
        }
    }

    fun isOnline() = repository.isOnline()
}
