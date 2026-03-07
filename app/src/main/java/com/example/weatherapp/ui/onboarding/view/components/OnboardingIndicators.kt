package com.example.weatherapp.ui.onboarding.view.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingIndicators(currentPage: Int, pageCount: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { i ->
            val isSelected = currentPage == i
            val width by animateDpAsState(
                targetValue = if (isSelected) 36.dp else 12.dp,
                animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
                label = ""
            )
            val color by animateColorAsState(
                targetValue = if (isSelected) androidx.compose.material3.MaterialTheme.colorScheme.primary else androidx.compose.material3.MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
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
}
