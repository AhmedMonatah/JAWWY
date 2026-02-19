package com.example.weatherapp.ui.islamic.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.weatherapp.ui.theme.RamadanGold
import com.example.weatherapp.utils.PrayerTimeInfo


@Composable
fun PrayerRowItem(prayer: PrayerTimeInfo) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (prayer.isNext) Color.White.copy(alpha = 0.1f) else Color.Transparent,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(if (prayer.isNext) RamadanGold else Color.Gray.copy(alpha = 0.3f), CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(prayer.nameResId),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (prayer.isNext) RamadanGold else Color.White,
                    fontWeight = if (prayer.isNext) FontWeight.Bold else FontWeight.Normal
                )
            }

            Text(
                text = prayer.time,
                style = MaterialTheme.typography.titleMedium,
                color = if (prayer.isNext) RamadanGold else Color.White.copy(alpha = 0.6f),
                fontWeight = if (prayer.isNext) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
