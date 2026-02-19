package com.example.weatherapp.ui.onboarding.view

import com.example.weatherapp.ui.onboarding.viewmodel.OnboardingViewModel
import com.example.weatherapp.model.onboardingPages

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.ui.components.onboarding.OnboardingPageContent
import com.example.weatherapp.ui.components.onboarding.RamadanSkyEffect
import com.example.weatherapp.ui.theme.RamadanDeepNavy
import com.example.weatherapp.ui.theme.RamadanDarkBlue
import com.example.weatherapp.ui.theme.RamadanGold
import com.example.weatherapp.ui.theme.RamadanMidnight
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    viewModel: OnboardingViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

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

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { i ->
                    val isSelected = pagerState.currentPage == i
                    val width by animateDpAsState(
                        targetValue = if (isSelected) 36.dp else 12.dp,
                        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
                        label = ""
                    )
                    val color by animateColorAsState(
                        targetValue = if (isSelected) RamadanGold else Color.White.copy(alpha = 0.2f),
                        label = ""
                    )
                    Box(
                        modifier = Modifier
                            .height(10.dp)
                            .width(width)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            val isLast = pagerState.currentPage == 2
            Button(
                onClick = {
                    if (isLast) {
                        viewModel.completeOnboarding()
                        onFinish()
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .width(220.dp)
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RamadanGold),
                shape = RoundedCornerShape(20.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 12.dp)
            ) {
                AnimatedContent(
                    targetState = isLast,
                    transitionSpec = {
                        fadeIn(tween(400)) + scaleIn(initialScale = 0.8f) togetherWith
                                fadeOut(tween(300)) + scaleOut(targetScale = 0.8f)
                    },
                    label = ""
                ) { last ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            if (last) "Get Started" else "Continue",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = RamadanDeepNavy,
                            letterSpacing = 1.sp
                        )
                        Spacer(Modifier.width(12.dp))
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = RamadanDeepNavy
                        )
                    }
                }
            }
        }
    }
}



