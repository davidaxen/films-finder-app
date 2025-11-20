package com.darvi.filmhunter.data.datasource

import com.darvi.filmhunter.data.model.supabase.FilmsSavedDTO
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Inject

class SupabaseDatabaseDataSourceImpl @Inject constructor(
    private val database: Postgrest
): SupabaseDatabaseDataSource {
    override suspend fun saveFilm(filmId: Int, filmType: String) {
        val filmToSave = FilmsSavedDTO(
            filmId = filmId,
            filmType = filmType
        )
        database.from("saved_films").insert(filmToSave)
    }

    override suspend fun removeSavedFilm(filmId: Int) {
    }
}