package com.darvi.filmhunter.presentation.saved

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darvi.filmhunter.presentation.core.components.FilmHunterCircularProgress
import com.darvi.filmhunter.presentation.core.components.FilmHunterText
import com.darvi.filmhunter.presentation.core.components.SavedFilmRow

@Composable
fun SavedListScreen(
    savedListViewModel: SavedListViewModel = hiltViewModel(),
    onFilmClick: (Int, Int) -> Unit
) {
    val uiState by savedListViewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    when {
        uiState.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                FilmHunterCircularProgress()
            }
        }

        uiState.hasError -> {
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = { savedListViewModel.refreshFilms() },
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    FilmHunterText(text = uiState.errorMessage)
                }
            }
        }

        else -> {
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = { savedListViewModel.refreshFilms() },
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ){
                    itemsIndexed(uiState.films, key = { index, _ -> index}) { _, film ->
                        SavedFilmRow(
                            film = film,
                            onClick = {
                                onFilmClick(film.id, film.type.value)
                            }
                        )
                    }
                }
            }
        }
    }
}