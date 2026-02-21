package com.example.weatherapp.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.weatherapp.R
import com.example.weatherapp.ui.theme.RamadanDarkBlue
import com.example.weatherapp.ui.theme.RamadanGold

@Composable
fun NoInternetConnectionDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.no_internet_title),
                color = Color.White
            )
        },
        text = {
            Text(
                text = stringResource(R.string.no_internet_message),
                color = Color.White.copy(alpha = 0.7f)
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RamadanGold
                )
            ) {
                Text(text = stringResource(id = R.string.ok))
            }
        },
        containerColor = RamadanDarkBlue
    )
}
