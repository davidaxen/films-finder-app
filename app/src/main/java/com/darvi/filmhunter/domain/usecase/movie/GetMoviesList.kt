package com.darvi.filmhunter.domain.usecase.movie

import com.darvi.filmhunter.domain.entity.movie.MovieEntity
import com.darvi.filmhunter.domain.repository.MovieRepository
import javax.inject.Inject

class GetMoviesList @Inject constructor(private val repository: MovieRepository) {
    suspend operator fun invoke(listType: String): List<MovieEntity> {
        return repository.getMoviesList(listType)
    }
}