package com.example.weatherapp.di

import android.content.Context
import androidx.room.Room
import com.example.weatherapp.data.local.LocalDataSource
import com.example.weatherapp.data.local.LocalDataSourceImpl
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.local.dao.AlertDao
import com.example.weatherapp.data.local.dao.WeatherDao
import com.example.weatherapp.data.remote.RemoteDataSource
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.data.repository.AppRepository
import com.example.weatherapp.data.repository.WeatherRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModelProvider

val LocalAppContainer = staticCompositionLocalOf<AppContainer> {
    error("No AppContainer provided")
}

/**
 * Dependency injection container at the application level.
 */
interface AppContainer {
    val weatherRepository: WeatherRepository
    val locationClient: FusedLocationProviderClient
    val viewModelFactory: ViewModelProvider.Factory
}

/**
 * Implementation of the AppContainer that provides dependencies for the application.
 */
class AppContainerImpl(private val context: Context) : AppContainer {

    private val database: WeatherDatabase by lazy {
        Room.databaseBuilder(
            context,
            WeatherDatabase::class.java,
            "weather_database"
        ).build()
    }

    private val weatherDao: WeatherDao by lazy {
        database.weatherDao()
    }

    private val alertDao: AlertDao by lazy {
        database.alertDao()
    }

    private val weatherApi: WeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }

    private val remoteDataSource: RemoteDataSource by lazy {
        RemoteDataSourceImpl(weatherApi)
    }

    private val localDataSource: LocalDataSource by lazy {
        LocalDataSourceImpl(context, database)
    }

    override val weatherRepository: WeatherRepository by lazy {
        AppRepository(context, remoteDataSource, localDataSource)
    }

    override val locationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    override val viewModelFactory: ViewModelProvider.Factory by lazy {
        com.example.weatherapp.ui.ViewModelFactory(this, context)
    }
}
