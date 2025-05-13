package com.loop.mobile.presentation.navigation

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

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Search.route) { SearchScreen() }
        composable(Screen.Library.route) { LibraryScreen() }
        composable(Screen.Profile.route) { ProfileScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
    }
}