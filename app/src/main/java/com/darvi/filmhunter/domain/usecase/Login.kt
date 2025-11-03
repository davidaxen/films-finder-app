package com.darvi.filmhunter.domain.usecase

import com.darvi.filmhunter.domain.repository.AuthRepository

class Login(private val authRepository: AuthRepository) {
    suspend fun invoke(email: String, password: String) {
        authRepository.doLogin(email, password)
    }
}