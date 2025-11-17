package com.darvi.filmhunter.domain.entity

data class MovieEntity(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val releaseDate: String,
    val voteAverage: Double
)
