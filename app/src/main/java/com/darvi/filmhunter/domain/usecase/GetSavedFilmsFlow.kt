package com.darvi.filmhunter.domain.usecase

import com.darvi.filmhunter.domain.entity.movie.MovieDetailEntity
import com.darvi.filmhunter.domain.entity.series.SeriesDetailEntity
import com.darvi.filmhunter.domain.repository.MovieRepository
import com.darvi.filmhunter.domain.repository.SeriesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetSavedFilmsFlow @Inject constructor(
    private val movieRepository: MovieRepository,
    private val seriesRepository: SeriesRepository,
) {
    operator fun invoke(): Flow<Pair<List<MovieDetailEntity>, List<SeriesDetailEntity>>> {
        return combine(
            movieRepository.getSavedMoviesFlow(),
            seriesRepository.getSavedSeriesFlow()
        ) { movies, series ->
            movies to series
        }
    }
}