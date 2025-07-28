package com.loop.mobile.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.loop.mobile.presentation.auth.login.LoginViewModel

@Composable
fun SocialSignInSection(
    navController: NavController,
    loginViewModel: LoginViewModel,
    dividerText: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
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
                text = dividerText,
                style = MaterialTheme.typography.bodyMedium,
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

        // Social buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
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
}