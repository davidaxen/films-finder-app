package com.darvi.filmhunter.domain.usecase.matcher

import com.darvi.filmhunter.domain.repository.MatcherSessionRepository
import javax.inject.Inject

class LeaveMatcherSession @Inject constructor(
    private val matcherSessionRepository: MatcherSessionRepository
) {
    suspend operator fun invoke(sessionId: String, userId: String): Result<Unit> {
        return matcherSessionRepository.leaveSession(sessionId, userId)
    }
}

