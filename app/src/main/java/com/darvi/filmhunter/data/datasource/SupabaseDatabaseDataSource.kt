package com.darvi.filmhunter.data.datasource

interface SupabaseDatabaseDataSource {
    suspend fun saveFilm(filmId: Int, filmType: String)

    suspend fun removeSavedFilm(filmId: Int)
}