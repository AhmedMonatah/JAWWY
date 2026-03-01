package com.example.weatherapp.ui.alerts.view.components

import android.app.AlarmManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.ui.favorites.view.components.TypeSelector
import com.example.weatherapp.ui.theme.RamadanDarkBlue
import com.example.weatherapp.ui.theme.RamadanDeepNavy
import com.example.weatherapp.ui.theme.RamadanGold
import java.util.Calendar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlertDialog(
    onDismiss: () -> Unit,
    onSave: (startTime: Long, endTime: Long, type: String, ringtoneUri: String?) -> Unit
) {
    val context = LocalContext.current
    val now = System.currentTimeMillis()

    var startTime by remember { mutableStateOf(now) }
    var endTime   by remember { mutableStateOf(now + 3_600_000L) }
    var type      by remember { mutableStateOf("notification") }

    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker   by remember { mutableStateOf(false) }

    // Step 2: schedule after notification permission granted
    var pendingSave by remember { mutableStateOf(false) }

    // POST_NOTIFICATIONS permission launcher
    val notifPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted && pendingSave) {
            onSave(startTime, if (type == "alarm") 0L else endTime, type, null)
        }
        pendingSave = false
        onDismiss()
    }

    // Exact alarm permission launcher (alarm type on API 31+)
    val exactAlarmLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val canSchedule = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else true
        if (canSchedule) {
            requestNotifThenSave(context, notifPermLauncher, setPending = { pendingSave = true }) {
                onSave(startTime, 0L, type, null)
                onDismiss()
            }
        } else {
            onDismiss()
        }
    }

    fun trySave() {
        if (type == "alarm") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (!alarmManager.canScheduleExactAlarms()) {
                    val intent = Intent(
                        Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                        Uri.parse("package:${context.packageName}")
                    )
                    exactAlarmLauncher.launch(intent)
                    return
                }
            }
        }
        requestNotifThenSave(context, notifPermLauncher, setPending = { pendingSave = true }) {
            onSave(startTime, if (type == "alarm") 0L else endTime, type, null)
            onDismiss()
        }
    }

    val cal = remember { Calendar.getInstance() }

    if (showStartPicker) {
        TimePickerDialog(
            label = stringResource(R.string.start_time_label),
            initialHour = cal.get(Calendar.HOUR_OF_DAY),
            initialMinute = cal.get(Calendar.MINUTE),
            onDismiss = { showStartPicker = false }
        ) { h, m ->
            cal.set(Calendar.HOUR_OF_DAY, h); cal.set(Calendar.MINUTE, m)
            cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
            startTime = cal.timeInMillis
            if (type == "notification" && endTime <= startTime) endTime = startTime + 3_600_000L
            showStartPicker = false
        }
    }

    if (showEndPicker) {
        TimePickerDialog(
            label = stringResource(R.string.end_time_label),
            initialHour = cal.get(Calendar.HOUR_OF_DAY),
            initialMinute = cal.get(Calendar.MINUTE),
            onDismiss = { showEndPicker = false }
        ) { h, m ->
            val tmp = Calendar.getInstance()
            tmp.set(Calendar.HOUR_OF_DAY, h); tmp.set(Calendar.MINUTE, m)
            tmp.set(Calendar.SECOND, 0); tmp.set(Calendar.MILLISECOND, 0)
            endTime = tmp.timeInMillis
            showEndPicker = false
        }
    }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = MaterialTheme.colorScheme.surface) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = stringResource(R.string.add_alert),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.alert_type),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TypeSelector(
                        text = stringResource(R.string.notification),
                        selected = type == "notification",
                        onClick = { type = "notification" },
                        modifier = Modifier.weight(1f)
                    )
                    TypeSelector(
                        text = stringResource(R.string.alarm),
                        selected = type == "alarm",
                        onClick = { type = "alarm" },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.duration),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))

                if (type == "alarm") {
                    TimeButton(
                        label = stringResource(R.string.start_time_label),
                        time = startTime,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showStartPicker = true }
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TimeButton(
                            label = stringResource(R.string.start_time_label),
                            time = startTime,
                            modifier = Modifier.weight(1f),
                            onClick = { showStartPicker = true }
                        )
                        TimeButton(
                            label = stringResource(R.string.end_time_label),
                            time = endTime,
                            modifier = Modifier.weight(1f),
                            onClick = { showEndPicker = true }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
                ) { Text(stringResource(R.string.cancel)) }

                Button(
                    onClick = { trySave() },
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    elevation = ButtonDefaults.buttonElevation(6.dp)
                ) {
                    Text(stringResource(R.string.save_alert), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

