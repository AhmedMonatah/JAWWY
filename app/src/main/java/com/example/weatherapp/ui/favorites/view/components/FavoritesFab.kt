package com.example.weatherapp.ui.favorites.view.components


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.ui.res.stringResource
import com.example.weatherapp.R
import com.example.weatherapp.ui.components.AppFloatingActionButton
import com.example.weatherapp.ui.navigation.Screen
import com.example.weatherapp.ui.favorites.viewmodel.FavoritesViewModel
import com.example.weatherapp.utils.network.runIfOnline
import kotlinx.coroutines.flow.StateFlow

@Composable
fun FavoritesFab(
    navController: NavController,
    viewModel: FavoritesViewModel,
    onOffline: () -> Unit,
    modifier: Modifier = Modifier
) {

    val onFabClick = runIfOnline(
        connectivityFlow = viewModel.connectivityFlow as StateFlow<Boolean>,
        onOffline = onOffline
    ) {
        navController.navigate(Screen.Map.createRoute("favorites"))
    }

    AppFloatingActionButton(
        icon = Icons.Outlined.FavoriteBorder,
        contentDescription = stringResource(R.string.add_favorite),
        onClick = onFabClick,
        modifier = modifier
    )
}