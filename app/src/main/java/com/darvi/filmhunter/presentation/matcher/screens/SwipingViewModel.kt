package com.darvi.filmhunter.presentation.matcher.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darvi.filmhunter.domain.entity.UserEntity
import com.darvi.filmhunter.domain.usecase.auth.GetCurrentUser
import com.darvi.filmhunter.domain.usecase.matcher.GetSessionTitles
import com.darvi.filmhunter.domain.usecase.matcher.SaveSessionSwipe
import com.darvi.filmhunter.domain.usecase.movie.GetMovieById
import com.darvi.filmhunter.domain.usecase.series.GetSeriesById
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SwipingViewModel @Inject constructor(
    private val getSessionTitles: GetSessionTitles,
    private val getMovieById: GetMovieById,
    private val getSeriesById: GetSeriesById,
    private val saveSessionSwipe: SaveSessionSwipe,
    getCurrentUser: GetCurrentUser
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SwipingUiState())
    val uiState: StateFlow<SwipingUiState> = _uiState
    
    val currentUser: StateFlow<UserEntity?> = getCurrentUser() as StateFlow<UserEntity?>
    
    fun loadSessionTitles(sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            getSessionTitles(sessionId)
                .onSuccess { titles ->
                    if (titles.isNotEmpty()) {
                        // Load first title details
                        loadTitleDetails(titles[0], sessionId)
                        // Store remaining titles
                        _uiState.value = _uiState.value.copy(
                            remainingTitles = titles.drop(1),
                            isLoading = false
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isEmpty = true
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        hasError = true,
                        errorMessage = error.message ?: "Error loading titles"
                    )
                }
        }
    }
    
    private suspend fun loadTitleDetails(title: Pair<Long, String>, sessionId: String) {
        try {
            val film: Any = if (title.second == "movie") {
                getMovieById(title.first.toInt())
            } else {
                getSeriesById(title.first.toInt())
            }
            
            _uiState.value = _uiState.value.copy(
                currentFilm = film,
                currentTitle = title
            )
        } catch (e: Exception) {
            // If loading fails, skip to next title
            _uiState.value = _uiState.value.copy(
                hasError = true,
                errorMessage = "Error loading film details"
            )
        }
    }
    
    fun onSwipe(vote: String) {
        val currentTitle = _uiState.value.currentTitle
        val sessionId = _uiState.value.sessionId
        val userId = currentUser.value?.id
        
        if (currentTitle != null && sessionId != null && userId != null) {
            viewModelScope.launch(Dispatchers.IO) {
                saveSessionSwipe(
                    sessionId = sessionId,
                    userId = userId,
                    tmdbId = currentTitle.first,
                    mediaType = currentTitle.second,
                    vote = vote
                )
            }
        }
        
        // Move to next title
        val remaining = _uiState.value.remainingTitles
        if (remaining.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                loadTitleDetails(remaining[0], sessionId ?: "")
                _uiState.value = _uiState.value.copy(
                    remainingTitles = remaining.drop(1),
                    currentTitle = remaining[0]
                )
            }
        } else {
            // No more titles
            _uiState.value = _uiState.value.copy(
                currentFilm = null,
                currentTitle = null,
                isEmpty = true
            )
        }
    }
    
    fun setSessionId(sessionId: String) {
        _uiState.value = _uiState.value.copy(sessionId = sessionId)
    }
}

data class SwipingUiState(
    val sessionId: String? = null,
    val currentFilm: Any? = null, // MovieDetailEntity or SeriesDetailEntity
    val currentTitle: Pair<Long, String>? = null,
    val remainingTitles: List<Pair<Long, String>> = emptyList(),
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null,
    val isEmpty: Boolean = false
)

