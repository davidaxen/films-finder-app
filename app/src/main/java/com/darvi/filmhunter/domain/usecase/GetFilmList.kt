package com.darvi.filmhunter.domain.usecase

import com.darvi.filmhunter.domain.entity.MovieEntity
import com.darvi.filmhunter.domain.repository.MovieRepository
import javax.inject.Inject

class GetFilmList @Inject constructor(private val repository: MovieRepository) {
    suspend operator fun invoke(listType: String): List<MovieEntity> {
        return repository.getMoviesList(listType)
    }
}