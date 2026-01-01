package com.darvi.filmhunter.presentation.core.model

import com.darvi.filmhunter.domain.entity.WatchProviderEntity
import com.darvi.filmhunter.domain.entity.movie.MovieDetailEntity
import com.darvi.filmhunter.domain.entity.series.SeriesDetailEntity
import com.darvi.filmhunter.domain.entity.series.SeriesSeasonEntity

data class FilmDetailUiModel(
    val id: Int,
    val title: String,
    val description: String,
    val runtime: Int? = null,
    val originalTitle: String,
    val posterPath: String? = null,
    val backdropPath: String? = null,
    val year: String,
    val rating: Double,
    val voteCount: Int,
    val genres: List<FilmGenreUiModel>,
    val type: FilmType,
    val isSaved: Boolean = false,
    val savedAt: Long? = null,
    val watchProviders: List<WatchProviderEntity>,
    val seasons: List<SeriesSeasonEntity> = emptyList(),
    val recommendations: List<FilmUiModel>
) {
    companion object {
        fun empty() = FilmDetailUiModel(
            id = 0,
            title = "",
            description = "",
            runtime = null,
            originalTitle = "",
            posterPath = null,
            backdropPath = null,
            year = "",
            rating = 0.0,
            voteCount = 0,
            genres = emptyList(),
            type = FilmType.MOVIE,
            seasons = emptyList(),
            watchProviders = emptyList(),
            recommendations = emptyList()
        )
    }
}

fun MovieDetailEntity.toUiModel(): FilmDetailUiModel {
    return FilmDetailUiModel(
        id = id,
        title = title,
        description = overview,
        runtime = runtime,
        originalTitle = originalTitle,
        posterPath = posterPath,
        backdropPath = backdropPath,
        year = releaseDate.take(4),
        rating = voteAverage,
        voteCount = voteCount,
        genres = genres.map { it.toUiModel() },
        type = FilmType.MOVIE,
        isSaved = isSaved,
        savedAt = savedAt,
        watchProviders = watchProviders,
        recommendations = recommendations.map { it.toUiModel() }
    )
}

fun SeriesDetailEntity.toUiModel(): FilmDetailUiModel{
    return FilmDetailUiModel(
        id = id,
        title = title,
        description = overview,
        originalTitle = originalTitle,
        posterPath = posterPath,
        backdropPath = backdropPath,
        year = releaseDate.take(4),
        rating = voteAverage,
        voteCount = voteCount,
        genres = genres.map { it.toUiModel() },
        type = FilmType.SERIES,
        isSaved = isSaved,
        savedAt = savedAt,
        seasons = seasons,
        watchProviders = watchProviders,
        recommendations = recommendations.map { it.toUiModel() }
    )
}


