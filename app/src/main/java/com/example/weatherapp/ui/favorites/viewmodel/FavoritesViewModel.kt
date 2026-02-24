package com.example.weatherapp.ui.favorites.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.FavoriteLocation
import com.example.weatherapp.data.repository.WeatherRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

sealed class FavoritesUiEvent {
    data class ShowUndoSnackbar(val deletedItems: List<FavoriteLocation>) : FavoritesUiEvent()
}

class FavoritesViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    val favorites: StateFlow<List<FavoriteLocation>> = repository.getFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedFavorites = MutableStateFlow<Set<FavoriteLocation>>(emptySet())
    val selectedFavorites = _selectedFavorites.asStateFlow()

    val units = repository.unitsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "metric")

    private val _uiEvents = MutableSharedFlow<FavoritesUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

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
            val currentUnits = units.value
            
            currentFavorites.forEach { location ->
                refreshFavorite(location, currentUnits, lang)
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

    fun toggleSelection(location: FavoriteLocation) {
        _selectedFavorites.value = if (_selectedFavorites.value.contains(location)) {
            _selectedFavorites.value - location
        } else {
            _selectedFavorites.value + location
        }
    }

    fun clearSelection() {
        _selectedFavorites.value = emptySet<FavoriteLocation>()
    }

    fun deleteSelectedFavorites() {
        val toDelete: List<FavoriteLocation> = _selectedFavorites.value.toList()
        viewModelScope.launch {
            for (location in toDelete) {
                repository.removeFavorite(location)
            }
            _uiEvents.emit(FavoritesUiEvent.ShowUndoSnackbar(toDelete))
            clearSelection()
        }
    }

    fun addFavorite(location: FavoriteLocation) {
        viewModelScope.launch {
            repository.addFavorite(location)
        }
    }

    fun isOnline() = repository.isOnline()
}
