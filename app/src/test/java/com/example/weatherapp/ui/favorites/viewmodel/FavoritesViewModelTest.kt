package com.example.weatherapp.ui.favorites.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.model.FavoriteLocation
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class FavoritesViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: FavoritesViewModel
    private val repository = mockk<WeatherRepository>(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        every { repository.getFavorites() } returns flowOf(emptyList())
        every { repository.unitsFlow } returns flowOf("metric")
        every { repository.languageFlow } returns flowOf("en")

        viewModel = FavoritesViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun favorites_initialValueIsEmptyList() = runTest {
        viewModel.favorites.test {
            assertThat(awaitItem(), `is`(emptyList<FavoriteLocation>()))
        }
    }

    @Test
    fun addFavorite_callsRepositoryAddFavorite() = runTest {
        // Given
        val favorite = FavoriteLocation(1, "Cairo", 30.0444, 31.2357, 25.0, "Sunny", "01d")
        coEvery { repository.addFavorite(any()) } returns Unit

        // When
        viewModel.addFavorite(favorite)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { repository.addFavorite(favorite) }
    }

    @Test
    fun toggleSelection_addsAndRemovesFromSelectedFavorites() = runTest {
        val favorite = FavoriteLocation(1, "Cairo", 30.0444, 31.2357, 25.0, "Sunny", "01d")
        
        viewModel.selectedFavorites.test {
            assertThat(awaitItem(), `is`(emptySet<FavoriteLocation>()))
            
            viewModel.toggleSelection(favorite)
            assertThat(awaitItem(), `is`(setOf(favorite)))
            
            viewModel.toggleSelection(favorite)
            assertThat(awaitItem(), `is`(emptySet<FavoriteLocation>()))
        }
    }
}
