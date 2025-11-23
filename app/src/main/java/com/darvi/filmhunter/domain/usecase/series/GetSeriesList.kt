package com.darvi.filmhunter.domain.usecase.series

import com.darvi.filmhunter.domain.entity.series.SeriesEntity
import com.darvi.filmhunter.domain.repository.SeriesRepository
import javax.inject.Inject

class GetSeriesList @Inject constructor(private val repository: SeriesRepository) {
    suspend operator fun invoke(listType: String): List<SeriesEntity> {
        return repository.getSeriesList(listType)
    }
}