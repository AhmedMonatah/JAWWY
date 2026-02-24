package com.example.weatherapp.ui.favorites.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.FavoriteLocation
import com.example.weatherapp.data.repository.WeatherRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    val favorites: StateFlow<List<FavoriteLocation>> = repository.getFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val connectivityFlow: Flow<Boolean> = repository.connectivityFlow

    init {
        viewModelScope.launch {
            repository.languageFlow.collect { lang ->
                refreshAllFavorites(lang)
            }
        }
    }

    private fun refreshAllFavorites(lang: String) {
        if (!repository.isOnline()) return
        
        viewModelScope.launch {
            val currentFavorites = favorites.value
            val units = repository.unitsFlow.stateIn(viewModelScope).value
            
            currentFavorites.forEach { location ->
                refreshFavorite(location, units, lang)
            }
        }
    }

    private suspend fun refreshFavorite(location: FavoriteLocation, units: String, lang: String) {
        val result = repository.fetchWeather(location.lat, location.lon, units, lang)
        if (result is com.example.weatherapp.utils.state.Resource.Success && result.data != null) {
            val updated = location.copy(
                name = result.data.cityName,
                currentTemp = result.data.temp,
                condition = result.data.description,
                icon = result.data.icon
            )
            repository.addFavorite(updated) // This will update because of PrimaryKey if it's not auto-gen 0
        }
    }

    fun removeFavorite(location: FavoriteLocation) {
        viewModelScope.launch {
            repository.removeFavorite(location)
        }
    }

    fun addFavorite(location: FavoriteLocation) {
        viewModelScope.launch {
            repository.addFavorite(location)
        }
    }

    fun isOnline() = repository.isOnline()
}
