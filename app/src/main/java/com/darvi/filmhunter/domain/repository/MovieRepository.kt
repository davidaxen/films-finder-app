package com.darvi.filmhunter.domain.repository

import com.darvi.filmhunter.domain.entity.MovieDetailEntity
import com.darvi.filmhunter.domain.entity.MovieEntity

interface MovieRepository {
    suspend fun getMoviesList(path: String): List<MovieEntity>
    suspend fun getMoviesByTitle(query: String, page: Int): List<MovieEntity>
    suspend fun getMovieById(id: Int): MovieDetailEntity
}