package com.darvi.filmhunter.presentation.matcher.screens

import android.util.Log
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darvi.filmhunter.domain.entity.WatchProvider
import com.darvi.filmhunter.domain.entity.movie.MovieGenre
import com.darvi.filmhunter.domain.entity.series.SeriesGenre
import com.darvi.filmhunter.presentation.core.components.FilmHunterPrimaryButton
import com.darvi.filmhunter.presentation.core.components.FilmHunterText
import com.darvi.filmhunter.presentation.core.components.GoBackIconButton
import com.darvi.filmhunter.presentation.core.model.FilmType
import com.darvi.filmhunter.presentation.matcher.MatcherViewModel

@Composable
fun MatcherSummaryScreen(
    matcherViewModel: MatcherViewModel = hiltViewModel(),
    onCreateSession: (String) -> Unit, // Pass session code
    onBackClick: () -> Unit = {},
) {
    val uiState by matcherViewModel.uiState.collectAsStateWithLifecycle()
    val sessionCreationState by matcherViewModel.sessionCreationState.collectAsStateWithLifecycle()
    val currentUser by matcherViewModel.currentUser.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                FilmHunterText(
                    text = "Resumen de tu selección",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Film Type Section
                SummaryCard(
                    title = "Tipo",
                    content = when (uiState.selectedFilmType) {
                        FilmType.MOVIE -> "Película"
                        FilmType.SERIES -> "Serie"
                    }
                )

                // Genres Section
                val selectedGenres = when (uiState.selectedFilmType) {
                    FilmType.MOVIE -> {
                        uiState.selectedMovieGenres.mapNotNull { id ->
                            MovieGenre.entries.firstOrNull { it.id == id }?.displayName
                        }
                    }
                    FilmType.SERIES -> {
                        uiState.selectedSeriesGenres.mapNotNull { id ->
                            SeriesGenre.entries.firstOrNull { it.id == id }?.displayName
                        }
                    }
                }

                SummaryCard(
                    title = "Géneros",
                    content = if (selectedGenres.isEmpty()) {
                        "Ninguno seleccionado"
                    } else {
                        selectedGenres.joinToString(", ")
                    }
                )

                // Platforms Section
                val platformsText = if (uiState.selectAllPlatforms) {
                    "Todas las plataformas"
                } else {
                    val selectedPlatformNames = uiState.selectedPlatforms.mapNotNull { id ->
                        WatchProvider.entries.firstOrNull { it.id == id }?.title
                    }
                    if (selectedPlatformNames.isEmpty()) {
                        "Ninguna seleccionada"
                    } else {
                        selectedPlatformNames.joinToString(", ")
                    }
                }

                SummaryCard(
                    title = "Plataformas",
                    content = platformsText
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            FilmHunterPrimaryButton(
                text = "Crear Sesión",
                onClick = {
                    matcherViewModel.createSession(
                        onSuccess = { session ->
                            Log.i("MatcherSummaryScreen", session.toString())
                            onCreateSession(session.code)
                        },
                        onError = { error ->
                            // TODO: Show error message
                            Log.e("MatcherSummaryScreen", "Error creating session", error)
                        }
                    )
                },
                enabled = currentUser != null && sessionCreationState !is com.darvi.filmhunter.presentation.matcher.SessionCreationState.Loading
            )
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilmHunterText(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            FilmHunterText(
                text = content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

