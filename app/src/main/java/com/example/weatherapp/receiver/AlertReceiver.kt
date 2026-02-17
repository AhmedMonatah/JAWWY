package com.example.weatherapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.weatherapp.worker.AlertWorker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alertId = intent.getIntExtra("ALERT_ID", -1)
        val alertType = intent.getStringExtra("ALERT_TYPE") ?: "notification"

        if (alertId != -1) {
            val workRequest = OneTimeWorkRequestBuilder<AlertWorker>()
                .setInputData(workDataOf(
                    "ALERT_ID" to alertId,
                    "ALERT_TYPE" to alertType
                ))
                .setExpedited(androidx.work.OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}
