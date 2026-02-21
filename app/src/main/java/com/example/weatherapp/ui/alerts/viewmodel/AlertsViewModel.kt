package com.example.weatherapp.ui.alerts.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.Alert
import com.example.weatherapp.data.repository.AppRepository
import com.example.weatherapp.data.service.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val repository: AppRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _alerts = MutableStateFlow<List<Alert>>(emptyList())
    val alerts: StateFlow<List<Alert>> = _alerts.asStateFlow()

    val language = repository.languageFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

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
            NotificationHelper.cancelAlert(
                context = context,
                alertId = alert.id,
                type = alert.type
            )
        }
    }

    fun isOnline() = repository.isOnline()
}
