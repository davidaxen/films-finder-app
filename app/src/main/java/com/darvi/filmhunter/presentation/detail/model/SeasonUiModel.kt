package com.darvi.filmhunter.presentation.detail.model

import com.darvi.filmhunter.domain.entity.SeriesSeasonEntity

data class SeasonUiModel(
    val id: Int,
    val name: String,
    val seasonNumber: Int,
    val episodeCount: Int,
    val posterPath: String? = null
)

fun SeriesSeasonEntity.toUiModel(): SeasonUiModel {
    return SeasonUiModel(
        id = id,
        name = name,
        seasonNumber = seasonNumber,
        episodeCount = episodeCount,
        posterPath = posterPath
    )
}