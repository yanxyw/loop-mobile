package com.loop.mobile.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.loop.mobile.shared.R

val Bitter = FontFamily(
    Font(R.font.bitter_bold, weight = FontWeight.Bold),
    Font(R.font.bitter_semibold, weight = FontWeight.SemiBold),
    Font(R.font.bitter_medium, weight = FontWeight.Medium),
    Font(R.font.bitter_regular, weight = FontWeight.Normal)
)

val Inter = FontFamily(
    Font(R.font.inter_bold, weight = FontWeight.Bold),
    Font(R.font.inter_semibold, weight = FontWeight.SemiBold),
    Font(R.font.inter_medium, weight = FontWeight.Medium),
    Font(R.font.inter_regular, weight = FontWeight.Normal),
    Font(R.font.inter_light, weight = FontWeight.Light)
)

val Typography = Typography(
    displayLarge = TextStyle( // Splashscreen
        fontFamily = Bitter,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Bitter,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Bitter,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Light,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Light,
        fontSize = 12.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Light,
        fontSize = 12.sp
    ),
)