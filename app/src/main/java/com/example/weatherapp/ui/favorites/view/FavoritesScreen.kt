package com.example.weatherapp.ui.favorites.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.di.LocalAppContainer
import androidx.navigation.NavController
import com.example.weatherapp.R
import com.example.weatherapp.ui.favorites.viewmodel.FavoritesViewModel
import com.example.weatherapp.ui.main.view.LocalSnackbarHostState
import com.example.weatherapp.ui.theme.*
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource
import com.example.weatherapp.model.FavoriteLocation
import com.example.weatherapp.ui.favorites.view.components.FavoriteItem
import com.example.weatherapp.ui.navigation.Screen
import com.example.weatherapp.ui.components.NoInternetConnectionDialog
import com.example.weatherapp.ui.components.AppFloatingActionButton
import com.example.weatherapp.ui.components.SelectionDeleteBar
import com.example.weatherapp.ui.main.view.LocalSelectionMode
import com.example.weatherapp.ui.favorites.view.components.FavoritesContent
import com.example.weatherapp.ui.favorites.view.components.FavoritesDeleteBar
import com.example.weatherapp.ui.favorites.view.components.FavoritesFab
import com.example.weatherapp.ui.favorites.view.components.FavoritesSelectionHandler
import com.example.weatherapp.utils.network.runIfOnline
import com.example.weatherapp.ui.favorites.viewmodel.FavoritesUiEvent
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    navController: NavController,
    viewModel: FavoritesViewModel = viewModel(factory = LocalAppContainer.current.viewModelFactory)
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = LocalSnackbarHostState.current
    val globalSelectionMode = LocalSelectionMode.current
    
    val favorites by viewModel.favorites.collectAsState()
    val selectedFavorites by viewModel.selectedFavorites.collectAsState(initial = emptySet<FavoriteLocation>())
    var showNoInternetDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is FavoritesUiEvent.ShowUndoSnackbar -> {
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "${context.getString(R.string.deleted)} ${event.deletedItems.size}",
                            actionLabel = context.getString(R.string.undo),
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

    FavoritesSelectionHandler(selectedFavorites, globalSelectionMode, onClear = { viewModel.clearSelection() })

    Box(Modifier.fillMaxSize()) {

        FavoritesContent(
            favorites = favorites,
            selectedFavorites = selectedFavorites,
            navController = navController,
            viewModel = viewModel,
            onOffline = { showNoInternetDialog = true }
        )

        FavoritesDeleteBar(
            selectedCount = selectedFavorites.size,
            onClear = { viewModel.clearSelection() },
            onDelete = { viewModel.deleteSelectedFavorites() },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        if (!globalSelectionMode.value) {
            FavoritesFab(
                navController = navController,
                viewModel = viewModel,
                onOffline = { showNoInternetDialog = true },
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }

        if (showNoInternetDialog) {
            NoInternetConnectionDialog { showNoInternetDialog = false }
        }
    }
}

