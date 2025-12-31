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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.darvi.filmhunter.domain.entity.WatchProvider
import com.darvi.filmhunter.presentation.core.components.FilmHunterPrimaryButton
import com.darvi.filmhunter.presentation.core.components.FilmHunterText
import com.darvi.filmhunter.presentation.core.components.GoBackIconButton
import com.darvi.filmhunter.presentation.core.util.ImageUrlHelper
import com.darvi.filmhunter.presentation.matcher.MatcherViewModel

@Composable
fun PlatformSelectionScreen(
    matcherViewModel: MatcherViewModel = hiltViewModel(),
    onPlatformsSelected: () -> Unit,
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
                text = "Selecciona plataformas",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            FilmHunterText(
                text = "Elige todas las plataformas o selecciona 1 o 2 específicas",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // "All" button
            PlatformCard(
                title = "Todas",
                isSelected = uiState.selectAllPlatforms,
                onClick = {
                    matcherViewModel.onSelectAllPlatforms()
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(WatchProvider.entries) { provider ->
                    PlatformCard(
                        title = provider.title,
                        logoPath = provider.logoPath,
                        isSelected = uiState.selectedPlatforms.contains(provider.id),
                        onClick = {
                            matcherViewModel.onPlatformToggled(provider.id)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            val hasSelection = uiState.selectAllPlatforms || uiState.selectedPlatforms.isNotEmpty()

            FilmHunterPrimaryButton(
                text = "Continuar",
                onClick = onPlatformsSelected,
                enabled = hasSelection
            )
        }
    }
}

@Composable
private fun PlatformCard(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    logoPath: String? = null
) {
    val shape = RoundedCornerShape(16.dp)
    Card(
        modifier = modifier
            .fillMaxWidth()
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (logoPath != null) {
                AsyncImage(
                    model = ImageUrlHelper.getOriginalUrl(logoPath),
                    contentDescription = title,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            FilmHunterText(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

