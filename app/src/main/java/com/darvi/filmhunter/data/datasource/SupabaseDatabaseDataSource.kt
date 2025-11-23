package com.darvi.filmhunter.data.datasource

interface SupabaseDatabaseDataSource {
    suspend fun saveFilm(filmId: Int, filmType: String)
    suspend fun removeSavedFilm(filmId: Int)
    suspend fun isFilmSaved(filmId: Int, filmType: String): Boolean
}