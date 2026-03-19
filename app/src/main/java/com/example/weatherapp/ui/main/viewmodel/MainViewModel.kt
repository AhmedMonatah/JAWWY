package com.example.weatherapp.ui.main.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.data.model.WeatherEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    val currentWeather: StateFlow<WeatherEntity?> = repository.getCurrentWeather()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val connectivityFlow: StateFlow<Boolean> = repository.connectivityFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.isOnline())

    private val _showNoInternet = MutableStateFlow(false)
    val showNoInternet = _showNoInternet.asStateFlow()

    fun setShowNoInternet(show: Boolean) {
        _showNoInternet.value = show
    }

    fun isOnline() = repository.isOnline()
}