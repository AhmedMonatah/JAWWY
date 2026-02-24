package com.example.weatherapp

import android.app.Application
import com.example.weatherapp.di.AppContainerImpl
import com.example.weatherapp.di.IAppContainer

class WeatherApplication : Application() {

    lateinit var container: IAppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}
