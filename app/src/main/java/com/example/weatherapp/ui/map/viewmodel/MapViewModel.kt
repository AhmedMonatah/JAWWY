package com.example.weatherapp.ui.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repository.AppRepository
import com.example.weatherapp.utils.Config
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    fun selectLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            val apiKey = Config.API_KEY
            repository.refreshCurrentWeather(lat, lon, apiKey, "metric", "en") 
        }
    }
}
