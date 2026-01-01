package com.darvi.filmhunter.domain.usecase.series

import com.darvi.filmhunter.domain.repository.SeriesRepository
import javax.inject.Inject

class RemoveSavedSeries @Inject constructor(private val repository: SeriesRepository) {
    suspend operator fun invoke(filmId: Int) {
        repository.removeSavedFilm(filmId)
    }
}