package com.darvi.filmhunter.data.datasource

import com.darvi.filmhunter.data.model.UserModel
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.StateFlow

interface SupabaseAuthDataSource {
    suspend fun signIn(email: String, password: String): Result<UserModel>
    suspend fun signUp(email: String, password: String): UserModel
    fun getCurrentUser(): UserModel?
    suspend fun getCurrentUserFlow(): StateFlow<SessionStatus>
    suspend fun signOut()
}