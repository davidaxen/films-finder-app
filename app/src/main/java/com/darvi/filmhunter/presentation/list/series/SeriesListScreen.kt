package com.darvi.filmhunter.presentation.list.series

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darvi.filmhunter.presentation.list.components.FilmListSection

@Composable
fun SeriesListScreen(
    seriesListViewModel: SeriesListViewModel = hiltViewModel()
) {
    val uiState by seriesListViewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(Modifier.fillMaxSize()) {
        item {
            FilmListSection(
                title = "Populares",
                list = uiState.popularSeries,
            )
        }
        item {
            FilmListSection(
                title = "Mejor valoradas",
                list = uiState.topRatedSeries,
            )
        }
        item {
            FilmListSection(
                title = "En retransmisión",
                list = uiState.airingTodaySeries,
            )
        }
        item {
            FilmListSection(
                title = "Proximos estrenos",
                list = uiState.onTheAirSeries,
            )
        }
    }
}