package com.darvi.filmhunter.presentation.list.model

import com.darvi.filmhunter.domain.entity.movie.MovieEntity
import com.darvi.filmhunter.domain.entity.series.SeriesEntity
import com.darvi.filmhunter.presentation.core.model.FilmType

data class FilmUiModel(
    val id: Int,
    val title: String,
    val description: String,
    val posterPath: String?,
    val type: FilmType
)

fun MovieEntity.toUiModel(): FilmUiModel {
    return FilmUiModel(
        id = id,
        title = title,
        description = overview,
        posterPath = posterPath,
        type = FilmType.MOVIE
    )
}

fun SeriesEntity.toUiModel(): FilmUiModel {
    return FilmUiModel(
        id = id,
        title = title,
        description = overview,
        posterPath = posterPath,
        type = FilmType.SERIES
    )
}