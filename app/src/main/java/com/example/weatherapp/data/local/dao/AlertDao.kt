package com.example.weatherapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.data.model.Alert
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {
    @Query("SELECT * FROM alerts")
    fun getAllAlerts(): Flow<List<Alert>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: Alert): Long

    @Delete
    suspend fun deleteAlert(alert: Alert)
    
    @Query("SELECT * FROM alerts WHERE isEnabled = 1")
    suspend fun getActiveAlerts(): List<Alert>

    @Query("SELECT * FROM alerts WHERE id = :id")
    suspend fun getAlertById(id: Int): Alert?

    @Query("DELETE FROM alerts")
    suspend fun deleteAllAlerts()
}
