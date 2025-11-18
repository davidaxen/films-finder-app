package com.darvi.filmhunter.domain.repository

import com.darvi.filmhunter.domain.entity.SeriesDetailEntity
import com.darvi.filmhunter.domain.entity.SeriesEntity

interface SeriesRepository {
    suspend fun getSeriesList(path: String): List<SeriesEntity>
    suspend fun getSeriesByTitle(query: String, page: Int): List<SeriesEntity>
    suspend fun getSeriesById(id: Int): SeriesDetailEntity
}