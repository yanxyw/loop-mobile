package com.loop.mobile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun StepProgressIndicator(
    step: Int,
    totalSteps: Int,
    instruction: String,
    modifier: Modifier = Modifier,
    barHeight: Dp = 1.dp,
    barShape: Shape = RoundedCornerShape(1.dp),
    barBackgroundColor: Color = MaterialTheme.colorScheme.outlineVariant,
    barProgressColor: Color = MaterialTheme.colorScheme.primary,
    instructionTextStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
    instructionColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val progressFraction = (step.coerceIn(0, totalSteps - 1)).toFloat() / (totalSteps - 1).coerceAtLeast(1)

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .clip(barShape)
                .background(barBackgroundColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progressFraction)
                    .fillMaxHeight()
                    .background(barProgressColor)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = instruction,
            color = instructionColor,
            style = instructionTextStyle,
            modifier = Modifier.align(Alignment.Start)
        )
    }
}