package com.darvi.filmhunter.data.repository

import com.darvi.filmhunter.data.datasource.api.SeriesApiService
import com.darvi.filmhunter.data.model.toDomain
import com.darvi.filmhunter.domain.entity.SeriesEntity
import com.darvi.filmhunter.domain.repository.SeriesRepository
import javax.inject.Inject

class SeriesRepositoryImpl @Inject constructor(private val api: SeriesApiService): SeriesRepository {
    override suspend fun getSeriesList(path: String): List<SeriesEntity> {
        return api.getSeriesList(path = path).results.map {
            it.toDomain()
        }
    }

    override suspend fun getSeriesByTitle(query: String): List<SeriesEntity> {
        return api.getSeriesByTitle(query).results.map {
            it.toDomain()
        }
    }
}