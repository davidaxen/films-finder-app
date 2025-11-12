package com.darvi.filmhunter.data.datasource.api

import com.darvi.filmhunter.data.model.SeriesResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SeriesApiService {
    @GET("tv/{path}")
    suspend fun getSeriesList(
        @Path("path") path: String,
        @Query("language") language: String = "es-ES"
    ): SeriesResponse
}