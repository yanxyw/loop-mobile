package com.loop.mobile.presentation.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

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
    focusRequester: FocusRequester? = null
) {
    var wasFocused by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = error != null,
        supportingText = {
            if (error != null) Text(error, color = MaterialTheme.colorScheme.error)
        },
        singleLine = true,
        visualTransformation = if (keyboardType == KeyboardType.Password)
            PasswordVisualTransformation() else VisualTransformation.None,
        modifier = modifier
            .then((focusRequester?.let { Modifier.focusRequester(it) } ?: Modifier))
            .onFocusChanged {
                if (wasFocused && !it.isFocused) onBlur()
                wasFocused = it.isFocused
            },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = keyboardType
        ),
        keyboardActions = KeyboardActions(
            onAny = { onImeAction() }
        )
    )
}
