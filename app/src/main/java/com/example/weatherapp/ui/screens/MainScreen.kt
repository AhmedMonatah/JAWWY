package com.example.weatherapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.ui.navigation.Screen
import com.example.weatherapp.ui.navigation.WeatherNavGraph
import com.example.weatherapp.ui.theme.AccentPurple
import com.example.weatherapp.ui.theme.DashboardBackground
import com.example.weatherapp.ui.theme.TranslucentBlack
import com.example.weatherapp.ui.theme.WeatherAppTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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
    val showBottomBar = currentRoute in topLevelRoutes

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
            when (currentRoute) {
                Screen.Favorites.route,
                Screen.Alarm.route -> {
                    FloatingActionButton(
                        onClick = { },
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
    )
    { @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
        CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
            Box {
                WeatherNavGraph(navController = navController)
            }
        }
    }
}

val LocalSnackbarHostState = staticCompositionLocalOf<SnackbarHostState> {
    error("No SnackbarHostState provided")
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
                selected = currentRoute == Screen.Home.route,
                onClick = { navController.navigate(Screen.Home.route) { popUpTo(0) } }
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
    WeatherAppTheme {
        val navController = rememberNavController()
        MainScreen(navController = navController)
    }
}