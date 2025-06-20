package com.loop.mobile.presentation.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.loop.mobile.presentation.theme.ThemeManager

@Composable
fun ThemeSwitcher(themeManager: ThemeManager) {
    val currentSelection by themeManager.overrideDarkMode.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(
                when (currentSelection) {
                    null -> "System"
                    false -> "Light"
                    true -> "Dark"
                }
            )
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("System") },
                onClick = {
                    themeManager.setOverrideDarkMode(null)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Light") },
                onClick = {
                    themeManager.setOverrideDarkMode(false)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Dark") },
                onClick = {
                    themeManager.setOverrideDarkMode(true)
                    expanded = false
                }
            )
        }
    }
}
