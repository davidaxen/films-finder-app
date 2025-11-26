package com.darvi.filmhunter.presentation.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.darvi.filmhunter.presentation.core.components.FilmHunterCircularProgress
import com.darvi.filmhunter.presentation.core.components.FilmHunterText
import com.darvi.filmhunter.presentation.core.components.WatchProviderLogo
import com.darvi.filmhunter.presentation.core.model.FilmDetailUiModel
import com.darvi.filmhunter.presentation.core.modifiers.shimmerLoading
import com.darvi.filmhunter.presentation.core.util.ImageUrlHelper
import java.util.Locale

@Composable
fun SavedListScreen(
    savedListViewModel: SavedListViewModel = hiltViewModel(),
    onFilmClick: (Int, Int) -> Unit
) {
    val uiState by savedListViewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }

    when {
        uiState.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                FilmHunterCircularProgress()
            }
        }

        uiState.hasError -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                FilmHunterText(text = uiState.errorMessage)
            }
        }

        else -> {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 4.dp)
            ){
                itemsIndexed(uiState.films, key = { index, _ -> index}) { index, film->
                    SavedFilmRow(
                        film = film,
                        onClick = {
                            onFilmClick(film.id, film.type.value)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SavedFilmRow(
    modifier: Modifier = Modifier,
    film: FilmDetailUiModel,
    onClick: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Imagen
            if (!film.posterPath.isNullOrEmpty()) {
                Box(
                    Modifier
                        .width(110.dp)
                        .aspectRatio(0.66f)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(ImageUrlHelper.getOriginalUrl(film.posterPath))
                            .crossfade(true)
                            .build(), // o tu helper de URLs
                        contentDescription = film.title,
                        modifier = Modifier
                            .matchParentSize()
                            .shimmerLoading(isVisible = isLoading),
                        contentScale = ContentScale.Crop,
                        onSuccess = { isLoading = false },
                        onError = { isLoading = false }
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .width(160.dp)
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    FilmHunterText(
                        text = "Imagen no disponible",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            // Info del episodio
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Título
                FilmHunterText(
                    text = film.title,
                    style = MaterialTheme.typography.titleMedium,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

                // Rating + votos + duración
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Estrella
                    FilmHunterText(
                        text = "★",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.width(4.dp))

                    FilmHunterText(
                        text = String.format(Locale.US, "%.1f", film.rating),
                        style = MaterialTheme.typography.bodyMedium,
                    )

                    Spacer(Modifier.width(4.dp))

                    FilmHunterText(
                        text = "(${film.voteCount})",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(8.dp))

                FilmHunterText(
                    text = film.description,
                    style = MaterialTheme.typography.bodySmall,
                    minLines = 4,
                    maxLines = 4,
                    textAlign = TextAlign.Justify,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(
                        items = film.watchProviders,
                        key = { it.id }
                    ) { provider ->
                        provider.logoPath?.let {
                            WatchProviderLogo(
                                name = provider.name,
                                size = 35.dp,
                                logoUrl = ImageUrlHelper.getOriginalUrl(it)
                            )
                        }
                    }
                }
            }
        }
    }
}