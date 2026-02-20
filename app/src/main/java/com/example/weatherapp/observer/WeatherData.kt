package com.example.weatherapp.observer

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class WeatherData @Inject constructor() : Subject {

    private val observers = arrayListOf<Observer>()

    // Current measurements
    var temperature: Double = 0.0
        private set
    var humidity: Int = 0
        private set
    var pressure: Int = 0
        private set
    var windSpeed: Double = 0.0
        private set
    var clouds: Int = 0
        private set
    var description: String = ""
        private set
    var cityName: String = ""
        private set
    var icon: String = ""
        private set

    // Compose-friendly reactive state
    private val _state = MutableStateFlow(WeatherDataSnapshot())
    val state: StateFlow<WeatherDataSnapshot> = _state.asStateFlow()

    override fun registerObserver(observer: Observer) {
        if (!observers.contains(observer)) {
            observers.add(observer)
        }
    }

    override fun removeObserver(observer: Observer) {
        observers.remove(observer)
    }

    override fun notifyObservers() {
        observers.forEach { observer ->
            observer.update(
                temperature, humidity, pressure,
                windSpeed, clouds, description, cityName, icon
            )
        }
    }


    fun setMeasurements(
        temperature: Double,
        humidity: Int,
        pressure: Int,
        windSpeed: Double,
        clouds: Int,
        description: String,
        cityName: String,
        icon: String
    ) {
        this.temperature = temperature
        this.humidity = humidity
        this.pressure = pressure
        this.windSpeed = windSpeed
        this.clouds = clouds
        this.description = description
        this.cityName = cityName
        this.icon = icon

        _state.value = WeatherDataSnapshot(
            temperature = temperature,
            humidity = humidity,
            pressure = pressure,
            windSpeed = windSpeed,
            clouds = clouds,
            description = description,
            cityName = cityName,
            icon = icon
        )

        notifyObservers()
    }
}


data class WeatherDataSnapshot(
    val temperature: Double = 0.0,
    val humidity: Int = 0,
    val pressure: Int = 0,
    val windSpeed: Double = 0.0,
    val clouds: Int = 0,
    val description: String = "",
    val cityName: String = "",
    val icon: String = ""
)
