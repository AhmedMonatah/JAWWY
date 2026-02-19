package com.example.weatherapp.ui.onboarding.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    fun completeOnboarding() {
        viewModelScope.launch {
            repository.setOnboardingShown()
        }
    }
}
