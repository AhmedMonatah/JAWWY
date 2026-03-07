package com.example.weatherapp.ui.splash.view.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.airbnb.lottie.compose.*
import com.example.weatherapp.R
import com.example.weatherapp.ui.theme.Gold

@Composable
fun SplashContent(
    isDark: Boolean = false
) {
    var triggered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { triggered = true }

    val entryAlpha by animateFloatAsState(
        targetValue = if (triggered) 1f else 0f,
        animationSpec = tween(1000, easing = LinearOutSlowInEasing),
        label = "ea"
    )

    // Name Animation: Scale + Fade + Slide with Overshoot
    val nameAlpha by animateFloatAsState(
        targetValue = if (triggered) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "na"
    )
    val nameScale by animateFloatAsState(
        targetValue = if (triggered) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "ns"
    )
    val nameSlide by animateFloatAsState(
        targetValue = if (triggered) 0f else 20f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "nsl"
    )

    // Tagline Animation: Slide + Fade with delay
    val tagAlpha by animateFloatAsState(
        targetValue = if (triggered) 1f else 0f,
        animationSpec = tween(1200, delayMillis = 800, easing = FastOutSlowInEasing),
        label = "ta"
    )
    val tagSlide by animateFloatAsState(
        targetValue = if (triggered) 0f else 15f,
        animationSpec = tween(1200, delayMillis = 800, easing = FastOutSlowInEasing),
        label = "ts"
    )

    val nameColor = if (isDark) Gold else Color(0xFF1565C0)
    val tagColor  = if (isDark) Color(0xFF7A94A6) else Color(0xFF546E7A)

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation_logo))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .alpha(entryAlpha)
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(220.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.app_name),
            fontSize = 48.sp,
            fontWeight = FontWeight.ExtraBold,
            color = nameColor,
            textAlign = TextAlign.Center,
            letterSpacing = 2.sp,
            modifier = Modifier
                .alpha(nameAlpha)
                .scale(nameScale)
                .offset(y = nameSlide.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.splash_subtitle),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = tagColor,
            textAlign = TextAlign.Center,
            letterSpacing = 1.sp,
            modifier = Modifier
                .alpha(tagAlpha)
                .offset(y = tagSlide.dp)
        )
    }
}
