package com.example.weatherapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.weatherapp.model.Alert
import com.example.weatherapp.model.FakeData
import com.example.weatherapp.ui.theme.*

@Composable
fun AlertsScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize().background(DashboardBackground)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                text = "WEATHER ALERTS",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                 verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(FakeData.alerts) { alert ->
                    AlertItem(alert = alert)
                }
            }
        }
    }
}

@Composable
fun AlertItem(alert: Alert) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = TranslucentBlack)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = AccentBlue.copy(alpha = 0.1f)
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.padding(10.dp), tint = AccentBlue)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = alert.type,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                 Icon(
                    imageVector = if (alert.isNotification) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                    contentDescription = null,
                    tint = if(alert.isNotification) AccentPurple else Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Text(text = "${alert.startTime} - ${alert.endTime}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
        }
    }
}
