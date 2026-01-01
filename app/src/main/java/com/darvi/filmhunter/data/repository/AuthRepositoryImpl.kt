package com.darvi.filmhunter.data.repository

import com.darvi.filmhunter.data.datasource.SupabaseAuthDataSource
import com.darvi.filmhunter.data.model.toDomain
import com.darvi.filmhunter.domain.entity.UserEntity
import com.darvi.filmhunter.domain.repository.AuthRepository

class AuthRepositoryImpl(private val authDataSource: SupabaseAuthDataSource): AuthRepository {
    override suspend fun doLogin(email: String, password: String): Result<UserEntity> {
        return authDataSource
                .signIn(email, password)
                .map {
                    it.toDomain()
                }
    }

    override suspend fun doRegister(email: String, password: String): UserEntity {
        return authDataSource.signUp(email, password).toDomain()
    }
}