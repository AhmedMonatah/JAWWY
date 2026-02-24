package com.example.weatherapp.di

import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.data.repository.WeatherRepository
import com.google.android.gms.location.FusedLocationProviderClient

interface IAppContainer {
        val weatherRepository: WeatherRepository
        val locationClient: FusedLocationProviderClient
        val viewModelFactory: ViewModelProvider.Factory
}