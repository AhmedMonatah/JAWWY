package com.example.weatherapp.ui.alerts.view

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.weatherapp.R
import com.example.weatherapp.ui.alerts.viewmodel.AlertsViewModel
import com.example.weatherapp.ui.components.alert.AddAlertDialog
import com.example.weatherapp.ui.components.alert.AlertItem
import java.util.*

import com.example.weatherapp.ui.theme.RamadanDarkBlue
import com.example.weatherapp.ui.theme.RamadanDeepNavy
import com.example.weatherapp.ui.theme.RamadanGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    navController: NavController,
    viewModel: AlertsViewModel = hiltViewModel()
) {
    val alerts by viewModel.alerts.collectAsState()
    val language by viewModel.language.collectAsState()
    val locale = remember(language) { Locale(language) }
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
                .statusBarsPadding()
                .padding(horizontal = 25.dp)
        ) {
            Spacer(modifier = Modifier.height(15.dp))
            

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
                            tint = RamadanGold.copy(alpha = 0.1f),
                            modifier = Modifier.size(120.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.no_alerts),
                            color = Color.White.copy(alpha = 0.4f),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.add_first_alert),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.2f)
                        )
                    }
                }
            } else {
                LazyColumn(
                     verticalArrangement = Arrangement.spacedBy(16.dp),
                     contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(alerts) { alert ->
                        AlertItem(
                            alert = alert,
                            onDelete = { viewModel.deleteAlert(alert) },
                            locale = locale
                        )
                    }
                }
            }
        }
        
        FloatingActionButton(
            onClick = { 
                if (!Settings.canDrawOverlays(context)) {
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
                    .background(RamadanGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Alarm, 
                    contentDescription = stringResource(R.string.add_alert), 
                    tint = RamadanDeepNavy
                )
            }
        }

        if (showPermissionDialog) {
            AlertDialog(
                onDismissRequest = { showPermissionDialog = false },
                containerColor = RamadanDarkBlue,
                title = { Text(stringResource(R.string.permission_required), color = Color.White) },
                text = { Text(stringResource(R.string.overlay_permission_desc), color = Color.White.copy(alpha = 0.7f)) },
                confirmButton = {
                    Button(
                        onClick = {
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${context.packageName}")
                            )
                            context.startActivity(intent)
                            showPermissionDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RamadanGold)
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
