package com.example.weatherapp.ui.home.view.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.model.HourlyForecastEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt


@Composable
fun HourlyForecastSection(
    hourly: List<HourlyForecastEntity>,
    locale: Locale,
    isDark: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            stringResource(R.string.hourly_forecast),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            itemsIndexed(hourly) { index, item ->
                val time = SimpleDateFormat("h a", locale).format(Date(item.dt * 1000))
                HourlyForecastItem(time, item.temp.roundToInt(), item.description, item.icon, isDark)
            }
        }
    }
}

@Composable
fun HourlyForecastItem(time: String, temp: Int, description: String, icon: String, isDark: Boolean) {
    val containerColor = if (isDark) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    } else {
        Color.White.copy(alpha = 0.5f)
    }
    val contentColor = MaterialTheme.colorScheme.onSurface
    val timeColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)

    Card(
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = Modifier.width(70.dp).height(120.dp),
        border = if (isDark) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
        } else {
            BorderStroke(1.dp, Color.White.copy(alpha = 0.4f))
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(time, style = MaterialTheme.typography.bodySmall, color = timeColor)

            val iconRes = getWeatherIconRes(icon, description)
            val iconTint = getWeatherIconTint(icon, description)

            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = iconTint
            )

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Text(
                    "$temp°",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }
        }
    }
}

