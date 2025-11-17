package com.darvi.filmhunter.domain.entity

data class MovieDetailEntity(
    val id: Int,
    val title: String,
    val overview: String,
    val runtime: Int? = null,
    val originalTitle: String,
    val posterPath: String? = null,
    val backdropPath: String? = null,
    val releaseDate: String,
    val voteAverage: Double,
    val voteCount: Int,
    val genres: List<MovieGenre>
)
