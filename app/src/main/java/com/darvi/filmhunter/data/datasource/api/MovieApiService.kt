package com.darvi.filmhunter.data.datasource.api

import com.darvi.filmhunter.data.model.MovieDetailResponse
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
        @Query("query") q: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "es-ES"
    ): MovieResponse

    @GET("movie/{id}")
    suspend fun getMovieById(
        @Path("id") id: Int,
        @Query("language") language: String = "es-ES"
    ): MovieDetailResponse
}