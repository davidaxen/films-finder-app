package com.darvi.filmhunter.presentation.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed
import com.darvi.filmhunter.presentation.core.components.FilmHunterText
import com.darvi.filmhunter.presentation.core.model.FilmGenreUiModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GenresSection(
    genres: List<FilmGenreUiModel>,
    onGenreClick: (genreId: Int, genreType: Int) -> Unit
) {
    if (genres.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        FilmHunterText(
            text = "Géneros",
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            genres.forEach { genre ->
                SuggestionChip(
                    onClick = dropUnlessResumed {
                        onGenreClick(genre.id, genre.genreType.value)
                    },
                    label = {
                        FilmHunterText(text = genre.displayName)
                    }
                )
            }
        }
    }
}