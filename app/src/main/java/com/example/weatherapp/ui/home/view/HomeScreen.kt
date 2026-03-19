package com.example.weatherapp.ui.home.view

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import java.util.Locale
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelStoreOwner
import com.example.weatherapp.di.LocalAppContainer
import androidx.navigation.NavController
import com.example.weatherapp.ui.home.view.components.WeatherEffects
import com.example.weatherapp.ui.home.viewmodel.HomeViewModel
import com.example.weatherapp.ui.components.AppPullToRefresh
import com.example.weatherapp.ui.home.view.components.DailyForecastSection
import com.example.weatherapp.ui.home.view.components.HeaderSection
import com.example.weatherapp.ui.home.view.components.HourlyForecastSection
import com.example.weatherapp.ui.home.view.components.TemperatureSection
import com.example.weatherapp.ui.home.view.components.WeatherStatsSection
import com.example.weatherapp.ui.theme.LocalIsDark
import com.example.weatherapp.ui.components.LocationPermissionDialog

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel(
        viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner,
        factory = LocalAppContainer.current.viewModelFactory
    ),
    lat: Double? = null,
    lon: Double? = null,
    cityName: String? = null
) {
    val currentWeather by viewModel.currentWeather.collectAsState()
    val forecast by viewModel.forecast.collectAsState()
    val context = LocalContext.current
    
    val showLocationDialog = remember { mutableStateOf(false) }
    val isDetailMode = lat != null

    HandleLocationPermissionsAndRefresh(
        lat = lat,
        lon = lon,
        viewModel = viewModel,
        context = context,
        showLocationDialog = showLocationDialog
    )

    if (isDetailMode) {
        DisposableEffect(Unit) {
            onDispose {
                viewModel.resetOverride()
            }
        }
    }
    
    val uiState by viewModel.uiState.collectAsState()

    val isDark = LocalIsDark.current
    val contentColor = MaterialTheme.colorScheme.onBackground

    val locale = remember(uiState.currentLang) { Locale(uiState.currentLang) }

    AppPullToRefresh(
        isRefreshing = uiState.isRefreshing,
        onRefresh = { viewModel.triggerManualRefresh() }
    ) {
        Box(modifier = Modifier.fillMaxSize() .background(Color.Transparent) ) {

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
                        iconCode = uiState.displayState.icon
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

    if (showLocationDialog.value && !isDetailMode) {
        LocationPermissionDialog(onDismiss = { showLocationDialog.value = false })
    }
}
