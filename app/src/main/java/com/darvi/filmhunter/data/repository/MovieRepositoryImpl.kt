package com.darvi.filmhunter.data.repository

import com.darvi.filmhunter.data.datasource.SupabaseDatabaseDataSource
import com.darvi.filmhunter.data.datasource.api.MovieApiService
import com.darvi.filmhunter.data.model.movie.toDomain
import com.darvi.filmhunter.domain.entity.movie.MovieDetailEntity
import com.darvi.filmhunter.domain.entity.movie.MovieEntity
import com.darvi.filmhunter.domain.repository.MovieRepository
import com.darvi.filmhunter.presentation.core.model.FilmType
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val api: MovieApiService,
    private val database: SupabaseDatabaseDataSource
): MovieRepository {
    override suspend fun getMoviesList(path: String): List<MovieEntity> {
        return api.getMoviesList(path = path).results.map {
            it.toDomain()
        }
    }

    override suspend fun getMoviesByTitle(query: String, page: Int): List<MovieEntity> {
        return api.getMoviesByTitle(q = query, page = page).results.map {
            it.toDomain()
        }
    }

    override suspend fun getMovieById(id: Int): MovieDetailEntity {
        return api.getMovieById(id = id).toDomain()
    }

    override suspend fun saveFilm(filmId: Int) {
        database.saveFilm(filmId, FilmType.MOVIE.name)
    }
}