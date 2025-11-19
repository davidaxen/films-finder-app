package com.darvi.filmhunter.presentation.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darvi.filmhunter.presentation.core.components.FilmHunterText
import com.darvi.filmhunter.presentation.core.components.FilmResultCard
import com.darvi.filmhunter.presentation.core.model.FilmUiModel

@Composable
fun RecommendationSection(
    films: List<FilmUiModel>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        FilmHunterText(
            text = "Recomendaciones",
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(films) { film ->
                FilmResultCard(
                    title = film.title,
                    posterPath = film.posterPath,
                    year = film.releaseYear,
                    titleMaxLines = 1,
                    aspectRatio = 0.66f
                ) {

                }
            }
        }
    }
}