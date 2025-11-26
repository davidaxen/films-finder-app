package com.darvi.filmhunter.domain.usecase

import com.darvi.filmhunter.domain.entity.movie.MovieDetailEntity
import com.darvi.filmhunter.domain.entity.series.SeriesDetailEntity
import com.darvi.filmhunter.domain.repository.MovieRepository
import com.darvi.filmhunter.domain.repository.SeriesRepository
import javax.inject.Inject

class GetSavedFilms @Inject constructor(
    private val movieRepository: MovieRepository,
    private val seriesRepository: SeriesRepository,
) {
    suspend operator fun invoke(): Pair<List<MovieDetailEntity>, List<SeriesDetailEntity>> {
        return Pair(
            first = movieRepository.getSavedFilms(),
            second = seriesRepository.getSavedFilms()
        )
    }
}