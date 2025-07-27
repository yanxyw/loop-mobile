package com.loop.mobile.presentation.auth.signup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.loop.mobile.presentation.auth.login.LoginViewModel
import com.loop.mobile.presentation.components.AppButton
import com.loop.mobile.presentation.components.InputField
import com.loop.mobile.presentation.components.SocialSignInSection
import com.loop.mobile.presentation.components.StepProgressIndicator
import com.loop.mobile.presentation.components.TopBarWithBackButton
import com.loop.mobile.presentation.navigation.Screen
import com.loop.mobile.utils.PlatformLogger

@Composable
fun SignUpScreen(navController: NavController, signUpViewModel: SignUpViewModel, loginViewModel: LoginViewModel) {
    val state by signUpViewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }
    val logger = PlatformLogger()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            focusManager.clearFocus()
            keyboardController?.hide()
            signUpViewModel.clearState()

            // TODO: change to confirmation screen
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

        TopBarWithBackButton(
            title = "Sign Up",
            onBack = {
                if (state.step > 0) {
                    signUpViewModel.onIntent(SignUpAction.PreviousStep)
                } else {
                    signUpViewModel.clearState()
                    navController.popBackStack()
                }
            },
            modifier = Modifier.align(Alignment.TopCenter)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(top = 72.dp)
                .align(Alignment.TopCenter),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.step > 0) {
                StepProgressIndicator(
                    step = state.step,
                    totalSteps = 3,
                    instruction = when (state.step) {
                        1 -> "Create a password"
                        2 -> "Choose a username"
                        else -> ""
                    }
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            when (state.step) {
                0 -> {
                    InputField(
                        value = state.email,
                        label = "Email",
                        onValueChange = { signUpViewModel.onIntent(SignUpAction.OnEmailChange(it)) },
                        onBlur = { signUpViewModel.onIntent(SignUpAction.OnEmailBlur) },
                        error = if (state.emailTouched) state.emailError else null,
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Email,
                        onImeAction = { focusManager.clearFocus() }
                    )
                }

                1 -> {
                    InputField(
                        value = state.password,
                        label = "Password",
                        onValueChange = { signUpViewModel.onIntent(SignUpAction.OnPasswordChange(it)) },
                        onBlur = { signUpViewModel.onIntent(SignUpAction.OnPasswordBlur) },
                        error = if (state.passwordTouched) state.passwordError else null,
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Password,
                        onImeAction = { focusManager.clearFocus() }
                    )
                }

                2 -> {
                    InputField(
                        value = state.username,
                        label = "Username",
                        onValueChange = { signUpViewModel.onIntent(SignUpAction.OnUsernameChange(it)) },
                        onBlur = { signUpViewModel.onIntent(SignUpAction.OnUsernameBlur) },
                        error = if (state.usernameTouched) state.usernameError else null,
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Text,
                        onImeAction = {
                            focusManager.clearFocus()
                            signUpViewModel.onIntent(SignUpAction.OnSignUp)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AppButton(
                onClick = {
                    when (state.step) {
                        0 -> {
                            signUpViewModel.onIntent(SignUpAction.OnEmailBlur)
                            if (state.emailError == null) {
                                signUpViewModel.onIntent(SignUpAction.NextStep)
                            }
                        }

                        1 -> {
                            signUpViewModel.onIntent(SignUpAction.OnPasswordBlur)
                            if (state.passwordError == null) {
                                signUpViewModel.onIntent(SignUpAction.NextStep)
                            }
                        }

                        2 -> {
                            signUpViewModel.onIntent(SignUpAction.OnUsernameBlur)
                            signUpViewModel.onIntent(SignUpAction.OnSignUp)
                        }
                    }
                },
                enabled = !state.isLoading,
                isLoading = state.isLoading,
            ) {
                Text(
                    when (state.step) {
                        0 -> "Let's get started"
                        1 -> "Next"
                        2 -> "Create Account"
                        else -> ""
                    }
                )
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

            if (state.step == 0) {
                SocialSignInSection(
                    navController = navController,
                    loginViewModel = loginViewModel,
                    dividerText = "or continue with"
                )
            }
        }

        // Bottom: Sign up Text
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Already have an account? ")
            Text(
                "Log in",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    signUpViewModel.clearState()
                    navController.navigate(Screen.Login.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
