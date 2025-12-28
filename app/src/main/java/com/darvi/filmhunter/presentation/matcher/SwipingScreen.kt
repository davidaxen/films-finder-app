package com.darvi.filmhunter.presentation.matcher

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.RotateLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import coil3.compose.SubcomposeAsyncImage
import coil3.imageLoader
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.darvi.filmhunter.domain.entity.movie.MovieDetailEntity
import com.darvi.filmhunter.domain.entity.series.SeriesDetailEntity
import com.darvi.filmhunter.presentation.core.components.FilmHunterPrimaryButton
import com.darvi.filmhunter.presentation.core.components.FilmHunterSecondaryButton
import com.darvi.filmhunter.presentation.core.components.FilmHunterText
import com.darvi.filmhunter.presentation.core.model.FilmType
import com.darvi.filmhunter.presentation.core.modifiers.shimmerLoading
import com.darvi.filmhunter.presentation.core.util.ImageUrlHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun SwipingScreen(
    sessionId: String,
    onFilmInfoClick: (Int, Int) -> Unit,
    viewModel: SwipingViewModel,
    isHost: Boolean = false,
    onFinishSession: () -> Unit = {},
    onLeaveSession: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val imageLoader = context.imageLoader
    var showEndSessionDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(sessionId) {
        viewModel.setSessionId(sessionId)
    }
    
    // Load titles only once when sessionId changes and we don't have titles yet
    LaunchedEffect(sessionId, uiState.currentTitle) {
        if (uiState.currentTitle == null && !uiState.isLoading && !uiState.hasError) {
            viewModel.loadSessionTitles(sessionId)
        }
    }
    
    // Preload images for next 5 films
    LaunchedEffect(uiState.currentTitle,uiState.preloadedPosterUrls) {
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
                    
                    // Swipeable Film Card
                    var triggerSwipeLeft by remember { mutableIntStateOf(0) }
                    var triggerSwipeRight by remember { mutableIntStateOf(0) }
                    
                    // Use current film as key to reset card state when film changes
                    val currentFilmKey = remember(uiState.currentFilm) {
                        when (val film = uiState.currentFilm) {
                            is MovieDetailEntity -> film.id.toString()
                            is SeriesDetailEntity -> film.id.toString()
                            else -> null
                        }
                    }
                    
                    key(currentFilmKey) {
                        SwipeableCard(
                            onSwipeLeft = { viewModel.onSwipe("dislike") },
                            onSwipeRight = { viewModel.onSwipe("like") },
                            triggerSwipeLeft = triggerSwipeLeft > 0,
                            triggerSwipeRight = triggerSwipeRight > 0,
                            onSwipeTriggered = { direction: String ->
                                // Reset trigger after swipe is triggered
                                if (direction == "left") {
                                    triggerSwipeLeft = 0
                                } else {
                                    triggerSwipeRight = 0
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 16.dp)
                        ) { borderColor ->
                            Card(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .then(
                                        if (borderColor != null) {
                                            Modifier.border(
                                                width = 4.dp,
                                                color = borderColor,
                                                shape = RoundedCornerShape(24.dp)
                                            )
                                        } else {
                                            Modifier
                                        }
                                    ),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Poster Image
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(2f/3f)
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
                                            var isLoading by remember { mutableStateOf(true) }

                                            // Use SubcomposeAsyncImage for instant display from cache
                                            SubcomposeAsyncImage(
                                                model = ImageRequest.Builder(context)
                                                    .data(imageUrl)
                                                    .memoryCachePolicy(CachePolicy.ENABLED)
                                                    .diskCachePolicy(CachePolicy.ENABLED)
                                                    .build(),
                                                contentDescription = filmTitle,
                                                contentScale = ContentScale.Crop, // Fill width to prevent top cropping
                                                modifier = Modifier.shimmerLoading(isVisible = isLoading).matchParentSize(),
                                                onLoading = { isLoading = true },
                                                onSuccess = { isLoading = false },
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
                                            .padding(12.dp)
                                            .weight(0.30f)
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
                                            maxLines = 1,
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
                                                text = "⭐ ${String.format(Locale.getDefault(),"%.1f", voteAverage)}",
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
                        } // End of SwipeableCard content
                    } // End of key block
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Action Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Back Button
                        IconButton(
                            onClick = dropUnlessResumed { viewModel.goBack() },
                            enabled = uiState.canGoBack,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    if (uiState.canGoBack) {
                                        MaterialTheme.colorScheme.surface
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    }
                                )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.RotateLeft,
                                contentDescription = "Back",
                                tint = if (uiState.canGoBack) {
                                    Color.Blue.copy(alpha = 0.40f)
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                                },
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        // Dislike Button
                        IconButton(
                            onClick = dropUnlessResumed { triggerSwipeLeft++ },
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dislike",
                                tint = Color.Red,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        // Info Button
                        val currentFilm = uiState.currentFilm
                        val filmId = when (currentFilm) {
                            is MovieDetailEntity -> currentFilm.id
                            is SeriesDetailEntity -> currentFilm.id
                            else -> null
                        }
                        val filmType = when (currentFilm) {
                            is MovieDetailEntity -> FilmType.MOVIE.value
                            is SeriesDetailEntity -> FilmType.SERIES.value
                            else -> null
                        }
                        
                        IconButton(
                            onClick = dropUnlessResumed {
                                if (filmId != null && filmType != null) {
                                    onFilmInfoClick(filmId, filmType)
                                }
                            },
                            enabled = filmId != null && filmType != null,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = "Info",
                                tint = Color.Yellow,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))

                        // Like Button
                        IconButton(
                            onClick = dropUnlessResumed { triggerSwipeRight++ },
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Like",
                                tint = Color.Green,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        // End Session Button
                        IconButton(
                            onClick = dropUnlessResumed { showEndSessionDialog = true },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "End Session",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
        
        // End Session Dialog
        if (showEndSessionDialog) {
            if (isHost) {
                FinishSessionDialog(
                    onConfirm = {
                        showEndSessionDialog = false
                        viewModel.finishSession(
                            sessionId = sessionId,
                            onSuccess = {
                                onFinishSession()
                            },
                            onError = { }
                        )
                    },
                    onDismiss = { showEndSessionDialog = false }
                )
            } else {
                LeaveSessionDialog(
                    onConfirm = {
                        showEndSessionDialog = false
                        val userId = currentUser?.id
                        if (userId != null) {
                            viewModel.leaveSession(
                                sessionId = sessionId,
                                userId = userId,
                                onSuccess = {
                                    onLeaveSession()
                                },
                                onError = { }
                            )
                        }
                    },
                    onDismiss = { showEndSessionDialog = false }
                )
            }
        }
        
        // Match Modal
        val match = uiState.match
        if (match != null) {
            MatchModal(
                match = match,
                onDismiss = { viewModel.dismissMatch() }
            )
        }
    }
}

@Composable
private fun SwipeableCard(
    modifier: Modifier = Modifier,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    triggerSwipeLeft: Boolean = false,
    triggerSwipeRight: Boolean = false,
    onSwipeTriggered: (String) -> Unit = {},
    content: @Composable (borderColor: Color?) -> Unit
) {
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    var offsetX by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    
    // Threshold for triggering swipe (in dp, converted to pixels)
    val swipeThreshold = with(density) { 150.dp.toPx() }
    
    // Calculate rotation based on offset (max 15 degrees)
    val rotation = (offsetX / swipeThreshold) * 15f
    
    // Animate only when not dragging - use raw offset during drag for immediate response
    val animatedOffsetX = animateFloatAsState(
        targetValue = offsetX,
        animationSpec = if (isDragging) tween(0) else tween(300),
        label = "offset"
    )
    
    // Animate rotation only when not dragging
    val animatedRotation = animateFloatAsState(
        targetValue = rotation,
        animationSpec = if (isDragging) tween(0) else tween(300),
        label = "rotation"
    )
    
    // Use raw values during drag for immediate response, animated values when snapping back
    val currentOffsetX = if (isDragging) offsetX else animatedOffsetX.value
    val currentRotation = if (isDragging) rotation else animatedRotation.value
    
    // Calculate border color based on swipe direction and threshold
    val borderColor = when {
        currentOffsetX > swipeThreshold -> Color.Green // Green for like
        currentOffsetX < -swipeThreshold -> Color.Red // Red for dislike
        else -> null // No border when threshold not reached
    }
    
    // Handle programmatic swipe triggers
    LaunchedEffect(triggerSwipeLeft) {
        if (triggerSwipeLeft && !isDragging) {
            isDragging = false
            offsetX = -swipeThreshold * 2f // Animate off screen to the left
            // Wait for animation to complete before triggering swipe
            delay(300)
            onSwipeLeft()
            onSwipeTriggered("left")
        }
    }
    
    LaunchedEffect(triggerSwipeRight) {
        if (triggerSwipeRight && !isDragging) {
            isDragging = false
            offsetX = swipeThreshold * 2f // Animate off screen to the right
            // Wait for animation to complete before triggering swipe
            delay(300)
            onSwipeRight()
            onSwipeTriggered("right")
        }
    }
    
    Box(
        modifier = modifier
            .offset { IntOffset(currentOffsetX.roundToInt(), 0) }
            .graphicsLayer {
                rotationZ = currentRotation
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        isDragging = true
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                    },
                    onDragEnd = {
                        isDragging = false
                        // Check if threshold is reached
                        when {
                            offsetX > swipeThreshold -> {
                                // Swipe right - like
                                offsetX = swipeThreshold * 2f // Animate off screen
                                onSwipeRight()
                                // Reset after animation
                                coroutineScope.launch {
                                    delay(300)
                                    offsetX = 0f
                                }
                            }
                            offsetX < -swipeThreshold -> {
                                // Swipe left - dislike
                                offsetX = -swipeThreshold * 2f // Animate off screen
                                onSwipeLeft()
                                // Reset after animation
                                coroutineScope.launch {
                                    delay(300)
                                    offsetX = 0f
                                }
                            }
                            else -> {
                                // Snap back to center
                                offsetX = 0f
                            }
                        }
                    }
                )
            }
    ) {
        content(borderColor)
    }
}

@Composable
private fun MatchModal(
    match: MatchInfo,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    
    val filmTitle = when (val film = match.film) {
        is MovieDetailEntity -> film.title
        is SeriesDetailEntity -> film.title
        else -> ""
    }
    
    val posterPath = when (val film = match.film) {
        is MovieDetailEntity -> film.posterPath
        is SeriesDetailEntity -> film.posterPath
        else -> null
    }
    val posterUrl = posterPath?.let { ImageUrlHelper.getOriginalUrl(it) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            FilmHunterText(
                modifier = Modifier.fillMaxWidth(),
                text = "🎉 ¡Es un Match! 🎉",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FilmHunterText(
                    text = "Ambos han dado like a:",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Film Poster
                if (posterUrl != null) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(posterUrl)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        contentDescription = filmTitle,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                FilmHunterText(
                    text = filmTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        confirmButton = {
            FilmHunterPrimaryButton(
                text = "Continuar",
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
private fun FinishSessionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            FilmHunterText(
                text = "Finalizar Sesión",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            FilmHunterText(
                text = "¿Estás seguro de que quieres finalizar la sesión? Todos los usuarios serán redirigidos al inicio.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            FilmHunterSecondaryButton(
                text = "Finalizar",
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth()
            )
        },
        dismissButton = {
            FilmHunterPrimaryButton(
                text = "Continuar",
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
private fun LeaveSessionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            FilmHunterText(
                text = "Salir de la Sesión",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            FilmHunterText(
                text = "¿Estás seguro de que quieres salir de esta sesión?",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            FilmHunterSecondaryButton(
                text = "Salir",
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth()
            )
        },
        dismissButton = {
            FilmHunterPrimaryButton(
                text = "Continuar",
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp)
    )
}
