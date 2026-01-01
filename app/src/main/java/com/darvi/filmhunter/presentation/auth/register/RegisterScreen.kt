package com.darvi.filmhunter.presentation.auth.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darvi.filmhunter.R
import com.darvi.filmhunter.presentation.core.components.FilmHunterPrimaryButton
import com.darvi.filmhunter.presentation.core.components.FilmHunterSecondaryButton
import com.darvi.filmhunter.presentation.core.components.FilmHunterText
import com.darvi.filmhunter.presentation.core.components.FilmHunterTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    registerViewModel: RegisterViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {
    val state by registerViewModel.uiState.collectAsStateWithLifecycle()

    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var repeatPasswordVisible by rememberSaveable { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 12.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {

        // App Logo
        Image(
            painter = painterResource(id = R.drawable.film_hunter_logo),
            contentDescription = stringResource(id = R.string.app_name),
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(200.dp)
        )

        // Form Fields
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            FilmHunterText(
                text = stringResource(id = R.string.register_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            FilmHunterTextField(
                value = state.email,
                onValueChange = { registerViewModel.onEmailChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
                singleLine = true,
                label = stringResource(R.string.register_email_label),
                placeholder = stringResource(id = R.string.register_email_placeholder),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            FilmHunterTextField(
                value = state.password,
                onValueChange = { registerViewModel.onPasswordChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
                singleLine = true,
                label = stringResource(id = R.string.register_password_label),
                placeholder = stringResource(id = R.string.register_password_placeholder),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    val contentDesc = if (passwordVisible) R.string.register_hide_password else R.string.register_show_password
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
                    imeAction = ImeAction.Next
                ),
            )

            FilmHunterTextField(
                value = state.repeatPassword,
                onValueChange = { registerViewModel.onRepeatPasswordChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
                singleLine = true,
                label = stringResource(id = R.string.register_repeat_password_label),
                placeholder = stringResource(id = R.string.register_repeat_password_placeholder),
                visualTransformation = if (repeatPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (repeatPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    val contentDesc = if (repeatPasswordVisible) R.string.register_hide_password else R.string.register_show_password
                    IconButton(
                        onClick = { repeatPasswordVisible = !repeatPasswordVisible },
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

            Spacer(modifier = Modifier.height(8.dp))

            // Register Button
            FilmHunterPrimaryButton(
                text = stringResource(id = R.string.register_cta),
                onClick = { registerViewModel.onClick() },
                enabled = !state.isLoading && state.isRegisterEnabled,
                isLoading = state.isLoading
            )
        }

        // Divider with Register Section
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            FilmHunterSecondaryButton(
                text = stringResource(id = R.string.register_already_have_account),
                onClick = { navigateBack() },
                enabled = !state.isLoading
            )
        }
    }

}