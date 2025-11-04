package com.darvi.filmhunter.domain.usecase

import com.darvi.filmhunter.domain.entity.UserEntity
import com.darvi.filmhunter.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUser @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): UserEntity? {
        return authRepository.getCurrentUser()
    }
}