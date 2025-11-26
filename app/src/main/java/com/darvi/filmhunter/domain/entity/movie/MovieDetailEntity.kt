package com.darvi.filmhunter.domain.entity.movie

import com.darvi.filmhunter.domain.entity.WatchProviderEntity

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
    val isSaved: Boolean = false,
    val savedAt: Long? = null,
    val genres: List<MovieGenre>,
    val watchProviders: List<WatchProviderEntity>,
    val recommendations: List<MovieEntity>,
)
