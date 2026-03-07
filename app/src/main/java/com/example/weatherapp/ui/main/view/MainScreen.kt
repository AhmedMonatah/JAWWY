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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.di.LocalAppContainer
import com.example.weatherapp.ui.home.view.components.WeatherBackground
import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import com.example.weatherapp.ui.main.view.components.DashboardBottomBar
import com.example.weatherapp.ui.main.view.viewmodel.MainViewModel
import com.example.weatherapp.ui.theme.LocalIsDark
import com.example.weatherapp.ui.components.NoInternetConnectionDialog
import com.example.weatherapp.utils.network.NetworkMonitor
import com.example.weatherapp.utils.weather.WeatherTypeUtil
import kotlinx.coroutines.launch

val LocalSnackbarHostState = staticCompositionLocalOf<SnackbarHostState> {
    error("No SnackbarHostState provided")
}

val LocalSelectionMode = compositionLocalOf<MutableState<Boolean>> {
    error("No SelectionMode provided")
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    startDestination: String,
    viewModel: MainViewModel = viewModel(factory = LocalAppContainer.current.viewModelFactory)
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val snackbarHostState = remember { SnackbarHostState() }
    val selectionMode = remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(pageCount = { 4 })

    val currentWeather by viewModel.currentWeather.collectAsState()

    NetworkMonitor(
        connectivityFlow = viewModel.connectivityFlow,
        snackbarHostState = snackbarHostState
    )
    
    val weatherType = remember(currentWeather) {
        WeatherTypeUtil.determineWeatherType(currentWeather?.description, currentWeather?.icon)
    }
    
    val isCold = (currentWeather?.temp ?: 20.0) < 5.0

    val showBottomBar = remember(currentRoute, selectionMode.value) {
        val isDashboard = currentRoute == Screen.Dashboard.route || currentRoute == null
        isDashboard && !selectionMode.value
    }

    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        WeatherBackground(weatherType = weatherType, isCold = isCold)
        
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                if (showBottomBar) {
                    val showNoInternet by viewModel.showNoInternet.collectAsState(initial = false)
                    
                    DashboardBottomBar(
                        currentPage = pagerState.currentPage,
                        onNavigate = { screen ->
                            val targetIndex = when (screen) {
                                Screen.Home -> 0
                                Screen.Favorites -> 1
                                Screen.Alerts -> 2
                                Screen.Settings -> 3
                                else -> 0
                            }
                            coroutineScope.launch {
                                pagerState.scrollToPage(targetIndex)
                            }
                        }
                    )

                    if (showNoInternet) {
                        NoInternetConnectionDialog(onDismiss = { viewModel.setShowNoInternet(false) })
                    }
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        actionColor = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            },

        ) { paddingValues ->
            CompositionLocalProvider(
                LocalSnackbarHostState provides snackbarHostState,
                LocalSelectionMode provides selectionMode
            ) {
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
