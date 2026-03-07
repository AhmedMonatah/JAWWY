package com.example.weatherapp.ui.map.view.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R

@Composable
fun MapHeader(source: String) {
    val isDark = com.example.weatherapp.ui.theme.LocalIsDark.current
    Surface(
        modifier = Modifier
            .padding(top = 40.dp, start = 20.dp, end = 20.dp)
            .fillMaxWidth()
            .then(
                if (!isDark) Modifier.border(
                    1.dp, androidx.compose.ui.graphics.Color.White.copy(alpha = 0.4f), RoundedCornerShape(24.dp)
                ) else Modifier
            ),
        shape = RoundedCornerShape(24.dp),
        color = if (isDark) {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
        } else {
            androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f)
        },
        tonalElevation = if (isDark) 8.dp else 0.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.choose_location),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Text(
                text = if (source == "settings")
                    stringResource(R.string.set_home_location)
                else
                    stringResource(R.string.tap_to_select_city),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
