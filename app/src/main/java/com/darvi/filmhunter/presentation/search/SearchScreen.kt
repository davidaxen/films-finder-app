package com.darvi.filmhunter.presentation.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darvi.filmhunter.presentation.core.components.FilmHunterTextField
import com.darvi.filmhunter.presentation.list.components.FilmListSection
import com.darvi.filmhunter.presentation.list.model.toUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by searchViewModel.uiState.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize()) {
        SearchBarItem(
            value = uiState.searchQuery,
            onCancelQuerySearch = { searchViewModel.onCancelQuerySearch() },
            onValueChange = {
                searchViewModel.onQueryChange(it)
                searchViewModel.onSearch()
            },
            onSearch = { searchViewModel.onSearch() }
        )

        LazyColumn {
            item {
                FilmListSection(
                    title = "Peliculas",
                    list = uiState.moviesFound.map { it.toUiModel() },
                )
            }

            item {
                FilmListSection(
                    title = "Series",
                    list = uiState.seriesFound.map { it.toUiModel() },
                )
            }
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
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