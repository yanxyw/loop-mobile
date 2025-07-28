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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.loop.mobile.presentation.components.AppButton
import com.loop.mobile.presentation.components.InputField
import com.loop.mobile.presentation.navigation.Screen
import com.loop.mobile.utils.PlatformLogger
import com.loop.mobile.presentation.components.SocialSignInSection
import com.loop.mobile.presentation.components.TopBarWithBackButton

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
        TopBarWithBackButton(
            title = "Login",
            onBack = {
                loginViewModel.clearState()
                navController.popBackStack()
            },
            modifier = Modifier.align(Alignment.TopCenter)
        )

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

            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(22.dp))

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

            SocialSignInSection(
                navController = navController,
                loginViewModel = loginViewModel,
                dividerText = "or continue with"
            )
        }

        // Bottom: Login Text
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
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.clickable {
                    loginViewModel.clearState()
                    navController.navigate(Screen.Register.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
