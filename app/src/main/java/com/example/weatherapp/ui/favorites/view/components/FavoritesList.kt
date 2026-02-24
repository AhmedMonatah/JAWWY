package com.example.weatherapp.ui.favorites.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.weatherapp.model.FavoriteLocation
import com.example.weatherapp.ui.favorites.viewmodel.FavoritesViewModel
import com.example.weatherapp.ui.main.view.LocalSelectionMode
import com.example.weatherapp.ui.navigation.Screen

@Composable
public fun FavoritesList(
    favorites: List<FavoriteLocation>,
    selectedFavorites: Set<FavoriteLocation>,
    navController: NavController,
    viewModel: FavoritesViewModel,
    onOffline: () -> Unit
) {
    val globalSelectionMode = LocalSelectionMode.current

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        items(favorites, key = { it.id }) { location ->

            val isSelected = selectedFavorites.contains(location)

            FavoriteItem(
                location = location,
                selected = isSelected,
                onNavigate = {
                    if (globalSelectionMode.value) {
                        viewModel.toggleSelection(location)
                    } else {
                        navController.navigate(
                            Screen.Home.createRoute(
                                location.lat,
                                location.lon,
                                location.name
                            )
                        )
                    }
                },
                onLongClick = {
                    viewModel.toggleSelection(location)
                }
            )
        }
    }
}