package com.darvi.filmhunter.data.repository

import com.darvi.filmhunter.data.model.toDomain
import com.darvi.filmhunter.data.datasource.api.ApiService
import com.darvi.filmhunter.domain.entity.MovieEntity
import com.darvi.filmhunter.domain.repository.MovieRepository
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(private val api: ApiService): MovieRepository {
    override suspend fun getPopularMovies(): List<MovieEntity> {
        return api.getPopularMovies().results.map {
            it.toDomain()
        }
    }
}