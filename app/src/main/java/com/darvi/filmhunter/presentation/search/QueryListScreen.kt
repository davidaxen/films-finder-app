package com.darvi.filmhunter.presentation.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darvi.filmhunter.presentation.search.components.ResultList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueryListScreen(
    resultListViewModel: ResultListViewModel = hiltViewModel(),
    query: String,
    filmType: Int,
    onFilmClick: (Int, Int) -> Unit,
    onBackPress: () -> Unit
) {
    val uiState by resultListViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        resultListViewModel.onQueryParamsLoad(q = query, filmType = filmType)
    }

    ResultList(
        modifier = Modifier.fillMaxSize(),
        films = uiState.filmsFound,
        title = "\"${uiState.searchQuery}\"",
        isSearching = uiState.isSearching,
        isLoadingMore = uiState.isLoadingMore,
        endReached = uiState.endReached,
        onFilmClick = onFilmClick,
        loadNextPage = { resultListViewModel.loadNextPage() },
        onBackPress = onBackPress
    )

}