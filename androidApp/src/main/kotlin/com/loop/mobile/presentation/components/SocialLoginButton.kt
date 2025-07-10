package com.loop.mobile.presentation.components

import android.accounts.AccountManager
import android.app.AlertDialog
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialInterruptedException
import androidx.credentials.exceptions.NoCredentialException
import androidx.navigation.NavController
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import kotlinx.coroutines.launch
import com.loop.mobile.presentation.auth.login.LoginViewModel
import com.loop.mobile.utils.PlatformLogger
import com.loop.mobile.presentation.auth.login.LoginAction
import com.loop.mobile.R

import android.content.ActivityNotFoundException
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.remember

import com.loop.mobile.BuildConfig
import kotlinx.coroutines.delay

@Composable
fun SocialSignInButton(
    navController: NavController,
    loginViewModel: LoginViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val logger = PlatformLogger()
    val loginState by loginViewModel.state.collectAsState()

    // Constants
    val webClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID

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

    // Create a class to hold all the sign-in logic and avoid circular dependencies
    val signInHandler = remember {
        object {
            fun openAddAccountSettings() {
                try {
                    val intent = Intent(Settings.ACTION_ADD_ACCOUNT).apply {
                        putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
                    }
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    val fallbackIntent = Intent(Settings.ACTION_SYNC_SETTINGS)
                    context.startActivity(fallbackIntent)
                }
            }

            fun checkGoogleAccountsExist(): Boolean {
                return try {
                    val accountManager = AccountManager.get(context)
                    val accounts = accountManager.getAccountsByType("com.google")
                    val hasAccounts = accounts.isNotEmpty()
                    logger.log("Google accounts found: ${accounts.size}")
                    accounts.forEach { account ->
                        logger.log("Account: ${account.name}")
                    }
                    hasAccounts
                } catch (e: Exception) {
                    logger.log("Error checking Google accounts: ${e.message}")
                    false
                }
            }

            fun handleSignInResult(result: GetCredentialResponse) {
                val credential = result.credential

                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken

                        logger.log("Google ID Token received")

                        loginViewModel.onIntent(
                            LoginAction.OnOAuthLogin(
                                provider = "google",
                                code = idToken,
                                redirectUri = "urn:ietf:wg:oauth:2.0:oob"
                            )
                        )
                    } catch (e: GoogleIdTokenParsingException) {
                        logger.log("Invalid Google ID token response: ${e.message}")
                    }
                } else {
                    logger.log("Unexpected credential type: ${credential.type}")
                }
            }

            fun createCredentialRequest(filterByAuthorizedAccounts: Boolean, autoSelectEnabled: Boolean = true): GetCredentialRequest {
                return GetCredentialRequest.Builder()
                    .addCredentialOption(
                        GetGoogleIdOption.Builder()
                            .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
                            .setServerClientId(webClientId)
                            .setAutoSelectEnabled(autoSelectEnabled)
                            .build()
                    )
                    .build()
            }

            fun showNoAccountDialog() {
                AlertDialog.Builder(context)
                    .setTitle("No Google Account Found")
                    .setMessage("To sign in with Google, please add a Google account to your device settings.")
                    .setPositiveButton("Add Account") { _, _ ->
                        openAddAccountSettings()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }

            fun showAccountExistsButNotAuthorizedDialog() {
                AlertDialog.Builder(context)
                    .setTitle("Account Authorization Required")
                    .setMessage("Google accounts are available, but need to be authorized for this app. Please try signing in again or manage your Google account settings.")
                    .setPositiveButton("Try Again") { _, _ ->
                        // Will retry with fresh credential manager
                        coroutineScope.launch {
                            delay(1000) // Give some time for any pending operations
                            attemptGoogleSignIn()
                        }
                    }
                    .setNeutralButton("Account Settings") { _, _ ->
                        openAddAccountSettings()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }

            fun handleCredentialException(exception: GetCredentialException, hasGoogleAccounts: Boolean) {
                when (exception) {
                    is GetCredentialCancellationException -> {
                        logger.log("User cancelled sign-in")
                    }
                    is GetCredentialInterruptedException -> {
                        logger.log("Sign-in interrupted")
                    }
                    is NoCredentialException -> {
                        logger.log("No Google credentials available")
                        if (hasGoogleAccounts) {
                            // Accounts exist but not authorized
                            showAccountExistsButNotAuthorizedDialog()
                        } else {
                            // No accounts at all
                            showNoAccountDialog()
                        }
                    }
                    else -> {
                        logger.log("Google sign-in failed: ${exception.message}")
                        if (hasGoogleAccounts) {
                            showAccountExistsButNotAuthorizedDialog()
                        } else {
                            showNoAccountDialog()
                        }
                    }
                }
            }

            suspend fun attemptGoogleSignIn() {
                // Always create fresh credential manager to avoid cache issues
                val freshCredentialManager = CredentialManager.create(context)

                try {
                    // First attempt: only previously authorized accounts
                    val authorizedRequest = createCredentialRequest(
                        filterByAuthorizedAccounts = true,
                        autoSelectEnabled = true
                    )

                    val result = freshCredentialManager.getCredential(
                        request = authorizedRequest,
                        context = context
                    )

                    handleSignInResult(result)

                } catch (noCredException: NoCredentialException) {
                    logger.log("No authorized credentials found, trying all accounts")

                    // Second attempt: allow all accounts with a fresh credential manager
                    try {
                        // Small delay to ensure any pending operations complete
                        delay(500)

                        val allAccountsRequest = createCredentialRequest(
                            filterByAuthorizedAccounts = false,
                            autoSelectEnabled = false
                        )

                        // Create another fresh instance to completely avoid cache
                        val secondFreshCredentialManager = CredentialManager.create(context)
                        val fallbackResult = secondFreshCredentialManager.getCredential(
                            request = allAccountsRequest,
                            context = context
                        )

                        handleSignInResult(fallbackResult)

                    } catch (credentialException: GetCredentialException) {
                        val hasGoogleAccounts = checkGoogleAccountsExist()
                        handleCredentialException(credentialException, hasGoogleAccounts)
                    }
                } catch (e: Exception) {
                    logger.log("Unexpected error during Google sign-in: ${e.message}")
                    val hasGoogleAccounts = checkGoogleAccountsExist()
                    if (hasGoogleAccounts) {
                        showAccountExistsButNotAuthorizedDialog()
                    } else {
                        showNoAccountDialog()
                    }
                }
            }
        }
    }

    Button(
        onClick = {
            coroutineScope.launch {
                signInHandler.attemptGoogleSignIn()
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
                    painter = painterResource(id = R.drawable.home_filled), // Replace with Google logo
                    contentDescription = "Google logo",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Continue with Google")
            }
        }
    }
}