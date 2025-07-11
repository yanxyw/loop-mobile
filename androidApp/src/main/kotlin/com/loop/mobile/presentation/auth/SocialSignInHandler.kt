import android.accounts.AccountManager
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialInterruptedException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.loop.mobile.BuildConfig
import com.loop.mobile.presentation.auth.login.LoginAction
import com.loop.mobile.presentation.auth.login.LoginViewModel
import com.loop.mobile.utils.PlatformLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SocialSignInHandler(
    private val context: Context,
    private val loginViewModel: LoginViewModel,
    private val logger: PlatformLogger,
    private val coroutineScope: CoroutineScope
) {
    private val googleWebClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID

    var addAccountLauncher: (() -> Unit)? = null

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

    fun createCredentialRequest(filterByAuthorizedAccounts: Boolean, autoSelectEnabled: Boolean): GetCredentialRequest {
        return GetCredentialRequest.Builder()
            .addCredentialOption(
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
                    .setServerClientId(googleWebClientId)
                    .setAutoSelectEnabled(autoSelectEnabled)
                    .build()
            )
            .build()
    }

    fun showNoAccountDialog(launchAddAccount: () -> Unit) {
        AlertDialog.Builder(context)
            .setTitle("No Google Account Found")
            .setMessage("To sign in with Google, please add a Google account to your device settings.")
            .setPositiveButton("Add Account") { _, _ ->
                launchAddAccount()
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
                    showAccountExistsButNotAuthorizedDialog()
                } else {
                    addAccountLauncher?.let { showNoAccountDialog(it) }
                }
            }
            else -> {
                logger.log("Google sign-in failed: ${exception.message}")
                if (hasGoogleAccounts) {
                    showAccountExistsButNotAuthorizedDialog()
                } else {
                    addAccountLauncher?.let { showNoAccountDialog(it) }
                }
            }
        }
    }

    suspend fun attemptSignIn(provider: String) {
        when (provider.lowercase()) {
            "google" -> attemptGoogleSignIn()
            "apple" -> TODO("Implement Apple sign-in here")
            else -> logger.log("Unsupported provider: $provider")
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

        } catch (_: NoCredentialException) {
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
                addAccountLauncher?.let { showNoAccountDialog(it) }
            }
        }
    }
}