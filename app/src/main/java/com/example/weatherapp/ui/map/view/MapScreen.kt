package com.example.weatherapp.ui.map.view

import android.content.Context
import android.preference.PreferenceManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.weatherapp.ui.map.viewmodel.MapViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    onLocationSelected: () -> Unit
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    var selectedLocation by remember { mutableStateOf<GeoPoint?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {

        val sharedPreferences = context.getSharedPreferences(
            context.packageName + "_preferences",
            Context.MODE_PRIVATE
        )
        Configuration.getInstance().load(context, sharedPreferences)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(5.0)
        mapView.controller.setCenter(GeoPoint(30.0444, 31.2357))
    }
    DisposableEffect(mapView) {
        onDispose {
            mapView.onDetach()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                mapView.apply {
                    val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
                        override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                            kotlin.run {
                                selectedLocation = p
                                overlays.removeAll { it is Marker }
                                val marker = Marker(this@apply)
                                marker.position = p
                                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                overlays.add(marker)
                                invalidate()
                            }
                            return true
                        }

                        override fun longPressHelper(p: GeoPoint?): Boolean {
                            return false
                        }
                    })
                    overlays.add(mapEventsOverlay)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        Button(
            onClick = {
                selectedLocation?.let {
                    viewModel.selectLocation(it.latitude, it.longitude)
                    onLocationSelected()
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter).padding(32.dp),
            enabled = selectedLocation != null
        ) {
            Text("Select This Location")
        }
    }
}
