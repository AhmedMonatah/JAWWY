package com.example.weatherapp.ui.home.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.model.WeatherEntity
import com.google.android.gms.location.FusedLocationProviderClient
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
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: HomeViewModel
    private val repository = mockk<WeatherRepository>(relaxed = true)
    private val locationClient = mockk<FusedLocationProviderClient>(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        every { repository.getCurrentWeather() } returns flowOf(null)
        every { repository.getForecast() } returns flowOf(emptyList())
        every { repository.getHourlyForecast() } returns flowOf(emptyList())
        every { repository.unitsFlow } returns flowOf("metric")
        every { repository.languageFlow } returns flowOf("en")
        every { repository.locationModeFlow } returns flowOf("gps")
        every { repository.manualLocationFlow } returns flowOf(null)

        viewModel = HomeViewModel(repository, locationClient)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun units_initialValueIsMetric() = runTest {
        viewModel.units.test {
            assertThat(awaitItem(), `is`("metric"))
        }
    }

    @Test
    fun language_initialValueIsEn() = runTest {
        viewModel.language.test {
            assertThat(awaitItem(), `is`("en"))
        }
    }

    @Test
    fun selectDay_updatesSelectedDayIndex() = runTest {
        viewModel.selectedDayIndex.test {
            assertThat(awaitItem(), `is`(0))
            viewModel.selectDay(2)
            assertThat(awaitItem(), `is`(2))
        }
    }
}
