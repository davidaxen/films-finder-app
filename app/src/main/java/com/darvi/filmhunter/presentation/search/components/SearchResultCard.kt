package com.darvi.filmhunter.presentation.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.darvi.filmhunter.R
import com.darvi.filmhunter.presentation.core.components.FilmHunterText
import com.darvi.filmhunter.presentation.core.modifiers.shimmerLoading

@Composable
fun SearchResultCard(
    title: String,
    posterPath: String?,
    year: String,
    onClick: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    val imageURL = posterPath?.let { "${stringResource(R.string.poster_url_original)}$it" }
    Card(
        Modifier.width(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
        ) {
            if (imageURL != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageURL)
                        .crossfade(true)
                        .build(),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.66f)
                        .clip(MaterialTheme.shapes.medium)
                        .shimmerLoading(isVisible = isLoading),
                    onSuccess = { isLoading = false },
                    onError = { isLoading = false }
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.66f)
                        .background(MaterialTheme.colorScheme.surface),
                ) {
                    FilmHunterText(
                        modifier = Modifier.align(Alignment.Center),
                        text = "Imagen no disponible",
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 6.dp),
            ) {
                FilmHunterText(
                    modifier = Modifier.fillMaxWidth(),
                    text = title,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )

                FilmHunterText(
                    text = year,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}