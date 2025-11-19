package com.darvi.filmhunter.presentation.detail.model

import com.darvi.filmhunter.domain.entity.movie.MovieDetailEntity
import com.darvi.filmhunter.domain.entity.movie.MovieGenre
import com.darvi.filmhunter.domain.entity.series.SeriesDetailEntity
import com.darvi.filmhunter.domain.entity.series.SeriesSeasonEntity
import com.darvi.filmhunter.domain.entity.WatchProviderEntity
import com.darvi.filmhunter.presentation.core.model.FilmType

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
    val genres: List<MovieGenre>,
    val type: FilmType,
    val watchProviders: List<WatchProviderEntity>,
    val seasons: List<SeriesSeasonEntity> = emptyList()
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
            watchProviders = emptyList()
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
        genres = genres,
        type = FilmType.MOVIE,
        watchProviders = watchProviders,
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
        genres = genres,
        type = FilmType.SERIES,
        seasons = seasons,
        watchProviders = watchProviders
    )
}


