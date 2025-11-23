package com.darvi.filmhunter.presentation.detail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.darvi.filmhunter.presentation.core.components.FilmHunterText
import com.darvi.filmhunter.presentation.core.model.FilmType
import com.darvi.filmhunter.presentation.core.util.RuntimeFormat

@Composable
fun TitleSection(
    modifier: Modifier = Modifier,
    title: String,
    releaseDate: String,
    runtime: Int? = null,
    filmType: FilmType? = null,
    onSaveClick: (() -> Unit)? = null,
    isSaved: Boolean?,
    rating: Double,
    voteCount: Int?,
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 8.dp)
        ) {
            FilmHunterText(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (releaseDate.isNotEmpty()) {
                    FilmHunterText(
                        text = releaseDate,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                }

                runtime?.let {
                    FilmHunterText(
                        text = " • ${RuntimeFormat.formatDuration(it)}",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                }
                if (filmType != null) {
                    val typeTitle = if (filmType == FilmType.MOVIE) "Película"
                    else "Serie"

                    FilmHunterText(
                        text = " • $typeTitle",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            RatingRow(rating = rating, voteCount = voteCount)
        }

        if (isSaved != null) {
            Icon(
                imageVector = if (isSaved) Icons.Filled.BookmarkAdded
                                else Icons.Outlined.BookmarkAdd,
                tint = if (isSaved) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .size(30.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onSaveClick?.invoke()
                    },
                contentDescription = "Agregar a favoritos"
            )
        }
    }
}