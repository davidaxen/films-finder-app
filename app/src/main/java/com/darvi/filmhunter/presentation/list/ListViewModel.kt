package com.darvi.filmhunter.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darvi.filmhunter.domain.entity.MovieEntity
import com.darvi.filmhunter.domain.repository.MovieRepository
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
            _uiState.update {
                it.copy(
                    movies = repository.getPopularMovies()
                )
            }
        }
    }
}

data class ListUiState(
    val movies: List<MovieEntity> = emptyList()
)