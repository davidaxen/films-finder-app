package com.darvi.filmhunter.presentation.search

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darvi.filmhunter.presentation.core.components.FilmHunterCircularProgress
import com.darvi.filmhunter.presentation.core.components.FilmHunterText
import com.darvi.filmhunter.presentation.core.components.GoBackIconButton
import com.darvi.filmhunter.presentation.list.model.FilmType
import com.darvi.filmhunter.presentation.search.components.SearchResultCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueryListScreen(
    queryListViewModel: QueryListViewModel = hiltViewModel(),
    query: String,
    filmType: Int,
    onBackPress: () -> Unit
) {
    val uiState by queryListViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        Log.i("QUERYLISTSCREEN LAUNCHED EFFECT", "$query, $filmType")
        queryListViewModel.onScreenParamsLoad(q = query, filmType = filmType)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GoBackIconButton {
                onBackPress()
            }
            FilmHunterText(
                text = "\"${uiState.searchQuery}\"",
                style = MaterialTheme.typography.titleMedium
            )
        }
        if (uiState.isSearching) {
            Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                FilmHunterCircularProgress(Modifier.align(Alignment.Center))
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(4.dp)
            ) {
                when (uiState.filmTypeSelected) {
                    FilmType.MOVIE -> {
                        items(uiState.moviesFound) { item ->
                            SearchResultCard(
                                title = item.title,
                                posterPath = item.posterPath,
                                year = item.releaseDate.take(4),
                                onClick = { /*onFilmClick(item.id)*/ }
                            )
                        }
                    }
                    FilmType.SERIES -> {
                        items(uiState.seriesFound) { item ->
                            SearchResultCard(
                                title = item.title,
                                posterPath = item.posterPath,
                                year = item.releaseDate.take(4),
                                onClick = { /*onFilmClick(item.id)*/ }
                            )
                        }
                    }
                }
            }
        }
    }
}