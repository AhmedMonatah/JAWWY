package com.example.weatherapp.ui.favorites.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.di.LocalAppContainer
import androidx.navigation.NavController
import com.example.weatherapp.R
import com.example.weatherapp.ui.favorites.viewmodel.FavoritesViewModel
import com.example.weatherapp.ui.main.view.LocalSnackbarHostState
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource
import com.example.weatherapp.ui.favorites.view.components.FavoriteItem
import com.example.weatherapp.ui.navigation.Screen
import com.example.weatherapp.ui.components.NoInternetConnectionDialog
import com.example.weatherapp.ui.components.AppFloatingActionButton
import com.example.weatherapp.ui.components.SelectionDeleteBar
import com.example.weatherapp.ui.main.view.LocalSelectionMode
import com.example.weatherapp.ui.favorites.viewmodel.FavoritesUiEvent
import androidx.compose.ui.platform.LocalContext
import com.example.weatherapp.ui.components.CommonScreenLayout
import com.example.weatherapp.ui.favorites.view.components.EmptyFavoritesState

@Composable
fun FavoritesScreen(
    navController: NavController,
    viewModel: FavoritesViewModel = viewModel(factory = LocalAppContainer.current.viewModelFactory)
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = LocalSnackbarHostState.current
    val globalSelectionMode = LocalSelectionMode.current
    val favorites by viewModel.favorites.collectAsState()
    val selectedFavorites by viewModel.selectedFavorites.collectAsState()
    val showNoInternetDialog by viewModel.showNoInternetDialog.collectAsState()
    val delete= stringResource(R.string.deleted)
    val undo=stringResource(R.string.undo)

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is FavoritesUiEvent.ShowUndoSnackbar -> {
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = delete + " " + event.deletedItems.size,
                            actionLabel =undo,
                            duration = SnackbarDuration.Short
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            event.deletedItems.forEach { viewModel.addFavorite(it) }
                        }
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearSelection()
        }
    }

    BackHandler(enabled = selectedFavorites.isNotEmpty()) {
        viewModel.clearSelection()
    }

    LaunchedEffect(selectedFavorites.size) {
        globalSelectionMode.value = selectedFavorites.isNotEmpty()
    }

    CommonScreenLayout(
        title = stringResource(R.string.saved_locations),
        description = stringResource(R.string.add_fav_description),
        isEmpty = favorites.isEmpty(),
        emptyContent = { EmptyFavoritesState() },
        floatingActionButton = {
            if (!globalSelectionMode.value) {
                AppFloatingActionButton(
                    icon = Icons.Outlined.FavoriteBorder,
                    contentDescription = stringResource(R.string.add_favorite),
                    onClick = {
                        if (viewModel.isOnline()) {
                            navController.navigate(Screen.Map.createRoute("favorites"))
                        } else {
                            viewModel.setShowNoInternetDialog(true)
                        }
                    }
                )
            }
        },
        selectionBar = {
            SelectionDeleteBar(
                selectedCount = selectedFavorites.size,
                onClearSelection = { viewModel.clearSelection() },
                onDeleteSelected = {
                    if (viewModel.isOnline()) {
                        viewModel.deleteSelectedFavorites()
                    } else {
                        viewModel.setShowNoInternetDialog(true)
                    }
                }
            )
        },
        content = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 120.dp),
                modifier = Modifier.fillMaxSize()
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
                                if (viewModel.isOnline()) {
                                    navController.navigate(
                                        Screen.HomeDetail.createRoute(
                                            location.lat,
                                            location.lon,
                                            location.name
                                        )
                                    )
                                } else {
                                    viewModel.setShowNoInternetDialog(true)
                                }
                            }
                        },
                        onLongClick = {
                            viewModel.toggleSelection(location)
                        }
                    )
                }
            }
        }
    )

    if (showNoInternetDialog) {
        NoInternetConnectionDialog { viewModel.setShowNoInternetDialog(false) }
    }
}

