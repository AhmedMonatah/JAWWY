package com.example.weatherapp.utils.home

import com.example.weatherapp.model.ForecastEntity
import com.example.weatherapp.model.HourlyForecastEntity
import com.example.weatherapp.model.WeatherEntity
import com.example.weatherapp.model.HomeDisplayState
import com.example.weatherapp.utils.state.Resource
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

fun computeDisplayState(
    currentWeather: WeatherEntity?,
    forecast: List<ForecastEntity>,
    selectedDayIndex: Int,
    locale: Locale,
    refreshStatus: Resource<WeatherEntity>?,
    cityNameParam: String?,
    timezoneOffset: Int = 0
): HomeDisplayState {
    val isToday = selectedDayIndex == 0
    
    val temps = (currentWeather?.temp?.roundToInt()?.let { listOf(it) } ?: listOf(0)) +
            forecast.take(7).map { it.tempDay.roundToInt() }
    val temp = temps.getOrElse(selectedDayIndex) { 0 }

    val condition = if (isToday) {
        currentWeather?.description?.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(locale) else it.toString()
        } ?: "..."
    } else {
        forecast.getOrNull(selectedDayIndex - 1)?.description?.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(locale) else it.toString()
        } ?: "..."
    }

    val humidity = (currentWeather?.humidity ?: 0).toFloat()
    val pressure = (currentWeather?.pressure ?: 0).toFloat()
    val wind = (currentWeather?.windSpeed ?: 0.0).toFloat()
    val clouds = if (isToday) (currentWeather?.clouds ?: 0) else 15

    val cityName = if (refreshStatus is Resource.Loading && currentWeather == null) {
        cityNameParam ?: "Loading..."
    } else {
        currentWeather?.cityName ?: cityNameParam ?: "..."
    }

    val utcTime = System.currentTimeMillis()
    val localTime = utcTime + (timezoneOffset * 1000L)
    val displayDate = Date(localTime)

    val dateFormat = SimpleDateFormat("EEE, MMM d", locale).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    val timeFormat = SimpleDateFormat("h:mm a", locale).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    val date = dateFormat.format(displayDate)
    val time = timeFormat.format(displayDate)

    return HomeDisplayState(cityName, temp, condition, date, time, humidity, pressure, wind, clouds)
}

fun filterHourlyForDay(
    hourlyForecast: List<HourlyForecastEntity>,
    selectedDayIndex: Int,
    forecast: List<ForecastEntity>,
    timezoneOffset: Int = 0
): List<HourlyForecastEntity> {
    val utcZone = TimeZone.getTimeZone("UTC")
    val targetCal = Calendar.getInstance(utcZone)
    val currentUtcTime = System.currentTimeMillis()
    targetCal.timeInMillis = currentUtcTime + (timezoneOffset * 1000L)
    if (selectedDayIndex != 0) {
        val targetDay = forecast.getOrNull(selectedDayIndex - 1)
        if (targetDay != null) {
            targetCal.timeInMillis = targetDay.dt * 1000
        }
    }
    val targetDayOfYear = targetCal.get(Calendar.DAY_OF_YEAR)
    val targetYear = targetCal.get(Calendar.YEAR)

    return hourlyForecast.filter {
        val cal = Calendar.getInstance(utcZone).apply { timeInMillis = (it.dt + timezoneOffset) * 1000L }
        cal.get(Calendar.YEAR) == targetYear && cal.get(Calendar.DAY_OF_YEAR) == targetDayOfYear
    }.sortedBy { it.dt }
}
