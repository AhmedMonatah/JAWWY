package com.example.weatherapp.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.weatherapp.ui.screens.*

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Settings : Screen("settings")
    object Favorites : Screen("favorites")
    object Alerts : Screen("alerts")
    object Alarm : Screen("alarm")
    object Search : Screen("search")
}

@Composable
fun WeatherNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }
        composable(Screen.Favorites.route) {
            FavoritesScreen(navController)
        }
        composable(Screen.Alerts.route) {
            AlertsScreen(navController)
        }
        composable(Screen.Alarm.route) {
            AlarmScreen(navController)
        }
        composable(Screen.Search.route) {
            AlarmScreen(navController)
        }
    }
}
