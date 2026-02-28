package com.example.weatherapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val LightBackground = Color(0xFFF8F5EB)
val LightSurface = Color(0xFFFFFDF5)
val SoftGold = Color(0xFFFFF3D6)
val SoftNavy = Color(0xFFE8EDF5)

private val DarkColorScheme = darkColorScheme(
    primary = RamadanGold,
    secondary = RamadanMoonGlow,
    tertiary = RamadanLanternOrange,
    background = RamadanDeepNavy,
    surface = RamadanDarkBlue,
    onPrimary = Color.White,
    onSecondary = RamadanDeepNavy,
    onTertiary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    surfaceVariant = TranslucentWhite
)

private val LightColorScheme = lightColorScheme(
    primary = RamadanGold,
    secondary = RamadanLanternOrange,
    tertiary = Color(0xFF7D5260),
    background = LightBackground,
    surface = LightSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = RamadanDeepNavy,
    onSurface = RamadanDeepNavy,
    surfaceVariant = SoftGold
)

@Composable
fun WeatherAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}