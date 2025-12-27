package com.darvi.filmhunter.domain.usecase.matcher

import com.darvi.filmhunter.domain.repository.MatcherSessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubscribeToSessionMembers @Inject constructor(
    private val matcherSessionRepository: MatcherSessionRepository
) {
    suspend operator fun invoke(sessionId: String, currentUserId: String): Flow<String> {
        return matcherSessionRepository.subscribeToSessionMembers(sessionId, currentUserId)
    }
}

