package com.darvi.filmhunter.domain.usecase.detail

import com.darvi.filmhunter.domain.entity.SeriesDetailEntity
import com.darvi.filmhunter.domain.repository.SeriesRepository
import javax.inject.Inject

class GetSeriesById  @Inject constructor(private val seriesRepository: SeriesRepository) {
    suspend operator fun invoke(id: Int): SeriesDetailEntity {
        return seriesRepository.getSeriesById(id)
    }
}