package com.darvi.filmhunter.presentation.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.darvi.filmhunter.domain.entity.series.EpisodeEntity
import com.darvi.filmhunter.domain.entity.series.SeasonDetailEntity
import com.darvi.filmhunter.presentation.core.components.FilmHunterCircularProgress
import com.darvi.filmhunter.presentation.core.components.FilmHunterText
import com.darvi.filmhunter.presentation.core.components.GoBackIconButton
import com.darvi.filmhunter.presentation.core.modifiers.shimmerLoading
import com.darvi.filmhunter.presentation.core.util.ImageUrlHelper
import com.darvi.filmhunter.presentation.detail.components.DetailHeader
import com.darvi.filmhunter.presentation.detail.components.OverviewSection
import com.darvi.filmhunter.presentation.detail.components.WatchProvidersSection
import java.util.Locale
import kotlin.math.min

@Composable
fun SeasonDetailScreen(
    seasonDetailViewModel: SeasonDetailViewModel = hiltViewModel(),
    seriesId: Int,
    seasonNumber: Int,
    onBackClick: () -> Unit
) {

    val uiState by seasonDetailViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(seriesId, seasonNumber) {
        seasonDetailViewModel.getDetail(seriesId, seasonNumber)
    }

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
            SeasonDetailContent(
                season = uiState.season,
                onBackClick = onBackClick
            )
        }
    }
}

@Composable
fun SeasonDetailContent(
    season: SeasonDetailEntity,
    onBackClick: () -> Unit
) {
    val listState = rememberLazyListState()

    val darkenFraction by remember {
        derivedStateOf {
            val scrollOffset = min(listState.firstVisibleItemScrollOffset, 1000)
            val rawFraction = scrollOffset / 1000f
            rawFraction * rawFraction
        }
    }

    var expandedEpisodeNumber by remember { mutableStateOf<Int?>(null) }
    var isEpisodesExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            state = listState
        ) {

            item {
                DetailHeader(
                    title = season.title,
                    posterPath = season.posterPath,
                    releaseDate = season.releaseDate,
                    rating = season.voteAverage,
                    darkenFraction = darkenFraction
                )
            }

            item {
                Spacer(Modifier.height(16.dp))
                OverviewSection(overview = season.description)
            }

            item {
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            expandedEpisodeNumber = null
                            isEpisodesExpanded = !isEpisodesExpanded
                        }
                ) {
                    FilmHunterText(
                        text = "Capítulos (${season.episodes.size})",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(Modifier.weight(1f))

                    val rotation by animateFloatAsState(
                        targetValue = if (isEpisodesExpanded) 180f else 0f,
                        label = "EpisodeChevronRotation"
                    )

                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Capítulos (${season.episodes.size})",
                        modifier = Modifier
                            .size(32.dp)
                            .graphicsLayer {
                                rotationZ = rotation
                            }
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            itemsIndexed(
                items = if (isEpisodesExpanded) season.episodes else emptyList(),
                key = { _, episode -> episode.episodeNumber }
            ) { index, episode ->
                val isExpanded = expandedEpisodeNumber == episode.episodeNumber

                Column(Modifier.padding(vertical = 2.dp)){
                    EpisodeRow(
                        episode = episode,
                        isExpanded = isExpanded,
                        modifier = Modifier.animateItem(),
                        onClick = {
                            expandedEpisodeNumber =
                                if (isExpanded) null else episode.episodeNumber
                        }
                    )

                    if (index < season.episodes.lastIndex) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.surface
                        )
                    }
                }
            }

            if (season.watchProviders.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(24.dp))
                    WatchProvidersSection(providers = season.watchProviders)
                }
            }

            item {
                Spacer(Modifier.height(24.dp))
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            GoBackIconButton(onClick = onBackClick)
        }
    }
}

@Composable
fun EpisodeRow(
    episode: EpisodeEntity,
    isExpanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
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
            if (!episode.picturePath.isNullOrEmpty()) {
                Box(
                    Modifier
                        .width(160.dp)
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(ImageUrlHelper.getOriginalUrl(episode.picturePath))
                            .crossfade(true)
                            .build(), // o tu helper de URLs
                        contentDescription = episode.title,
                        modifier = Modifier
                            .width(160.dp)
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(8.dp))
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
                    text = "${episode.episodeNumber}- ${episode.title}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

                if (episode.releaseDate.isNotEmpty()) {
                    // Fecha
                    FilmHunterText(
                        text = episode.releaseDate,
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    FilmHunterText(
                        text = "Fecha no disponible",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(4.dp))

                // Rating + votos + duración
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Estrella
                    FilmHunterText(
                        text = "★",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(Modifier.width(4.dp))

                    FilmHunterText(
                        text = String.format(Locale.US, "%.1f", episode.voteAverage),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(Modifier.width(4.dp))

                    FilmHunterText(
                        text = "(${episode.voteCount})",
                        style = MaterialTheme.typography.bodySmall
                    )

                    episode.runtime?.let {
                        Spacer(Modifier.width(8.dp))
                        FilmHunterText(
                            text = "$it min",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            FilmHunterText(
                modifier = Modifier.padding(top = 8.dp),
                text = episode.overview,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Justify,
            )
        }
    }
}