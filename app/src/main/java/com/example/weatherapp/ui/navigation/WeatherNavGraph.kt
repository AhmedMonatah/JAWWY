package com.example.weatherapp.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.weatherapp.ui.home.view.HomeScreen
import com.example.weatherapp.ui.main.view.DashboardScreen
import com.example.weatherapp.ui.map.view.MapScreen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WeatherNavGraph(
    navController: NavHostController,
    pagerState: PagerState,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Consolidate main screens into a single destination for smooth paging
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController, pagerState)
        }

        composable(
            route = Screen.HomeDetail.route,
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType },
                navArgument("lon") { type = NavType.StringType },
                navArgument("city") { type = NavType.StringType }
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
                navArgument("source") { defaultValue = "favorites" }
            ),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(500)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(500)
                )
            }
        ) { backStackEntry ->
            val source = backStackEntry.arguments?.getString("source") ?: "favorites"
            MapScreen(
                source = source,
                onLocationSelected = { src ->
                    if (src == "settings") {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = true }
                        }
                    } else {
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}
