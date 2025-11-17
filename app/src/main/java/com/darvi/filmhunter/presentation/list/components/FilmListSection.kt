package com.darvi.filmhunter.presentation.list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.darvi.filmhunter.presentation.core.components.FilmHunterText
import com.darvi.filmhunter.presentation.core.util.ImageUrlHelper
import com.darvi.filmhunter.presentation.list.model.FilmUiModel

@Composable
fun FilmListSection(
    modifier: Modifier = Modifier,
    title: String,
    list: List<FilmUiModel>,
    onMovieClick: (FilmUiModel) -> Unit = {}
) {
    Column(
        modifier
            .fillMaxWidth()
            .fillMaxHeight(0.30f)
            .padding(horizontal = 4.dp, vertical = 8.dp)
    ) {
        FilmHunterText(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        LazyRow {
            items(
                items = list,
                key = { it.id }
            ) { film ->
                FilmListItem(film = film, onClick = { onMovieClick(film) })
            }
        }
    }
}


@Composable
private fun FilmListItem(
    film: FilmUiModel,
    onClick: () -> Unit
) {
    val posterUrl = film.posterPath?.let { ImageUrlHelper.getW342Url(it) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
//                    .fillMaxWidth()
                    .aspectRatio(2f / 3f),
                contentAlignment = Alignment.Center
            ) {
                if (posterUrl != null) {
                    AsyncImage(
                        model = posterUrl,
                        contentDescription = "${film.title} poster",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface),
                    ) {
                        Icon(
                            Icons.Filled.Image, contentDescription = null,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}