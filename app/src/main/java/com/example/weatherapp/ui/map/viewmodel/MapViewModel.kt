package com.example.weatherapp.ui.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.data.model.FavoriteLocation
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

class MapViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    private var currentUnits = "metric"
    private var currentLang = "en"
    
    private val _navigateToPrevious = MutableSharedFlow<Unit>()
    val navigateToPrevious = _navigateToPrevious.asSharedFlow()

    private val _selectedPoint = MutableStateFlow<LatLng?>(null)
    val selectedPoint: StateFlow<LatLng?> = _selectedPoint.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            repository.unitsFlow.collect { currentUnits = it }
        }
        viewModelScope.launch {
            repository.languageFlow.collect { currentLang = it }
        }
    }

    fun updateSelectedPoint(point: LatLng) {
        _selectedPoint.value = point
    }

    fun selectLocation(lat: Double, lon: Double, source: String = "favorites") {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val currentUnits = repository.unitsFlow.first()
                val currentLang = repository.languageFlow.first()
                
                if (source == "settings") {
                    repository.setManualLocation(lat, lon)
                    repository.setLocationMode("map")
                    repository.refreshCurrentWeather(lat, lon, currentUnits, currentLang)
                } else {
                    val weather = repository.fetchWeather(lat, lon, currentUnits, currentLang)
                    repository.addFavorite(
                        FavoriteLocation(
                            name = weather.cityName.ifBlank { "Selected Location" },
                            lat = weather.lat,
                            lon = weather.lon,
                            currentTemp = weather.temp,
                            condition = weather.description,
                            icon = weather.icon
                        )
                    )
                    repository.refreshCurrentWeather(lat, lon, currentUnits, currentLang)
                }
                
                _navigateToPrevious.emit(Unit)
            } catch (e: Exception) {
                android.util.Log.e("MapViewModel", "Error fetching weather for location", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
