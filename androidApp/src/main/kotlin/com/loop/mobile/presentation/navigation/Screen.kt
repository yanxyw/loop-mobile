package com.loop.mobile.presentation.navigation

import com.loop.mobile.R

sealed class Screen(val route: String, val label: String, val selectedIconResId: Int, val unselectedIconResId: Int) {
    data object Home : Screen("home", "Home", R.drawable.home_filled, R.drawable.home_outlined)
    data object Search : Screen("search", "Search", R.drawable.search_filled, R.drawable.search_outlined)
    data object Library : Screen("library", "Library", R.drawable.library_filled, R.drawable.library_outlined)
    data object Profile : Screen("profile", "Me", R.drawable.profile_filled, R.drawable.profile_outlined)
    data object Login : Screen("auth/login", "Login", R.drawable.home_filled, R.drawable.home_outlined)
    data object Register : Screen("auth/register", "Register", R.drawable.home_filled, R.drawable.home_outlined)
}