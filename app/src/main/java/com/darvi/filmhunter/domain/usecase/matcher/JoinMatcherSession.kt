package com.darvi.filmhunter.domain.usecase.matcher

import com.darvi.filmhunter.domain.entity.MatcherSessionEntity
import com.darvi.filmhunter.domain.repository.MatcherSessionRepository
import javax.inject.Inject

class JoinMatcherSession @Inject constructor(
    private val matcherSessionRepository: MatcherSessionRepository
) {
    suspend operator fun invoke(
        code: String,
        currentUserId: String
    ): Result<MatcherSessionEntity> {
        return matcherSessionRepository.joinSessionByCode(code, currentUserId)
    }
}

