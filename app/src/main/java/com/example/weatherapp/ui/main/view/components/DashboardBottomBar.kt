package com.example.weatherapp.ui.main.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapp.ui.theme.DashboardBackground


@Composable
fun DashboardBottomBar(currentPage: Int, onPageSelected: (Int) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp),
        color = DashboardBackground.copy(alpha = 0.9f),
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(Icons.Default.Home, "HOME", currentPage == 0) { onPageSelected(0) }
            BottomNavItem(Icons.Default.Star, "SAVED", currentPage == 1) { onPageSelected(1) }
            BottomNavItem(Icons.Default.Alarm, "ALARM", currentPage == 2) { onPageSelected(2) }
            BottomNavItem(Icons.Default.Settings, "SETTING", currentPage == 3) { onPageSelected(3) }
        }
    }
}