package com.darvi.filmhunter.data.repository

import com.darvi.filmhunter.data.datasource.api.ApiService
import com.darvi.filmhunter.data.model.toDomain
import com.darvi.filmhunter.domain.entity.SeriesEntity
import com.darvi.filmhunter.domain.repository.SeriesRepository
import javax.inject.Inject

class SeriesRepositoryImpl @Inject constructor(private val api: ApiService): SeriesRepository {
    override suspend fun getSeriesList(path: String): List<SeriesEntity> {
        return api.getSeriesList(path = path).results.map {
            it.toDomain()
        }
    }
}