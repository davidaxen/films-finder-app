package com.darvi.filmhunter.domain.repository

import com.darvi.filmhunter.domain.entity.SessionState
import com.darvi.filmhunter.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SessionRepository {
    fun getCurrentUser(): StateFlow<UserEntity?>
    fun setCurrentUser(user: UserEntity)
    fun observeSession(): Flow<SessionState>
    suspend fun signOut()
}