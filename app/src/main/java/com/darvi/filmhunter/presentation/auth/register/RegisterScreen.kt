package com.darvi.filmhunter.presentation.auth.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darvi.filmhunter.presentation.core.components.FilmHunterText

@Composable
fun RegisterScreen(
    registerViewModel: RegisterViewModel = hiltViewModel()
) {
    val state by registerViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.email,
                onValueChange = { registerViewModel.onEmailChanged(it) },
                label = { FilmHunterText(text = "Email") }
            )
            Spacer(Modifier.height(8.dp))
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.password,
                onValueChange = { registerViewModel.onPasswordChanged(it) },
                label = { FilmHunterText(text = "Password") }
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { registerViewModel.onClick() }
            ) {
                FilmHunterText(text = "Registrar")
            }
        }
    }
}