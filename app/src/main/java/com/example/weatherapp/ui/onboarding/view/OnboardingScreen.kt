package com.example.weatherapp.ui.onboarding.view

import com.example.weatherapp.ui.onboarding.viewmodel.OnboardingViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.di.LocalAppContainer
import com.example.weatherapp.data.model.onboardingPages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.weatherapp.ui.onboarding.view.components.OnboardingPageContent
import com.example.weatherapp.ui.onboarding.view.components.RamadanSkyEffect
import com.example.weatherapp.ui.theme.DeepNavy
import com.example.weatherapp.ui.theme.DarkBlue
import com.example.weatherapp.ui.theme.Midnight
import kotlinx.coroutines.launch


import com.example.weatherapp.ui.onboarding.view.components.LanguageToggle
import com.example.weatherapp.ui.onboarding.view.components.OnboardingIndicators
import com.example.weatherapp.ui.onboarding.view.components.OnboardingNextButton
import com.example.weatherapp.ui.onboarding.viewmodel.OnboardingUiEvent

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    viewModel: OnboardingViewModel = viewModel(factory = LocalAppContainer.current.viewModelFactory)
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val currentLang by viewModel.language.collectAsState()

    LaunchedEffect(pagerState.currentPage) {
        viewModel.updateCurrentPage(pagerState.currentPage)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is OnboardingUiEvent.ScrollToPage -> {
                    scope.launch {
                        pagerState.animateScrollToPage(event.page)
                    }
                }
                is OnboardingUiEvent.Finish -> {
                    onFinish()
                }
                else -> {}
            }
        }
    }

    val isDark = MaterialTheme.colorScheme.background != Color.White
    val bgColors = if (isDark) listOf(Midnight, DeepNavy, DarkBlue) else listOf(Color(0xFFE8EDF5), Color.White, Color.White)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(bgColors)
                )
        ) {
            RamadanSkyEffect()
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            OnboardingPageContent(
                data = onboardingPages[page],
                isActive = pagerState.currentPage == page
            )
        }

        Row(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(top = 20.dp, end = 20.dp, start = 20.dp),
            horizontalArrangement = Arrangement.End
        ) {
            LanguageToggle(
                currentLang = currentLang,
                onLangChange = { viewModel.updateLanguage(it) }
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 60.dp)
                .zIndex(1.0f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            OnboardingIndicators(
                currentPage = pagerState.currentPage,
                pageCount = 3
            )

            OnboardingNextButton(
                isLast = pagerState.currentPage == 2,
                onClick = { viewModel.onNextClicked() }
            )
        }
    }
}



