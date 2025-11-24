package com.darvi.filmhunter.presentation.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darvi.filmhunter.domain.entity.UserEntity
import com.darvi.filmhunter.domain.usecase.auth.GetCurrentUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val getCurrentUser: GetCurrentUser
) : ViewModel() {
    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Loading)
    val sessionState: StateFlow<SessionState> = _sessionState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getCurrentUser().collectLatest { user ->
                _sessionState.value = if (user != null) {
                    SessionState.Authenticated(user)
                } else {
                    SessionState.Unauthenticated
                }
            }
        }
    }
}

sealed interface SessionState {
    data object Loading : SessionState
    data class Authenticated(val user: UserEntity) : SessionState
    data object Unauthenticated : SessionState
}
