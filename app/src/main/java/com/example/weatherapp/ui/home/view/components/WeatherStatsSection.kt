package com.example.weatherapp.ui.home.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R


@Composable
fun WeatherStatsSection(
    pressure: Float,
    humidity: Float,
    wind: Float,
    clouds: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatArcCard(
                Modifier.weight(1f),
                stringResource(R.string.pressure),
                "${pressure.toInt()}",
                stringResource(R.string.unit_hpa),
                pressure / 1100f,
                Icons.Default.Speed
            )
            StatArcCard(
                Modifier.weight(1f),
                stringResource(R.string.humidity),
                "${humidity.toInt()}",
                stringResource(R.string.unit_percent),
                humidity / 100f,
                Icons.Default.WaterDrop
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatArcCard(
                Modifier.weight(1f),
                stringResource(R.string.wind),
                "${wind.toInt()}",
                stringResource(R.string.unit_ms),
                (wind / 30f).coerceIn(0f, 1f),
                Icons.Default.Air
            )
            StatArcCard(
                Modifier.weight(1f),
                stringResource(R.string.clouds),
                "$clouds",
                stringResource(R.string.unit_percent),
                clouds / 100f,
                Icons.Default.Cloud
            )
        }
    }
}
