package com.loop.mobile.presentation.navigation

import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.loop.mobile.presentation.auth.login.LoginScreen
import com.loop.mobile.presentation.home.HomeScreen
import com.loop.mobile.presentation.library.LibraryScreen
import com.loop.mobile.presentation.profile.ProfileScreen
import com.loop.mobile.presentation.search.SearchScreen
import com.loop.mobile.presentation.theme.ThemeManager

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier, themeManager: ThemeManager) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(
            route = Screen.Home.route,
            enterTransition = {
                fadeIn(animationSpec = tween(200)) +
                        scaleIn(initialScale = 0.9f, animationSpec = tween(200))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(200)) +
                        scaleOut(targetScale = 0.9f, animationSpec = tween(200))
            }
        ) {
            HomeScreen(navController)
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
            LoginScreen(navController)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController, themeManager)
        }

        composable(Screen.Search.route) { SearchScreen() }
        composable(Screen.Library.route) { LibraryScreen() }
    }
}