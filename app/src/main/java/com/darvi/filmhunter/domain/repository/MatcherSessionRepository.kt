package com.darvi.filmhunter.domain.repository

import com.darvi.filmhunter.domain.entity.MatcherSessionEntity
import kotlinx.coroutines.flow.Flow

interface MatcherSessionRepository {
    suspend fun createSession(
        code: String,
        createdBy: String,
        filters: Map<String, Any>
    ): Result<MatcherSessionEntity>
    
    suspend fun cancelSession(sessionId: String): Result<Unit>
    
    suspend fun joinSessionByCode(code: String, currentUserId: String): Result<MatcherSessionEntity>
    
    suspend fun initiateSession(sessionId: String): Result<Unit>
    
    suspend fun subscribeToSessionMembers(sessionId: String, currentUserId: String): Flow<String>

    suspend fun unsubscribeFromSessionMembers(sessionId: String)

    suspend fun subscribeToSessionStatus(sessionId: String): Flow<String>

    suspend fun unsubscribeFromSessionStatus(sessionId: String)
    
    suspend fun unsubscribeAllSessionListeners(sessionId: String)
    
    suspend fun insertSessionTitles(sessionId: String, titles: List<Pair<Long, String>>): Result<Unit>
    
    suspend fun getSessionTitles(sessionId: String): Result<List<Pair<Long, String>>>
    
    suspend fun insertSessionSwipe(sessionId: String, userId: String, tmdbId: Long, mediaType: String, vote: String): Result<Unit>
    
    suspend fun getSessionMembers(sessionId: String): Result<List<String>> // Returns list of user IDs
    
    suspend fun getSessionSwipes(sessionId: String): Result<List<com.darvi.filmhunter.data.model.supabase.SessionSwipeDTO>>
    
    suspend fun subscribeToSessionSwipes(sessionId: String): Flow<com.darvi.filmhunter.data.model.supabase.SessionSwipeDTO>
    
    suspend fun unsubscribeFromSessionSwipes(sessionId: String)
}

