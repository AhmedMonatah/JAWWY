package com.example.weatherapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.weatherapp.data.service.NotificationHelper

class AlertReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alertId = intent.getIntExtra("ALERT_ID", -1)
        val alertType = intent.getStringExtra("ALERT_TYPE") ?: "alarm"

        Log.d("AlertReceiver", "onReceive: alertId=$alertId, type=$alertType")

        if (alertId != -1 && alertType == "alarm") {
            NotificationHelper.showAlarmNotification(context, alertId)
        }
    }
}
