package com.darvi.filmhunter.presentation.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darvi.filmhunter.domain.usecase.FetchSavedFilms
import com.darvi.filmhunter.domain.usecase.GetSavedFilmsFlow
import com.darvi.filmhunter.presentation.core.model.FilmDetailUiModel
import com.darvi.filmhunter.presentation.core.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedListViewModel @Inject constructor(
    private val fetchSavedFilms: FetchSavedFilms,
    private val getSavedFilmsFlow: GetSavedFilmsFlow
) : ViewModel() {
    private val _uiState = MutableStateFlow(SavedListUiState())
    val uiState: StateFlow<SavedListUiState> = _uiState

    init {
        observeFilms()
    }

    private fun observeFilms() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(isLoading = true, hasError = false, errorMessage = "")
            }
            getSavedFilmsFlow()
                .onStart {
                    _uiState.update {
                        it.copy(
                            isLoading = true,
                            hasError = false,
                            errorMessage = ""
                        )
                    }
                }
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            hasError = true,
                            errorMessage = "Error al encontrar películas guardadas: ${e.message}"
                        )
                    }
                }
                .collect { (movies, series) ->
                    val uiMovies = movies.map { it.toUiModel() }
                    val uiSeries = series.map { it.toUiModel() }

                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            isRefreshing = false,
                            films = (uiMovies + uiSeries).sortedByDescending { it.savedAt }
                        )
                    }
                }
        }
    }

    fun refreshFilms() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    isRefreshing = true,
                    hasError = false,
                    errorMessage = ""
                )
            }
            executeFetch()
        }
    }

    private fun executeFetch() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                fetchSavedFilms()
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        hasError = false,
                        errorMessage = ""
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        hasError = true,
                        errorMessage = "Error al encontrar peliculas guardadas: ${e.message}"
                    )
                }
            }
        }
    }
}

data class SavedListUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String = "",
    val films: List<FilmDetailUiModel> = emptyList()
)