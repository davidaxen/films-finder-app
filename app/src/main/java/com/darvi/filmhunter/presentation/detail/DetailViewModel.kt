package com.darvi.filmhunter.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darvi.filmhunter.domain.usecase.detail.GetMovieById
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
    private val getMovieById: GetMovieById
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState

    fun getDetail(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(isLoading = true)
            }
            try {
                val film = getMovieById(id)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        film = film.toUiModel()
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        hasError = true,
                        errorMessage = "Ha ocurrido un error"
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