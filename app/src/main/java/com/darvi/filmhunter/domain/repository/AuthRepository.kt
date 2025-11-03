package com.darvi.filmhunter.domain.repository

import com.darvi.filmhunter.domain.entity.UserEntity

interface AuthRepository {
    suspend fun doLogin(email: String, password: String): UserEntity
}