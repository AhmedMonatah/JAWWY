package com.example.weatherapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R

val CairoFontFamily = FontFamily(
    Font(R.font.cairo_regular, FontWeight.Normal),
    Font(R.font.cairo_medium, FontWeight.Medium),
    Font(R.font.cairo_bold, FontWeight.Bold)
)
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = CairoFontFamily,
        fontSize = 57.sp
    ),
    displayMedium = TextStyle(
        fontFamily = CairoFontFamily,
        fontSize = 45.sp
    ),
    displaySmall = TextStyle(
        fontFamily = CairoFontFamily,
        fontSize = 36.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = CairoFontFamily,
        fontSize = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = CairoFontFamily,
        fontSize = 28.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = CairoFontFamily,
        fontSize = 24.sp
    ),
    titleLarge = TextStyle(
        fontFamily = CairoFontFamily,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = CairoFontFamily,
        fontSize = 16.sp
    ),
    titleSmall = TextStyle(
        fontFamily = CairoFontFamily,
        fontSize = 14.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = CairoFontFamily,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = CairoFontFamily,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = CairoFontFamily,
        fontSize = 12.sp
    ),
    labelLarge = TextStyle(
        fontFamily = CairoFontFamily,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = CairoFontFamily,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        fontFamily = CairoFontFamily,
        fontSize = 11.sp
    )
)
