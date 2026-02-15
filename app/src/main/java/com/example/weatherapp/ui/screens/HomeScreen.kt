package com.example.weatherapp.ui.screens

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.ui.components.SegmentedArcIndicator
import com.example.weatherapp.ui.components.WeatherBackground
import com.example.weatherapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    var selectedDayIndex by remember { mutableStateOf(0) }
    var showDetailsSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val temps = listOf(22, 24, 21, 23, 25, 22, 24)
    val conditions = listOf("Sunny", "Cloudy", "Rainy", "Thunder", "Clear", "Cloudy", "Sunny")
    
    val currentDayName = days[selectedDayIndex]
    val currentTemp = temps[selectedDayIndex]
    val currentCondition = conditions[selectedDayIndex]

    val calendar = Calendar.getInstance()
    val dateFormatter = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    val currentDate = dateFormatter.format(calendar.time)
    val currentTime = timeFormatter.format(calendar.time)

    Box(modifier = Modifier.fillMaxSize().background(DashboardBackground)) {
        WeatherBackground(weatherType = "snow")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 30.dp),
            verticalArrangement = Arrangement.spacedBy(25.dp)
        ) {
            HeaderSection()
            
            TemperatureSection(
                temp = currentTemp, 
                condition = currentCondition,
                date = currentDate,
                time = currentTime
            )
            
            DailyForecastRow(
                days = days,
                temps = temps,
                selectedIndex = selectedDayIndex,
                onDaySelected = { selectedDayIndex = it }
            )
            
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatArcCard(Modifier.weight(1f), "Pressure", "29", "onHg", 0.7f, Icons.Default.Speed)
                    StatArcCard(Modifier.weight(1f), "Humidity", "45", "%", 0.45f, Icons.Default.WaterDrop)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatArcCard(Modifier.weight(1f), "Wind", "12", "mph", 0.3f, Icons.Default.Air)
                    StatArcCard(Modifier.weight(1f), "Clouds", "75", "%", 0.75f, Icons.Default.Cloud)
                }
            }
            
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}


@Composable
fun HeaderSection() {
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
                Text("San Francisco", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

    }
}

@Composable
fun TemperatureSection(temp: Int, condition: String, date: String, time: String) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        // Hero Weather Icon Placeholder (image icon style)
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
@Composable fun DailyForecastRow(days: List<String>, temps: List<Int>, selectedIndex: Int, onDaySelected: (Int) -> Unit) {
    LazyRow( horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(end = 15.dp) ) {
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
                    Text("${temps[index]}°", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

    }
}


@Composable
fun StatArcCard(modifier: Modifier, title: String, value: String, unit: String, progress: Float, icon: ImageVector) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = TranslucentBlack)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(35.dp),
                    shape = CircleShape,
                    color = AccentPurple.copy(alpha = 0.2f)
                ) {
                    Icon(icon, null, Modifier.padding(8.dp), AccentPurple)
                }
                Spacer(Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
            
            Spacer(Modifier.height(16.dp))

            SegmentedArcIndicator(
                progress = progress,
                label = value,
                unit = unit,
                modifier = Modifier.size(140.dp)
            )
        }
    }
}
@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
fun HomeScreenPreview() {
    WeatherAppTheme {
        val navController = rememberNavController()
        HomeScreen(navController = navController)
    }
}