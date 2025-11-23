package com.darvi.filmhunter.domain.usecase.series

import com.darvi.filmhunter.domain.entity.series.SeasonDetailEntity
import com.darvi.filmhunter.domain.repository.SeriesRepository
import javax.inject.Inject

class GetSeasonDetail @Inject constructor(private val seriesRepository: SeriesRepository) {
    suspend operator fun invoke(seriesId: Int, seasonNumber: Int): SeasonDetailEntity {
        return seriesRepository.getSeriesSeasonDetail(seriesId = seriesId, seasonNumber = seasonNumber)
    }
}