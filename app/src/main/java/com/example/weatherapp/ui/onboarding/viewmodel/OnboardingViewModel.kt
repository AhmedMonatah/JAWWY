package com.example.weatherapp.ui.onboarding.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repository.WeatherRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    val language = repository.languageFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    fun updateLanguage(lang: String) {
        viewModelScope.launch {
            repository.setLanguage(lang)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            repository.setOnboardingShown()
        }
    }
}
