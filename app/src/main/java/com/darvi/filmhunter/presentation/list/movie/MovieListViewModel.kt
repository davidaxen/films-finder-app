package com.darvi.filmhunter.presentation.list.movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darvi.filmhunter.domain.usecase.GetFilmList
import com.darvi.filmhunter.presentation.list.model.FilmUiModel
import com.darvi.filmhunter.presentation.list.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val getList: GetFilmList
): ViewModel() {
    private val _uiState = MutableStateFlow(ListUiState())
    val uiState: StateFlow<ListUiState> = _uiState

    init {
        getFilmLists()
    }

    private fun getFilmLists() {
        getFilmList(ListSection.POPULAR)
        getFilmList(ListSection.UPCOMING)
        getFilmList(ListSection.TOP_RATED)
        getFilmList(ListSection.NOW_PLAYING)
    }

    private fun getFilmList(listType: ListSection) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = getList(listType.path).map { it.toUiModel() }
            when (listType) {
                ListSection.POPULAR -> _uiState.update { it.copy(popularMovies = list) }
                ListSection.TOP_RATED -> _uiState.update { it.copy(topRatedMovies = list) }
                ListSection.NOW_PLAYING -> _uiState.update { it.copy(nowPlayingMovies = list) }
                ListSection.UPCOMING -> _uiState.update { it.copy(upcomingMovies = list) }
            }
        }
    }
}

enum class ListSection(val path: String) {
    POPULAR("popular"),
    TOP_RATED("top_rated"),
    NOW_PLAYING("now_playing"),
    UPCOMING("upcoming")
}

data class ListUiState(
    val popularMovies: List<FilmUiModel> = emptyList(),
    val upcomingMovies: List<FilmUiModel> = emptyList(),
    val nowPlayingMovies: List<FilmUiModel> = emptyList(),
    val topRatedMovies: List<FilmUiModel> = emptyList()
)