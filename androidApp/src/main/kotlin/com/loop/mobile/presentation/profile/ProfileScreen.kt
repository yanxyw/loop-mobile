package com.loop.mobile.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.loop.mobile.domain.auth.AuthStateManager
import com.loop.mobile.presentation.auth.logout.LogoutViewModel
import com.loop.mobile.presentation.navigation.Screen
import com.loop.mobile.presentation.theme.ThemeManager
import com.loop.mobile.utils.PlatformLogger

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    themeManager: ThemeManager,
    authStateManager: AuthStateManager,
    profileViewModel: ProfileViewModel,
    logoutViewModel: LogoutViewModel
) {
    val state by profileViewModel.state.collectAsState()
    val logoutState by logoutViewModel.state.collectAsState()
    val logger = PlatformLogger()
    val context = LocalContext.current
    val currentProvider = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state.user) {
        if (state.user == null && !state.isLoading) {
            profileViewModel.onIntent(ProfileAction.LoadProfile)
        }
    }

    LaunchedEffect(logoutState.isSuccess) {
        if (logoutState.isSuccess) {
            currentProvider.value?.let { provider ->
                if (provider == "google") {
                    try {
                        val credentialManager = CredentialManager.create(context)
                        val request = ClearCredentialStateRequest()

                        credentialManager.clearCredentialState(request)
                        logger.log("Credential cleared successfully")
                    } catch (e: Exception) {
                        logger.log("Failed to clear google credential state: ${e.message}")
                    }
                }
            }
            logoutViewModel.clearState()
            profileViewModel.clearProfile()
            navController.navigate(Screen.Home.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
            return@Column
        }

        state.user?.let { user ->
            if (!user.profileUrl.isNullOrBlank()) {
                AsyncImage(
                    model = user.profileUrl,
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = user.username,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )

            Text(
                text = user.email,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = {
                currentProvider.value = authStateManager.provider.value
                logoutViewModel.logout()
            }) {
                Text("Logout")
            }
        }

        if (logoutState.isLoading) {
            CircularProgressIndicator()
        }

        logoutState.error?.let { errorMessage ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }
    }

    Column {
        Text("Appearance")
        ThemeSwitcher(themeManager = themeManager)
    }
}
