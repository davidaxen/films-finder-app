package com.darvi.filmhunter.presentation.core.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun WatchProviderLogo(
    name: String,
    logoUrl: String,
    size: Dp = 50.dp
) {
    Box(modifier = Modifier.size(size)) {
        AsyncImage(
            model = logoUrl,
            contentDescription = name,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
    }
}