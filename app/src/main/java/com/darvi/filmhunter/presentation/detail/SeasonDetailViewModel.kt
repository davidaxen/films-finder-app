package com.darvi.filmhunter.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darvi.filmhunter.domain.entity.series.SeasonDetailEntity
import com.darvi.filmhunter.domain.usecase.series.GetSeasonDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeasonDetailViewModel @Inject constructor(
    private val getSeasonDetail: GetSeasonDetail,
): ViewModel() {
    private val _uiState = MutableStateFlow(SeasonDetailUiState())
    val uiState: StateFlow<SeasonDetailUiState> = _uiState

    fun getDetail(seriesId: Int, seasonNumber: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(isLoading = true)
            }
            try {
                _uiState.update {
                    it.copy(
                        season = getSeasonDetail(seriesId, seasonNumber),
                        isLoading = false
                    )
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

}

data class SeasonDetailUiState(
    val season: SeasonDetailEntity = SeasonDetailEntity.empty(),
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
    val errorMessage: String = ""
)