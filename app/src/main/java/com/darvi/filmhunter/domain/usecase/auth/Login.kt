package com.darvi.filmhunter.domain.usecase.auth

import com.darvi.filmhunter.domain.entity.UserEntity
import com.darvi.filmhunter.domain.repository.AuthRepository
import com.darvi.filmhunter.domain.repository.SessionRepository
import javax.inject.Inject

class Login @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<UserEntity> {
        val result = authRepository.doLogin(email, password)

        result.onSuccess { userEntity ->
            sessionRepository.setCurrentUser(userEntity)
        }

        return result
    }
}