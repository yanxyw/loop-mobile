package com.loop.mobile.presentation.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.loop.mobile.R

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun InputField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    onBlur: () -> Unit,
    error: String?,
    modifier: Modifier = Modifier,
    imeAction: ImeAction,
    keyboardType: KeyboardType = KeyboardType.Text,
    onImeAction: () -> Unit,
    focusRequester: FocusRequester? = null,
) {
    var wasFocused by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    var passwordVisible by remember { mutableStateOf(false) }
    val isPasswordType = keyboardType == KeyboardType.Password
    val visualTransformation = if (isPasswordType && !passwordVisible)
        PasswordVisualTransformation() else VisualTransformation.None

    // Check if label should be floating (focused or has content)
    val shouldFloat = isFocused || value.isNotEmpty()

    // Animation for label position and size
    val labelOffsetY by animateFloatAsState(
        targetValue = if (shouldFloat) -10f else 13f,
        animationSpec = tween(150), label = ""
    )
    val labelOffsetX by animateFloatAsState(
        targetValue = if (shouldFloat) 8f else 12f,
        animationSpec = tween(150), label = ""
    )
    val labelScale by animateFloatAsState(
        targetValue = if (shouldFloat) 0.75f else 1f,
        animationSpec = tween(150), label = ""
    )

    // Colors
    val borderColor = when {
        error != null -> MaterialTheme.colorScheme.error
        isFocused -> MaterialTheme.colorScheme.outline
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    val labelColor = when {
        error != null -> MaterialTheme.colorScheme.error
        isFocused -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.outline
    }

    Column(modifier = modifier.animateContentSize(
        animationSpec = tween(300, easing = EaseOutCubic)
    )) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            // Border container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .border(
                        width = 1.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(4.dp)
                    )
            ) {
                // Text Input
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier
                            .weight(1f)
                            .then(focusRequester?.let { Modifier.focusRequester(it) } ?: Modifier)
                            .onFocusChanged { focusState ->
                                isFocused = focusState.isFocused
                                if (wasFocused && !focusState.isFocused) onBlur()
                                wasFocused = focusState.isFocused
                            },
                        textStyle = LocalTextStyle.current.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp
                        ),
                        singleLine = true,
                        visualTransformation = if (isPasswordType && !passwordVisible)
                            PasswordVisualTransformation() else VisualTransformation.None,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = imeAction,
                            keyboardType = keyboardType
                        ),
                        keyboardActions = KeyboardActions(onAny = { onImeAction() }),
                        interactionSource = interactionSource,
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, top = 10.dp, bottom = 10.dp)
                            ) {
                                innerTextField()
                            }
                        }
                    )

                    if (isPasswordType) {
                        IconButton(
                            onClick = { passwordVisible = !passwordVisible },
                            modifier = Modifier.padding(end = 3.dp)
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (passwordVisible) R.drawable.eye_open else R.drawable.eye_closed
                                ),
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

            }

            // Floating Label (rendered on top)
            Text(
                text = label,
                color = labelColor,
                fontSize = 16.sp,
                modifier = Modifier
                    .offset(
                        x = labelOffsetX.dp,
                        y = labelOffsetY.dp
                    )
                    .scale(labelScale)
                    .background(
                        if (shouldFloat) MaterialTheme.colorScheme.background else Color.Transparent,
                        RoundedCornerShape(2.dp)
                    )
                    .padding(horizontal = 4.dp)
                    .zIndex(1f) // Ensure label is on top
            )
        }

        // Supporting text (error message)
        AnimatedVisibility(
            visible = error != null,
            enter = slideInVertically(
                initialOffsetY = { -it / 2 },
                animationSpec = tween(300, easing = EaseOutCubic)
            ) + fadeIn(
                animationSpec = tween(300, easing = EaseOutCubic)
            ),
            exit = slideOutVertically(
                targetOffsetY = { -it / 2 },
                animationSpec = tween(200, easing = EaseInCubic)
            ) + fadeOut(
                animationSpec = tween(200, easing = EaseInCubic)
            )
        ) {
            Text(
                text = error ?: "",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}