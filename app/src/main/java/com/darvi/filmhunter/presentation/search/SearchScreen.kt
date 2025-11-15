package com.darvi.filmhunter.presentation.search

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darvi.filmhunter.domain.entity.MovieEntity
import com.darvi.filmhunter.domain.entity.MovieGenre
import com.darvi.filmhunter.domain.entity.SeriesEntity
import com.darvi.filmhunter.domain.entity.SeriesGenre
import com.darvi.filmhunter.domain.entity.WatchProvider
import com.darvi.filmhunter.presentation.core.components.FilmHunterText
import com.darvi.filmhunter.presentation.core.components.FilmHunterTextField
import com.darvi.filmhunter.presentation.list.model.FilmType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by searchViewModel.uiState.collectAsStateWithLifecycle()

    BackHandler(enabled = uiState.searchQuery.isNotEmpty()) {
        searchViewModel.onCancelQuerySearch()
    }

    Column(Modifier
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
            SearchedFilms(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .zIndex(1f),
                isVisible = uiState.searchQuery.isNotEmpty(),
                isSearching = uiState.isSearching,
                moviesFound = uiState.moviesFound,
                seriesFound = uiState.seriesFound,
            ) {

            }

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
                            onClick = { /*genre -> onMovieGenreClick(genre)*/ }
                        )
                    }
                    FilmType.SERIES -> {
                        genreGrid(
                            items = SeriesGenre.entries.toList(),
                            label = { it.displayName },
                            onClick = { /*genre -> onSeriesGenreClick(genre)*/ }
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun SearchedFilms(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    isSearching: Boolean,
    moviesFound: List<MovieEntity>,
    seriesFound: List<SeriesEntity>,
    onFilmClick: () -> Unit
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                items(moviesFound) { item ->
                    FilmHunterText(
                        text = item.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onFilmClick() }
                            .padding(16.dp)
                    )
                    HorizontalDivider()
                }
                items(seriesFound) { item ->
                    FilmHunterText(
                        text = item.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onFilmClick() }
                            .padding(16.dp)
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun SearchFilterChip(
    isSelected: Boolean,
    text: String,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.surface
        ),
        label = {
            FilmHunterText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                text = text,
                textAlign = TextAlign.Center
            )
        },
    )
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

@Composable
fun SearchBarItem(
    value: String,
    showBackIcon: Boolean = false,
    onCancelQuerySearch: () -> Unit,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    Row(Modifier.fillMaxWidth()) {
        AnimatedVisibility(
            visible = showBackIcon,
            enter = slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(300)
            ) + fadeIn(tween(400)),
            exit = slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(300)
            ) + fadeOut(tween(200))
        ) {
            IconButton(
                onClick = { onCancelQuerySearch() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Borrar busqueda"
                )
            }
        }

        FilmHunterTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = { onValueChange(it) },
            shape = MaterialTheme.shapes.large,
            label = "",
            placeholder = "Busca una pelicula o serie...",
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { onSearch() }
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Busqueda"
                )
            },
            trailingIcon = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clipToBounds(),
                    contentAlignment = Alignment.Center
                ) {
                    this@Row.AnimatedVisibility(
                        visible = value.isNotEmpty(),
                        enter = slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(300)
                        ) + fadeIn(tween(400)),
                        exit = slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = tween(300)
                        ) + fadeOut(tween(200))
                    ) {
                        IconButton(onClick = { onCancelQuerySearch() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancelar"
                            )
                        }
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                focusedLeadingIconColor = MaterialTheme.colorScheme.onSurface,
            )
        )
    }
}