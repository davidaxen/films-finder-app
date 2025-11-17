package com.darvi.filmhunter.domain.usecase.list

import com.darvi.filmhunter.domain.entity.SeriesEntity
import com.darvi.filmhunter.domain.repository.SeriesRepository
import javax.inject.Inject

class GetSeriesList @Inject constructor(private val repository: SeriesRepository) {
    suspend operator fun invoke(listType: String): List<SeriesEntity> {
        return repository.getSeriesList(listType)
    }
}