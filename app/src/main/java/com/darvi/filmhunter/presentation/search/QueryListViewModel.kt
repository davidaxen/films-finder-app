package com.darvi.filmhunter.presentation.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darvi.filmhunter.domain.entity.MovieEntity
import com.darvi.filmhunter.domain.entity.SeriesEntity
import com.darvi.filmhunter.domain.usecase.search.SearchMoviesByTitle
import com.darvi.filmhunter.domain.usecase.search.SearchSeriesByTitle
import com.darvi.filmhunter.presentation.list.model.FilmType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QueryListViewModel @Inject constructor(
    private val searchMoviesByTitle: SearchMoviesByTitle,
    private val searchSeriesByTitle: SearchSeriesByTitle
) : ViewModel() {

    private val _uiState = MutableStateFlow(QueryListUiState())
    val uiState: StateFlow<QueryListUiState> = _uiState

    fun onScreenParamsLoad(
        q: String,
        filmType: Int
    ) {
        if (q.isNotEmpty()) {
            val type = if (filmType == FilmType.SERIES.value) FilmType.SERIES else FilmType.MOVIE
            _uiState.update {
                it.copy(
                    searchQuery = q,
                    isSearching = true,
                    filmTypeSelected = type
                )
            }
            searchFilms()
        }
    }

    fun searchFilms() {
        viewModelScope.launch(Dispatchers.IO) {
            when(_uiState.value.filmTypeSelected) {
                FilmType.MOVIE -> {
                    _uiState.update {
                        it.copy(
                            moviesFound = searchMoviesByTitle(it.searchQuery),
                            isSearching = false
                        )
                    }
                }
                FilmType.SERIES -> {
                    _uiState.update {
                        it.copy(
                            seriesFound = searchSeriesByTitle(it.searchQuery),
                            isSearching = false
                        )
                    }
                }
            }
        }
    }
}

data class QueryListUiState(
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val moviesFound: List<MovieEntity> = emptyList(),
    val seriesFound: List<SeriesEntity> = emptyList(),
    val filmTypeSelected: FilmType = FilmType.MOVIE,
)