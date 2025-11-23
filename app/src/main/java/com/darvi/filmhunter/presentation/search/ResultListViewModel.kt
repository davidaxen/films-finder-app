package com.darvi.filmhunter.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darvi.filmhunter.domain.usecase.movie.SearchMoviesByTitle
import com.darvi.filmhunter.domain.usecase.series.SearchSeriesByTitle
import com.darvi.filmhunter.presentation.core.model.FilmType
import com.darvi.filmhunter.presentation.core.model.FilmUiModel
import com.darvi.filmhunter.presentation.core.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultListViewModel @Inject constructor(
    private val searchMoviesByTitle: SearchMoviesByTitle,
    private val searchSeriesByTitle: SearchSeriesByTitle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResultListUiState())
    val uiState: StateFlow<ResultListUiState> = _uiState

    fun onQueryParamsLoad(
        q: String,
        filmType: Int
    ) {
        if (q.isNotEmpty()) {
            val type = if (filmType == FilmType.SERIES.value) FilmType.SERIES else FilmType.MOVIE
            _uiState.update {
                it.copy(
                    searchQuery = q,
                    isSearching = true,
                    isLoadingMore = false,
                    endReached = false,
                    currentPage = 1,
                    filmTypeSelected = type,
                    filmsFound = emptyList()
                )
            }
            loadPage(page = 1, isFirstPage = true)
        }
    }

    fun loadNextPage() {
        if (_uiState.value.isLoadingMore || _uiState.value.endReached || _uiState.value.isSearching) return
        loadPage(page = _uiState.value.currentPage, isFirstPage = false)
    }

    private fun loadPage(page: Int, isFirstPage: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isFirstPage) {
                _uiState.update { it.copy(isSearching = true) }
            } else {
                _uiState.update { it.copy(isLoadingMore = true) }
            }

            when (_uiState.value.filmTypeSelected) {
                FilmType.MOVIE -> {
                    val newMovies = searchMoviesByTitle(_uiState.value.searchQuery, page).map { it.toUiModel() }

                    _uiState.update { state ->
                        val allMovies =
                            if (isFirstPage) newMovies else state.filmsFound + newMovies

                        state.copy(
                            filmsFound = allMovies,
                            isSearching = false,
                            isLoadingMore = false,
                            endReached = newMovies.isEmpty(),
                            currentPage = if (newMovies.isNotEmpty()) page + 1 else state.currentPage
                        )
                    }
                }

                FilmType.SERIES -> {
                    val newSeries = searchSeriesByTitle(_uiState.value.searchQuery, page).map { it.toUiModel() }

                    _uiState.update { state ->
                        val allSeries =
                            if (isFirstPage) newSeries else state.filmsFound + newSeries

                        state.copy(
                            filmsFound = allSeries,
                            isSearching = false,
                            isLoadingMore = false,
                            endReached = newSeries.isEmpty(),
                            currentPage = if (newSeries.isNotEmpty()) page + 1 else state.currentPage
                        )
                    }
                }
            }
        }
    }
}

data class ResultListUiState(
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val isLoadingMore: Boolean = false,
    val endReached: Boolean = false,
    val currentPage: Int = 1,
    val filmsFound: List<FilmUiModel> = emptyList(),
    val filmTypeSelected: FilmType = FilmType.MOVIE,
)