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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darvi.filmhunter.domain.entity.series.SeasonDetailEntity
import com.darvi.filmhunter.presentation.core.components.FilmHunterCircularProgress
import com.darvi.filmhunter.presentation.core.components.FilmHunterText
import com.darvi.filmhunter.presentation.core.components.GoBackIconButton
import com.darvi.filmhunter.presentation.detail.components.DetailHeader
import com.darvi.filmhunter.presentation.detail.components.OverviewSection
import com.darvi.filmhunter.presentation.detail.components.WatchProvidersSection
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
                    darkenFraction  = darkenFraction
                )
            }

            item {
                Spacer(Modifier.height(16.dp))
                OverviewSection(overview = season.description)
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