package com.darvi.filmhunter.presentation.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darvi.filmhunter.presentation.core.model.FilmType
import com.darvi.filmhunter.presentation.core.model.SearchMethod
import com.darvi.filmhunter.presentation.search.components.ResultList

@Composable
fun HomeFilmsListScreen(
    resultListViewModel: ResultListViewModel = hiltViewModel(),
    searchMethod: String,
    filmType: Int,
    onFilmClick: (Int, Int) -> Unit,
    onBackPress: () -> Unit
) {
    val uiState by resultListViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        resultListViewModel.onSearchMethodParamsLoad(searchMethod, filmType)
    }

    val title = when (uiState.filmTypeSelected) {
        FilmType.MOVIE -> "Películas ${SearchMethod.fromValue(searchMethod)?.displayName}"
        FilmType.SERIES -> "Series ${SearchMethod.fromValue(searchMethod)?.displayName}"
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