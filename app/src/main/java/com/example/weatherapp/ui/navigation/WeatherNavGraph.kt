package com.example.weatherapp.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.weatherapp.ui.alerts.view.AlertsScreen
import com.example.weatherapp.ui.favorites.view.FavoritesScreen
import com.example.weatherapp.ui.home.view.HomeScreen
import com.example.weatherapp.ui.settings.view.SettingsScreen
import com.example.weatherapp.ui.alarm.view.AlarmScreen

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
            HomeScreen(navController = navController)
        }
        composable(Screen.Settings.route) {
             SettingsScreen(navController = navController)
        }
        composable(Screen.Favorites.route) {
            FavoritesScreen(navController = navController)
        }
        composable(Screen.Alerts.route) {
            AlertsScreen(navController = navController)
        }
        composable(Screen.Alarm.route) {
            AlarmScreen(navController = navController)
        }
    }
}
