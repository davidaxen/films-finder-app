package com.darvi.filmhunter.domain.usecase.matcher

import com.darvi.filmhunter.domain.entity.MatcherSessionEntity
import com.darvi.filmhunter.domain.repository.MatcherSessionRepository
import javax.inject.Inject

class CreateMatcherSession @Inject constructor(
    private val matcherSessionRepository: MatcherSessionRepository
) {
    suspend operator fun invoke(
        code: String,
        createdBy: String,
        filters: Map<String, Any>
    ): Result<MatcherSessionEntity> {
        return matcherSessionRepository.createSession(code, createdBy, filters)
    }
}

