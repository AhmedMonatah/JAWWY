package com.example.weatherapp.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.model.ForecastEntity
import com.example.weatherapp.data.model.WeatherEntity
import com.example.weatherapp.data.model.HourlyForecastEntity
import com.example.weatherapp.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import android.util.Log
import android.annotation.SuppressLint
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.combine
import com.example.weatherapp.utils.state.Resource
import com.example.weatherapp.data.model.HomeDisplayState
import com.example.weatherapp.utils.home.computeDisplayState
import com.example.weatherapp.utils.home.filterHourlyForDay
import com.example.weatherapp.utils.weather.WeatherTypeUtil
import kotlinx.coroutines.delay
import java.util.Locale
import kotlinx.coroutines.delay

data class HomeUiState(
    val displayState: HomeDisplayState = HomeDisplayState(),
    val displayHourly: List<HourlyForecastEntity> = emptyList(),
    val weatherType: String = "clear",
    val isRefreshing: Boolean = false,
    val selectedDayIndex: Int = 0,
    val currentLang: String = "en",
    val showSnow: Boolean = false,
    val showRain: Boolean = false
)

class HomeViewModel(
    private val repository: WeatherRepository,
    private val locationClient: FusedLocationProviderClient
) : ViewModel() {

    private val _refreshStatus = MutableStateFlow<Resource<Unit>?>(null)
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

    val locationMode = repository.locationModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "gps")

    val manualLocation = repository.manualLocationFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _selectedDayIndex = MutableStateFlow(0)
    val selectedDayIndex = _selectedDayIndex.asStateFlow()

    val uiState: StateFlow<HomeUiState> = combine(
        currentWeather,
        forecast,
        hourlyForecast,
        language,
        refreshStatus,
        _selectedDayIndex
    ) { args ->
        val currentTyped = args[0] as? WeatherEntity
        @Suppress("UNCHECKED_CAST")
        val dailyTyped = args[1] as List<ForecastEntity>
        @Suppress("UNCHECKED_CAST")
        val hourlyTyped = args[2] as List<HourlyForecastEntity>
        val langTyped = args[3] as String
        @Suppress("UNCHECKED_CAST")
        val statusTyped = args[4] as? Resource<Unit>
        val indexTyped = args[5] as Int

        val locale = Locale(langTyped)
        val timezoneOffset = currentTyped?.timezoneOffset ?: 0
        
        val displayState = computeDisplayState(
            currentTyped, dailyTyped, indexTyped, locale, statusTyped, null, timezoneOffset
        )
        
        val displayHourly = filterHourlyForDay(hourlyTyped, indexTyped, dailyTyped, timezoneOffset)
        
        val weatherType = WeatherTypeUtil.determineWeatherType(displayState.condition, displayState.icon)
        
        val isRefreshing = statusTyped is Resource.Loading<*>
        
        val showSnow = weatherType == "snow" || displayState.temp <= 0.0
        val showRain = weatherType == "rain" || weatherType.contains("thunder")

        HomeUiState(
            displayState = displayState,
            displayHourly = displayHourly,
            weatherType = weatherType,
            isRefreshing = isRefreshing,
            selectedDayIndex = indexTyped,
            currentLang = langTyped,
            showSnow = showSnow,
            showRain = showRain
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    private val _lastCoords = MutableStateFlow<Pair<Double, Double>?>(null)
    private val _isManualOverride = MutableStateFlow(false)

    init {

        viewModelScope.launch {
            combine(
                combine(repository.unitsFlow, repository.languageFlow, repository.locationModeFlow) { u, l, m -> Triple(u, l, m) },
                combine(repository.manualLocationFlow, _lastCoords, _isManualOverride) { m, g, o -> Triple(m, g, o) }
            ) { (unit, lang, mode), (manual, gps, isOverride) ->
                val coords = if (isOverride){
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
                    
                    viewModelScope.launch {
                        try {
                            _refreshStatus.value = Resource.Loading()
                            repository.refreshCurrentWeather(lat, lon, input.unit, input.lang)
                            repository.refreshHourlyForecast(lat, lon, input.unit, input.lang)
                            repository.refreshForecast(lat, lon, input.unit, input.lang)
                            _refreshStatus.value = Resource.Success(Unit)
                        } catch (e: Exception) {
                            Log.e("HomeVM", "Error refreshing weather", e)
                            _refreshStatus.value = Resource.Error(e.message ?: "Unknown error")
                        }
                    }
                }
            }
        }

        viewModelScope.launch {
            combine(repository.locationModeFlow, _isManualOverride) { mode, isOverride ->
                mode == "gps" && !isOverride
            }.collect { shouldRequest ->
                if (shouldRequest) {
                    requestCurrentLocation()
                }
            }
        }
    }

    private data class DataInput(val unit: String, val lang: String, val coords: Pair<Double, Double>?)

    @SuppressLint("MissingPermission")
    fun requestCurrentLocation() {
        if (_isManualOverride.value || locationMode.value == "map") return
        
        if (_lastCoords.value != null && currentWeather.value != null) {
            return
        }

        viewModelScope.launch {
            locationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).addOnSuccessListener { location ->
                if (location != null && !_isManualOverride.value) {
                    _lastCoords.value = location.latitude to location.longitude
                } else if (!_isManualOverride.value) {
                    locationClient.lastLocation.addOnSuccessListener { lastLoc ->
                        if (lastLoc != null && !_isManualOverride.value) {
                            _lastCoords.value = lastLoc.latitude to location.longitude
                        } else if (!_isManualOverride.value) {
                            _lastCoords.value = 30.0444 to 31.2357
                        }
                    }
                }
            }
        }
    }

    fun refreshWeather(lat: Double, lon: Double) {
        _isManualOverride.value = true
        _lastCoords.value = lat to lon
    }

    fun resetOverride() {
        _isManualOverride.value = false
        _lastCoords.value = null
    }

    fun selectDay(index: Int) {
        _selectedDayIndex.value = index
    }

    fun triggerManualRefresh() {
        viewModelScope.launch {
            delay(100)
            
            val isManual = _isManualOverride.value
            val mode = locationMode.value
            val manualCoords = manualLocation.value
            val gpsCoords = _lastCoords.value

            val coords = if (isManual) {
                gpsCoords
            } else if (mode == "map") {
                manualCoords
            } else {
                gpsCoords
            }

            Log.d("HomeVM", "Refresh triggered: mode=$mode, isManual=$isManual, coords=$coords")

            if (coords != null) {
                _refreshStatus.value = Resource.Loading()
                val unit = units.value
                val lang = language.value

                viewModelScope.launch {
                    try {
                        repository.refreshCurrentWeather(coords.first, coords.second, unit, lang)
                        repository.refreshHourlyForecast(coords.first, coords.second, unit, lang)
                        repository.refreshForecast(coords.first, coords.second, unit, lang)
                        _refreshStatus.value = Resource.Success(Unit)
                    } catch (e: Exception) {
                        Log.e("HomeVM", "Manual refresh failed", e)
                        _refreshStatus.value = Resource.Error(e.message ?: "Refresh failed")
                    }
                }
            } else {
                requestCurrentLocation()
            }
        }
    }
}
