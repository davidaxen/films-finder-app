package com.darvi.filmhunter.domain.usecase.series

import com.darvi.filmhunter.domain.entity.series.SeriesEntity
import com.darvi.filmhunter.domain.repository.SeriesRepository
import javax.inject.Inject

class SearchSeriesByGenres @Inject constructor(private val seriesRepository: SeriesRepository) {
    suspend operator fun invoke(genres: String, page: Int = 1): List<SeriesEntity> {
        return seriesRepository.getSeriesByGenres(genres, page)
    }
}