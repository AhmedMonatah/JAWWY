package com.example.weatherapp.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.ui.theme.RamadanDeepNavy
import com.example.weatherapp.ui.theme.RamadanDarkBlue
import com.example.weatherapp.ui.theme.RamadanGold
import com.example.weatherapp.ui.theme.RamadanLanternOrange
import com.example.weatherapp.ui.theme.RamadanMoonGlow
import com.example.weatherapp.ui.theme.RamadanStarLight
import com.example.weatherapp.ui.theme.RamadanMidnight
import com.example.weatherapp.ui.theme.RamadanNightSky
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

private data class OnboardingPageData(
    val title: String,
    val description: String,
    val type: PageType
)

private enum class PageType {
    RAMADAN_NIGHT,
    WEATHER_DAY,
    ALERTS
}

private val onboardingPages = listOf(
    OnboardingPageData(
        title = "Ramadan Blessings",
        description = "Experience the holy month with accurate prayer times and weather guidance for your spiritual journey.",
        type = PageType.RAMADAN_NIGHT
    ),
    OnboardingPageData(
        title = "Accurate Forecast",
        description = "Plan your day with precise weather updates. Know exactly when to seek shade or enjoy the sun.",
        type = PageType.WEATHER_DAY
    ),
    OnboardingPageData(
        title = "Stay Informed",
        description = "Get timely weather alerts to keep you and your family safe during sudden weather changes.",
        type = PageType.ALERTS
    )
)

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
        // Dynamic Background based on page
        BackgroundTransition(pagerState.currentPage, pagerState.currentPageOffsetFraction)

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            OnboardingPageContent(
                data = onboardingPages[page],
                isActive = pagerState.currentPage == page
            )
        }

        // Bottom Controls
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            // Indicators
            Row(
                modifier = Modifier.align(Alignment.CenterStart),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) { i ->
                    val isSelected = pagerState.currentPage == i
                    val width by animateDpAsState(
                        targetValue = if (isSelected) 32.dp else 10.dp,
                        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
                        label = ""
                    )
                    val color by animateColorAsState(
                        targetValue = if (isSelected) RamadanGold else Color.White.copy(alpha = 0.3f),
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

            // Next/Finish Button
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
                    .align(Alignment.CenterEnd)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RamadanGold),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                AnimatedContent(
                    targetState = isLast,
                    transitionSpec = {
                        fadeIn(tween(300)) + slideInHorizontally { it / 2 } togetherWith
                                fadeOut(tween(200)) + slideOutHorizontally { -it / 2 }
                    },
                    label = ""
                ) { last ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            if (last) "Get Started" else "Next",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = RamadanDeepNavy
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = RamadanDeepNavy
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BackgroundTransition(page: Int, offset: Float) {
    val color1 = listOf(RamadanMidnight, RamadanDeepNavy, RamadanNightSky) // Night
    val color2 = listOf(Color(0xFF4A90E2), Color(0xFF87CEEB), Color(0xFFE0F7FA)) // Day/Weather
    val color3 = listOf(Color(0xFF2C3E50), Color(0xFF34495E), Color(0xFF5D6D7E)) // Storm/Alerts

    val targetColors = when (page) {
        0 -> color1
        1 -> color2
        else -> color3
    }
    
    // We can't easily interpolate arrays of colors, so we just switch brushes with animation
    // Ideally we would interpolate, but for simplicity we crossfade based on page
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (page == 0) color1 else if (page == 1) color2 else color3
                )
            )
    ) {
         // Ambient effects
         if (page == 0) RamadanSkyEffect()
         if (page == 1) SunCloudEffect()
         if (page == 2) RainEffect()
    }
}


@Composable
private fun OnboardingPageContent(data: OnboardingPageData, isActive: Boolean) {
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
            .padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main Visual Area
        Box(
            modifier = Modifier
                .size(280.dp)
                .graphicsLayer {
                    scaleX = 0.8f + (enterAnim.value * 0.2f)
                    scaleY = 0.8f + (enterAnim.value * 0.2f)
                    alpha = enterAnim.value
                },
            contentAlignment = Alignment.Center
        ) {
            when (data.type) {
                PageType.RAMADAN_NIGHT -> RamadanVisual()
                PageType.WEATHER_DAY -> WeatherVisual()
                PageType.ALERTS -> AlertsVisual()
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = data.title,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = if (data.type == PageType.WEATHER_DAY) Color.White else RamadanGold,
            textAlign = TextAlign.Center,
            modifier = Modifier.graphicsLayer {
                translationY = (1 - enterAnim.value) * 50f
                alpha = enterAnim.value
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = data.description,
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.graphicsLayer {
                translationY = (1 - enterAnim.value) * 100f
                alpha = enterAnim.value
            }
        )
    }
}

@Composable
fun RamadanVisual() {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val floatY by infiniteTransition.animateFloat(
        initialValue = -15f, targetValue = 15f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = ""
    )
    
    Box(modifier = Modifier.graphicsLayer { translationY = floatY }) {
         Text(text = "🌙", fontSize = 120.sp)
         Text(text = "🏮", fontSize = 60.sp, modifier = Modifier.align(Alignment.BottomEnd).offset(x = (-20).dp))
    }
}

@Composable
fun WeatherVisual() {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val rotate by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing), RepeatMode.Restart),
        label = ""
    )
    
    Box {
         // Sun
         Canvas(modifier = Modifier.size(140.dp)) {
             drawCircle(
                 brush = Brush.radialGradient(listOf(Color(0xFFFFD700), Color(0xFFFFA500))),
                 radius = size.minDimension / 2
             )
         }
         // Clouds
         Text(text = "☁️", fontSize = 100.sp, modifier = Modifier.align(Alignment.BottomStart))
    }
}

@Composable
fun AlertsVisual() {
     val infiniteTransition = rememberInfiniteTransition(label = "")
     val alpha by infiniteTransition.animateFloat(
         initialValue = 1f, targetValue = 0.3f,
         animationSpec = infiniteRepeatable(tween(800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
         label = ""
     )
     
     Box {
         Text(text = "⚡", fontSize = 120.sp, modifier = Modifier.align(Alignment.TopCenter))
         Text(text = "⚠️", fontSize = 60.sp, modifier = Modifier.align(Alignment.BottomEnd).graphicsLayer { this.alpha = alpha })
     }
}


@Composable
private fun RamadanSkyEffect() {
    val particles = remember { List(20) { Random.nextFloat() to Random.nextFloat() } }
    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { (x, y) ->
            drawCircle(
                color = Color.White.copy(alpha = 0.6f),
                radius = Random.nextFloat() * 3f + 1f,
                center = Offset(x * size.width, y * size.height)
            )
        }
    }
}

@Composable
fun SunCloudEffect() {
     // Subtle sun rays or cloud movement could go here
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha=0.1f), Color.Transparent),
                radius = size.maxDimension * 0.8f,
                center = Offset(size.width, 0f)
            ),
             radius = size.maxDimension * 0.8f,
             center = Offset(size.width, 0f)
        )
    }
}

@Composable
fun RainEffect() {
     val infiniteTransition = rememberInfiniteTransition(label = "")
     val offset by infiniteTransition.animateFloat(
         initialValue = 0f, targetValue = 1000f,
         animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Restart),
         label = ""
     )
     
     Canvas(modifier = Modifier.fillMaxSize()) {
         for(i in 0..30) {
             val x = (i * 50f) % size.width
             val y = (offset + i * 100f) % size.height
             drawLine(
                 color = Color.White.copy(alpha = 0.3f),
                 start = Offset(x, y),
                 end = Offset(x - 10f, y + 20f),
                 strokeWidth = 2f
             )
         }
     }
}

