package com.loop.mobile.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.loop.mobile.domain.auth.AuthStateManager
import com.loop.mobile.presentation.auth.login.LoginScreen
import com.loop.mobile.presentation.auth.login.LoginViewModel
import com.loop.mobile.presentation.auth.logout.LogoutViewModel
import com.loop.mobile.presentation.home.HomeScreen
import com.loop.mobile.presentation.library.LibraryScreen
import com.loop.mobile.presentation.profile.ProfileScreen
import com.loop.mobile.presentation.profile.ProfileViewModel
import com.loop.mobile.presentation.search.SearchScreen
import com.loop.mobile.presentation.search.SearchViewModel
import com.loop.mobile.presentation.theme.ThemeManager

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier,
    themeManager: ThemeManager,
    authStateManager: AuthStateManager,
    profileViewModel: ProfileViewModel,
    loginViewModel: LoginViewModel,
    logoutViewModel: LogoutViewModel,
    searchViewModel: SearchViewModel,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            route = Screen.Home.route,
            enterTransition = { fadeTransition() },
            exitTransition = { fadeExitTransition() },
            popEnterTransition = { fadeTransition() },
            popExitTransition = { fadeExitTransition() }
        ) {
            HomeScreen(navController, authStateManager)
        }

        composable(
            route = Screen.Login.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it }, // Slide in from right
                    animationSpec = tween(200, easing = EaseOutCubic)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it }, // Slide out to left
                    animationSpec = tween(200, easing = EaseInCubic)
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it }, // Slide in from left when returning
                    animationSpec = tween(200, easing = EaseOutCubic)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it }, // Slide out to right when going back
                    animationSpec = tween(200, easing = EaseInCubic)
                )
            }
        ) {
            LoginScreen(navController, loginViewModel)
        }

        composable(
            Screen.Search.route,
            enterTransition = { fadeTransition() },
            exitTransition = { fadeExitTransition() },
            popEnterTransition = { fadeTransition() },
            popExitTransition = { fadeExitTransition() }
        ) {
            SearchScreen(searchViewModel)
        }

        composable(
            route = Screen.Library.route,
            enterTransition = { fadeTransition() },
            exitTransition = { fadeExitTransition() },
            popEnterTransition = { fadeTransition() },
            popExitTransition = { fadeExitTransition() }
        ) {
            LibraryScreen()
        }

        composable(
            route = Screen.Profile.route,
            enterTransition = { fadeTransition() },
            exitTransition = { fadeExitTransition() },
            popEnterTransition = { fadeTransition() },
            popExitTransition = { fadeExitTransition() }
        ) {
            ProfileScreen(navController, themeManager, profileViewModel, logoutViewModel)
        }
    }
}

private fun fadeTransition(duration: Int = 50): EnterTransition {
    return fadeIn(animationSpec = tween(duration))
}

private fun fadeExitTransition(duration: Int = 50): ExitTransition {
    return fadeOut(animationSpec = tween(duration))
}