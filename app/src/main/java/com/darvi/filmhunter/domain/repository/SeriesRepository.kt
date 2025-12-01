package com.darvi.filmhunter.domain.repository

import com.darvi.filmhunter.domain.entity.series.SeasonDetailEntity
import com.darvi.filmhunter.domain.entity.series.SeriesDetailEntity
import com.darvi.filmhunter.domain.entity.series.SeriesEntity
import kotlinx.coroutines.flow.Flow

interface SeriesRepository {
    fun getSavedSeriesFlow(): Flow<List<SeriesDetailEntity>>
    suspend fun getSeriesList(path: String): List<SeriesEntity>
    suspend fun getSeriesByTitle(query: String, page: Int): List<SeriesEntity>
    suspend fun getSeriesByGenres(genres: String, page: Int): List<SeriesEntity>
    suspend fun getSeriesById(id: Int): SeriesDetailEntity
    suspend fun getSeriesSeasonDetail(seriesId: Int, seasonNumber: Int): SeasonDetailEntity
    suspend fun saveFilm(filmId: Int)
    suspend fun removeSavedFilm(filmId: Int)
    suspend fun fetchSavedSeries()
}