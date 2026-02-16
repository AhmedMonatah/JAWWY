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

import android.net.Uri
import androidx.compose.foundation.pager.PagerState

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard?page={page}") {
        fun createRoute(page: Int) = "dashboard?page=$page"
    }
    object Home : Screen("home?lat={lat}&lon={lon}&city={city}") {
        fun createRoute(lat: Double? = null, lon: Double? = null, city: String? = null): String {
            return if (lat != null && lon != null && city != null) {
                val encodedCity = Uri.encode(city)
                "home?lat=$lat&lon=$lon&city=$encodedCity"
            } else "dashboard?page=0"
        }
    }
    object Settings : Screen("dashboard?page=3")
    object Favorites : Screen("dashboard?page=2")
    object Alerts : Screen("alerts")
    object Alarm : Screen("dashboard?page=1")
    object Map : Screen("map?source={source}") {
        fun createRoute(source: String = "favorites") = "map?source=$source"
    }
}

@Composable
fun WeatherNavGraph(
    navController: NavHostController,
    pagerState: androidx.compose.foundation.pager.PagerState,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(
            route = Screen.Dashboard.route,
            arguments = listOf(
                androidx.navigation.navArgument("page") { defaultValue = "0" }
            )
        ) { backStackEntry ->
            val page = backStackEntry.arguments?.getString("page")?.toIntOrNull() ?: 0
            com.example.weatherapp.ui.main.view.DashboardPager(
                navController = navController,
                pagerState = pagerState
            )
        }
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
        composable(
            route = Screen.Map.route,
            arguments = listOf(
                androidx.navigation.navArgument("source") { defaultValue = "favorites" }
            )
        ) { backStackEntry ->
            val source = backStackEntry.arguments?.getString("source") ?: "favorites"
            com.example.weatherapp.ui.map.view.MapScreen(
                source = source,
                onLocationSelected = { src ->
                    if (src == "settings") {
                        navController.navigate(Screen.Dashboard.createRoute(0)) {
                            popUpTo(0) { inclusive = true }
                        }
                    } else {
                        navController.popBackStack()
                    }
                }
            )
        }
        composable(Screen.Alerts.route) {
            AlertsScreen(navController = navController)
        }
    }
}
