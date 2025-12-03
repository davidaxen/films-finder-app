package com.darvi.filmhunter.presentation.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darvi.filmhunter.presentation.search.components.ResultList

@Composable
fun QueryListScreen(
    resultListViewModel: ResultListViewModel = hiltViewModel(),
    query: String,
    filmType: Int,
    onFilmClick: (Int, Int) -> Unit,
    onBackPress: () -> Unit
) {
    val uiState by resultListViewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberSaveable(saver = LazyGridState.Saver) { LazyGridState() }

    LaunchedEffect(Unit) {
        resultListViewModel.onQueryParamsLoad(q = query, filmType = filmType)
    }

    ResultList(
        modifier = Modifier.fillMaxSize(),
        films = uiState.filmsFound,
        listState = listState,
        title = "\"${uiState.searchQuery}\"",
        isSearching = uiState.isSearching,
        isLoadingMore = uiState.isLoadingMore,
        endReached = uiState.endReached,
        onFilmClick = onFilmClick,
        loadNextPage = { resultListViewModel.loadNextPage() },
        onBackPress = onBackPress
    )

}