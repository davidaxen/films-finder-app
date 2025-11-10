package com.darvi.filmhunter.data.datasource.api

import com.darvi.filmhunter.data.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("language") language: String = "es-ES"
    ): MovieResponse
}