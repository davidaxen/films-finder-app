package com.darvi.filmhunter.domain.usecase

import com.darvi.filmhunter.domain.repository.AuthRepository
import com.darvi.filmhunter.domain.repository.SessionRepository
import javax.inject.Inject

class Login @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(email: String, password: String) {
        sessionRepository.setCurrentUser(authRepository.doLogin(email, password))
    }
}