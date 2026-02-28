package com.example.weatherapp.data.repository

import android.content.Context
import com.example.weatherapp.data.local.LocalDataSource
import com.example.weatherapp.data.remote.RemoteDataSource
import com.example.weatherapp.model.FavoriteLocation
import com.example.weatherapp.model.WeatherEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import android.net.ConnectivityManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class WeatherRepositoryTest {

    private lateinit var repository: AppRepository
    private lateinit var context: Context
    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var localDataSource: LocalDataSource

    @Before
    fun setup() {
        context = mockk(relaxed = true)
        remoteDataSource = mockk(relaxed = true)
        localDataSource = mockk(relaxed = true)

        val connectivityManager = mockk<ConnectivityManager>(relaxed = true)
        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        
        repository = AppRepository(context, remoteDataSource, localDataSource)
    }

    @Test
    fun getFavorites_returnsDataFromLocalDataSource() = runTest {
        // Given
        val favorites = listOf(
            FavoriteLocation(1, "Cairo", 30.0444, 31.2357, 25.0, "Sunny", "01d")
        )
        coEvery { localDataSource.getFavorites() } returns flowOf(favorites)

        // When
        val result = repository.getFavorites().first()

        // Then
        assertThat(result, `is`(favorites))
        coVerify { localDataSource.getFavorites() }
    }

    @Test
    fun addFavorite_callsLocalDataSourceInsert() = runTest {
        // Given
        val favorite = FavoriteLocation(1, "Cairo", 30.0444, 31.2357, 25.0, "Sunny", "01d")
        coEvery { localDataSource.insertFavorite(any()) } returns Unit

        // When
        repository.addFavorite(favorite)

        // Then
        coVerify { localDataSource.insertFavorite(favorite) }
    }

    @Test
    fun removeFavorite_callsLocalDataSourceDelete() = runTest {
        // Given
        val favorite = FavoriteLocation(1, "Cairo", 30.0444, 31.2357, 25.0, "Sunny", "01d")
        coEvery { localDataSource.deleteFavorite(any()) } returns Unit

        // When
        repository.removeFavorite(favorite)

        // Then
        coVerify { localDataSource.deleteFavorite(favorite) }
    }
}
