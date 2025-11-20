package com.darvi.filmhunter.domain.entity.series

import com.darvi.filmhunter.domain.entity.WatchProviderEntity

data class SeasonDetailEntity(
    val title: String,
    val description: String,
    val posterPath: String?,
    val episodes: List<EpisodeEntity>,
    val voteAverage: Double,
    val watchProviders: List<WatchProviderEntity>,
    val releaseDate: String,
) {
    companion object {
        fun empty(): SeasonDetailEntity {
            return SeasonDetailEntity(
                title = "",
                description = "",
                posterPath = null,
                episodes = emptyList(),
                voteAverage = 0.0,
                releaseDate = "",
                watchProviders = emptyList()
            )
        }
    }
}


