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
import android.annotation.SuppressLint
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AppRepository,
    private val locationClient: FusedLocationProviderClient
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

    private val _lastCoords = MutableStateFlow<Pair<Double, Double>?>(null)

    init {
        viewModelScope.launch {
            kotlinx.coroutines.flow.combine(
                repository.unitsFlow, 
                repository.languageFlow, 
                repository.locationModeFlow,
                repository.manualLocationFlow,
                _lastCoords
            ) { unit, lang, mode, manual, gps ->
                val coords = if (mode == "map") manual else gps
                DataInput(unit, lang, coords)
            }.collect { input ->
                input.coords?.let { (lat, lon) ->
                    val apiKey = Config.API_KEY
                    _refreshStatus.value = Resource.Loading<WeatherEntity>()
                    val result = repository.refreshCurrentWeather(lat, lon, apiKey, input.unit, input.lang)
                    _refreshStatus.value = result
                    if (result is Resource.Success<WeatherEntity>) {
                        result.data?.let { repository.refreshForecast(it.cityName, apiKey, input.unit, input.lang) }
                    }
                }
            }
        }

        // Trigger GPS if needed
        viewModelScope.launch {
            repository.locationModeFlow.collect { mode ->
                if (mode == "gps") {
                    requestCurrentLocation()
                }
            }
        }
    }

    private data class DataInput(val unit: String, val lang: String, val coords: Pair<Double, Double>?)

    @SuppressLint("MissingPermission")
    fun requestCurrentLocation() {
        viewModelScope.launch {
            // First attempt: current precise location
            locationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).addOnSuccessListener { location ->
                if (location != null) {
                    _lastCoords.value = location.latitude to location.longitude
                } else {
                    // Fallback attempt: last known location
                    locationClient.lastLocation.addOnSuccessListener { lastLoc ->
                        lastLoc?.let {
                            _lastCoords.value = it.latitude to it.longitude
                        } ?: run {
                            // Ultimate fallback to Cairo if all fails
                            _lastCoords.value = 30.0444 to 31.2357
                        }
                    }
                }
            }
        }
    }

    fun refreshWeather(lat: Double, lon: Double) {
        _lastCoords.value = lat to lon
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
