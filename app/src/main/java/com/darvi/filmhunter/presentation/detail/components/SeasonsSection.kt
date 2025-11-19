package com.darvi.filmhunter.presentation.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darvi.filmhunter.domain.entity.series.SeriesSeasonEntity
import com.darvi.filmhunter.presentation.core.components.FilmHunterText
import com.darvi.filmhunter.presentation.core.components.FilmResultCard

@Composable
fun SeasonsSection(
    seasons: List<SeriesSeasonEntity>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp)
    ) {
        FilmHunterText(
            text = "Temporadas",
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(seasons, key = { it.id }) { season ->
                FilmResultCard(
                    title = season.name,
                    posterPath = season.posterPath,
                    subtitle = "${season.episodeCount} episodios",
                    aspectRatio = 0.66f,
                    titleMaxLines = 1
                ) {

                }
            }
        }
    }
}