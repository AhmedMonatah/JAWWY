package com.example.weatherapp.ui.favorites.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.FavoriteLocation
import com.example.weatherapp.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    val favorites: StateFlow<List<FavoriteLocation>> = repository.getFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

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
