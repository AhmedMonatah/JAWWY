package com.example.weatherapp.ui.favorites.viewmodel

import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    private val _favorites = kotlinx.coroutines.flow.MutableStateFlow(com.example.weatherapp.model.FakeData.favorites)
    val favorites = _favorites.asStateFlow()

    fun removeFavorite(location: com.example.weatherapp.model.FavoriteLocation) {
        _favorites.value = _favorites.value.filter { it.id != location.id }
    }

    fun addFavorite(location: com.example.weatherapp.model.FavoriteLocation) {
        _favorites.value = _favorites.value + location
    }
}
