package com.darvi.filmhunter.presentation.matcher.sessions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import com.darvi.filmhunter.domain.entity.MatcherSessionEntity
import com.darvi.filmhunter.domain.entity.WatchProvider
import com.darvi.filmhunter.domain.entity.movie.MovieGenre
import com.darvi.filmhunter.domain.entity.series.SeriesGenre
import com.darvi.filmhunter.presentation.core.components.FilmHunterCircularProgress
import com.darvi.filmhunter.presentation.core.components.FilmHunterText
import com.darvi.filmhunter.presentation.core.components.GoBackIconButton
import com.darvi.filmhunter.presentation.core.components.SavedFilmRow
import com.darvi.filmhunter.presentation.core.model.FilmType

@Composable
fun SessionDetailScreen(
    session: MatcherSessionEntity,
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GoBackIconButton(onClick = onBackClick)
                            Spacer(modifier = Modifier.width(8.dp))
                            FilmHunterText(
                                text = "Detalles de la sesión",
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
                        uiState.session?.let { loadedSession ->
                            val filmTypeValue = loadedSession.filters["filmType"] as? Int ?: 0
                            val filmType = FilmType.entries.firstOrNull { it.value == filmTypeValue } ?: FilmType.MOVIE
                            val sectionTitle = when (filmType) {
                                FilmType.MOVIE -> "Películas coincidentes"
                                FilmType.SERIES -> "Series coincidentes"
                            }
                            
                            FilmHunterText(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                text = sectionTitle,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    if (uiState.matchedFilms.isEmpty()) {
                        item {
                            uiState.session?.let { loadedSession ->
                                val filmTypeValue = loadedSession.filters["filmType"] as? Int ?: 0
                                val filmType = FilmType.entries.firstOrNull { it.value == filmTypeValue } ?: FilmType.MOVIE
                                val emptyMessage = when (filmType) {
                                    FilmType.MOVIE -> "No hay películas coincidentes en esta sesión"
                                    FilmType.SERIES -> "No hay series coincidentes en esta sesión"
                                }
                                
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    FilmHunterText(
                                        text = emptyMessage,
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    } else {
                        items(uiState.matchedFilms) { film ->
                            SavedFilmRow(
                                film = film,
                                onClick = dropUnlessResumed {
                                    onFilmClick(film.id, film.type.value)
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
    session: MatcherSessionEntity
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
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
            Spacer(modifier = Modifier.height(8.dp))

            session.createdAt?.let { createdAt ->
                val dateOnly = createdAt.take(10) // Extract YYYY-MM-DD from ISO format
                FilmHunterText(
                    text = "Fecha: $dateOnly",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

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


