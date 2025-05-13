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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.loop.mobile.presentation.auth.logout.LogoutViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ProfileScreen(
    navController: NavController
) {
    val viewModel: ProfileViewModel = koinInject()
    val logoutViewModel: LogoutViewModel = koinInject()
    val state by viewModel.state.collectAsState()
    val logoutState by logoutViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onIntent(ProfileAction.LoadProfile)
    }

    LaunchedEffect(logoutState.isSuccess) {
        if (logoutState.isSuccess) {
            navController.navigate("login") {
                popUpTo("profile") { inclusive = true }
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
}
