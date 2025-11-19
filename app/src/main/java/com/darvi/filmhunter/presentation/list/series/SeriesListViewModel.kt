package com.darvi.filmhunter.presentation.list.series

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darvi.filmhunter.domain.usecase.series.GetSeriesList
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
class SeriesListViewModel @Inject constructor(
    private val getList: GetSeriesList
): ViewModel() {
    private val _uiState = MutableStateFlow(SeriesListUiState())
    val uiState: StateFlow<SeriesListUiState> = _uiState

    init {
        getSeriesLists()
    }

    private fun getSeriesLists() {
        getSeriesList(SeriesListSection.POPULAR)
        getSeriesList(SeriesListSection.AIRING_TODAY)
        getSeriesList(SeriesListSection.TOP_RATED)
        getSeriesList(SeriesListSection.ON_THE_AIR)
    }

    private fun getSeriesList(listType: SeriesListSection) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = getList(listType.path).map { it.toUiModel() }
            when (listType) {
                SeriesListSection.POPULAR -> _uiState.update { it.copy(popularSeries = list) }
                SeriesListSection.TOP_RATED -> _uiState.update { it.copy(topRatedSeries = list) }
                SeriesListSection.AIRING_TODAY -> _uiState.update { it.copy(airingTodaySeries = list) }
                SeriesListSection.ON_THE_AIR -> _uiState.update { it.copy(onTheAirSeries = list) }
            }
        }
    }
}

enum class SeriesListSection(val path: String) {
    POPULAR("popular"),
    TOP_RATED("top_rated"),
    AIRING_TODAY("airing_today"),
    ON_THE_AIR("on_the_air")
}

data class SeriesListUiState(
    val popularSeries: List<FilmUiModel> = emptyList(),
    val airingTodaySeries: List<FilmUiModel> = emptyList(),
    val onTheAirSeries: List<FilmUiModel> = emptyList(),
    val topRatedSeries: List<FilmUiModel> = emptyList()
)