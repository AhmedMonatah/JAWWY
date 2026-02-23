package com.example.weatherapp.data.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class AlarmWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val alertId = inputData.getInt("alertId", -1)
        if (alertId != -1) {
            NotificationHelper.showAlarmNotification(appContext, alertId)
        }
        return Result.success()
    }
}
