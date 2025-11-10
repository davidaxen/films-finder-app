package com.darvi.filmhunter.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darvi.filmhunter.domain.repository.MovieRepository
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
class ListViewModel @Inject constructor(private val repository: MovieRepository): ViewModel() {
    private val _uiState = MutableStateFlow(ListUiState())
    val uiState: StateFlow<ListUiState> = _uiState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { state ->
                state.copy(
                    movies = repository.getPopularMovies().map { it.toUiModel() },
                    isLoading = false
                )
            }
        }
    }
}

data class ListUiState(
    val movies: List<FilmUiModel> = emptyList(),
    val isLoading: Boolean = true
)