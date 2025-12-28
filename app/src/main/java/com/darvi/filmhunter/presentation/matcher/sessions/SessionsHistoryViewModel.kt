package com.darvi.filmhunter.presentation.matcher.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darvi.filmhunter.domain.entity.MatcherSessionEntity
import com.darvi.filmhunter.domain.entity.UserEntity
import com.darvi.filmhunter.domain.usecase.auth.GetCurrentUser
import com.darvi.filmhunter.domain.usecase.matcher.GetMatchedFilms
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
    private val getMatchedFilms: GetMatchedFilms,
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
                    // Load match counts for each session
                    val sessionsWithMatches = sessions.map { session ->
                        val matchCount = getMatchedFilms(session.id)
                            .getOrNull()
                            ?.size
                            ?: 0
                        SessionWithMatchCount(session, matchCount)
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        sessions = sessionsWithMatches,
                        isLoading = false,
                        hasLoaded = true,
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

data class SessionWithMatchCount(
    val session: MatcherSessionEntity,
    val matchCount: Int
)

data class SessionsHistoryUiState(
    val sessions: List<SessionWithMatchCount> = emptyList(),
    val hasLoaded: Boolean = false,
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null
)

