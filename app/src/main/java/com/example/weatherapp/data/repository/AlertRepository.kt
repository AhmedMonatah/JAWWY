package com.example.weatherapp.data.repository

import com.example.weatherapp.data.local.dao.AlertDao
import com.example.weatherapp.data.local.entity.Alert
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlertRepository @Inject constructor(
    private val alertDao: AlertDao
) {
    fun getAllAlerts(): Flow<List<Alert>> = alertDao.getAllAlerts()

    suspend fun insertAlert(alert: Alert): Long = alertDao.insertAlert(alert)

    suspend fun deleteAlert(alert: Alert) = alertDao.deleteAlert(alert)
    
    suspend fun getActiveAlerts(): List<Alert> = alertDao.getActiveAlerts()
    
    suspend fun getAlertById(id: Int): Alert? = alertDao.getAlertById(id)

    suspend fun deleteAllAlerts() = alertDao.deleteAllAlerts()
}
