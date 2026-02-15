package com.example.weatherapp.ui.home.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val currentWeather by viewModel.currentWeather.collectAsState()
    val forecast by viewModel.forecast.collectAsState()
    
    var selectedDayIndex by remember { mutableStateOf(0) }
    
    // Dynamic Data Mapping
    // Fallbacks provided for initial load or null state
    val cityName = currentWeather?.cityName ?: "Loading..."
    val currentTemp = currentWeather?.temp?.roundToInt() ?: 0
    val currentCondition = currentWeather?.description?.replaceFirstChar { it.uppercase() } ?: "..."
    // Icons can be mapped here too if needed, currently hardcoded WbSunny in TempSection

    // Formatting date/time
    val date = SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date())
    val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())

    Box(modifier = Modifier.fillMaxSize().background(DashboardBackground)) {
        // Use weather condition for background logic if desired, defaulting to 'snow' as per user design
        WeatherBackground(weatherType = currentCondition.lowercase())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 30.dp),
            verticalArrangement = Arrangement.spacedBy(25.dp)
        ) {
            HeaderSection(cityName)
            
            TemperatureSection(
                temp = currentTemp, 
                condition = currentCondition,
                date = date,
                time = time
            )
            
            // Map forecast to UI model (taking first 7 days)
            val days = forecast.take(7).map { 
                SimpleDateFormat("EEE", Locale.getDefault()).format(Date(it.dt * 1000)) 
            }
            val temps = forecast.take(7).map { it.tempDay.roundToInt() }
            
            // Only show if we have data, otherwise show placeholder or loading
            if (days.isNotEmpty()) {
                DailyForecastRow(
                    days = days,
                    temps = temps,
                    selectedIndex = selectedDayIndex,
                    onDaySelected = { selectedDayIndex = it }
                )
            } else {
                // Placeholder if no forecast yet
                 DailyForecastRow(
                    days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
                    temps = listOf(0, 0, 0, 0, 0, 0, 0),
                    selectedIndex = selectedDayIndex,
                    onDaySelected = { selectedDayIndex = it }
                )
            }
            
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                     // Normalize values for progress 0f..1f
                    val pressureVal = (currentWeather?.pressure ?: 0).toFloat()
                    val humidityVal = (currentWeather?.humidity ?: 0).toFloat()
                    
                    StatArcCard(Modifier.weight(1f), "Pressure", "${pressureVal.toInt()}", "hPa", pressureVal / 1100f, Icons.Default.Speed)
                    StatArcCard(Modifier.weight(1f), "Humidity", "${humidityVal.toInt()}", "%", humidityVal / 100f, Icons.Default.WaterDrop)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    val windVal = (currentWeather?.windSpeed ?: 0.0).toFloat()
                    // Assuming clouds/visibility for last card or just static as per user request
                    val clouds = "75" 
                     
                    StatArcCard(Modifier.weight(1f), "Wind", "${windVal.toInt()}", "m/s", (windVal / 30f).coerceIn(0f, 1f), Icons.Default.Air)
                    StatArcCard(Modifier.weight(1f), "Clouds", clouds, "%", 0.75f, Icons.Default.Cloud)
                }
            }
            
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
fun HeaderSection(cityName: String) {
    Row(
        modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(top = 30.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = AccentPurple.copy(alpha = 0.2f)
            ) {
                Icon(Icons.Default.LocationOn, null, Modifier.padding(8.dp), AccentPurple)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(cityName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun TemperatureSection(temp: Int, condition: String, date: String, time: String) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = RoundedCornerShape(30.dp),
            color = Color.White.copy(alpha = 0.05f)
        ) {
            Icon(Icons.Default.WbSunny, null, Modifier.padding(20.dp), Color.Yellow)
        }
        Spacer(Modifier.height(16.dp))
        Text("$temp°", fontSize = 100.sp, fontWeight = FontWeight.Light, color = Color.White)
        Text(condition, style = MaterialTheme.typography.headlineSmall, color = Color.White)
        Text("$date | $time", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
    }
}

@Composable 
fun DailyForecastRow(days: List<String>, temps: List<Int>, selectedIndex: Int, onDaySelected: (Int) -> Unit) {
    LazyRow( horizontalArrangement = Arrangement.spacedBy(12.dp))
    {
        itemsIndexed(days) {
            index, day -> val isSelected = index == selectedIndex
            Card( modifier = Modifier .width(75.dp)
                .clip(RoundedCornerShape(24.dp))
                .clickable { onDaySelected(index) },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                containerColor = if (isSelected)
                AccentPurple else TranslucentBlack ) ) {
                Column( modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally ) {
                    Text(day, style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected) Color.White else TextSecondary)
                    Spacer(Modifier.height(12.dp))
                    Icon(Icons.Default.Cloud, null, Modifier.size(24.dp),
                        if (isSelected) Color.White else AccentPurple)
                    Spacer(Modifier.height(12.dp))
                    Text("${temps.getOrElse(index) { 0 }}°", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
