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
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.example.weatherapp.BuildConfig
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.ui.ViewModelFactory

val LocalAppContainer = staticCompositionLocalOf<IAppContainer> {
    error("No AppContainer provided")
}


class AppContainerImpl(private val context: Context) : IAppContainer {

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

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val url = original.url.newBuilder()
                    .addQueryParameter("appid", BuildConfig.API_KEY)
                    .build()
                chain.proceed(original.newBuilder().url(url).build())
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()
    }

    private val weatherApi: WeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }

    private val remoteDataSource: RemoteDataSource by lazy {
        RemoteDataSourceImpl(weatherApi)
    }

    private val localDataSource: LocalDataSource by lazy {
        LocalDataSourceImpl(context, weatherDao, alertDao, database.favoriteDao())
    }

    override val weatherRepository: WeatherRepository by lazy {
        AppRepository(context, remoteDataSource, localDataSource)
    }

    override val locationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    override val viewModelFactory: ViewModelProvider.Factory by lazy {
        ViewModelFactory(this, context)
    }
}
