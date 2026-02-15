package com.example.weatherapp.ui.map.view

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.weatherapp.ui.map.viewmodel.MapViewModel
import com.example.weatherapp.ui.theme.AccentPurple
import com.example.weatherapp.ui.theme.TranslucentBlack
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*

@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    source: String = "favorites",
    onLocationSelected: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedPoint by remember { mutableStateOf<LatLng?>(null) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(30.0444, 31.2357), 5f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = false,
                mapStyleOptions = null // Can add dark mode json here
            ),
            uiSettings = MapUiSettings(zoomControlsEnabled = false),
            onMapClick = { selectedPoint = it }
        ) {
            selectedPoint?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Selected Location",
                    snippet = "Lat: ${it.latitude}, Lon: ${it.longitude}"
                )
            }
        }

        // Modern Header
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 40.dp, start = 20.dp, end = 20.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = TranslucentBlack,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "CHOOSE LOCATION",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Text(
                    if (source == "settings") "Set your home location" else "Tap on the map to select a city",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        }

        // Sleek Floating Button
        if (selectedPoint != null) {
            Button(
                onClick = {
                    selectedPoint?.let {
                        viewModel.selectLocation(it.latitude, it.longitude, source)
                        onLocationSelected(source)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 50.dp)
                    .height(60.dp)
                    .fillMaxWidth(0.7f),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MyLocation, null, tint = Color.White)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        if (source == "settings") "SET THIS LOCATION" else "SAVE THIS LOCATION",
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}
