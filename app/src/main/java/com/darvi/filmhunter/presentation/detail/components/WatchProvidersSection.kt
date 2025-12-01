package com.darvi.filmhunter.presentation.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darvi.filmhunter.domain.entity.WatchProviderEntity
import com.darvi.filmhunter.presentation.core.components.FilmHunterText
import com.darvi.filmhunter.presentation.core.components.WatchProviderLogo
import com.darvi.filmhunter.presentation.core.util.ImageUrlHelper

@Composable
fun WatchProvidersSection(
    providers: List<WatchProviderEntity>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        FilmHunterText(
            text = "Ver en",
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = providers,
                key = { it.id }
            ) { provider ->
                provider.logoPath?.let {
                    WatchProviderLogo(
                        name = provider.name,
                        logoUrl = ImageUrlHelper.getOriginalUrl(it)
                    )
                }
            }
        }
    }

}