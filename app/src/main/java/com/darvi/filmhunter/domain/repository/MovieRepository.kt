package com.darvi.filmhunter.domain.repository

import com.darvi.filmhunter.domain.entity.MovieEntity

interface MovieRepository {
    suspend fun getPopularMovies(): List<MovieEntity>
}