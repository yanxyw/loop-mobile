package com.loop.mobile.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AppButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    width: Dp? = null,
    cornerRadii: CornerRadii = CornerRadii(), // custom data class below
    contentPadding: PaddingValues = PaddingValues(12.dp),
    content: @Composable RowScope.() -> Unit
) {
    val shape = RoundedCornerShape(
        topStart = cornerRadii.topStart,
        topEnd = cornerRadii.topEnd,
        bottomEnd = cornerRadii.bottomEnd,
        bottomStart = cornerRadii.bottomStart
    )

    val widthModifier = if (width != null) modifier.width(width) else modifier.fillMaxWidth()

    Button(
        onClick = onClick,
        modifier = widthModifier,
        enabled = enabled,
        shape = shape,
        contentPadding = contentPadding,
    ) {
        if (isLoading) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Loading...")
            }
        } else {
            content()
        }
    }
}

data class CornerRadii(
    val topStart: Dp = 4.dp,
    val topEnd: Dp = 4.dp,
    val bottomEnd: Dp = 4.dp,
    val bottomStart: Dp = 4.dp
)
