package com.darvi.filmhunter.presentation.search

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.darvi.filmhunter.domain.entity.MovieGenre
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

    Column(Modifier
        .fillMaxSize()
        .padding(horizontal = 8.dp)
    ) {
        SearchBarItem(
            value = uiState.searchQuery,
            onCancelQuerySearch = { searchViewModel.onCancelQuerySearch() },
            onValueChange = {
                searchViewModel.onQueryChange(it)
                searchViewModel.onSearch()
            },
            onSearch = { searchViewModel.onSearch() }
        )

        Box(Modifier.fillMaxSize()) {
            this@Column.AnimatedVisibility(
                visible = uiState.searchQuery.isNotEmpty(),
                enter = expandVertically(
                    expandFrom = Alignment.Top,
                    animationSpec = tween(durationMillis = 500)
                ),
                exit = shrinkVertically(
                    shrinkTowards = Alignment.Top,
                    animationSpec = tween(durationMillis = 500)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .zIndex(1f)
            ) {
                if (uiState.isSearching) {
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
                        items(uiState.moviesFound) { item ->
                            FilmHunterText(
                                text = item.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { }
                                    .padding(16.dp)
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterSection(title = "Tipo") {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        FilmType.entries.forEach { filmType ->
                            FilterChip(
                                modifier = Modifier.weight(1f),
                                selected = uiState.filmTypeSelected == filmType,
                                onClick = { searchViewModel.onFilmTypeSelected(filmType) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.surface
                                ),
                                label = {
                                    FilmHunterText(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        text = filmType.title,
                                        textAlign = TextAlign.Center
                                    )
                                },
                            )
                        }
                    }
                }

                FilterSection(title = "Proveedor") {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        item {
                            FilterChip(
                                modifier = Modifier.weight(1f),
                                selected = uiState.watchProviderSelected == null,
                                onClick = { searchViewModel.onWatchProviderSelected(null) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.surface
                                ),
                                label = {
                                    FilmHunterText(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        text = "Todos",
                                        textAlign = TextAlign.Center
                                    )
                                },
                            )
                        }
                        items(WatchProvider.entries) { watchProvider ->
                            FilterChip(
                                modifier = Modifier.weight(1f),
                                selected = uiState.watchProviderSelected == watchProvider,
                                onClick = { searchViewModel.onWatchProviderSelected(watchProvider) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.surface
                                ),
                                label = {
                                    FilmHunterText(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        text = watchProvider.title,
                                        textAlign = TextAlign.Center
                                    )
                                },
                            )
                        }
                    }
                }


                FilterSection(title = "Generos") {
                    when (uiState.filmTypeSelected) {
                        FilmType.MOVIE -> {
                            GenreGrid(
                                items = MovieGenre.entries.toList(),
                                label = { it.displayName },
                                onClick = { /*genre -> onMovieGenreClick(genre)*/ }
                            )
                        }

                        FilmType.SERIES -> {
                            GenreGrid(
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreSelector(
    modifier: Modifier = Modifier,
    onMovieGenreClick: (MovieGenre) -> Unit,
    onSeriesGenreClick: (SeriesGenre) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf(FilmType.MOVIE) }

    Column(modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            OutlinedTextField(
                readOnly = true,
                value = when (selectedType) {
                    FilmType.MOVIE -> "Películas"
                    FilmType.SERIES -> "Series"
                },
                onValueChange = {},
                label = { FilmHunterText(text = "Tipo de contenido") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                        enabled = true
                    )
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { FilmHunterText(text = "Películas") },
                    onClick = {
                        selectedType = FilmType.MOVIE
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { FilmHunterText(text = "Series") },
                    onClick = {
                        selectedType = FilmType.SERIES
                        expanded = false
                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        when (selectedType) {
            FilmType.MOVIE -> {
                FilterSection(title = "Generos") {
                    GenreGrid(
                        items = MovieGenre.entries.toList(),
                        label = { it.displayName },
                        onClick = { genre -> onMovieGenreClick(genre) }
                    )
                }
            }

            FilmType.SERIES -> {
                FilterSection(title = "Generos") {
                    GenreGrid(
                        items = SeriesGenre.entries.toList(),
                        label = { it.displayName },
                        onClick = { genre -> onSeriesGenreClick(genre) }
                    )
                }
            }
        }
    }
}

@Composable
fun FilterSection(modifier: Modifier = Modifier, title: String, content: @Composable (() -> Unit)) {
    Column {
        Row(
            modifier = modifier.fillMaxWidth().padding(bottom = 4.dp),
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
        content()
    }

}

@Composable
private fun <T> GenreGrid(
    items: List<T>,
    label: (T) -> String,
    onClick: (T) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
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
}

@Composable
fun SearchBarItem(
    value: String,
    onCancelQuerySearch: () -> Unit,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit
) {
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
                AnimatedVisibility(
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