package com.darvi.filmhunter.data.datasource

import com.darvi.filmhunter.data.model.supabase.FilmsSavedDTO
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Count
import javax.inject.Inject

class SupabaseDatabaseDataSourceImpl @Inject constructor(
    private val database: Postgrest
): SupabaseDatabaseDataSource {
    override suspend fun saveFilm(filmId: Int, filmType: String) {
        val filmToSave = FilmsSavedDTO(
            filmId = filmId,
            filmType = filmType,
            createdAt = null
        )
        database.from("saved_films").insert(filmToSave)
    }

    override suspend fun removeSavedFilm(filmId: Int) {
    }

    override suspend fun isFilmSaved(filmId: Int, filmType: String): Boolean {
        val resp = database
            .from("saved_films")
            .select {
                filter {
                    eq("film_id", filmId)
                    eq("film_type", filmType)
                }
                count(Count.EXACT)
            }
            .countOrNull()

        return (resp ?: 0) > 0
    }

    override suspend fun getSavedFilmsId(filmType: String): List<FilmsSavedDTO> {
        val resp = database
            .from("saved_films")
            .select {
                filter {
                    eq("film_type", filmType)
                }
            }
            .decodeList<FilmsSavedDTO>()

        return resp
    }
}