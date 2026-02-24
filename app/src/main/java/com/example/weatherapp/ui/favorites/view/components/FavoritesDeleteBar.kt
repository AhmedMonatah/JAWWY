package com.example.weatherapp.ui.favorites.view.components


import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import com.example.weatherapp.model.FavoriteLocation
import com.example.weatherapp.ui.components.SelectionDeleteBar
import com.example.weatherapp.ui.favorites.viewmodel.FavoritesViewModel
import kotlinx.coroutines.CoroutineScope
import androidx.compose.ui.res.stringResource
import com.example.weatherapp.R

@Composable
fun FavoritesDeleteBar(
    selectedCount: Int,
    onClear: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    SelectionDeleteBar(
        selectedCount = selectedCount,
        onClearSelection = onClear,
        onDeleteSelected = onDelete,
        modifier = modifier
    )
}