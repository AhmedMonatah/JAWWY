package com.example.weatherapp.ui.main.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.weatherapp.ui.navigation.Screen
import com.example.weatherapp.ui.navigation.WeatherNavGraph
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.weatherapp.ui.components.background.WeatherBackground
import com.example.weatherapp.ui.theme.DashboardBackground
import com.example.weatherapp.ui.theme.RamadanGold
import androidx.compose.foundation.background
import androidx.navigation.NavGraph.Companion.findStartDestination
import kotlinx.coroutines.launch
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import com.example.weatherapp.ui.components.main.DashboardBottomBar
import com.example.weatherapp.ui.main.view.viewmodel.MainViewModel
import com.example.weatherapp.ui.theme.RamadanDarkBlue
import com.example.weatherapp.utils.network.NetworkMonitor
import com.example.weatherapp.utils.weather.WeatherTypeUtil
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

    val pagerState = rememberPagerState(pageCount = { 5 })
    
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
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = RamadanDarkBlue,
                        contentColor = Color.White,
                        actionColor = RamadanGold,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(12.dp)
                    )
                }
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



