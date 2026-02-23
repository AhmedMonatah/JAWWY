package com.example.weatherapp.ui.onboarding.view.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.model.OnboardingPageData
import com.example.weatherapp.ui.theme.RamadanGold


@Composable
fun OnboardingPageContent(data: OnboardingPageData, isActive: Boolean) {
    val enterAnim = remember { Animatable(0f) }

    LaunchedEffect(isActive) {
        if (isActive) {
            enterAnim.snapTo(0f)
            enterAnim.animateTo(1f, tween(800, easing = FastOutSlowInEasing))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 32.dp)
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(320.dp)
                .graphicsLayer {
                    scaleX = 0.9f + (enterAnim.value * 0.1f)
                    scaleY = 0.9f + (enterAnim.value * 0.1f)
                    alpha = enterAnim.value
                },
            contentAlignment = Alignment.Center
        ) {
            WeatherAssistantWithVisual(data.type)
        }

        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = stringResource(data.titleResId),
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
            color = RamadanGold,
            textAlign = TextAlign.Center,
            modifier = Modifier.graphicsLayer {
                translationY = (1 - enterAnim.value) * 40f
                alpha = enterAnim.value
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(data.descriptionResId),
            fontSize = 18.sp,
            color = Color.White.copy(alpha = 0.9f),
            textAlign = TextAlign.Center,
            lineHeight = 28.sp,
            modifier = Modifier.graphicsLayer {
                translationY = (1 - enterAnim.value) * 80f
                alpha = enterAnim.value
            }
        )
    }
}