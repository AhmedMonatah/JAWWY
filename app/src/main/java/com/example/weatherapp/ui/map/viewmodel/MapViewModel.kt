package com.example.weatherapp.ui.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repository.AppRepository
import com.example.weatherapp.utils.Config
import com.example.weatherapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: AppRepository
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
            val apiKey = Config.API_KEY
            val result = repository.fetchWeather(lat, lon, apiKey, currentUnits, currentLang)
            
            if (result is Resource.Success) {
                result.data?.let { weather ->
                    if (source == "settings") {
                        repository.setManualLocation(lat, lon)
                        repository.setLocationMode("map")
                    } else {
                        repository.addFavorite(
                            com.example.weatherapp.data.local.entity.FavoriteLocation(
                                name = weather.cityName,
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
