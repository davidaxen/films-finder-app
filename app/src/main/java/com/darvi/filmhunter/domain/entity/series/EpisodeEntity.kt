package com.darvi.filmhunter.domain.entity.series

data class EpisodeEntity(
    val title: String,
    val overview: String,
    val runtime: Int? = null,
    val episodeNumber: Int,
    val voteAverage: Double,
    val voteCount: Int,
    val releaseDate: String,
    val picturePath: String?,
)