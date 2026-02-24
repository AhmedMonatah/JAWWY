package com.example.weatherapp.ui.alerts.view.components

import android.Manifest
import android.content.Context
import android.os.Build


fun requestNotifThenSave(
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
