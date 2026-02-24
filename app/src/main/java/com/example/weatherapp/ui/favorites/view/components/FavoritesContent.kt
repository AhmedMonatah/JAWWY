package com.example.weatherapp.ui.favorites.view.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.weatherapp.model.FavoriteLocation
import com.example.weatherapp.ui.favorites.viewmodel.FavoritesViewModel
import com.example.weatherapp.R

@Composable
public fun FavoritesContent(
    favorites: List<FavoriteLocation>,
    selectedFavorites: Set<FavoriteLocation>,
    navController: NavController,
    viewModel: FavoritesViewModel,
    onOffline: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 25.dp)
    ) {
        Spacer(Modifier.height(15.dp))

        Text(
            text = stringResource(R.string.saved_locations),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(Modifier.height(24.dp))

        if (favorites.isEmpty()) {
            EmptyFavoritesState()
        } else {
            FavoritesList(
                favorites = favorites,
                selectedFavorites = selectedFavorites,
                navController = navController,
                viewModel = viewModel,
                onOffline = onOffline
            )
        }
    }
}