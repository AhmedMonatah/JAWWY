package com.example.weatherapp.ui.components.fav

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.model.FavoriteLocation
import com.example.weatherapp.ui.theme.AccentBlue
import com.example.weatherapp.ui.theme.AccentPurple


@Composable
fun FavoriteItem(location: FavoriteLocation, onNavigate: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(140.dp).clickable { onNavigate() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = AccentPurple)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Top Left: Section label
            Text(
                text = stringResource(R.string.weather),
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.TopStart)
            )
            Text(
                text = location.name,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterStart)
            )

            Text(
                text = location.condition,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.align(Alignment.BottomStart)
            )

            Surface(
                modifier = Modifier.size(50.dp).align(Alignment.TopEnd),
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.15f)
            ) {
                val weatherIcon = when {
                    location.icon.startsWith("01") -> Icons.Default.WbSunny
                    location.icon.startsWith("02") || location.icon.startsWith("03") -> Icons.Default.Cloud
                    location.icon.startsWith("04") -> Icons.Default.Cloud
                    location.icon.startsWith("09") || location.icon.startsWith("10") -> Icons.Default.WaterDrop
                    location.icon.startsWith("11") -> Icons.Default.WaterDrop
                    location.icon.startsWith("13") -> Icons.Default.AcUnit
                    location.icon.startsWith("50") -> Icons.Default.Cloud
                    // Fallback to text matching if icon code not useful
                    location.condition.lowercase().contains("cloud") -> Icons.Default.Cloud
                    location.condition.lowercase().contains("rain") -> Icons.Default.WaterDrop
                    location.condition.lowercase().contains("snow") -> Icons.Default.AcUnit
                    else -> Icons.Default.WbSunny
                }

                val iconColor = when {
                    location.icon.startsWith("01") -> Color.Yellow
                    location.icon.startsWith("10") || location.icon.startsWith("09") -> AccentBlue
                    else -> Color.White
                }

                Icon(
                    imageVector = weatherIcon,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp),
                    tint = iconColor
                )
            }

            Text(
                text = "${location.currentTemp.toInt()}°",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}
