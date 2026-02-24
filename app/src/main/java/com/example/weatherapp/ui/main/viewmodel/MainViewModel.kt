package com.example.weatherapp.ui.main.view.viewmodel

import androidx.lifecycle.ViewModel

import com.example.weatherapp.data.repository.WeatherRepository

class MainViewModel(
    val repository: WeatherRepository
) : ViewModel()