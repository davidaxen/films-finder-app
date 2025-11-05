package com.darvi.filmhunter.domain.repository

import com.darvi.filmhunter.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun getCurrentUser(): Flow<UserEntity?>
    fun setCurrentUser(user: UserEntity)
    suspend fun signOut()
}