package com.darvi.filmhunter.domain.usecase.matcher

import com.darvi.filmhunter.domain.repository.MatcherSessionRepository
import javax.inject.Inject

class UnsubscribeFromSessionStatus @Inject constructor(
    private val matcherSessionRepository: MatcherSessionRepository
) {
    suspend operator fun invoke(sessionId: String) {
        matcherSessionRepository.unsubscribeFromSessionStatus(sessionId)
    }
}

