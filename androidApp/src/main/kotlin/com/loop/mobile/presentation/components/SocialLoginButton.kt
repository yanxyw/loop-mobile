package com.loop.mobile.presentation.components

import SocialSignInHandler
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.*
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
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel,
    provider: String,
    disabled: Boolean = false
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val logger = PlatformLogger()
    val loginState by loginViewModel.state.collectAsState()
    val capitalizedProvider = provider.replaceFirstChar { it.uppercaseChar() }

    var showNoAccountDialog by remember { mutableStateOf(false) }

    val socialSignInHandler = remember {
        SocialSignInHandler(context, loginViewModel, logger)
    }

    val addAccountLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        // Try again after account added
        coroutineScope.launch {
            socialSignInHandler.attemptSignIn(provider)
        }
    }

    LaunchedEffect(Unit) {
        socialSignInHandler.addAccountLauncher = {
            addAccountLauncher.launch(
                Intent(Settings.ACTION_ADD_ACCOUNT).apply {
                    putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
                }
            )
        }

        socialSignInHandler.onNoAccountFound = {
            showNoAccountDialog = true
        }
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

    AppAlertDialog(
        showDialog = showNoAccountDialog,
        onDismiss = { showNoAccountDialog = false },
        onConfirm = {
            showNoAccountDialog = false
            socialSignInHandler.addAccountLauncher?.invoke()
        },
        title = "No $provider Account Found",
        content = "To sign in with $provider, please add a $provider account to your device settings."
    )

    OutlinedButton(
        onClick = {
            coroutineScope.launch {
                socialSignInHandler.attemptSignIn(provider)
            }
        },
        enabled = !disabled && !loginState.isLoading,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        if (loginState.isLoading && loginState.loadingProvider == provider) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colorScheme.outline,
                    strokeWidth = 1.5.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Loading...",
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
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
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    capitalizedProvider,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}