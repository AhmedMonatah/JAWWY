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

import java.util.*

import com.example.weatherapp.ui.alerts.viewmodel.AlertsUiEvent
import com.example.weatherapp.ui.alerts.view.components.EmptyAlertsState
import com.example.weatherapp.ui.alerts.view.components.OverlayPermissionDialog
import com.example.weatherapp.ui.components.CommonScreenLayout
import com.example.weatherapp.ui.main.view.LocalSnackbarHostState

@Composable
fun AlertsScreen(
    navController: NavController,
    viewModel: AlertsViewModel = viewModel(factory = LocalAppContainer.current.viewModelFactory)
) {
    val alerts by viewModel.alerts.collectAsState()
    val language by viewModel.language.collectAsState()
    val locale = remember(language) { Locale(language) }

    val showBottomSheet by viewModel.showBottomSheet.collectAsState()
    val showPermissionDialog by viewModel.showPermissionDialog.collectAsState()
    val showNoInternetDialog by viewModel.showNoInternetDialog.collectAsState()

    val context = LocalContext.current
    val globalSelectionMode = LocalSelectionMode.current
    val snackbarHostState = LocalSnackbarHostState.current
    val selectedAlerts by viewModel.selectedAlerts.collectAsState()
    val scope = rememberCoroutineScope()

    val deletedString = stringResource(R.string.deleted)


    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is AlertsUiEvent.ShowSnackbar -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = deletedString + " " + event.count,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearSelection()
        }
    }

    BackHandler(enabled = selectedAlerts.isNotEmpty()) {
        viewModel.clearSelection()
    }

    LaunchedEffect(selectedAlerts.size) {
        globalSelectionMode.value = selectedAlerts.isNotEmpty()
    }

    CommonScreenLayout(
        title = stringResource(R.string.alerts_title),
        description = stringResource(R.string.alerts_description),
        isEmpty = alerts.isEmpty(),
        emptyContent = { EmptyAlertsState() },
        floatingActionButton = {
            if (!globalSelectionMode.value) {
                AppFloatingActionButton(
                    icon = Icons.Default.Alarm,
                    contentDescription = stringResource(R.string.add_alert),
                    onClick = {
                        if (viewModel.isOnline()) {
                            if (!Settings.canDrawOverlays(context)) {
                                viewModel.setShowPermissionDialog(true)
                            } else {
                                viewModel.setShowBottomSheet(true)
                            }
                        } else {
                            viewModel.setShowNoInternetDialog(true)
                        }
                    }
                )
            }
        },
        selectionBar = {
            SelectionDeleteBar(
                selectedCount = selectedAlerts.size,
                onClearSelection = { viewModel.clearSelection() },
                onDeleteSelected = {
                    if (viewModel.isOnline()) {
                        viewModel.deleteSelectedAlerts()
                    } else {
                        viewModel.setShowNoInternetDialog(true)
                    }
                }
            )
        },
        content = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 110.dp),
                modifier = Modifier.fillMaxSize()
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
    )

    if (showPermissionDialog) {
        OverlayPermissionDialog(onDismiss = { viewModel.setShowPermissionDialog(false) })
    }

    if (showBottomSheet) {
        AddAlertDialog(
            onDismiss = { viewModel.setShowBottomSheet(false) },
            onSave = { start, end, type ->
                viewModel.addAlert(start, end, type)
                viewModel.setShowBottomSheet(false)
            }
        )
    }

    if (showNoInternetDialog) {
        NoInternetConnectionDialog(onDismiss = { viewModel.setShowNoInternetDialog(false) })
    }
}
