package com.darvi.filmhunter.domain.usecase.search

import com.darvi.filmhunter.domain.entity.MovieEntity
import com.darvi.filmhunter.domain.repository.MovieRepository
import javax.inject.Inject

class SearchMoviesByTitle @Inject constructor(private val movieRepository: MovieRepository) {
    suspend operator fun invoke(query: String, page: Int = 1): List<MovieEntity> {
        return movieRepository.getMoviesByTitle(query, page)
    }
}