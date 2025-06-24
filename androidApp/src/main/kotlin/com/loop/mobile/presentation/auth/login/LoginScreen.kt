package com.loop.mobile.presentation.auth.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import com.loop.mobile.presentation.components.InputField
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
        // Dismiss keyboard on background tap
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { focusManager.clearFocus() }
                )
        )

        // Top: Back Icon + Title Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 24.dp, start = 32.dp, end = 32.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Back",
                modifier = Modifier
                    .clickable { navController.popBackStack() }
                    .size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Login",
                style = MaterialTheme.typography.headlineMedium,
            )
        }

        // Middle: Form
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .padding(top = 64.dp), // push below top row
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InputField(
                value = state.email,
                label = "Email",
                onValueChange = { viewModel.onIntent(LoginAction.OnEmailChange(it)) },
                onBlur = { viewModel.onIntent(LoginAction.OnEmailBlur) },
                error = if (state.emailTouched) state.emailError else null,
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email,
                onImeAction = { passwordFocusRequester.requestFocus() }
            )

            InputField(
                value = state.password,
                label = "Password",
                onValueChange = { viewModel.onIntent(LoginAction.OnPasswordChange(it)) },
                onBlur = { viewModel.onIntent(LoginAction.OnPasswordBlur) },
                error = if (state.passwordTouched) state.passwordError else null,
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password,
                onImeAction = {
                    focusManager.clearFocus()
                    viewModel.onIntent(LoginAction.OnLogin)
                },
                focusRequester = passwordFocusRequester
            )

//            Spacer(modifier = Modifier.height(16.dp))

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

        // Bottom: Sign Up Text
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Don’t have an account? ")
            Text(
                "Sign up",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    // TODO: Add navigation to sign up screen
                }
            )
        }
    }
}
