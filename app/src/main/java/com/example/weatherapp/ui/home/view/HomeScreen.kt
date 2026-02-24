package com.example.weatherapp.ui.home.view

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import java.util.Locale
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.di.LocalAppContainer
import androidx.navigation.NavController
import com.example.weatherapp.ui.home.view.components.WeatherEffects
import com.example.weatherapp.ui.home.viewmodel.HomeViewModel
import com.example.weatherapp.ui.components.AppPullToRefresh
import com.example.weatherapp.utils.state.Resource
import com.example.weatherapp.ui.home.view.components.DailyForecastSection
import com.example.weatherapp.ui.home.view.components.HeaderSection
import com.example.weatherapp.ui.home.view.components.HourlyForecastSection
import com.example.weatherapp.ui.home.view.components.TemperatureSection
import com.example.weatherapp.ui.home.view.components.WeatherStatsSection
import com.example.weatherapp.utils.home.computeDisplayState
import com.example.weatherapp.utils.home.filterHourlyForDay
import com.example.weatherapp.utils.weather.WeatherTypeUtil

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel(factory = LocalAppContainer.current.viewModelFactory),
    lat: Double? = null,
    lon: Double? = null,
    cityName: String? = null
) {
    val currentWeather by viewModel.currentWeather.collectAsState()
    val forecast by viewModel.forecast.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[ACCESS_FINE_LOCATION] == true ||
            permissions[ACCESS_COARSE_LOCATION] == true) {
            viewModel.requestCurrentLocation()
        }
    }
    

    LaunchedEffect(lat, lon) {
        if (lat != null && lon != null) {
            viewModel.refreshWeather(lat, lon)
        } else {
            val hasFine = checkSelfPermission(
                context, ACCESS_FINE_LOCATION
            ) == PERMISSION_GRANTED
            val hasCoarse = checkSelfPermission(
                context, ACCESS_COARSE_LOCATION
            ) == PERMISSION_GRANTED

            if (hasFine || hasCoarse) {
                viewModel.requestCurrentLocation()
            } else {
                val permissions = mutableListOf(
                    ACCESS_FINE_LOCATION,
                    ACCESS_COARSE_LOCATION
                )
                locationPermissionLauncher.launch(permissions.toTypedArray())
            }
        }
    }
    
    val hourlyForecast by viewModel.hourlyForecast.collectAsState(initial = emptyList())
    val refreshStatus by viewModel.refreshStatus.collectAsState()
    val currentLang by viewModel.language.collectAsState()
    val locale = remember(currentLang) { Locale(currentLang) }
    val isDark = true
    val contentColor = Color.White

    var selectedDayIndex by remember { mutableStateOf(0) }
    val isDetailMode = lat != null
    
    val displayState = remember(currentWeather, forecast, selectedDayIndex, locale, refreshStatus, cityName) {
        computeDisplayState(
            currentWeather,
            forecast,
            selectedDayIndex,
            locale,
            refreshStatus,
            cityName,
            currentWeather?.timezoneOffset ?: 0
        )
    }

    val displayHourly = remember(hourlyForecast, selectedDayIndex, forecast, currentWeather) {
        filterHourlyForDay(hourlyForecast, selectedDayIndex, forecast, currentWeather?.timezoneOffset ?: 0)
    }

    val weatherType = remember(currentWeather) {
        WeatherTypeUtil.determineWeatherType(currentWeather?.description, currentWeather?.icon)
    }

    val isRefreshing = refreshStatus is Resource.Loading<*>
    AppPullToRefresh(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.triggerManualRefresh() }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 25.dp)
            ) {

                HeaderSection(
                    cityName = displayState.cityName,
                    isDetailMode = isDetailMode,
                    navController = navController,
                    textColor = contentColor
                )

                Spacer(modifier = Modifier.height(15.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {

                    TemperatureSection(
                        temp = displayState.temp,
                        condition = displayState.condition,
                        date = displayState.date,
                        time = displayState.time,
                        textColor = contentColor,
                        weatherType = weatherType
                    )

                    DailyForecastSection(
                        forecast = forecast.take(7),
                        currentWeather = currentWeather,
                        selectedIndex = selectedDayIndex,
                        onDaySelected = { selectedDayIndex = it },
                        isDark = isDark,
                        locale = locale
                    )

                    if (displayHourly.isNotEmpty()) {
                        HourlyForecastSection(displayHourly, locale, isDark)
                    }

                    WeatherStatsSection(
                        pressure = displayState.pressure,
                        humidity = displayState.humidity,
                        wind = displayState.wind,
                        clouds = displayState.clouds
                    )

                    Spacer(modifier = Modifier.height(50.dp))
                }
            }

            val currentTemp = currentWeather?.temp ?: 0.0
            val showSnow = weatherType == "snow" || currentTemp <= 0.0

            if (showSnow) {
                WeatherEffects(
                    weatherType = "snow",
                    modifier = Modifier.fillMaxSize()
                )
            } else if (weatherType == "rain" || weatherType.contains("thunder")) {
                WeatherEffects(
                    weatherType = "rain",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}