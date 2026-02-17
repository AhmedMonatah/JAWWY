package com.example.weatherapp.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.weatherapp.ui.theme.AccentPurple
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

private data class PageData(
    val icon: ImageVector,
    val title: String,
    val description: String
)

private val pages = listOf(
    PageData(
        icon = Icons.Default.WbSunny,
        title = "Real-Time Weather",
        description = "Get instant weather updates with precise temperature, humidity, and wind data for your exact location."
    ),
    PageData(
        icon = Icons.Default.CalendarMonth,
        title = "7-Day Forecast",
        description = "Plan your week with detailed hourly and daily forecasts for the coming days."
    ),
    PageData(
        icon = Icons.Default.NotificationsActive,
        title = "Smart Alerts",
        description = "Receive intelligent weather alerts for severe conditions. Never be caught off guard."
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    navController: NavController,
    onFinish: () -> Unit,
    viewModel: OnboardingViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F1923))
    ) {
        FloatingDots()

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            OnboardingPageContent(
                data = pages[page],
                isActive = pagerState.currentPage == page
            )
        }

        // Bottom bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicators
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) { i ->
                    val isSelected = pagerState.currentPage == i
                    val width by animateDpAsState(
                        targetValue = if (isSelected) 28.dp else 8.dp,
                        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
                        label = ""
                    )
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(width)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) AccentPurple else Color.White.copy(alpha = 0.2f)
                            )
                    )
                }
            }

            // Button
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
                modifier = Modifier.height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                shape = RoundedCornerShape(26.dp)
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
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(data: PageData, isActive: Boolean) {
    val enterAnim = remember { Animatable(0f) }

    LaunchedEffect(isActive) {
        if (isActive) {
            enterAnim.snapTo(0f)
            enterAnim.animateTo(1f, tween(600, easing = FastOutSlowInEasing))
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val floatY by infiniteTransition.animateFloat(
        initialValue = -8f, targetValue = 8f,
        animationSpec = infiniteRepeatable(
            tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ),
        label = ""
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 40.dp)
            .padding(top = 120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon with float animation
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.graphicsLayer {
                translationY = floatY
                scaleX = 0.7f + (enterAnim.value * 0.3f)
                scaleY = 0.7f + (enterAnim.value * 0.3f)
                alpha = enterAnim.value
            }
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(AccentPurple.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = data.icon,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = AccentPurple
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = data.title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.graphicsLayer {
                translationY = (1 - enterAnim.value) * 40f
                alpha = enterAnim.value
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = data.description,
            fontSize = 15.sp,
            color = Color.White.copy(alpha = 0.55f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.graphicsLayer {
                translationY = (1 - enterAnim.value) * 60f
                alpha = enterAnim.value
            }
        )
    }
}

@Composable
private fun FloatingDots(count: Int = 20) {
    val particles = remember {
        List(count) {
            FloatArray(4).apply {
                this[0] = Random.nextFloat()         // x
                this[1] = Random.nextFloat()         // y
                this[2] = Random.nextFloat() * 3 + 1 // radius
                this[3] = Random.nextFloat() * 0.2f + 0.1f // speed
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(60_000, easing = LinearEasing)),
        label = ""
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { p ->
            val x = (p[0] * size.width + sin((time * p[3] * 0.01f).toDouble()).toFloat() * 20f) % size.width
            val y = (p[1] * size.height - (time * p[3] * 0.3f) % size.height + size.height) % size.height
            drawCircle(
                color = Color.White.copy(alpha = 0.08f),
                radius = p[2],
                center = Offset(x, y)
            )
        }
    }
}
