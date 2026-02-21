package com.example.weatherapp.ui.islamic.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.weatherapp.R
import com.example.weatherapp.ui.islamic.viewmodel.IslamicViewModel
import com.example.weatherapp.ui.components.islamic.RamadanHeroCard
import com.example.weatherapp.ui.components.islamic.NextPrayerLargeCard
import com.example.weatherapp.ui.components.islamic.PrayerRowItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IslamicScreen(
    navController: NavController,
    viewModel: IslamicViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (!uiState.isLoading) {
            val prayerTimes = uiState.prayerTimes
            val islamicInfo = uiState.islamicInfo
            val nextPrayer = uiState.nextPrayer

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 25.dp)
            ) {
                Spacer(modifier = Modifier.height(15.dp))
                
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
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {

                    islamicInfo?.let { info ->
                        item {
                            RamadanHeroCard(info)
                        }
                    }

                    nextPrayer?.let { prayer ->
                        item {
                            NextPrayerLargeCard(prayer)
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
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}
