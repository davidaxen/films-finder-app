package com.darvi.filmhunter.data.datasource.api

import com.darvi.filmhunter.data.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {
    @GET("movie/{path}")
    suspend fun getMoviesList(
        @Path("path") path: String,
        @Query("language") language: String = "es-ES"
    ): MovieResponse

    @GET("search/movie")
    suspend fun getMoviesByTitle(
        @Query("query") q: String = "es-ES",
        @Query("language") language: String = "es-ES"
    ): MovieResponse
}