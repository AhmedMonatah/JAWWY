package com.example.weatherapp.ui.favorites.view.components

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.weatherapp.model.FavoriteLocation

@Composable
public fun FavoritesSelectionHandler(
    selectedItems: Set<FavoriteLocation>,
    globalSelectionMode: MutableState<Boolean>,
    onClear: () -> Unit
) {
    BackHandler(enabled = selectedItems.isNotEmpty()) {
        onClear()
    }

    LaunchedEffect(selectedItems.size) {
        globalSelectionMode.value = selectedItems.isNotEmpty()
    }
}