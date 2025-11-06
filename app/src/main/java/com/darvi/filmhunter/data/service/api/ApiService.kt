package com.darvi.filmhunter.data.service.api

import com.darvi.filmhunter.data.model.MovieResponse
import retrofit2.http.GET

interface ApiService {

    @GET("movie/popular")
    suspend fun getPopularMovies(): MovieResponse
}