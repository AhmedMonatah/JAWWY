package com.example.weatherapp.observer

/**
 * DisplayElement interface (Head First Design Patterns style).
 * Components that display weather data should implement this
 * to provide a consistent display contract.
 */
interface DisplayElement {
    fun display(): Map<String, Any>
}
