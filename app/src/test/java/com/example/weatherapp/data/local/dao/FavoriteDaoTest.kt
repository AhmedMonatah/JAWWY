package com.example.weatherapp.data.local.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4

import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.model.FavoriteLocation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
class FavoriteDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: WeatherDatabase
    private lateinit var dao: FavoriteDao

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.favoriteDao()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertFavorite_retrievesSameFavorite() = runTest {
        // Given
        val favorite = FavoriteLocation(1, "Cairo", 30.0444, 31.2357, 25.0, "Sunny", "01d")

        // When
        dao.insertFavorite(favorite)

        // Then
        val result = dao.getFavoriteByCoords(30.0444, 31.2357)
        assertThat(result?.name, `is`("Cairo"))
    }

    @Test
    fun deleteFavorite_favoriteIsNoLongerInDatabase() = runTest {
        // Given
        val favorite = FavoriteLocation(1, "Cairo", 30.0444, 31.2357, 25.0, "Sunny", "01d")
        dao.insertFavorite(favorite)

        // When
        dao.deleteFavorite(favorite)

        // Then
        val result = dao.getFavoriteByCoords(30.0444, 31.2357)
        assertThat(result == null, `is`(true))
    }

    @Test
    fun getAllFavorites_returnsAllInsertedFavorites() = runTest {
        // Given
        val favorite1 = FavoriteLocation(1, "Cairo", 30.0444, 31.2357, 25.0, "Sunny", "01d")
        val favorite2 = FavoriteLocation(2, "Alexandria", 31.2001, 29.9187, 22.0, "Cloudy", "03d")
        dao.insertFavorite(favorite1)
        dao.insertFavorite(favorite2)

        // When
        val favorites = dao.getAllFavorites().first()

        // Then
        assertThat(favorites.size, `is`(2))
        assertThat(favorites[0].name, `is`("Cairo"))
        assertThat(favorites[1].name, `is`("Alexandria"))
    }
}
