package com.loop.mobile.presentation.auth.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.loop.mobile.presentation.navigation.Screen
import org.koin.compose.koinInject

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(navController: NavController) {
    val viewModel: LoginViewModel = koinInject()
    val state by viewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }
    val passwordFocusRequester = remember { FocusRequester() }
    var emailWasFocused by remember { mutableStateOf(false) }
    var passwordWasFocused by remember { mutableStateOf(false) }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            // Clear focus and hide keyboard on success
            focusManager.clearFocus()
            keyboardController?.hide()

            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Click outside to clear focus
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { focusManager.clearFocus() }
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome Back!", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.onIntent(LoginAction.OnEmailChange(it)) },
                label = { Text("Email") },
                isError = state.emailTouched && state.emailError != null,
                supportingText = {
                    if (state.emailTouched && state.emailError != null) {
                        Text(state.emailError!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (emailWasFocused && !focusState.isFocused) {
                            viewModel.onIntent(LoginAction.OnEmailBlur)
                        }
                        emailWasFocused = focusState.isFocused
                    },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { passwordFocusRequester.requestFocus() }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = { viewModel.onIntent(LoginAction.OnPasswordChange(it)) },
                label = { Text("Password") },
                isError = state.passwordTouched && state.passwordError != null,
                supportingText = {
                    if (state.passwordTouched && state.passwordError != null) {
                        Text(state.passwordError!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(passwordFocusRequester)
                    .onFocusChanged { focusState ->
                        if (passwordWasFocused && !focusState.isFocused) {
                            viewModel.onIntent(LoginAction.OnPasswordBlur)
                        }
                        passwordWasFocused = focusState.isFocused
                    },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        viewModel.onIntent(LoginAction.OnLogin)
                    }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = { viewModel.onIntent(LoginAction.OnLogin) },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 8.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logging In...")
                } else {
                    Text("Log In")
                }
            }

            if (state.isSuccess) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Login successful! ✅",
                    color = Color.Green,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
