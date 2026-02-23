package com.example.weatherapp.ui.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.utils.state.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private var currentUnits = "metric"
    private var currentLang = "en"

    init {
        viewModelScope.launch {
            repository.unitsFlow.collect { currentUnits = it }
        }
        viewModelScope.launch {
            repository.languageFlow.collect { currentLang = it }
        }
    }

    fun selectLocation(lat: Double, lon: Double, source: String = "favorites") {
        viewModelScope.launch {
            val result = repository.fetchWeather(lat, lon, currentUnits, currentLang)
            
            if (result is Resource.Success) {
                result.data?.let { weather ->
                    if (source == "settings") {
                        repository.setManualLocation(lat, lon)
                        repository.setLocationMode("map")
                        // Force update dashboard current weather
                        repository.refreshCurrentWeather(lat, lon, currentUnits, currentLang)
                    } else {
                        repository.addFavorite(
                            com.example.weatherapp.model.FavoriteLocation(
                                name = weather.cityName.ifBlank { "Selected Location" },
                                lat = weather.lat,
                                lon = weather.lon,
                                currentTemp = weather.temp,
                                condition = weather.description,
                                icon = weather.icon
                            )
                        )
                    }
                }
            }
        }
    }
}
