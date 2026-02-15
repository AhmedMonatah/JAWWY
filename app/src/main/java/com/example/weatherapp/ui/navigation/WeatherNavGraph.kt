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
    object Home : Screen("home?lat={lat}&lon={lon}&city={city}") {
        fun createRoute(lat: Double? = null, lon: Double? = null, city: String? = null): String {
            return if (lat != null && lon != null && city != null) {
                "home?lat=$lat&lon=$lon&city=$city"
            } else "home"
        }
    }
    object Settings : Screen("settings")
    object Favorites : Screen("favorites")
    object Alerts : Screen("alerts")
    object Alarm : Screen("alarm")
    object Map : Screen("map?source={source}") {
        fun createRoute(source: String = "favorites") = "map?source=$source"
    }
}

@Composable
fun WeatherNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable(
            route = Screen.Home.route,
            arguments = listOf(
                androidx.navigation.navArgument("lat") { nullable = true; type = androidx.navigation.NavType.StringType },
                androidx.navigation.navArgument("lon") { nullable = true; type = androidx.navigation.NavType.StringType },
                androidx.navigation.navArgument("city") { nullable = true; type = androidx.navigation.NavType.StringType }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull()
            val lon = backStackEntry.arguments?.getString("lon")?.toDoubleOrNull()
            val city = backStackEntry.arguments?.getString("city")
            
            HomeScreen(navController = navController, lat = lat, lon = lon, cityName = city)
        }
        composable(Screen.Settings.route) {
             SettingsScreen(navController = navController)
        }
        composable(Screen.Favorites.route) {
            FavoritesScreen(navController = navController)
        }
        composable(
            route = Screen.Map.route,
            arguments = listOf(
                androidx.navigation.navArgument("source") { defaultValue = "favorites" }
            )
        ) { backStackEntry ->
            val source = backStackEntry.arguments?.getString("source") ?: "favorites"
            com.example.weatherapp.ui.map.view.MapScreen(
                source = source,
                onLocationSelected = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Alerts.route) {
            AlertsScreen(navController = navController)
        }
        composable(Screen.Alarm.route) {
            AlarmScreen(navController = navController)
        }
    }
}
