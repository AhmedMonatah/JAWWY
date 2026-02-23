package com.example.weatherapp.ui.islamic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.WeatherEntity
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.utils.state.Resource
import com.example.weatherapp.utils.islamic.IslamicDateInfo
import com.example.weatherapp.utils.islamic.PrayerTimeInfo
import com.example.weatherapp.utils.islamic.PrayerTimesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class IslamicScreenState(
    val prayerTimes: List<PrayerTimeInfo> = emptyList(),
    val islamicInfo: IslamicDateInfo? = null,
    val nextPrayer: PrayerTimeInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class IslamicViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationClient: com.google.android.gms.location.FusedLocationProviderClient
) : ViewModel() {

    private val _gpsCoords = MutableStateFlow<Pair<Double, Double>?>(null)

    init {
        requestLocation()
    }

    @android.annotation.SuppressLint("MissingPermission")
    private fun requestLocation() {
        locationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                _gpsCoords.value = location.latitude to location.longitude
            } else {
                // Fallback to Cairo if everything fails
                _gpsCoords.value = 30.0444 to 31.2357
            }
        }
    }

    val uiState: StateFlow<IslamicScreenState> = combine(
        repository.getCurrentWeather(),
        repository.locationModeFlow,
        repository.manualLocationFlow,
        _gpsCoords
    ) { weather, mode, manual, gps ->
        val coords = if (mode == "map") manual else (gps ?: weather?.let { it.lat to it.lon })
        
        if (coords != null) {
            val (lat, lon) = coords
            val prayerTimes = PrayerTimesManager.getPrayerTimes(
                lat = lat,
                lon = lon,
                countryCode = weather?.countryCode ?: "EG",
                timezoneOffsetSeconds = weather?.timezoneOffset ?: 0
            )
            val islamicInfo = PrayerTimesManager.getIslamicDateInfo()
            val nextPrayer = prayerTimes.find { it.isNext }
            IslamicScreenState(
                prayerTimes = prayerTimes,
                islamicInfo = islamicInfo,
                nextPrayer = nextPrayer,
                isLoading = false
            )
        } else {
            IslamicScreenState(isLoading = false)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = IslamicScreenState(isLoading = true)
    )
}
