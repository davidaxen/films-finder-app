package com.darvi.filmhunter.domain.usecase.matcher

import com.darvi.filmhunter.domain.entity.MatcherSessionEntity
import com.darvi.filmhunter.domain.repository.MatcherSessionRepository
import javax.inject.Inject

class GetSessionById @Inject constructor(
    private val matcherSessionRepository: MatcherSessionRepository
) {
    suspend operator fun invoke(sessionId: String): Result<MatcherSessionEntity> {
        return matcherSessionRepository.getSessionById(sessionId)
    }
}

