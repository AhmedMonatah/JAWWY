package com.example.weatherapp.ui.alerts.view

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.weatherapp.R
import com.example.weatherapp.data.local.entity.Alert
import com.example.weatherapp.ui.alerts.viewmodel.AlertsViewModel
import com.example.weatherapp.ui.theme.AccentPurple
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    navController: NavController,
    viewModel: AlertsViewModel = hiltViewModel()
) {
    val alerts by viewModel.alerts.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .statusBarsPadding()
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.alerts_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                if (alerts.isNotEmpty()) {
                    IconButton(
                        onClick = { viewModel.deleteAllAlerts() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = stringResource(R.string.delete_all),
                            tint = Color(0xFFFF6B6B)
                        )
                    }
                }
            }
            
            Text(
                text = stringResource(R.string.alerts_description),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            if (alerts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(bottom = 100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.1f),
                            modifier = Modifier.size(120.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.no_alerts),
                            color = Color.White.copy(alpha = 0.4f),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            } else {
                LazyColumn(
                     verticalArrangement = Arrangement.spacedBy(16.dp),
                     contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(alerts) { alert ->
                        AlertItem(alert = alert, onDelete = { viewModel.deleteAlert(alert) })
                    }
                }
            }
        }
        
        FloatingActionButton(
            onClick = { 
                if (!android.provider.Settings.canDrawOverlays(context)) {
                    showPermissionDialog = true
                } else {
                    showBottomSheet = true 
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(30.dp),
            containerColor = Color.Transparent,
            elevation = FloatingActionButtonDefaults.elevation(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFE94560), Color(0xFF533483))
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_alert), tint = Color.White)
            }
        }

        if (showPermissionDialog) {
            AlertDialog(
                onDismissRequest = { showPermissionDialog = false },
                containerColor = Color(0xFF16213E),
                title = { Text(stringResource(R.string.permission_required), color = Color.White) },
                text = { Text(stringResource(R.string.overlay_permission_desc), color = Color.Gray) },
                confirmButton = {
                    Button(
                        onClick = {
                            val intent = android.content.Intent(
                                android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                android.net.Uri.parse("package:${context.packageName}")
                            )
                            context.startActivity(intent)
                            showPermissionDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
                    ) {
                        Text(stringResource(R.string.grant))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPermissionDialog = false }) {
                        Text(stringResource(R.string.cancel), color = Color.White.copy(alpha = 0.6f))
                    }
                }
            )
        }

        if (showBottomSheet) {
            AddAlertDialog(
                onDismiss = { showBottomSheet = false },
                onSave = { start, end, type ->
                    viewModel.addAlert(start, end, type)
                    showBottomSheet = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlertDialog(
    onDismiss: () -> Unit,
    onSave: (Long, Long, String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    
    var startTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var endTime by remember { mutableStateOf(System.currentTimeMillis() + 3600000) }
    var type by remember { mutableStateOf("notification") }

    val startFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    val endFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())

    val startTimePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            startTime = calendar.timeInMillis
            if (endTime <= startTime) {
                endTime = startTime + 3600000
            }
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false
    )

    val endTimePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val tempCal = Calendar.getInstance()
            tempCal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            tempCal.set(Calendar.MINUTE, minute)
            endTime = tempCal.timeInMillis
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF16213E)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = stringResource(R.string.add_alert),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Duration
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(stringResource(R.string.duration), style = MaterialTheme.typography.titleMedium, color = Color.White.copy(alpha = 0.8f))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { startTimePickerDialog.show() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.start_time_label), fontSize = 12.sp, color = Color.Gray)
                            Text(startFormatter.format(Date(startTime)), fontWeight = FontWeight.Bold)
                        }
                    }
                    OutlinedButton(
                        onClick = { endTimePickerDialog.show() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.end_time_label), fontSize = 12.sp, color = Color.Gray)
                            Text(endFormatter.format(Date(endTime)), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Alert Type
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(stringResource(R.string.alert_type), style = MaterialTheme.typography.titleMedium, color = Color.White.copy(alpha = 0.8f))
                Row(
                   modifier = Modifier.fillMaxWidth().background(Color(0xFF0F3460), RoundedCornerShape(12.dp)).padding(4.dp),
                   horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TypeSelector(
                        text = stringResource(R.string.notification),
                        selected = type == "notification",
                        onClick = { type = "notification" },
                        modifier = Modifier.weight(1f)
                    )
                    TypeSelector(
                        text = stringResource(R.string.alarm),
                        selected = type == "alarm",
                        onClick = { type = "alarm" },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text(stringResource(R.string.cancel))
                }

                Button(
                    onClick = { onSave(startTime, endTime, type) },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                    elevation = ButtonDefaults.buttonElevation(8.dp)
                ) {
                    Text(stringResource(R.string.save_alert), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TypeSelector(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) AccentPurple else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else Color.Gray,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun AlertItem(alert: Alert, onDelete: () -> Unit) {
    val isNotification = alert.type.lowercase() == "notification"
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16213E).copy(alpha = 0.8f)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        if (isNotification) Color(0xFF4CC9F0).copy(alpha = 0.2f) else Color(0xFFF72585).copy(alpha = 0.2f),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isNotification) Icons.Default.NotificationsActive else Icons.Default.Alarm,
                    contentDescription = null,
                    tint = if (isNotification) Color(0xFF4CC9F0) else Color(0xFFF72585),
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isNotification) stringResource(R.string.notification) else stringResource(R.string.alarm),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                val startStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(alert.startTime))
                val endStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(alert.endTime))
                val dateStr = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(alert.startTime))
                
                Text(
                    text = "$startStr - $endStr",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Delete",
                    tint = Color.White.copy(alpha = 0.3f)
                )
            }
        }
    }
}
