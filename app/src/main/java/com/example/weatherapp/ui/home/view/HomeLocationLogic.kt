package com.example.weatherapp.ui.home.view

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat.checkSelfPermission
import com.example.weatherapp.ui.home.viewmodel.HomeViewModel

@Composable
fun HandleLocationPermissionsAndRefresh(
    lat: Double?,
    lon: Double?,
    viewModel: HomeViewModel,
    context: Context
) {
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[ACCESS_FINE_LOCATION] == true ||
            permissions[ACCESS_COARSE_LOCATION] == true) {
            viewModel.requestCurrentLocation()
        }
    }

    LaunchedEffect(lat, lon) {
        if (lat != null && lon != null) {
            viewModel.refreshWeather(lat, lon)
        } else {
            val hasFine = checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
            val hasCoarse = checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED

            if (hasFine || hasCoarse) {
                viewModel.requestCurrentLocation()
            } else {
                locationPermissionLauncher.launch(
                    arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
                )
            }
        }
    }
}
