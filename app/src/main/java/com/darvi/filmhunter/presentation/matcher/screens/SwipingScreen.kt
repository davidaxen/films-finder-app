package com.darvi.filmhunter.presentation.matcher.screens

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.dropUnlessResumed
import coil3.ImageLoader
import coil3.compose.SubcomposeAsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.darvi.filmhunter.domain.entity.movie.MovieDetailEntity
import com.darvi.filmhunter.domain.entity.series.SeriesDetailEntity
import com.darvi.filmhunter.presentation.core.components.FilmHunterText
import com.darvi.filmhunter.presentation.core.util.ImageUrlHelper

@Composable
fun SwipingScreen(
    sessionId: String,
    viewModel: SwipingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val imageLoader = remember { ImageLoader(context) }
    
    LaunchedEffect(sessionId) {
        viewModel.setSessionId(sessionId)
        viewModel.loadSessionTitles(sessionId)
    }
    
    // Preload images for next 5 films
    LaunchedEffect(uiState.preloadedPosterUrls) {
        uiState.preloadedPosterUrls.forEach { posterUrl ->
            val preloadRequest = ImageRequest.Builder(context)
                .data(posterUrl)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build()
            imageLoader.enqueue(preloadRequest)
        }
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
                    CircularProgressIndicator()
                }
            }
            uiState.hasError -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        FilmHunterText(
                            text = "Error",
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FilmHunterText(
                            text = uiState.errorMessage ?: "Unknown error",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            uiState.isEmpty || uiState.currentFilm == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    FilmHunterText(
                        text = "No hay más títulos para mostrar",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
            else -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Film Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Poster Image
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                            ) {
                                val posterPath = when (val film = uiState.currentFilm) {
                                    is MovieDetailEntity -> film.posterPath
                                    is SeriesDetailEntity -> film.posterPath
                                    else -> null
                                }
                                
                                val filmTitle = when (val film = uiState.currentFilm) {
                                    is MovieDetailEntity -> film.title
                                    is SeriesDetailEntity -> film.title
                                    else -> ""
                                }
                                
                                val imageUrl = posterPath?.let { ImageUrlHelper.getOriginalUrl(it) }
                                
                                if (imageUrl != null) {
                                    // Use SubcomposeAsyncImage for instant display from cache
                                    SubcomposeAsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(imageUrl)
                                            .memoryCachePolicy(CachePolicy.ENABLED)
                                            .diskCachePolicy(CachePolicy.ENABLED)
                                            .build(),
                                        contentDescription = filmTitle,
                                        contentScale = ContentScale.FillBounds, // Fill width to prevent top cropping
                                        modifier = Modifier.fillMaxSize(),
                                        loading = {
                                            // Show nothing while loading - image should be cached
                                            Box(modifier = Modifier.fillMaxSize())
                                        },
                                        error = {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(MaterialTheme.colorScheme.surface),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                FilmHunterText(
                                                    text = "Error al cargar imagen",
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(MaterialTheme.colorScheme.surface),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        FilmHunterText(
                                            text = "Imagen no disponible",
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                            
                            // Film Info
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp)
                            ) {
                                val title = when (val film = uiState.currentFilm) {
                                    is MovieDetailEntity -> film.title
                                    is SeriesDetailEntity -> film.title
                                    else -> ""
                                }
                                
                                FilmHunterText(
                                    text = title,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                val overview = when (val film = uiState.currentFilm) {
                                    is MovieDetailEntity -> film.overview
                                    is SeriesDetailEntity -> film.overview
                                    else -> ""
                                }
                                
                                FilmHunterText(
                                    text = overview,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 4,
                                    overflow = TextOverflow.Ellipsis
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                val voteAverage = when (val film = uiState.currentFilm) {
                                    is MovieDetailEntity -> film.voteAverage
                                    is SeriesDetailEntity -> film.voteAverage
                                    else -> 0.0
                                }
                                
                                val releaseDate = when (val film = uiState.currentFilm) {
                                    is MovieDetailEntity -> film.releaseDate
                                    is SeriesDetailEntity -> film.releaseDate
                                    else -> ""
                                }
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    FilmHunterText(
                                        text = "⭐ ${String.format("%.1f", voteAverage)}",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    FilmHunterText(
                                        text = releaseDate,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Action Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 24.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Dislike Button
                        IconButton(
                            onClick = dropUnlessResumed { viewModel.onSwipe("dislike") },
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dislike",
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(32.dp))
                        
                        // Like Button
                        IconButton(
                            onClick = dropUnlessResumed { viewModel.onSwipe("like") },
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Like",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
