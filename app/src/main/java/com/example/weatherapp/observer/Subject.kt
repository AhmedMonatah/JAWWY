package com.example.weatherapp.observer

/**
 * Subject interface (Head First Design Patterns style).
 * The weather data source implements this to manage observers.
 */
interface Subject {
    fun registerObserver(observer: Observer)
    fun removeObserver(observer: Observer)
    fun notifyObservers()
}
