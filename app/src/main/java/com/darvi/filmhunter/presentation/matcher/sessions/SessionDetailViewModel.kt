package com.darvi.filmhunter.presentation.matcher.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darvi.filmhunter.domain.entity.MatcherSessionEntity
import com.darvi.filmhunter.domain.usecase.matcher.GetMatchedFilms
import com.darvi.filmhunter.domain.usecase.matcher.GetSessionById
import com.darvi.filmhunter.domain.usecase.movie.GetMovieById
import com.darvi.filmhunter.domain.usecase.series.GetSeriesById
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionDetailViewModel @Inject constructor(
    private val getMatchedFilms: GetMatchedFilms,
    private val getSessionById: GetSessionById,
    private val getMovieById: GetMovieById,
    private val getSeriesById: GetSeriesById
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SessionDetailUiState())
    val uiState: StateFlow<SessionDetailUiState> = _uiState
    
    fun loadSessionDetail(session: MatcherSessionEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                hasError = false
            )
            
            // Load full session details if session is incomplete (only has ID)
            val fullSession = if (session.code.isEmpty() && session.filters.isEmpty()) {
                getSessionById(session.id).getOrElse { session }
            } else {
                session
            }
            
            _uiState.value = _uiState.value.copy(session = fullSession)
            
            // Load matched films
            getMatchedFilms(fullSession.id)
                .onSuccess { matchedFilmKeys ->
                    // Load details for each matched film
                    val matchedFilms = mutableListOf<Any>() // MovieDetailEntity or SeriesDetailEntity
                    
                    matchedFilmKeys.forEach { (tmdbId, mediaType) ->
                        when (mediaType) {
                            "movie" -> {
                                matchedFilms.add(getMovieById(tmdbId.toInt()))
                            }
                            "tv" -> {
                                matchedFilms.add(getSeriesById(tmdbId.toInt()))
                            }
                        }
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        matchedFilms = matchedFilms,
                        isLoading = false,
                        hasError = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        hasError = true,
                        errorMessage = error.message ?: "Error al cargar películas coincidentes"
                    )
                }
        }
    }
}

data class SessionDetailUiState(
    val session: MatcherSessionEntity? = null,
    val matchedFilms: List<Any> = emptyList(), // List of MovieDetailEntity or SeriesDetailEntity
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null
)

