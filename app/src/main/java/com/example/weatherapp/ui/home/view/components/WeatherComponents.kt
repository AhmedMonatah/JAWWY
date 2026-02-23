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

import com.example.weatherapp.ui.theme.*

@Composable
fun StatArcCard(modifier: Modifier, title: String, value: String, unit: String, progress: Float, icon: ImageVector) {
    val isDark = true
    val containerColor = if (isDark) RamadanMidnight.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surfaceContainerHigh
    val borderStroke = if (isDark) BorderStroke(1.dp, RamadanGold.copy(alpha = 0.3f)) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    val iconBgColor = if (isDark) RamadanGold.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primaryContainer
    val iconTintColor = if (isDark) RamadanGold else MaterialTheme.colorScheme.primary

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 4.dp),
        border = borderStroke
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(35.dp),
                    shape = CircleShape,
                    color = iconBgColor
                ) {
                    Icon(icon, null, Modifier.padding(8.dp), iconTintColor)
                }
                Spacer(Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.labelSmall, color = if (isDark) RamadanMoonGlow else Color.Gray)
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

