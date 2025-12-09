package com.darvi.filmhunter.presentation.list.movie

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darvi.filmhunter.presentation.core.components.FilmHunterCircularProgress
import com.darvi.filmhunter.presentation.core.model.FilmType
import com.darvi.filmhunter.presentation.core.model.MovieListSection
import com.darvi.filmhunter.presentation.list.components.FilmListSection

@Composable
fun MovieListScreen(
    movieListViewModel: MovieListViewModel = hiltViewModel(),
    onSeeAllClick: (searchMethod: String, filmType: Int) -> Unit,
    onFilmClick: (filmId: Int, filmType: Int) -> Unit
) {
    val uiState by movieListViewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                FilmHunterCircularProgress()
            }
        }

        else -> {
            LazyColumn(Modifier.fillMaxSize()) {
                item {
                    FilmListSection(
                        title = "Populares",
                        list = uiState.popularMovies,
                        onSeeAllClick = {
                            onSeeAllClick(MovieListSection.POPULAR.searchMethod, FilmType.MOVIE.value)
                        },
                        onFilmClick = onFilmClick
                    )
                }
                item {
                    FilmListSection(
                        title = "Mejor valoradas",
                        list = uiState.topRatedMovies,
                        onSeeAllClick = {
                            onSeeAllClick(MovieListSection.TOP_RATED.searchMethod, FilmType.MOVIE.value)
                        },
                        onFilmClick = onFilmClick
                    )
                }
                item {
                    FilmListSection(
                        title = "En cines",
                        list = uiState.nowPlayingMovies,
                        onSeeAllClick = {
                            onSeeAllClick(MovieListSection.NOW_PLAYING.searchMethod, FilmType.MOVIE.value)
                        },
                        onFilmClick = onFilmClick
                    )
                }
            }
        }
    }
}