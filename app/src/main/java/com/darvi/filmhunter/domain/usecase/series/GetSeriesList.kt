package com.darvi.filmhunter.domain.usecase.series

import com.darvi.filmhunter.domain.entity.series.SeriesEntity
import com.darvi.filmhunter.domain.repository.SeriesRepository
import javax.inject.Inject

class GetSeriesList @Inject constructor(private val repository: SeriesRepository) {
    suspend operator fun invoke(listType: String, page: Int = 1): List<SeriesEntity> {
        return repository.getSeriesList(listType, page)
    }
}