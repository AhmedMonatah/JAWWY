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
import com.example.weatherapp.ui.theme.RamadanGold
import androidx.compose.foundation.background
import androidx.compose.material.icons.automirrored.filled.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import kotlinx.coroutines.launch
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.res.stringResource
import com.example.weatherapp.ui.home.view.HomeScreen
import com.example.weatherapp.ui.favorites.view.FavoritesScreen
import com.example.weatherapp.ui.settings.view.SettingsScreen
import com.example.weatherapp.utils.NetworkMonitor
import com.example.weatherapp.utils.WeatherTypeUtil
import kotlinx.coroutines.flow.StateFlow

val LocalSnackbarHostState = staticCompositionLocalOf<SnackbarHostState> {
    error("No SnackbarHostState provided")
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    startDestination: String,
    viewModel: MainViewModel = hiltViewModel()
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val snackbarHostState = remember { SnackbarHostState() }

    val pagerState = rememberPagerState(pageCount = { 4 })
    
    val routePage = navBackStackEntry?.arguments?.getString("page")?.toIntOrNull() ?: 0
    
    LaunchedEffect(routePage) {
        if (pagerState.currentPage != routePage) {
            pagerState.scrollToPage(routePage)
        }
    }

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
    val isOnline by viewModel.repository.connectivityFlow.collectAsState(initial = true)
    
    NetworkMonitor(
        connectivityFlow = viewModel.repository.connectivityFlow as StateFlow<Boolean>,
        snackbarHostState = snackbarHostState
    )
    
    val weatherType = remember(currentWeather) {
        WeatherTypeUtil.determineWeatherType(currentWeather?.description, currentWeather?.icon)
    }
    
    val isCold = (currentWeather?.temp ?: 20.0) < 5.0

    val showBottomBar = remember(currentRoute) {
        val onDashboard = currentRoute == Screen.Dashboard.route || currentRoute == null
        val onMap = currentRoute?.startsWith(Screen.Map.route.substringBefore("{")) == true
        onDashboard && !onMap
    }

    Box(modifier = Modifier.fillMaxSize().background(DashboardBackground)) {
        WeatherBackground(weatherType = weatherType, isCold = isCold)
        
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                if (showBottomBar) {
                    DashboardBottomBar(pagerState.currentPage) { page ->
                        scope.launch {
                            pagerState.scrollToPage(page)
                        }
                    }
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },

        ) { paddingValues ->
            CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                ) {
                    WeatherNavGraph(
                        navController = navController, 
                        pagerState = pagerState,
                        startDestination = startDestination
                    )
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
            1 -> com.example.weatherapp.ui.alerts.view.AlertsScreen(navController = navController)
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