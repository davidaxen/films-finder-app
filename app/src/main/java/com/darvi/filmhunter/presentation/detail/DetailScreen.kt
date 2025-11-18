package com.darvi.filmhunter.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darvi.filmhunter.presentation.core.components.FilmHunterCircularProgress
import com.darvi.filmhunter.presentation.core.components.FilmHunterText
import com.darvi.filmhunter.presentation.core.components.GoBackIconButton
import com.darvi.filmhunter.presentation.detail.components.DetailHeader
import com.darvi.filmhunter.presentation.detail.components.DetailInfoSection
import com.darvi.filmhunter.presentation.detail.components.GenresSection
import com.darvi.filmhunter.presentation.detail.components.OverviewSection
import com.darvi.filmhunter.presentation.detail.components.SeasonsSection
import com.darvi.filmhunter.presentation.detail.model.FilmDetailUiModel
import kotlin.math.min

@Composable
fun DetailScreen(
    detailViewModel: DetailViewModel = hiltViewModel(),
    filmId: Int,
    filmType: Int,
    onBackClick: () -> Unit
) {
    val uiState by detailViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(filmId) {
        detailViewModel.getDetail(filmId, filmType)
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
            DetailContent(
                film = uiState.film,
                onBackClick = onBackClick
            )
        }
    }
}

@Composable
fun DetailContent(
    film: FilmDetailUiModel,
    onBackClick: () -> Unit
) {
    val listState = rememberLazyListState()

    val maxOffsetPx = 1000
    val scrollOffset = min(listState.firstVisibleItemScrollOffset, maxOffsetPx)
    val rawFraction = scrollOffset / maxOffsetPx.toFloat()
    val darkenFraction = (rawFraction * rawFraction)
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
                    film = film,
                    darkenFraction  = darkenFraction
                )
            }

            item {
                Spacer(Modifier.height(16.dp))
                DetailInfoSection(film = film)
            }

            item {
                Spacer(Modifier.height(16.dp))
                GenresSection(genres = film.genres)
            }

            item {
                Spacer(Modifier.height(16.dp))
                OverviewSection(overview = film.description)
            }

            if (film.seasons.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(24.dp))
                    SeasonsSection(seasons = film.seasons)
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

