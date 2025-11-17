package com.darvi.filmhunter.domain.entity

data class SeriesEntity(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val releaseDate: String,
    val voteAverage: Double
)
