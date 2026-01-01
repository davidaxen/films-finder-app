package com.darvi.filmhunter.presentation.auth.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darvi.filmhunter.domain.usecase.auth.Register
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val register: Register,
): ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun onEmailChanged(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                isRegisterEnabled = validateForm(email, it.password, it.repeatPassword)
            )
        }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                isRegisterEnabled = validateForm(it.email, password, it.repeatPassword)
            )
        }
    }

    fun onRepeatPasswordChanged(repeatPassword: String) {
        _uiState.update {
            it.copy(
                repeatPassword = repeatPassword,
                isRegisterEnabled = validateForm(it.email, it.password, repeatPassword)
            )
        }
    }

    fun onClick() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            val response = register(_uiState.value.email, _uiState.value.password)

            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(isLoading = false) }
                Log.i("PRUEBA", response.toString())
            }
        }
    }

    private fun validateForm(email: String, password: String, repeatPassword: String): Boolean {
        return email.isNotBlank() && 
               password.isNotBlank() && 
               repeatPassword.isNotBlank() &&
               password == repeatPassword
    }
}

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val isLoading: Boolean = false,
    val isRegisterEnabled: Boolean = false
)