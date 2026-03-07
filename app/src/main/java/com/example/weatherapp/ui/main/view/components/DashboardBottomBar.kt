package com.example.weatherapp.ui.main.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapp.ui.navigation.Screen


@Composable
fun DashboardBottomBar(currentPage: Int, onNavigate: (Screen) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        color = MaterialTheme.colorScheme.background.copy(0.50F),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(Icons.Default.Home, "HOME", currentPage == 0) { onNavigate(Screen.Home) }
            
            BottomNavItem(Icons.Default.Star, "SAVED", currentPage == 1) { onNavigate(Screen.Favorites) }
            
            BottomNavItem(Icons.Default.Alarm, "ALERTS", currentPage == 2) { onNavigate(Screen.Alerts) }
            
            BottomNavItem(Icons.Default.Settings, "SETTING", currentPage == 3) { onNavigate(Screen.Settings) }
        }
    }
}