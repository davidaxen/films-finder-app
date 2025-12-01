package com.darvi.filmhunter.presentation.search.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.dropUnlessResumed
import com.darvi.filmhunter.presentation.core.components.FilmHunterText

@Composable
fun SearchItemsHeader(modifier: Modifier = Modifier, title: String, onClick: () -> Unit) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilmHunterText(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )

        FilmHunterText(
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() } ,
                indication = null,
                onClick = dropUnlessResumed { onClick() }
            ),
            text = "Ver todo",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF39B3E3)
        )
    }
}