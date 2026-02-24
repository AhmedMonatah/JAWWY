package com.example.weatherapp.ui.alerts.view

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.di.LocalAppContainer
import androidx.navigation.NavController
import com.example.weatherapp.R
import com.example.weatherapp.ui.alerts.viewmodel.AlertsViewModel
import com.example.weatherapp.ui.alerts.view.components.AddAlertDialog
import com.example.weatherapp.ui.alerts.view.components.AlertItem
import com.example.weatherapp.ui.components.NoInternetConnectionDialog
import com.example.weatherapp.ui.components.AppFloatingActionButton
import com.example.weatherapp.ui.theme.RamadanDarkBlue
import com.example.weatherapp.ui.theme.RamadanGold
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    navController: NavController,
    viewModel: AlertsViewModel = viewModel(factory = LocalAppContainer.current.viewModelFactory)
) {
    val alerts by viewModel.alerts.collectAsState()
    val language by viewModel.language.collectAsState()
    val locale = remember(language) { Locale(language) }

    var showBottomSheet       by remember { mutableStateOf(false) }
    var showPermissionDialog  by remember { mutableStateOf(false) }
    var showNoInternetDialog  by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.alerts_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = stringResource(R.string.alerts_description),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.55f),
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
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
                            tint = RamadanGold.copy(alpha = 0.12f),
                            modifier = Modifier.size(110.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(stringResource(R.string.no_alerts),
                            color = Color.White.copy(alpha = 0.4f),
                            style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(stringResource(R.string.add_first_alert),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.2f))
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 110.dp)
                ) {
                    items(alerts, key = { it.id }) { alert ->
                        // ── Swipe to delete ──────────────────────────────────
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                if (value != SwipeToDismissBoxValue.Settled) {
                                    viewModel.deleteAlert(alert)
                                    true
                                } else false
                            }
                        )
                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = true,
                            enableDismissFromEndToStart = true,
                            backgroundContent = {
                                val isSettled = dismissState.currentValue == SwipeToDismissBoxValue.Settled &&
                                               dismissState.targetValue == SwipeToDismissBoxValue.Settled
                                
                                // Only show background if NOT settled (actively swiping or dismissing)
                                if (!isSettled) {
                                    val alignment = when (dismissState.targetValue) {
                                        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                                        else -> Alignment.CenterEnd
                                    }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(20.dp))
                                            .background(Color(0xFFE53935)),
                                        contentAlignment = alignment
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color.White,
                                            modifier = Modifier.padding(horizontal = 24.dp).size(28.dp)
                                        )
                                    }
                                }
                            }
                        ) {
                            AlertItem(
                                alert = alert,
                                locale = locale,
                                onToggle = { viewModel.toggleAlert(alert) }
                            )
                        }
                    }
                }
            }
        }

        AppFloatingActionButton(
            icon = Icons.Default.Alarm,
            contentDescription = stringResource(R.string.add_alert),
            onClick = {
                if (viewModel.isOnline()) {
                    if (!Settings.canDrawOverlays(context)) {
                        showPermissionDialog = true
                    } else {
                        showBottomSheet = true
                    }
                } else {
                    showNoInternetDialog = true
                }
            },
            modifier = Modifier.align(Alignment.BottomEnd)
        )

        // ── Overlay permission dialog ─────────────────────────────────────────
        if (showPermissionDialog) {
            AlertDialog(
                onDismissRequest = { showPermissionDialog = false },
                containerColor = RamadanDarkBlue,
                title = { Text(stringResource(R.string.permission_required), color = Color.White) },
                text = { Text(stringResource(R.string.overlay_permission_desc), color = Color.White.copy(alpha = 0.7f)) },
                confirmButton = {
                    Button(
                        onClick = {
                            context.startActivity(Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${context.packageName}")
                            ))
                            showPermissionDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RamadanGold)
                    ) { Text(stringResource(R.string.grant)) }
                },
                dismissButton = {
                    TextButton(onClick = { showPermissionDialog = false }) {
                        Text(stringResource(R.string.cancel), color = Color.White.copy(alpha = 0.6f))
                    }
                }
            )
        }

        // ── Bottom sheet ──────────────────────────────────────────────────────
        if (showBottomSheet) {
            AddAlertDialog(
                onDismiss = { showBottomSheet = false },
                onSave = { start, end, type ->
                    viewModel.addAlert(start, end, type)
                    showBottomSheet = false
                }
            )
        }

        if (showNoInternetDialog) {
            NoInternetConnectionDialog(onDismiss = { showNoInternetDialog = false })
        }
    }
}
