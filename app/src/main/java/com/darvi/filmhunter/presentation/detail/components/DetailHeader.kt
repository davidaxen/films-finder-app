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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.util.lerp
import coil3.compose.AsyncImage
import com.darvi.filmhunter.presentation.core.util.ImageUrlHelper
import com.darvi.filmhunter.presentation.detail.model.FilmDetailUiModel

@Composable
fun DetailHeader(
    film: FilmDetailUiModel,
    collapseFraction: Float,
) {
    val clampedFraction = collapseFraction.coerceIn(0f, 1f)

    // Opacidad del degradado: cuanto más scroll, más oscuro
    val baseAlpha = 0.35f
    val maxAlpha = 0.85f
    val overlayAlpha = lerp(
        start = baseAlpha,
        stop = maxAlpha,
        fraction = clampedFraction
    )

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
                            Color.Black.copy(alpha = overlayAlpha * 0.1f), // arriba muy suave
                            Color.Black.copy(alpha = overlayAlpha * 0.4f),
                            Color.Black.copy(alpha = overlayAlpha * 0.8f), // abajo muy oscuro
                            MaterialTheme.colorScheme.background
//                            MaterialTheme.colorScheme.background.copy(alpha = 0.25f),
//                            MaterialTheme.colorScheme.background.copy(alpha = 0.45f),
//                            MaterialTheme.colorScheme.background.copy(alpha = 0.60f),
//                            MaterialTheme.colorScheme.background.copy(alpha = 0.85f),
//                            MaterialTheme.colorScheme.background
                        ),
                    )
                )
        )

        TitleSection(
            modifier = Modifier.align(Alignment.BottomStart),
            film = film
        )
    }
}