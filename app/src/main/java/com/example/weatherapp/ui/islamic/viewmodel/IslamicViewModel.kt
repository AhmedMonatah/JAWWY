package com.example.weatherapp.ui.islamic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.WeatherEntity
import com.example.weatherapp.data.repository.AppRepository
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
    val isLoading: Boolean = false
)

@HiltViewModel
class IslamicViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)

    val uiState: StateFlow<IslamicScreenState> = combine(
        repository.getCurrentWeather(),
        _isLoading
    ) { weather, loading ->
        if (weather != null) {
            val prayerTimes = PrayerTimesManager.getPrayerTimes(weather.lat, weather.lon)
            val islamicInfo = PrayerTimesManager.getIslamicDateInfo()
            val nextPrayer = prayerTimes.find { it.isNext }
            IslamicScreenState(
                prayerTimes = prayerTimes,
                islamicInfo = islamicInfo,
                nextPrayer = nextPrayer,
                isLoading = loading
            )
        } else {
            IslamicScreenState(isLoading = loading)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = IslamicScreenState(isLoading = true)
    )
}
