package com.darvi.filmhunter.presentation.matcher.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darvi.filmhunter.domain.entity.MatcherSessionEntity
import com.darvi.filmhunter.domain.entity.UserEntity
import com.darvi.filmhunter.domain.usecase.auth.GetCurrentUser
import com.darvi.filmhunter.domain.usecase.matcher.GetUserSessions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionsHistoryViewModel @Inject constructor(
    private val getUserSessions: GetUserSessions,
    getCurrentUser: GetCurrentUser
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SessionsHistoryUiState())
    val uiState: StateFlow<SessionsHistoryUiState> = _uiState
    
    val currentUser: StateFlow<UserEntity?> = getCurrentUser() as StateFlow<UserEntity?>
    
    fun loadSessions() {
        val userId = currentUser.value?.id ?: return
        
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true, hasError = false)
            
            getUserSessions(userId)
                .onSuccess { sessions ->
                    _uiState.value = _uiState.value.copy(
                        sessions = sessions,
                        isLoading = false,
                        hasError = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        hasError = true,
                        errorMessage = error.message ?: "Error al cargar sesiones"
                    )
                }
        }
    }
}

data class SessionsHistoryUiState(
    val sessions: List<MatcherSessionEntity> = emptyList(),
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null
)

