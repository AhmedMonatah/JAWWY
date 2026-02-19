package com.example.weatherapp.model

data class HomeDisplayState(
    val cityName: String,
    val temp: Int,
    val condition: String,
    val date: String,
    val time: String,
    val humidity: Float,
    val pressure: Float,
    val wind: Float,
    val clouds: Int
)
