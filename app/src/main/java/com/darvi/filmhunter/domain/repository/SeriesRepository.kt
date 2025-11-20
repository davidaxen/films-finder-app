package com.darvi.filmhunter.domain.repository

import com.darvi.filmhunter.domain.entity.series.SeasonDetailEntity
import com.darvi.filmhunter.domain.entity.series.SeriesDetailEntity
import com.darvi.filmhunter.domain.entity.series.SeriesEntity

interface SeriesRepository {
    suspend fun getSeriesList(path: String): List<SeriesEntity>
    suspend fun getSeriesByTitle(query: String, page: Int): List<SeriesEntity>
    suspend fun getSeriesById(id: Int): SeriesDetailEntity
    suspend fun getSeriesSeasonDetail(seriesId: Int, seasonNumber: Int): SeasonDetailEntity
    suspend fun saveFilm(filmId: Int)
}