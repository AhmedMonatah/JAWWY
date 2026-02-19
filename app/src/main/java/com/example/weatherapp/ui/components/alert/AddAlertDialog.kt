package com.example.weatherapp.ui.components.alert

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R
import com.example.weatherapp.ui.components.fav.TypeSelector
import com.example.weatherapp.ui.theme.RamadanDarkBlue
import com.example.weatherapp.ui.theme.RamadanDeepNavy
import com.example.weatherapp.ui.theme.RamadanGold
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlertDialog(
    onDismiss: () -> Unit,
    onSave: (Long, Long, String) -> Unit
) {
    var startTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var endTime by remember { mutableStateOf(System.currentTimeMillis() + 3600000) }
    var type by remember { mutableStateOf("notification") }
    val calendar = Calendar.getInstance()
    val startFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    val endFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            onSave(startTime, endTime, type)
        }
    }

    if (showStartTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE),
            is24Hour = false
        )

        AlertDialog(
            onDismissRequest = { showStartTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    calendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                    calendar.set(Calendar.MINUTE, timePickerState.minute)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    startTime = calendar.timeInMillis
                    if (endTime <= startTime) {
                        endTime = startTime + 3600000
                    }
                    showStartTimePicker = false
                }) { Text(stringResource(R.string.yes), color = RamadanGold) }
            },
            dismissButton = {
                TextButton(onClick = { showStartTimePicker = false }) { Text(stringResource(R.string.cancel), color = Color.Gray) }
            },
            containerColor = RamadanDarkBlue,
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimePicker(
                        state = timePickerState,
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

    if (showEndTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE),
            is24Hour = false
        )

        AlertDialog(
            onDismissRequest = { showEndTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val tempCal = Calendar.getInstance()
                    tempCal.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                    tempCal.set(Calendar.MINUTE, timePickerState.minute)
                    tempCal.set(Calendar.SECOND, 0)
                    tempCal.set(Calendar.MILLISECOND, 0)
                    endTime = tempCal.timeInMillis
                    showEndTimePicker = false
                }) { Text(stringResource(R.string.yes), color = RamadanGold) }
            },
            dismissButton = {
                TextButton(onClick = { showEndTimePicker = false }) { Text(stringResource(R.string.cancel), color = Color.Gray) }
            },
            containerColor = RamadanDarkBlue,
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimePicker(
                        state = timePickerState,
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

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = RamadanDarkBlue
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = stringResource(R.string.add_alert),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(stringResource(R.string.duration), style = MaterialTheme.typography.titleMedium, color = Color.White.copy(alpha = 0.8f))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { showStartTimePicker = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.start_time_label), fontSize = 12.sp, color = Color.Gray)
                            Text(startFormatter.format(Date(startTime)), fontWeight = FontWeight.Bold)
                        }
                    }
                    OutlinedButton(
                        onClick = { showEndTimePicker = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.end_time_label), fontSize = 12.sp, color = Color.Gray)
                            Text(endFormatter.format(Date(endTime)), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(stringResource(R.string.alert_type), style = MaterialTheme.typography.titleMedium, color = Color.White.copy(alpha = 0.8f))
                Row(
                    modifier = Modifier.fillMaxWidth().background(RamadanDeepNavy, RoundedCornerShape(12.dp)).padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TypeSelector(
                        text = stringResource(R.string.notification),
                        selected = type == "notification",
                        onClick = {
                            type = "notification"

                        },
                        modifier = Modifier.weight(1f)
                    )
                    TypeSelector(
                        text = stringResource(R.string.alarm),
                        selected = type == "alarm",
                        onClick = {
                            type = "alarm"

                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text(stringResource(R.string.cancel))
                }

                Button(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && type == "notification") {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            onSave(startTime, endTime, type)
                        }
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RamadanGold),
                    elevation = ButtonDefaults.buttonElevation(8.dp)
                ) {
                    Text(stringResource(R.string.save_alert), fontWeight = FontWeight.Bold)

                }}}
    }
}
