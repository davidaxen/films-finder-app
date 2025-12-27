package com.darvi.filmhunter.presentation.matcher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.darvi.filmhunter.data.util.Base32CodeGenerator
import com.darvi.filmhunter.domain.entity.MatcherSessionEntity
import com.darvi.filmhunter.domain.entity.UserEntity
import com.darvi.filmhunter.domain.usecase.auth.GetCurrentUser
import com.darvi.filmhunter.domain.usecase.matcher.CancelMatcherSession
import com.darvi.filmhunter.domain.usecase.matcher.CreateMatcherSession
import com.darvi.filmhunter.domain.usecase.matcher.JoinMatcherSession
import com.darvi.filmhunter.domain.usecase.matcher.InitiateMatcherSession
import com.darvi.filmhunter.domain.usecase.matcher.SubscribeToSessionMembers
import com.darvi.filmhunter.domain.usecase.matcher.UnsubscribeFromSessionMembers
import com.darvi.filmhunter.domain.usecase.matcher.SubscribeToSessionStatus
import com.darvi.filmhunter.domain.usecase.matcher.UnsubscribeFromSessionStatus
import com.darvi.filmhunter.presentation.core.model.FilmType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MatcherViewModel @Inject constructor(
    private val createMatcherSession: CreateMatcherSession,
    private val cancelMatcherSession: CancelMatcherSession,
    private val joinMatcherSession: JoinMatcherSession,
    private val initiateMatcherSession: InitiateMatcherSession,
    private val subscribeToSessionMembers: SubscribeToSessionMembers,
    private val unsubscribeFromSessionMembers: UnsubscribeFromSessionMembers,
    private val subscribeToSessionStatus: SubscribeToSessionStatus,
    private val unsubscribeFromSessionStatus: UnsubscribeFromSessionStatus,
    getCurrentUser: GetCurrentUser
) : ViewModel() {
    private val _uiState = MutableStateFlow(MatcherUiState())
    val uiState: StateFlow<MatcherUiState> = _uiState
    
    private val _sessionCreationState = MutableStateFlow<SessionCreationState>(SessionCreationState.Idle)
    val sessionCreationState: StateFlow<SessionCreationState> = _sessionCreationState
    
    private val _sessionJoinState = MutableStateFlow<SessionJoinState>(SessionJoinState.Idle)
    val sessionJoinState: StateFlow<SessionJoinState> = _sessionJoinState
    
    private val _currentSession = MutableStateFlow<MatcherSessionEntity?>(null)
    val currentSession: StateFlow<MatcherSessionEntity?> = _currentSession
    
    private val _hasOtherUserJoined = MutableStateFlow(false)
    val hasOtherUserJoined: StateFlow<Boolean> = _hasOtherUserJoined
    
    private val _sessionBecameActive = MutableStateFlow(false)
    val sessionBecameActive: StateFlow<Boolean> = _sessionBecameActive

    val currentUser: StateFlow<UserEntity?> = getCurrentUser() as StateFlow<UserEntity?>

    fun onCodeChanged(code: String) {
        if (code.length > 6) return
        _uiState.update {
            it.copy(
                code = code,
                joinRoomEnabled = code.length == 6
            )
        }
    }

    fun onFilmTypeSelected(filmType: FilmType) {
        _uiState.update {
            it.copy(selectedFilmType = filmType)
        }
    }

    fun onGenreToggled(genreId: Int) {
        _uiState.update { state ->
            when (state.selectedFilmType) {
                FilmType.MOVIE -> {
                    val updatedGenres = if (state.selectedMovieGenres.contains(genreId)) {
                        state.selectedMovieGenres - genreId
                    } else {
                        state.selectedMovieGenres + genreId
                    }
                    state.copy(selectedMovieGenres = updatedGenres)
                }
                FilmType.SERIES -> {
                    val updatedGenres = if (state.selectedSeriesGenres.contains(genreId)) {
                        state.selectedSeriesGenres - genreId
                    } else {
                        state.selectedSeriesGenres + genreId
                    }
                    state.copy(selectedSeriesGenres = updatedGenres)
                }
            }
        }
    }

    fun onSelectAllPlatforms() {
        _uiState.update {
            it.copy(
                selectAllPlatforms = true,
                selectedPlatforms = emptySet()
            )
        }
    }

    fun onPlatformToggled(platformId: Int) {
        _uiState.update { state ->
            // If "all" is currently selected, deselect it and select only this platform
            if (state.selectAllPlatforms) {
                return@update state.copy(
                    selectAllPlatforms = false,
                    selectedPlatforms = setOf(platformId)
                )
            }

            val isCurrentlySelected = state.selectedPlatforms.contains(platformId)
            val updatedPlatforms = if (isCurrentlySelected) {
                state.selectedPlatforms - platformId
            } else {
                state.selectedPlatforms + platformId
            }

            // If user selects all 3 platforms, automatically select "all" and clear individual selections
            val allPlatformIds = setOf(8, 337, 1899) // Netflix, Disney+, HBO Max
            val shouldSelectAll = updatedPlatforms.size == allPlatformIds.size && 
                                  updatedPlatforms.containsAll(allPlatformIds)

            state.copy(
                selectAllPlatforms = shouldSelectAll,
                selectedPlatforms = if (shouldSelectAll) emptySet() else updatedPlatforms
            )
        }
    }

    fun createSession(onSuccess: (MatcherSessionEntity) -> Unit, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            val userId = currentUser.value?.id
            if (userId == null) {
                onError(Exception("Usuario no autenticado"))
                return@launch
            }

            _sessionCreationState.value = SessionCreationState.Loading
            
            val code = Base32CodeGenerator.generateCode()
            
            // Build filters map
            val filters = buildFiltersMap()
            
            createMatcherSession(code, userId, filters)
                .onSuccess { session ->
                    _sessionCreationState.value = SessionCreationState.Success(session)
                    _currentSession.value = session
                    // Subscribe to Realtime for session_members changes
                    startListeningToSessionMembers(session.id, userId)
                    onSuccess(session)
                }
                .onFailure { error ->
                    _sessionCreationState.value = SessionCreationState.Error(error)
                    onError(error)
                }
        }
    }

    private fun buildFiltersMap(): Map<String, Any> {
        val state = _uiState.value
        val filters = mutableMapOf<String, Any>()
        
        // Add film type
        filters["filmType"] = state.selectedFilmType.value
        
        // Add genres
        val genres = when (state.selectedFilmType) {
            FilmType.MOVIE -> state.selectedMovieGenres.toList()
            FilmType.SERIES -> state.selectedSeriesGenres.toList()
        }
        filters["genres"] = genres
        
        // Add platforms
        if (state.selectAllPlatforms) {
            filters["platforms"] = "all"
        } else {
            filters["platforms"] = state.selectedPlatforms.toList()
        }
        
        return filters
    }

    fun cancelSession(sessionId: String, onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            cancelMatcherSession(sessionId)
                .onSuccess {
                    onSuccess()
                }
                .onFailure { error ->
                    onError(error)
                }
        }
    }

    fun joinSession(code: String, onSuccess: (MatcherSessionEntity) -> Unit, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            val userId = currentUser.value?.id
            if (userId == null) {
                onError(Exception("Usuario no autenticado"))
                return@launch
            }

            _sessionJoinState.value = SessionJoinState.Loading
            
            joinMatcherSession(code, userId)
                .onSuccess { session ->
                    _sessionJoinState.value = SessionJoinState.Success(session)
                    _currentSession.value = session
                    // Subscribe to session status changes (for non-host users)
                    startListeningToSessionStatus(session.id)
                    onSuccess(session)
                }
                .onFailure { error ->
                    _sessionJoinState.value = SessionJoinState.Error(error)
                    onError(error)
                }
        }
    }
    
    private fun startListeningToSessionMembers(sessionId: String, currentUserId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            subscribeToSessionMembers(sessionId, currentUserId)
                .onEach { joinedSessionId ->
                    if (joinedSessionId == sessionId) {
                        _hasOtherUserJoined.value = true
                    }
                }
                .launchIn(viewModelScope)
        }
    }
    
    private fun startListeningToSessionStatus(sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            subscribeToSessionStatus(sessionId)
                .onEach { activeSessionId ->
                    if (activeSessionId == sessionId) {
                        _sessionBecameActive.value = true
                    }
                }
                .launchIn(viewModelScope)
        }
    }
    
    fun initiateSession(sessionId: String, onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            initiateMatcherSession(sessionId)
                .onSuccess {
                    // Session status changed to active, set the flag
                    _sessionBecameActive.value = true
                    onSuccess()
                }
                .onFailure { error ->
                    onError(error)
                }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        _currentSession.value?.let { session ->
            viewModelScope.launch(Dispatchers.IO) {
                unsubscribeFromSessionMembers(session.id)
                unsubscribeFromSessionStatus(session.id)
            }
        }
    }
}

sealed class SessionCreationState {
    data object Idle : SessionCreationState()
    data object Loading : SessionCreationState()
    data class Success(val session: MatcherSessionEntity) : SessionCreationState()
    data class Error(val error: Throwable) : SessionCreationState()
}

sealed class SessionJoinState {
    data object Idle : SessionJoinState()
    data object Loading : SessionJoinState()
    data class Success(val session: MatcherSessionEntity) : SessionJoinState()
    data class Error(val error: Throwable) : SessionJoinState()
}

data class MatcherUiState(
    val code: String = "",
    val joinRoomEnabled: Boolean = false,
    val selectedFilmType: FilmType = FilmType.MOVIE,
    val selectedMovieGenres: Set<Int> = emptySet(),
    val selectedSeriesGenres: Set<Int> = emptySet(),
    val selectAllPlatforms: Boolean = true,
    val selectedPlatforms: Set<Int> = emptySet(),
)