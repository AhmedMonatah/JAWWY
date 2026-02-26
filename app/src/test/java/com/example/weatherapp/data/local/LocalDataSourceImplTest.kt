package com.example.weatherapp.data.local

import android.content.Context
import com.example.weatherapp.data.local.dao.AlertDao
import com.example.weatherapp.data.local.dao.FavoriteDao
import com.example.weatherapp.data.local.dao.WeatherDao
import com.example.weatherapp.model.FavoriteLocation
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class LocalDataSourceImplTest {

    private lateinit var localDataSource: LocalDataSourceImpl
    private val context = mockk<Context>(relaxed = true)
    private val weatherDao = mockk<WeatherDao>()
    private val alertDao = mockk<AlertDao>()
    private val favoriteDao = mockk<FavoriteDao>()

    @Before
    fun setup() {
        localDataSource = LocalDataSourceImpl(context, weatherDao, alertDao, favoriteDao)
    }

    @Test
    fun insertFavorite_callsDaoInsert() = runTest {
        // Given
        val favorite = FavoriteLocation(1, "Cairo", 30.0444, 31.2357, 25.0, "Sunny", "01d")
        coEvery { favoriteDao.insertFavorite(any()) } returns Unit

        // When
        localDataSource.insertFavorite(favorite)

        // Then
        coVerify { favoriteDao.insertFavorite(favorite) }
    }

    @Test
    fun deleteFavorite_callsDaoDelete() = runTest {
        // Given
        val favorite = FavoriteLocation(1, "Cairo", 30.0444, 31.2357, 25.0, "Sunny", "01d")
        coEvery { favoriteDao.deleteFavorite(any()) } returns Unit

        // When
        localDataSource.deleteFavorite(favorite)

        // Then
        coVerify { favoriteDao.deleteFavorite(favorite) }
    }

    @Test
    fun getFavorites_callsDaoGetAll() = runTest {
        // Given
        val favoritesList = listOf(
            FavoriteLocation(1, "Cairo", 30.0444, 31.2357, 25.0, "Sunny", "01d")
        )
        coEvery { favoriteDao.getAllFavorites() } returns flowOf(favoritesList)

        // When
        val result = localDataSource.getFavorites()

        // Then
        result.collect {
            assertThat(it, `is`(favoritesList))
        }
    }
}
