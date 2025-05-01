package com.loop.mobile.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchScreen() {
    val viewModel: SearchViewModel = koinInject()
    val state by viewModel.state.collectAsState()
    val focusManager: FocusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }

    // Create a Box that handles clicks outside the TextField
    Box(modifier = Modifier.fillMaxSize()) {
        // Background clickable surface
        Box(modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { focusManager.clearFocus() }
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = { viewModel.onIntent(SearchAction.OnQueryChange(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            keyboardController?.show()
                        } else {
                            keyboardController?.hide()
                        }
                    },
                label = { Text("Search") },
                keyboardOptions = KeyboardOptions.Default,
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.onIntent(SearchAction.OnSearch) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors()
            ) {
                Text("Search")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                state.results.forEach { result ->
                    Text(result, modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}