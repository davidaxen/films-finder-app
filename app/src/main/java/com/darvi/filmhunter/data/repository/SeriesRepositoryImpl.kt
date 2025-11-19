package com.darvi.filmhunter.data.repository

import com.darvi.filmhunter.data.datasource.api.SeriesApiService
import com.darvi.filmhunter.data.model.series.toDomain
import com.darvi.filmhunter.domain.entity.series.SeriesDetailEntity
import com.darvi.filmhunter.domain.entity.series.SeriesEntity
import com.darvi.filmhunter.domain.repository.SeriesRepository
import javax.inject.Inject

class SeriesRepositoryImpl @Inject constructor(private val api: SeriesApiService): SeriesRepository {
    override suspend fun getSeriesList(path: String): List<SeriesEntity> {
        return api.getSeriesList(path = path).results.map {
            it.toDomain()
        }
    }

    override suspend fun getSeriesByTitle(query: String, page: Int): List<SeriesEntity> {
        return api.getSeriesByTitle(q = query, page = page).results.map {
            it.toDomain()
        }
    }

    override suspend fun getSeriesById(id: Int): SeriesDetailEntity {
        return api.getSeriesById(id = id).toDomain()
    }
}