package com.darvi.filmhunter.domain.repository

import com.darvi.filmhunter.domain.entity.movie.MovieDetailEntity
import com.darvi.filmhunter.domain.entity.movie.MovieEntity

interface MovieRepository {
    suspend fun getMoviesList(path: String): List<MovieEntity>
    suspend fun getMoviesByTitle(query: String, page: Int): List<MovieEntity>
    suspend fun getMoviesByGenres(genres: String, page: Int): List<MovieEntity>
    suspend fun getMovieById(id: Int): MovieDetailEntity
    suspend fun saveFilm(filmId: Int)
    suspend fun removeSavedFilm(filmId: Int)
    suspend fun getSavedFilms(): List<MovieDetailEntity>
}