package com.darvi.filmhunter.domain.usecase

import com.darvi.filmhunter.domain.repository.AuthRepository
import javax.inject.Inject

class Login @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) {
        authRepository.doLogin(email, password)
    }
}