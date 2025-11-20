package com.darvi.filmhunter.domain.usecase.movie

import com.darvi.filmhunter.domain.repository.MovieRepository
import javax.inject.Inject

class SaveMovie @Inject constructor(private val repository: MovieRepository) {
    suspend operator fun invoke(filmId: Int) {
        repository.saveFilm(filmId)
    }
}