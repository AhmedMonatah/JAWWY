package com.example.weatherapp.ui.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repository.WeatherRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed class SettingsUiEvent {
    object NavigateToMap : SettingsUiEvent()
    object ShowNoInternet : SettingsUiEvent()
}

class SettingsViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    val units = repository.unitsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "metric")

    val language = repository.languageFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    val locationMode = repository.locationModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "gps")

    val themeMode = repository.themeModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")

    private val _uiEvents = MutableSharedFlow<SettingsUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    fun updateSettings(newUnits: String, newLang: String) {
        if (!repository.isOnline()) {
            viewModelScope.launch { _uiEvents.emit(SettingsUiEvent.ShowNoInternet) }
            return
        }
        viewModelScope.launch {
            repository.setUnits(newUnits)
            repository.setLanguage(newLang)
        }
    }

    fun updateTemperatureUnit(unit: String) {
        if (!repository.isOnline()) {
            viewModelScope.launch { _uiEvents.emit(SettingsUiEvent.ShowNoInternet) }
            return
        }
        viewModelScope.launch {
            repository.setUnits(unit)
        }
    }

    fun updateLanguage(lang: String) {
        if (!repository.isOnline()) {
            viewModelScope.launch { _uiEvents.emit(SettingsUiEvent.ShowNoInternet) }
            return
        }
        viewModelScope.launch {
            repository.setLanguage(lang)
        }
    }

    fun updateLocationMode(mode: String) {
        if (mode == "map" && !repository.isOnline()) {
            viewModelScope.launch { _uiEvents.emit(SettingsUiEvent.ShowNoInternet) }
            return
        }
        
        viewModelScope.launch {
            if (mode == "map") {
                _uiEvents.emit(SettingsUiEvent.NavigateToMap)
            } else {
                repository.setLocationMode(mode)
            }
        }
    }

    fun updateThemeMode(mode: String) {
        viewModelScope.launch {
            repository.setThemeMode(mode)
        }
    }

    fun isOnline() = repository.isOnline()
}
