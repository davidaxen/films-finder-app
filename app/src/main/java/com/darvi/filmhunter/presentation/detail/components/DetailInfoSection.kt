package com.darvi.filmhunter.presentation.detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darvi.filmhunter.presentation.core.components.FilmHunterText
import com.darvi.filmhunter.presentation.detail.model.FilmDetailUiModel

@Composable
fun DetailInfoSection(film: FilmDetailUiModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        if (film.originalTitle.isNotBlank() && film.originalTitle != film.title) {
            FilmHunterText(
                text = "Título original",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            FilmHunterText(
                text = film.originalTitle,
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}