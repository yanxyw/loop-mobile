package com.loop.mobile.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.loop.mobile.domain.auth.AuthStateManager
import com.loop.mobile.presentation.auth.login.LoginViewModel
import com.loop.mobile.presentation.auth.logout.LogoutViewModel
import com.loop.mobile.presentation.navigation.NavGraph
import com.loop.mobile.presentation.navigation.Screen
import com.loop.mobile.presentation.profile.ProfileViewModel
import com.loop.mobile.presentation.search.SearchViewModel
import com.loop.mobile.presentation.theme.ThemeManager
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(themeManager: ThemeManager) {
    val navController = rememberNavController()
    val screens = listOf(Screen.Home, Screen.Search, Screen.Library, Screen.Profile)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val profileViewModel: ProfileViewModel = koinInject()
    val logoutViewModel: LogoutViewModel = koinInject()
    val loginViewModel: LoginViewModel = koinInject()
    val authStateManager: AuthStateManager = koinInject()
    val searchViewModel: SearchViewModel = koinInject()

    // Check if current screen is an auth screen
    val isAuthScreen = currentRoute?.startsWith("auth/") == true
    val shouldShowBottomNav = !isAuthScreen

    Scaffold(
        bottomBar = {
            if (shouldShowBottomNav) {
                Column {
                    HorizontalDivider(
                        thickness = 0.3.dp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.background
                    ) {
                        screens.forEach { screen ->
                            val isSelected = currentRoute == screen.route
                            CompositionLocalProvider(
                                LocalRippleConfiguration provides RippleConfiguration(
                                    color = Color.Transparent,
                                    rippleAlpha = RippleAlpha(
                                        0.0F, 0.0F, 0.0F, 0.0F
                                    )
                                )
                            ) {
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            painter = painterResource(id = if (isSelected) screen.selectedIconResId else screen.unselectedIconResId),
                                            contentDescription = screen.label,
                                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = screen.label,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    },
                                    selected = currentRoute == screen.route,
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        indicatorColor = Color.Transparent
                                    ),
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        NavGraph(
            navController = navController,
            modifier = if (shouldShowBottomNav) Modifier.padding(padding) else Modifier,
            themeManager = themeManager,
            authStateManager = authStateManager,
            profileViewModel = profileViewModel,
            loginViewModel = loginViewModel,
            logoutViewModel = logoutViewModel,
            searchViewModel = searchViewModel

        )
    }
}