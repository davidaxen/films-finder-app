package com.darvi.filmhunter.domain.repository

import com.darvi.filmhunter.domain.entity.MatcherSessionEntity

interface MatcherSessionRepository {
    suspend fun createSession(
        code: String,
        createdBy: String,
        filters: Map<String, Any>
    ): Result<MatcherSessionEntity>
    
    suspend fun cancelSession(sessionId: String): Result<Unit>
    
    suspend fun joinSessionByCode(code: String, currentUserId: String): Result<MatcherSessionEntity>
}

