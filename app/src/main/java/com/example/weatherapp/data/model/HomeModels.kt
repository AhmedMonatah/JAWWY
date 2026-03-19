package com.example.weatherapp.data.model

data class HomeDisplayState(
    val cityName: String = "",
    val temp: Int = 0,
    val condition: String = "",
    val date: String = "",
    val time: String = "",
    val humidity: Float = 0f,
    val pressure: Float = 0f,
    val wind: Float = 0f,
    val clouds: Int = 0,
    val icon: String = ""
)
