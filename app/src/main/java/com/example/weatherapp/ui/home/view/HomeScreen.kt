package com.example.weatherapp.ui.home.view

import com.example.weatherapp.R
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.weatherapp.ui.components.OfflineBanner
import com.example.weatherapp.ui.components.StatArcCard
import com.example.weatherapp.ui.components.WeatherBackground
import com.example.weatherapp.ui.home.viewmodel.HomeViewModel
import com.example.weatherapp.data.local.entity.HourlyForecastEntity
import com.example.weatherapp.ui.theme.*
import com.example.weatherapp.utils.Resource
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
            // Dashboard mode: Request location
            locationPermissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
    
    val hourlyForecast by viewModel.hourlyForecast.collectAsState(initial = emptyList())
    
    var selectedDayIndex by remember { mutableStateOf(0) }
    
    // Filter hourly forecast based on SELECTED day
    val displayHourly = remember(hourlyForecast, selectedDayIndex, forecast) {
        val targetCal = Calendar.getInstance()
        if (selectedDayIndex != 0) {
            val targetDay = forecast.getOrNull(selectedDayIndex - 1)
            if (targetDay != null) {
                targetCal.timeInMillis = targetDay.dt * 1000
            }
        }
        
        val targetDayOfYear = targetCal.get(Calendar.DAY_OF_YEAR)
        val targetYear = targetCal.get(Calendar.YEAR)
        
        hourlyForecast.filter { 
            val cal = Calendar.getInstance().apply { timeInMillis = it.dt * 1000 }
            cal.get(Calendar.YEAR) == targetYear && cal.get(Calendar.DAY_OF_YEAR) == targetDayOfYear
        }.sortedBy { it.dt }
    }

    val isDetailMode = lat != null
    
    val currentLang by viewModel.language.collectAsState()
    val locale = remember(currentLang) { Locale(currentLang) }
    
    // Combine Today + 7 Days Forecast
    val days = listOf(stringResource(R.string.today)) + forecast.take(7).map { 
        SimpleDateFormat("EEE", locale).format(Date(it.dt * 1000)) 
    }
    val temps = (currentWeather?.temp?.roundToInt()?.let { listOf(it) } ?: listOf(0)) + 
                forecast.take(7).map { it.tempDay.roundToInt() }

    val isToday = selectedDayIndex == 0
    val displayTemp = temps.getOrElse(selectedDayIndex) { 0 }
    
    val displayCondition = if (isToday) {
        currentWeather?.description?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() } ?: "..."
    } else {
        forecast.getOrNull(selectedDayIndex - 1)?.description?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() } ?: "..."
    }

    val displayHumidity = if (isToday) {
        (currentWeather?.humidity ?: 0).toFloat()
    } else {
        (currentWeather?.humidity ?: 0).toFloat()
    }

    val displayPressure = if (isToday) {
        (currentWeather?.pressure ?: 0).toFloat()
    } else {
        (currentWeather?.pressure ?: 0).toFloat()
    }

    val displayWind = if (isToday) {
        (currentWeather?.windSpeed ?: 0.0).toFloat()
    } else {
        (currentWeather?.windSpeed ?: 0.0).toFloat()
    }

    val displayClouds = if (isToday) {
        (currentWeather?.clouds ?: 0)
    } else {
        15 // Fallback
    }
    val refreshStatus by viewModel.refreshStatus.collectAsState()

    val cityNameDisplay = if (refreshStatus is Resource.Loading && currentWeather == null) {
        cityName ?: stringResource(R.string.loading)
    } else {
        currentWeather?.cityName ?: cityName ?: "..."
    }
    val date = SimpleDateFormat("EEE, MMM d", locale).format(Date())
    val time = SimpleDateFormat("h:mm a", locale).format(Date())
    val weatherType = remember(currentWeather) {
        val desc = (currentWeather?.description ?: "").lowercase()
        val icon = currentWeather?.icon ?: ""
        when {
            desc.contains("snow") || icon.startsWith("13") -> "snow"
            desc.contains("rain") || desc.contains("drizzle") || icon.startsWith("09") || icon.startsWith("10") -> "rain"
            desc.contains("cloud") || icon.startsWith("02") || icon.startsWith("03") || icon.startsWith("04") -> "clouds"
            else -> "clear"
        }
    }

    val isDark = isSystemInDarkTheme()
    val contentColor = if (isDark) Color.White else Color.Black

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 30.dp),
            verticalArrangement = Arrangement.spacedBy(25.dp)
        ) {
                if (!viewModel.isOnline()) {
                    OfflineBanner()
                }

                if (refreshStatus is Resource.Loading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().height(2.dp),
                        color = AccentPurple,
                        trackColor = Color.Transparent
                    )
                }

                HeaderSection(
                    cityName = cityNameDisplay,
                    isDetailMode = isDetailMode,
                    navController = navController,
                    textColor = contentColor
                )
                
                TemperatureSection(
                    temp = displayTemp, 
                    condition = displayCondition,
                    date = date,
                    time = time,
                    textColor = contentColor,
                    weatherType = weatherType
                )
                
                DailyForecastSection(
                    forecast = forecast.take(7),
                    selectedIndex = selectedDayIndex,
                    onDaySelected = { selectedDayIndex = it },
                    isDark = isDark,
                    locale = locale
                )

                if (displayHourly.isNotEmpty()) {
                    HourlyForecastSection(displayHourly, locale, isDark)
                }
                
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatArcCard(
                            Modifier.weight(1f), 
                            androidx.compose.ui.res.stringResource(com.example.weatherapp.R.string.pressure), 
                            "${displayPressure.toInt()}", 
                            androidx.compose.ui.res.stringResource(com.example.weatherapp.R.string.unit_hpa), 
                            displayPressure / 1100f, 
                            Icons.Default.Speed
                        )
                        StatArcCard(
                            Modifier.weight(1f), 
                            androidx.compose.ui.res.stringResource(com.example.weatherapp.R.string.humidity), 
                            "${displayHumidity.toInt()}", 
                            androidx.compose.ui.res.stringResource(com.example.weatherapp.R.string.unit_percent), 
                            displayHumidity / 100f, 
                            Icons.Default.WaterDrop
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatArcCard(
                            Modifier.weight(1f), 
                            androidx.compose.ui.res.stringResource(com.example.weatherapp.R.string.wind), 
                            "${displayWind.toInt()}", 
                            androidx.compose.ui.res.stringResource(com.example.weatherapp.R.string.unit_ms), 
                            (displayWind / 30f).coerceIn(0f, 1f), 
                            Icons.Default.Air
                        )
                        StatArcCard(
                            Modifier.weight(1f), 
                            androidx.compose.ui.res.stringResource(com.example.weatherapp.R.string.clouds), 
                            "$displayClouds", 
                            androidx.compose.ui.res.stringResource(com.example.weatherapp.R.string.unit_percent), 
                            displayClouds / 100f, 
                            Icons.Default.Cloud
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(50.dp))
            }
            
            // Weather Effects Overlay
            val currentTemp = currentWeather?.temp ?: 0.0
            val showSnow = weatherType == "snow" || currentTemp <= 0.0
            
            // Only show effects if we have a valid weather type or condition
            if (showSnow) {
                com.example.weatherapp.ui.components.WeatherEffects(weatherType = "snow", modifier = Modifier.fillMaxSize())
            } else if (weatherType == "rain" || weatherType.contains("thunder")) {
                com.example.weatherapp.ui.components.WeatherEffects(weatherType = "rain", modifier = Modifier.fillMaxSize())
            }
        }
    }

@Composable
fun HourlyForecastSection(
    hourly: List<com.example.weatherapp.data.local.entity.HourlyForecastEntity>,
    locale: Locale,
    isDark: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            androidx.compose.ui.res.stringResource(com.example.weatherapp.R.string.hourly_forecast),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color.White else Color.Black
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 8.dp) // Add padding for elevation shadows
        ) {
            itemsIndexed(hourly) { index, item ->
                val time = SimpleDateFormat("h a", locale).format(Date(item.dt * 1000))
                HourlyForecastItem(time, item.temp.roundToInt(), item.description, item.icon, isDark)
            }
        }
    }
}

@Composable
fun HourlyForecastItem(time: String, temp: Int, description: String, icon: String, isDark: Boolean) {
    Card(
        shape = RoundedCornerShape(30.dp), // More rounded for modern look
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) TranslucentBlack.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.5f)
        ),
        modifier = Modifier.width(70.dp).height(120.dp) // Vertical pill shape
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(time, style = MaterialTheme.typography.bodySmall, color = if (isDark) TextSecondary else Color.Gray)
            
            val iconRes = getWeatherIconRes(icon, description)
            val iconTint = getWeatherIconTint(icon, description)
            
            Icon(
                painter = androidx.compose.ui.res.painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = iconTint
            )
            
            Text(
                "$temp°",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color.Black
            )
        }
    }
}

@Composable
fun DailyForecastSection(
    forecast: List<com.example.weatherapp.data.local.entity.ForecastEntity>,
    selectedIndex: Int,
    onDaySelected: (Int) -> Unit,
    isDark: Boolean,
    locale: Locale
) {
    Column {
        Text(
            androidx.compose.ui.res.stringResource(com.example.weatherapp.R.string.daily_forecast),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color.White else Color.Black
        )
        Spacer(Modifier.height(16.dp))
        
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Today item
            item {
                DailyCardItem(
                    dayName = stringResource(R.string.today),
                    temp = 0,
                    isSelected = selectedIndex == 0,
                    onClick = { onDaySelected(0) },
                    isDark = isDark,
                    isToday = true
                )
            }
            
            // Forecast items
            itemsIndexed(forecast) { index, item ->
                val dayName = SimpleDateFormat("EEE", locale).format(Date(item.dt * 1000))
                DailyCardItem(
                    dayName = dayName,
                    temp = item.tempDay.roundToInt(),
                    icon = item.icon,
                    isSelected = selectedIndex == index + 1,
                    onClick = { onDaySelected(index + 1) },
                    isDark = isDark
                )
            }
        }
    }
}

@Composable
fun DailyCardItem(
    dayName: String,
    temp: Int,
    icon: String? = null,
    isSelected: Boolean,
    onClick: () -> Unit,
    isDark: Boolean,
    isToday: Boolean = false
) {
    val containerColor = if (isSelected) AccentPurple else if (isDark) TranslucentBlack.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.5f)
    val contentColor = if (isSelected) Color.White else if (isDark) Color.White.copy(alpha = 0.7f) else Color.Gray
    
    Card(
        modifier = Modifier
            .width(85.dp)
            .height(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(dayName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = contentColor)
            
            if (icon != null) {
                val iconRes = getWeatherIconRes(icon, "")
                val iconTint = if (isSelected) Color.White else getWeatherIconTint(icon, "")
                Icon(
                    painter = androidx.compose.ui.res.painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = iconTint
                )
            } else {
                 Icon(androidx.compose.ui.res.painterResource(id = R.drawable.ic_cloud), null, Modifier.size(28.dp), if (isSelected) Color.White else AccentPurple)
            }

            if (!isToday || temp != 0) {
                Text(
                    "$temp°",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else if (isDark) Color.White else Color.Black
                )
            } else {
                Text("-", style = MaterialTheme.typography.titleMedium, color = contentColor)
            }
        }
    }
}

@Composable
fun HeaderSection(cityName: String, isDetailMode: Boolean = false, navController: NavController? = null, textColor: Color = Color.White) {
    Row(
        modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(top = 30.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isDetailMode) {
                IconButton(
                    onClick = { navController?.popBackStack() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = textColor)
                }
                Spacer(Modifier.width(8.dp))
            }
            
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = AccentPurple.copy(alpha = 0.2f)
            ) {
                Icon(Icons.Default.LocationOn, null, Modifier.padding(8.dp), AccentPurple)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(cityName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = textColor)
            }
        }
    }
}

@Composable
fun TemperatureSection(temp: Int, condition: String, date: String, time: String, textColor: Color = Color.White, weatherType: String = "clear") {
    val isDark = isSystemInDarkTheme()
    val iconRes = getWeatherIconRes(weatherType, condition) // weatherType logic below covers condition mostly
    val iconTint = getWeatherIconTint(weatherType, condition)
    
    // ... use Icon(painterResource(id = iconRes), tint = AccentPurple) ...

    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = RoundedCornerShape(30.dp),
            color = if (isDark) Color.White.copy(alpha = 0.05f) else iconTint.copy(alpha = 0.1f)
        ) {
            Icon(
                painter = androidx.compose.ui.res.painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.padding(20.dp),
                tint = iconTint
            )
        }
        Spacer(Modifier.height(16.dp))
        Text("$temp°", fontSize = 100.sp, fontWeight = FontWeight.Light, color = textColor)
        Text(condition, style = MaterialTheme.typography.headlineSmall, color = textColor)
        Text("$date | $time", style = MaterialTheme.typography.bodyLarge, color = if (isDark) TextSecondary else Color.Gray)
    }
}

fun getWeatherIconRes(icon: String, description: String = ""): Int {
    val lowerDesc = description.lowercase()
    return when {
        lowerDesc.contains("snow") || icon.startsWith("13") -> R.drawable.ic_snowy
        lowerDesc.contains("rain") || lowerDesc.contains("drizzle") || icon.startsWith("09") || icon.startsWith("10") -> R.drawable.ic_rainy
        lowerDesc.contains("thunder") || icon.startsWith("11") -> R.drawable.ic_rainy // Reuse rainy for thunder for now
        lowerDesc.contains("cloud") || icon.startsWith("02") || icon.startsWith("03") || icon.startsWith("04") -> R.drawable.ic_cloud
        else -> R.drawable.ic_sunny
    }
}

fun getWeatherIconTint(icon: String, description: String = ""): Color {
    val lowerDesc = description.lowercase()
    return when {
        lowerDesc.contains("snow") || icon.startsWith("13") -> Color(0xFFB0C4DE) // Light Steel Blue for Snow/Cold
        lowerDesc.contains("rain") || lowerDesc.contains("drizzle") || icon.startsWith("09") || icon.startsWith("10") -> Color(0xFF4682B4) // Steel Blue for Rain
        lowerDesc.contains("thunder") || icon.startsWith("11") -> Color(0xFF483D8B) // Dark Slate Blue for Thunder
        lowerDesc.contains("cloud") || icon.startsWith("02") || icon.startsWith("03") || icon.startsWith("04") -> Color(0xFF87CEEB) // Sky Blue for Clouds (Baby Blueish)
        else -> Color(0xFFFFD700) // Gold/Yellow for Sun/Clear
    }
}


