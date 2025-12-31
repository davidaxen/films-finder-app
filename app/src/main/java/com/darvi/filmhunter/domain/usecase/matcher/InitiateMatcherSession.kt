package com.darvi.filmhunter.domain.usecase.matcher

import com.darvi.filmhunter.domain.repository.MatcherSessionRepository
import javax.inject.Inject

class InitiateMatcherSession @Inject constructor(
    private val matcherSessionRepository: MatcherSessionRepository
) {
    suspend operator fun invoke(sessionId: String): Result<Unit> {
        return matcherSessionRepository.initiateSession(sessionId)
    }
}

