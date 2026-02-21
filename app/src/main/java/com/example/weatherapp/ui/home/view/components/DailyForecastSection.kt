package com.example.weatherapp.ui.home.view.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.model.ForecastEntity
import com.example.weatherapp.model.WeatherEntity
import com.example.weatherapp.ui.home.view.components.getWeatherIconRes
import com.example.weatherapp.ui.home.view.components.getWeatherIconTint
import com.example.weatherapp.ui.theme.AccentPurple
import com.example.weatherapp.ui.theme.TranslucentBlack

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@Composable
fun DailyForecastSection(
    forecast: List<ForecastEntity>,
    currentWeather: WeatherEntity?,
    selectedIndex: Int,
    onDaySelected: (Int) -> Unit,
    isDark: Boolean,
    locale: Locale
) {
    Column {
        Text(
            stringResource(R.string.daily_forecast),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color.White else Color.Black
        )
        Spacer(Modifier.height(16.dp))
        
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                DailyCardItem(
                    dayName = stringResource(R.string.today),
                    temp = currentWeather?.temp?.roundToInt() ?: 0,
                    icon = currentWeather?.icon,
                    isSelected = selectedIndex == 0,
                    onClick = { onDaySelected(0) },
                    isDark = isDark,
                    isToday = true
                )
            }
            
            itemsIndexed(forecast) { index, item ->
                val dayName = SimpleDateFormat("EEE", locale).format(Date(item.dt * 1000))
                DailyCardItem(
                    dayName = dayName,
                    temp = item.tempDay.roundToInt(),
                    icon = item.icon,
                    isSelected = selectedIndex == index + 1,
                    onClick = { onDaySelected(index + 1) },
                    isDark = isDark
                )
            }
        }
    }
}

@Composable
fun DailyCardItem(
    dayName: String,
    temp: Int,
    icon: String? = null,
    isSelected: Boolean,
    onClick: () -> Unit,
    isDark: Boolean,
    isToday: Boolean = false
) {
    val containerColor = if (isSelected) AccentPurple else if (isDark) TranslucentBlack.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.5f)
    val contentColor = if (isSelected) Color.White else if (isDark) Color.White.copy(alpha = 0.7f) else Color.Gray
    
    Card(
        modifier = Modifier
            .width(85.dp)
            .height(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(dayName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = contentColor)
            
            if (icon != null) {
                val iconRes = getWeatherIconRes(icon, "")
                val iconTint = if (isSelected) Color.White else getWeatherIconTint(icon, "")
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = iconTint
                )
            } else {
                 Icon(painterResource(id = R.drawable.ic_cloud), null, Modifier.size(28.dp), if (isSelected) Color.White else AccentPurple)
            }

            if (!isToday || temp != 0) {
                Text(
                    "$temp°",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else if (isDark) Color.White else Color.Black
                )
            } else {
                Text("-", style = MaterialTheme.typography.titleMedium, color = contentColor)
            }
        }
    }
}
