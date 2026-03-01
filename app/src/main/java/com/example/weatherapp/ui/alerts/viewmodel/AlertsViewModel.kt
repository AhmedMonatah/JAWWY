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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class AlertsUiEvent {
    data class ShowSnackbar(val count: Int) : AlertsUiEvent()
}

class AlertsViewModel(
    private val repository: WeatherRepository,
    private val context: Context
) : ViewModel() {

    private val _alerts = MutableStateFlow<List<Alert>>(emptyList())
    val alerts: StateFlow<List<Alert>> = _alerts.asStateFlow()

    private val _selectedAlerts = MutableStateFlow<Set<Alert>>(emptySet())
    val selectedAlerts = _selectedAlerts.asStateFlow()

    private val _uiEvents = MutableSharedFlow<AlertsUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet = _showBottomSheet.asStateFlow()

    private val _showPermissionDialog = MutableStateFlow(false)
    val showPermissionDialog = _showPermissionDialog.asStateFlow()

    private val _showNoInternetDialog = MutableStateFlow(false)
    val showNoInternetDialog = _showNoInternetDialog.asStateFlow()

    val language = repository.languageFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    init {
        viewModelScope.launch {
            repository.getAllAlerts().collect { _alerts.value = it }
        }
    }

    fun toggleSelection(alert: Alert) {
        _selectedAlerts.value = if (_selectedAlerts.value.contains(alert)) {
            _selectedAlerts.value - alert
        } else {
            _selectedAlerts.value + alert
        }
    }

    fun clearSelection() {
        _selectedAlerts.value = emptySet()
    }

    fun deleteSelectedAlerts() {
        val toDelete = _selectedAlerts.value.toList()
        viewModelScope.launch {
            for (alert in toDelete) {
                repository.deleteAlert(alert)
                NotificationHelper.cancelAlert(context, alert.id, alert.type)
            }
            _uiEvents.emit(AlertsUiEvent.ShowSnackbar(toDelete.size))
            clearSelection()
        }
    }



    fun setShowBottomSheet(show: Boolean) {
        _showBottomSheet.value = show
    }

    fun setShowPermissionDialog(show: Boolean) {
        _showPermissionDialog.value = show
    }

    fun setShowNoInternetDialog(show: Boolean) {
        _showNoInternetDialog.value = show
    }

    fun addAlert(startTime: Long, endTime: Long, type: String, ringtoneUri: String?) {
        if (!isOnline()) {
            _showNoInternetDialog.value = true
            return
        }

        viewModelScope.launch {
            val alert = Alert(startTime = startTime, endTime = endTime, type = type, isEnabled = true)
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



    fun toggleAlert(alert: Alert) {
        viewModelScope.launch {
            val updated = alert.copy(isEnabled = !alert.isEnabled)
            repository.insertAlert(updated)
            if (updated.isEnabled) {
                NotificationHelper.scheduleAlert(context, updated.startTime, updated.endTime, updated.type, updated.id)
            } else {
                NotificationHelper.cancelAlert(context, updated.id, updated.type)
            }
        }
    }

    fun isOnline() = repository.isOnline()
}
