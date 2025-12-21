package com.darvi.filmhunter.data.datasource

import com.darvi.filmhunter.data.model.supabase.FilmsSavedDTO
import com.darvi.filmhunter.data.model.supabase.SessionDTO
import com.darvi.filmhunter.data.model.supabase.SessionMemberDTO

interface SupabaseDatabaseDataSource {
    suspend fun saveFilm(filmId: Int, filmType: String)
    suspend fun removeSavedFilm(filmId: Int, filmType: String)
    suspend fun isFilmSaved(filmId: Int, filmType: String): Boolean
    suspend fun getSavedFilmsId(filmType: String): List<FilmsSavedDTO>
    suspend fun createSession(session: SessionDTO): SessionDTO
    suspend fun addSessionMember(member: SessionMemberDTO): SessionMemberDTO
}