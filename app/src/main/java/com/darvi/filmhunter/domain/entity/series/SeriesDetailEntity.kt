package com.darvi.filmhunter.domain.entity.series

import com.darvi.filmhunter.domain.entity.WatchProviderEntity
import com.darvi.filmhunter.domain.entity.movie.MovieGenre

data class SeriesDetailEntity(
    val id: Int,
    val title: String,
    val overview: String,
    val originalTitle: String,
    val posterPath: String? = null,
    val backdropPath: String? = null,
    val releaseDate: String,
    val voteAverage: Double,
    val voteCount: Int,
    val genres: List<MovieGenre>,
    val seasons: List<SeriesSeasonEntity>,
    val watchProviders: List<WatchProviderEntity>,
    val recommendations: List<SeriesEntity>,
)

data class SeriesSeasonEntity(
    val id: Int,
    val name: String,
    val seasonNumber: Int,
    val episodeCount: Int,
    val posterPath: String? = null
)