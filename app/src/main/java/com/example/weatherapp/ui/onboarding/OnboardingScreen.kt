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
import com.example.weatherapp.ui.onboarding.OnboardingPageData
import com.example.weatherapp.ui.onboarding.PageType
import com.example.weatherapp.ui.onboarding.onboardingPages
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

// Onboarding pages are now defined in OnboardingModels.kt

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
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Person/Assistant Visual Area
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
            text = data.title,
            fontSize = 36.sp,
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
            text = data.description,
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

@Composable
fun WeatherAssistantWithVisual(type: PageType) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        // Primary Weather Icon (Larger)
        Box(modifier = Modifier.offset(y = (-40).dp)) {
            when (type) {
                PageType.RAMADAN_NIGHT -> RamadanVisual()
                PageType.WEATHER_DAY -> WeatherVisual()
                PageType.ALERTS -> AlertsVisual()
            }
        }
        

        
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 40.dp)
                .size(100.dp, 4.dp)
                .background(RamadanGold.copy(alpha = 0.3f), CircleShape)
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
