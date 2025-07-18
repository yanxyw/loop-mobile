package com.loop.mobile.presentation.auth.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import com.loop.mobile.presentation.components.AppButton
import com.loop.mobile.presentation.components.InputField
import com.loop.mobile.presentation.navigation.Screen
import com.loop.mobile.utils.PlatformLogger
import com.loop.mobile.R
import com.loop.mobile.presentation.components.SocialSignInButton

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(navController: NavController, loginViewModel: LoginViewModel) {
    val state by loginViewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }
    val passwordFocusRequester = remember { FocusRequester() }
    val logger = PlatformLogger()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            // Clear focus and hide keyboard on success
            focusManager.clearFocus()
            keyboardController?.hide()
            loginViewModel.clearState()

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
                .padding(
                    top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { focusManager.clearFocus() }
                )
        )

        // Top: Back Icon + Title Row
        Box(
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth(0.9f)
                .align(Alignment.TopCenter)
                .height(28.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(40.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        loginViewModel.clearState()
                        navController.popBackStack()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left),
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .size(16.dp)
                        .offset(x = (-14).dp)
                )
            }
            Text(
                "Login",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Middle: Form
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(top = 72.dp)
                .align(Alignment.TopCenter), // push below top row
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InputField(
                value = state.email,
                label = "Email",
                onValueChange = { loginViewModel.onIntent(LoginAction.OnEmailChange(it)) },
                onBlur = { loginViewModel.onIntent(LoginAction.OnEmailBlur) },
                error = if (state.emailTouched) state.emailError else null,
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email,
                onImeAction = { passwordFocusRequester.requestFocus() }
            )

            Spacer(modifier = Modifier.height(12.dp))

            InputField(
                value = state.password,
                label = "Password",
                onValueChange = { loginViewModel.onIntent(LoginAction.OnPasswordChange(it)) },
                onBlur = { loginViewModel.onIntent(LoginAction.OnPasswordBlur) },
                error = if (state.passwordTouched) state.passwordError else null,
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password,
                onImeAction = {
                    focusManager.clearFocus()
                    loginViewModel.onIntent(LoginAction.OnLogin)
                },
                focusRequester = passwordFocusRequester
            )

            Spacer(modifier = Modifier.height(20.dp))

            AppButton(
                onClick = { loginViewModel.onIntent(LoginAction.OnLogin) },
                enabled = !state.isLoading,
                isLoading = state.isLoading && state.loadingProvider == "password",
            ) {
                Text("Login")
            }

            AnimatedVisibility(
                visible = state.error != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = state.error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                    logger.log("logger: ${state.error}")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Divider
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                Text(
                    "or login with",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Social Login Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SocialSignInButton(
                    navController = navController,
                    modifier = Modifier.weight(1f),
                    loginViewModel = loginViewModel,
                    provider = "google"
                )
                SocialSignInButton(
                    navController = navController,
                    modifier = Modifier.weight(1f),
                    loginViewModel = loginViewModel,
                    provider = "apple",
                    disabled = true
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
