package com.darvi.filmhunter.data.datasource

import com.darvi.filmhunter.data.model.supabase.FilmsSavedDTO

interface SupabaseDatabaseDataSource {
    suspend fun saveFilm(filmId: Int, filmType: String)
    suspend fun removeSavedFilm(filmId: Int, filmType: String)
    suspend fun isFilmSaved(filmId: Int, filmType: String): Boolean
    suspend fun getSavedFilmsId(filmType: String): List<FilmsSavedDTO>
}