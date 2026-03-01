package com.example.weatherapp.ui.favorites.view.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.model.FavoriteLocation

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavoriteItem(
    location: FavoriteLocation,
    selected: Boolean = false,
    onNavigate: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .combinedClickable(
                onClick = onNavigate,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.primary),
        border = if (selected) BorderStroke(3.dp, MaterialTheme.colorScheme.tertiary) else null
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.weather),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.TopStart)
            )
            Text(
                text = location.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                modifier = Modifier.align(Alignment.CenterStart).fillMaxWidth(0.7f)
            )

            Text(
                text = location.condition,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                modifier = Modifier.align(Alignment.BottomStart)
            )

            Surface(
                modifier = Modifier.size(50.dp).align(Alignment.TopEnd),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f)
            ) {
                val weatherIcon = when {
                    location.icon.startsWith("01") -> Icons.Default.WbSunny
                    location.icon.startsWith("02") || location.icon.startsWith("03") -> Icons.Default.Cloud
                    location.icon.startsWith("04") -> Icons.Default.Cloud
                    location.icon.startsWith("09") || location.icon.startsWith("10") -> Icons.Default.WaterDrop
                    location.icon.startsWith("11") -> Icons.Default.WaterDrop
                    location.icon.startsWith("13") -> Icons.Default.AcUnit
                    location.icon.startsWith("50") -> Icons.Default.Cloud
                    location.condition.lowercase().contains("cloud") -> Icons.Default.Cloud
                    location.condition.lowercase().contains("rain") -> Icons.Default.WaterDrop
                    location.condition.lowercase().contains("snow") -> Icons.Default.AcUnit
                    else -> Icons.Default.WbSunny
                }

                val iconColor = when {
                    location.icon.startsWith("01") -> Color.White
                    location.icon.startsWith("10") || location.icon.startsWith("09") -> Color(0xFF64B5F6)
                    else -> MaterialTheme.colorScheme.onPrimary
                }

                Icon(
                    imageVector = weatherIcon,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp),
                    tint = iconColor
                )
            }

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Text(
                    text = "${location.currentTemp.toInt()}°",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }
        }
    }
}
