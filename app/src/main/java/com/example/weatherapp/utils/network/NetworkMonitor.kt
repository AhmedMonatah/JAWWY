package com.example.weatherapp.utils.network

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.example.weatherapp.R
import kotlinx.coroutines.flow.StateFlow

@Composable
fun NetworkMonitor(
    connectivityFlow: StateFlow<Boolean>,
    snackbarHostState: SnackbarHostState
) {
    val isOnline by connectivityFlow.collectAsState()
    val offlineMessage = stringResource(R.string.offline_mode)

    LaunchedEffect(isOnline) {
        if (!isOnline) {
            snackbarHostState.showSnackbar(
                message = offlineMessage,
                duration = SnackbarDuration.Short
            )
        } else {
            snackbarHostState.currentSnackbarData?.dismiss()
        }
    }
}
