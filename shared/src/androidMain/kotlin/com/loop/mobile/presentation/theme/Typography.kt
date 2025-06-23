package com.loop.mobile.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.loop.mobile.shared.R

val Bitter = FontFamily(
    Font(R.font.bitter_bold, weight = FontWeight.Bold)
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = Bitter,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),
//    bodyLarge = TextStyle(
//        fontFamily = Inter,
//        fontWeight = FontWeight.Normal,
//        fontSize = 16.sp
//    ),
    // Add more styles as needed
)