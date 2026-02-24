package com.example.weatherapp

import android.app.Application
import com.example.weatherapp.di.AppContainer
import com.example.weatherapp.di.AppContainerImpl

class WeatherApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}
