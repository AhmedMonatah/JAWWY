package com.example.weatherapp.ui.onboarding.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repository.WeatherRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed class OnboardingUiEvent {
    data class ScrollToPage(val page: Int) : OnboardingUiEvent()
    object Finish : OnboardingUiEvent()
}

class OnboardingViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    val language = repository.languageFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    private val _uiEvents = MutableSharedFlow<OnboardingUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    private var currentPage = 0

    fun updateLanguage(lang: String) {
        viewModelScope.launch {
            repository.setLanguage(lang)
        }
    }

    fun updateCurrentPage(page: Int) {
        currentPage = page
    }

    fun onNextClicked() {
        viewModelScope.launch {
            if (currentPage == 2) {
                repository.setOnboardingShown()
                _uiEvents.emit(OnboardingUiEvent.Finish)
            } else {
                _uiEvents.emit(OnboardingUiEvent.ScrollToPage(currentPage + 1))
            }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            repository.setOnboardingShown()
        }
    }
}
