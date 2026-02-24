package com.example.weatherapp.ui.alerts.view

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
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
import com.example.weatherapp.ui.components.SelectionDeleteBar
import com.example.weatherapp.ui.main.view.LocalSelectionMode
import kotlinx.coroutines.launch
import com.example.weatherapp.ui.theme.RamadanDarkBlue
import com.example.weatherapp.ui.theme.RamadanGold
import java.util.*

import com.example.weatherapp.ui.alerts.viewmodel.AlertsUiEvent
import com.example.weatherapp.ui.alerts.view.components.EmptyAlertsState
import com.example.weatherapp.ui.alerts.view.components.OverlayPermissionDialog

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

    val globalSelectionMode = LocalSelectionMode.current
    val snackbarHostState = com.example.weatherapp.ui.main.view.LocalSnackbarHostState.current
    val selectedAlerts by viewModel.selectedAlerts.collectAsState()
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is AlertsUiEvent.ShowSnackbar -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "${context.getString(R.string.deleted)} ${event.count}",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
                else -> {}
            }
        }
    }

    BackHandler(enabled = selectedAlerts.isNotEmpty()) {
        viewModel.clearSelection()
    }
    
    LaunchedEffect(selectedAlerts.size) {
        globalSelectionMode.value = selectedAlerts.isNotEmpty()
    }

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
                EmptyAlertsState()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 110.dp)
                ) {
                    items(alerts, key = { it.id }) { alert ->
                        val isSelected = selectedAlerts.contains(alert)
                        AlertItem(
                            alert = alert,
                            locale = locale,
                            selected = isSelected,
                            onToggle = { viewModel.toggleAlert(alert) },
                            onClick = {
                                if (globalSelectionMode.value) {
                                    viewModel.toggleSelection(alert)
                                }
                            },
                            onLongClick = {
                                viewModel.toggleSelection(alert)
                            }
                        )
                    }
                }
            }
        }

        SelectionDeleteBar(
            selectedCount = selectedAlerts.size,
            onClearSelection = { viewModel.clearSelection() },
            onDeleteSelected = { viewModel.deleteSelectedAlerts() },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        if (!globalSelectionMode.value) {
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
        }

        if (showPermissionDialog) {
            OverlayPermissionDialog(onDismiss = { showPermissionDialog = false })
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

        if (showNoInternetDialog) {
            NoInternetConnectionDialog(onDismiss = { showNoInternetDialog = false })
        }
    }
}
