package com.darvi.filmhunter.presentation.matcher.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darvi.filmhunter.domain.entity.UserEntity
import com.darvi.filmhunter.domain.entity.movie.MovieDetailEntity
import com.darvi.filmhunter.domain.entity.series.SeriesDetailEntity
import com.darvi.filmhunter.domain.usecase.auth.GetCurrentUser
import com.darvi.filmhunter.domain.usecase.matcher.GetSessionTitles
import com.darvi.filmhunter.domain.usecase.matcher.SaveSessionSwipe
import com.darvi.filmhunter.domain.usecase.movie.GetMovieById
import com.darvi.filmhunter.domain.usecase.series.GetSeriesById
import com.darvi.filmhunter.presentation.core.util.ImageUrlHelper
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
    
    // Preloaded films queue (next 5 films with their details)
    private val preloadedFilms = mutableListOf<PreloadedFilm>()
    
    val currentUser: StateFlow<UserEntity?> = getCurrentUser() as StateFlow<UserEntity?>
    
    data class PreloadedFilm(
        val title: Pair<Long, String>,
        val film: Any, // MovieDetailEntity or SeriesDetailEntity
        val posterUrl: String?
    )
    
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
                        // Preload next 5 films
                        preloadNextFilms(titles.drop(1).take(5), sessionId)
                        // Update preloaded poster URLs after preloading starts
                        _uiState.value = _uiState.value.copy(
                            preloadedPosterUrls = preloadedFilms.take(5).mapNotNull { it.posterUrl }
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
            // Check if film is already preloaded
            val preloaded = preloadedFilms.firstOrNull { it.title == title }
            val film: Any = if (preloaded != null) {
                // Use preloaded film
                preloadedFilms.remove(preloaded)
                preloaded.film
            } else {
                // Load from API
                if (title.second == "movie") {
                    getMovieById(title.first.toInt())
                } else {
                    getSeriesById(title.first.toInt())
                }
            }
            
            _uiState.value = _uiState.value.copy(
                currentFilm = film,
                currentTitle = title,
                preloadedPosterUrls = preloadedFilms.take(5).mapNotNull { it.posterUrl }
            )
            
            // Preload next films if needed
            val remaining = _uiState.value.remainingTitles
            if (remaining.isNotEmpty() && preloadedFilms.size < 5) {
                val titlesToPreload = remaining.take(5 - preloadedFilms.size)
                preloadNextFilms(titlesToPreload, sessionId)
            } else {
                // Update preloaded poster URLs
                _uiState.value = _uiState.value.copy(
                    preloadedPosterUrls = preloadedFilms.take(5).mapNotNull { it.posterUrl }
                )
            }
        } catch (e: Exception) {
            // If loading fails, skip to next title
            _uiState.value = _uiState.value.copy(
                hasError = true,
                errorMessage = "Error loading film details"
            )
        }
    }
    
    private fun preloadNextFilms(titles: List<Pair<Long, String>>, sessionId: String) {
        if (titles.isEmpty()) return
        
        viewModelScope.launch(Dispatchers.IO) {
            titles.forEach { title ->
                try {
                    // Skip if already preloaded
                    if (preloadedFilms.any { it.title == title }) {
                        return@forEach
                    }
                    
                    // Load film details
                    val film: Any = if (title.second == "movie") {
                        getMovieById(title.first.toInt())
                    } else {
                        getSeriesById(title.first.toInt())
                    }
                    
                    // Get poster URL
                    val posterPath = when (film) {
                        is MovieDetailEntity -> film.posterPath
                        is SeriesDetailEntity -> film.posterPath
                        else -> null
                    }
                    val posterUrl = posterPath?.let { ImageUrlHelper.getOriginalUrl(it) }
                    
                    // Store preloaded film
                    preloadedFilms.add(
                        PreloadedFilm(
                            title = title,
                            film = film,
                            posterUrl = posterUrl
                        )
                    )
                    
                    // Update UI state with preloaded poster URLs
                    _uiState.value = _uiState.value.copy(
                        preloadedPosterUrls = preloadedFilms.take(5).mapNotNull { it.posterUrl }
                    )
                } catch (e: Exception) {
                    // Silently fail preloading - we'll load it when needed
                }
            }
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
    val preloadedPosterUrls: List<String> = emptyList(), // Poster URLs of next 5 preloaded films
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null,
    val isEmpty: Boolean = false
)

