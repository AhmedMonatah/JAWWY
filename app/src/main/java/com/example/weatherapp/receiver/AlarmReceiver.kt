package com.example.weatherapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.weatherapp.ui.service.NotificationHelper

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alertId = intent.getIntExtra("alertId", -1)
        if (alertId != -1) {
            NotificationHelper.showAlarmNotification(context, alertId)
        }
    }
}
