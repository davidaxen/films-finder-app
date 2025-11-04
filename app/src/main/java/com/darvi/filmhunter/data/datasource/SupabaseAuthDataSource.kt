package com.darvi.filmhunter.data.datasource

import com.darvi.filmhunter.data.model.UserModel

interface SupabaseAuthDataSource {
    suspend fun signIn(email: String, password: String): UserModel
    suspend fun signUp(email: String, password: String): UserModel
    fun getCurrentUser(): UserModel?
    suspend fun signOut()
}