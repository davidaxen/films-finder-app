package com.darvi.filmhunter.presentation.search

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darvi.filmhunter.domain.entity.WatchProvider
import com.darvi.filmhunter.domain.entity.movie.MovieEntity
import com.darvi.filmhunter.domain.entity.movie.MovieGenre
import com.darvi.filmhunter.domain.entity.series.SeriesEntity
import com.darvi.filmhunter.domain.entity.series.SeriesGenre
import com.darvi.filmhunter.presentation.core.components.FilmHunterText
import com.darvi.filmhunter.presentation.core.components.FilmResultCard
import com.darvi.filmhunter.presentation.core.model.FilmType
import com.darvi.filmhunter.presentation.search.components.SearchBarItem
import com.darvi.filmhunter.presentation.search.components.SearchFilterChip
import com.darvi.filmhunter.presentation.search.components.SearchItemsHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = hiltViewModel(),
    onFilmClick: (Int, Int) -> Unit,
    onSearchByGenres: (Int, Int) -> Unit,
    onSeeAllClick: (String, Int) -> Unit
) {
    val uiState by searchViewModel.uiState.collectAsStateWithLifecycle()

    BackHandler(enabled = uiState.searchQuery.isNotEmpty()) {
        searchViewModel.onCancelQuerySearch()
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        SearchBarItem(
            value = uiState.searchQuery,
            showBackIcon = uiState.searchQuery.isNotEmpty(),
            onCancelQuerySearch = { searchViewModel.onCancelQuerySearch() },
            onValueChange = {
                searchViewModel.onQueryChange(it)
                searchViewModel.onSearch()
            },
            onSearch = { searchViewModel.onSearch() }
        )

        Box(Modifier.fillMaxSize()) {
            SearchedFilmsList(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .zIndex(1f),
                isVisible = uiState.searchQuery.isNotEmpty(),
                isSearching = uiState.isSearching,
                moviesFound = uiState.moviesFound,
                seriesFound = uiState.seriesFound,
                onSeeAllClick = { onSeeAllClick(uiState.searchQuery, it.value) },
                onFilmClick = onFilmClick
            )

            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item(span = { GridItemSpan(2) }) {
                    FilterSectionTitle(title = "Tipo")
                }
                items(FilmType.entries) { filmType ->
                    SearchFilterChip(
                        isSelected = uiState.filmTypeSelected == filmType,
                        text = filmType.title
                    ) {
                        searchViewModel.onFilmTypeSelected(filmType)
                    }
                }

                item(span = { GridItemSpan(2) }) {
                    Spacer(Modifier.height(32.dp))
                }

                item(span = { GridItemSpan(2) }) {
                    FilterSectionTitle(title = "Plataforma")
                }
                item {
                    SearchFilterChip(
                        isSelected = uiState.watchProviderSelected == null,
                        text = "Todos"
                    ) {
                        searchViewModel.onWatchProviderSelected(null)
                    }
                }
                items(WatchProvider.entries) { watchProvider ->
                    SearchFilterChip(
                        isSelected = uiState.watchProviderSelected == watchProvider,
                        imageUrl = watchProvider.logoPath,
                        text = watchProvider.title
                    ) {
                        searchViewModel.onWatchProviderSelected(watchProvider)
                    }
                }

                item(span = { GridItemSpan(2) }) {
                    Spacer(Modifier.height(32.dp))
                }

                item(span = { GridItemSpan(2) }) {
                    FilterSectionTitle(title = "Género")
                }
                when (uiState.filmTypeSelected) {
                    FilmType.MOVIE -> {
                        genreGrid(
                            items = MovieGenre.entries.toList(),
                            label = { it.displayName },
                            onClick = { genre ->
                                searchViewModel.onGenreClicked(genre.id)
                                onSearchByGenres(genre.id, FilmType.MOVIE.value)
                            }
                        )
                    }

                    FilmType.SERIES -> {
                        genreGrid(
                            items = SeriesGenre.entries.toList(),
                            label = { it.displayName },
                            onClick = { genre ->
                                searchViewModel.onGenreClicked(genre.id)
                                onSearchByGenres(genre.id, FilmType.SERIES.value)
                            }
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun SearchedFilmsList(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    isSearching: Boolean,
    moviesFound: List<MovieEntity>,
    seriesFound: List<SeriesEntity>,
    onSeeAllClick: (FilmType) -> Unit,
    onFilmClick: (Int, Int) -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = tween(durationMillis = 500)
        ),
        exit = shrinkVertically(
            shrinkTowards = Alignment.Top,
            animationSpec = tween(durationMillis = 500)
        ),
        modifier = modifier
    ) {
        if (isSearching) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.Center),
                    strokeWidth = 4.dp
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(4.dp)
            ) {

                // ---- Películas ----
                val moviesToShow = moviesFound.take(6)
                if (moviesToShow.isNotEmpty()) {
                    item(span = { GridItemSpan(3) }) {
                        SearchItemsHeader(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 2.dp),
                            title = "Películas",
                        ) {
                            onSeeAllClick(FilmType.MOVIE)
                        }
                    }
                    items(moviesToShow) { item ->
                        FilmResultCard(
                            title = item.title,
                            posterPath = item.posterPath,
                            subtitle = item.releaseDate.take(4),
                            onClick = { onFilmClick(item.id, FilmType.MOVIE.value) }
                        )
                    }
                }

                // ---- Series ----
                val seriesToShow = seriesFound.take(6)
                if (seriesToShow.isNotEmpty()) {
                    item(span = { GridItemSpan(3) }) {
                        SearchItemsHeader(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 2.dp),
                            title = "Series",
                        ) {
                            onSeeAllClick(FilmType.SERIES)
                        }
                    }
                    items(seriesToShow) { item ->
                        FilmResultCard(
                            title = item.title,
                            posterPath = item.posterPath,
                            subtitle = item.releaseDate.take(4),
                            onClick = { onFilmClick(item.id, FilmType.SERIES.value) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterSectionTitle(modifier: Modifier = Modifier, title: String) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(Modifier.weight(1f))
        FilmHunterText(
            modifier = Modifier.weight(1f),
            text = title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )
        HorizontalDivider(Modifier.weight(1f))
    }
}


private fun <T> LazyGridScope.genreGrid(
    items: List<T>,
    label: (T) -> String,
    onClick: (T) -> Unit
) {
    items(items) { item ->
        SuggestionChip(
            onClick = { onClick(item) },
            label = {
                FilmHunterText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    text = label(item),
                    textAlign = TextAlign.Center
                )
            },
        )
    }
}