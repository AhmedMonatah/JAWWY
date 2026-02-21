package com.example.weatherapp.ui.main.view.components

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
import com.example.weatherapp.ui.islamic.view.IslamicScreen
import com.example.weatherapp.ui.settings.view.SettingsScreen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DashboardPager(
    navController: NavHostController,
    pagerState: PagerState
) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
    ) { page ->
        when (page) {
            0 -> HomeScreen(navController = navController)
            1 -> IslamicScreen(navController = navController)
            2 -> FavoritesScreen (navController = navController)
            3 -> AlertsScreen(navController = navController)
            4 -> SettingsScreen(navController = navController)
        }
    }
}
