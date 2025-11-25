package com.darvi.filmhunter.domain.usecase.auth

import com.darvi.filmhunter.domain.entity.SessionState
import com.darvi.filmhunter.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentSession @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    operator fun invoke(): Flow<SessionState> = sessionRepository.observeSession()
}