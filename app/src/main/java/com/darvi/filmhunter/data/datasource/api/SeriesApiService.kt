package com.darvi.filmhunter.data.datasource.api

import com.darvi.filmhunter.data.model.series.SeriesDetailResponse
import com.darvi.filmhunter.data.model.series.SeriesResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SeriesApiService {
    @GET("tv/{path}")
    suspend fun getSeriesList(
        @Path("path") path: String,
        @Query("language") language: String = "es-ES"
    ): SeriesResponse

    @GET("search/tv")
    suspend fun getSeriesByTitle(
        @Query("query") q: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "es-ES"
    ): SeriesResponse

    @GET("tv/{id}")
    suspend fun getSeriesById(
        @Path("id") id: Int,
        @Query("append_to_response") appendToResponse: String = "watch/providers,recommendations",
        @Query("language") language: String = "es-ES"
    ): SeriesDetailResponse
}