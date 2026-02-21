package com.example.weatherapp.ui.main.view.viewmodel

import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val repository: AppRepository
) : ViewModel()