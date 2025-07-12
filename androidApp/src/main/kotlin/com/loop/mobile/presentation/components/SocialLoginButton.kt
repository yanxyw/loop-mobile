package com.loop.mobile.presentation.components

import SocialSignInHandler
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.loop.mobile.presentation.auth.login.LoginViewModel
import com.loop.mobile.utils.PlatformLogger
import com.loop.mobile.R

@Composable
fun SocialSignInButton(
    navController: NavController,
    loginViewModel: LoginViewModel,
    provider: String
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val logger = PlatformLogger()
    val loginState by loginViewModel.state.collectAsState()
    val capitalizedProvider = provider.replaceFirstChar { it.uppercaseChar() }
    val socialSignInHandler = remember {
        SocialSignInHandler(context, loginViewModel, logger, coroutineScope)
    }

    // Handle login state changes
    LaunchedEffect(loginState.isSuccess) {
        if (loginState.isSuccess) {
            logger.log("Login successful")
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
            loginViewModel.clearState()
        }
    }

    // Handle login errors
    LaunchedEffect(loginState.error) {
        loginState.error?.let { error ->
            logger.log("Login failed: $error")
            // You can show a toast or snackbar here
        }
    }

    Button(
        onClick = {
            coroutineScope.launch {
                socialSignInHandler.attemptSignIn(provider)
            }
        },
        enabled = !loginState.isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(4.dp),
    ) {
        if (loginState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(
                        id = when (provider) {
                            "google" -> R.drawable.google
                            "apple" -> R.drawable.apple
                            else -> R.drawable.google
                        }
                    ),
                    contentDescription = "$capitalizedProvider logo",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Continue with $capitalizedProvider")
            }
        }
    }
}