package com.darvi.filmhunter.presentation.list.movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darvi.filmhunter.domain.usecase.movie.GetMoviesList
import com.darvi.filmhunter.presentation.core.model.FilmUiModel
import com.darvi.filmhunter.presentation.core.model.MovieListSection
import com.darvi.filmhunter.presentation.core.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val getList: GetMoviesList
): ViewModel() {
    private val _uiState = MutableStateFlow(MovieListUiState())
    val uiState: StateFlow<MovieListUiState> = _uiState

    init {
        getMoviesLists()
    }

    private fun getMoviesLists() {
        getMoviesList(MovieListSection.POPULAR)
        getMoviesList(MovieListSection.TOP_RATED)
        getMoviesList(MovieListSection.NOW_PLAYING)
//        getMoviesList(MovieListSection.UPCOMING)
    }

    private fun getMoviesList(listType: MovieListSection) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = getList(listType.path).map { it.toUiModel() }
            when (listType) {
                MovieListSection.POPULAR -> _uiState.update { it.copy(popularMovies = list) }
                MovieListSection.TOP_RATED -> _uiState.update { it.copy(topRatedMovies = list) }
                MovieListSection.NOW_PLAYING -> _uiState.update { it.copy(nowPlayingMovies = list) }
                MovieListSection.UPCOMING -> _uiState.update { it.copy(upcomingMovies = list) }
            }
        }
    }
}

data class MovieListUiState(
    val popularMovies: List<FilmUiModel> = emptyList(),
    val upcomingMovies: List<FilmUiModel> = emptyList(),
    val nowPlayingMovies: List<FilmUiModel> = emptyList(),
    val topRatedMovies: List<FilmUiModel> = emptyList()
)