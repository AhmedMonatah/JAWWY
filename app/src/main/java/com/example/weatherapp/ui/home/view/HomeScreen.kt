package com.example.weatherapp.ui.home.view

import com.example.weatherapp.R
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.weatherapp.ui.components.OfflineBanner
import com.example.weatherapp.ui.home.viewmodel.HomeViewModel
import com.example.weatherapp.ui.home.view.components.DailyForecastSection
import com.example.weatherapp.ui.home.view.components.HeaderSection
import com.example.weatherapp.ui.home.view.components.HourlyForecastSection
import com.example.weatherapp.ui.home.view.components.TemperatureSection
import com.example.weatherapp.ui.home.view.components.WeatherStatsSection
import com.example.weatherapp.ui.theme.*
import com.example.weatherapp.utils.Resource
import com.example.weatherapp.utils.WeatherTypeUtil
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
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
    
    // Location permission handling
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
    
    // Collect state
    val hourlyForecast by viewModel.hourlyForecast.collectAsState(initial = emptyList())
    val refreshStatus by viewModel.refreshStatus.collectAsState()
    val currentLang by viewModel.language.collectAsState()
    val locale = remember(currentLang) { Locale(currentLang) }
    val isDark = isSystemInDarkTheme()
    val contentColor = if (isDark) Color.White else Color.Black

    // Day selection
    var selectedDayIndex by remember { mutableStateOf(0) }
    val isToday = selectedDayIndex == 0
    val isDetailMode = lat != null
    
    // Compute display data
    val displayState = remember(currentWeather, forecast, selectedDayIndex, locale, refreshStatus, cityName) {
        computeDisplayState(currentWeather, forecast, selectedDayIndex, locale, refreshStatus, cityName)
    }

    val displayHourly = remember(hourlyForecast, selectedDayIndex, forecast) {
        filterHourlyForDay(hourlyForecast, selectedDayIndex, forecast)
    }

    val weatherType = remember(currentWeather) {
        WeatherTypeUtil.determineWeatherType(currentWeather?.description, currentWeather?.icon)
    }

// UI Layout
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 25.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            if (!viewModel.isOnline()) {
                OfflineBanner()
            }

            // Notification Permission Banner
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                com.example.weatherapp.ui.home.view.components.NotificationBanner()
            }

            if (refreshStatus is Resource.Loading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().height(2.dp),
                    color = RamadanGold,
                    trackColor = Color.Transparent
                )
            }

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
            com.example.weatherapp.ui.components.WeatherEffects(
                weatherType = "snow", modifier = Modifier.fillMaxSize()
            )
        } else if (weatherType == "rain" || weatherType.contains("thunder")) {
            com.example.weatherapp.ui.components.WeatherEffects(
                weatherType = "rain", modifier = Modifier.fillMaxSize()
            )
        }
    }
}

// --- Helper data and functions ---

private data class HomeDisplayState(
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

private fun computeDisplayState(
    currentWeather: com.example.weatherapp.data.local.entity.WeatherEntity?,
    forecast: List<com.example.weatherapp.data.local.entity.ForecastEntity>,
    selectedDayIndex: Int,
    locale: Locale,
    refreshStatus: Resource<com.example.weatherapp.data.local.entity.WeatherEntity>?,
    cityNameParam: String?
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

    val date = SimpleDateFormat("EEE, MMM d", locale).format(Date())
    val time = SimpleDateFormat("h:mm a", locale).format(Date())

    return HomeDisplayState(cityName, temp, condition, date, time, humidity, pressure, wind, clouds)
}

private fun filterHourlyForDay(
    hourlyForecast: List<com.example.weatherapp.data.local.entity.HourlyForecastEntity>,
    selectedDayIndex: Int,
    forecast: List<com.example.weatherapp.data.local.entity.ForecastEntity>
): List<com.example.weatherapp.data.local.entity.HourlyForecastEntity> {
    val targetCal = Calendar.getInstance()
    if (selectedDayIndex != 0) {
        val targetDay = forecast.getOrNull(selectedDayIndex - 1)
        if (targetDay != null) {
            targetCal.timeInMillis = targetDay.dt * 1000
        }
    }
    val targetDayOfYear = targetCal.get(Calendar.DAY_OF_YEAR)
    val targetYear = targetCal.get(Calendar.YEAR)

    return hourlyForecast.filter {
        val cal = Calendar.getInstance().apply { timeInMillis = it.dt * 1000 }
        cal.get(Calendar.YEAR) == targetYear && cal.get(Calendar.DAY_OF_YEAR) == targetDayOfYear
    }.sortedBy { it.dt }
}
