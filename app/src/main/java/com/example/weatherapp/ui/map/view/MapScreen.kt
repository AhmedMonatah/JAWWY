package com.example.weatherapp.ui.map.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.di.LocalAppContainer
import com.example.weatherapp.ui.map.viewmodel.MapViewModel
import com.example.weatherapp.ui.main.view.LocalSnackbarHostState
import com.example.weatherapp.ui.theme.AccentPurple
import com.example.weatherapp.ui.theme.TranslucentBlack
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import com.example.weatherapp.R
import com.example.weatherapp.ui.map.view.components.MapHeader
import com.example.weatherapp.ui.map.view.components.MapActionButton

@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel(factory = LocalAppContainer.current.viewModelFactory),
    source: String = "favorites",
    onLocationSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val selectedPoint by viewModel.selectedPoint.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val scope = rememberCoroutineScope()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(30.0444, 31.2357), 2f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val isDark = MaterialTheme.colorScheme.background != Color.White
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = false,
                minZoomPreference = 2f,
                maxZoomPreference = 20f,
                mapStyleOptions = if (isDark) {
                    MapStyleOptions.loadRawResourceStyle(context, R.raw.map_dark_style)
                } else null
            ),
            uiSettings = MapUiSettings(zoomControlsEnabled = false),
            onMapClick = { point ->
                viewModel.updateSelectedPoint(point)
                scope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(point, 10f)
                    )
                }
            }
        ) {
            selectedPoint?.let { Marker(state = MarkerState(it), title = "Selected Location") }
        }

        Box(modifier = Modifier.align(Alignment.TopCenter)) {
            MapHeader(source = source)
        }

        LaunchedEffect(Unit) {
            viewModel.navigateToPrevious.collect {
                onLocationSelected(source)
            }
        }

        selectedPoint?.let { point ->
            MapActionButton(
                source = source,
                isLoading = isLoading,
                onClick = {
                    viewModel.selectLocation(point.latitude, point.longitude, source)
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
