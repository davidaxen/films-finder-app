package com.darvi.filmhunter.presentation.core.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign

@Composable
fun FilmHunterText(
    modifier: Modifier = Modifier,
    text: String,
    textAlign: TextAlign? = null,
    color: Color = MaterialTheme.colorScheme.onBackground,
    style: TextStyle = MaterialTheme.typography.bodyMedium
) {
    Text(text = text, modifier = modifier, color = color, style = style, textAlign = textAlign)
}