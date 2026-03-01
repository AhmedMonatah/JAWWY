package com.example.weatherapp.ui.alerts.view.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import com.example.weatherapp.ui.theme.LocalIsDark
import com.example.weatherapp.ui.theme.RamadanDarkBlue
import com.example.weatherapp.ui.theme.RamadanGold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlertItem(
    alert: Alert,
    locale: Locale,
    selected: Boolean = false,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    val isAlarm = alert.type.lowercase() == "alarm"
    val timeFormatter = remember(locale) { SimpleDateFormat("h:mm a", locale) }
    val dateFormatter = remember(locale) { SimpleDateFormat("MMM d", locale) }

    val isDark = LocalIsDark.current
    Card(
        modifier = Modifier.fillMaxWidth().combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick
        ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (alert.isEnabled) { if (selected) 0.8f else 1f } else 0.5f)
        ),
        border = if (selected) BorderStroke(3.dp, RamadanGold) else BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(if (isDark) 0.dp else if (alert.isEnabled) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = if (alert.isEnabled) 0.18f else 0.08f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isAlarm) Icons.Default.Alarm else Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = if (alert.isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isAlarm) stringResource(R.string.alarm)
                           else stringResource(R.string.notification),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (alert.isEnabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                )
                Spacer(modifier = Modifier.height(3.dp))
                val startStr = timeFormatter.format(Date(alert.startTime))
                val subtitle = if (isAlarm) {
                    "${dateFormatter.format(Date(alert.startTime))}  ·  $startStr"
                } else {
                    val endStr = timeFormatter.format(Date(alert.endTime))
                    "$startStr → $endStr"
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (alert.isEnabled) 0.6f else 0.3f)
                )
            }

            Switch(
                checked = alert.isEnabled,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.surface,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    uncheckedTrackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f)
                )
            )
        }
    }
}