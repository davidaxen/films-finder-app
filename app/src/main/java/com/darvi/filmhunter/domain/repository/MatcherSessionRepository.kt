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
}

