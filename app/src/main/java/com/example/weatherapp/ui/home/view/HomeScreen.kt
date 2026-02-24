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
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    
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
    
    val uiState by viewModel.uiState.collectAsState()

    val isDark = true
    val contentColor = Color.White
    val isDetailMode = lat != null
    
    val locale = remember(uiState.currentLang) { Locale(uiState.currentLang) }

    AppPullToRefresh(
        isRefreshing = uiState.isRefreshing,
        onRefresh = { viewModel.triggerManualRefresh() }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 25.dp)
            ) {

                HeaderSection(
                    cityName = uiState.displayState.cityName,
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
                        temp = uiState.displayState.temp,
                        condition = uiState.displayState.condition,
                        date = uiState.displayState.date,
                        time = uiState.displayState.time,
                        textColor = contentColor,
                        weatherType = uiState.weatherType
                    )

                    DailyForecastSection(
                        forecast = forecast.take(7),
                        currentWeather = currentWeather,
                        selectedIndex = uiState.selectedDayIndex,
                        onDaySelected = { viewModel.selectDay(it) },
                        isDark = isDark,
                        locale = locale
                    )

                    if (uiState.displayHourly.isNotEmpty()) {
                        HourlyForecastSection(uiState.displayHourly, locale, isDark)
                    }

                    WeatherStatsSection(
                        pressure = uiState.displayState.pressure,
                        humidity = uiState.displayState.humidity,
                        wind = uiState.displayState.wind,
                        clouds = uiState.displayState.clouds
                    )

                    Spacer(modifier = Modifier.height(50.dp))
                }
            }

            if (uiState.showSnow) {
                WeatherEffects(
                    weatherType = "snow",
                    modifier = Modifier.fillMaxSize()
                )
            } else if (uiState.showRain) {
                WeatherEffects(
                    weatherType = "rain",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
