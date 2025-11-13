package com.darvi.filmhunter.presentation.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darvi.filmhunter.domain.entity.MovieEntity
import com.darvi.filmhunter.domain.entity.SeriesEntity
import com.darvi.filmhunter.domain.usecase.search.SearchMoviesByTitle
import com.darvi.filmhunter.domain.usecase.search.SearchSeriesByTitle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMoviesByTitle: SearchMoviesByTitle,
    private val searchSeriesByTitle: SearchSeriesByTitle
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    private companion object {
        const val DEBOUNCE_MS = 600L
        const val MIN_CHARS = 2
    }

    fun onQueryChange(q: String) {
        _uiState.update {
            it.copy(searchQuery = q)
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    fun onSearch() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }
            _uiState
                .map { it.searchQuery }
                .distinctUntilChanged()
                .debounce(DEBOUNCE_MS)
                .mapLatest { q ->
                    if (q.length < MIN_CHARS) {
                        return@mapLatest SearchUiState(
                            searchQuery = q,
                            moviesFound = emptyList(),
                            seriesFound = emptyList(),
                            isLoading = false
                        )
                    }

                    val (movies, series) = coroutineScope {
                        val moviesDef = async(Dispatchers.IO) { searchMoviesByTitle(q) }
                        val seriesDef = async(Dispatchers.IO) { searchSeriesByTitle(q) }
                        moviesDef.await() to seriesDef.await()
                    }

                    _uiState.value.copy(
                        moviesFound = movies,
                        seriesFound = series,
                        isLoading = false
                    )
                }.catch { e ->
                    _uiState.update {
                        it.copy(
                            moviesFound = emptyList(),
                            seriesFound = emptyList(),
                            isLoading = false
                        )
                    }
                    Log.e("DEBOUNCE SEARCH VIEWMODEL", e.toString())
                }.collect { newState ->
                    _uiState.value = newState
                }
        }
    }

    fun onCancelQuerySearch() {
        _uiState.update {
            it.copy(searchQuery = "")
        }
    }
}

data class SearchUiState(
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val moviesFound: List<MovieEntity> = emptyList(),
    val seriesFound: List<SeriesEntity> = emptyList(),
)