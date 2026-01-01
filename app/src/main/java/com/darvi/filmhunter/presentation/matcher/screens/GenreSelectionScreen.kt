package com.darvi.filmhunter.presentation.matcher.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darvi.filmhunter.domain.entity.movie.MovieGenre
import com.darvi.filmhunter.domain.entity.series.SeriesGenre
import com.darvi.filmhunter.presentation.core.components.FilmHunterPrimaryButton
import com.darvi.filmhunter.presentation.core.components.FilmHunterText
import com.darvi.filmhunter.presentation.core.components.GoBackIconButton
import com.darvi.filmhunter.presentation.core.model.FilmType
import com.darvi.filmhunter.presentation.matcher.MatcherViewModel

@Composable
fun GenreSelectionScreen(
    matcherViewModel: MatcherViewModel = hiltViewModel(),
    onGenresSelected: () -> Unit,
    onBackClick: () -> Unit = {},
) {
    val uiState by matcherViewModel.uiState.collectAsStateWithLifecycle()

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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            FilmHunterText(
                text = "Selecciona géneros",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            FilmHunterText(
                text = "Elige uno o más géneros que te interesen",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                when (uiState.selectedFilmType) {
                    FilmType.MOVIE -> items(MovieGenre.entries) { genre ->
                        GenreCard(
                            genreName = genre.displayName,
                            isSelected = uiState.selectedMovieGenres.contains(genre.id),
                            onClick = {
                                matcherViewModel.onGenreToggled(
                                    genreId = genre.id
                                )
                            }
                        )
                    }
                    FilmType.SERIES -> items(SeriesGenre.entries) { genre ->
                        GenreCard(
                            genreName = genre.displayName,
                            isSelected = uiState.selectedSeriesGenres.contains(genre.id),
                            onClick = {
                                matcherViewModel.onGenreToggled(
                                    genreId = genre.id
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            val hasSelectedGenres = when (uiState.selectedFilmType) {
                FilmType.MOVIE -> uiState.selectedMovieGenres.isNotEmpty()
                FilmType.SERIES -> uiState.selectedSeriesGenres.isNotEmpty()
            }

            FilmHunterPrimaryButton(
                text = "Continuar",
                onClick = onGenresSelected,
                enabled = hasSelectedGenres
            )
        }
    }
}

@Composable
private fun GenreCard(
    genreName: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(shape)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        shape = shape,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            FilmHunterText(
                text = genreName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = TextAlign.Center,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

