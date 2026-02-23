package com.example.weatherapp.utils.network

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.StateFlow


@Composable
fun runIfOnline(
    connectivityFlow: StateFlow<Boolean>,
    onOffline: () -> Unit,
    action: () -> Unit
): () -> Unit {
    val isOnline by connectivityFlow.collectAsState()
    
    return {
        if (isOnline) {
            action()
        } else {
            onOffline()
        }
    }
}
