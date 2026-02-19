package com.example.weatherapp.observer

/**
 * Observer interface (Head First Design Patterns style).
 * Any display element that wants to receive weather updates must implement this.
 */
interface Observer {
    fun update(
        temperature: Double,
        humidity: Int,
        pressure: Int,
        windSpeed: Double,
        clouds: Int,
        description: String,
        cityName: String,
        icon: String
    )
}
