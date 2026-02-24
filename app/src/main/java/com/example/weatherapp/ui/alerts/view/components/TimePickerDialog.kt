package com.example.weatherapp.ui.alerts.view.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.weatherapp.R
import com.example.weatherapp.ui.theme.RamadanDarkBlue
import com.example.weatherapp.ui.theme.RamadanDeepNavy
import com.example.weatherapp.ui.theme.RamadanGold


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    label: String,
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    val state = rememberTimePickerState(initialHour = initialHour, initialMinute = initialMinute, is24Hour = false)
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = RamadanDarkBlue,
        title = { Text(label, color = Color.White, fontWeight = FontWeight.Bold) },
        confirmButton = {
            TextButton(onClick = { onConfirm(state.hour, state.minute) }) {
                Text(stringResource(R.string.yes), color = RamadanGold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = Color.Gray)
            }
        },
        text = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TimePicker(
                    state = state,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = RamadanDeepNavy,
                        clockDialSelectedContentColor = RamadanDeepNavy,
                        clockDialUnselectedContentColor = RamadanGold.copy(alpha = 0.6f),
                        selectorColor = RamadanGold,
                        periodSelectorSelectedContainerColor = RamadanGold,
                        periodSelectorUnselectedContainerColor = Color.Transparent,
                        periodSelectorSelectedContentColor = RamadanDeepNavy,
                        periodSelectorUnselectedContentColor = RamadanGold,
                        timeSelectorSelectedContainerColor = RamadanGold,
                        timeSelectorUnselectedContainerColor = RamadanDeepNavy,
                        timeSelectorSelectedContentColor = RamadanDeepNavy,
                        timeSelectorUnselectedContentColor = RamadanGold
                    )
                )
            }
        }
    )
}