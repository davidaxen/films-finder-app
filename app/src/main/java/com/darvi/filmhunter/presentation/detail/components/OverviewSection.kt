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

@Composable
fun OverviewSection(overview: String) {
    if (overview.isBlank()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        FilmHunterText(
            text = "Sinopsis",
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(Modifier.height(8.dp))
        FilmHunterText(
            text = overview,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}