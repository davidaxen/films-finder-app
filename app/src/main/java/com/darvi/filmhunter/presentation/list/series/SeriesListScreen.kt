package com.darvi.filmhunter.presentation.list.series

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darvi.filmhunter.presentation.core.model.FilmType
import com.darvi.filmhunter.presentation.list.components.FilmListSection

@Composable
fun SeriesListScreen(
    seriesListViewModel: SeriesListViewModel = hiltViewModel(),
    onSeeAllClick: (searchMethod: String, filmType: Int) -> Unit,
    onFilmClick: (filmId: Int, filmType: Int) -> Unit
) {
    val uiState by seriesListViewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(Modifier.fillMaxSize()) {
        item {
            FilmListSection(
                title = "Populares",
                list = uiState.popularSeries,
                onSeeAllClick = {
                    onSeeAllClick(SeriesListSection.POPULAR.searchMethod, FilmType.SERIES.value)
                },
                onFilmClick = onFilmClick
            )
        }
        item {
            FilmListSection(
                title = "Mejor valoradas",
                list = uiState.topRatedSeries,
                onSeeAllClick = {
                    onSeeAllClick(SeriesListSection.TOP_RATED.searchMethod, FilmType.SERIES.value)
                },
                onFilmClick = onFilmClick
            )
        }
        item {
            FilmListSection(
                title = "En retransmisión",
                list = uiState.airingTodaySeries,
                onSeeAllClick = {
                    onSeeAllClick(SeriesListSection.AIRING_TODAY.searchMethod, FilmType.SERIES.value)
                },
                onFilmClick = onFilmClick
            )
        }
    }
}