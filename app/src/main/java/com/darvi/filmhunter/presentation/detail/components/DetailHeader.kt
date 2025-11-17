package com.darvi.filmhunter.presentation.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.darvi.filmhunter.presentation.core.util.ImageUrlHelper
import com.darvi.filmhunter.presentation.detail.model.FilmDetailUiModel

@Composable
fun DetailHeader(
    film: FilmDetailUiModel,
    darkenFraction: Float,
) {
    val fraction = darkenFraction.coerceIn(0f, 1f)
    val bgOverlayAlpha = (fraction * 1.0f).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.66f)
    ) {
        // Backdrop
        if (film.posterPath != null) {
            AsyncImage(
                model = ImageUrlHelper.getOriginalUrl(film.posterPath),
                contentDescription = "${film.title} backdrop",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background.copy(alpha = 0.15f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.35f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.65f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                            MaterialTheme.colorScheme.background
                        ),
                    )
                )
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    MaterialTheme.colorScheme.background.copy(alpha = bgOverlayAlpha)
                )
        )

        TitleSection(
            modifier = Modifier.align(Alignment.BottomStart),
            film = film
        )
    }
}