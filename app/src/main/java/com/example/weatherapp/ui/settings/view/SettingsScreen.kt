package com.example.weatherapp.ui.settings.view

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.weatherapp.R
import com.example.weatherapp.ui.components.WeatherBackground
import com.example.weatherapp.ui.main.view.LocalSnackbarHostState
import com.example.weatherapp.ui.settings.viewmodel.SettingsViewModel
import com.example.weatherapp.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val currentUnits by viewModel.units.collectAsState()
    val currentLang by viewModel.language.collectAsState() 

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
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            val snackbarHostState = LocalSnackbarHostState.current
            val scope = rememberCoroutineScope()
            val offlineMsg = stringResource(R.string.internet_required)

            val locationMode by viewModel.locationMode.collectAsState()

            SettingsGroup(title = stringResource(R.string.settings_location)) {
                SettingsRadioButton(
                    text = stringResource(R.string.gps),
                    selected = locationMode == "gps",
                    onClick = { viewModel.updateLocationMode("gps") }
                )
                SettingsRadioButton(
                    text = stringResource(R.string.map),
                    selected = locationMode == "map",
                    onClick = { 
                        if (viewModel.isOnline()) {
                            navController.navigate(com.example.weatherapp.ui.navigation.Screen.Map.createRoute("settings")) 
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(offlineMsg)
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsGroup(title = stringResource(R.string.settings_temp_unit)) {
                SettingsRadioButton(
                    text = stringResource(R.string.celsius), 
                    selected = currentUnits == "metric", 
                    onClick = { viewModel.updateSettings("metric", currentLang) }
                )
                SettingsRadioButton(
                    text = stringResource(R.string.kelvin), 
                    selected = currentUnits == "standard", 
                    onClick = { viewModel.updateSettings("standard", currentLang) }
                )
                SettingsRadioButton(
                    text = stringResource(R.string.fahrenheit), 
                    selected = currentUnits == "imperial", 
                    onClick = { viewModel.updateSettings("imperial", currentLang) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsGroup(title = stringResource(R.string.settings_language)) {
                SettingsRadioButton(
                    text = stringResource(R.string.english),
                    selected = currentLang == "en",
                    onClick = { viewModel.updateSettings(currentUnits, "en") }
                )
                SettingsRadioButton(
                    text = stringResource(R.string.arabic),
                    selected = currentLang == "ar",
                    onClick = { viewModel.updateSettings(currentUnits, "ar") }
                )
            }

            Spacer(modifier = Modifier.height(110.dp))
        }


    }
}

@Composable
fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = TranslucentBlack)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun SettingsRadioButton(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                role = Role.RadioButton,
                onClick = onClick
            )
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = AccentPurple, 
                unselectedColor = Color.White.copy(alpha=0.5f)
            )
        )
        Spacer(Modifier.width(16.dp))
        Text(text = text, style = MaterialTheme.typography.titleMedium, color = Color.White)
    }
}
