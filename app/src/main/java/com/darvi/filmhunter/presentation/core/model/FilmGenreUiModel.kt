package com.darvi.filmhunter.presentation.core.model

import com.darvi.filmhunter.domain.entity.movie.MovieGenre
import com.darvi.filmhunter.domain.entity.series.SeriesGenre

data class FilmGenreUiModel(
    val id: Int,
    val displayName: String,
    val genreType: FilmType
)

fun MovieGenre.toUiModel(): FilmGenreUiModel {
    return FilmGenreUiModel(
        id = id,
        displayName = displayName,
        genreType = FilmType.MOVIE
    )
}

fun SeriesGenre.toUiModel(): FilmGenreUiModel {
    return FilmGenreUiModel(
        id = id,
        displayName = displayName,
        genreType = FilmType.SERIES
    )
}