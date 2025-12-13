package com.darvi.filmhunter.presentation.matcher

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.darvi.filmhunter.presentation.core.model.FilmType
import javax.inject.Inject

@HiltViewModel
class MatcherViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(MatcherUiState())
    val uiState: StateFlow<MatcherUiState> = _uiState

    fun onCodeChanged(code: String) {
        if (code.length > 4) return
        _uiState.update {
            it.copy(
                code = code,
                joinRoomEnabled = code.length == 4
            )
        }
    }

    fun onFilmTypeSelected(filmType: FilmType) {
        _uiState.update {
            it.copy(selectedFilmType = filmType)
        }
    }
}

data class MatcherUiState(
    val code: String = "",
    val joinRoomEnabled: Boolean = false,
    val selectedFilmType: FilmType? = null,
)