package com.darvi.filmhunter.presentation.core.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FilmHunterCircularProgress(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier.size(60.dp),
        strokeWidth = 4.dp
    )
}