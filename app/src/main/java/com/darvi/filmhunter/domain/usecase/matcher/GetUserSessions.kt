package com.darvi.filmhunter.domain.usecase.matcher

import com.darvi.filmhunter.domain.entity.MatcherSessionEntity
import com.darvi.filmhunter.domain.repository.MatcherSessionRepository
import javax.inject.Inject

class GetUserSessions @Inject constructor(
    private val matcherSessionRepository: MatcherSessionRepository
) {
    suspend operator fun invoke(userId: String): Result<List<MatcherSessionEntity>> {
        return matcherSessionRepository.getUserSessions(userId)
    }
}

