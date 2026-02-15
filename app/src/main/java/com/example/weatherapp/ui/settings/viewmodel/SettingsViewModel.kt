package com.example.weatherapp.ui.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    val units = repository.unitsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "metric")

    val language = repository.languageFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    val locationMode = repository.locationModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "gps")

    val darkMode = repository.darkModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")

    fun updateSettings(newUnits: String, newLang: String) {
        viewModelScope.launch {
            repository.setUnits(newUnits)
            repository.setLanguage(newLang)
        }
    }

    fun updateLocationMode(mode: String) {
        viewModelScope.launch {
            repository.setLocationMode(mode)
        }
    }

    fun updateDarkMode(mode: String) {
        viewModelScope.launch {
            repository.setDarkMode(mode)
        }
    }
}
