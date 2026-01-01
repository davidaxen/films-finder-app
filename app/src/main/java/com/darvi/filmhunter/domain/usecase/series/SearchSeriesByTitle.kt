package com.darvi.filmhunter.domain.usecase.series

import com.darvi.filmhunter.domain.entity.series.SeriesEntity
import com.darvi.filmhunter.domain.repository.SeriesRepository
import javax.inject.Inject

class SearchSeriesByTitle @Inject constructor(private val seriesRepository: SeriesRepository) {
    suspend operator fun invoke(query: String, page: Int = 1): List<SeriesEntity> {
        return seriesRepository.getSeriesByTitle(query, page)
    }
}