package com.darvi.filmhunter.domain.repository

import com.darvi.filmhunter.domain.entity.movie.MovieDetailEntity
import com.darvi.filmhunter.domain.entity.movie.MovieEntity
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getSavedMoviesFlow(): Flow<List<MovieDetailEntity>>
    suspend fun getMoviesList(path: String): List<MovieEntity>
    suspend fun getMoviesByTitle(query: String, page: Int): List<MovieEntity>
    suspend fun getMoviesByGenres(genres: String, platforms: String, page: Int): List<MovieEntity>
    suspend fun getMovieById(id: Int): MovieDetailEntity
    suspend fun saveFilm(filmId: Int)
    suspend fun removeSavedFilm(filmId: Int)
    suspend fun fetchSavedMovies()
}