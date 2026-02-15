package com.example.weatherapp.ui.alarm.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.weatherapp.ui.components.WeatherBackground
import com.example.weatherapp.ui.navigation.Screen
import com.example.weatherapp.ui.theme.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreen(navController: NavController) {
    val viewModel: com.example.weatherapp.ui.settings.viewmodel.SettingsViewModel = hiltViewModel()
    val currentLang by viewModel.language.collectAsState()
    val locale = remember(currentLang) { java.util.Locale(currentLang) }
    
    var alarms by remember { mutableStateOf(listOf("07:00 AM", "08:30 AM", "09:00 PM")) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .statusBarsPadding()
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(com.example.weatherapp.R.string.weather_alarms),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                items(alarms) { alarmTime ->
                    AlarmItem(time = alarmTime)
                }
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate(Screen.Map.createRoute("alarm")) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 40.dp),
            containerColor = AccentPurple,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Alarm Location")
        }
    }
}

@Composable
fun AlarmItem(time: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = TranslucentBlack)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = AccentPurple.copy(alpha = 0.1f)
                ) {
                    Icon(Icons.Default.Alarm, contentDescription = null, modifier = Modifier.padding(12.dp), tint = AccentPurple)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = time,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = stringResource(com.example.weatherapp.R.string.daily),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
            Switch(
                checked = true,
                onCheckedChange = {},
                colors = SwitchDefaults.colors(
                    checkedThumbColor = AccentPurple,
                    checkedTrackColor = AccentPurple.copy(alpha = 0.3f)
                )
            )
        }
    }
}
