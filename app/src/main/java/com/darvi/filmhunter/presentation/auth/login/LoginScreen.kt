package com.darvi.filmhunter.presentation.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darvi.filmhunter.R
import com.darvi.filmhunter.presentation.core.components.FilmHunterText
import com.darvi.filmhunter.presentation.core.components.FilmHunterTextField

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val state by loginViewModel.uiState.collectAsStateWithLifecycle()

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FilmHunterText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                text = stringResource(id = R.string.login_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )

            FilmHunterTextField(
                value = state.email,
                onValueChange = { loginViewModel.onEmailChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
                singleLine = true,
                label = stringResource(R.string.login_email_label),
                placeholder = stringResource(id = R.string.login_email_placeholder),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            FilmHunterTextField(
                value = state.password,
                onValueChange = { loginViewModel.onPasswordChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
                singleLine = true,
                label = stringResource(id = R.string.login_password_label),
                placeholder = stringResource(id = R.string.login_password_placeholder),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    val contentDesc = if (passwordVisible) R.string.login_hide_password else R.string.login_show_password
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible },
                        enabled = !state.isLoading
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = stringResource(id = contentDesc)
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
            )
            val isButtonEnabled = !state.isLoading && state.isLoginEnabled
            Button(
                onClick = { loginViewModel.onClick() },
                enabled = isButtonEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isButtonEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    FilmHunterText(
                        text = stringResource(id = R.string.login_cta),
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isButtonEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            TextButton(
                onClick = { },
                enabled = !state.isLoading,
                modifier = Modifier.align(Alignment.End)
            ) {
                FilmHunterText(
                    text = stringResource(id = R.string.login_forgot_password),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // CTA crear cuenta
            OutlinedButton(
                onClick = {},
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = MaterialTheme.shapes.large
            ) {
                FilmHunterText(
                    text = stringResource(id = R.string.login_create_account),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}