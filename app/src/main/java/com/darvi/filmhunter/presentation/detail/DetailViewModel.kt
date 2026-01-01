package com.darvi.filmhunter.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darvi.filmhunter.domain.usecase.movie.GetMovieById
import com.darvi.filmhunter.domain.usecase.movie.RemoveSavedMovie
import com.darvi.filmhunter.domain.usecase.movie.SaveMovie
import com.darvi.filmhunter.domain.usecase.series.GetSeriesById
import com.darvi.filmhunter.domain.usecase.series.RemoveSavedSeries
import com.darvi.filmhunter.domain.usecase.series.SaveSeries
import com.darvi.filmhunter.presentation.core.model.FilmType
import com.darvi.filmhunter.presentation.core.model.FilmDetailUiModel
import com.darvi.filmhunter.presentation.core.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getMovieById: GetMovieById,
    private val getSeriesById: GetSeriesById,
    private val saveMovie: SaveMovie,
    private val removeSavedMovie: RemoveSavedMovie,
    private val saveSeries: SaveSeries,
    private val removeSavedSeries: RemoveSavedSeries,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState

    fun getDetail(
        id: Int,
        filmType: Int
    ) {
        _uiState.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val type = if (filmType == FilmType.SERIES.value) FilmType.SERIES else FilmType.MOVIE
                if (type == FilmType.SERIES) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            film = getSeriesById(id).toUiModel()
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            film = getMovieById(id).toUiModel()
                        )
                    }
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        hasError = true,
                        errorMessage = "Ha ocurrido un error, ${e.toString()}"
                    )
                }
            }
        }
    }

    fun onSaveFilm() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.update {
                    it.copy(
                        film = it.film.copy(isSaved = !it.film.isSaved)
                    )
                }
                val filmId = _uiState.value.film.id
                if (_uiState.value.film.isSaved) {
                    when(_uiState.value.film.type) {
                        FilmType.MOVIE -> saveMovie(filmId)
                        FilmType.SERIES -> saveSeries(filmId)
                    }
                } else {
                    when(_uiState.value.film.type) {
                        FilmType.MOVIE -> removeSavedMovie(filmId)
                        FilmType.SERIES -> removeSavedSeries(filmId)
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        film = it.film.copy(isSaved = !it.film.isSaved),
                        hasError = true,
                        errorMessage = "Error al guardar en favoritos ${e.toString()}"
                    )
                }
            }
        }
    }

}

data class DetailUiState(
    val film: FilmDetailUiModel = FilmDetailUiModel.empty(),
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
    val errorMessage: String = ""
)