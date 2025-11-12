package com.darvi.filmhunter.data.repository

import com.darvi.filmhunter.data.model.toDomain
import com.darvi.filmhunter.data.datasource.api.MovieApiService
import com.darvi.filmhunter.domain.entity.MovieEntity
import com.darvi.filmhunter.domain.repository.MovieRepository
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(private val api: MovieApiService): MovieRepository {
    override suspend fun getMoviesList(path: String): List<MovieEntity> {
        return api.getMoviesList(path = path).results.map {
            it.toDomain()
        }
    }

    override suspend fun getMoviesByTitle(query: String): List<MovieEntity> {
        return api.getMoviesByTitle(query).results.map {
            it.toDomain()
        }
    }
}