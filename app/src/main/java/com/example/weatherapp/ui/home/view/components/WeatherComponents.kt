package com.example.weatherapp.ui.home.view.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.weatherapp.ui.theme.LocalIsDark
@Composable
fun StatArcCard(
    modifier: Modifier,
    title: String,
    value: String,
    unit: String,
    progress: Float,
    icon: ImageVector
) {
    val isDark = LocalIsDark.current
    val iconBgColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    val iconTintColor = MaterialTheme.colorScheme.primary

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color.Transparent else Color.White,
            contentColor = MaterialTheme.colorScheme.onBackground
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // padding مناسب
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(35.dp),
                    shape = CircleShape,
                    color = iconBgColor
                ) {
                    Icon(icon, contentDescription = null, modifier = Modifier.padding(8.dp), tint = iconTintColor)
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }

            Spacer(Modifier.height(16.dp))

            SegmentedArcIndicator(
                progress = progress,
                label = value,
                unit = unit,
                modifier = Modifier.size(160.dp)
            )
        }
    }
}