package com.example.weatherapp.data.local.dao

import androidx.room.*
import com.example.weatherapp.data.local.entity.FavoriteLocation
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<FavoriteLocation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteLocation)

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteLocation)

    @Query("SELECT * FROM favorites WHERE lat = :lat AND lon = :lon LIMIT 1")
    suspend fun getFavoriteByCoords(lat: Double, lon: Double): FavoriteLocation?
}
