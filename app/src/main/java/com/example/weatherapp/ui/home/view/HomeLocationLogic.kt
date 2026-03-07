package com.example.weatherapp.ui.home.view

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.core.content.ContextCompat.checkSelfPermission
import com.example.weatherapp.ui.home.viewmodel.HomeViewModel

@Composable
fun HandleLocationPermissionsAndRefresh(
    lat: Double?,
    lon: Double?,
    viewModel: HomeViewModel,
    context: Context,
    showLocationDialog: MutableState<Boolean>
) {
    val hasRequestedPermission = rememberSaveable { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasRequestedPermission.value = true
        if (permissions[ACCESS_FINE_LOCATION] == true ||
            permissions[ACCESS_COARSE_LOCATION] == true) {
            showLocationDialog.value = false
            viewModel.requestCurrentLocation()
        } else {
            showLocationDialog.value = true
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (lat == null && lon == null) {
                    val hasFine = checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
                    val hasCoarse = checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
                    if (hasFine || hasCoarse) {
                        showLocationDialog.value = false
                        viewModel.requestCurrentLocation()
                    } else if (hasRequestedPermission.value) {
                        showLocationDialog.value = true
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(lat, lon) {
        if (lat != null && lon != null) {
            viewModel.refreshWeather(lat, lon)
        } else {
            val hasFine = checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
            val hasCoarse = checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED

            if (hasFine || hasCoarse) {
                showLocationDialog.value = false
                viewModel.requestCurrentLocation()
            } else if (!hasRequestedPermission.value) {
                locationPermissionLauncher.launch(
                    arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
                )
            }
        }
    }
}
