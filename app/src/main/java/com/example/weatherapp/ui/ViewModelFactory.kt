package com.example.weatherapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.ui.alerts.viewmodel.AlertsViewModel
import com.example.weatherapp.ui.favorites.viewmodel.FavoritesViewModel
import com.example.weatherapp.ui.home.viewmodel.HomeViewModel
import com.example.weatherapp.ui.settings.viewmodel.SettingsViewModel
import com.example.weatherapp.ui.onboarding.viewmodel.OnboardingViewModel
import com.example.weatherapp.ui.map.viewmodel.MapViewModel
import com.example.weatherapp.ui.main.view.viewmodel.MainViewModel
import android.content.Context
import com.example.weatherapp.di.IAppContainer

class ViewModelFactory(
    private val appContainer: IAppContainer,
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(appContainer.weatherRepository, appContainer.locationClient) as T
            }
            modelClass.isAssignableFrom(AlertsViewModel::class.java) -> {
                AlertsViewModel(appContainer.weatherRepository, context) as T
            }
            modelClass.isAssignableFrom(FavoritesViewModel::class.java) -> {
                FavoritesViewModel(appContainer.weatherRepository) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(appContainer.weatherRepository) as T
            }

            modelClass.isAssignableFrom(OnboardingViewModel::class.java) -> {
                OnboardingViewModel(appContainer.weatherRepository) as T
            }
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                MapViewModel(appContainer.weatherRepository) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(appContainer.weatherRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
