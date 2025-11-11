package com.darvi.filmhunter.domain.repository

import com.darvi.filmhunter.domain.entity.MovieEntity

interface MovieRepository {
    suspend fun getMoviesList(path: String): List<MovieEntity>
}