package com.example.weatherapp.ui.alerts.view.components

import android.Manifest
import android.app.AlarmManager
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
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R
import com.example.weatherapp.ui.favorites.view.components.TypeSelector
import com.example.weatherapp.ui.theme.RamadanDarkBlue
import com.example.weatherapp.ui.theme.RamadanDeepNavy
import com.example.weatherapp.ui.theme.RamadanGold
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// ─── Shared time-picker dialog ────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
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

// ─── Bottom-sheet time button ─────────────────────────────────────────────────

@Composable
private fun TimeButton(label: String, time: Long, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val formatter = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(formatter.format(Date(time)), fontWeight = FontWeight.Bold)
        }
    }
}

// ─── Main bottom-sheet ────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlertDialog(
    onDismiss: () -> Unit,
    onSave: (startTime: Long, endTime: Long, type: String) -> Unit
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

    // POST_NOTIFICATIONS permission launcher (needed for BOTH alarm + notification)
    val notifPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted && pendingSave) {
            onSave(startTime, if (type == "alarm") 0L else endTime, type)
        }
        pendingSave = false
        onDismiss()
    }

    // Exact alarm permission launcher (alarm type on API 31+)
    val exactAlarmLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val canSchedule = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else true
        if (canSchedule) {
            // Now also ask for notification permission if needed
            requestNotifThenSave(context, notifPermLauncher, setPending = { pendingSave = true }) {
                onSave(startTime, 0L, type)
                onDismiss()
            }
        } else {
            onDismiss()
        }
    }

    fun trySave() {
        if (type == "alarm") {
            // 1. Check exact alarm permission first
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
        // 2. Request POST_NOTIFICATIONS for both types (needed to show notification/alarm)
        requestNotifThenSave(context, notifPermLauncher, setPending = { pendingSave = true }) {
            onSave(startTime, if (type == "alarm") 0L else endTime, type)
            onDismiss()
        }
    }

    // ── Time picker dialogs ──
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

    // ── Bottom sheet ──
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = RamadanDarkBlue) {
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
                color = Color.White
            )

            // ── Type selector ──
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.alert_type),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.8f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(RamadanDeepNavy, RoundedCornerShape(12.dp))
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

            // ── Time pickers ──
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.duration),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.8f))

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

            // ── Action buttons ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) { Text(stringResource(R.string.cancel)) }

                Button(
                    onClick = { trySave() },
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RamadanGold),
                    elevation = ButtonDefaults.buttonElevation(6.dp)
                ) {
                    Text(stringResource(R.string.save_alert), fontWeight = FontWeight.Bold, color = RamadanDeepNavy)
                }
            }
        }
    }
}

// ─── Helper: request POST_NOTIFICATIONS then execute save ────────────────────

private fun requestNotifThenSave(
    context: Context,
    launcher: androidx.activity.result.ActivityResultLauncher<String>,
    setPending: () -> Unit,
    onAlreadyGranted: () -> Unit
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val granted = context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
        if (granted) {
            onAlreadyGranted()
        } else {
            setPending()
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    } else {
        onAlreadyGranted()
    }
}
