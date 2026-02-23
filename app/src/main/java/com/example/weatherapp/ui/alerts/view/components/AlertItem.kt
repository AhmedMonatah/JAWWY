package com.example.weatherapp.ui.alerts.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.model.Alert
import com.example.weatherapp.ui.theme.RamadanDarkBlue
import com.example.weatherapp.ui.theme.RamadanGold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AlertItem(
    alert: Alert,
    locale: Locale,
    onToggle: () -> Unit
) {
    val isAlarm = alert.type.lowercase() == "alarm"
    val timeFormatter = remember(locale) { SimpleDateFormat("h:mm a", locale) }
    val dateFormatter = remember(locale) { SimpleDateFormat("MMM d", locale) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = RamadanDarkBlue.copy(alpha = if (alert.isEnabled) 1f else 0.5f)
        ),
        elevation = CardDefaults.cardElevation(if (alert.isEnabled) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon badge
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        RamadanGold.copy(alpha = if (alert.isEnabled) 0.18f else 0.08f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isAlarm) Icons.Default.Alarm else Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = if (alert.isEnabled) RamadanGold else RamadanGold.copy(alpha = 0.4f),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Text info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isAlarm) stringResource(R.string.alarm)
                           else stringResource(R.string.notification),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (alert.isEnabled) Color.White else Color.White.copy(alpha = 0.45f)
                )
                Spacer(modifier = Modifier.height(3.dp))
                val startStr = timeFormatter.format(Date(alert.startTime))
                val subtitle = if (isAlarm) {
                    // Alarm: show date + time
                    "${dateFormatter.format(Date(alert.startTime))}  ·  $startStr"
                } else {
                    // Notification: show start–end
                    val endStr = timeFormatter.format(Date(alert.endTime))
                    "$startStr → $endStr"
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = if (alert.isEnabled) 0.6f else 0.3f)
                )
            }

            // Toggle switch
            Switch(
                checked = alert.isEnabled,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = RamadanDarkBlue,
                    checkedTrackColor = RamadanGold,
                    uncheckedThumbColor = Color.White.copy(alpha = 0.5f),
                    uncheckedTrackColor = Color.White.copy(alpha = 0.15f)
                )
            )
        }
    }
}