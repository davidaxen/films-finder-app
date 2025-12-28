package com.darvi.filmhunter.presentation.matcher.sessions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.darvi.filmhunter.domain.entity.WatchProvider
import com.darvi.filmhunter.domain.entity.movie.MovieDetailEntity
import com.darvi.filmhunter.domain.entity.movie.MovieGenre
import com.darvi.filmhunter.domain.entity.series.SeriesDetailEntity
import com.darvi.filmhunter.domain.entity.series.SeriesGenre
import com.darvi.filmhunter.presentation.core.components.FilmHunterCircularProgress
import com.darvi.filmhunter.presentation.core.components.FilmHunterText
import com.darvi.filmhunter.presentation.core.components.GoBackIconButton
import com.darvi.filmhunter.presentation.core.model.FilmType
import com.darvi.filmhunter.presentation.core.util.ImageUrlHelper
import java.util.Locale

@Composable
fun SessionDetailScreen(
    session: com.darvi.filmhunter.domain.entity.MatcherSessionEntity,
    viewModel: SessionDetailViewModel = hiltViewModel(),
    onFilmClick: (Int, Int) -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(session) {
        viewModel.loadSessionDetail(session)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    FilmHunterCircularProgress()
                }
            }
            uiState.hasError -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    FilmHunterText(
                        text = uiState.errorMessage ?: "Error al cargar detalles",
                        textAlign = TextAlign.Center
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GoBackIconButton(onClick = onBackClick)
                            Spacer(modifier = Modifier.width(8.dp))
                            FilmHunterText(
                                text = "Detalles de Sesión",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    // Session Info Card
                    item {
                        uiState.session?.let { loadedSession ->
                            SessionInfoCard(session = loadedSession)
                        }
                    }

                    // Matched Films Section
                    item {
                        FilmHunterText(
                            text = "Películas Matcheadas",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    if (uiState.matchedFilms.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                FilmHunterText(
                                    text = "No hay películas coincidentes en esta sesión",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    } else {
                        items(uiState.matchedFilms) { film ->
                            MatchedFilmCard(
                                film = film,
                                onClick = dropUnlessResumed {
                                    when (film) {
                                        is MovieDetailEntity -> onFilmClick(film.id, FilmType.MOVIE.value)
                                        is SeriesDetailEntity -> onFilmClick(film.id, FilmType.SERIES.value)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionInfoCard(
    session: com.darvi.filmhunter.domain.entity.MatcherSessionEntity
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Code
            FilmHunterText(
                text = "Código: ${session.code}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Film Type
            val filmTypeValue = session.filters["filmType"] as? Int ?: 0
            val filmType = FilmType.entries.firstOrNull { it.value == filmTypeValue } ?: FilmType.MOVIE
            FilmHunterText(
                text = "Tipo: ${filmType.title}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Genres
            val genres = (session.filters["genres"] as? List<*>)?.mapNotNull {
                when (it) {
                    is Int -> it
                    is String -> it.toIntOrNull()
                    else -> null
                }
            } ?: emptyList()

            if (genres.isNotEmpty()) {
                val genreNames = genres.mapNotNull { genreId ->
                    when (filmType) {
                        FilmType.MOVIE -> MovieGenre.fromId(genreId)?.displayName
                        FilmType.SERIES -> SeriesGenre.fromId(genreId)?.displayName
                    }
                }
                FilmHunterText(
                    text = "Géneros: ${genreNames.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Platforms
            val platforms = when (val platformsValue = session.filters["platforms"]) {
                is String -> {
                    if (platformsValue == "all") {
                        WatchProvider.entries.map { it.id }
                    } else {
                        emptyList()
                    }
                }
                is List<*> -> {
                    platformsValue.mapNotNull {
                        when (it) {
                            is Int -> it
                            is String -> it.toIntOrNull()
                            else -> null
                        }
                    }
                }
                else -> emptyList()
            }

            if (platforms.isNotEmpty()) {
                val platformNames = platforms.mapNotNull { platformId ->
                    WatchProvider.fromId(platformId)?.title
                }
                FilmHunterText(
                    text = "Plataformas: ${platformNames.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun MatchedFilmCard(
    film: Any,
    onClick: () -> Unit
) {
    val title = when (film) {
        is MovieDetailEntity -> film.title
        is SeriesDetailEntity -> film.title
        else -> ""
    }
    val posterPath = when (film) {
        is MovieDetailEntity -> film.posterPath
        is SeriesDetailEntity -> film.posterPath
        else -> null
    }
    val releaseDate = when (film) {
        is MovieDetailEntity -> film.releaseDate
        is SeriesDetailEntity -> film.releaseDate
        else -> ""
    }
    val voteAverage = when (film) {
        is MovieDetailEntity -> film.voteAverage
        is SeriesDetailEntity -> film.voteAverage
        else -> 0.0
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp)
        ) {
            // Poster
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                val imageUrl = posterPath?.let { ImageUrlHelper.getW342Url(it) }
                if (imageUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        FilmHunterText(
                            text = "Sin imagen",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                FilmHunterText(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                FilmHunterText(
                    text = releaseDate.take(4),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                FilmHunterText(
                    text = "⭐ ${String.format(Locale.getDefault(), "%.1f", voteAverage)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

