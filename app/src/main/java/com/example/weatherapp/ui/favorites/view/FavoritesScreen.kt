package com.example.weatherapp.ui.favorites.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.data.local.entity.FavoriteLocation
import com.example.weatherapp.ui.components.WeatherBackground
import com.example.weatherapp.ui.favorites.viewmodel.FavoritesViewModel
import com.example.weatherapp.ui.main.view.LocalSnackbarHostState
import com.example.weatherapp.ui.theme.*
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.ui.graphics.Brush
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    navController: NavController,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val favoritesList by viewModel.favorites.collectAsState()
    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 25.dp)
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(com.example.weatherapp.R.string.saved_locations),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))
            
            if (favoritesList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(bottom = 100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            modifier = Modifier.size(120.dp),
                            shape = CircleShape,
                            color = RamadanGold.copy(alpha = 0.1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = RamadanGold.copy(alpha = 0.3f),
                                modifier = Modifier.padding(30.dp).size(60.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = stringResource(com.example.weatherapp.R.string.no_favorites),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.4f),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(com.example.weatherapp.R.string.add_fav_description),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.2f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    items(
                        items = favoritesList,
                        key = { it.id }
                    ) { location ->

                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                if (value != SwipeToDismissBoxValue.Settled) {

                                    viewModel.removeFavorite(location)

                                    scope.launch {
                                        val result = snackbarHostState.showSnackbar(
                                            message = "Deleted ${location.name}",
                                            actionLabel = "Undo"
                                        )

                                        if (result == SnackbarResult.ActionPerformed) {
                                            viewModel.addFavorite(location)
                                        }
                                    }

                                    true
                                } else false
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Color.Red.copy(alpha = 0.5f),
                                            RoundedCornerShape(24.dp)
                                        ),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.padding(horizontal = 24.dp)
                                    )
                                }
                            }
                        ) {
                            FavoriteItem(
                                location = location,
                                onNavigate = {
                                    navController.navigate(
                                        com.example.weatherapp.ui.navigation.Screen.Home
                                            .createRoute(location.lat, location.lon, location.name)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                navController.navigate(
                    com.example.weatherapp.ui.navigation.Screen.Map.createRoute("favorites")
                )
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(30.dp),
            containerColor = Color.Transparent,
            elevation = FloatingActionButtonDefaults.elevation(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(RamadanGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Add Favorite",
                    tint = RamadanDeepNavy
                )
            }
        }
    }
}


@Composable
fun FavoriteItem(location: com.example.weatherapp.data.local.entity.FavoriteLocation, onNavigate: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(140.dp).clickable { onNavigate() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = AccentPurple)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Top Left: Section label
            Text(
                text = stringResource(com.example.weatherapp.R.string.weather),
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.TopStart)
            )
            Text(
                text = location.name,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterStart)
            )

            Text(
                text = location.condition,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.align(Alignment.BottomStart)
            )

            Surface(
                modifier = Modifier.size(50.dp).align(Alignment.TopEnd),
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.15f)
            ) {
                // Use the icon code directly if available, fallback to condition string
                val weatherIcon = when {
                    location.icon.startsWith("01") -> Icons.Default.WbSunny
                    location.icon.startsWith("02") || location.icon.startsWith("03") -> Icons.Default.Cloud
                    location.icon.startsWith("04") -> Icons.Default.Cloud
                    location.icon.startsWith("09") || location.icon.startsWith("10") -> Icons.Default.WaterDrop
                    location.icon.startsWith("11") -> Icons.Default.WaterDrop
                    location.icon.startsWith("13") -> Icons.Default.AcUnit
                    location.icon.startsWith("50") -> Icons.Default.Cloud
                    // Fallback to text matching if icon code not useful
                    location.condition.lowercase().contains("cloud") -> Icons.Default.Cloud
                    location.condition.lowercase().contains("rain") -> Icons.Default.WaterDrop
                    location.condition.lowercase().contains("snow") -> Icons.Default.AcUnit
                    else -> Icons.Default.WbSunny
                }

                val iconColor = when {
                    location.icon.startsWith("01") -> Color.Yellow
                    location.icon.startsWith("10") || location.icon.startsWith("09") -> AccentBlue
                    else -> Color.White
                }

                Icon(
                    imageVector = weatherIcon,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp),
                    tint = iconColor
                )
            }

            Text(
                text = "${location.currentTemp.toInt()}°",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}
