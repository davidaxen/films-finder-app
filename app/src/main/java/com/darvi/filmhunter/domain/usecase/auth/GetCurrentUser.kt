package com.darvi.filmhunter.domain.usecase.auth

import com.darvi.filmhunter.domain.entity.UserEntity
import com.darvi.filmhunter.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUser @Inject constructor(private val sessionRepository: SessionRepository) {
    operator fun invoke(): Flow<UserEntity?> {
        return sessionRepository.getCurrentUser()
    }
}