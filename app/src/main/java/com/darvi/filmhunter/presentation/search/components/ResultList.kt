package com.darvi.filmhunter.presentation.search.components

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
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darvi.filmhunter.presentation.core.components.FilmHunterCircularProgress
import com.darvi.filmhunter.presentation.core.components.FilmHunterText
import com.darvi.filmhunter.presentation.core.components.FilmResultCard
import com.darvi.filmhunter.presentation.core.components.GoBackIconButton
import com.darvi.filmhunter.presentation.core.model.FilmUiModel

@Composable
fun ResultList(
    modifier: Modifier = Modifier,
    title: String,
    isSearching: Boolean,
    isLoadingMore: Boolean,
    endReached: Boolean,
    onFilmClick: (Int, Int) -> Unit,
    loadNextPage: () -> Unit,
    onBackPress: () -> Unit,
    films: List<FilmUiModel>
) {
    Column(
        modifier = modifier.padding(horizontal = 8.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GoBackIconButton {
                onBackPress()
            }
            FilmHunterText(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        }
        if (isSearching) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                FilmHunterCircularProgress(Modifier.align(Alignment.Center))
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
                itemsIndexed(films, key = { index, _ -> index }) { index, item ->
                    FilmResultCard(
                        title = item.title,
                        posterPath = item.posterPath,
                        aspectRatio = 0.56f,
                        subtitle = item.releaseYear,
                        onClick = { onFilmClick(item.id, item.type.value) }
                    )

                    if (
                        index == films.lastIndex &&
                        !isLoadingMore &&
                        !endReached
                    ) {
                        loadNextPage()
                    }
                }

                if (isLoadingMore) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            FilmHunterCircularProgress()
                        }
                    }
                }
            }
        }
    }

}