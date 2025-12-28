package com.darvi.filmhunter.presentation.matcher.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darvi.filmhunter.domain.entity.UserEntity
import com.darvi.filmhunter.domain.entity.movie.MovieDetailEntity
import com.darvi.filmhunter.domain.entity.series.SeriesDetailEntity
import com.darvi.filmhunter.domain.usecase.auth.GetCurrentUser
import com.darvi.filmhunter.domain.usecase.matcher.GetSessionMembers
import com.darvi.filmhunter.domain.usecase.matcher.GetSessionSwipes
import com.darvi.filmhunter.domain.usecase.matcher.GetSessionTitles
import com.darvi.filmhunter.domain.usecase.matcher.SaveSessionSwipe
import com.darvi.filmhunter.domain.usecase.matcher.SubscribeToSessionSwipes
import com.darvi.filmhunter.domain.usecase.matcher.UnsubscribeFromSessionSwipes
import com.darvi.filmhunter.domain.usecase.movie.GetMovieById
import com.darvi.filmhunter.domain.usecase.series.GetSeriesById
import com.darvi.filmhunter.presentation.core.util.ImageUrlHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SwipingViewModel @Inject constructor(
    private val getSessionTitles: GetSessionTitles,
    private val getMovieById: GetMovieById,
    private val getSeriesById: GetSeriesById,
    private val saveSessionSwipe: SaveSessionSwipe,
    private val getSessionMembers: GetSessionMembers,
    private val getSessionSwipes: GetSessionSwipes,
    private val subscribeToSessionSwipes: SubscribeToSessionSwipes,
    private val unsubscribeFromSessionSwipes: UnsubscribeFromSessionSwipes,
    getCurrentUser: GetCurrentUser
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SwipingUiState())
    val uiState: StateFlow<SwipingUiState> = _uiState
    
    // Preloaded films queue (next 8 films with their details)
    private val preloadedFilms = mutableListOf<PreloadedFilm>()
    
    // Track swipes by both users for match detection
    private val userSwipes = mutableMapOf<String, MutableSet<Pair<Long, String>>>() // userId -> Set<(tmdbId, mediaType)>
    // Track matches that have already been shown to prevent duplicates
    private val shownMatches = mutableSetOf<Pair<Long, String>>()
    // Track if titles have been loaded to prevent reloading when navigating back
    private var titlesLoaded = false
    
    val currentUser: StateFlow<UserEntity?> = getCurrentUser() as StateFlow<UserEntity?>
    
    data class PreloadedFilm(
        val title: Pair<Long, String>,
        val film: Any, // MovieDetailEntity or SeriesDetailEntity
        val posterUrl: String?
    )
    
    fun loadSessionTitles(sessionId: String) {
        // Don't reload if titles are already loaded
        if (titlesLoaded && _uiState.value.currentTitle != null) {
            return
        }
        
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            // Get session members to know both user IDs
            getSessionMembers(sessionId)
                .onSuccess { memberIds ->
                    // Initialize swipe tracking for all members
                    memberIds.forEach { userId ->
                        userSwipes[userId] = mutableSetOf()
                    }
                    
                    // Load existing swipes from database
                    getSessionSwipes(sessionId)
                        .onSuccess { existingSwipes ->
                            // Track existing "like" swipes
                            existingSwipes.forEach { swipe ->
                                if (swipe.vote == "like") {
                                    val userId = swipe.userId
                                    val filmKey = Pair(swipe.tmdbId, swipe.mediaType)
                                    if (userSwipes[userId] == null) {
                                        userSwipes[userId] = mutableSetOf()
                                    }
                                    userSwipes[userId]?.add(filmKey)
                                }
                            }
                        }
                        .onFailure {
                            // Continue even if loading swipes fails
                        }
                    
                    // Start listening to swipes
                    startListeningToSwipes(sessionId)
                }
                .onFailure {
                    // Continue even if getting members fails
                }
            
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
                        titlesLoaded = true
                        // Preload next 8 films
                        preloadNextFilms(titles.drop(1).take(8), sessionId)
                        // Update preloaded poster URLs after preloading starts
                        _uiState.value = _uiState.value.copy(
                            preloadedPosterUrls = preloadedFilms.take(8).mapNotNull { it.posterUrl }
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
    
    private fun startListeningToSwipes(sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            subscribeToSessionSwipes(sessionId)
                .onEach { swipe ->
                    // Only track "like" votes for match detection
                    if (swipe.vote == "like") {
                        val userId = swipe.userId
                        val filmKey = Pair(swipe.tmdbId, swipe.mediaType)
                        
                        // Ensure userSwipes has an entry for this user
                        if (userSwipes[userId] == null) {
                            userSwipes[userId] = mutableSetOf()
                        }
                        
                        // Add like to user's set
                        userSwipes[userId]?.add(filmKey)
                        
                        // Check for match
                        checkForMatch(sessionId, filmKey)
                    }
                }
                .catch { e ->
                    // Handle error silently
                }
                .launchIn(viewModelScope)
        }
    }
    
    private suspend fun checkForMatch(sessionId: String, filmKey: Pair<Long, String>) {
        // Skip if we've already shown this match
        if (shownMatches.contains(filmKey)) return
        
        // Get all user IDs in the session
        val allUserIds = userSwipes.keys.toList()
        if (allUserIds.size < 2) return // Need at least 2 users
        
        // Check if all users have liked this film
        val allLiked = allUserIds.all { userId ->
            userSwipes[userId]?.contains(filmKey) == true
        }
        
        if (allLiked) {
            // Mark this match as shown
            shownMatches.add(filmKey)
            
            // Load film details for the match
            val film: Any = try {
                if (filmKey.second == "movie") {
                    getMovieById(filmKey.first.toInt())
                } else {
                    getSeriesById(filmKey.first.toInt())
                }
            } catch (e: Exception) {
                return // If we can't load the film, skip showing the match
            }
            
            // Update UI state with match (on main thread)
            withContext(Dispatchers.Main) {
                _uiState.value = _uiState.value.copy(
                    match = MatchInfo(
                        film = film,
                        tmdbId = filmKey.first,
                        mediaType = filmKey.second
                    )
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
                preloadedPosterUrls = preloadedFilms.take(8).mapNotNull { it.posterUrl }
            )
            
            // Preload next films if needed
            val remaining = _uiState.value.remainingTitles
            if (remaining.isNotEmpty() && preloadedFilms.size < 8) {
                val titlesToPreload = remaining.take(8 - preloadedFilms.size)
                preloadNextFilms(titlesToPreload, sessionId)
            } else {
                // Update preloaded poster URLs
                _uiState.value = _uiState.value.copy(
                    preloadedPosterUrls = preloadedFilms.take(8).mapNotNull { it.posterUrl }
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
                        preloadedPosterUrls = preloadedFilms.take(8).mapNotNull { it.posterUrl }
                    )
                } catch (e: Exception) {
                    // Silently fail preloading - we'll load it when needed
                }
            }
        }
    }
    
    fun onSwipe(vote: String) {
        val sessionId = _uiState.value.sessionId ?: return
        val userId = currentUser.value?.id ?: return
        val currentTitle = _uiState.value.currentTitle ?: return

        viewModelScope.launch(Dispatchers.IO) {
            saveSessionSwipe(sessionId, userId, currentTitle.first, currentTitle.second, vote)

            // Immediately track the current user's swipe if it's a "like" and check for match
            if (vote == "like") {
                val filmKey = Pair(currentTitle.first, currentTitle.second)
                // Ensure userSwipes has an entry for this user
                if (userSwipes[userId] == null) {
                    userSwipes[userId] = mutableSetOf()
                }
                userSwipes[userId]?.add(filmKey)
                
                // Check for match immediately
                checkForMatch(sessionId, filmKey)
            }

            val remaining = _uiState.value.remainingTitles
            if (remaining.isEmpty()) {
                _uiState.value = _uiState.value.copy(currentFilm = null, currentTitle = null, isEmpty = true)
                return@launch
            }

            val nextTitle = remaining.first()
            // 1) "consumimos" el siguiente en el estado YA
            _uiState.value = _uiState.value.copy(
                currentTitle = nextTitle,
                remainingTitles = remaining.drop(1)
            )
            // 2) ahora cargamos el film (usará remainingTitles correcto)
            loadTitleDetails(nextTitle, sessionId)
        }
    }
    
    fun setSessionId(sessionId: String) {
        _uiState.value = _uiState.value.copy(sessionId = sessionId)
    }
    
    fun dismissMatch() {
        _uiState.value = _uiState.value.copy(match = null)
    }
    
    override fun onCleared() {
        super.onCleared()
        val sessionId = _uiState.value.sessionId
        if (sessionId != null) {
            viewModelScope.launch(Dispatchers.IO) {
                unsubscribeFromSessionSwipes(sessionId)
            }
        }
    }
}

data class SwipingUiState(
    val sessionId: String? = null,
    val currentFilm: Any? = null, // MovieDetailEntity or SeriesDetailEntity
    val currentTitle: Pair<Long, String>? = null,
    val remainingTitles: List<Pair<Long, String>> = emptyList(),
    val preloadedPosterUrls: List<String> = emptyList(), // Poster URLs of next 8 preloaded films
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null,
    val isEmpty: Boolean = false,
    val match: MatchInfo? = null
)

data class MatchInfo(
    val film: Any, // MovieDetailEntity or SeriesDetailEntity
    val tmdbId: Long,
    val mediaType: String
)

