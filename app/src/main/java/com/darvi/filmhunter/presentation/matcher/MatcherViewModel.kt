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

    fun onGenreToggled(genreId: Int) {
        _uiState.update { state ->
            when (state.selectedFilmType) {
                FilmType.MOVIE -> {
                    val updatedGenres = if (state.selectedMovieGenres.contains(genreId)) {
                        state.selectedMovieGenres - genreId
                    } else {
                        state.selectedMovieGenres + genreId
                    }
                    state.copy(selectedMovieGenres = updatedGenres)
                }
                FilmType.SERIES -> {
                    val updatedGenres = if (state.selectedSeriesGenres.contains(genreId)) {
                        state.selectedSeriesGenres - genreId
                    } else {
                        state.selectedSeriesGenres + genreId
                    }
                    state.copy(selectedSeriesGenres = updatedGenres)
                }
            }
        }
    }

    fun onSelectAllPlatforms() {
        _uiState.update {
            it.copy(
                selectAllPlatforms = true,
                selectedPlatforms = emptySet()
            )
        }
    }

    fun onPlatformToggled(platformId: Int) {
        _uiState.update { state ->
            // If "all" is currently selected, deselect it and select only this platform
            if (state.selectAllPlatforms) {
                return@update state.copy(
                    selectAllPlatforms = false,
                    selectedPlatforms = setOf(platformId)
                )
            }

            val isCurrentlySelected = state.selectedPlatforms.contains(platformId)
            val updatedPlatforms = if (isCurrentlySelected) {
                state.selectedPlatforms - platformId
            } else {
                state.selectedPlatforms + platformId
            }

            // If user selects all 3 platforms, automatically select "all" and clear individual selections
            val allPlatformIds = setOf(8, 337, 1899) // Netflix, Disney+, HBO Max
            val shouldSelectAll = updatedPlatforms.size == allPlatformIds.size && 
                                  updatedPlatforms.containsAll(allPlatformIds)

            state.copy(
                selectAllPlatforms = shouldSelectAll,
                selectedPlatforms = if (shouldSelectAll) emptySet() else updatedPlatforms
            )
        }
    }
}

data class MatcherUiState(
    val code: String = "",
    val joinRoomEnabled: Boolean = false,
    val selectedFilmType: FilmType = FilmType.MOVIE,
    val selectedMovieGenres: Set<Int> = emptySet(),
    val selectedSeriesGenres: Set<Int> = emptySet(),
    val selectAllPlatforms: Boolean = true,
    val selectedPlatforms: Set<Int> = emptySet(),
)