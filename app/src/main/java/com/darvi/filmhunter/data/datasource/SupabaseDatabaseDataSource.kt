package com.darvi.filmhunter.data.datasource

import com.darvi.filmhunter.data.model.supabase.FilmsSavedDTO
import com.darvi.filmhunter.data.model.supabase.SessionDTO
import com.darvi.filmhunter.data.model.supabase.SessionMemberDTO
import com.darvi.filmhunter.data.model.supabase.SessionSwipeDTO
import com.darvi.filmhunter.data.model.supabase.SessionTitleDTO
import kotlinx.coroutines.flow.Flow

interface SupabaseDatabaseDataSource {
    suspend fun getUserNameById(id: String): String?
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
    suspend fun leaveSession(sessionId: String, userId: String)
    suspend fun subscribeToSessionMembers(sessionId: String, currentUserId: String): Flow<String>
    suspend fun unsubscribeFromSessionMembers(sessionId: String)
    suspend fun subscribeToSessionStatus(sessionId: String): Flow<String>
    suspend fun unsubscribeFromSessionStatus(sessionId: String)
    suspend fun unsubscribeAllSessionListeners(sessionId: String)
    suspend fun insertSessionTitles(titles: List<SessionTitleDTO>)
    suspend fun getSessionTitles(sessionId: String): List<SessionTitleDTO>
    suspend fun insertSessionSwipe(swipe: SessionSwipeDTO)
    suspend fun deleteSessionSwipe(sessionId: String, userId: String, tmdbId: Long, mediaType: String)
    suspend fun getSessionMembers(sessionId: String): List<SessionMemberDTO>
    suspend fun getSessionSwipes(sessionId: String): List<SessionSwipeDTO>
    suspend fun subscribeToSessionSwipes(sessionId: String): Flow<SessionSwipeDTO>
    suspend fun unsubscribeFromSessionSwipes(sessionId: String)
    suspend fun getUserSessions(userId: String): List<SessionDTO>
    suspend fun getMatchedFilms(sessionId: String): List<Pair<Long, String>> // Returns list of (tmdbId, mediaType) pairs
}