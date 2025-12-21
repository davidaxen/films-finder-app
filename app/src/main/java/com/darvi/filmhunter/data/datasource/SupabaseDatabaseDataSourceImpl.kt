package com.darvi.filmhunter.data.datasource

import com.darvi.filmhunter.data.model.supabase.FilmsSavedDTO
import com.darvi.filmhunter.data.model.supabase.SessionDTO
import com.darvi.filmhunter.data.model.supabase.SessionMemberDTO
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Count
import javax.inject.Inject

class SupabaseDatabaseDataSourceImpl @Inject constructor(
    private val database: Postgrest
): SupabaseDatabaseDataSource {
    object Tables {
        const val SAVED_FILMS = "saved_films"
        const val SESSIONS = "sessions"
        const val SESSION_MEMBERS = "session_members"
    }

    override suspend fun saveFilm(filmId: Int, filmType: String) {
        val filmToSave = FilmsSavedDTO(
            filmId = filmId,
            filmType = filmType,
        )
        database.from(Tables.SAVED_FILMS).insert(filmToSave)
    }

    override suspend fun removeSavedFilm(filmId: Int, filmType: String) {
        database.from(Tables.SAVED_FILMS)
            .delete {
                filter {
                    eq("film_id", filmId)
                    eq("film_type", filmType)
                }
            }
    }

    override suspend fun isFilmSaved(filmId: Int, filmType: String): Boolean {
        val resp = database
            .from(Tables.SAVED_FILMS)
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
            .from(Tables.SAVED_FILMS)
            .select {
                filter {
                    eq("film_type", filmType)
                }
            }
            .decodeList<FilmsSavedDTO>()

        return resp
    }

    override suspend fun createSession(session: SessionDTO): SessionDTO {
        val response = database
            .from(Tables.SESSIONS)
            .insert(session) {
                select()
            }
            .decodeSingle<SessionDTO>()
        
        return response
    }

    override suspend fun addSessionMember(member: SessionMemberDTO): SessionMemberDTO {
        val response = database
            .from(Tables.SESSION_MEMBERS)
            .insert(member) {
                select()
            }
            .decodeSingle<SessionMemberDTO>()
        
        return response
    }
}