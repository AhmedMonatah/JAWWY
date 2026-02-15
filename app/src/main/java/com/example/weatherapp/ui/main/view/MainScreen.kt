package com.example.weatherapp.ui.main.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.weatherapp.ui.navigation.Screen
import com.example.weatherapp.ui.navigation.WeatherNavGraph
import com.example.weatherapp.ui.theme.AccentPurple
import com.example.weatherapp.ui.theme.DashboardBackground

val LocalSnackbarHostState = staticCompositionLocalOf<SnackbarHostState> {
    error("No SnackbarHostState provided")
}

@Composable
fun MainScreen(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val snackbarHostState = remember { SnackbarHostState() }

    val topLevelRoutes = listOf(
        Screen.Home.route,
        Screen.Favorites.route,
        Screen.Alerts.route,
        Screen.Settings.route,
        Screen.Alarm.route
    )
    
    // Bottom bar is shown for all top-level routes.
    // We only hide it if we are on the Home screen but with VALiD location arguments (Detail mode).
    val isHomeRoute = currentRoute?.startsWith("home") == true
    val latArg = navBackStackEntry?.arguments?.getString("lat")
    val hasValidLat = latArg != null && latArg != "{lat}"
    
    val isDetailMode = isHomeRoute && hasValidLat
    val isTopLevel = currentRoute in topLevelRoutes || (isHomeRoute && !isDetailMode)
    
    val showBottomBar = isTopLevel && !isDetailMode

    Scaffold(
        containerColor = DashboardBackground,
        bottomBar = {
            if (showBottomBar) {
                DashboardBottomBar(currentRoute, navController)
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            when {
                currentRoute == Screen.Favorites.route -> {
                    FloatingActionButton(
                        onClick = { navController.navigate(Screen.Map.route) },
                        containerColor = AccentPurple,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
            ) {
                WeatherNavGraph(navController = navController)
            }
        }
    }
}

@Composable
fun DashboardBottomBar(currentRoute: String?, navController: NavHostController) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(0.dp),
        color = DashboardBackground,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Default.Dashboard,
                label = "DASH",
                selected = currentRoute?.startsWith("home") == true,
                onClick = { navController.navigate("home") { popUpTo(0) } }
            )

            BottomNavItem(
                icon = Icons.Default.Alarm,
                label = "ALARM",
                selected = currentRoute == Screen.Alarm.route,
                onClick = { navController.navigate(Screen.Alarm.route) { popUpTo(0) } }
            )

            BottomNavItem(
                icon = Icons.Default.Star,
                label = "SAVED",
                selected = currentRoute == Screen.Favorites.route,
                onClick = { navController.navigate(Screen.Favorites.route) { popUpTo(0) } }
            )

            BottomNavItem(
                icon = Icons.Default.Settings,
                label = "SET",
                selected = currentRoute == Screen.Settings.route,
                onClick = { navController.navigate(Screen.Settings.route) { popUpTo(0) } }
            )
        }
    }
}

@Composable
fun BottomNavItem(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(start = 8.dp, end = 8.dp)
    ) {
        IconButton(onClick = onClick) {
            Icon(
                icon,
                contentDescription = label,
                tint = if (selected) AccentPurple else Color.White.copy(alpha = 0.5f)
            )
        }
    }
}