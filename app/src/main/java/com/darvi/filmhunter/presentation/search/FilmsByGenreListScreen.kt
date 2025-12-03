package com.darvi.filmhunter.presentation.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darvi.filmhunter.domain.entity.movie.MovieGenre
import com.darvi.filmhunter.presentation.core.model.FilmType
import com.darvi.filmhunter.presentation.search.components.ResultList

@Composable
fun FilmsByGenreListScreen(
    resultListViewModel: ResultListViewModel = hiltViewModel(),
    genre: Int,
    platformId: Int?,
    filmType: Int,
    onFilmClick: (Int, Int) -> Unit,
    onBackPress: () -> Unit
) {
    val uiState by resultListViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        resultListViewModel.onGenresParamsLoad(genre = genre, platformId = platformId, filmType = filmType)
    }
    var title = when (uiState.filmTypeSelected) {
        FilmType.MOVIE -> "Películas de ${MovieGenre.fromId(genre)?.displayName}"
        FilmType.SERIES -> "Series de ${MovieGenre.fromId(genre)?.displayName}"
    }

    title += when (uiState.platformSelected) {
        null -> ""
        else -> " (${uiState.platformSelected?.title})"
    }

    ResultList(
        modifier = Modifier.fillMaxSize(),
        films = uiState.filmsFound,
        title = title,
        isSearching = uiState.isSearching,
        isLoadingMore = uiState.isLoadingMore,
        endReached = uiState.endReached,
        onFilmClick = onFilmClick,
        loadNextPage = { resultListViewModel.loadNextPage() },
        onBackPress = onBackPress
    )

}