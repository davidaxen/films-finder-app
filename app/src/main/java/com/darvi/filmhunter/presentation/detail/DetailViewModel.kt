package com.darvi.filmhunter.presentation.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darvi.filmhunter.domain.usecase.movie.GetMovieById
import com.darvi.filmhunter.domain.usecase.movie.SaveMovie
import com.darvi.filmhunter.domain.usecase.series.GetSeriesById
import com.darvi.filmhunter.domain.usecase.series.SaveSeries
import com.darvi.filmhunter.presentation.core.model.FilmType
import com.darvi.filmhunter.presentation.detail.model.FilmDetailUiModel
import com.darvi.filmhunter.presentation.detail.model.toUiModel
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
    private val saveSeries: SaveSeries,
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
        _uiState.update {
            it.copy(
                film = it.film.copy(isSaved = !it.film.isSaved)
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val filmId = _uiState.value.film.id
                when(_uiState.value.film.type) {
                    FilmType.MOVIE -> saveMovie(filmId)
                    FilmType.SERIES -> saveSeries(filmId)
                }
            } catch (e: Exception) {
                Log.i("DETAILVIEWMODEL SAVE FILM", e.toString())
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