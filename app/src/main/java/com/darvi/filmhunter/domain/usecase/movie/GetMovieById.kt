package com.darvi.filmhunter.domain.usecase.movie

import com.darvi.filmhunter.domain.entity.movie.MovieDetailEntity
import com.darvi.filmhunter.domain.repository.MovieRepository
import javax.inject.Inject

class GetMovieById  @Inject constructor(private val movieRepository: MovieRepository) {
    suspend operator fun invoke(id: Int): MovieDetailEntity {
        return movieRepository.getMovieById(id)
    }
}