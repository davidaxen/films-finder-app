package com.darvi.filmhunter.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darvi.filmhunter.domain.entity.MovieEntity
import com.darvi.filmhunter.domain.entity.SeriesEntity
import com.darvi.filmhunter.domain.usecase.search.SearchMoviesByTitle
import com.darvi.filmhunter.domain.usecase.search.SearchSeriesByTitle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMoviesByTitle: SearchMoviesByTitle,
    private val searchSeriesByTitle: SearchSeriesByTitle
): ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    fun onQueryChange(q: String) {
        _uiState.update {
            it.copy(searchQuery = q)
        }
    }

    fun onSearch() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    moviesFound = searchMoviesByTitle(it.searchQuery),
                    seriesFound = searchSeriesByTitle(it.searchQuery)
                )
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
    val moviesFound: List<MovieEntity> = emptyList(),
    val seriesFound: List<SeriesEntity> = emptyList(),
)