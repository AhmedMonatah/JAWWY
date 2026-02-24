package com.example.weatherapp.ui.alerts.view.components

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.weatherapp.R
import com.example.weatherapp.ui.theme.RamadanDarkBlue
import com.example.weatherapp.ui.theme.RamadanGold

@Composable
fun OverlayPermissionDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = RamadanDarkBlue,
        title = { Text(stringResource(R.string.permission_required), color = Color.White) },
        text = { Text(stringResource(R.string.overlay_permission_desc), color = Color.White.copy(alpha = 0.7f)) },
        confirmButton = {
            Button(
                onClick = {
                    context.startActivity(Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${context.packageName}")
                    ))
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = RamadanGold)
            ) { Text(stringResource(R.string.grant)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = Color.White.copy(alpha = 0.6f))
            }
        }
    )
}
