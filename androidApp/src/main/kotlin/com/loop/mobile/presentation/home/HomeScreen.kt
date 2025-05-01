package com.loop.mobile.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.loop.mobile.presentation.navigation.Screen

@Composable
fun HomeScreen(navController: NavController) {
    Column {
        Text("Welcome to Home!")
        Button(onClick = { navController.navigate(Screen.Login.route) }) {
            Text("Go to Login")
        }
    }
}