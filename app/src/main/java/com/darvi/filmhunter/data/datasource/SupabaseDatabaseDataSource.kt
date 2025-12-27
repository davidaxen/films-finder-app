package com.darvi.filmhunter.data.datasource

import com.darvi.filmhunter.data.model.supabase.FilmsSavedDTO
import com.darvi.filmhunter.data.model.supabase.SessionDTO
import com.darvi.filmhunter.data.model.supabase.SessionMemberDTO
import kotlinx.coroutines.flow.Flow

interface SupabaseDatabaseDataSource {
    suspend fun saveFilm(filmId: Int, filmType: String)
    suspend fun removeSavedFilm(filmId: Int, filmType: String)
    suspend fun isFilmSaved(filmId: Int, filmType: String): Boolean
    suspend fun getSavedFilmsId(filmType: String): List<FilmsSavedDTO>
    suspend fun createSession(session: SessionDTO): SessionDTO
    suspend fun addSessionMember(member: SessionMemberDTO): SessionMemberDTO
    suspend fun cancelSession(sessionId: String)
    suspend fun joinSessionByCode(code: String): String
    suspend fun getSessionById(sessionId: String): SessionDTO
    suspend fun updateSessionStatus(sessionId: String, status: String)
    suspend fun subscribeToSessionMembers(sessionId: String, currentUserId: String): Flow<String>
    suspend fun unsubscribeFromSessionMembers(sessionId: String)
    suspend fun subscribeToSessionStatus(sessionId: String): Flow<String>
    suspend fun unsubscribeFromSessionStatus(sessionId: String)
    suspend fun unsubscribeAllSessionListeners(sessionId: String)
}