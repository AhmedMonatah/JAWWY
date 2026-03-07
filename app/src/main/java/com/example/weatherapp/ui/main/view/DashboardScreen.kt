package com.example.weatherapp.ui.main.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.weatherapp.ui.alerts.view.AlertsScreen
import com.example.weatherapp.ui.favorites.view.FavoritesScreen
import com.example.weatherapp.ui.home.view.HomeScreen
import com.example.weatherapp.ui.settings.view.SettingsScreen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    pagerState: PagerState
) {
    // Simplified: No longer synchronizes with routes. Just hosts the pager.
    // This allows animateScrollToPage to work seamlessly without jitter.
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        userScrollEnabled = true
    ) { page ->
        when (page) {
            0 -> HomeScreen(navController = navController)
            1 -> FavoritesScreen(navController = navController)
            2 -> AlertsScreen(navController = navController)
            3 -> SettingsScreen(navController = navController)
        }
    }
}
