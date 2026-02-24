package com.example.weatherapp.ui.map.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.ui.theme.TranslucentBlack

@Composable
fun MapHeader(source: String) {
    Surface(
        modifier = Modifier
            .padding(top = 40.dp, start = 20.dp, end = 20.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = TranslucentBlack,
        tonalElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.choose_location),
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.7f)
            )

            Text(
                text = if (source == "settings")
                    stringResource(R.string.set_home_location)
                else
                    stringResource(R.string.tap_to_select_city),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
    }
}
