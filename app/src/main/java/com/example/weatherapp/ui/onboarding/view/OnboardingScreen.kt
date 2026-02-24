package com.example.weatherapp.ui.onboarding.view

import com.example.weatherapp.ui.onboarding.viewmodel.OnboardingViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.di.LocalAppContainer
import com.example.weatherapp.model.onboardingPages
import com.example.weatherapp.R

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.weatherapp.ui.onboarding.view.components.OnboardingPageContent
import com.example.weatherapp.ui.onboarding.view.components.RamadanSkyEffect
import com.example.weatherapp.ui.theme.RamadanDeepNavy
import com.example.weatherapp.ui.theme.RamadanDarkBlue
import com.example.weatherapp.ui.theme.RamadanGold
import com.example.weatherapp.ui.theme.RamadanMidnight
import com.example.weatherapp.ui.theme.TranslucentBlack
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RamadanMidnight)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(RamadanMidnight, RamadanDeepNavy, RamadanDarkBlue)
                    )
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



