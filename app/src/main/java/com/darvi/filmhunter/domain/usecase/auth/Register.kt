package com.darvi.filmhunter.domain.usecase.auth

import com.darvi.filmhunter.domain.entity.UserEntity
import com.darvi.filmhunter.domain.repository.AuthRepository
import javax.inject.Inject

class Register @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): UserEntity {
        return authRepository.doRegister(email, password)
    }
}