package com.example.weatherapp.di

import android.content.Context
import androidx.room.Room
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.local.dao.AlertDao
import com.example.weatherapp.data.local.dao.WeatherDao
import com.example.weatherapp.data.remote.WeatherApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWeatherDatabase(@ApplicationContext context: Context): WeatherDatabase {
        return Room.databaseBuilder(
            context,
            WeatherDatabase::class.java,
            "weather_db"
        ).fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideWeatherDao(db: WeatherDatabase): WeatherDao {
        return db.weatherDao()
    }

    @Provides
    @Singleton
    fun provideAlertDao(db: WeatherDatabase): AlertDao {
        return db.alertDao()
    }

    @Provides
    @Singleton
    fun provideWeatherApi(): WeatherApi {
        val apiKeyInterceptor = okhttp3.Interceptor { chain ->
            val original = chain.request()
            val originalHttpUrl = original.url
            val url = originalHttpUrl.newBuilder()
                .addQueryParameter("appid", BuildConfig.API_KEY)
                .build()
            val requestBuilder = original.newBuilder().url(url)
            chain.proceed(requestBuilder.build())
        }

        val client = okhttp3.OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun provideRemoteDataSource(api: WeatherApi): com.example.weatherapp.data.remote.RemoteDataSource {
        return com.example.weatherapp.data.remote.RemoteDataSourceImpl(api)
    }

    @Provides
    @Singleton
    fun provideLocalDataSource(@ApplicationContext context: Context, db: WeatherDatabase): com.example.weatherapp.data.local.LocalDataSource {
        return com.example.weatherapp.data.local.LocalDataSourceImpl(context, db)
    }
}
