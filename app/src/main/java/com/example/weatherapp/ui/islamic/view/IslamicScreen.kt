package com.example.weatherapp.ui.islamic.view

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.weatherapp.R
import com.example.weatherapp.ui.home.viewmodel.HomeViewModel
import com.example.weatherapp.ui.theme.*
import com.example.weatherapp.utils.IslamicDateInfo
import com.example.weatherapp.utils.PrayerTimeInfo
import com.example.weatherapp.utils.PrayerTimesManager
import androidx.compose.material.icons.filled.Nightlight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IslamicScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val currentWeather by viewModel.currentWeather.collectAsState()
    val lat = currentWeather?.lat
    val lon = currentWeather?.lon

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (lat != null && lon != null) {
            val prayerTimes = remember(lat, lon) { PrayerTimesManager.getPrayerTimes(lat, lon) }
            val islamicInfo = remember { PrayerTimesManager.getIslamicDateInfo() }
            val nextPrayer = prayerTimes.find { it.isNext }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 25.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    text = stringResource(R.string.islamic_center),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {

                item {
                    RamadanHeroCard(islamicInfo)
                }

                if (nextPrayer != null) {
                    item {
                        NextPrayerLargeCard(nextPrayer)
                    }
                }

                item {
                    Text(
                        text = stringResource(R.string.prayer_schedule),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(prayerTimes) { prayer ->
                    PrayerRowItem(prayer)
                }
            }
        }
        }
    }
}
