package com.example.weatherapp.ui.home.view

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.weatherapp.ui.components.StatArcCard
import com.example.weatherapp.ui.components.WeatherBackground
import com.example.weatherapp.ui.home.viewmodel.HomeViewModel
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
    
    val isDetailMode = lat != null
    
    var selectedDayIndex by remember { mutableStateOf(0) }
    
    val currentLang by viewModel.language.collectAsState()
    val locale = remember(currentLang) { Locale(currentLang) }
    
    // Combine Today + 7 Days Forecast
    val days = listOf("Today") + forecast.take(7).map { 
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
    val refreshStatus by viewModel.refreshStatus.collectAsState()

    val cityNameDisplay = if (refreshStatus is Resource.Loading) "Loading..." else currentWeather?.cityName ?: "..."
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
    val bgColor = if (isDark) DashboardBackground else Color(0xFFF5F5F7)
    val contentColor = if (isDark) Color.White else Color.Black

    Box(modifier = Modifier.fillMaxSize().background(bgColor)) {
        WeatherBackground(weatherType = weatherType)
        
        Scaffold(
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 30.dp),
                verticalArrangement = Arrangement.spacedBy(25.dp)
            ) {
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
                
                DailyForecastRow(
                    days = days,
                    temps = temps,
                    selectedIndex = selectedDayIndex,
                    onDaySelected = { selectedDayIndex = it }
                )
                
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatArcCard(Modifier.weight(1f), "Pressure", "${displayPressure.toInt()}", "hPa", displayPressure / 1100f, Icons.Default.Speed)
                        StatArcCard(Modifier.weight(1f), "Humidity", "${displayHumidity.toInt()}", "%", displayHumidity / 100f, Icons.Default.WaterDrop)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatArcCard(Modifier.weight(1f), "Wind", "${displayWind.toInt()}", "m/s", (displayWind / 30f).coerceIn(0f, 1f), Icons.Default.Air)
                        StatArcCard(Modifier.weight(1f), "Clouds", "75", "%", 0.75f, Icons.Default.Cloud)
                    }
                }
                
                Spacer(modifier = Modifier.height(50.dp))
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
    val icon = when (weatherType) {
        "snow" -> Icons.Default.Cloud // Use placeholder or better icon
        "rain" -> Icons.Default.WaterDrop
        "clouds" -> Icons.Default.Cloud
        else -> Icons.Default.WbSunny
    }
    val iconColor = when (weatherType) {
        "clear" -> Color.Yellow
        "snow" -> Color.White
        else -> AccentBlue
    }

    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = RoundedCornerShape(30.dp),
            color = if (isDark) Color.White.copy(alpha = 0.05f) else AccentPurple.copy(alpha = 0.1f)
        ) {
            Icon(icon, null, Modifier.padding(20.dp), iconColor)
        }
        Spacer(Modifier.height(16.dp))
        Text("$temp°", fontSize = 100.sp, fontWeight = FontWeight.Light, color = textColor)
        Text(condition, style = MaterialTheme.typography.headlineSmall, color = textColor)
        Text("$date | $time", style = MaterialTheme.typography.bodyLarge, color = if (isDark) TextSecondary else Color.Gray)
    }
}

@Composable 
fun DailyForecastRow(days: List<String>, temps: List<Int>, selectedIndex: Int, onDaySelected: (Int) -> Unit) {
    val isDark = isSystemInDarkTheme()
    
    LazyRow( horizontalArrangement = Arrangement.spacedBy(12.dp))
    {
        itemsIndexed(days) {
            index, day -> val isSelected = index == selectedIndex
            val containerColor = if (isSelected) AccentPurple 
                                else if (isDark) TranslucentBlack else MaterialTheme.colorScheme.surfaceVariant
            val contentColor = if (isSelected) Color.White 
                              else if (isDark) TextSecondary else Color.Gray
            val iconColor = if (isSelected) Color.White else AccentPurple

            Card( modifier = Modifier .width(75.dp)
                .clip(RoundedCornerShape(24.dp))
                .clickable { onDaySelected(index) },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = containerColor) ) {
                Column( modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally ) {
                    Text(day, style = MaterialTheme.typography.bodySmall, color = contentColor)
                    Spacer(Modifier.height(12.dp))
                    Icon(Icons.Default.Cloud, null, Modifier.size(24.dp), iconColor)
                    Spacer(Modifier.height(12.dp))
                    Text("${temps.getOrElse(index) { 0 }}°", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else if (isDark) Color.White else Color.Black)
                }
            }
        }
    }
}
