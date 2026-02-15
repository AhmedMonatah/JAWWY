package com.example.weatherapp.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.local.entity.ForecastEntity
import com.example.weatherapp.data.local.entity.WeatherEntity
import com.example.weatherapp.data.repository.AppRepository
import com.example.weatherapp.utils.Config
import com.example.weatherapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _refreshStatus = MutableStateFlow<Resource<WeatherEntity>?>(null)
    val refreshStatus = _refreshStatus.asStateFlow()

    val currentWeather: StateFlow<WeatherEntity?> = repository.getCurrentWeather()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val forecast: StateFlow<List<ForecastEntity>> = repository.getForecast()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val units = repository.unitsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "metric")

    val language = repository.languageFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    fun refreshWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            _refreshStatus.value = Resource.Loading<WeatherEntity>()
            val apiKey = Config.API_KEY
            val currentUnits = units.value
            val currentLang = language.value

            val result = repository.refreshCurrentWeather(lat, lon, apiKey, currentUnits, currentLang)
            _refreshStatus.value = result
            
            if (result is Resource.Success<WeatherEntity>) {
                 // Trigger forecast refresh using the data from the success result
                 result.data?.cityName?.let { refreshForecast(it) }
            }
        }
    }

    fun refreshForecast(city: String) {
        viewModelScope.launch {
             val apiKey = Config.API_KEY
             val currentUnits = units.value
             val currentLang = language.value
             repository.refreshForecast(city, apiKey, currentUnits, currentLang)
        }
    }
}
