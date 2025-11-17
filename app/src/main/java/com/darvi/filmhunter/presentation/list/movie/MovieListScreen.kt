package com.darvi.filmhunter.presentation.list.movie

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darvi.filmhunter.presentation.list.components.FilmListSection

@Composable
fun MovieListScreen(
    movieListViewModel: MovieListViewModel = hiltViewModel(),
) {
    val uiState by movieListViewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(Modifier.fillMaxSize()) {
        item {
            FilmListSection(
                title = "Populares",
                list = uiState.popularMovies,
            )
        }
        item {
            FilmListSection(
                title = "Mejor valoradas",
                list = uiState.topRatedMovies,
            )
        }
        item {
            FilmListSection(
                title = "En cines",
                list = uiState.nowPlayingMovies,
            )
        }
        item {
            FilmListSection(
                title = "Proximos estrenos",
                list = uiState.upcomingMovies,
            )
        }
    }
}