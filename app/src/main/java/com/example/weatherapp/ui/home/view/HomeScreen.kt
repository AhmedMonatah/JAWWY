package com.example.weatherapp.ui.home.view

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import java.util.Locale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.weatherapp.ui.components.background.WeatherEffects
import com.example.weatherapp.ui.home.viewmodel.HomeViewModel
import com.example.weatherapp.ui.components.home.DailyForecastSection
import com.example.weatherapp.ui.components.home.HeaderSection
import com.example.weatherapp.ui.components.home.HourlyForecastSection
import com.example.weatherapp.ui.components.home.TemperatureSection
import com.example.weatherapp.ui.components.background.WeatherStatsSection
import com.example.weatherapp.utils.weather.WeatherTypeUtil

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
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
        if (permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            viewModel.requestCurrentLocation()
        }
    }
    

    LaunchedEffect(lat, lon) {
        if (lat != null && lon != null) {
            viewModel.refreshWeather(lat, lon)
        } else {
            val hasFine = androidx.core.content.ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            val hasCoarse = androidx.core.content.ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED

            if (hasFine || hasCoarse) {
                viewModel.requestCurrentLocation()
            } else {
                val permissions = mutableListOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
                locationPermissionLauncher.launch(permissions.toTypedArray())
            }
        }
    }
    
    val hourlyForecast by viewModel.hourlyForecast.collectAsState(initial = emptyList())
    val refreshStatus by viewModel.refreshStatus.collectAsState()
    val currentLang by viewModel.language.collectAsState()
    val locale = remember(currentLang) { Locale(currentLang) }
    val isDark = isSystemInDarkTheme()
    val contentColor = if (isDark) Color.White else Color.Black

    var selectedDayIndex by remember { mutableStateOf(0) }
    val isDetailMode = lat != null
    
    val displayState = remember(currentWeather, forecast, selectedDayIndex, locale, refreshStatus, cityName) {
        computeDisplayState(currentWeather, forecast, selectedDayIndex, locale, refreshStatus, cityName)
    }

    val displayHourly = remember(hourlyForecast, selectedDayIndex, forecast) {
        filterHourlyForDay(hourlyForecast, selectedDayIndex, forecast)
    }

    val weatherType = remember(currentWeather) {
        WeatherTypeUtil.determineWeatherType(currentWeather?.description, currentWeather?.icon)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 25.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            HeaderSection(
                cityName = displayState.cityName,
                isDetailMode = isDetailMode,
                navController = navController,
                textColor = contentColor
            )
            
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
        
        // Weather effects overlay
        val currentTemp = currentWeather?.temp ?: 0.0
        val showSnow = weatherType == "snow" || currentTemp <= 0.0
        
        if (showSnow) {
            WeatherEffects(
                weatherType = "snow", modifier = Modifier.fillMaxSize()
            )
        } else if (weatherType == "rain" || weatherType.contains("thunder")) {
            WeatherEffects(
                weatherType = "rain", modifier = Modifier.fillMaxSize()
            )
        }
    }
}

