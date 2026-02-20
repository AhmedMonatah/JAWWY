package com.example.weatherapp.ui.favorites.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.weatherapp.ui.favorites.viewmodel.FavoritesViewModel
import com.example.weatherapp.ui.main.view.LocalSnackbarHostState
import com.example.weatherapp.ui.theme.*
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource
import androidx.compose.material.icons.filled.Favorite
import com.example.weatherapp.R
import com.example.weatherapp.ui.components.fav.FavoriteItem
import com.example.weatherapp.ui.navigation.Screen

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
                text = stringResource(R.string.saved_locations),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))
            if (favoritesList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 100.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = RamadanGold.copy(alpha = 0.3f),
                            modifier = Modifier.size(120.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = stringResource(R.string.no_favorites),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.4f),
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.add_fav_description),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.2f)
                        )
                    }
                }
            }


            else {
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
                                        Screen.Home.createRoute(
                                            location.lat,
                                            location.lon,
                                            location.name
                                        )
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
                    Screen.Map.createRoute("favorites")
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

