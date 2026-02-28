package com.example.weatherapp.ui.settings.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.di.LocalAppContainer
import androidx.navigation.NavController
import com.example.weatherapp.R
import com.example.weatherapp.ui.settings.viewmodel.SettingsViewModel
import com.example.weatherapp.ui.theme.*
import com.example.weatherapp.ui.components.NoInternetConnectionDialog
import com.example.weatherapp.ui.navigation.Screen

import com.example.weatherapp.ui.settings.view.components.SettingsGroup
import com.example.weatherapp.ui.settings.view.components.SettingsRadioButton
import com.example.weatherapp.ui.settings.viewmodel.SettingsUiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = viewModel(factory = LocalAppContainer.current.viewModelFactory)
) {
    val currentUnits by viewModel.units.collectAsState()
    val currentLang by viewModel.language.collectAsState() 
    val locationMode by viewModel.locationMode.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    var showNoInternetDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is SettingsUiEvent.NavigateToMap -> {
                    navController.navigate(Screen.Map.createRoute("settings"))
                }
                is SettingsUiEvent.ShowNoInternet -> {
                    showNoInternetDialog = true
                }
                else -> {}
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 35.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.settings_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            }

            SettingsGroup(title = stringResource(R.string.theme_mode)) {
                SettingsRadioButton(
                    text = stringResource(R.string.theme_light),
                    selected = themeMode == "light",
                    onClick = { viewModel.updateThemeMode("light") }
                )
                SettingsRadioButton(
                    text = stringResource(R.string.theme_dark),
                    selected = themeMode == "dark",
                    onClick = { viewModel.updateThemeMode("dark") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsGroup(title = stringResource(R.string.settings_location)) {
                SettingsRadioButton(
                    text = stringResource(R.string.gps),
                    selected = locationMode == "gps",
                    onClick = { viewModel.updateLocationMode("gps") }
                )
                SettingsRadioButton(
                    text = stringResource(R.string.map),
                    selected = locationMode == "map",
                    onClick = { viewModel.updateLocationMode("map") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsGroup(title = stringResource(R.string.settings_temp_unit)) {
                SettingsRadioButton(
                    text = stringResource(R.string.celsius), 
                    selected = currentUnits == "metric", 
                    onClick = { viewModel.updateTemperatureUnit("metric") }
                )
                SettingsRadioButton(
                    text = stringResource(R.string.kelvin), 
                    selected = currentUnits == "standard", 
                    onClick = { viewModel.updateTemperatureUnit("standard") }
                )
                SettingsRadioButton(
                    text = stringResource(R.string.fahrenheit), 
                    selected = currentUnits == "imperial", 
                    onClick = { viewModel.updateTemperatureUnit("imperial") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsGroup(title = stringResource(R.string.settings_language)) {
                SettingsRadioButton(
                    text = stringResource(R.string.english),
                    selected = currentLang == "en",
                    onClick = { viewModel.updateLanguage("en") }
                )
                SettingsRadioButton(
                    text = stringResource(R.string.arabic),
                    selected = currentLang == "ar",
                    onClick = { viewModel.updateLanguage("ar") }
                )
            }

            Spacer(modifier = Modifier.height(90.dp))
        }

        if (showNoInternetDialog) {
            NoInternetConnectionDialog(onDismiss = { showNoInternetDialog = false })
        }
    }
}
