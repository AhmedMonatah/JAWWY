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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.weatherapp.ui.components.WeatherBackground
import com.example.weatherapp.ui.theme.AccentPurple
import com.example.weatherapp.ui.theme.DashboardBackground
import androidx.compose.foundation.background
import androidx.compose.material.icons.automirrored.filled.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import kotlinx.coroutines.launch
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import com.example.weatherapp.ui.alarm.view.AlarmScreen
import com.example.weatherapp.ui.home.view.HomeScreen
import com.example.weatherapp.ui.favorites.view.FavoritesScreen
import com.example.weatherapp.ui.settings.view.SettingsScreen

val LocalSnackbarHostState = staticCompositionLocalOf<SnackbarHostState> {
    error("No SnackbarHostState provided")
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val snackbarHostState = remember { SnackbarHostState() }

    // Lifted PagerState for perfect sync between BottomBar and Swiping
    val pagerState = rememberPagerState(pageCount = { 4 })
    
    // Get current page from route arguments to handle initial navigation
    val routePage = navBackStackEntry?.arguments?.getString("page")?.toIntOrNull() ?: 0
    
    // Sync Pager with Route (for initial load or external deep links)
    LaunchedEffect(routePage) {
        if (pagerState.currentPage != routePage) {
            pagerState.scrollToPage(routePage)
        }
    }

    // Sync Route with Pager (for swiping)
    LaunchedEffect(pagerState.currentPage) {
        val currentPathPage = navBackStackEntry?.arguments?.getString("page")?.toIntOrNull() ?: 0
        if (currentPathPage != pagerState.currentPage) {
            navController.navigate(Screen.Dashboard.createRoute(pagerState.currentPage)) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    val scope = rememberCoroutineScope()
    val currentWeather by viewModel.repository.getCurrentWeather().collectAsState(initial = null)
    
    val weatherType = remember(currentWeather) {
        val desc = (currentWeather?.description ?: "").lowercase()
        val icon = currentWeather?.icon ?: ""
        when {
            desc.contains("snow") || icon.startsWith("13") -> "snow"
            desc.contains("rain") || desc.contains("drizzle") || icon.startsWith("09") || icon.startsWith("10") -> "rain"
            desc.contains("cloud") || icon.startsWith("02") || icon.startsWith("03") || icon.startsWith("04") -> "clouds"
            else -> "clear"
        }
    }
    
    val isCold = (currentWeather?.temp ?: 20.0) < 5.0

    // Top-level routes where we show bottom bar
    val showBottomBar = remember(currentRoute) {
        currentRoute == Screen.Dashboard.route || currentRoute == null
    }

    Box(modifier = Modifier.fillMaxSize().background(DashboardBackground)) {
        WeatherBackground(weatherType = weatherType, isCold = isCold)
        
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                if (showBottomBar) {
                    DashboardBottomBar(pagerState.currentPage) { page ->
                        scope.launch {
                            // Use scrollToPage for instant transition to avoid jumping intermediate screens
                            pagerState.scrollToPage(page)
                        }
                    }
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            floatingActionButton = {
                if (showBottomBar && pagerState.currentPage == 2) { // Favorites Page
                    FloatingActionButton(
                        onClick = { navController.navigate(Screen.Map.createRoute("favorites")) },
                        containerColor = AccentPurple,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                }
            }
        ) { paddingValues ->
            CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                ) {
                    WeatherNavGraph(navController = navController, pagerState = pagerState)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DashboardPager(
    navController: NavHostController,
    pagerState: androidx.compose.foundation.pager.PagerState
) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
    ) { page ->
        when (page) {
            0 -> HomeScreen(navController = navController)
            1 -> AlarmScreen(navController = navController)
            2 -> FavoritesScreen(navController = navController)
            3 -> SettingsScreen(navController = navController)
        }
    }
}

@Composable
fun DashboardBottomBar(currentPage: Int, onPageSelected: (Int) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        color = DashboardBackground.copy(alpha = 0.9f),
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(Icons.Default.Dashboard, "DASH", currentPage == 0) { onPageSelected(0) }
            BottomNavItem(Icons.Default.Alarm, "ALARM", currentPage == 1) { onPageSelected(1) }
            BottomNavItem(Icons.Default.Star, "SAVED", currentPage == 2) { onPageSelected(2) }
            BottomNavItem(Icons.Default.Settings, "SET", currentPage == 3) { onPageSelected(3) }
        }
    }
}

@Composable
fun BottomNavItem(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(50.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                icon,
                contentDescription = label,
                tint = if (selected) AccentPurple else Color.White.copy(alpha = 0.5f),
                modifier = Modifier.size(if (selected) 28.dp else 24.dp)
            )
            if (selected) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(4.dp)
                        .background(AccentPurple, CircleShape)
                )
            }
        }
    }
}