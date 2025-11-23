package com.darvi.filmhunter.domain.usecase.movie

import com.darvi.filmhunter.domain.entity.movie.MovieEntity
import com.darvi.filmhunter.domain.repository.MovieRepository
import javax.inject.Inject

class SearchMoviesByGenres @Inject constructor(private val movieRepository: MovieRepository) {
    suspend operator fun invoke(genres: String, page: Int = 1): List<MovieEntity> {
        return movieRepository.getMoviesByGenres(genres, page)
    }
}