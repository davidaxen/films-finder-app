package com.darvi.filmhunter.domain.usecase.matcher

import com.darvi.filmhunter.domain.repository.MatcherSessionRepository
import javax.inject.Inject

class UnsubscribeFromSessionMembers @Inject constructor(
    private val matcherSessionRepository: MatcherSessionRepository
) {
    suspend operator fun invoke(sessionId: String) {
        matcherSessionRepository.unsubscribeFromSessionMembers(sessionId)
    }
}

