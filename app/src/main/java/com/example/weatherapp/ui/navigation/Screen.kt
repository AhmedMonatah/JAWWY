package com.example.weatherapp.ui.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    
    object Home : Screen("home")
    object Favorites : Screen("favorites")
    object Alerts : Screen("alerts")
    object Settings : Screen("settings")
    
    object HomeDetail : Screen("home/{lat}/{lon}/{city}") {
        fun createRoute(lat: Double, lon: Double, city: String): String {
            val encodedCity = Uri.encode(city)
            return "home/$lat/$lon/$encodedCity"
        }
    }
    
    object Map : Screen("map/{source}") {
        fun createRoute(source: String) = "map/$source"
    }
}
