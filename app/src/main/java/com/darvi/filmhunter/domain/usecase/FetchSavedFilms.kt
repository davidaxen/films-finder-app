package com.darvi.filmhunter.domain.usecase

import com.darvi.filmhunter.domain.repository.MovieRepository
import com.darvi.filmhunter.domain.repository.SeriesRepository
import javax.inject.Inject

class FetchSavedFilms @Inject constructor(
    private val movieRepository: MovieRepository,
    private val seriesRepository: SeriesRepository,
) {
    suspend operator fun invoke() {
        movieRepository.fetchSavedMovies()
        seriesRepository.fetchSavedSeries()
    }
}