package com.example.weatherapp.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.ForecastEntity
import com.example.weatherapp.model.WeatherEntity
import com.example.weatherapp.model.HourlyForecastEntity
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.utils.state.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import android.annotation.SuppressLint
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationClient: FusedLocationProviderClient
) : ViewModel() {

    private val _refreshStatus = MutableStateFlow<Resource<WeatherEntity>?>(null)
    val refreshStatus = _refreshStatus.asStateFlow()

    val currentWeather: StateFlow<WeatherEntity?> = repository.getCurrentWeather()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val forecast: StateFlow<List<ForecastEntity>> = repository.getForecast()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hourlyForecast: StateFlow<List<HourlyForecastEntity>> = repository.getHourlyForecast()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val units = repository.unitsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "metric")

    val language = repository.languageFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    private val _lastCoords = MutableStateFlow<Pair<Double, Double>?>(null)
    private var isManualOverride = false

    init {

        viewModelScope.launch {
            combine(
                repository.unitsFlow,
                repository.languageFlow, 
                repository.locationModeFlow,
                repository.manualLocationFlow,
                _lastCoords
            ) { unit, lang, mode, manual, gps ->
                val coords = if (isManualOverride){
                gps
                }
                else if (mode == "map"){
                    manual
                } else {
                    gps
                }
                DataInput(unit, lang, coords)
            }.distinctUntilChanged { old, new ->
                old.coords == new.coords && old.unit == new.unit && old.lang == new.lang
            }.collect { input ->
                input.coords?.let { (lat, lon) ->
                    _refreshStatus.value = Resource.Loading()
                    
                    coroutineScope {
                        val currentDeferred = async { repository.refreshCurrentWeather(lat, lon, input.unit, input.lang) }
                        val hourlyDeferred = async { repository.refreshHourlyForecast(lat, lon, input.unit, input.lang) }
                        
                        val currentResult = currentDeferred.await()
                        _refreshStatus.value = currentResult
                        
                        if (currentResult is Resource.Success<WeatherEntity>) {
                            currentResult.data?.let { 
                                repository.refreshForecast(lat, lon, input.unit, input.lang)
                            }
                        }
                        hourlyDeferred.await()
                    }
                }
            }
        }

        viewModelScope.launch {
            repository.locationModeFlow.collect { mode ->
                if (mode == "gps" && !isManualOverride) {
                    requestCurrentLocation()
                }
            }
        }
    }

    private data class DataInput(val unit: String, val lang: String, val coords: Pair<Double, Double>?)

    @SuppressLint("MissingPermission")
    fun requestCurrentLocation() {
        if (isManualOverride) return
        
        viewModelScope.launch {
            locationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).addOnSuccessListener { location ->
                if (location != null && !isManualOverride) {
                    _lastCoords.value = location.latitude to location.longitude
                } else if (!isManualOverride) {
                    locationClient.lastLocation.addOnSuccessListener { lastLoc ->
                        if (lastLoc != null && !isManualOverride) {
                            _lastCoords.value = lastLoc.latitude to lastLoc.longitude
                        } else if (!isManualOverride) {
                            _lastCoords.value = 30.0444 to 31.2357
                        }
                    }
                }
            }
        }
    }

    fun refreshWeather(lat: Double, lon: Double) {
        isManualOverride = true
        _lastCoords.value = lat to lon
    }

    fun triggerManualRefresh() {
        viewModelScope.launch {
            val mode = repository.locationModeFlow.stateIn(viewModelScope).value
            val coords = if (mode == "map") {
                repository.manualLocationFlow.stateIn(viewModelScope).value
            } else {
                _lastCoords.value
            }

            if (coords != null) {
                _refreshStatus.value = Resource.Loading()
                val unit = repository.unitsFlow.stateIn(viewModelScope).value
                val lang = repository.languageFlow.stateIn(viewModelScope).value

                coroutineScope {
                    val currentDeferred = async { repository.refreshCurrentWeather(coords.first, coords.second, unit, lang) }
                    val hourlyDeferred = async { repository.refreshHourlyForecast(coords.first, coords.second, unit, lang) }

                    val currentResult = currentDeferred.await()
                    _refreshStatus.value = currentResult

                    if (currentResult is Resource.Success<WeatherEntity>) {
                        currentResult.data?.let {
                            repository.refreshForecast(coords.first, coords.second, unit, lang)
                        }
                    }
                    hourlyDeferred.await()
                }
            } else {
                requestCurrentLocation()
            }
        }
    }
}
