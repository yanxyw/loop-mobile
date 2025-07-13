import android.app.AlertDialog
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.loop.mobile.BuildConfig
import com.loop.mobile.presentation.auth.login.LoginAction
import com.loop.mobile.presentation.auth.login.LoginViewModel
import com.loop.mobile.utils.PlatformLogger

class SocialSignInHandler(
    private val context: Context,
    private val loginViewModel: LoginViewModel,
    private val logger: PlatformLogger
) {
    private val googleWebClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID
    private val credentialManager = CredentialManager.create(context)

    var addAccountLauncher: (() -> Unit)? = null

    suspend fun attemptSignIn(provider: String) {
        when (provider.lowercase()) {
            "google" -> attemptGoogleSignIn()
            "apple" -> TODO("Implement Apple sign-in here")
            else -> logger.log("Unsupported provider: $provider")
        }
    }

    suspend fun attemptGoogleSignIn() {
        try {
            // First attempt: only previously authorized accounts
            val authorizedRequest = createCredentialRequest(
                filterByAuthorizedAccounts = true,
                autoSelectEnabled = true
            )

            val result = credentialManager.getCredential(
                request = authorizedRequest,
                context = context
            )

            handleSignIn(result)

        } catch (_: NoCredentialException) {
            logger.log("No authorized credentials found, trying all accounts")

            // Second attempt: allow all accounts with a fresh credential manager
            try {
                val allAccountsRequest = createCredentialRequest(
                    filterByAuthorizedAccounts = false,
                    autoSelectEnabled = false
                )

                val fallbackResult = credentialManager.getCredential(
                    request = allAccountsRequest,
                    context = context
                )

                handleSignIn(fallbackResult)

            } catch (_: GetCredentialException) {
                addAccountLauncher?.let { showNoAccountDialog(it) }
            }
        } catch (e: Exception) {
            logger.log("Unexpected error during Google sign-in: ${e.message}")
        }
    }


    fun handleSignIn(result: GetCredentialResponse) {
        when (val credential = result.credential) {

            // GoogleIdToken credential
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken

                        logger.log("Get google id token: $idToken")

                        loginViewModel.onIntent(
                            LoginAction.OnOAuthLogin(
                                provider = "google",
                                code = idToken,
                                redirectUri = "urn:ietf:wg:oauth:2.0:oob"
                            )
                        )
                    } catch (e: GoogleIdTokenParsingException) {
                        logger.log("Received an invalid google id token response: $e")
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    logger.log("Unexpected type of credential")
                }
            }
            else -> {
                // Catch any unrecognized credential type here.
                logger.log("Unexpected type of credential")
            }
        }
    }

    fun createCredentialRequest(
        filterByAuthorizedAccounts: Boolean,
        autoSelectEnabled: Boolean
    ): GetCredentialRequest {
        return GetCredentialRequest.Builder()
            .addCredentialOption(
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
                    .setAutoSelectEnabled(autoSelectEnabled)
                    .setServerClientId(googleWebClientId)
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
}